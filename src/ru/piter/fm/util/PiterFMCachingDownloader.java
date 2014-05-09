package ru.piter.fm.util;

import static junit.framework.Assert.*;
import static ru.piter.fm.util.RadioUtils.CHANNEL_PREFIX;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

/**
 * <p>
 * This class wraps the downloading and caching of mp4 audio tracks from
 * fresh.moskva.fm.
 * </p>
 * <p>
 * The constructor is private, because it stores the downloaded files in
 * {@link Utils#CHUNKS_DIR}. Use {@link #INSTANCE} to get the global object
 * instance. To prevent file leaks, the files have fixed names. Same file may
 * later represent a different track. <br/>
 * Up to {@link #READ_AHEAD_NUM} files can be cached.
 * </p>
 * <p>
 * Use {@link #getFile(String, TrackCalendar)} to request a track. The method
 * will also schedule the download of several subsequent files in temporal
 * order.<br/>
 * Use {@link #releaseFile(String, boolean)} to release a file.
 * </p>
 * <p>
 * The downloader part can retry multiple times in case of network error. It is
 * controlled by the parameters {@link Settings#getReconnectCount()} and
 * {@link Settings#getReconnectTimeout()}.
 * </p>
 * <p>
 * This class is thread-safe. You can use it from AsyncTask
 * </p>
 * <h3>Internals</h3>
 * <p>
 * The variable part of the cache consists of:
 * <ul>
 * <li/>{@link #urlQueue}
 * <li/>{@link #entries}
 * <li/>{@link #currentUrl}
 * </ul>
 * Their combination tells the cache what to do next.
 * </p>
 * <p>
 * An URL can be either queued or not queued, depending on its presence in the
 * queue. {@link #getFile(String, TrackCalendar) getFile()} constructs several
 * time-ordered URLs from its parameters and rewrites the queue with them.
 * </p>
 * <p>
 * Cache Entries can have different states:
 * 
 * <pre>
 * empty -> scheduled -> downloaded <-> locked -> bad
 *             |   |          |                    |
 *             +-<-+          |                    |
 *             +-------<------+                    |
 *             +-------<---------------------------+
 * </pre>
 * 
 * <ul>
 * <li/>empty: <code>{@link CacheEntry#m_url m_url} == null</code>
 * <li/>scheduled:
 * <code>{@link CacheEntry#m_url m_url} != null && {@link CacheEntry#m_fos m_fos} != null</code>
 * <br/>
 * {@link #currentUrl} controls whether it's paused or downloading.<br/>
 * {@link CacheEntry#omniCount omniCount} holds the tryNo in this state<br/>
 * Sometimes a scheduled entry must be cancelled, because it's not in the queue
 * and another file needs to be downloaded. In that case
 * {@link CacheEntry#m_fos m_fos} is closed and replaced with another instance.
 * The downloader thread notices that and stops itself.
 * <li/>downloaded:
 * <code>{@link CacheEntry#m_url m_url} != null && {@link CacheEntry#m_fos m_fos} == null && {@link CacheEntry#omniCount omniCount} == 0</code>
 * <br/>
 * {@link CacheEntry#omniCount omniCount} holds the refCount in this state
 * <li/>locked: like downloaded, but <code>omniCount != 0</code>
 * <li/>bad: like downloaded, but <code>m_url == {@link #BADFILE}</code>
 * </ul>
 * </p>
 * <p>
 * The state of the cache is always accessed inside a synchronized block using
 * internal {@link #lock} object. A paused thread calls <code>lock.wait()</code>
 * in a loop until the changed state allows it to continue. If another thread
 * changes the state, it calls <code>lock.notifyAll()</code>.
 * </p>
 * 
 * @author Ilya Basin
 */
public class PiterFMCachingDownloader {

    /** global object instance */
    public static final PiterFMCachingDownloader INSTANCE = new PiterFMCachingDownloader();

    /** cache size */
    private static final int READ_AHEAD_NUM = 5;

    /**
     * a dummy non-null value for {@link CacheEntry#m_url} to ensure the entry
     * is not picked by {@link #getFile(String, TrackCalendar)}. null can't be
     * used here, because the file may still be locked
     */
    private static final String BADFILE = "badfile";

    private static final String Tag = "PiterFMCachingDownloader";

    /** lock for everything */
    private final Object lock = new Object();

    /** holds queued URLs */
    private ArrayList<String> urlQueue = new ArrayList<String>(READ_AHEAD_NUM);

    /** holds Cache Entries */
    private CacheEntry[] entries = new CacheEntry[READ_AHEAD_NUM];

    /** current URL for download. If differs from thread's URL, then thread waits. Can be null. */
    private String currentUrl;

    /** constant map of entries by path. See also {@link #byUrl(String)} */
    private final HashMap<String, CacheEntry> byPath = new HashMap<String, CacheEntry>(READ_AHEAD_NUM);

    {
        for (int i = 0; i < READ_AHEAD_NUM; i++) {
            urlQueue.add(null);
            CacheEntry entry = new CacheEntry(i);
            entries[i] = entry;
            byPath.put(entry.file.getAbsolutePath(), entry);
        }
    }

    /**
     * Get and lock a file with the provided params. This function is
     * synchronous and may block forever, if 5 other files stay locked.
     */
    public String getFile(String channelId, TrackCalendar trackTime) throws InterruptedException {
        final String funcname = "getFile";
        Log.d(Tag, funcname + ",channelId = " + channelId + ", trackTime = " + trackTime.asURLPart());
        trackTime = trackTime.clone();
        Log.v(Tag, funcname + ",synchronized before, dummyNo:145"); try {
        synchronized (lock) {
            Log.v(Tag, funcname + ",synchronized in, dummyNo:147");
            String trackUrl;
            // update the queue. Not queued entries may become victims, if not locked
            String prefix = CHANNEL_PREFIX + channelId + "/mp4/";
            for (int i = 0;; i++) {
                trackUrl = prefix + trackTime.asURLPart() + ".mp4";
                urlQueue.set(i, trackUrl);
                if (i == READ_AHEAD_NUM - 1)
                    break;
                trackTime.nextTrackTime();
            }
            resumeOrCreateNextDownloadNoLock();
            lock.notifyAll();

            trackUrl = urlQueue.get(0);
            for (;;) {
                CacheEntry entry = byUrl(trackUrl);
                if (entry != null) {
                    if (entry.m_fos == null) {
                        Log.d(Tag, funcname + ",complete entry for url found, ok");
                        entry.omniCount++;
                        return entry.file.getAbsolutePath();
                    }
                    if (!trackUrl.equals(currentUrl)) {
                        Log.d(Tag, funcname + ",incomplete entry for url found, but it is cancelled or exceed attempts, failed");
                        return null;
                    }
                    Log.d(Tag, funcname + ",incomplete entry for url found, waiting");
                } else if (urlQueue.indexOf(trackUrl) == -1) {
                    Log.d(Tag, funcname + ",url not found in entries and queue, failed");
                    return null;
                }
                else { Log.d(Tag, funcname + ",url found in queue, waiting"); }
                lock.wait();
            }
        }
        } finally { Log.v(Tag, funcname + ",synchronized after, dummyNo:183"); }
    }

    /**
     * marks the file weak, so it can be replaced by more important files
     * 
     * @param path
     *            returned by {@link #getFile(String, TrackCalendar)}
     * @param badFile
     *            Ensures that this file will be redownloaded
     */
    public void releaseFile(String path, boolean badFile) {
        final String funcname = "releaseFile";
        Log.d(Tag, funcname + ",path = " + path + ", badFile = " + badFile);
        Log.v(Tag, funcname + ",synchronized before, dummyNo:197"); try {
        synchronized (lock) {
            Log.v(Tag, funcname + ",synchronized in, dummyNo:199");
            CacheEntry entry = byPath.get(path);
            assertNull(entry.m_fos);
            assertTrue(entry.omniCount > 0);
            entry.omniCount--;
            if (badFile) {
                entry.m_url = BADFILE;
            }
            if (entry.omniCount == 0) {
                resumeOrCreateNextDownloadNoLock();
                lock.notifyAll();
            }
        }
        } finally { Log.v(Tag, funcname + ",synchronized after, dummyNo:212"); }
    }

    private PiterFMCachingDownloader() {
    }

    private void resumeOrCreateNextDownloadNoLock() {
        final String funcname = "resumeOrCreateNextDownloadNoLock";
        Log.d(Tag, funcname + ",");
        // find next not-yet-downloaded file
        for (String url : urlQueue) {
            CacheEntry entry = byUrl(url);
            if (entry != null) {
                if (entry.m_fos == null) {
                    continue; // this file is downloaded
                }
                Log.d(Tag, funcname + ",not downloaded, but scheduled: " + url);
                entry.omniCount = 0; // reset tryNo
                currentUrl = url; // unpause it
                return;
            }
            Log.d(Tag, funcname + ",not downloaded and not yet scheduled: " + url);

            // rank victim candidates. null > scheduled > complete unrefed
            entry = null;
            for (CacheEntry candidate : entries) {
                if (candidate.m_url == null) {
                    entry = candidate; // best choice
                    break;
                } else if (urlQueue.indexOf(candidate.m_url) == -1) {
                    if (candidate.m_fos != null) {
                        entry = candidate; // acceptable choice
                    } else if (candidate.omniCount == 0) {
                        if (entry == null)
                            entry = candidate; // worst choice
                    }
                }
            }
            if (entry != null) {
                Log.d(Tag, funcname + ",victim found with url: " + entry.m_url);
                entry.rescheduleNoLock(url);
                entry.omniCount = 0;
                currentUrl = url;
            }
            else { Log.d(Tag, funcname + ",no available entry"); }
            return;
        }
        Log.d(Tag, funcname + ",All queued files alredy downloaded");
    }

    private class CacheEntry {
        /** if {@link #thread} == null, holds the data downloaded from {@link #m_url} */
        public final File file;
        /** */
        public int omniCount;

        /** public file stream, can be closed externally to steal the file */
        public OutputStream m_fos; 

        /** {@link #m_url} may change while waiting for {@link #thread} to finish */
        public String m_url;

        public CacheEntry(int nEntry) {
            file = new File(Utils.CHUNKS_DIR, nEntry + ".mp4");
        }

        private void rescheduleNoLock(String url) {
            final String funcname = "CacheEntry,rescheduleNoLock";
            Log.d(Tag, funcname + ",url = " + url);
            try {
                if (m_url != null) {
                    if (m_fos != null) {
                        Log.d(Tag, funcname + ",steal file from thread with url: " + m_url);
                        m_fos.close();
                    }
                }
                m_url = url;
                m_fos = new FileOutputStream(file);
            } catch (IOException e1) {
                Log.d(Tag, funcname + ",", e1);
                e1.printStackTrace();
                System.exit(1);
            }
            final OutputStream fos = m_fos;
            new Thread("trackDownloader") {
                @Override
                public void run() {
                    final String funcname = "CacheEntry,run";
                    Log.d(Tag, funcname + ",");
                    try {
                        downloadTrack(fos);
                    } catch (InterruptedException e) {
                        // nothing
                        Log.d(Tag, funcname + ",cancelled");
                    } catch (Exception e) {
                        Log.d(Tag, funcname + ",", e);
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }.start();
        }

        private void downloadTrack(OutputStream fos) throws InterruptedException, IOException {
            final String funcname = "downloadTrack";
            Log.d(Tag, funcname + ",");
            byte[] buffer = new byte[512];
            int oldpos = 0;
            boolean rethrowIO = false;
            String url = m_url; // m_url may change, if cancelled between synchronized and openConnection()
            for (;;) {
                int tryNo;
                Log.v(Tag, funcname + ",synchronized before, dummyNo:324"); try {
                synchronized (lock) {
                    Log.v(Tag, funcname + ",synchronized in, dummyNo:326");
                    //tryNo = omniCount;
                    int attemptCount = Settings.isReconnect() ? Settings.getReconnectCount() : 0;
                    if (fos == m_fos && omniCount >= attemptCount && url.equals(currentUrl)) {
                        Log.w(Tag, funcname + ",not cancelled, exceed attempts, and is current,, pausing");
                        currentUrl = null; // This should tell thi loop in getFile(), that we failed
                        lock.notifyAll(); // This should unfreeze waiting getFile()
                    }
                    checkpointNoLock(fos);
                    tryNo = omniCount;
                    omniCount++;
                }
                } finally { Log.v(Tag, funcname + ",synchronized after, dummyNo:337"); }

                int pos = 0;
                InputStream in = null;
                try {
                    Log.d(Tag, funcname + ",before openConnection(), tryNo = " + tryNo + ", url: " + url);
                    in = Utils.openConnection(url);
                    Log.d(Tag, funcname + ",after openConnection()");
                    Log.v(Tag, funcname + ",synchronized before, dummyNo:345"); try {
                    synchronized (lock) {
                        Log.v(Tag, funcname + ",synchronized in, dummyNo:347");
                        checkpointNoLock(fos);
                    }
                    } finally { Log.v(Tag, funcname + ",synchronized after, dummyNo:350"); }
                    for(;;) {
                        Log.v(Tag, funcname + ",before read()");
                        int len = in.read(buffer);
                        Log.v(Tag, funcname + ",after read(), pos = " + pos + ", rslt = " + len);
                        if (len == -1) {
                            Log.d(Tag, funcname + ",download complete");
                            break;
                        }
                        int ofs = oldpos - pos;
                        pos += len;
                        Log.v(Tag, funcname + ",synchronized before, dummyNo:361"); try {
                        synchronized (lock) {
                            Log.v(Tag, funcname + ",synchronized in, dummyNo:363");
                            checkpointNoLock(fos);
                            if (ofs < len) {
                                if (ofs < 0)
                                    ofs = 0;
                                else
                                    len -= ofs;
                                rethrowIO = true;
                                Log.v(Tag, funcname + ",write(), [" + ofs + ".." + (ofs + len - 1) + "], " + len + " byte(s)");
                                fos.write(buffer, ofs, len);
                                rethrowIO = false;
                            }
                            else { Log.v(Tag, funcname + ",skip"); }
                        }
                        } finally { Log.v(Tag, funcname + ",synchronized after, dummyNo:377"); }
                    }
                    rethrowIO = true;
                    Log.v(Tag, funcname + ",synchronized before, dummyNo:380"); try {
                    synchronized (lock) {
                        Log.v(Tag, funcname + ",synchronized in, dummyNo:382");
                        checkpointNoLock(fos);
                        fos.close();
                        m_fos = null; // status: downloaded
                        omniCount = 0; // now it's refCount
                        resumeOrCreateNextDownloadNoLock();
                        lock.notifyAll();
                    }
                    } finally { Log.v(Tag, funcname + ",synchronized after, dummyNo:390"); }
                    return;
                } catch(IOException e) {
                    if (rethrowIO)
                        throw e; // This IOException is fatal
                    Log.d(Tag, funcname + ",download failed: " + e.toString());
                } finally {
                    if (in != null) try { in.close(); } catch (IOException e) {}
                }
                oldpos = pos;
                int attemptDelay = Settings.getReconnectTimeout() * 1000;
                Log.d(Tag, funcname + ",sleeping " + attemptDelay + "ms");
                Thread.sleep(attemptDelay);
            }
        }

        private void checkpointNoLock(OutputStream fos) throws InterruptedException {
            final String funcname = "checkpointNoLock";
            boolean dbgWasWaiting = false;
            for (;;) {
                if (fos != m_fos) {
                    Log.d(Tag, funcname + ",fos != m_fos, cancelled");
                    throw new InterruptedException("cancelled");
                }
                // url cannot be null, if not cancelled
                if (m_url.equals(currentUrl)) {
                    if (dbgWasWaiting) { Log.d(Tag, funcname + ",m_url == currentUrl, continue"); }
                    break;
                }
                Log.d(Tag, funcname + ",m_url != currentUrl, waiting");
                lock.wait();
                dbgWasWaiting = true;
            }
        }
    }

    /** @return Cache Entry with matching URL */
    private CacheEntry byUrl(String url) {
        for (CacheEntry entry : entries)
            if (url.equals(entry.m_url))
                return entry;
        return null;
    }
}
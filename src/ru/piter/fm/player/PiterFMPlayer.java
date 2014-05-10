package ru.piter.fm.player;

import static junit.framework.Assert.*;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import ru.piter.fm.player.PlayerInterface.EventHandler;
import ru.piter.fm.player.PlayerInterface.EventType;
import ru.piter.fm.util.TrackCalendar;
import ru.piter.fm.util.PiterFMCachingDownloader;

class PiterFMPlayer {

    protected void locksAcquire() {
    }
    protected void locksRelease() {
    }

    private static final String Tag = "PiterFMPlayer";

    private final Handler handler = new Handler();

    private final TrackCalendar trackCal = new TrackCalendar();
    private String channelId;
    private final PiterFMCachingDownloader cache = PiterFMCachingDownloader.INSTANCE;

    private AsyncTask<?,?,?> getFileTask;
    private final PlayerWrap player1 = new PlayerWrap();
    private final PlayerWrap player2 = new PlayerWrap();
    private SmoothMediaPlayer currentPlayer;

    private boolean isPaused = true;

    private EventHandler eventHandler;

    private boolean isNextPlayerSet;

    { assertUIThread(); }

    private void assertUIThread() { assertEquals(Looper.getMainLooper().getThread(), Thread.currentThread()); }

    /** "2010:11:13:20:31:12" */
    public void open(String ch, String trackTimeStr) {
        final String funcname = "open";
        Log.d(Tag, funcname + ",ch = " + ch + ", trackTimeStr = " + trackTimeStr);
        assertUIThread();

        channelId = ch;
        trackCal.setTrackTime(trackTimeStr);

        setPausedFalse();
        reopen();
    }

    private void reopen() {
        final String funcname = "reopen";
        Log.d(Tag, funcname + ",");

        postEvent(EventType.Buffering);

        currentPlayer = null;
        isNextPlayerSet = false;
        resetCommon();

        player1.scheduleGetFile();
    }

    private void resetCommon() {
        final String funcname = "reset";
        Log.d(Tag, funcname + ",");
        if (getFileTask != null) {
            Log.d(Tag, funcname + ", getFileTask != null, calling cancel()");
            getFileTask.cancel(false);
            getFileTask = null;
        }
        player1.reset();
        player2.reset();
    }

    private class PlayerWrap implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnCompletionListener
    {
        private final int dbgId = player1 == null ? 1 : 2;
        public final SmoothMediaPlayer player = SmoothMediaPlayer.newInstance(dbgId);
        public String path;

        /** Gingerbread has a bug that onSeekComplete() is called twice: 1st after seekTo() and 2nd after start() */
        private boolean onSeekCompleteCalled;

        {
            final String funcname = "PlayerWrap," + dbgId;
            Log.d(Tag, funcname + ",");
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnCompletionListener(this);
        }

        public void reset() {
            final String funcname = "PlayerWrap," + dbgId + ",reset";
            Log.d(Tag, funcname + ",");
            onSeekCompleteCalled = false;
            player.reset();
            if (path != null) {
                Log.d(Tag, funcname + ",path != null, calling releaseFile('" + path + "')");
                cache.releaseFile(path, false);
                path = null;
            }
        }

        public void scheduleGetFile() {
            final String funcname = "PlayerWrap," + dbgId + ",scheduleGetFile";
            Log.d(Tag, funcname + ",");
            assertNull(getFileTask);
            final String channelId = PiterFMPlayer.this.channelId;
            final TrackCalendar trackCal = PiterFMPlayer.this.trackCal.clone();
            Log.d(Tag, funcname + ",channelId = " + channelId + ", trackCal = " + trackCal.asURLPart());

            getFileTask = new AsyncTask<Void, Void, Void>() {
                private String path;

                @Override
                protected Void doInBackground(Void... params) {
                    final String funcname = "PlayerWrap," + dbgId + ",doInBackground";
                    Log.d(Tag, funcname + ",channelId = " + channelId + ", trackCal = " + trackCal.asURLPart());
                    try {
                        path = cache.getFile(channelId, trackCal);
                    } catch (Exception e) {
                        Log.d(Tag, funcname + ",Exception caught", e);
                    }
                    return null;
                }

                @Override
                protected void onCancelled() {
                    final String funcname = "PlayerWrap," + dbgId + ",onCancelled";
                    Log.d(Tag, funcname + ",");
                    if (path != null) {
                        Log.d(Tag, funcname + ",path != null, calling releaseFile('" + path + "')");
                        cache.releaseFile(path, false);
                    }
                };
    
                @Override
                protected void onPostExecute(Void result) {
                    final String funcname = "PlayerWrap," + dbgId + ",onPostExecute";
                    Log.d(Tag, funcname + ",");
                    assertNull(PlayerWrap.this.path);
                    if (path == null) {
                        Log.d(Tag, funcname + ",path == null, calling onError()");
                        internalOnError();
                    } else {
                        PlayerWrap.this.path = path;
                        try {
                            Log.d(Tag, funcname + ",calling player.setDataSource(), path = " + path);
                            player.setDataSource(path);
                            player.prepare();
                        } catch (IOException e) {
                            Log.d(Tag, funcname + ",Exception caught", e);
                            onError(null, 0, 0);
                        }
                    }
                }
            }.execute((Void[])null);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            final String funcname = "PlayerWrap," + dbgId + ",onError";
            Log.d(Tag, funcname + ",what = " + what + ", extra = " + extra);
            cache.releaseFile(path, true);
            path = null;
            internalOnError();
            return true;
        }

        private void internalOnError() {
            final String funcname = "PlayerWrap," + dbgId + ",internalOnError";
            Log.d(Tag, funcname + ",");
            // not resetting player now, because it won't be used until reopen()
            getFileTask = null;
            if (currentPlayer == null) {
                Log.d(Tag, funcname + ",currentPlayer == null, calling giveUp()");
                giveUp();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            final String funcname = "PlayerWrap," + dbgId + ",onPrepared";
            Log.d(Tag, funcname + ",");
            int msec = trackCal.getSeekTo();
            if (msec == 0) {
                internalOnSeekComplete();
            } else {
                player.seekTo(msec);
            }
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            final String funcname = "PlayerWrap," + dbgId + ",onSeekComplete";
            Log.d(Tag, funcname + ",currentPosition = " + mp.getCurrentPosition());
            if (onSeekCompleteCalled) {
                Log.d(Tag, funcname + ",2nd call of onSeekComplete(), ignoring");
                return;
            }
            internalOnSeekComplete();
        }

        private void internalOnSeekComplete() {
            final String funcname = "PlayerWrap," + dbgId + ",internalOnSeekComplete";
            Log.d(Tag, funcname + ",");
            onSeekCompleteCalled = true;
            getFileTask = null;
            PlayerWrap other = getOtherPlayer();
            // The other player is either playing now or has finished playing
            if (currentPlayer == null) {
                Log.d(Tag, funcname + ",currentPlayer == null, not trying to setNextMediaPlayer");
                currentPlayer = player;
                if (!isPaused) {
                    Log.d(Tag, funcname + ",isPaused == false, calling start()");
                    player.start();
                    callEvent(EventType.NotBuffering);
                }
                else { Log.d(Tag, funcname + ",isPaused == true, not calling start()"); }
                trackCal.nextTrackTime();
                other.scheduleGetFile();
            } else {
                Log.d(Tag, funcname + ",currentPlayer != null, trying to set me as NextMediaPlayer");
                other.player.setNextSmoothMediaPlayer(player);
                isNextPlayerSet = true;
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            final String funcname = "PlayerWrap," + dbgId + ",onCompletion";
            //Log.d(Tag, funcname + ",");
            if (isNextPlayerSet) {
                //Log.d(Tag, funcname + ",isNextPlayerSet == true, trying to start next mediaplayer");
                currentPlayer = getOtherPlayer().player;
            }
            Log.d(Tag, funcname + ",");
            reset();
            if (isNextPlayerSet) {
                Log.d(Tag, funcname + ",isNextPlayerSet == true, calling scheduleGetFile()");
                isNextPlayerSet = false;
                trackCal.nextTrackTime();
                scheduleGetFile();
            } else if (getFileTask == null) {
                Log.w(Tag, funcname + ",isNextPlayerSet == false && getFileTask == null, calling giveUp()");
                currentPlayer = null;
                giveUp();
            } else {
                Log.d(Tag, funcname + "isNextPlayerSet == false && getFileTask != null, next file not downloaded yet");
                currentPlayer = null;
                callEvent(EventType.Buffering);
            }
        }

        private PlayerWrap getOtherPlayer() {
            //final String funcname = "PlayerWrap," + dbgId + ",getOtherPlayer";
            //Log.d(Tag, funcname + ",");
            return (this == player1 ? player2 : player1);
        }
    }

    public void pause() {
        final String funcname = "pause";
        Log.d(Tag, funcname + ",");
        assertUIThread();
        if (!isPaused) {
            Log.d(Tag, funcname + ",isPaused == false, maybe there is a player to pause");
            setPausedTrue();
            postEvent(EventType.NotBuffering);
            if (currentPlayer != null) {
                Log.d(Tag, funcname + ",currentPlayer != null, calling pause()");
                currentPlayer.pause();
            }
            else { Log.d(Tag, funcname + ",currentPlayer == null, nothing to pause"); }
        }
        else { Log.d(Tag, funcname + ",isPaused == true, doing nothing"); }
    }

    public void resume() {
        final String funcname = "resume";
        Log.d(Tag, funcname + ",");
        assertUIThread();
        assertNotNull(channelId);
        if (isPaused) {
            Log.d(Tag, funcname + ",isPaused == true, maybe there is a player to resume");
            setPausedFalse();
            if (currentPlayer != null) {
                Log.d(Tag, funcname + ",currentPlayer != null, calling start()");
                currentPlayer.start();
                postEvent(EventType.NotBuffering);
            } else if (getFileTask == null) {
                Log.d(Tag, funcname + ",getFileTask == null, calling reopen()");
                reopen();
            } else {
                Log.d(Tag, funcname + ",currentPlayer == null && getFileTask != null, something is downloading");
                postEvent(EventType.Buffering);
            }
        }
        else { Log.d(Tag, funcname + ",isPaused == false, doing nothing"); }
    }

    public String getChannelId() {
        assertUIThread();
        return channelId;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void giveUp() {
        final String funcname = "giveUp";
        Log.d(Tag, funcname + ",");
        assertFalse(isPaused);
        setPausedTrue();
        callEvent(EventType.Error);
    }

    private void postEvent(final EventType ev) {
        final String funcname = "postEvent";
        Log.d(Tag, funcname + ",ev = " + ev);
        handler.post(new Runnable() {
            @Override
            public void run() {
                callEvent(ev);
            }
        });
    }

    private void callEvent(EventType ev) {
        final String funcname = "callEvent";
        Log.d(Tag, funcname + ",ev = " + ev);
        if (eventHandler != null)
            eventHandler.onEvent(ev);
    }

    public void release() {
        setPausedTrue();
        resetCommon();
        player1.player.release();
        player2.player.release();
    }

    public void setEventHandler(EventHandler handler) {
        assertUIThread();
        eventHandler = handler;
    }

    private void setPausedFalse() {
        if (isPaused) {
            isPaused = false;
            locksAcquire();
        }
    }

    private void setPausedTrue() {
        if (!isPaused) {
            isPaused = true;
            locksRelease();
        }
    }
}

/**
 * 
 */
package ru.piter.fm.aac;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import ru.piter.fm.player.PlayerInterface.EventHandler;
import ru.piter.fm.player.PlayerInterface.EventType;
import ru.piter.fm.util.TrackCalendar;

/**
 * @author Ilya Basin
 * 
 */
public abstract class PiterFMPlayer {

    protected abstract void callEvent2(EventType ev);
    protected abstract void locksAcquire();
    protected abstract void locksRelease();

    /**
     * Not all methods of MediaPlayer are asynchronous, e.g. setDataSource("http://.../test.aac") takes half a minute,
     * regardless of download speed. This is why when I need to cancel the current operation, I create a new instance of
     * MediaPlayer.
     */
    private AsyncTask<?,?,?> getFileTask;
    private MediaPlayer player = new MediaPlayer();

    private B b = new B();
    private PlayerEvents playerEvents = new PlayerEvents();
    private long startTime;
    private String channelId;

    private boolean isPlayerReady;
    private boolean isPaused = true;

    private static final String Tag = "PiterFMPlayer";

    private final Handler handler = new Handler();

    { assertUIThread(); }

    protected void assertUIThread() { assertEquals(Looper.getMainLooper().getThread(), Thread.currentThread()); }

    public void open(String ch, TrackCalendar trackTime) {
        final String funcname = "open";
        Log.d(Tag, funcname + ",");
        assertUIThread();

        channelId = ch;
        startTime = trackTime.getTimeInMillis();
        setPausedFalse();
        reopen();
    }

    private void reopen() {
        final String funcname = "reopen";
        Log.d(Tag, funcname + ",");

        postEvent(EventType.Buffering);

        resetCommon();

        Log.d(Tag, funcname + ",starting background task");
        getFileTask = new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                if (isCancelled()) return null;
                final String funcname = "doInBackground";
                Log.d(Tag, funcname + ",");
                try {
                    String streamUrl = b.doIt(channelId, startTime);
                    if (isCancelled()) return null;
                    Log.d(Tag, funcname + ",calling player.setDataSource(),streamUrl = " + streamUrl);
                    player.setDataSource(streamUrl);
                    if (isCancelled()) return null;
                    Log.d(Tag, funcname + ",calling player.prepare()");
                    player.prepare();
                } catch (Exception e) {
                    Log.e(Tag, funcname + ",Exception caught", e);
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception exception) {
                final String funcname = "onPostExecute";
                Log.d(Tag, funcname + ",");

                if (exception != null) {
                    getFileTask = null;
                    giveUp();
                }
            };
        }.execute((Void[])null);
    }

    public TrackCalendar getPosition() {
        final String funcname = "getPosition";
        Log.d(Tag, funcname + ",");

        if (startTime == 0)
            return null;
        TrackCalendar rslt = new TrackCalendar();
        rslt.setTimeInMillis(startTime + player.getCurrentPosition());
        Log.d(Tag, funcname + ",returning " + rslt);
        return rslt;
    }

    public void pause() {
        final String funcname = "pause";
        Log.d(Tag, funcname + ",");

        assertUIThread();
        if (!isPaused) {
            Log.d(Tag, funcname + ",isPaused == false, maybe there is a player to pause");
            setPausedTrue();
            postEvent(EventType.NotBuffering);
            if (isPlayerReady) {
                Log.d(Tag, funcname + ",isPlayerReady == true, calling pause()");
                player.pause();
            }
            else { Log.d(Tag, funcname + ",isPlayerReady == false, nothing to pause"); }
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
            if (isPlayerReady) {
                Log.d(Tag, funcname + ",isPlayerReady == true, calling player.start()");
                player.start();
                postEvent(EventType.NotBuffering);
            } else if (getFileTask == null) {
                Log.d(Tag, funcname + ",getFileTask == null, need to adjust startTime and reopen");
                int curPos = player.getCurrentPosition();
                Log.d(Tag, funcname + ",player.getCurrentPosition() returned " + curPos);
                startTime += curPos;
                reopen();
            } else {
                Log.d(Tag, funcname + ",isPlayerReady == false && getFileTask != null, something is downloading");
                postEvent(EventType.Buffering);
            }
        }
        else { Log.d(Tag, funcname + ",isPaused == false, doing nothing"); }
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private class PlayerEvents implements
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener
    {
        {
            final String funcname = "PlayerEvents,<init>";
            Log.d(Tag, funcname + ",");
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            final String funcname = "PlayerEvents,onCompletion";
            Log.d(Tag, funcname + ",");
            giveUp();
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            final String funcname = "PlayerEvents,onPrepared";
            Log.d(Tag, funcname + ",");

            getFileTask = null;
            isPlayerReady = true;
            if (!isPaused) {
                Log.d(Tag, funcname + ",isPaused == false, calling player.start()");
                player.start();
                callEvent(EventType.NotBuffering);
            }
            else { Log.d(Tag, funcname + ",isPaused == true, not calling player.start()"); }
        }

    }

    private void callEvent(EventType ev) {
        final String funcname = "callEvent";
        Log.d(Tag, funcname + ",ev = " + ev);
        callEvent2(ev);
    }

    private void setPausedFalse() {
        if (isPaused) {
            isPaused = false;
            locksAcquire();
        }
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

    private void resetCommon() {
        final String funcname = "resetCommon";
        Log.d(Tag, funcname + ",");
        if (getFileTask != null) {
            Log.d(Tag, funcname + ", getFileTask != null, calling cancel()");
            getFileTask.cancel(false);
            getFileTask = null;
        }
        player.reset();
        isPlayerReady = false;
    }

    private void giveUp() {
        final String funcname = "giveUp";
        Log.d(Tag, funcname + ",");
        assertUIThread();
        assertFalse(isPaused);
        setPausedTrue();
        isPlayerReady = false;
        callEvent(EventType.Error);
    }

    private void setPausedTrue() {
        if (!isPaused) {
            isPaused = true;
            locksRelease();
        }
    }
}

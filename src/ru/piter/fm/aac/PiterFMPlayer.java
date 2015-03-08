/**
 * 
 */
package ru.piter.fm.aac;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

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

        getFileTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isCancelled()) return null;
                final String funcname = "doInBackground";
                Log.d(Tag, funcname + ",");
                try {
                    String streamUrl = b.doIt(channelId, startTime);
                    if (isCancelled()) return null;
                    player.setDataSource(streamUrl);
                    if (isCancelled()) return null;
                    player.prepare();
                } catch (Exception e) {
                    Log.e(Tag, funcname + ",Exception caught", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (getFileTask == this) {
                }
            };
        }.execute((Void[])null);
    }

    public TrackCalendar getPosition() {
        if (startTime == 0)
            return null;
        TrackCalendar rslt = new TrackCalendar();
        rslt.setTimeInMillis(startTime + player.getCurrentPosition());
        return rslt;
    }

    public void pause() {
        player.pause();
    }

    public void resume() {
        player.start();
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isPaused() {
        return true;
    }

    private class PlayerEvents implements MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener
    {
        {
            final String funcname = "PlayerEvents,";
            Log.d(Tag, funcname + ",");
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            final String funcname = "PlayerEvents,onCompletion";
            Log.d(Tag, funcname + ",");
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            final String funcname = "PlayerEvents,onPrepared";
            Log.d(Tag, funcname + ",");
            player.start();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            final String funcname = "PlayerEvents,onError";
            Log.d(Tag, funcname + ",");
            return false;
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
    }

    private void giveUp() {
        final String funcname = "giveUp";
        Log.d(Tag, funcname + ",");
        assertUIThread();
        assertFalse(isPaused);
        setPausedTrue();
        callEvent(EventType.Error);
    }

    private void setPausedTrue() {
        if (!isPaused) {
            isPaused = true;
            locksRelease();
        }
    }
}

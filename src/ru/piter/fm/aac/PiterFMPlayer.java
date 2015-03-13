/**
 * 
 */
package ru.piter.fm.aac;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import ru.piter.fm.App;
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

    private AsyncTask<?,?,?> openStreamTask;
    private MediaPlayer player;

    //private final StreamerUtil b = new StreamerUtil();
    private final CachingAacStreamProxy b = new CachingAacStreamProxy();
    final PlayerEvents playerEvents = new PlayerEvents();

    private long startTime;
    private String channelId;

    private boolean isPlayerReady;
    private boolean isPaused = true;

    private static final String Tag = "PiterFMPlayer";

    private final Handler handler = new Handler();

    { assertUIThread(); }

    private long nanosBegin;
    private long nanosTotal;

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
        openStreamTask = new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                if (isCancelled()) return null;
                final String funcname = "doInBackground";
                Log.d(Tag, funcname + ",");
                try {
                    String streamUrl = b.getStreamUrl(channelId, startTime);
                    if (isCancelled()) return null;
                    Log.d(Tag, funcname + ",calling player.setDataSource(),streamUrl = " + streamUrl);
                    player.setDataSource(streamUrl);
                    if (isCancelled()) return null;
                    Log.d(Tag, funcname + ",calling player.prepareAsync()");
                    player.prepareAsync();
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
                    giveUp();
                }
                // if ok, not clearing openStreamTask until prepared
            };
        }.execute((Void[])null);
    }

    public TrackCalendar getPosition() {
        final String funcname = "getPosition";
        Log.d(Tag, funcname + ",");

        if (startTime == 0)
            return null;
        TrackCalendar rslt = new TrackCalendar();
        int curPos = 0;
        if (isPlayerReady) {
            curPos = playerGetCurrentPosition();
        }
        rslt.setTimeInMillis(startTime + curPos);
        Log.d(Tag, funcname + ",returning " + rslt + " ( +" + curPos + "ms)");
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
                playerPaused();
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

                playerStart();

                postEvent(EventType.NotBuffering);
            } else if (openStreamTask == null) {
                Log.d(Tag, funcname + ",openStreamTask == null, need to reopen");
                reopen();
            } else {
                Log.d(Tag, funcname + ",isPlayerReady == false && openStreamTask != null, something is downloading");
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

    private void createPlayer() {
        final String funcname = "createPlayer";
        Log.d(Tag, funcname + ",");
        player = new MediaPlayer();
        player.setOnPreparedListener(playerEvents);
        player.setOnCompletionListener(playerEvents);
    }

    private class PlayerEvents implements
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener
    {
        @Override
        public void onCompletion(MediaPlayer mp) {
            final String funcname = "PlayerEvents,onCompletion";
            if (mp != player) {
                Log.w(Tag, funcname + ",got event from cancelled player");
                return;
            }
            if (!isPaused) {
                playerPaused();
            }
            Log.d(Tag, funcname + ",");
            giveUp();
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            final String funcname = "PlayerEvents,onPrepared";
            if (mp != player) {
                Log.w(Tag, funcname + ",got event from reset player");
                return;
            }
            Log.d(Tag, funcname + ",");

            openStreamTask = null;
            isPlayerReady = true;
            if (!isPaused) {
                Log.d(Tag, funcname + ",isPaused == false, calling player.start()");
                playerStart();
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
        if (openStreamTask != null) {
            Log.d(Tag, funcname + ", openStreamTask != null, calling cancel()");
            openStreamTask.cancel(false);
            openStreamTask = null;
        }
        nanosTotal = 0;
        if (player != null)
            player.reset();
        createPlayer();
        isPlayerReady = false;
    }

    private void giveUp() {
        final String funcname = "giveUp";
        Log.d(Tag, funcname + ",");
        assertUIThread();

        openStreamTask = null;
        setPausedTrue();
        if (isPlayerReady) {
            isPlayerReady = false;
            int curPos = playerGetCurrentPosition();
            Log.d(Tag, funcname + ",player.getCurrentPosition() returned " + curPos);
            startTime += curPos;
        }
        callEvent(EventType.Error);
    }

    private void setPausedTrue() {
        if (!isPaused) {
            isPaused = true;
            locksRelease();
        }
    }

    private void playerPaused() {
        nanosTotal += System.nanoTime() - nanosBegin;
    }

    private void playerStart() {
        nanosBegin = System.nanoTime();
        player.start();
    }

    private int playerGetCurrentPosition() {
        //return player.getCurrentPosition();
        long n = nanosTotal;
        if (player.isPlaying()) {
            n += System.nanoTime() - nanosBegin;
        }
        return (int)(n / 1000000);
    }
}

package ru.piter.fm.player;

import static junit.framework.Assert.*;

import java.io.IOException;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import ru.piter.fm.util.TrackCalendar;
import ru.piter.fm.util.PiterFMCachingDownloader;

public class PiterFMPlayer implements PlayerInterface {

    private static final int SDK_INT = getSDK_INT();

    private final Handler handler = new Handler();

    private final TrackCalendar trackCal = new TrackCalendar();
    private String channelId;
    private final PiterFMCachingDownloader cache = PiterFMCachingDownloader.INSTANCE;

    private AsyncTask<?,?,?> getFileTask;
    private final PlayerWrap player1 = new PlayerWrap();
    private final PlayerWrap player2 = new PlayerWrap();
    private MediaPlayer currentPlayer;

    private boolean isPaused = true;

    private EventHandler eventHandler;

    private boolean isNextPlayerSet;

    { assertUIThread(); }

    private void assertUIThread() { assertEquals(Looper.getMainLooper().getThread(), Thread.currentThread()); }

    /** "2010:11:13:20:31:12" */
    @Override
    public void open(String ch, String trackTimeStr) {
        final String funcname = "open";
        Log.d("PiterFMPlayer", funcname + ",ch = " + ch + ", trackTimeStr = " + trackTimeStr);
        assertUIThread();
        channelId = ch;
        trackCal.setTrackTime(trackTimeStr);

        isPaused = false;
        reopen();
    }

    private void reopen() {
        final String funcname = "reopen";
        Log.d("PiterFMPlayer", funcname + ",");

        postEvent(EventType.Buffering);

        currentPlayer = null;
        isNextPlayerSet = false;
        resetCommon();

        player1.scheduleGetFile();
    }

    private void resetCommon() {
        final String funcname = "reset";
        Log.d("PiterFMPlayer", funcname + ",");
        if (getFileTask != null) {
            Log.d("PiterFMPlayer", funcname + ", getFileTask != null, calling cancel()");
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
        public final MediaPlayer player = new MediaPlayer();
        public String path;

        /** Gingerbread has a bug that onSeekComplete() is called twice: 1st after seekTo() and 2nd after start() */
        private boolean onSeekCompleteCalled;

        private final int dbgId = player1 == null ? 1 : 2;

        {
            final String funcname = "PlayerWrap," + dbgId;
            Log.d("PiterFMPlayer", funcname + ",");
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnCompletionListener(this);
        }

        public void reset() {
            final String funcname = "PlayerWrap," + dbgId + ",reset";
            Log.d("PiterFMPlayer", funcname + ",");
            onSeekCompleteCalled = false;
            player.reset();
            if (path != null) {
                Log.d("PiterFMPlayer", funcname + ",path != null, calling releaseFile('" + path + "')");
                cache.releaseFile(path, false);
                path = null;
            }
        }

        public void scheduleGetFile() {
            final String funcname = "PlayerWrap," + dbgId + ",scheduleGetFile";
            Log.d("PiterFMPlayer", funcname + ",");
            assertNull(getFileTask);
            final String channelId = PiterFMPlayer.this.channelId;
            final TrackCalendar trackCal = PiterFMPlayer.this.trackCal.clone();
            Log.d("PiterFMPlayer", funcname + ",channelId = " + channelId + ", trackCal = " + trackCal.asURLPart());

            getFileTask = new AsyncTask<Void, Void, Void>() {
                private String path;

                @Override
                protected Void doInBackground(Void... params) {
                    final String funcname = "PlayerWrap," + dbgId + ",doInBackground";
                    Log.d("PiterFMPlayer", funcname + ",channelId = " + channelId + ", trackCal = " + trackCal.asURLPart());
                    try {
                        path = cache.getFile(channelId, trackCal);
                    } catch (Exception e) {
                        Log.d("PiterFMPlayer", funcname + ",Exception caught", e);
                    }
                    return null;
                }

                @Override
                protected void onCancelled() {
                    final String funcname = "PlayerWrap," + dbgId + ",onCancelled";
                    Log.d("PiterFMPlayer", funcname + ",");
                    if (path != null) {
                        Log.d("PiterFMPlayer", funcname + ",path != null, calling releaseFile('" + path + "')");
                        cache.releaseFile(path, false);
                    }
                };
    
                @Override
                protected void onPostExecute(Void result) {
                    final String funcname = "PlayerWrap," + dbgId + ",onPostExecute";
                    Log.d("PiterFMPlayer", funcname + ",");
                    assertNull(PlayerWrap.this.path);
                    if (path == null) {
                        Log.d("PiterFMPlayer", funcname + ",path == null, calling onError()");
                        internalOnError();
                    } else {
                        PlayerWrap.this.path = path;
                        try {
                            Log.d("PiterFMPlayer", funcname + ",calling player.setDataSource(), path = " + path);
                            player.setDataSource(path);
                            player.prepare();
                        } catch (IOException e) {
                            Log.d("PiterFMPlayer", funcname + ",Exception caught", e);
                            onError(null, 0, 0);
                        }
                    }
                }
            }.execute((Void[])null);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            final String funcname = "PlayerWrap," + dbgId + ",onError";
            Log.d("PiterFMPlayer", funcname + ",what = " + what + ", extra = " + extra);
            cache.releaseFile(path, true);
            path = null;
            internalOnError();
            return true;
        }

        private void internalOnError() {
            final String funcname = "PlayerWrap," + dbgId + ",internalOnError";
            Log.d("PiterFMPlayer", funcname + ",");
            // not resetting player now, because it won't be used until reopen()
            getFileTask = null;
            if (currentPlayer == null) {
                Log.d("PiterFMPlayer", funcname + ",currentPlayer == null, calling giveUp()");
                giveUp();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            final String funcname = "PlayerWrap," + dbgId + ",onPrepared";
            Log.d("PiterFMPlayer", funcname + ",");
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
            Log.d("PiterFMPlayer", funcname + ",currentPosition = " + mp.getCurrentPosition());
            if (onSeekCompleteCalled) {
                Log.d("PiterFMPlayer", funcname + ",2nd call of onSeekComplete(), ignoring");
                return;
            }
            internalOnSeekComplete();
		}

		private void internalOnSeekComplete() {
            final String funcname = "PlayerWrap," + dbgId + ",internalOnSeekComplete";
            Log.d("PiterFMPlayer", funcname + ",");
            onSeekCompleteCalled = true;
            getFileTask = null;
            PlayerWrap other = getOtherPlayer();
            // The other player is either playing now or has finished playing
            if (currentPlayer == null) {
                Log.d("PiterFMPlayer", funcname + ",currentPlayer == null, not trying to setNextMediaPlayer");
                currentPlayer = player;
                if (!isPaused) {
                    Log.d("PiterFMPlayer", funcname + ",isPaused == false, calling start()");
                    player.start();
                    callEvent(EventType.Playing);
                }
                else { Log.d("PiterFMPlayer", funcname + ",isPaused == true, not calling start()"); }
                trackCal.nextTrackTime();
                other.scheduleGetFile();
            } else {
                Log.d("PiterFMPlayer", funcname + ",currentPlayer != null, trying to set me as NextMediaPlayer");
                trySetNextMediaPlayer(other.player, player);
                isNextPlayerSet = true;
            }
        }

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		private void trySetNextMediaPlayer(MediaPlayer current, MediaPlayer next) {
		    if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		        current.setNextMediaPlayer(next);
		    }
		}

		private void tryStartNextMediaPlayer(MediaPlayer next) {
            //assertTrue(!isNextPlayerSet || SDK_INT < Build.VERSION_CODES.JELLY_BEAN || getOtherPlayer().player.isPlaying());
            if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                next.start();
                try { Thread.sleep(500); } catch (InterruptedException e) {} // yield a lot
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            final String funcname = "PlayerWrap," + dbgId + ",onCompletion";
            //Log.d("PiterFMPlayer", funcname + ",");
            if (isNextPlayerSet) {
                //Log.d("PiterFMPlayer", funcname + ",isNextPlayerSet == true, trying to start next mediaplayer");
                currentPlayer = getOtherPlayer().player;
                tryStartNextMediaPlayer(currentPlayer);
            }
            Log.d("PiterFMPlayer", funcname + ",");
            reset();
            if (isNextPlayerSet) {
                Log.d("PiterFMPlayer", funcname + ",isNextPlayerSet == true, calling scheduleGetFile()");
                isNextPlayerSet = false;
                trackCal.nextTrackTime();
                scheduleGetFile();
            } else if (getFileTask == null) {
                Log.w("PiterFMPlayer", funcname + ",isNextPlayerSet == false && getFileTask == null, calling giveUp()");
                currentPlayer = null;
                giveUp();
            } else {
                Log.d("PiterFMPlayer", funcname + "isNextPlayerSet == false && getFileTask != null, next file not downloaded yet");
                currentPlayer = null;
                callEvent(EventType.Buffering);
            }
        }

        private PlayerWrap getOtherPlayer() {
            //final String funcname = "PlayerWrap," + dbgId + ",getOtherPlayer";
            //Log.d("PiterFMPlayer", funcname + ",");
            return (this == player1 ? player2 : player1);
        }
    }

    @Override
    public void pause() {
        final String funcname = "pause";
        Log.d("PiterFMPlayer", funcname + ",");
        assertUIThread();
        if (!isPaused) {
            Log.d("PiterFMPlayer", funcname + ",isPaused == false, maybe there is a player to pause");
            isPaused = true;
            if (currentPlayer != null) {
                Log.d("PiterFMPlayer", funcname + ",currentPlayer != null, calling pause()");
                currentPlayer.pause();
            }
            else { Log.d("PiterFMPlayer", funcname + ",currentPlayer == null, nothing to pause"); }
        }
        else { Log.d("PiterFMPlayer", funcname + ",isPaused == true, doing nothing"); }
    }

    @Override
    public void resume() {
        final String funcname = "resume";
        Log.d("PiterFMPlayer", funcname + ",");
        assertUIThread();
        assertNotNull(channelId);
        if (isPaused) {
            Log.d("PiterFMPlayer", funcname + ",isPaused == true, maybe there is a player to resume");
            isPaused = false;
            if (currentPlayer != null) {
                Log.d("PiterFMPlayer", funcname + ",currentPlayer != null, calling start()");
                currentPlayer.start();
                postEvent(EventType.Playing);
            } else if (getFileTask == null) {
                Log.d("PiterFMPlayer", funcname + ",getFileTask == null, calling reopen()");
                reopen();
            } else {
                Log.d("PiterFMPlayer", funcname + ",currentPlayer == null && getFileTask != null, something is downloading");
                postEvent(EventType.Buffering);
            }
        }
        else { Log.d("PiterFMPlayer", funcname + ",isPaused == false, doing nothing"); }
    }

    @Override
    public String getChannelId() {
        assertUIThread();
        return channelId;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    private void giveUp() {
        final String funcname = "giveUp";
        Log.d("PiterFMPlayer", funcname + ",");
        assertFalse(isPaused);
        isPaused = true;
        callEvent(EventType.Error);
    }

    private void postEvent(final EventType ev) {
        final String funcname = "postEvent";
        Log.d("PiterFMPlayer", funcname + ",ev = " + ev);
        handler.post(new Runnable() {
            @Override
            public void run() {
                callEvent(ev);
            }
        });
    }

    private void callEvent(EventType ev) {
        final String funcname = "callEvent";
        Log.d("PiterFMPlayer", funcname + ",ev = " + ev);
        if (eventHandler != null)
            eventHandler.onEvent(ev);
    }

    @Override
    public void release() {
        resetCommon();
        player1.player.release();
        player2.player.release();
    }

    @Override
    public void setEventHandler(EventHandler handler) {
        assertUIThread();
        eventHandler = handler;
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    private static int getSDK_INT() {
        try {
            return Build.VERSION.SDK_INT;
        } catch(NoSuchFieldError e) {
            return 3;
        }
    }
}

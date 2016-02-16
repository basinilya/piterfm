package ru.piter.fm.player;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.Calendar;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import ru.piter.fm.player.PlayerInterface.EventType;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.util.TrackCalendar;
import ru.piter.fm.util.PiterFMCachingDownloader;

abstract class PiterFMPlayer {

    protected abstract void callEvent2(EventType ev);
    protected abstract void locksAcquire();
    protected abstract void locksRelease();

    private static final String Tag = "PiterFMPlayer";

    private final Handler handler = new Handler();

    private TrackCalendar nextChunkTime;
    private Channel channel;
    private final PiterFMCachingDownloader cache = PiterFMCachingDownloader.INSTANCE;

    private AsyncTask<?,?,?> getFileTask;
    private final PlayerWrap player1 = new PlayerWrap();
    private final PlayerWrap player2 = new PlayerWrap();
    {
        player1.otherPlayerWrap = player2;
        player2.otherPlayerWrap = player1;
    }

    private PlayerWrap currentPlayer;

    private boolean isPaused = true;

    private boolean isNextPlayerSet;

    { assertUIThread(); }

    protected void assertUIThread() { assertEquals(Looper.getMainLooper().getThread(), Thread.currentThread()); }

    public void open(Channel ch_, TrackCalendar trackTime) {
        final String funcname = "open";
        Log.d(Tag, funcname + ",ch = " + ch_.getChannelId() + ", trackTimeStr = " + trackTime);
        assertUIThread();

        channel = ch_;
        nextChunkTime = trackTime.clone();

        setPausedFalse();
        reopen();
    }

    public TrackCalendar getPosition() {
        TrackCalendar rslt;
        if (currentPlayer == null) {
            rslt = nextChunkTime;
        } else {
            rslt = currentPlayer.chunkTime;
            rslt.set(Calendar.SECOND, 0);
            rslt.set(Calendar.MINUTE, rslt.get(Calendar.MINUTE) / TrackCalendar.SEGMENT_MINUTES * TrackCalendar.SEGMENT_MINUTES);
            rslt.set(Calendar.MILLISECOND, currentPlayer.player.getCurrentPosition());
        }
        return rslt == null ? null : rslt.clone();
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
        public TrackCalendar chunkTime;
        public PlayerWrap otherPlayerWrap;
        public String path;

        @Override
        public String toString() {
            return "PlayerWrap_" + dbgId;
        }

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
            final String channelId = PiterFMPlayer.this.channel.getTomskStationId();
            chunkTime = nextChunkTime.clone();
            Log.d(Tag, funcname + ",channelId = " + channelId + ", trackCal = " + chunkTime.asURLPart());

            getFileTask = new AsyncTask<Void, Void, Void>() {
                private String path;

                @Override
                protected Void doInBackground(Void... params) {
                    final String funcname = "PlayerWrap," + dbgId + ",doInBackground";
                    Log.d(Tag, funcname + ",channelId = " + channelId + ", trackCal = " + chunkTime.asURLPart());
                    try {
                        path = cache.getFile(channelId, chunkTime);
                    } catch (Exception e) {
                        Log.e(Tag, funcname + ",Exception caught", e);
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
                            Log.e(Tag, funcname + ",Exception caught", e);
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
            if (currentPlayer != otherPlayerWrap) {
                // this check is for when error occurs in next player, while currentPlayer is playing
                Log.d(Tag, funcname + ",currentPlayer != otherPlayerWrap.player, calling giveUp()");
                giveUp();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            final String funcname = "PlayerWrap," + dbgId + ",onPrepared";
            Log.d(Tag, funcname + ",");
            int msec = nextChunkTime.getSeekTo();
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
            PlayerWrap other = otherPlayerWrap;
            // The other player is either playing now or has finished playing
            if (currentPlayer == null) {
                Log.d(Tag, funcname + ",currentPlayer == null, not trying to setNextMediaPlayer");
                currentPlayer = this;
                if (!isPaused) {
                    Log.d(Tag, funcname + ",isPaused == false, calling start()");
                    player.start();
                    callEvent(EventType.NotBuffering);
                }
                else { Log.d(Tag, funcname + ",isPaused == true, not calling start()"); }
                nextChunkTime.nextTrackTime();
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
                currentPlayer = otherPlayerWrap;
            }
            Log.d(Tag, funcname + ",");
            reset();
            if (isNextPlayerSet) {
                Log.d(Tag, funcname + ",isNextPlayerSet == true, calling scheduleGetFile()");
                isNextPlayerSet = false;
                nextChunkTime.nextTrackTime();
                scheduleGetFile();
            } else if (getFileTask == null) {
                Log.w(Tag, funcname + ",isNextPlayerSet == false && getFileTask == null, calling giveUp()");
                giveUp();
            } else {
                Log.d(Tag, funcname + "isNextPlayerSet == false && getFileTask != null, next file not downloaded yet");
                currentPlayer = null;
                callEvent(EventType.Buffering);
            }
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
                currentPlayer.player.pause();
            }
            else { Log.d(Tag, funcname + ",currentPlayer == null, nothing to pause"); }
        }
        else { Log.d(Tag, funcname + ",isPaused == true, doing nothing"); }
    }

    public void resume() {
        final String funcname = "resume";
        Log.d(Tag, funcname + ",");
        assertUIThread();
        assertNotNull(channel);
        assertNotNull(channel.getTomskStationId());
        if (isPaused) {
            Log.d(Tag, funcname + ",isPaused == true, maybe there is a player to resume");
            setPausedFalse();
            if (currentPlayer != null) {
                Log.d(Tag, funcname + ",currentPlayer != null, calling start()");
                currentPlayer.player.start();
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
        return channel == null ? null : channel.getChannelId();
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void giveUp() {
        final String funcname = "giveUp";
        Log.d(Tag, funcname + ",");
        currentPlayer = null; // it may be already null, if failed before seek success
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
        callEvent2(ev);
    }

    public void release() {
        setPausedTrue();
        resetCommon();
        player1.player.release();
        player2.player.release();
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

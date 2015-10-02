/**
 *
 */
package ru.piter.fm.player;

import ru.piter.fm.util.StatsCalc;
import ru.piter.fm.util.TrackCalendar;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.util.Log;

/**
 * This wrapper is not fully-featured and made solely to work with {@link PiterFMPlayer}. For example:
 * <ul>
 * <li/>it's not thread-safe
 * <li/>it doesn't support calling {@link #seekTo(int)} multiple times
 * <li/>{@link #setNextMediaPlayer(MediaPlayer)} can only be called at certain time
 * </ul>
 *
 * @author Ilya Basin
 *
 */
public class SmoothMediaPlayer extends MediaPlayer {

    public static final String LowerApiTag = "MediaPlayerLower";

    /** just call super */
    @SuppressLint("NewApi")
    public void setNextSmoothMediaPlayer(SmoothMediaPlayer next) {
        Log.v(LowerApiTag, dbgId + ",setNextMediaPlayer," + "next = " + next);
        super.setNextMediaPlayer(next);
    }

    public int getCurrentPositionNoLog() {
        return super.getCurrentPosition();
    }

    // BEGIN JUST LOGGERS
    public int getCurrentPosition() {
        int rslt = super.getCurrentPosition();
        Log.v(LowerApiTag, dbgId + ",getCurrentPosition," + "rslt = " + rslt);
        return rslt;
    }

    public boolean isPlaying() {
        boolean rslt = super.isPlaying();
        Log.v(LowerApiTag, dbgId + ",isPlaying," + "rslt = " + rslt);
        return rslt;
    }

    public void pause() throws IllegalStateException {
        Log.v(LowerApiTag, dbgId + ",pause,");
        super.pause();
    }

    public void prepare() throws java.io.IOException, IllegalStateException {
        Log.v(LowerApiTag, dbgId + ",prepare,");
        super.prepare();
    }

    public void release() {
        Log.v(LowerApiTag, dbgId + ",release,");
        super.release();
    }

    public void reset() {
        Log.v(LowerApiTag, dbgId + ",reset,");
        super.reset();
    }

    public void seekTo(int msec) throws IllegalStateException {
        Log.v(LowerApiTag, dbgId + ",seekTo," + "msec = " + msec);
        super.seekTo(msec);
    }

    public void setDataSource(String path) throws java.io.IOException, IllegalArgumentException, SecurityException,
            IllegalStateException {
        Log.v(LowerApiTag, dbgId + ",setDataSource," + "path = " + path);
        super.setDataSource(path);
    }

    private class DbgListener implements OnErrorListener, OnPreparedListener, OnSeekCompleteListener,
            OnCompletionListener {

        public void onCompletion(MediaPlayer mp) {
            Log.v(LowerApiTag, "onCompletion," + "mp = " + mp);
            if (onCompletionListener != null)
                onCompletionListener.onCompletion(mp);
        }

        public void onSeekComplete(MediaPlayer mp) {
            Log.v(LowerApiTag, "onSeekComplete," + "mp = " + mp);
            if (onSeekCompleteListener != null)
                onSeekCompleteListener.onSeekComplete(mp);
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.v(LowerApiTag, "onPrepared," + "mp = " + mp);
            if (onPreparedListener != null)
                onPreparedListener.onPrepared(mp);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.v(LowerApiTag, "onError," + "mp = " + mp + ", what = " + what + ", extra = " + extra);
            if (onErrorListener != null)
                return onErrorListener.onError(mp, what, extra);
            else
                return false;
        }
    }

    private OnCompletionListener onCompletionListener;
    private OnSeekCompleteListener onSeekCompleteListener;
    private OnErrorListener onErrorListener;
    private OnPreparedListener onPreparedListener;

    {
        DbgListener listener = new DbgListener();
        super.setOnErrorListener(listener);
        super.setOnPreparedListener(listener);
        super.setOnSeekCompleteListener(listener);
        super.setOnCompletionListener(listener);
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        Log.v(LowerApiTag, dbgId + ",setOnCompletionListener," + "listener = " + listener);
        onCompletionListener = listener;
    }

    public void setOnErrorListener(OnErrorListener listener) {
        Log.v(LowerApiTag, dbgId + ",setOnErrorListener," + "listener = " + listener);
        onErrorListener = listener;
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        Log.v(LowerApiTag, dbgId + ",setOnPreparedListener," + "listener = " + listener);
        onPreparedListener = listener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        Log.v(LowerApiTag, dbgId + ",setOnSeekCompleteListener," + "listener = " + listener);
        onSeekCompleteListener = listener;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        Log.v(LowerApiTag, dbgId + ",setVolume," + "leftVolume = " + leftVolume + ", rightVolume = " + rightVolume);
        super.setVolume(leftVolume, rightVolume);
    }

    public void start() throws IllegalStateException {
        internalStart();
    }
    // END JUST LOGGERS

    protected void internalStart() {
        Log.v(LowerApiTag, dbgId + ",start,");
        super.start();
    }


    protected static final String Tag = "SmoothMediaPlayer";

    /** Feature check */
    private static final boolean HAVE_SETNEXTMEDIAPLAYER = haveSetNextMediaPlayer();

    protected final int dbgId;

    @Override
    public String toString() {
        return "SmoothMediaPlayer_" + dbgId;
    }

    public static SmoothMediaPlayer newInstance(int dbgId) {
        if (HAVE_SETNEXTMEDIAPLAYER) {
            return new SmoothMediaPlayer(dbgId);
        } else {
            return new SmoothMediaPlayerImpl(dbgId);
        }
    }

    protected SmoothMediaPlayer(int dbgId) {
        this.dbgId = dbgId;
    }

    private static boolean haveSetNextMediaPlayer() {
        try {
            MediaPlayer.class.getMethod("setNextMediaPlayer", MediaPlayer.class);
            return true;
        } catch (NoSuchMethodException e1) {
            return false;
        }
    }
}

class SmoothMediaPlayerImpl extends SmoothMediaPlayer implements OnCompletionListener, OnSeekCompleteListener, OnErrorListener
{

    /** track position, where next track begins */
    private static final int OVERLAP_BEGIN_MS = TrackCalendar.SEGMENT_MINUTES * 60 * 1000;

    /**
     * Switch to next track approx this value before real {@link OnCompletionListener#onCompletion(MediaPlayer)
     * onCompletion()}
     */
    private static final int EARLY_SWITCH_MS = 500;

    /** Sleep to let player finish its operations in time */
    private static final int YIELD_MS = 50;

    /**
     * unpaused player skips some milliseconds: if entered
     * {@link MediaPlayer#start()} at system time 0ms and measured
     * {@link MediaPlayer#getCurrentPosition() getCurrentPosition()} at 200ms,
     * it may show 300. Then we consider that it skipped 100ms.
     */
    private static StatsCalc unpausedPlayerSkipsMs = new StatsCalc(5, 80, -50, 150);

    private OnCompletionListener onCompletionListener;
    private OnSeekCompleteListener onSeekCompleteListener;
    private OnErrorListener onErrorListener;
    private Handler handler = new Handler();

    private SmoothMediaPlayer nextPlayer;
    private Runnable activeEarlySwitchTimer;
    private Runnable activeOnSeekCompleteEvent;

    private boolean onCompletionCalled;
    private boolean onSeekCompleteCalled;

    public SmoothMediaPlayerImpl(int dbgId)
    {
        super(dbgId);
        super.setOnCompletionListener(this);
        super.setOnSeekCompleteListener(this);
        super.setOnErrorListener(this);
    }

    private void resetCommon() {
        onCompletionCalled = false;
        onSeekCompleteCalled = false;
        activeEarlySwitchTimer = null;
        activeOnSeekCompleteEvent = null;
        nextPlayer = null;
    }

    @Override
    public void reset() {
        final String funcname = dbgId + ",reset";
        Log.d(Tag, funcname + ",");
        setVolume(1.0f, 1.0f);
        resetCommon();
        super.reset();
    }

    @Override
    public void release() {
        final String funcname = dbgId + ",release";
        Log.d(Tag, funcname + ",");
        resetCommon();
        super.release();
    }

    @Override
    public void pause() throws IllegalStateException {
        final String funcname = dbgId + ",pause";
        Log.d(Tag, funcname + ",");
        activeEarlySwitchTimer = null;
        super.pause();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        activeEarlySwitchTimer = null;
        activeOnSeekCompleteEvent = null; // for when error was posted during warmup
        if (onErrorListener != null)
            return onErrorListener.onError(mp, what, extra);
        else
            return false;
    }

    @Override
    public void start() throws IllegalStateException {
        final String funcname = dbgId + ",start";
        Log.d(Tag, funcname + ",");
        onSeekCompleteCalled = true;
        scheduleEarlySwitch();
        super.start();
    }

    @Override
    public void setNextSmoothMediaPlayer(SmoothMediaPlayer next) {
        final String funcname = dbgId + ",setNextMediaPlayer";
        Log.d(Tag, funcname + ",next = " + next);
        nextPlayer = next; // next can be null
        if (isPlaying()) {
            scheduleEarlySwitch();
        }
    }

    private void scheduleEarlySwitch() {
        final String funcname = dbgId + ",scheduleEarlySwitch";
        Log.d(Tag, funcname + ",");
        if (nextPlayer == null)
            return;
        activeEarlySwitchTimer = new Runnable() {
            @Override
            public void run() {
                final String funcname = dbgId + ",Runnable,run";
                if (this == activeEarlySwitchTimer) {
                    Log.d(Tag, funcname + ",");
                    internalOnCompletion();
                }
            }
        };
        int curpos = getCurrentPosition();
        int nextPlayerPos = nextPlayer.getCurrentPosition();
        int delayMillis = OVERLAP_BEGIN_MS + nextPlayerPos - curpos - EARLY_SWITCH_MS;
        if (delayMillis < 0)
            delayMillis = 0;
        handler.postDelayed(activeEarlySwitchTimer, delayMillis);
        Log.d(Tag, funcname + ",delayMillis = " + delayMillis + ", curpos = " + curpos + ", nextPlayerPos = " + nextPlayerPos);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        internalOnCompletion();
    }

    private void internalOnCompletion() {
        final String funcname = dbgId + ",internalOnCompletion";
        Log.d(Tag, funcname + ",");
        if (onCompletionCalled)
            return;
        onCompletionCalled = true;

        if (nextPlayer != null) {
            nextPlayer.setVolume(0, 0);
            try {
                final int nextPlayerPosBeforeResume = nextPlayer.getCurrentPosition();
                final long unpausedPlayerSkipsAvg = unpausedPlayerSkipsMs.getAvg();
                final long whenToStartNextPlayer = OVERLAP_BEGIN_MS + nextPlayerPosBeforeResume + unpausedPlayerSkipsAvg;

                final int curposBefore = waitPosChange(this, 1); // synchronize with current player
                final long toSleep = whenToStartNextPlayer - curposBefore;

                if (toSleep > 0) {
                    Thread.sleep(toSleep);
                }

                final long nanosBeforeResumeNextPlayer = System.nanoTime();

                nextPlayer.internalStart();
                Thread.sleep(YIELD_MS);

                final int nextPlayerPosAfterResumeAndWait = waitPosChange(nextPlayer, 1); // synchronize with next player
                final long nanosAfterResumeAndWait = System.nanoTime();

                this.setVolume(0f, 0f);
                nextPlayer.setVolume(1.0f, 1.0f);

                final long unpausedPlayerSkipped =  (nextPlayerPosAfterResumeAndWait - nextPlayerPosBeforeResume) - ((nanosAfterResumeAndWait - nanosBeforeResumeNextPlayer) / M);
                unpausedPlayerSkipsMs.put(unpausedPlayerSkipped);

                Log.
                    i(Tag, funcname + ",after switch"
                            + ", unpausedPlayerSkipsAvg: " + unpausedPlayerSkipsAvg
                            + ", unpausedPlayerSkipped: " + unpausedPlayerSkipped
                            + ", pos: " + getCurrentPosition()
                            + ", nextPlayerPos: " + nextPlayer.getCurrentPosition()
                        );
            } catch (InterruptedException e) {
                //
            }
        }

        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(this);
        }
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        final String funcname = dbgId + ",seekTo";
        Log.d(Tag, funcname + ", msec = " + msec);
        if (msec == 2000) {
            /* Next track detected */
            onSeekCompleteCalled = true;

            Runnable successEv = activeOnSeekCompleteEvent = new Runnable() {
                public void run() {
                    if (this == activeOnSeekCompleteEvent) {
                        if (onSeekCompleteListener != null) {
                            onSeekCompleteListener.onSeekComplete(SmoothMediaPlayerImpl.this);
                        }
                    }
                }
            };

            warmUp();

            // maybe warmUp() failed and error posted; if true, this success event will be cancelled by our onError()
            handler.post(successEv);
        } else {
            super.seekTo(msec);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (onSeekCompleteCalled)
            return;
        onSeekCompleteCalled = true;

        if (onSeekCompleteListener != null) {
            onSeekCompleteListener.onSeekComplete(this);
        }
    }

    private void warmUp() {
        setVolume(0, 0);
        try {
            super.start();
            waitPosChange(this, 3);
            super.pause();
            Thread.sleep(YIELD_MS); // yield a lot
        } catch (InterruptedException e) {
            //
        }
        setVolume(1.0f, 1.0f);
    }

    /**/
    private static int waitPosChange(SmoothMediaPlayer pl, int nTimes) throws InterruptedException {
        int pos1 = pl.getCurrentPositionNoLog();
        int pos2 = pos1;
        long breakTime = System.nanoTime() + (500 * M);
        while(nTimes > 0) {
            for (;;) {
                if (System.nanoTime() - breakTime > 0)
                    return pos2;
                Thread.sleep(1);
                pos2 = pl.getCurrentPositionNoLog();
                if (pos1 != pos2) {
                    pos1 = pos2;
                    nTimes--;
                    break; // for
                }
            }
        }
        return pos2;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        onErrorListener = listener;
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        onSeekCompleteListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        onCompletionListener = listener;
    }

    private static final long M = 1000000L; // prevents typos
}

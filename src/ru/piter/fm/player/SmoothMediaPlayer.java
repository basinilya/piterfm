/**
 *
 */
package ru.piter.fm.player;

import ru.piter.fm.util.StatsCalc;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.util.Log;

/**
 * This wrapper is not fully-featured and made solely to work with {@link PiterFMPlayer}. For example:
 * <ul>
 * <li/>it's not thread-safe
 * <li/>it doesn't support calling {@link #seekTo(int)} multiple times
 * <li/>{@link #setNextMediaPlayer(MediaPlayer)} can only be called ar certain time
 * </ul>
 *
 * @author Ilya Basin
 *
 */
public class SmoothMediaPlayer extends MediaPlayer {
    protected static final String Tag = "SmoothMediaPlayer";

    /** Feature check */
    private static final boolean HAVE_SETNEXTMEDIAPLAYER = haveSetNextMediaPlayer();

    protected final int dbgId;

    @Override
    public String toString() {
        return "SmoothMediaPlayer," + dbgId;
    }

    public static SmoothMediaPlayer newInstance(int dbgId) {
        if (HAVE_SETNEXTMEDIAPLAYER) {
            return new SmoothMediaPlayer(dbgId);
        } else {
            return new SmoothMediaPlayerImpl(dbgId);
        }
    }

    /** just call super */
    @SuppressLint("NewApi")
    public void setNextSmoothMediaPlayer(SmoothMediaPlayer next) {
        super.setNextMediaPlayer(next);
    }

    protected SmoothMediaPlayer(int dbgId) {
        this.dbgId = dbgId;
    }

    protected void internalStart() {
        super.start();
    }

    protected void internalPause() {
        super.pause();
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

class SmoothMediaPlayerImpl extends SmoothMediaPlayer implements OnCompletionListener, OnSeekCompleteListener
{

    /** track position, where next track begins */
    private static final int OVERLAP_BEGIN_MS = 60000;

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
        final String funcname = dbgId + ",onCompletion,";
        Log.d(Tag, funcname + ",");
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
            warmUp();

            activeOnSeekCompleteEvent = new Runnable() {
                public void run() {
                    if (this == activeOnSeekCompleteEvent) {
                        if (onSeekCompleteListener != null) {
                            onSeekCompleteListener.onSeekComplete(SmoothMediaPlayerImpl.this);
                        }
                    }
                }
            };
            handler.post(activeOnSeekCompleteEvent);
        } else {
            super.seekTo(msec);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        final String funcname = dbgId + ",onSeekComplete,";
        Log.d(Tag, funcname + ",");
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
        int pos1 = pl.getCurrentPosition();
        int pos2 = pos1;
        long breakTime = System.nanoTime() + (500 * M);
        while(nTimes > 0) {
            for (;;) {
                if (System.nanoTime() - breakTime > 0)
                    return pos2;
                Thread.sleep(1);
                pos2 = pl.getCurrentPosition();
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
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        final String funcname = dbgId + ",setOnSeekCompleteListener,";
        Log.d(Tag, funcname + ",listener = " + listener);
        onSeekCompleteListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        final String funcname = dbgId + ",setOnCompletionListener,";
        Log.d(Tag, funcname + ",listener = " + listener);
        onCompletionListener = listener;
    }

    private static final long M = 1000000L; // prevents typos
}

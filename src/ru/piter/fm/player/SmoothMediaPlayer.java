/**
 * 
 */
package ru.piter.fm.player;

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

    /** Decoder produces silence at the beginning. You should seek next track beyond this value */
    private static final int DECODED_SILENCE_MS = 127;

    /**
     * OnPrepared does not guarantee immediate start. So warm-up muted player. Unfortunately, doesn't help after real
     * seekTo(), so only warm-up after prepare
     */
    private static final int WARMUP_MS = DECODED_SILENCE_MS;

    /**
     * Switch to next track approx this value before real {@link OnCompletionListener#onCompletion(MediaPlayer)
     * onCompletion()}
     */
    private static final int EARLY_SWITCH_MS = 400;

    /**
     * For precise switch time, I sleep less than needed, then check current time in a loop. This value specifies the
     * maximum duration of this loop
     */
    private static final int MAX_HASTE_MS = 50;

    /** Not sure it's needed */
    private static final int SLEEP_AFTER_SWITCH_MS = 50;

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
            Thread t = Thread.currentThread();
            try {
                int sleepInaccuracy = 0;
                long nLoops = 0;
                long realDelay = 0;
                int nextPlayerPos = nextPlayer.getCurrentPosition();
                long nanotimeBefore = System.nanoTime();
                int curposBefore = getCurrentPosition();
                final int remainingMs = OVERLAP_BEGIN_MS + nextPlayerPos - curposBefore;

                if (remainingMs > 0) {
                    int toSleep = remainingMs - maxSleepInaccuracy;
                    if (toSleep > 0) {
                        Thread.sleep(toSleep);
                        sleepInaccuracy = (int)((System.nanoTime() - nanotimeBefore) / M) - toSleep;
                        inaccuracyStats_put(sleepInaccuracy);
                    }
    
                    long breakTime = nanotimeBefore + (remainingMs * M);
                    long remainingNanos;
                    for (;;nLoops++) {
                        remainingNanos = breakTime - System.nanoTime();
                        if (remainingNanos <= 0)
                            break;
                        //java.util.concurrent.locks.LockSupport.parkNanos(remainingNanos);
                    }
                    realDelay = (breakTime - remainingNanos - nanotimeBefore) / M;
                }

                nextPlayer.internalStart();
                super.pause();

                if (SLEEP_AFTER_SWITCH_MS > 0)
                    Thread.sleep(SLEEP_AFTER_SWITCH_MS); // yield a lot
                int curposAfter = getCurrentPosition();
                Log.
                    i(Tag, funcname + ",called nextPlayer.start()"
                        + ", stopped at: " + curposAfter
                        + ", nextPlayerPos was: " + nextPlayerPos
                        + ", curposBefore was: " + curposBefore
                        + ", remainingMs was: " + remainingMs
                        + ", real delay was: " + realDelay
                        + ", nLoops: " + nLoops
                        + ", sleepInaccuracy: " + sleepInaccuracy
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
        if (msec == 2000)
            msec = DECODED_SILENCE_MS; /*
                                        * Next track detected, but only play without seek assures seamless playback.
                                        * Changing to the minimal value
                                        */
        if (msec > WARMUP_MS) {
            super.seekTo(msec);
        } else {
            onSeekCompleteCalled = true;
            warmUp(msec);

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

    private void warmUp(int desiredPos) {
        setVolume(0, 0);
        int delay = desiredPos - getCurrentPosition();

        Thread t = Thread.currentThread();
        int oldPrio = t.getPriority();
        t.setPriority(Thread.MAX_PRIORITY);
        try {
            super.start();
            if (delay > 0)
                Thread.sleep(delay);
            super.pause();
        } catch (InterruptedException e) {
            //
        } finally {
            t.setPriority(oldPrio);
        }
        setVolume(1.0f, 1.0f);
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

    /** contains maximum sleep inaccuracy seen lately */
    private int maxSleepInaccuracy = MAX_HASTE_MS;

    // ring buffer for stats values
    private int[] inaccuracyStatsRbuf = new int[] { maxSleepInaccuracy ,0,0,0,0 };
    private int inaccuracyStatsRbuf_index = 0;

    private void inaccuracyStats_put(int newval) {
        if (newval > MAX_HASTE_MS)
            newval = MAX_HASTE_MS;
        inaccuracyStatsRbuf_index = (inaccuracyStatsRbuf_index + 1) % inaccuracyStatsRbuf.length;
        inaccuracyStatsRbuf[inaccuracyStatsRbuf_index] = newval;
        maxSleepInaccuracy = 0;
        for (int val : inaccuracyStatsRbuf) {
            if (maxSleepInaccuracy < val)
                maxSleepInaccuracy = val;
        }
    }

    private static final long M = 1000000L; // prevents typos
}

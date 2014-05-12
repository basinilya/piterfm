package ru.piter.fm.util;

public class PreciseSleeper {
    public static void sleep(int remainingMs) throws InterruptedException {
        long nanotimeBefore = System.nanoTime();
        int sleepInaccuracy;
        long nLoops = 0;
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
        }
    }

    /**
     * For precise switch time, I sleep less than needed, then check current time in a loop. This value specifies the
     * maximum duration of this loop
     */
    private static final int MAX_HASTE_MS = 50;

    /** contains maximum sleep inaccuracy seen lately */
    private static int maxSleepInaccuracy = MAX_HASTE_MS;

    private static final long M = 1000000L; // prevents typos

    // ring buffer for stats values
    private static int[] inaccuracyStatsRbuf = new int[] { maxSleepInaccuracy ,0,0,0,0 };
    private static int inaccuracyStatsRbuf_index = 0;

    private static void inaccuracyStats_put(int newval) {
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
}

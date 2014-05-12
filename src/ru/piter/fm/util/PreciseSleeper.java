package ru.piter.fm.util;

public class PreciseSleeper {
    public static void sleep(long remainingMs) throws InterruptedException {
        long nanotimeBefore = System.nanoTime();
        long sleepInaccuracy;
        if (remainingMs > 0) {
            long toSleep = remainingMs - inaccuracyStats.getMax();
            if (toSleep > 0) {
                Thread.sleep(toSleep);
                sleepInaccuracy = ((System.nanoTime() - nanotimeBefore) / M) - toSleep;
                inaccuracyStats.put(sleepInaccuracy);
            }

            long breakTime = nanotimeBefore + (remainingMs * M);
            long remainingNanos;
            for (;;) {
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


    private static final long M = 1000000L; // prevents typos

    /** calculates maximum sleep inaccuracy seen lately */
    private static StatsCalc inaccuracyStats = new StatsCalc(5, MAX_HASTE_MS, 0, MAX_HASTE_MS);
}

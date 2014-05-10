package ru.piter.fm.util;

public class StatsCalc {

    private final long[] rbuf;
    private int rbuf_index = 0;
    private long bound_min, bound_max;

    public StatsCalc(int sz, long dflt, long bound_min, long bound_max) {
        rbuf = new long[sz];
        for(;sz > 0;) {
            sz--;
            rbuf[sz] = dflt;
        }
        this.bound_min = bound_min;
        this.bound_max = bound_max;
    }

    public void put(long newval) {
        if (newval > bound_max)
            newval = bound_max;
        else if (newval < bound_min)
            newval = bound_min;

        rbuf_index = (rbuf_index + 1) % rbuf.length;
        rbuf[rbuf_index] = newval;
    }

    public long getAvg() {
        if (rbuf.length == 0)
            return 0;
        long avg = 0;
        for (long val : rbuf) {
            avg += val;
        }
        return avg / rbuf.length;
    }

    public long getMax() {
        long max = 0;
        for (long val : rbuf) {
            if (max < val)
                max = val;
        }
        return max;
    }
}

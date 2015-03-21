package ru.piter.fm.test.server;

import com.google.common.util.concurrent.RateLimiter;

public class Throttler {

	private final double FREQ = 4.0;

	private RateLimiter r;

	public Throttler(double bytesPerSecond) {
		r = RateLimiter.create(bytesPerSecond);
	}

	public int bestBufsz() {
		double desiredBps = r.getRate();
		return Math.max(1, (int)Math.min(desiredBps / FREQ, Integer.MAX_VALUE));
	}

	public void acquire(int nbytes) {
		r.acquire(nbytes);
	}
}

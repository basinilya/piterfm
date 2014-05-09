package ru.piter.fm.test;

import java.io.IOException;

import org.foo.StopButtonInstrumentationTestCase;

import ru.piter.fm.player.Synchronizer;
import ru.piter.fm.util.TrackCalendar;
import android.os.Handler;
import android.os.Looper;
import android.test.InstrumentationTestCase;
import android.util.Log;

public class PiterFMCachingDownloaderTest extends InstrumentationTestCase {

	public static final String T = "PiterFMPlayerCacheTest";

	private Synchronizer player;
	private boolean setupOk;

	protected void setUp() throws Exception {
		Log.d(T, "setUp");
		super.setUp();
		StopButtonInstrumentationTestCase.showStopButton(getInstrumentation(), getName());

		// create player in UI thread
		final Object lock = new Object();
		synchronized (lock) {
			setupOk = false;
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					Log.d(T, "I'm in MainLooper thread");
					player = new Synchronizer();
					synchronized (lock) {
						lock.notify();
						setupOk = true;
					}
				}
			});
			lock.wait(10000);
		}
		assertTrue(setupOk);
	}

	@Override
	protected void tearDown() throws Exception {
		Log.d(T, "tearDown");
		super.tearDown();
		StopButtonInstrumentationTestCase.hideStopButton(getInstrumentation());
	}

	public void testB() throws InterruptedException, IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		String ch = "7822";
		TrackCalendar trackTime = new TrackCalendar();
		trackTime.setTrackTime("2014:05:07:12:39:00");
		player.open(ch, trackTime.asTrackTime());
		Thread.sleep(1200000); // should be stopped by the stop button on device screen
	}
}
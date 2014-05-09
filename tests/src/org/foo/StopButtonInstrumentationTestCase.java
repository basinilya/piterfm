package org.foo;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class StopButtonInstrumentationTestCase 

{ /* extends android.test.InstrumentationTestCase {

	public void testSleep() throws Exception {
		Thread.sleep(10000);
	}

	public void testSleep2() throws Exception {
		Thread.sleep(10000);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		showStopButton(getInstrumentation(), getName());
	}

	@Override
	protected void tearDown() throws Exception {
		hideStopButton(getInstrumentation());
		super.tearDown();
	}
*/
	private static final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.exit(1);
		}
	};

	public static void showStopButton(Instrumentation instrumentation, String testName) {
		// register stop tests receiver
		Context targetCtx = instrumentation.getTargetContext();
		targetCtx.registerReceiver(receiver, new IntentFilter(StopperActivity.ACTION));

		// show stop tests button
		Context ctx = instrumentation.getContext();
		Intent intent = new Intent(ctx, StopperActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(StopperActivity.PARAM_TEST_NAME, testName);
		ctx.startActivity(intent);
	}

	public static void hideStopButton(Instrumentation instrumentation) {
		Context targetCtx = instrumentation.getTargetContext();
		targetCtx.unregisterReceiver(receiver);
		// hide stop tests button
		Context ctx = instrumentation.getContext();
		Intent intent = new Intent(ctx, StopperActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP); // android:launchMode="singleTop" doesn't work in old versions
		intent.putExtra(StopperActivity.PARAM_KILL, true);
		ctx.startActivity(intent);
	}
}

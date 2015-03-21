package org.foo;

import java.util.Date;

import com.google.common.util.concurrent.RateLimiter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class TestRateLimiter  extends InstrumentationTestCase {
    public static final String T = "RememberHowToTest";

    private boolean backgroundTaskOk;
    private Handler handler;
    private Object lock = new Object();
    
    protected void setUp() throws Exception {
        Log.d(T, "setUp");
        super.setUp();
        StopButtonInstrumentationTestCase.showStopButton(getInstrumentation(), getName());
        handler = new Handler(Looper.getMainLooper());

    }

    @Override
    protected void tearDown() throws Exception {
        Log.d(T, "tearDown");
        super.tearDown();
        StopButtonInstrumentationTestCase.hideStopButton(getInstrumentation());
    }
    
    public void testAdapter() throws Exception {
    	RateLimiter r = RateLimiter.create(1.0);
    	for (int i = 0; i < 10; i++) {
    		Log.d(T, "" + new Date());
    		r.acquire();
    	}
    }



}

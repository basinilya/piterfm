package org.foo;

import ru.piter.fm.test.MediaPlayerTest;
import android.app.ListFragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;

public class RememberHowToTest  extends InstrumentationTestCase {
    public static final String T = "RememberHowToTest";

    private boolean backgroundTaskOk;
    private Handler handler;
    private Object lock = new Object();
    
    protected void setUp() throws Exception {
        Log.d(T, "setUp");
        super.setUp();
        StopButtonInstrumentationTestCase.showStopButton(getInstrumentation(), getName());
        handler = new Handler(Looper.getMainLooper());

        synchronized (lock) {
            backgroundTaskOk = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(T, "I'm in MainLooper thread");

                    synchronized (lock) {
                        lock.notify();
                        backgroundTaskOk = true;
                    }
                }
            });
            lock.wait(100000);
        }
        assertTrue(backgroundTaskOk);
    }

    @Override
    protected void tearDown() throws Exception {
        Log.d(T, "tearDown");
        super.tearDown();
        StopButtonInstrumentationTestCase.hideStopButton(getInstrumentation());
    }

    private ArrayAdapter<String> adapter;
    private int count;
    private Filter filter;

    private void _testAdapterStep1() {
        Context ctx = getInstrumentation().getContext();
        adapter = new ArrayAdapter<String>(ctx, 0);
        adapter.add("a");

        count = adapter.getCount();
        Log.d(T, "original count: " + count);

        filter = adapter.getFilter();
        filter.filter("b");

        count = adapter.getCount();
        Log.d(T, "count after calling filter():" + count);

        handler.postDelayed(new Runnable() { public void run() {
            _testAdapterStep2();
        } }, 1000);
    }

    private void _testAdapterStep2() {

        count = adapter.getCount();
        Log.d(T, "count after postDelayed: " + count);

        filter.filter("");
        
        handler.postDelayed(new Runnable() { public void run() {
            _testAdapterStep3();
        } }, 1000);
    }

    private void _testAdapterStep3() {

        count = adapter.getCount();
        Log.d(T, "count after postDelayed: " + count);

        handler.post(new Runnable() { public void run() {
            _testAdapterStep4();
        } });
    }

    private void _testAdapterStep4() {
        handler.post(new Runnable() { public void run() {
            _testAdapterStepLast();
        } });
    }

    private void _testAdapterStepLast() {
        synchronized (lock) {
            lock.notify();
            backgroundTaskOk = true;
        }
    }
    
    public void testAdapter() throws Exception {

        synchronized (lock) {
            backgroundTaskOk = false;
            handler.post(new Runnable() { public void run() {
                _testAdapterStep1();
            } });
            lock.wait(100000);
        }
        assertTrue(backgroundTaskOk);
    }



}

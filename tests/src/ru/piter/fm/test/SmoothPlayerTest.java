/**
 * 
 */
package ru.piter.fm.test;

import org.foo.StopButtonInstrumentationTestCase;

import ru.piter.fm.player.SmoothMediaPlayer;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.test.InstrumentationTestCase;
import android.util.Log;

public class SmoothPlayerTest extends InstrumentationTestCase implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {

    public static final String T = "PiterFMPlayerCacheTest";

    private SmoothMediaPlayer player1;
    private SmoothMediaPlayer player2;
    private boolean setupOk;
    private Handler handler;

    protected void setUp() throws Exception {
        Log.d(T, "setUp");
        super.setUp();
        StopButtonInstrumentationTestCase.showStopButton(getInstrumentation(), getName());

        handler = new Handler(Looper.getMainLooper());

        // create player in UI thread
        final Object lock = new Object();
        synchronized (lock) {
            setupOk = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(T, "I'm in MainLooper thread");
                    player1 = SmoothMediaPlayer.newInstance(1);
                    player2 = SmoothMediaPlayer.newInstance(2);
                    player1.setOnPreparedListener(SmoothPlayerTest.this);
                    player2.setOnPreparedListener(SmoothPlayerTest.this);
                    player1.setOnSeekCompleteListener(SmoothPlayerTest.this);
                    player2.setOnSeekCompleteListener(SmoothPlayerTest.this);

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

    public void testB() throws Exception {
        handler.post(new Runnable() {
            @SuppressLint({ "NewApi", "SdCardPath" })
            @Override
            public void run() {
                String f1 = "/mnt/sdcard/piterfm/1.mp4";
                String f2 = "/mnt/sdcard/piterfm/2.mp4";
                
                try {
                    player1.setDataSource(f1);
                    player2.setDataSource(f2);
                    player1.prepare();
                    player2.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        Thread.sleep(1200000); // should be stopped by the stop button on device screen
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("SmoothMediaPlayer", "test onPrepared");
        if (mp == player2) {
            player2.seekTo(2000);
        } else {
            //player1.start();
            
            player1.seekTo(50000);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d("SmoothMediaPlayer", "test onSeekComplete, pos = " + mp.getCurrentPosition());
        if (mp == player1) {
            player1.start();
        }
        if (mp == player2) {
            player1.setNextSmoothMediaPlayer(player2);
        }
    }
}

package ru.piter.fm.test;

import java.io.File;

import org.foo.StopButtonInstrumentationTestCase;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.test.InstrumentationTestCase;
import android.util.Log;

public class MediaPlayerTest extends InstrumentationTestCase implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {

    public static final String T = "MediaPlayerTest";

    private MediaPlayer player1;
    private MediaPlayer player2;
    private boolean setupOk;
    private Handler handler;

    private boolean onSeekCompleteCalled1;
    private boolean onSeekCompleteCalled2;

    protected void setUp() throws Exception {
        Log.d(T, "setUp");
        super.setUp();
        StopButtonInstrumentationTestCase.showStopButton(getInstrumentation(), getName());

        onSeekCompleteCalled1 = false;
        onSeekCompleteCalled2 = false;
        handler = new Handler(Looper.getMainLooper());

        // create player in UI thread
        final Object lock = new Object();
        synchronized (lock) {
            setupOk = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(T, "I'm in MainLooper thread");
                    player1 = new MediaPlayer();
                    player2 = new MediaPlayer();
                    player1.setOnPreparedListener(MediaPlayerTest.this);
                    player2.setOnPreparedListener(MediaPlayerTest.this);
                    player1.setOnSeekCompleteListener(MediaPlayerTest.this);
                    player2.setOnSeekCompleteListener(MediaPlayerTest.this);

                    synchronized (lock) {
                        lock.notify();
                        setupOk = true;
                    }
                }
            });
            lock.wait(100000);
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
            @SuppressLint({ "NewApi" })
            @Override
            public void run() {
                String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
                String f1 = sdcard + "/piterfm/1.mp4";
                String f2 = sdcard + "/piterfm/2.mp4";
                
                try {
                    Log.d(T, f1);
                    assertTrue(new File(f1).exists());
                    player1.setDataSource(f1);
                    assertTrue(new File(f2).exists());
                    Log.d(T, f2);
                    player2.setDataSource(f2);
                    player1.prepare();
                    player2.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        Thread.sleep(8000);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(T, "test onPrepared");
        if (mp == player2) {
            try {
                Thread.sleep(1);
            //player2.seekTo(2000);
            //player2.seekTo(500);
            for(int i = 0; i < 2; i++) {
            int posNew;
            StringBuilder sb = new StringBuilder("again\n");
            long nLoops = 0;

            long nanosBeg = System.nanoTime();
            long nanosCur = nanosBeg;
            long nanosBeg2 = nanosBeg;
            player2.start();
            //Thread.sleep(200);
            int posOld = player2.getCurrentPosition();
            for(;nanosCur - nanosBeg < 1000 * 1000000L;nLoops++) {
                posNew = player2.getCurrentPosition();
                if (posOld != posNew) {
                    nanosCur = System.nanoTime();
                    //sb.append(posNew).append(" (").
                    sb.append(posNew - posOld).append("\t").append((nanosCur-nanosBeg2)/1000000L).append("\n");
                    posOld = posNew;
                    nanosBeg2 = nanosCur;
                }
                //Thread.sleep(1);
            }
            Log.d(T, sb.toString());
            }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
        if (mp == player1) {
            //player1.start();
            
            player1.seekTo(50000);
        }
    }

    public void onSeekComplete(MediaPlayer mp) {
        Log.d(T, "test onSeekComplete, pos = " + mp.getCurrentPosition());
        if (mp == player1) {
            if (onSeekCompleteCalled1)
                return;
            onSeekCompleteCalled1 = true;
            //player1.start();
        }
        if (mp == player2) {
            if (onSeekCompleteCalled2)
                return;
            onSeekCompleteCalled2 = true;
            //player1.setNextMediaPlayer(player2);
        }
    }
}

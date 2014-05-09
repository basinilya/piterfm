package ru.piter.fm.player;

import android.os.Handler;
import android.os.Looper;

/** Temporary workaround until PlayerTask is removed. */
public class Synchronizer implements PlayerInterface, PlayerInterface.EventHandler {

    private final PiterFMPlayer playerInstance = new PiterFMPlayer();
    private final Handler handler = new Handler();
    private final Object lock = new Object();
    private RuntimeException runtEx;
    protected boolean isIncomplete;

    {
        playerInstance.setEventHandler(this);
    }

    private abstract class MySender implements Runnable {
        public MySender() {
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                run2();
            } else {
                runtEx = null;
                synchronized(lock) {
                    handler.post(this);
                    try { lock.wait(); } catch (InterruptedException e) { throw new RuntimeException(e); }
                }
                if (runtEx != null) throw runtEx;
            }
        }

        @Override
        public final void run() {
            try {
                run2();
            } catch (RuntimeException e) {
                runtEx = e;
            }
            if (!isIncomplete) {
                synchronized(lock) {
                    lock.notify();
                }
            }
        }
        public abstract void run2();
    }

    @Override
    public void onEvent(EventType ev) {
        if (isIncomplete && ev != EventType.Buffering) {
            synchronized(lock) {
                lock.notify();
                isIncomplete = false;
            }
        }
    }

    @Override
    public synchronized void open(final String channelId, final String trackTime) {
        new MySender() {
            @Override
            public void run2() {
                playerInstance.open(channelId, trackTime);
                isIncomplete = true;
            }
        };
    }

    @Override
    public synchronized void resume() {
        new MySender() {
            @Override
            public void run2() {
                playerInstance.resume();
                isIncomplete = true;
            }
        };
    }

    @Override
    public synchronized void pause() {
        new MySender() {
            @Override
            public void run2() {
                playerInstance.pause();
            }
        };
    }

    @Override
    public synchronized String getChannelId() {
        return new MySender() {
            String rslt;
            @Override
            public void run2() {
                rslt = playerInstance.getChannelId();
            }
        }.rslt;
    }

    @Override
    public synchronized boolean isPaused() {
        return new MySender() {
            boolean rslt;
            @Override
            public void run2() {
                rslt = playerInstance.isPaused();
            }
        }.rslt;
    }

    @Override
    public void setEventHandler(EventHandler handler) {
        // TODO Auto-generated method stub
    }

    @Override
    public void release() {
        playerInstance.release();
    }
}

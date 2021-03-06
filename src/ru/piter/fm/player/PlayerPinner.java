/**
 * 
 */
package ru.piter.fm.player;

import java.util.ArrayList;

import ru.piter.fm.radio.Channel;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.TrackCalendar;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * @author Ilya Basin
 *
 */
public class PlayerPinner extends PiterFMPlayer implements PlayerInterface {

    private PowerManager.WakeLock cpuWakeLock;

    private Notification currentNotif;
    private boolean delayStartForeground;

    private boolean wasPausedByMe;

    private Service svc;

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (wasPausedByMe) {
                        wasPausedByMe = false;
                        resume();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    /* fallthrough */
                case TelephonyManager.CALL_STATE_RINGING:
                    if (!isPaused()) {
                        wasPausedByMe = true;
                        pause();
                    }
                    break;
            }
        }
    };

    public PlayerPinner(Context ctx) {
        cpuWakeLock = ((PowerManager) ctx.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "PiterFMPlayerWakeLock");

        TelephonyManager tm = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        ctx.bindService(new Intent(ctx, PlayerService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            svc = ((PlayerService.PlayerServiceListener) service).getService();
            if (delayStartForeground) {
                startForeground();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            svc = null;
        }
    };

    private final ArrayList<EventHandler> eventHandlers = new ArrayList<EventHandler>();

    @Override
    public void addEventHandler(EventHandler handler) {
        assertUIThread();
        eventHandlers.add(handler);
    }

    @Override
    public void removeEventHandler(EventHandler handler) {
        assertUIThread();
        eventHandlers.remove(handler);
    }

    @Override
    protected void callEvent2(EventType ev) {
        if (ev == EventType.Error) {
            Notifications.show(Notifications.CANT_LOAD_TRACK, new Intent());
        }
        @SuppressWarnings("unchecked")
        ArrayList<EventHandler> eventHandlers = (ArrayList<EventHandler>)this.eventHandlers.clone();
        for (EventHandler handler : eventHandlers) {
            handler.onEvent(ev);
        }
    }

    @Override
    protected void locksAcquire() {
        cpuWakeLock.acquire();
        if (svc != null) {
            startForeground();
        } else {
            delayStartForeground = true;
        }
    }

    @Override
    protected void locksRelease() {
        if (svc != null) {
            stopForeground();
        } else {
            delayStartForeground = false;
        }
        cpuWakeLock.release();
    }

    @SuppressLint("NewApi")
    private void startForeground() {
        svc.startForeground(Notifications.PLAY_STOP, currentNotif);
    }

    @SuppressLint("NewApi")
    private void stopForeground() {
        svc.stopForeground(true);
    }

    @Override
    public void open(Intent notificationIntent, Channel channel, TrackCalendar trackTime) {
        beforeOpenOrResume(notificationIntent);
        super.open(channel, trackTime);
    }

    @Override
    public void resume(Intent notificationIntent) {
        beforeOpenOrResume(notificationIntent);
        super.resume();
    }

    private void beforeOpenOrResume(Intent notificationIntent) {
        wasPausedByMe = false;
        currentNotif = Notifications.newNotification(Notifications.PLAY_STOP, notificationIntent);
    }
}

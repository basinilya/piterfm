package ru.piter.fm.player;

import ru.piter.fm.util.Notifications;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 30.08.2010
 * Time: 15:37:13
 * To change this template use File | SettingsActivity | File Templates.
 */
public class PlayerService extends Service implements PlayerInterface {

    private final IBinder mBinder = new PlayerServiceListener();

    private NotificationManager nm;

    private PiterFMPlayer player;

    private PowerManager.WakeLock cpuWakeLock;

    private Notification currentNotif;
    private boolean notifVisible;

    private boolean wasPausedByMe;

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (wasPausedByMe) {
                        wasPausedByMe = false;
                        player.resume();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    /* fallthrough */
                case TelephonyManager.CALL_STATE_RINGING:
                    if (!player.isPaused()) {
                        wasPausedByMe = true;
                        player.pause();
                    }
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        cpuWakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "PiterFMPlayerWakeLock");

        player = new PiterFMPlayer() {
            @Override
            protected void locksAcquire() {
                super.locksAcquire();
                cpuWakeLock.acquire();
                startForeground(Notifications.PLAY_STOP, currentNotif);
                notifVisible = true;
            }
            @Override
            protected void locksRelease() {
                stopForeground(true);
                notifVisible = false;
                cpuWakeLock.release();
                super.locksRelease();
            }
        };

        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        player.release();
        super.onDestroy();
    }

    public class PlayerServiceListener extends Binder {
        public PlayerInterface getService() {
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void open(Intent notificationIntent, String channelId, String trackTime) {
        beforeOpenOrResume(notificationIntent);
        player.open(channelId, trackTime);
        afterOpenOrResume();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void resume(Intent notificationIntent) {
        beforeOpenOrResume(notificationIntent);
        player.resume();
        afterOpenOrResume();
    }

    @Override
    public String getChannelId() {
        return player.getChannelId();
    }

    @Override
    public boolean isPaused() {
        return player.isPaused();
    }

    @Override
    public void setEventHandler(EventHandler handler) {
        player.setEventHandler(handler);
    }

    private void beforeOpenOrResume(Intent notificationIntent) {
        wasPausedByMe = false;
        currentNotif = Notifications.newNotification(Notifications.PLAY_STOP, notificationIntent);
        notifVisible = false;
    }

    private void afterOpenOrResume() {
        if (!notifVisible) {
            nm.notify(Notifications.PLAY_STOP, currentNotif);
        }
    }
}

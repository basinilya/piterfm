package ru.piter.fm.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import ru.piter.fm.App;
import ru.piter.fm.R;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 23.09.2010
 * Time: 0:36:18
 * To change this template use File | SettingsActivity | File Templates.
 */
public class Notifications {

    public static final int SD_CARD_UNAVAILABLE = 1;
    public static final int CANT_LOAD_CHANNELS = 2;
    public static final int CANT_LOAD_TRACKS = 3;
    public static final int CANT_LOAD_TRACK = 4;
    public static final int PLAY_STOP = 5;


    private static Context context = App.getContext();
    private static NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    public static void show(int notificationId, Intent intent) {
        String message = "";
        Notification notification = new Notification(R.drawable.logo, "", System.currentTimeMillis());
        PendingIntent contentIntent = null;
        switch (notificationId) {
            case SD_CARD_UNAVAILABLE:
                message = context.getResources().getString(R.string.sdCardUnavailable);
                break;
            case CANT_LOAD_CHANNELS:
                message = context.getResources().getString(R.string.cantLoadChannels);
                break;
            case CANT_LOAD_TRACKS:
                message = context.getResources().getString(R.string.cantLoadTracks);
                break;
            case CANT_LOAD_TRACK:
                message = context.getResources().getString(R.string.cantLoadTrack);
                break;
            case PLAY_STOP:
                Serializable channel = intent.getExtras().getSerializable("channel");
                if ( channel != null)
                    message = ((Channel) channel).getName();
                break;
        }

        contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.tickerText = message;
        notification.contentIntent = contentIntent;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, "PITER FM", message, contentIntent);


        if (notificationId != PLAY_STOP){
            notification.vibrate = new long[]{0, 300};
        }else {
            if (App.getPlayer().state == PlayerService.State.Playing)
                notification.flags = Notification.FLAG_NO_CLEAR;
        }

        if (Settings.isNotificationsSoundEnabled())
            notification.defaults |= Notification.DEFAULT_SOUND;

        if (Settings.isNotificationsEnabled())
            nm.notify(notificationId, notification);


    }

    public static void killNotification(int id) {
        nm.cancel(id);
    }


}

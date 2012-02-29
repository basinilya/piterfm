package ru.piter.fm.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import ru.piter.fm.R;
import ru.piter.fm.activities.MainActivity;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 23.09.2010
 * Time: 0:36:18
 * To change this template use File | Settings | File Templates.
 */
public class Notifications {

    public static final int TRANSLATION_UNAVAILABLE = 1;
    public static final int SD_CARD_UNAVAILABLE = 2;
    public static final int CANT_LOAD_CHANNELS = 3;
    public static final int OPEN_APPLICATION = 4;

    private static final String MESSAGE_TRANSLATION_UNAVAILABLE = "Translation unavailable";
    private static final String MESSAGE_SD_CARD_UNAVAILABLE = "SD card unavailable";
    private static final String MESSAGE_CANT_LOAD_CHANNELS = "Can't load channels";
    private static final String MESSAGE_OPEN_APPLICATION = "PiterFM/MoskvaFM";

    private static final String NOTIFICATION_SERVICE = "notification";

    private static NotificationManager nm;
    private static Notification n;
    private static PendingIntent contentIntent;


    public static void showErrorNotification(Context context, String message, int id) {
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        n = new Notification(R.drawable.logo_error, "", System.currentTimeMillis());
        contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        n.setLatestEventInfo(context, "PiterFM", message, contentIntent);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(id, n);

    }

    public static void showAppNotification(Context context, String channel, int id) {
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        n = new Notification(R.drawable.logo, channel, System.currentTimeMillis());
        contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        n.setLatestEventInfo(context, "PiterFM/MoskvaFM", channel.equals("") ? "" : channel, contentIntent);
        n.flags |= Notification.FLAG_NO_CLEAR;
        nm.notify(id, n);
    }

    public static void killAppNotification(Context context, int id) {
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(id);
    }

}

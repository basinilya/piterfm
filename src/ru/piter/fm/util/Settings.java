package ru.piter.fm.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ru.piter.fm.App;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 02.11.2010
 * Time: 0:05:38
 * To change this template use File | Settings | File Templates.
 */

public class Settings {

    public static final String CHANNEL_SORT_TYPE = "channel_sort_key";
    public static final String TRACK_SORT_TYPE = "track_sort_key";
    public static final String FAVOURITES = "favourites_key";
    public static final String NOTIFICATION = "notification_key";
    public static final String NOTIFICATION_SOUND = "notification_sound_key";
    public static final String RECONNECT = "reconnect_key";
    public static final String RECONNECT_COUNT = "reconnect_count_key";
    public static final String RECONNECT_TIMEOUT = "reconnect_timeout_key";
    public static final String CACHE_SIZE = "cache_size_key";
    public static final String DEBUG_LOG_ENABLED = "debug_log_enabled_key";


    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    private static String getString(String name) {
        return getPreferences().getString(name, "");
    }

    private static Boolean getBoolean(String name) {
        return getPreferences().getBoolean(name, false);
    }

    private static int getInt(String name) {
        return Integer.parseInt(getPreferences().getString(name, "-1"));
    }

    public static String getChannelSort(){
        return getString(CHANNEL_SORT_TYPE);
    }

    public static String getTrackSort() {
        return getString(TRACK_SORT_TYPE);
    }

    public static boolean isFavouritesEnabled() {
        return getBoolean(FAVOURITES);
    }

    public static boolean isNotificationsEnabled() {
        return getBoolean(NOTIFICATION);
    }

    public static boolean isNotificationsSoundEnabled() {
        return getBoolean(NOTIFICATION_SOUND);
    }

    public static boolean isReconnect(){
        return getBoolean(RECONNECT);
    }

    public static int getReconnectCount(){
        return getInt(RECONNECT_COUNT);
    }

    public static int getReconnectTimeout(){
        return getInt(RECONNECT_TIMEOUT);
    }

    public static int getCacheSize() {
        return getInt(CACHE_SIZE);
    }
}

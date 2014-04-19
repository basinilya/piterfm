package ru.piter.fm.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ru.piter.fm.App;

public class Settings
{
  public static final String CHANNEL_SORT_TYPE = "channel_sort_key";
  public static final String FAVOURITES = "favourites_key";
  public static final String NOTIFICATION = "notification_key";
  public static final String NOTIFICATION_SOUND = "notification_sound_key";
  public static final String RECONNECT = "reconnect_key";
  public static final String RECONNECT_COUNT = "reconnect_count_key";
  public static final String RECONNECT_TIMEOUT = "reconnect_timeout_key";
  public static final String TRACK_SORT_TYPE = "track_sort_key";
  
  private static Boolean getBoolean(String paramString)
  {
    return Boolean.valueOf(getPreferences().getBoolean(paramString, false));
  }
  
  public static String getChannelSort()
  {
    return getString("channel_sort_key");
  }
  
  private static int getInt(String paramString)
  {
    return Integer.parseInt(getPreferences().getString(paramString, "-1"));
  }
  
  public static SharedPreferences getPreferences()
  {
    return PreferenceManager.getDefaultSharedPreferences(App.getContext());
  }
  
  public static int getReconnectCount()
  {
    return getInt("reconnect_count_key");
  }
  
  public static int getReconnectTimeout()
  {
    return getInt("reconnect_timeout_key");
  }
  
  private static String getString(String paramString)
  {
    return getPreferences().getString(paramString, "");
  }
  
  public static String getTrackSort()
  {
    return getString("track_sort_key");
  }
  
  public static boolean isFavouritesEnabled()
  {
    return getBoolean("favourites_key").booleanValue();
  }
  
  public static boolean isNotificationsEnabled()
  {
    return getBoolean("notification_key").booleanValue();
  }
  
  public static boolean isNotificationsSoundEnabled()
  {
    return getBoolean("notification_sound_key").booleanValue();
  }
  
  public static boolean isReconnect()
  {
    return getBoolean("reconnect_key").booleanValue();
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.Settings
 * JD-Core Version:    0.7.0.1
 */
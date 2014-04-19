package ru.piter.fm.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import ru.piter.fm.util.Settings;

public class SettingsActivity
  extends PreferenceActivity
  implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private CheckBoxPreference notificationPref;
  private CheckBoxPreference notificationSoundPref;
  private ListPreference reconnectCountPref;
  private CheckBoxPreference reconnectPref;
  private ListPreference reconnectTimeoutPref;
  private ListPreference sortChannelPref;
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2130968576);
    this.sortChannelPref = ((ListPreference)getPreferenceScreen().findPreference("channel_sort_key"));
    this.notificationPref = ((CheckBoxPreference)getPreferenceScreen().findPreference("notification_key"));
    this.notificationSoundPref = ((CheckBoxPreference)getPreferenceScreen().findPreference("notification_sound_key"));
    this.reconnectPref = ((CheckBoxPreference)getPreferenceScreen().findPreference("reconnect_key"));
    this.reconnectCountPref = ((ListPreference)getPreferenceScreen().findPreference("reconnect_count_key"));
    this.reconnectTimeoutPref = ((ListPreference)getPreferenceScreen().findPreference("reconnect_timeout_key"));
  }
  
  protected void onPause()
  {
    super.onPause();
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }
  
  protected void onResume()
  {
    super.onResume();
    Object localObject1 = getResources().getString(2131361856);
    String str1 = getResources().getString(2131361857);
    Resources localResources = getResources();
    int i;
    Object localObject2;
    label76:
    Object localObject3;
    label98:
    CheckBoxPreference localCheckBoxPreference3;
    if (Settings.getChannelSort().equals("sort_by_range"))
    {
      i = 2131361825;
      String str2 = localResources.getString(i);
      this.sortChannelPref.setSummary(str2);
      CheckBoxPreference localCheckBoxPreference1 = this.notificationPref;
      if (!Settings.isNotificationsEnabled()) {
        break label170;
      }
      localObject2 = localObject1;
      localCheckBoxPreference1.setSummary((CharSequence)localObject2);
      CheckBoxPreference localCheckBoxPreference2 = this.notificationSoundPref;
      if (!Settings.isNotificationsSoundEnabled()) {
        break label176;
      }
      localObject3 = localObject1;
      localCheckBoxPreference2.setSummary((CharSequence)localObject3);
      localCheckBoxPreference3 = this.reconnectPref;
      if (!Settings.isReconnect()) {
        break label182;
      }
    }
    for (;;)
    {
      localCheckBoxPreference3.setSummary((CharSequence)localObject1);
      this.reconnectCountPref.setSummary(String.valueOf(Settings.getReconnectCount()));
      this.reconnectTimeoutPref.setSummary(String.valueOf(Settings.getReconnectTimeout()));
      getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
      return;
      i = 2131361826;
      break;
      label170:
      localObject2 = str1;
      break label76;
      label176:
      localObject3 = str1;
      break label98;
      label182:
      localObject1 = str1;
    }
  }
  
  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    Object localObject = getResources().getString(2131361856);
    String str1 = getResources().getString(2131361857);
    int i;
    if (paramString.equals("channel_sort_key"))
    {
      Resources localResources = getResources();
      if (Settings.getChannelSort().equals("sort_by_range"))
      {
        i = 2131361825;
        String str2 = localResources.getString(i);
        this.sortChannelPref.setSummary(str2);
      }
    }
    do
    {
      return;
      i = 2131361826;
      break;
      if (paramString.equals("notification_key"))
      {
        CheckBoxPreference localCheckBoxPreference3 = this.notificationPref;
        if (paramSharedPreferences.getBoolean(paramString, false)) {}
        for (;;)
        {
          localCheckBoxPreference3.setSummary((CharSequence)localObject);
          return;
          localObject = str1;
        }
      }
      if (paramString.equals("notification_sound_key"))
      {
        CheckBoxPreference localCheckBoxPreference2 = this.notificationSoundPref;
        if (paramSharedPreferences.getBoolean(paramString, false)) {}
        for (;;)
        {
          localCheckBoxPreference2.setSummary((CharSequence)localObject);
          return;
          localObject = str1;
        }
      }
      if (paramString.equals("reconnect_key"))
      {
        CheckBoxPreference localCheckBoxPreference1 = this.reconnectPref;
        if (paramSharedPreferences.getBoolean(paramString, false)) {}
        for (;;)
        {
          localCheckBoxPreference1.setSummary((CharSequence)localObject);
          return;
          localObject = str1;
        }
      }
      if (paramString.equals("reconnect_count_key"))
      {
        this.reconnectCountPref.setSummary(paramSharedPreferences.getString("reconnect_count_key", "-1"));
        return;
      }
    } while (!paramString.equals("reconnect_timeout_key"));
    this.reconnectTimeoutPref.setSummary(paramSharedPreferences.getString("reconnect_timeout_key", "-1"));
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.activities.SettingsActivity
 * JD-Core Version:    0.7.0.1
 */
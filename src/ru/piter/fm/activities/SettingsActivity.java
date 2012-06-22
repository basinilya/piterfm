package ru.piter.fm.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import ru.piter.fm.R;
import ru.piter.fm.util.Settings;


/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 12.10.2010
 * Time: 1:20:31
 * To change this template use File | SettingsActivity | File Templates.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private CheckBoxPreference notificationPref;
    private CheckBoxPreference notificationSoundPref;
    private ListPreference sortChannelPref;
    private CheckBoxPreference reconnectPref;
    private ListPreference reconnectCountPref;
    private ListPreference reconnectTimeoutPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


        sortChannelPref = (ListPreference) getPreferenceScreen().findPreference(Settings.CHANNEL_SORT_TYPE);
        notificationPref = (CheckBoxPreference) getPreferenceScreen().findPreference(Settings.NOTIFICATION);
        notificationSoundPref = (CheckBoxPreference) getPreferenceScreen().findPreference(Settings.NOTIFICATION_SOUND);
        reconnectPref = (CheckBoxPreference) getPreferenceScreen().findPreference(Settings.RECONNECT);
        reconnectCountPref = (ListPreference) getPreferenceScreen().findPreference(Settings.RECONNECT_COUNT);
        reconnectTimeoutPref = (ListPreference) getPreferenceScreen().findPreference(Settings.RECONNECT_TIMEOUT);
    }


    @Override
    protected void onResume() {
        super.onResume();

        String on = getResources().getString(R.string.on);
        String off = getResources().getString(R.string.off);

        String channelSummary = getResources().getString(Settings.getChannelSort().equals("sort_by_range") ? R.string.sort_by_range : R.string.sort_by_name);
        sortChannelPref.setSummary(channelSummary);

        notificationPref.setSummary(Settings.isNotificationsEnabled() ? on : off);
        notificationSoundPref.setSummary(Settings.isNotificationsSoundEnabled() ? on : off);
        reconnectPref.setSummary(Settings.isReconnect() ? on : off);
        reconnectCountPref.setSummary(String.valueOf(Settings.getReconnectCount()));
        reconnectTimeoutPref.setSummary(String.valueOf(Settings.getReconnectTimeout()));


        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        String on = getResources().getString(R.string.on);
        String off = getResources().getString(R.string.off);


        if (key.equals(Settings.CHANNEL_SORT_TYPE)) {
            String channelSummary = getResources().getString(Settings.getChannelSort().equals("sort_by_range") ? R.string.sort_by_range : R.string.sort_by_name);
            sortChannelPref.setSummary(channelSummary);
        } else if (key.equals(Settings.NOTIFICATION)) {
            notificationPref.setSummary(sharedPreferences.getBoolean(key, false) ? on : off);
        } else if (key.equals(Settings.NOTIFICATION_SOUND)) {
            notificationSoundPref.setSummary(sharedPreferences.getBoolean(key, false) ? on : off);
        } else if (key.equals(Settings.RECONNECT)) {
            reconnectPref.setSummary(sharedPreferences.getBoolean(key, false) ? on : off);
        } else if (key.equals(Settings.RECONNECT_COUNT)) {
            reconnectCountPref.setSummary(sharedPreferences.getString(Settings.RECONNECT_COUNT, "-1"));
        } else if (key.equals(Settings.RECONNECT_TIMEOUT)) {
            reconnectTimeoutPref.setSummary(sharedPreferences.getString(Settings.RECONNECT_TIMEOUT, "-1"));
        }

    }
}

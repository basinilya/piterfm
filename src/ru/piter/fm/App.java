package ru.piter.fm;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ru.piter.fm.player.PlayerInterface;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.player.PlayerPinner;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.prototype.BuildConfig;
import ru.piter.fm.prototype.R;
import ru.piter.fm.util.GetAllStackTracesTimer;
import ru.piter.fm.util.SelfLogcatSaver;
import ru.piter.fm.util.Settings;
import ru.piter.fm.util.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 04.09.2010
 * Time: 0:11:48
 * To change this template use File | SettingsActivity | File Templates.
 */
public class App extends Application implements OnSharedPreferenceChangeListener {

    private static final String Tag = "PiterFMPlayer";

    private static Context context;
    private static PlayerInterface player;

    static {
        if (BuildConfig.DEBUG)
            new GetAllStackTracesTimer();
    }

    private final SelfLogcatSaver selfLogcatSaver = new SelfLogcatSaver();

    @Override
    public void onCreate() {
        final String funcname = "App,onCreate";
        super.onCreate();

        @SuppressWarnings("deprecation")
        String SDK = VERSION.SDK;
        Log.d(Tag, funcname + ",running on " + VERSION.RELEASE + " (API " + SDK + ")");
        // init preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sp.contains(Settings.DEBUG_LOG_ENABLED)) {
            sp.edit().putBoolean(Settings.DEBUG_LOG_ENABLED, BuildConfig.DEBUG).commit();
        }

        context = this;

        switchSelfLogcatSaver(sp);

        sp.registerOnSharedPreferenceChangeListener(this);

        Utils.fixProxy(this);

        // init image loader
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY)
                .discCache(new UnlimitedDiscCache(Utils.isSdAvailable() ? Utils.CACHE_DIR : getApplicationContext().getCacheDir()))
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);

        // bind player service
        player = new PlayerPinner(this);
    }

    public static PlayerInterface getPlayer() {
        return player;
    }

    public static boolean isPlaying(Channel ch) {
        return ch.getChannelId().equals(player.getChannelId()) && (!player.isPaused());
    }

    public static boolean isPlaying() {
        return !player.isPaused();
    }

    public static Context getContext(){
        return context;
    }

    private void switchSelfLogcatSaver(SharedPreferences sp) {
        if (sp.getBoolean(Settings.DEBUG_LOG_ENABLED, false)) {
            selfLogcatSaver.enable();
        } else {
            selfLogcatSaver.disable();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Settings.DEBUG_LOG_ENABLED)) {
            switchSelfLogcatSaver(sharedPreferences);
        }
    }

}

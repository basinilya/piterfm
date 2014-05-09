package ru.piter.fm;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ru.piter.fm.player.PiterFMPlayer;
import ru.piter.fm.player.PlayerInterface;
import ru.piter.fm.prototype.R;
import ru.piter.fm.util.DBAdapter;
import ru.piter.fm.util.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 04.09.2010
 * Time: 0:11:48
 * To change this template use File | SettingsActivity | File Templates.
 */
public class App extends Application {

    private static Context context;
    private static PlayerInterface player;
    private static DBAdapter db;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

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
        if (player == null)
            player = new PiterFMPlayer();

        // init preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        db = new DBAdapter(context);
    }

    public static PlayerInterface getPlayer() {
        return player;
    }

    public static DBAdapter getDb(){
        return db;
    }

    public static Context getContext(){
        return context;
    }

}

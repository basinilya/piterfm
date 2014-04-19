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
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import java.io.File;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.player.PlayerService.PlayerServiceListener;
import ru.piter.fm.util.DBAdapter;
import ru.piter.fm.util.Utils;

public class App
  extends Application
{
  private static Context context;
  private static DBAdapter db;
  private static PlayerService player;
  private ServiceConnection connection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      App.access$002(((PlayerService.PlayerServiceListener)paramAnonymousIBinder).getService());
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      App.access$002(null);
    }
  };
  
  public static Context getContext()
  {
    return context;
  }
  
  public static DBAdapter getDb()
  {
    return db;
  }
  
  public static PlayerService getPlayer()
  {
    return player;
  }
  
  public void onCreate()
  {
    super.onCreate();
    context = this;
    DisplayImageOptions localDisplayImageOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
    ImageLoaderConfiguration.Builder localBuilder = new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPoolSize(3).threadPriority(5);
    if (Utils.isSdAvailable()) {}
    for (File localFile = Utils.CACHE_DIR;; localFile = getApplicationContext().getCacheDir())
    {
      ImageLoaderConfiguration localImageLoaderConfiguration = localBuilder.discCache(new UnlimitedDiscCache(localFile)).denyCacheImageMultipleSizesInMemory().defaultDisplayImageOptions(localDisplayImageOptions).build();
      ImageLoader.getInstance().init(localImageLoaderConfiguration);
      if (player == null) {
        bindService(new Intent(this, PlayerService.class), this.connection, 1);
      }
      PreferenceManager.setDefaultValues(this, 2130968576, false);
      db = new DBAdapter(context);
      return;
    }
  }
  
  public void onTerminate()
  {
    super.onTerminate();
    player.stop();
    player = null;
    getApplicationContext().unbindService(this.connection);
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.App
 * JD-Core Version:    0.7.0.1
 */
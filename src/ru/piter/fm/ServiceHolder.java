package ru.piter.fm;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import ru.piter.fm.activities.RadioActivity;
import ru.piter.fm.player.PlayerService;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtil;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 04.09.2010
 * Time: 0:11:48
 * To change this template use File | Settings | File Templates.
 */
public class ServiceHolder extends Application {

    private static final String TAG = "ServiceHolder";
    private PlayerService playerService;
    private boolean isBound;
    
    public PlayerService getPlayerService() {
        return playerService;
    }

    public boolean isBound() {
        return isBound;
    }

    public void setBound(boolean bound) {
        isBound = bound;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        isBound = false;
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            playerService = ((PlayerService.PlayerBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            playerService = null;
        }
    };


    public void startService() {
        if (this.playerService == null) {
            getApplicationContext().bindService(new Intent(ServiceHolder.this, PlayerService.class), connection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    public void stopService() {
        playerService.stop(true);
        playerService = null;
        getApplicationContext().unbindService(connection);
        isBound = false;
    }
}

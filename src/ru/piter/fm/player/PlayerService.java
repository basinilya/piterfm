package ru.piter.fm.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 30.08.2010
 * Time: 15:37:13
 * To change this template use File | SettingsActivity | File Templates.
 */
public class PlayerService extends Service {

    private final IBinder mBinder = new PlayerServiceListener();

    public class PlayerServiceListener extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}

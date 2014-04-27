package ru.piter.fm.tasks;

import android.content.Context;
import android.content.Intent;
import ru.piter.fm.App;
import ru.piter.fm.exception.NoInternetException;
import ru.piter.fm.exception.NoSDCardException;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.04.12
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
public class PlayerTask extends BaseTask<Void> {

    public PlayerTask(Context context) {
        super(context);
    }

    @Override
    protected void onPreExecute() {
        if (!Utils.isSdAvailable()) {
            this.cancel(true);
            this.onError(new NoSDCardException("No SD card available"));
        } else {
            super.onPreExecute();
        }
    }

    @Override
    public Void doWork(Object... objects) throws Exception {
        Channel channel = (Channel) objects[0];
        if (objects.length > 1) {
            Track track = (Track) objects[1];
            App.getPlayer().play(channel, track);
        } else {
            App.getPlayer().play(channel);
        }
        return null;
    }


    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        if (e instanceof NoInternetException)
            Notifications.show(Notifications.CANT_LOAD_TRACK, new Intent());
        if (e instanceof NoSDCardException)
            Notifications.show(Notifications.SD_CARD_UNAVAILABLE, new Intent());


    }

    @Override
    public void onResult(Void result) {

    }


}

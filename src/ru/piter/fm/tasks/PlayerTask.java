package ru.piter.fm.tasks;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import ru.piter.fm.App;
import ru.piter.fm.exception.NoInternetException;
import ru.piter.fm.exception.NoSDCardException;
import ru.piter.fm.player.PlayerInterface;
import ru.piter.fm.player.PlayerInterface.EventType;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;
import ru.piter.fm.util.Notifications;
import ru.piter.fm.util.RadioUtils;
import ru.piter.fm.util.TrackCalendar;
import ru.piter.fm.util.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.04.12
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlayerTask extends BaseTask<Void> {

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

    // BEGIN DIRTY HACK

    // hide final execute methods of super
    public void execute(Channel ch, Track tr) {
        executeInternal(ch, tr);
    }
    public void execute(Channel ch) {
        executeInternal(ch);
    }

    private static ArrayList<PlayerTask> startedTasks = new ArrayList<PlayerTask>();

    static {
        App.getPlayer().setEventHandler(new PlayerInterface.EventHandler() {
            @Override
            public void onEvent(EventType ev) {
                switch (ev) {
                case Buffering:
                    break;
                case Error:
                    Notifications.show(Notifications.CANT_LOAD_TRACK, new Intent());
                    /* fallthrough */
                default:
                    for (PlayerTask t : startedTasks) {
                        t.onPostExecute(null);
                    }
                    startedTasks.clear();
                }
            }
        });
    }

    private boolean calledPause; // pause() does not raise events

    private void executeInternal(Object... objects) {
        onPreExecute();
        if (isCancelled()) {
            onCancelled();
        } else {
            calledPause = false;
            Void result = doInBackground(objects);
            //onPreExecute2(pl);
            if (exception != null || calledPause) {
                onPostExecute(result);
            } else {
                startedTasks.add(this);
            }
        }
    }
    // END DIRTY HACK

    @Override
    public Void doWork(Object... objects) throws Exception {
        Channel channel = (Channel) objects[0];
        TrackCalendar trackTime;
        PlayerInterface pl = App.getPlayer();
        String ch = channel.getChannelId();
        if (objects.length > 1) {
            // open another time and/or channel
            Track track = (Track) objects[1];
            trackTime = track.getTime();
        } else {
            if (ch.equals(pl.getChannelId())) {
                // maybe pause, maybe resume
                if (pl.isPaused()) {
                    pl.resume(getPlayingNotificationIntent());
                } else {
                    pl.pause();
                    calledPause = true;
                }
                return null;
            } else {
                trackTime = RadioUtils.getCurrentTrackTime(ch);
            }
        }
        pl.open(getPlayingNotificationIntent(), ch, trackTime);
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

    protected abstract Intent getPlayingNotificationIntent();


}

package ru.piter.fm.tasks;

import android.content.Context;
import android.content.Intent;
import ru.piter.fm.App;
import ru.piter.fm.exception.NoInternetException;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.util.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 16.03.12
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
public class GetChannelsTask extends BaseTask<List<Channel>> {

    private ChannelsLoadingListener channelsLoadingListener;

    public GetChannelsTask(Context context) {
        super(context);
    }


    @Override
    public List<Channel> doWork(Object... objects) throws Exception {
        Radio radio = (Radio) objects[0];
        DBAdapter db = App.getDb();
        List<Channel> channels = null;

        if (radio.getName().equals(RadioFactory.FAVOURITE))
            channels = db.selectAllChannels(radio);
        else {
            boolean redownload = objects.length > 2 && (Boolean)objects[2];
            channels = RadioUtils.getRadioChannels(radio, context, redownload);
        }

//        // force update
//        if (!radio.getName().equals(RadioFactory.FAVOURITE) && (channels == null || objects.length > 1)) {
//            if (!isOnline) {
//                this.cancel(true);
//                this.onError(new NoInternetException("No Internet Available"));
//                return null;
//            } else {
//                channels = RadioUtils.getRadioChannels(radio, context);
//                db.deleteChannels(radio);
//                db.insertChannels(channels, radio);
//            }
//        }


        if (channels != null)
            Collections.sort(channels, new ChannelComparator(Settings.getChannelSort()));

        return channels;

    }

    public void setChannelsLoadingListener(ChannelsLoadingListener channelsLoadingListener) {
        this.channelsLoadingListener = channelsLoadingListener;
    }

    @Override
    public void onResult(List<Channel> result) {
        Notifications.killNotification(Notifications.CANT_LOAD_CHANNELS);
        channelsLoadingListener.onChannelsLoaded(result);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        if (e instanceof NoInternetException)
            Notifications.show(Notifications.CANT_LOAD_CHANNELS, new Intent());


    }

    public interface ChannelsLoadingListener {
        public void onChannelsLoaded(List<Channel> channels);
    }
}

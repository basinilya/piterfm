package ru.piter.fm.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.piter.fm.exception.NoInternetException;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;
import ru.piter.fm.util.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class GetTracksTask extends BaseTask<List<Track>> {


    public TracksLoadingListener tracksLoadingListener;

    public GetTracksTask(Context context) {
        super(context);
    }


    @Override
    public List<Track> doWork(Object... objects) throws Exception {
        if (!isOnline) {
            this.cancel(true);
            this.onError(new NoInternetException("No Internet Available"));
            return null;
        }
        List<Track> tracks = null;
        String day = (String) objects[0];
        Channel channel = (Channel) objects[1];
        // force update
        if (objects.length != 3){
            tracks = TracksCache.get(channel.getChannelId(), day);
            if (tracks != null)
                return tracks;

        }
        String url = RadioUtils.getTracksUrl(day, channel);
        tracks = RadioUtils.getTracks(url);
        if (tracks != null){
            TracksCache.put(channel.getChannelId(), day, tracks);
            Collections.sort(tracks, new TrackComparator(Settings.getTrackSort()));
        }
        return tracks;
    }

    @Override
    public void onResult(List<Track> result) {
        Notifications.killNotification(Notifications.CANT_LOAD_TRACKS);
        tracksLoadingListener.onTracksLoaded(result);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        if (e instanceof NoInternetException)
            Notifications.show(Notifications.CANT_LOAD_TRACKS, new Intent());

    }


    public void setTracksLoadingListener(TracksLoadingListener tracksLoadingListener) {
        this.tracksLoadingListener = tracksLoadingListener;
    }

    public interface TracksLoadingListener {
        public void onTracksLoaded(List<Track> channels);
    }
}

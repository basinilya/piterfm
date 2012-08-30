package ru.piter.fm.util;

import ru.piter.fm.radio.Track;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 20.06.12
 * Time: 3:11
 * To change this template use File | Settings | File Templates.
 */
public class TracksCache {

    private static HashMap<String, HashMap<String, List<Track>>> tracks = new HashMap<String, HashMap<String, List<Track>>>();
    private static HashMap<String, Long> time = new HashMap<String, Long>();


    public static void put(String channelId, String date, List<Track> trackList) {
        HashMap<String, List<Track>> map = tracks.get(channelId);
        if (map == null)
            map = new HashMap<String, List<Track>>();
        map.put(date, trackList);
        tracks.put(channelId, map);
        time.put(channelId, System.currentTimeMillis());
    }


    public static List<Track> get(String channelId, String date) {
        HashMap<String, List<Track>> map = tracks.get(channelId);
        if (map != null) {
            List<Track> tl = map.get(date);
            Long t = time.get(channelId);
            if (tl != null && (System.currentTimeMillis() - t) < 1000 * 60 * 5)
                return tl;
        }
        return null;

    }


}

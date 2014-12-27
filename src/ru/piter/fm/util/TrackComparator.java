package ru.piter.fm.util;

import ru.piter.fm.radio.Track;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 12.10.2010
 * Time: 3:05:28
 * To change this template use File | Settings | File Templates.
 */
public class TrackComparator implements Comparator {

    private static String sortBy = "time";

    public TrackComparator(String sortBy) {
        this.sortBy = sortBy;
    }

    public int compare(Object o1, Object o2) {
        Track track_1 = (Track) o1;
        Track track_2 = (Track) o2;

        if (sortBy.equals("sort_by_rate")) {

            int currentValue = Integer.parseInt(track_1.getPlayCount());
            int newValue = Integer.parseInt(track_2.getPlayCount());

            if (currentValue < newValue)
                return 1;
            else if (currentValue > newValue)
                return -1;
            return 0;
        }

        if (sortBy.equals("sort_by_time")){
            long time1 = track_1.getTimeInMillis();
            long time2 = track_2.getTimeInMillis();
            if (time1 > time2)
                return 1;
            else if (time1 < time2)
                return -1;
            return 0;

        }

        return 0;
    }
}

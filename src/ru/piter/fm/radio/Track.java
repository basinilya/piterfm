package ru.piter.fm.radio;

import ru.piter.fm.util.SearchFilter;
import ru.piter.fm.util.TrackCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 13.11.2010
 * Time: 23:41:58
 * To change this template use File | Settings | File Templates.
 */
public class Track implements SearchFilter.Filterable {

    public static final int TYPE_TRACK = 1;
    public static final int TYPE_SHOW = 2;

    private String trackId;
    private String artistName;
    private String trackName;
    private String duration;
    private long time;
    private String timestamp;
    private String cover;
    private String playCount;
    private int type;

    public long getClientTimeInMillis() {
        return time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Track() {
    }

    public Track(int type) {
        this.type = type;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setClientTimeInMillis(long time) {
        this.time = time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    @Override
    public String toFilterString() {
        return artistName.toLowerCase() + trackName.toLowerCase();
    }

    @Override
    public String toString() {
        return "Track{" +
                "artistName='" + artistName + '\'' +
                ", trackName='" + trackName + '\'' +
                ", type=" + type +
                '}';
    }
}

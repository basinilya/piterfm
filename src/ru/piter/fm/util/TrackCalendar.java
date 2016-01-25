package ru.piter.fm.util;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This Calendar wrapper provides convenience methods for radio tracks. It
 * combines the datetime of the track and time to seek inside the track.
 * 
 * @author Ilya Basin
 * 
 */
public class TrackCalendar extends GregorianCalendar {

    private GregorianCalendar serverCal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));

    private static final long serialVersionUID = -5323025958149225720L;

    private static final long CLIENT_TZ_MS = 0*3600*1000;

    public void setClientTimeInMillis(long milliseconds) {
        setTimeInMillis(milliseconds + CLIENT_TZ_MS);
    }

    public long getClientTimeInMillis() {
        return getTimeInMillis() - CLIENT_TZ_MS;
    }

    private static TimeZone getTimezone() {
        return TimeZone.getTimeZone("GMT+3");
    }

    public TrackCalendar() {
        super(getTimezone());
    }

    /**
     * Use <code>clone()</code>, when the caller of the current method passed
     * {@link TrackCalendar} as parameter and doesn't expect it to change
     */
    @Override
    public TrackCalendar clone() {
        return (TrackCalendar)super.clone();
    }

    /** format me as "yyyy/MM/dd/HH00.m4a?start=n&end=n" */
    public String asURLPart() {
        serverCal.setTimeInMillis(getTimeInMillis());

        int h = serverCal.get(HOUR_OF_DAY);
        int m = serverCal.get(MINUTE);
        int start = m * 60;
        int end = start + (SEGMENT_MINUTES*60+2);
        return String.format(Locale.US, "%d/%02d/%02d/%02d00.m4a?start=%d&end=%d", serverCal.get(YEAR),
                serverCal.get(MONTH) + 1, serverCal.get(DATE), h, start, end);
    }

    @Override
    public String toString() {
        return asTrackTime() + "." + get(MILLISECOND);
    }

    public String asClientDMY() {
        return String.format(Locale.US, "%02d.%02d.%d"
                , get(DAY_OF_MONTH)
                , get(MONTH)+1
                , get(YEAR)
                );
    }

    public String asClientHMM() {
        return String.format(Locale.US, "%d:%02d", get(HOUR_OF_DAY), get(MINUTE));
    }

    /** format me as "yyyy:MM:dd:HH:mm:ss" */
    public String asTrackTime() {
        return String.format(Locale.US, "%d:%02d:%02d:%02d:%02d:%02d", get(YEAR),
                get(MONTH) + 1, get(DATE), get(HOUR_OF_DAY), get(MINUTE), get(SECOND));
    }

    public String asTracksUrlPart() {
        return String.format(Locale.US, "%d%02d%02d", get(YEAR), get(MONTH)+1, get(DAY_OF_MONTH));

        //http://www.piter.fm/station.xml.html?station=7835&day=20101218&r=0.47836548276245594
    }

    public static final int SEGMENT_MINUTES = 4;

    /** Add one minute and set default time to seek for subsequent tracks */
    public void nextTrackTime() {
        add(MINUTE, SEGMENT_MINUTES);
        set(SECOND, 2);
    }

    /** @return time to seek */
    public int getSeekTo() {
        return get(SECOND) * 1000;
    }
}

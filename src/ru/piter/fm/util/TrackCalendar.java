package ru.piter.fm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import ru.piter.fm.radio.Channel;

/**
 * This Calendar wrapper provides convenience methods for radio tracks. It
 * combines the datetime of the track and time to seek inside the track.
 * 
 * @author Ilya Basin
 * 
 */
public class TrackCalendar extends GregorianCalendar {

    private static final long serialVersionUID = -5323025958149225720L;

    private static final long CLIENT_TZ_MS = 3*3600*1000;

    public void setClientTimeInMillis(long milliseconds) {
        setTimeInMillis(milliseconds + CLIENT_TZ_MS);
    }

    public long getClientTimeInMillis() {
        return getTimeInMillis() - CLIENT_TZ_MS;
    }

    public static TimeZone getTimezone() {
        return TimeZone.getTimeZone("GMT+0");
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

    @Override
    public String toString() {
        return asTrackTime() + "." + get(MILLISECOND);
    }

    public String asHMM() {
        return String.format(Locale.US, "%d:%02d", get(HOUR_OF_DAY), get(MINUTE));
    }

    /** format me as "yyyy:MM:dd:HH:mm:ss" */
    public String asTrackTime() {
        return String.format(Locale.US, "%d:%02d:%02d:%02d:%02d:%02d", get(YEAR),
                get(MONTH) + 1, get(DATE), get(HOUR_OF_DAY), get(MINUTE), get(SECOND));
    }

    /** Add one minute and set default time to seek for subsequent tracks */
    public void nextTrackTime() {
        add(MINUTE, 1);
        set(SECOND, 2);
    }

    public String asTracksUrlPart() {
        return String.format(Locale.US, "%d%02d%02d", get(YEAR), get(MONTH)+1, get(DAY_OF_MONTH));

        //http://www.piter.fm/station.xml.html?station=7835&day=20101218&r=0.47836548276245594
    }
}

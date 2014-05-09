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

    private static final long serialVersionUID = -5323025958149225720L;

    public TrackCalendar() {
        super(TimeZone.getTimeZone("GMT+4"));
    }

    private transient String trackTime;
    private transient int ofs;

    /**
     * Use <code>clone()</code>, when the caller of the current method passed
     * {@link TrackCalendar} as parameter and doesn't expect it to change
     */
    @Override
    public TrackCalendar clone() {
        return (TrackCalendar)super.clone();
    }

    /** parse a string in format "yyyy:MM:dd:HH:mm:ss" */
    public void setTrackTime(String t) {
        ofs = 0;
        trackTime = t;
        set(YEAR, nextField(4));
        set(MONTH, nextField(2) - 1);
        set(DATE, nextField(2));
        set(HOUR_OF_DAY, nextField(2));
        set(MINUTE, nextField(2));
        set(SECOND, nextField(2));
    }

    /** format me as "yyyy/MM/dd/HHmm" */
    public String asURLPart() {
        return String.format(Locale.US, "%d/%02d/%02d/%02d%02d", get(YEAR),
                get(MONTH) + 1, get(DATE), get(HOUR_OF_DAY), get(MINUTE));
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

    /** @return time to seek */
    public int getSeekTo() {
        return get(SECOND) * 1000;
    }

    private int nextField(int len) {
        int end = ofs + len;
        int rslt = Integer.parseInt(trackTime.substring(ofs, end));
        ofs = end + 1;
        return rslt;
    }
}

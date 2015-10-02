package ru.piter.fm.player;

import ru.piter.fm.radio.Channel;
import ru.piter.fm.util.TrackCalendar;
import android.content.Intent;

public interface PlayerInterface {

    /** try to start playback */
    void open(Intent notificationIntent, Channel channel, TrackCalendar trackTime);

    TrackCalendar getPosition();

    /** pause */
    void pause();

    /** try to resume playback */
    void resume(Intent notificationIntent);

    String getChannelId();

    /** @return <i>desired</i> paused state */
    boolean isPaused();

    void addEventHandler(EventHandler handler);
    void removeEventHandler(EventHandler handler);

    public interface EventHandler {
        void onEvent(EventType ev);
    }

    public enum EventType {
        /** Buffering. Expect subsequent NotBuffering or Error. */
        Buffering,
        /** Buffering ended, because playback started or pause() was called */
        NotBuffering,
        /** failed to start or continue playback. Player becomes paused */
        Error
    }
}

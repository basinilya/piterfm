package ru.piter.fm.player;

import android.content.Intent;

public interface PlayerInterface {

    /** try to start playback */
    void open(Intent notificationIntent, String channelId, String trackTime);

    /** pause */
    void pause();

    /** try to resume playback */
    void resume(Intent notificationIntent);

    String getChannelId();

    /** @return <i>desired</i> paused state */
    boolean isPaused();

    void setEventHandler(EventHandler handler);

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

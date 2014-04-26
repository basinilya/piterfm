package ru.piter.fm.player;

public interface PlayerInterface {

    /** try to start playback */
    void open(String channelId, String trackTime);

    /** pause */
    void pause();

    /** try to resume playback */
    void resume();

    String getChannelId();

    /** @return <i>desired</i> paused state */
    boolean isPaused();

    void release();

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

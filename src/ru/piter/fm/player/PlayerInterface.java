package ru.piter.fm.player;

public interface PlayerInterface {

    /**
     * try to start playback. Will raise {@link EventHandler#onPlaying()} or
     * {@link EventHandler#onError()}
     */
    void open(String channelId, String trackTime);

    /** pause */
    void pause();

    /**
     * try to resume playback. Will raise {@link EventHandler#onPlaying()} or
     * {@link EventHandler#onError()}
     */
    void resume();

    String getChannelId();

    boolean isPaused();

    void release();

    void setEventHandler(EventHandler handler);

    public interface EventHandler {
        void onEvent(EventType ev);
    }

    public enum EventType {
        /** Buffering. Expect subsequent Playing or Error. */
        Buffering,
        /** Successfully started playback. Expect subsequent Buffering or Error. */
        Playing,
        /** failed to start or continue playback. Player becomes paused */
        Error
    }
}

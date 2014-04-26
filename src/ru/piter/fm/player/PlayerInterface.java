package ru.piter.fm.player;

import ru.piter.fm.player.PlayerService.State;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Track;

public interface PlayerInterface {
    void stop();
    String getChannelId();
    State getState();
    void play(String channelId, String trackTime);
    void terminate();
}

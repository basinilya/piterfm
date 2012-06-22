package ru.piter.fm.radio;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.08.2010
 * Time: 16:44:38
 * To change this template use File | SettingsActivity | File Templates.
 */
public class Radio implements Serializable{

    private String name;
    private String hostUrl;
    private String stationsUrl;

    private List<Channel> channels;

    public Radio(String name, String hostUrl) {
        this.name = name;
        this.hostUrl = hostUrl;
        this.stationsUrl = hostUrl + "/stations";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public String getStationsUrl() {
        return stationsUrl;
    }

    public void setStationsUrl(String stationsUrl) {
        this.stationsUrl = stationsUrl;
    }
}

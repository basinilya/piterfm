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

    private static final long serialVersionUID = -5284411684704086564L;

    private String name;

    private List<Channel> channels;

    public Radio(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
}

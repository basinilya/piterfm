package ru.piter.fm.radio;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 25.08.2010
 * Time: 15:59:25
 * To change this template use File | Settings | File Templates.
 */
public class Channel implements IChannel, Serializable, Comparable {

    private String channelId;
    private Bitmap logo;
    private String name;
    private String range;


    public Channel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }


    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (!channelId.equals(channel.channelId)) return false;
        //if (!logo.equals(channel.logo)) return false;
        if (!name.equals(channel.name)) return false;
        if (!range.equals(channel.range)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = channelId.hashCode();
        result = 31 * result + logo.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }

    public int compareTo(Object o) {
        Channel channel = (Channel) o;
        float currentValue = Float.parseFloat(this.range.split("\\s")[0]);
        float newValue = Float.parseFloat(channel.getRange().split("\\s")[0]);
        if (currentValue < newValue)
            return -1;
        else if (currentValue > newValue)
            return 1;
        return 0;
    }
}

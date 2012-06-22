package ru.piter.fm.radio;

import android.graphics.Bitmap;
import ru.piter.fm.util.SearchFilter;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 25.08.2010
 * Time: 15:59:25
 * To change this template use File | SettingsActivity | File Templates.
 */
public class Channel implements Serializable, Comparable, SearchFilter.Filterable {

    private String channelId;
    private String logoUrl;
    private String translationUrl;
    private String name;
    private String range;
    private Radio radio;


    public Channel() {
    }


    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    public String getTranslationUrl() {
        return translationUrl;
    }

    public void setTranslationUrl(String translationUrl) {
        this.translationUrl = translationUrl;
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


    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }


    @Override
    public String toFilterString() {
        return name.toLowerCase() + range.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (!channelId.equals(channel.channelId)) return false;
        if (!logoUrl.equals(channel.logoUrl)) return false;
        if (!name.equals(channel.name)) return false;
        if (!range.equals(channel.range)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = channelId.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }

    public int compareTo(Object o) {
        Channel channel = (Channel) o;
        try {
            float currentValue = Float.parseFloat(this.range.split("\\s")[0]);
            float newValue = Float.parseFloat(channel.getRange().split("\\s")[0]);
            if (currentValue < newValue)
                return -1;
            else if (currentValue > newValue)
                return 1;
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

package ru.piter.fm.radio;

import ru.piter.fm.util.RadioUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.08.2010
 * Time: 16:44:38
 * To change this template use File | Settings | File Templates.
 */
public class Radio implements IRadio {

    public static final String PITER_FM = "PiterFm";
    public static final String MOSKVA_FM = "MoskvaFm";

    long radioId;
    private String name;

    public Radio(String name) {
        this.name = name;
    }

    public Radio(String name, long radioId) {
        this.name = name;
        this.radioId = radioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRadioId() {
        return radioId;
    }

    public void setRadioId(long radioId) {
        this.radioId = radioId;
    }

    public List<Channel> getChannels() throws Exception{
        return RadioUtil.getRadioChannels(this.name);
    }
}

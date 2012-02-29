package ru.piter.fm.radio;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.08.2010
 * Time: 16:34:45
 * To change this template use File | Settings | File Templates.
 */
public interface IChannel {

    public String getChannelId();    
    public String getName();
    public String getRange();

}

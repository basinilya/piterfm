package ru.piter.fm.radio;



import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 25.08.2010
 * Time: 23:34:04
 * To change this template use File | Settings | File Templates.
 */
public interface IRadio {
    public List<Channel>  getChannels() throws Exception;
    public long getRadioId();
    public String getName();
    
}

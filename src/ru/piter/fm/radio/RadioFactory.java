package ru.piter.fm.radio;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 26.08.2010
 * Time: 0:04:44
 * To change this template use File | Settings | File Templates.
 */
public class RadioFactory {

    private IRadio radio;
    public static final String PITER_FM ="PiterFm";
    public static final String MOSKVA_FM ="MoskvaFm";

     public static IRadio getRadio(String impl){
        if (impl.equals(PITER_FM)) return new Radio(PITER_FM, 1L);
        if (impl.equals(MOSKVA_FM)) return new Radio(MOSKVA_FM, 2L);
        return null;
    }

}

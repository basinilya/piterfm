package ru.piter.fm.radio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class TomskStation {
    public final String mixer_name;
    public final String mixer_city;
    public final String mixer_freq;
    public final String station_id;
    public final boolean station_master;

    public TomskStation(String mixer_name, String mixer_city, String mixer_freq, String station_id, String station_master) {
        mixer_freq += " FM";
        this.mixer_name = mixer_name;
        this.mixer_city = mixer_city;
        this.mixer_freq = mixer_freq;
        this.station_id = station_id;
        this.station_master = !"0".equals(station_master);
        stations.put(getKey(mixer_city, mixer_freq), this);
        // <range>101.2 FM</range>
        // <div class="mixer_freq">73.1 FM</div>
    }

    public Channel mkChannel(Radio radio) {
        Channel ch = new Channel();
        ch.setChannelId(station_id);
        ch.setLogoUrl("http://radio-archive.ru/images/stations/small/station_" + station_id + "_65x65.png");
        ch.setName(mixer_name);
        ch.setRadio(radio);
        ch.setRange(mixer_freq);
        return ch;
    }

    public static String getKey(String radioName, String freq) {
        String tomsk_city = radioName;
        return freq + "/" + tomsk_city;
    }

    public static TomskStation get(String radioName, String freq) {
        String tomsk_city = radioName;
        return stations.get(getKey(radioName, freq));
    }

    public static final HashMap<String, TomskStation> stations = new HashMap<String, TomskStation>();
    public static final String[] cities;  
    
    static {
        Tomsk.init();
        TreeSet<String> citiesSet = new TreeSet<String>();
        for (TomskStation tst : stations.values()) {
            citiesSet.add(tst.mixer_city);
        }
        cities = new String[citiesSet.size()];
        int i = 0;
        for (String s : citiesSet) {
            cities[i++] = s;
        }
    }
}

package ru.piter.fm.radio;

import java.util.HashMap;

public class TomskStation {
    public final String mixer_name;
    public final String mixer_city;
    public final String mixer_freq;
    public final String station_id;
    public final String mixer_rds;

    public TomskStation(String mixer_name, String mixer_city, String mixer_freq, String station_id, String mixer_rds) {
        super();
        this.mixer_name = mixer_name;
        this.mixer_city = mixer_city;
        this.mixer_freq = mixer_freq;
        this.station_id = station_id;
        this.mixer_rds = mixer_rds;
        stations.put(mixer_freq + "/" + mixer_city, this);
        // <range>101.2 FM</range>
        // <div class="mixer_freq">73.1 FM</div>
    }

    public static TomskStation get(String radioName, String freq) {
        String tomsk_city;
        if (RadioFactory.MOSKVA_FM.equals(radioName)) {
            tomsk_city = "Москва";
        } else if (RadioFactory.PITER_FM.equals(radioName)) {
            tomsk_city = "Санкт-Петербург";
        } else {
            return null;
        }
        return stations.get(freq + "/" + tomsk_city);
    }

    private static final HashMap<String, TomskStation> stations = new HashMap<String, TomskStation>();

    static {
        Tomsk.init();
    }
}

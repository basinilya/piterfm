package ru.piter.fm.util;

import android.content.Context;
import android.util.Log;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.piter.fm.radio.Track;

import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 14.11.2010
 * Time: 15:53:30
 * To change this template use File | Settings | File Templates.
 */
public class RadioUtils {

    private static final String CHANNEL_HOST = "http://fresh.moskva.fm";
    private static final String PITER_FM_URL = "http://piter.fm/stations";
    private static final String MOSKVA_FM_URL = "http://moskva.fm/stations";
    private static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd/HHmm");
    private static final long TIME_MINUTE = 60000;

    public static String getTracksUrl(Date startAt, Channel channel) {
        String date = new SimpleDateFormat("yyyyMMdd").format(startAt);
        String url = channel.getRadio().getHostUrl() + "/station.xml.html?station=" + channel.getChannelId() + "&day=" + date;
        return url;

        //http://www.piter.fm/station.xml.html?station=7835&day=20101218&r=0.47836548276245594
    }

    public static List<Track> getTracks(String url) {
        Log.d("PiterFM","tracks url = " + url);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<Track> trackList = new ArrayList<Track>();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document dom = null;
            Log.d("Parser", new URL(url).getContent().toString());
            dom = builder.parse(new URL(url).openStream());
            Element root = dom.getDocumentElement();
            NodeList tracks = root.getElementsByTagName("track");
            Log.d("Parser ", "Tracks size = " + tracks.getLength());
            for (int i = 0; i < tracks.getLength(); i++) {
                Track trackInfo = new Track(Track.TYPE_TRACK);
                Element track = (Element) tracks.item(i);
                trackInfo.setArtistName(track.getAttribute("name"));
                trackInfo.setTrackName(track.getAttribute("artistName"));
                trackInfo.setDuration(track.getAttribute("duration"));

                String time = track.getAttribute("startAt");
                trackInfo.setStartAt(Long.parseLong(time));
                Date date = getGMT4Date(new Date(Long.parseLong(time) * 1000), "Europe/Moscow");
                DateFormat df = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                time = df.format(date);
                trackInfo.setTime(time);

                trackInfo.setPlayCount(track.getAttribute("playCount"));
                trackList.add(trackInfo);
            }

            tracks = root.getElementsByTagName("show");
            Log.d("PiterFM", "shows length = " + tracks.getLength());
            for (int i = 0; i < tracks.getLength(); i++) {
                Track trackInfo = new Track(Track.TYPE_SHOW);
                Element track = (Element) tracks.item(i);
                trackInfo.setTrackName(track.getAttribute("name"));
                trackInfo.setDuration(track.getAttribute("duration"));
                String time = track.getAttribute("startAt");
                trackInfo.setStartAt(Long.parseLong(time));
                Date date = getGMT4Date(new Date(Long.parseLong(time) * 1000), "Europe/Moscow");
                DateFormat df = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                time = df.format(date);
                trackInfo.setTime(time);
                trackInfo.setPlayCount("0");
                trackList.add(trackInfo);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Log.d("PiterFM: ", e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            Log.d("PiterFM: ", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("PiterFM: ", e.getMessage());
        }


        return trackList;
    }

    public static Date getGMT4Date(Date currentDate, String timeZoneId) {
        // TimeZone tz = TimeZone.getTimeZone(timeZoneId);
        Calendar mbCal = new GregorianCalendar(TimeZone.getTimeZone("GMT+4"));
        mbCal.setTimeInMillis(currentDate.getTime());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

        return cal.getTime();
    }


    public static int getTrackOffset(String time) {
        time = time.replaceAll(":", "/");
        String offset = time.substring(17, 19);
        return Integer.parseInt(offset);
    }

    public static String getTrackUrl(String time /*2010:11:13:20:31:12 */, String channelId) {
        time = time.replaceAll(":", "/");
        String currentTrack = time.substring(0, 11) + time.substring(11, 17).replaceAll("/", "");
        String url = CHANNEL_HOST + "/files/" + channelId + "/mp4/" + currentTrack + ".mp4";
        return url;
    }

    public static String getDuration(String durationInSeconds) {
        String minutes = String.valueOf(Integer.parseInt(durationInSeconds) / 60);
        int sec = Integer.parseInt(durationInSeconds) % 60;
        String seconds = String.valueOf(Integer.parseInt(durationInSeconds) % 60);
        if (sec < 10) seconds = "0" + seconds;
        return minutes + ":" + seconds;
    }


    private static String getNodeValue(NodeList list) {
        return list.item(0).getTextContent();
    }

    public static List<Channel> getRadioChannels(Radio radio, Context context) throws Exception {
        List<Channel> channels = new ArrayList<Channel>();

        //InputStream stream = Utils.openConnection(radio.getStationsUrl());
        InputStream stream = context.getAssets().open("xml/" + radio.getName() + ".xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        builder = factory.newDocumentBuilder();
        Document dom = builder.parse(stream);
        Element root = dom.getDocumentElement();
        NodeList tracks = root.getElementsByTagName("channel");

        for (int i = 0; i < tracks.getLength(); i++) {
            try {
                Channel channel = new Channel();
                Element ch = (Element) tracks.item(i);

                channel.setChannelId(getNodeValue(ch.getElementsByTagName("id")));
                channel.setName(getNodeValue(ch.getElementsByTagName("name")));
                channel.setRange(getNodeValue(ch.getElementsByTagName("range")));
                channel.setLogoUrl(getNodeValue(ch.getElementsByTagName("logo")));
                channel.setTranslationUrl(getNodeValue(ch.getElementsByTagName("translation")));
                channel.setRadio(radio);
                channels.add(channel);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("PiterFM: ", e.getMessage());
                continue;
            }
        }



        return channels;
    }


    public static String getTrackUrl(Channel channel) {
        String channelId = channel.getChannelId();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HHmm");
        Date date = getGMT4Date(new Date(System.currentTimeMillis() - (TIME_MINUTE * 5)), "Europe/Moscow");
        String currentTrack = dateFormat.format(date);
        String trackUrl = CHANNEL_HOST + "/files/" + channelId + "/mp4/" + currentTrack + ".mp4";
        if (trackUrl == null) {
            Log.d("PiterFM", "trackUrl is null ! Date = " + date + " Channel = " + channel + " currentTrack = " + currentTrack);
        }
        Log.d("PiterFM", "trackUrl = " + trackUrl);
        return trackUrl;
    }

    public static String getNextTrackUrl(String currentTrack) {
        String nextTrack = currentTrack;
        String array[] = currentTrack.split("/");
        String channelId = array[4];
        String year = array[6];
        String month = array[7];
        String day = array[8];
        String hs = (array[9]).split("\\.")[0];
        String hours = hs.substring(0, 2);
        String minutes = hs.substring(2, 4);

        try {
            Date date = DF.parse(year + "/" + month + "/" + day + "/" + hours + minutes);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 1);
            String track = DF.format(calendar.getTime());
            nextTrack = CHANNEL_HOST + "/files/" + channelId + "/mp4/" + track + ".mp4";
            return nextTrack;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("PiterFM: ", e.getMessage() + "\n" + e.getCause());
        }

        return nextTrack;

    }

    public static String getTrackNameFromUrl(String trackUrl) {
        if (trackUrl == null) return "";
        String[] a = trackUrl.split("/");
        return a[4] + "_" + a[5] + "_" + a[6] + "_" + a[7] + "_" + a[8] + "_" + a[9];
    }


}

package ru.piter.fm.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.piter.fm.radio.Track;

import javax.xml.parsers.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    public static final String CHANNEL_PREFIX = "http://fresh.moskva.fm/files/";
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
            TrackCalendar trackCal = new TrackCalendar();
            for (int i = 0; i < tracks.getLength(); i++) {
                Track trackInfo = new Track(Track.TYPE_TRACK);
                Element track = (Element) tracks.item(i);
                trackInfo.setArtistName(track.getAttribute("name"));
                trackInfo.setTrackName(track.getAttribute("artistName"));
                trackInfo.setDuration(track.getAttribute("duration"));

                String time = track.getAttribute("startAt");
                trackInfo.setStartAt(Long.parseLong(time));
                Date date = new Date(Long.parseLong(time) * 1000);
                trackCal.setTime(date);
                time = trackCal.asTrackTime();
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
                Date date = new Date(Long.parseLong(time) * 1000);
                trackCal.setTime(date);
                time = trackCal.asTrackTime();
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

    public static String getDuration(String durationInSeconds) {
        String minutes = String.valueOf(Integer.parseInt(durationInSeconds) / 60);
        int sec = Integer.parseInt(durationInSeconds) % 60;
        String seconds = String.valueOf(Integer.parseInt(durationInSeconds) % 60);
        if (sec < 10) seconds = "0" + seconds;
        return minutes + ":" + seconds;
    }

    private static boolean haveGetTextContent() {
        try {
            Node.class.getMethod("getTextContent");
            return true;
        } catch (NoSuchMethodException e1) {
            return false;
        }
    }

    /** Feature check */
    private static final boolean HAVE_GETTEXTCONTENT = haveGetTextContent();

    private static String getNodeValue(NodeList list) {
        return myGetTextContent(list.item(0));
    }

    @SuppressLint("NewApi")
    private static String myGetTextContent(Node n) {
        String s;
        if (HAVE_GETTEXTCONTENT) {
            s = n.getTextContent();
        } else {
            s = getFirstTextNode(n);
        }
        return s;
    }

    private static String getFirstTextNode(Node baseNode) {
        for (Node child = baseNode.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                return child.getNodeValue();
            }
        }
        return "";
    }

    public static List<Channel> getRadioChannels(Radio radio, Context context, boolean redownload) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        builder = factory.newDocumentBuilder();

        String assetName = "xml/" + radio.getName() + ".xml";
        File overrideFile = new File(Utils.OVERRIDE_DIR, assetName);

        List<Channel> channels = null;
        Document dom = null;
        if (overrideFile.exists()) {
            InputStream is = new FileInputStream(overrideFile);
            try {
                dom = builder.parse(is);
                channels = parseDom(radio, dom);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("PiterFM: ", e.getMessage());
            } finally {
                try { is.close(); } catch (IOException e) {}
            }
        } else {
            byte[] buf = new byte[1024];
            InputStream is = null;
            FileOutputStream os = null;
            try {
                is = context.getAssets().open(assetName);
                os = new FileOutputStream(overrideFile);
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            } finally {
                if (is != null) try { is.close(); } catch (IOException e) {}
                if (os != null) try { os.close(); } catch (IOException e) {}
            }
        }
        if (channels == null) {
            InputStream is = context.getAssets().open(assetName);
            try {
                dom = builder.parse(is);
            } finally {
                try { is.close(); } catch (IOException e) {}
            }
        }

        if (redownload) {
            new ChannelsDownloader().doStuff(dom, overrideFile);
            channels = null;
        }

        if (channels == null) {
            channels = parseDom(radio, dom);
        }

        return channels;
    }

    private static List<Channel> parseDom(Radio radio, Document dom) throws Exception {
        List<Channel> channels = new ArrayList<Channel>();
        
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


    public static String getCurrentTrackTime(String channelId) {
        TrackCalendar trackCal = new TrackCalendar();
        Date date = new Date(System.currentTimeMillis() - (TIME_MINUTE * 5));
        trackCal.setTime(date);
        trackCal.set(Calendar.SECOND, 0);
        String currentTrack = trackCal.asTrackTime();
        return currentTrack;
    }
}

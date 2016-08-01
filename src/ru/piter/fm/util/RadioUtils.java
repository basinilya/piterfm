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

    public static final long TIME_MINUTE = 60000;

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

    public static TrackCalendar getCurrentTrackTime(String channelId) {
        TrackCalendar trackCal = new TrackCalendar();
        trackCal.setClientTimeInMillis(System.currentTimeMillis() - (TIME_MINUTE * 5));
        trackCal.set(Calendar.SECOND, 0);
        trackCal.set(Calendar.MILLISECOND, 0);
        return trackCal;
    }
}

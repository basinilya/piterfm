package ru.piter.fm.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import net.htmlparser.jericho.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.08.2010
 * Time: 17:18:07
 * To change this template use File | Settings | File Templates.
 */
public class RadioUtil {

    private static final String TAG = "RadioUtil:";

    private static final String PITER_FM_URL = "http://piter.fm/stations";
    private static final String MOSKVA_FM_URL = "http://moskva.fm/stations";
    private static final String CHANNEL_HOST = "fresh.moskva.fm";
    private static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd/HHmm");
    private static final long TIME_MINUTE = 60000;
    public static final File APP_DIR = new File(Environment.getExternalStorageDirectory() + "/piterfm");
    public static final File IMG_DIR = new File(APP_DIR + "/img");

    private static InputStream openConnection(String url) {

        InputStream content = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpParams httpParameters = new BasicHttpParams();
            //HttpConnectionParams.setConnectionTimeout(httpParameters, 3000); // Set the timeout in milliseconds until a connection is established.
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);   //// Set the default socket timeout (SO_TIMEOUT)  in milliseconds which is the timeout for waiting for data
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpclient.execute(httpGet);
            content = response.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    private static Bitmap getChannelLogo(String URL) {
        Bitmap bMap = null;
        try {
            String name = URL.split("/")[6];
            String path = IMG_DIR.getAbsolutePath() + "/" + name;
            InputStream in = new FileInputStream(path);
            bMap = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e1) {
            InputStream in = openConnection(URL);
            bMap = BitmapFactory.decodeStream(in);
            return bMap;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bMap;

    }


    public static List<Channel> getRadioChannels(String radio) throws Exception {
        List<Channel> channels = new ArrayList<Channel>();
        InputStream stream = null;
        Source source = null;
        if (radio.equals(Radio.PITER_FM)) stream = openConnection(PITER_FM_URL);
        if (radio.equals(Radio.MOSKVA_FM)) stream = openConnection(MOSKVA_FM_URL);
        source = new Source(stream);

        Element mainDiv = source.getElementById("col_main");
        Element stationsList = mainDiv.getFirstElementByClass("list_station_wide");
        List<Element> stations = stationsList.getAllElements(HTMLElementName.LI);
        Iterator iter = stations.iterator();
         while (iter.hasNext()) {
            try {
                Element li = (Element) iter.next();
                Element a = li.getFirstElement(HTMLElementName.A);
                Element img = a.getFirstElement(HTMLElementName.IMG);
                Element p = li.getFirstElement(HTMLElementName.P);
                Element rangeSpan = p.getFirstElementByClass("amount");
                Element playLink = p.getFirstElementByClass("button_item_play");

                Channel ch = new Channel();
                ch.setName(img.getAttributeValue("title"));
                ch.setRange(rangeSpan.getContent().toString());
                String href = playLink.getAttributeValue("href");
                ch.setChannelId(href.split("/")[4]);
                ch.setLogo(getChannelLogo(img.getAttributeValue("src")));
                channels.add(ch);
            } catch (Exception e) {
                continue;
            }
        }
        return channels;
    }


    public static void downloadTrack(String trackUrl) throws IOException, NullPointerException {
        InputStream in = null;
        in = openConnection(trackUrl);
        if (!APP_DIR.exists()) APP_DIR.mkdir();
        FileOutputStream fos = new FileOutputStream(new File(APP_DIR, getTrackNameFromUrl(trackUrl)));
        byte[] buffer = new byte[512];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.flush();
        fos.close();
        in.close();

    }

    public static void clearDirectory(File dir) {
        if (!isSdAvailible()) return;
        if (!dir.exists()) return;
        for (File f : dir.listFiles()) {
            if (!f.delete()) {
                if (f.isDirectory()) {
                    clearDirectory(f);
                }
                f.delete();
            }

        }
    }


    public static boolean deletePreviousTrack(String previousTrackUrl) {
        File fileToDelete = new File(APP_DIR, getTrackNameFromUrl(previousTrackUrl));
        return fileToDelete.delete();
    }


    public static String getTrackUrl(Channel currentChannel) {
        String channelId = currentChannel.getChannelId();
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        Calendar cal = new GregorianCalendar(tz);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HHmm");
        dateFormat.setCalendar(cal);
        Date date = new Date(System.currentTimeMillis() - (TIME_MINUTE * 5));
        String currentTrack = dateFormat.format(date);
        return "http://" + CHANNEL_HOST + "/files/" + channelId + "/mp4/" + currentTrack + ".mp4";
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
            nextTrack = "http://" + CHANNEL_HOST + "/files/" + channelId + "/mp4/" + track + ".mp4";
            return nextTrack;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return nextTrack;

    }

    public static String getTrackNameFromUrl(String trackUrl) {
        String[] a = trackUrl.split("/");
        String trackName = a[4] + "_" + a[5] + "_" + a[6] + "_" + a[7] + "_" + a[8] + "_" + a[9];
        return trackName;
    }


    public static boolean isSdAvailible() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return false;
        } else {
            return false;
        }
    }

}

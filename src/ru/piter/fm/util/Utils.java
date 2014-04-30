package ru.piter.fm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 26.08.2010
 * Time: 17:18:07
 * To change this template use File | SettingsActivity | File Templates.
 */
public class Utils {

    public static final File SD_DIR = Environment.getExternalStorageDirectory();
    public static final File APP_DIR = new File(SD_DIR + "/piterfm");
    public static final File CACHE_DIR = new File(APP_DIR + "/cache");
    public static final File CHUNKS_DIR = new File(APP_DIR + "/chunks");
    public static final File LOG_DIR = new File(APP_DIR + "/log");


    static {
        if (!APP_DIR.exists()) APP_DIR.mkdir();
        if (!CACHE_DIR.exists()) CACHE_DIR.mkdir();
        if (!CHUNKS_DIR.exists()) CHUNKS_DIR.mkdir();
        if (!LOG_DIR.exists()) LOG_DIR.mkdir();
    }


    public static void writeFile(String filename, String content){

        try {
            Writer writer = new BufferedWriter(new FileWriter(new File(APP_DIR, filename)));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** On Gingerbread system property "http.proxyHost" not set by default */
    @SuppressWarnings("deprecation")
    public static void fixProxy(Context ctx) {
        if (System.getProperty("http.proxyHost") == null) {
            final String proxyHost = android.net.Proxy.getHost(ctx);
            final int proxyPort = android.net.Proxy.getPort(ctx); // returns -1 if not set
            if (proxyPort != -1) {
                System.setProperty("http.proxyHost", proxyHost);
                System.setProperty("http.proxyPort", Integer.toString(proxyPort));
            }
        }
    }

    public static InputStream openConnection(String url) throws IOException {

        InputStream content = null;
        URLConnection httpclient = new URL(url).openConnection();
        httpclient.setUseCaches(false);
        httpclient.setConnectTimeout(20000);
        httpclient.setReadTimeout(30000);
        content = httpclient.getInputStream();
        return content;
    }


    public static void downloadTrack(String trackUrl) throws Exception {
        Log.d("PiterFM", "Download track = " + trackUrl);
        InputStream in = openConnection(trackUrl);
        FileOutputStream fos = new FileOutputStream(new File(CHUNKS_DIR, RadioUtils.getTrackNameFromUrl(trackUrl)));
        byte[] buffer = new byte[512];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.flush();
        fos.close();
        in.close();

    }

    public static void clearDirectory(final File dir) {
        new Thread() {
            public void run() {
                if (!isSdAvailable()) return;
                if (!dir.exists()) return;
                for (File f : dir.listFiles())
                    f.delete();
            }
        }.start();

    }

    public static boolean deletePreviousTrack(String previousTrackUrl) {
        File fileToDelete = new File(CHUNKS_DIR, RadioUtils.getTrackNameFromUrl(previousTrackUrl));
        return fileToDelete.delete();
    }


    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else
            return false;
    }

    public static boolean isSdAvailable() {
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

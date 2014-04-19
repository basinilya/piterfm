package ru.piter.fm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

public class Utils
{
  public static final File APP_DIR;
  public static final File CACHE_DIR;
  public static final File CHUNKS_DIR;
  public static final File LOG_DIR;
  public static final File SD_DIR = ;
  
  static
  {
    APP_DIR = new File(SD_DIR + "/piterfm");
    CACHE_DIR = new File(APP_DIR + "/cache");
    CHUNKS_DIR = new File(APP_DIR + "/chunks");
    LOG_DIR = new File(APP_DIR + "/log");
    if (!APP_DIR.exists()) {
      APP_DIR.mkdir();
    }
    if (!CACHE_DIR.exists()) {
      CACHE_DIR.mkdir();
    }
    if (!CHUNKS_DIR.exists()) {
      CHUNKS_DIR.mkdir();
    }
    if (!LOG_DIR.exists()) {
      LOG_DIR.mkdir();
    }
  }
  
  public static void clearDirectory(File paramFile)
  {
    new Thread()
    {
      public void run()
      {
        if (!Utils.isSdAvailable()) {}
        for (;;)
        {
          return;
          if (this.val$dir.exists())
          {
            File[] arrayOfFile = this.val$dir.listFiles();
            int i = arrayOfFile.length;
            for (int j = 0; j < i; j++) {
              arrayOfFile[j].delete();
            }
          }
        }
      }
    }.start();
  }
  
  public static boolean deletePreviousTrack(String paramString)
  {
    return new File(CHUNKS_DIR, RadioUtils.getTrackNameFromUrl(paramString)).delete();
  }
  
  public static void downloadTrack(String paramString)
    throws Exception
  {
    Log.d("PiterFM", "Download track = " + paramString);
    InputStream localInputStream = openConnection(paramString);
    FileOutputStream localFileOutputStream = new FileOutputStream(new File(CHUNKS_DIR, RadioUtils.getTrackNameFromUrl(paramString)));
    byte[] arrayOfByte = new byte[512];
    for (;;)
    {
      int i = localInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      localFileOutputStream.write(arrayOfByte, 0, i);
    }
    localFileOutputStream.flush();
    localFileOutputStream.close();
    localInputStream.close();
  }
  
  public static boolean isInternetAvailable(Context paramContext)
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    return (localNetworkInfo != null) && (localNetworkInfo.isConnectedOrConnecting());
  }
  
  public static boolean isSdAvailable()
  {
    String str = Environment.getExternalStorageState();
    boolean bool2;
    if ("mounted".equals(str)) {
      bool2 = true;
    }
    boolean bool1;
    do
    {
      return bool2;
      bool1 = "mounted_ro".equals(str);
      bool2 = false;
    } while (!bool1);
    return false;
  }
  
  public static InputStream openConnection(String paramString)
    throws Exception
  {
    HttpGet localHttpGet = new HttpGet(paramString);
    BasicHttpParams localBasicHttpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 20000);
    HttpConnectionParams.setSoTimeout(localBasicHttpParams, 30000);
    return new DefaultHttpClient(localBasicHttpParams).execute(localHttpGet).getEntity().getContent();
  }
  
  public static void writeFile(String paramString1, String paramString2)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(new File(APP_DIR, paramString1)));
      localBufferedWriter.write(paramString2);
      localBufferedWriter.flush();
      localBufferedWriter.close();
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.Utils
 * JD-Core Version:    0.7.0.1
 */
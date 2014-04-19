package ru.piter.fm.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.radio.Track;

public class RadioUtils
{
  private static final String CHANNEL_HOST = "http://fresh.moskva.fm";
  private static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd/HHmm");
  private static final String MOSKVA_FM_URL = "http://moskva.fm/stations";
  private static final String PITER_FM_URL = "http://piter.fm/stations";
  private static final long TIME_MINUTE = 60000L;
  
  public static String getDuration(String paramString)
  {
    String str1 = String.valueOf(Integer.parseInt(paramString) / 60);
    int i = Integer.parseInt(paramString) % 60;
    String str2 = String.valueOf(Integer.parseInt(paramString) % 60);
    if (i < 10) {
      str2 = "0" + str2;
    }
    return str1 + ":" + str2;
  }
  
  public static Date getGMT4Date(Date paramDate, String paramString)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+4"));
    localGregorianCalendar.setTimeInMillis(paramDate.getTime());
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(1, localGregorianCalendar.get(1));
    localCalendar.set(2, localGregorianCalendar.get(2));
    localCalendar.set(5, localGregorianCalendar.get(5));
    localCalendar.set(11, localGregorianCalendar.get(11));
    localCalendar.set(12, localGregorianCalendar.get(12));
    localCalendar.set(13, localGregorianCalendar.get(13));
    localCalendar.set(14, localGregorianCalendar.get(14));
    return localCalendar.getTime();
  }
  
  public static String getNextTrackUrl(String paramString)
  {
    String[] arrayOfString = paramString.split("/");
    String str1 = arrayOfString[4];
    String str2 = arrayOfString[6];
    String str3 = arrayOfString[7];
    String str4 = arrayOfString[8];
    String str5 = arrayOfString[9].split("\\.")[0];
    String str6 = str5.substring(0, 2);
    String str7 = str5.substring(2, 4);
    try
    {
      Date localDate = DF.parse(str2 + "/" + str3 + "/" + str4 + "/" + str6 + str7);
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.setTime(localDate);
      localCalendar.add(12, 1);
      String str8 = DF.format(localCalendar.getTime());
      String str9 = "http://fresh.moskva.fm/files/" + str1 + "/mp4/" + str8 + ".mp4";
      return str9;
    }
    catch (ParseException localParseException)
    {
      localParseException.printStackTrace();
      Log.d("PiterFM: ", localParseException.getMessage() + "\n" + localParseException.getCause());
    }
    return paramString;
  }
  
  private static String getNodeValue(NodeList paramNodeList)
  {
    return paramNodeList.item(0).getTextContent();
  }
  
  public static List<Channel> getRadioChannels(Radio paramRadio, Context paramContext)
    throws Exception
  {
    ArrayList localArrayList = new ArrayList();
    InputStream localInputStream = paramContext.getAssets().open("xml/" + paramRadio.getName() + ".xml");
    NodeList localNodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(localInputStream).getDocumentElement().getElementsByTagName("channel");
    int i = 0;
    for (;;)
    {
      if (i < localNodeList.getLength()) {
        try
        {
          Channel localChannel = new Channel();
          Element localElement = (Element)localNodeList.item(i);
          localChannel.setChannelId(getNodeValue(localElement.getElementsByTagName("id")));
          localChannel.setName(getNodeValue(localElement.getElementsByTagName("name")));
          localChannel.setRange(getNodeValue(localElement.getElementsByTagName("range")));
          localChannel.setLogoUrl(getNodeValue(localElement.getElementsByTagName("logo")));
          localChannel.setTranslationUrl(getNodeValue(localElement.getElementsByTagName("translation")));
          localChannel.setRadio(paramRadio);
          localArrayList.add(localChannel);
          i++;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localException.printStackTrace();
            Log.d("PiterFM: ", localException.getMessage());
          }
        }
      }
    }
    return localArrayList;
  }
  
  public static String getTrackNameFromUrl(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    String[] arrayOfString = paramString.split("/");
    return arrayOfString[4] + "_" + arrayOfString[5] + "_" + arrayOfString[6] + "_" + arrayOfString[7] + "_" + arrayOfString[8] + "_" + arrayOfString[9];
  }
  
  public static int getTrackOffset(String paramString)
  {
    return Integer.parseInt(paramString.replaceAll(":", "/").substring(17, 19));
  }
  
  public static String getTrackUrl(String paramString1, String paramString2)
  {
    String str1 = paramString1.replaceAll(":", "/");
    String str2 = str1.substring(0, 11) + str1.substring(11, 17).replaceAll("/", "");
    return "http://fresh.moskva.fm/files/" + paramString2 + "/mp4/" + str2 + ".mp4";
  }
  
  public static String getTrackUrl(Channel paramChannel)
  {
    String str1 = paramChannel.getChannelId();
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HHmm");
    Date localDate = getGMT4Date(new Date(System.currentTimeMillis() - 300000L), "Europe/Moscow");
    String str2 = localSimpleDateFormat.format(localDate);
    String str3 = "http://fresh.moskva.fm/files/" + str1 + "/mp4/" + str2 + ".mp4";
    if (str3 == null) {
      Log.d("PiterFM", "trackUrl is null ! Date = " + localDate + " Channel = " + paramChannel + " currentTrack = " + str2);
    }
    Log.d("PiterFM", "trackUrl = " + str3);
    return str3;
  }
  
  public static List<Track> getTracks(String paramString)
  {
    Log.d("PiterFM", "tracks url = " + paramString);
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localArrayList = new ArrayList();
    try
    {
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      Log.d("Parser", new URL(paramString).getContent().toString());
      Element localElement1 = localDocumentBuilder.parse(new URL(paramString).openStream()).getDocumentElement();
      NodeList localNodeList1 = localElement1.getElementsByTagName("track");
      Log.d("Parser ", "Tracks size = " + localNodeList1.getLength());
      for (int i = 0; i < localNodeList1.getLength(); i++)
      {
        Track localTrack1 = new Track(1);
        Element localElement2 = (Element)localNodeList1.item(i);
        localTrack1.setArtistName(localElement2.getAttribute("name"));
        localTrack1.setTrackName(localElement2.getAttribute("artistName"));
        localTrack1.setDuration(localElement2.getAttribute("duration"));
        String str1 = localElement2.getAttribute("startAt");
        localTrack1.setStartAt(Long.parseLong(str1));
        Date localDate1 = getGMT4Date(new Date(1000L * Long.parseLong(str1)), "Europe/Moscow");
        localTrack1.setTime(new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(localDate1));
        localTrack1.setPlayCount(localElement2.getAttribute("playCount"));
        localArrayList.add(localTrack1);
      }
      NodeList localNodeList2 = localElement1.getElementsByTagName("show");
      Log.d("PiterFM", "shows length = " + localNodeList2.getLength());
      for (int j = 0; j < localNodeList2.getLength(); j++)
      {
        Track localTrack2 = new Track(2);
        Element localElement3 = (Element)localNodeList2.item(j);
        localTrack2.setTrackName(localElement3.getAttribute("name"));
        localTrack2.setDuration(localElement3.getAttribute("duration"));
        String str2 = localElement3.getAttribute("startAt");
        localTrack2.setStartAt(Long.parseLong(str2));
        Date localDate2 = getGMT4Date(new Date(1000L * Long.parseLong(str2)), "Europe/Moscow");
        localTrack2.setTime(new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(localDate2));
        localTrack2.setPlayCount("0");
        localArrayList.add(localTrack2);
      }
      return localArrayList;
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      localParserConfigurationException.printStackTrace();
      Log.d("PiterFM: ", localParserConfigurationException.getMessage());
      return localArrayList;
    }
    catch (SAXException localSAXException)
    {
      localSAXException.printStackTrace();
      Log.d("PiterFM: ", localSAXException.getMessage());
      return localArrayList;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      Log.d("PiterFM: ", localIOException.getMessage());
    }
  }
  
  public static String getTracksUrl(Date paramDate, Channel paramChannel)
  {
    String str = new SimpleDateFormat("yyyyMMdd").format(paramDate);
    return paramChannel.getRadio().getHostUrl() + "/station.xml.html?station=" + paramChannel.getChannelId() + "&day=" + str;
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.RadioUtils
 * JD-Core Version:    0.7.0.1
 */
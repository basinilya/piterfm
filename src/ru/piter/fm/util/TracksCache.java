package ru.piter.fm.util;

import java.util.HashMap;
import java.util.List;
import ru.piter.fm.radio.Track;

public class TracksCache
{
  private static HashMap<String, Long> time = new HashMap();
  private static HashMap<String, HashMap<String, List<Track>>> tracks = new HashMap();
  
  public static List<Track> get(String paramString1, String paramString2)
  {
    HashMap localHashMap = (HashMap)tracks.get(paramString1);
    if (localHashMap != null)
    {
      List localList = (List)localHashMap.get(paramString2);
      Long localLong = (Long)time.get(paramString1);
      if ((localList != null) && (System.currentTimeMillis() - localLong.longValue() < 300000L)) {
        return localList;
      }
    }
    return null;
  }
  
  public static void put(String paramString1, String paramString2, List<Track> paramList)
  {
    HashMap localHashMap = (HashMap)tracks.get(paramString1);
    if (localHashMap == null) {
      localHashMap = new HashMap();
    }
    localHashMap.put(paramString2, paramList);
    tracks.put(paramString1, localHashMap);
    time.put(paramString1, Long.valueOf(System.currentTimeMillis()));
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.TracksCache
 * JD-Core Version:    0.7.0.1
 */
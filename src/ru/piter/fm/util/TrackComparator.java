package ru.piter.fm.util;

import java.util.Comparator;
import ru.piter.fm.radio.Track;

public class TrackComparator
  implements Comparator
{
  private static String sortBy = "time";
  
  public TrackComparator(String paramString)
  {
    sortBy = paramString;
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    Track localTrack1 = (Track)paramObject1;
    Track localTrack2 = (Track)paramObject2;
    int i;
    int j;
    if (sortBy.equals("sort_by_rate"))
    {
      i = Integer.parseInt(localTrack1.getPlayCount());
      j = Integer.parseInt(localTrack2.getPlayCount());
      if (i >= j) {}
    }
    long l1;
    long l2;
    do
    {
      return 1;
      if (i > j) {
        return -1;
      }
      return 0;
      if (!sortBy.equals("sort_by_time")) {
        break;
      }
      l1 = localTrack1.getStartAt();
      l2 = localTrack2.getStartAt();
    } while (l1 > l2);
    if (l1 < l2) {
      return -1;
    }
    return 0;
    return 0;
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.TrackComparator
 * JD-Core Version:    0.7.0.1
 */
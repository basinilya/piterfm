package ru.piter.fm.util;

import java.util.Comparator;
import ru.piter.fm.radio.Channel;

public class ChannelComparator
  implements Comparator
{
  private static String sortBy = "range";
  
  public ChannelComparator(String paramString)
  {
    sortBy = paramString;
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    Channel localChannel1 = (Channel)paramObject1;
    Channel localChannel2 = (Channel)paramObject2;
    float f1;
    float f2;
    int i;
    if (sortBy.equals("sort_by_range"))
    {
      f1 = Float.parseFloat(localChannel1.getRange().split("\\s")[0]);
      f2 = Float.parseFloat(localChannel2.getRange().split("\\s")[0]);
      if (f1 < f2) {
        i = -1;
      }
    }
    boolean bool1;
    do
    {
      boolean bool2;
      do
      {
        return i;
        bool2 = f1 < f2;
        i = 0;
      } while (!bool2);
      return 1;
      bool1 = sortBy.equals("sort_by_name");
      i = 0;
    } while (!bool1);
    return localChannel1.getName().compareTo(localChannel2.getName());
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.ChannelComparator
 * JD-Core Version:    0.7.0.1
 */
package com.nostra13.universalimageloader.cache.disc;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class LimitedDiscCache
  extends BaseDiscCache
{
  private int cacheSize = 0;
  private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap());
  private int sizeLimit;
  
  public LimitedDiscCache(File paramFile, int paramInt)
  {
    super(paramFile);
    this.sizeLimit = paramInt;
    calculateCacheSizeAndFillUsageMap();
  }
  
  private void calculateCacheSizeAndFillUsageMap()
  {
    int i = 0;
    for (File localFile : getCacheDir().listFiles())
    {
      i += getSize(localFile);
      this.lastUsageDates.put(localFile, Long.valueOf(localFile.lastModified()));
    }
    this.cacheSize = i;
  }
  
  private int removeNext()
  {
    int i;
    if (this.lastUsageDates.isEmpty()) {
      i = 0;
    }
    for (;;)
    {
      return i;
      Object localObject1 = null;
      File localFile = null;
      Set localSet = this.lastUsageDates.entrySet();
      synchronized (this.lastUsageDates)
      {
        Iterator localIterator = localSet.iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (localFile == null)
          {
            localFile = (File)localEntry.getKey();
            localObject1 = (Long)localEntry.getValue();
          }
          else
          {
            Long localLong = (Long)localEntry.getValue();
            if (localLong.longValue() < ((Long)localObject1).longValue())
            {
              localObject1 = localLong;
              localFile = (File)localEntry.getKey();
            }
          }
        }
        i = getSize(localFile);
        if (!localFile.delete()) {
          continue;
        }
        this.lastUsageDates.remove(localFile);
        return i;
      }
    }
  }
  
  public void clear()
  {
    this.lastUsageDates.clear();
    this.cacheSize = 0;
    super.clear();
  }
  
  public File get(String paramString)
  {
    File localFile = super.get(paramString);
    Long localLong = Long.valueOf(System.currentTimeMillis());
    localFile.setLastModified(localLong.longValue());
    this.lastUsageDates.put(localFile, localLong);
    return localFile;
  }
  
  protected abstract int getSize(File paramFile);
  
  public void put(String paramString, File paramFile)
  {
    int i = getSize(paramFile);
    for (;;)
    {
      int j;
      if (i + this.cacheSize > this.sizeLimit)
      {
        j = removeNext();
        if (j != 0) {}
      }
      else
      {
        this.cacheSize = (i + this.cacheSize);
        Long localLong = Long.valueOf(System.currentTimeMillis());
        paramFile.setLastModified(localLong.longValue());
        this.lastUsageDates.put(paramFile, localLong);
        return;
      }
      this.cacheSize -= j;
    }
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.cache.disc.LimitedDiscCache
 * JD-Core Version:    0.7.0.1
 */
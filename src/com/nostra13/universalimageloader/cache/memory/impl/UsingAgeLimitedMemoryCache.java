package com.nostra13.universalimageloader.cache.memory.impl;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class UsingAgeLimitedMemoryCache
  extends LimitedMemoryCache<String, Bitmap>
{
  private final Map<Bitmap, Long> lastUsageDates = Collections.synchronizedMap(new HashMap());
  
  public UsingAgeLimitedMemoryCache(int paramInt)
  {
    super(paramInt);
  }
  
  public void clear()
  {
    this.lastUsageDates.clear();
    super.clear();
  }
  
  protected Reference<Bitmap> createReference(Bitmap paramBitmap)
  {
    return new WeakReference(paramBitmap);
  }
  
  public Bitmap get(String paramString)
  {
    Bitmap localBitmap = (Bitmap)super.get(paramString);
    if ((localBitmap != null) && ((Long)this.lastUsageDates.get(localBitmap) != null)) {
      this.lastUsageDates.put(localBitmap, Long.valueOf(System.currentTimeMillis()));
    }
    return localBitmap;
  }
  
  protected int getSize(Bitmap paramBitmap)
  {
    return paramBitmap.getRowBytes() * paramBitmap.getHeight();
  }
  
  public boolean put(String paramString, Bitmap paramBitmap)
  {
    if (super.put(paramString, paramBitmap))
    {
      this.lastUsageDates.put(paramBitmap, Long.valueOf(System.currentTimeMillis()));
      return true;
    }
    return false;
  }
  
  public void remove(String paramString)
  {
    Bitmap localBitmap = (Bitmap)super.get(paramString);
    if (localBitmap != null) {
      this.lastUsageDates.remove(localBitmap);
    }
    super.remove(paramString);
  }
  
  protected Bitmap removeNext()
  {
    Object localObject1 = null;
    Bitmap localBitmap = null;
    Set localSet = this.lastUsageDates.entrySet();
    synchronized (this.lastUsageDates)
    {
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (localBitmap == null)
        {
          localBitmap = (Bitmap)localEntry.getKey();
          localObject1 = (Long)localEntry.getValue();
        }
        else
        {
          Long localLong = (Long)localEntry.getValue();
          if (localLong.longValue() < ((Long)localObject1).longValue())
          {
            localObject1 = localLong;
            localBitmap = (Bitmap)localEntry.getKey();
          }
        }
      }
      this.lastUsageDates.remove(localBitmap);
      return localBitmap;
    }
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.cache.memory.impl.UsingAgeLimitedMemoryCache
 * JD-Core Version:    0.7.0.1
 */
package com.nostra13.universalimageloader.cache.disc.impl;

import com.nostra13.universalimageloader.cache.disc.BaseDiscCache;
import java.io.File;

public class UnlimitedDiscCache
  extends BaseDiscCache
{
  public UnlimitedDiscCache(File paramFile)
  {
    super(paramFile);
  }
  
  protected String keyToFileName(String paramString)
  {
    return String.valueOf(paramString.hashCode());
  }
  
  public void put(String paramString, File paramFile) {}
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache
 * JD-Core Version:    0.7.0.1
 */
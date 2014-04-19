package com.nostra13.universalimageloader.cache.disc.impl;

import com.nostra13.universalimageloader.cache.disc.LimitedDiscCache;
import java.io.File;

public class FileCountLimitedDiscCache
  extends LimitedDiscCache
{
  public FileCountLimitedDiscCache(File paramFile, int paramInt)
  {
    super(paramFile, paramInt);
  }
  
  protected int getSize(File paramFile)
  {
    return 1;
  }
  
  protected String keyToFileName(String paramString)
  {
    return String.valueOf(paramString.hashCode());
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache
 * JD-Core Version:    0.7.0.1
 */
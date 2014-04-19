package com.nostra13.universalimageloader.cache.disc;

import java.io.File;

public abstract interface DiscCacheAware
{
  public abstract void clear();
  
  public abstract File get(String paramString);
  
  public abstract void put(String paramString, File paramFile);
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.cache.disc.DiscCacheAware
 * JD-Core Version:    0.7.0.1
 */
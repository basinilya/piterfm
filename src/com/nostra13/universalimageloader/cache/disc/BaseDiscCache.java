package com.nostra13.universalimageloader.cache.disc;

import java.io.File;

public abstract class BaseDiscCache
  implements DiscCacheAware
{
  private File cacheDir;
  
  public BaseDiscCache(File paramFile)
  {
    this.cacheDir = paramFile;
  }
  
  public void clear()
  {
    File[] arrayOfFile = this.cacheDir.listFiles();
    if (arrayOfFile != null)
    {
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++) {
        arrayOfFile[j].delete();
      }
    }
  }
  
  public File get(String paramString)
  {
    String str = keyToFileName(paramString);
    return new File(this.cacheDir, str);
  }
  
  protected File getCacheDir()
  {
    return this.cacheDir;
  }
  
  protected abstract String keyToFileName(String paramString);
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.cache.disc.BaseDiscCache
 * JD-Core Version:    0.7.0.1
 */
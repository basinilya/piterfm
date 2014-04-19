package com.nostra13.universalimageloader.core;

import java.util.Comparator;

class MemoryCacheKeyUtil
{
  private static final String MEMORY_CACHE_KEY_FORMAT = "%s_%sx%s";
  private static final String URL_AND_SIZE_SEPARATOR = "_";
  
  static Comparator<String> createFuzzyKeyComparator()
  {
    new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        return paramAnonymousString1.substring(0, paramAnonymousString1.lastIndexOf("_")).compareTo(paramAnonymousString2.substring(0, paramAnonymousString2.lastIndexOf("_")));
      }
    };
  }
  
  static String generateKey(String paramString, ImageSize paramImageSize)
  {
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = paramString;
    arrayOfObject[1] = Integer.valueOf(paramImageSize.width);
    arrayOfObject[2] = Integer.valueOf(paramImageSize.height);
    return String.format("%s_%sx%s", arrayOfObject);
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.core.MemoryCacheKeyUtil
 * JD-Core Version:    0.7.0.1
 */
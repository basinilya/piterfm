package com.nostra13.universalimageloader.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;
import java.io.IOException;

public final class StorageUtils
{
  private static final String INDIVIDUAL_DIR_NAME = "uil-images";
  
  public static File getCacheDirectory(Context paramContext)
  {
    boolean bool = Environment.getExternalStorageState().equals("mounted");
    File localFile = null;
    if (bool) {
      localFile = getExternalCacheDir(paramContext);
    }
    if (localFile == null) {
      localFile = paramContext.getCacheDir();
    }
    return localFile;
  }
  
  private static File getExternalCacheDir(Context paramContext)
  {
    File localFile1 = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
    File localFile2 = new File(new File(localFile1, paramContext.getPackageName()), "cache");
    if (!localFile2.exists()) {}
    try
    {
      new File(localFile1, ".nomedia").createNewFile();
      if (!localFile2.mkdirs())
      {
        Log.w(ImageLoader.TAG, "Unable to create external cache directory");
        localFile2 = null;
      }
      return localFile2;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.w(ImageLoader.TAG, "Can't create \".nomedia\" file in application external cache directory", localIOException);
      }
    }
  }
  
  public static File getIndividualCacheDirectory(Context paramContext)
  {
    File localFile1 = getCacheDirectory(paramContext);
    File localFile2 = new File(localFile1, "uil-images");
    if ((!localFile2.exists()) && (!localFile2.mkdir())) {
      localFile2 = localFile1;
    }
    return localFile2;
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.utils.StorageUtils
 * JD-Core Version:    0.7.0.1
 */
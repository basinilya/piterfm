package com.nostra13.universalimageloader.core;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader
{
  private static final int ATTEMPT_COUNT_TO_DECODE_BITMAP = 3;
  private static final String ERROR_IMAGEVIEW_CONTEXT = "ImageView context must be of Activity typeIf you create ImageView in code you must pass your current activity in ImageView constructor (e.g. new ImageView(MyActivity.this); or new ImageView(getActivity())).";
  private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
  private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
  private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference are required)";
  private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
  private static final String LOG_CACHE_IMAGE_ON_DISC = "Cache image on disc [%s]";
  private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";
  private static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
  private static final String LOG_LOAD_IMAGE_FROM_INTERNET = "Load image from Internet [%s]";
  private static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";
  private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
  public static final String TAG = ImageLoader.class.getSimpleName();
  private static volatile ImageLoader instance;
  private Map<ImageView, String> cacheKeyForImageView = Collections.synchronizedMap(new WeakHashMap());
  private ExecutorService cachedImageLoadingExecutor;
  private ImageLoaderConfiguration configuration;
  private ImageLoadingListener emptyListener;
  private ExecutorService imageLoadingExecutor;
  private boolean loggingEnabled = false;
  
  private void checkExecutors()
  {
    if ((this.imageLoadingExecutor == null) || (this.imageLoadingExecutor.isShutdown())) {
      this.imageLoadingExecutor = Executors.newFixedThreadPool(this.configuration.threadPoolSize, this.configuration.displayImageThreadFactory);
    }
    if ((this.cachedImageLoadingExecutor == null) || (this.cachedImageLoadingExecutor.isShutdown())) {
      this.cachedImageLoadingExecutor = Executors.newSingleThreadExecutor(this.configuration.displayImageThreadFactory);
    }
  }
  
  private ImageSize getImageSizeScaleTo(ImageView paramImageView)
  {
    int i = -1;
    int j = -1;
    try
    {
      Field localField1 = ImageView.class.getDeclaredField("mMaxWidth");
      Field localField2 = ImageView.class.getDeclaredField("mMaxHeight");
      localField1.setAccessible(true);
      localField2.setAccessible(true);
      int n = ((Integer)localField1.get(paramImageView)).intValue();
      int i1 = ((Integer)localField2.get(paramImageView)).intValue();
      if ((n >= 0) && (n < 2147483647)) {
        i = n;
      }
      if ((i1 >= 0) && (i1 < 2147483647)) {
        j = i1;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        ViewGroup.LayoutParams localLayoutParams;
        int k;
        int m;
        Log.e(TAG, localException.getMessage(), localException);
      }
    }
    if ((i < 0) && (j < 0))
    {
      localLayoutParams = paramImageView.getLayoutParams();
      i = localLayoutParams.width;
      j = localLayoutParams.height;
    }
    if ((i < 0) && (j < 0))
    {
      i = this.configuration.maxImageWidthForMemoryCache;
      j = this.configuration.maxImageHeightForMemoryCache;
      k = paramImageView.getContext().getResources().getConfiguration().orientation;
      if (((k == 1) && (i > j)) || ((k == 2) && (i < j)))
      {
        m = i;
        i = j;
        j = m;
      }
    }
    return new ImageSize(i, j);
  }
  
  public static ImageLoader getInstance()
  {
    if (instance == null) {}
    try
    {
      if (instance == null) {
        instance = new ImageLoader();
      }
      return instance;
    }
    finally {}
  }
  
  public void cancelDisplayTask(ImageView paramImageView)
  {
    this.cacheKeyForImageView.remove(paramImageView);
  }
  
  public void clearDiscCache()
  {
    if (this.configuration != null) {
      this.configuration.discCache.clear();
    }
  }
  
  public void clearMemoryCache()
  {
    if (this.configuration != null) {
      this.configuration.memoryCache.clear();
    }
  }
  
  public void displayImage(String paramString, ImageView paramImageView)
  {
    displayImage(paramString, paramImageView, null, null);
  }
  
  public void displayImage(String paramString, ImageView paramImageView, DisplayImageOptions paramDisplayImageOptions)
  {
    displayImage(paramString, paramImageView, paramDisplayImageOptions, null);
  }
  
  public void displayImage(String paramString, ImageView paramImageView, DisplayImageOptions paramDisplayImageOptions, ImageLoadingListener paramImageLoadingListener)
  {
    if (this.configuration == null) {
      throw new RuntimeException("ImageLoader must be init with configuration before using");
    }
    if (paramImageView == null)
    {
      Log.w(TAG, "Wrong arguments were passed to displayImage() method (ImageView reference are required)");
      return;
    }
    if (paramImageLoadingListener == null) {
      paramImageLoadingListener = this.emptyListener;
    }
    if (paramDisplayImageOptions == null) {
      paramDisplayImageOptions = this.configuration.defaultDisplayImageOptions;
    }
    if ((paramString == null) || (paramString.length() == 0))
    {
      this.cacheKeyForImageView.remove(paramImageView);
      if (paramDisplayImageOptions.isShowImageForEmptyUrl())
      {
        paramImageView.setImageResource(paramDisplayImageOptions.getImageForEmptyUrl().intValue());
        return;
      }
      paramImageView.setImageBitmap(null);
      return;
    }
    ImageSize localImageSize = getImageSizeScaleTo(paramImageView);
    String str = MemoryCacheKeyUtil.generateKey(paramString, localImageSize);
    this.cacheKeyForImageView.put(paramImageView, str);
    Bitmap localBitmap = (Bitmap)this.configuration.memoryCache.get(str);
    if ((localBitmap != null) && (!localBitmap.isRecycled()))
    {
      if (this.loggingEnabled) {
        Log.i(TAG, String.format("Load image from memory cache [%s]", new Object[] { str }));
      }
      paramImageLoadingListener.onLoadingStarted();
      paramImageView.setImageBitmap(localBitmap);
      paramImageLoadingListener.onLoadingComplete();
      return;
    }
    paramImageLoadingListener.onLoadingStarted();
    checkExecutors();
    DisplayImageTask localDisplayImageTask = new DisplayImageTask(new ImageLoadingInfo(paramString, paramImageView, localImageSize, paramDisplayImageOptions, paramImageLoadingListener));
    if (localDisplayImageTask.isImageCachedOnDisc()) {
      this.cachedImageLoadingExecutor.submit(localDisplayImageTask);
    }
    while (paramDisplayImageOptions.isShowStubImage())
    {
      paramImageView.setImageResource(paramDisplayImageOptions.getStubImage().intValue());
      return;
      this.imageLoadingExecutor.submit(localDisplayImageTask);
    }
    paramImageView.setImageBitmap(null);
  }
  
  public void displayImage(String paramString, ImageView paramImageView, ImageLoadingListener paramImageLoadingListener)
  {
    displayImage(paramString, paramImageView, null, paramImageLoadingListener);
  }
  
  public void enableLogging()
  {
    this.loggingEnabled = true;
  }
  
  public void init(ImageLoaderConfiguration paramImageLoaderConfiguration)
  {
    if (paramImageLoaderConfiguration == null) {
      try
      {
        throw new IllegalArgumentException("ImageLoader configuration can not be initialized with null");
      }
      finally {}
    }
    if (this.configuration == null)
    {
      this.configuration = paramImageLoaderConfiguration;
      this.emptyListener = new EmptyListener(null);
    }
  }
  
  public void stop()
  {
    if (this.imageLoadingExecutor != null) {
      this.imageLoadingExecutor.shutdown();
    }
    if (this.cachedImageLoadingExecutor != null) {
      this.cachedImageLoadingExecutor.shutdown();
    }
  }
  
  private class DisplayBitmapTask
    implements Runnable
  {
    private final Bitmap bitmap;
    private final ImageLoader.ImageLoadingInfo imageLoadingInfo;
    
    public DisplayBitmapTask(ImageLoader.ImageLoadingInfo paramImageLoadingInfo, Bitmap paramBitmap)
    {
      this.bitmap = paramBitmap;
      this.imageLoadingInfo = paramImageLoadingInfo;
    }
    
    public void run()
    {
      if (this.imageLoadingInfo.isConsistent())
      {
        if (ImageLoader.this.loggingEnabled)
        {
          String str = ImageLoader.TAG;
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = ImageLoader.ImageLoadingInfo.access$400(this.imageLoadingInfo);
          Log.i(str, String.format("Display image in ImageView [%s]", arrayOfObject));
        }
        ImageLoader.ImageLoadingInfo.access$1100(this.imageLoadingInfo).setImageBitmap(this.bitmap);
        ImageLoader.ImageLoadingInfo.access$1000(this.imageLoadingInfo).onLoadingComplete();
      }
    }
  }
  
  private class DisplayImageTask
    implements Runnable
  {
    private final ImageLoader.ImageLoadingInfo imageLoadingInfo;
    
    public DisplayImageTask(ImageLoader.ImageLoadingInfo paramImageLoadingInfo)
    {
      this.imageLoadingInfo = paramImageLoadingInfo;
    }
    
    private Bitmap decodeImage(URL paramURL)
      throws IOException
    {
      ImageDecoder localImageDecoder = new ImageDecoder(paramURL, ImageLoader.ImageLoadingInfo.access$800(this.imageLoadingInfo), ImageLoader.ImageLoadingInfo.access$500(this.imageLoadingInfo).getDecodingType());
      int i = 1;
      for (;;)
      {
        Object localObject = null;
        if (i <= 3) {}
        try
        {
          Bitmap localBitmap = localImageDecoder.decodeFile();
          localObject = localBitmap;
          return localObject;
        }
        catch (OutOfMemoryError localOutOfMemoryError)
        {
          Log.e(ImageLoader.TAG, localOutOfMemoryError.getMessage(), localOutOfMemoryError);
          switch (i)
          {
          }
        }
        label92:
        long l = i * 1000;
        try
        {
          Thread.sleep(l);
          i++;
          continue;
          System.gc();
          break label92;
          ImageLoader.this.configuration.memoryCache.clear();
          System.gc();
          break label92;
          throw localOutOfMemoryError;
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            Log.e(ImageLoader.TAG, localInterruptedException.getMessage(), localInterruptedException);
          }
        }
      }
    }
    
    private void fireImageLoadingFailedEvent()
    {
      tryRunOnUiThread(new Runnable()
      {
        public void run()
        {
          ImageLoader.ImageLoadingInfo.access$1000(ImageLoader.DisplayImageTask.this.imageLoadingInfo).onLoadingFailed();
        }
      });
    }
    
    private boolean isImageCachedOnDisc()
    {
      return ImageLoader.this.configuration.discCache.get(ImageLoader.ImageLoadingInfo.access$700(this.imageLoadingInfo)).exists();
    }
    
    private Bitmap loadBitmap()
    {
      File localFile = ImageLoader.this.configuration.discCache.get(ImageLoader.ImageLoadingInfo.access$700(this.imageLoadingInfo));
      try
      {
        if (localFile.exists())
        {
          if (ImageLoader.this.loggingEnabled)
          {
            String str3 = ImageLoader.TAG;
            Object[] arrayOfObject3 = new Object[1];
            arrayOfObject3[0] = ImageLoader.ImageLoadingInfo.access$400(this.imageLoadingInfo);
            Log.i(str3, String.format("Load image from disc cache [%s]", arrayOfObject3));
          }
          Bitmap localBitmap2 = decodeImage(localFile.toURL());
          if (localBitmap2 != null) {
            return localBitmap2;
          }
        }
        if (ImageLoader.this.loggingEnabled)
        {
          String str2 = ImageLoader.TAG;
          Object[] arrayOfObject2 = new Object[1];
          arrayOfObject2[0] = ImageLoader.ImageLoadingInfo.access$400(this.imageLoadingInfo);
          Log.i(str2, String.format("Load image from Internet [%s]", arrayOfObject2));
        }
        if (ImageLoader.ImageLoadingInfo.access$500(this.imageLoadingInfo).isCacheOnDisc())
        {
          if (ImageLoader.this.loggingEnabled)
          {
            String str1 = ImageLoader.TAG;
            Object[] arrayOfObject1 = new Object[1];
            arrayOfObject1[0] = ImageLoader.ImageLoadingInfo.access$400(this.imageLoadingInfo);
            Log.i(str1, String.format("Cache image on disc [%s]", arrayOfObject1));
          }
          saveImageOnDisc(localFile);
          ImageLoader.this.configuration.discCache.put(ImageLoader.ImageLoadingInfo.access$700(this.imageLoadingInfo), localFile);
        }
        Bitmap localBitmap1;
        for (URL localURL = localFile.toURL();; localURL = new URL(ImageLoader.ImageLoadingInfo.access$700(this.imageLoadingInfo)))
        {
          localBitmap1 = decodeImage(localURL);
          break;
        }
        boolean bool;
        return localBitmap1;
      }
      catch (IOException localIOException)
      {
        Log.e(ImageLoader.TAG, localIOException.getMessage(), localIOException);
        fireImageLoadingFailedEvent();
        bool = localFile.exists();
        localBitmap1 = null;
        if (bool)
        {
          localFile.delete();
          localBitmap1 = null;
        }
      }
      catch (Throwable localThrowable)
      {
        Log.e(ImageLoader.TAG, localThrowable.getMessage(), localThrowable);
        fireImageLoadingFailedEvent();
        localBitmap1 = null;
      }
    }
    
    /* Error */
    private void saveImageOnDisc(File paramFile)
      throws java.net.MalformedURLException, IOException
    {
      // Byte code:
      //   0: new 177	java/net/URL
      //   3: dup
      //   4: aload_0
      //   5: getfield 19	com/nostra13/universalimageloader/core/ImageLoader$DisplayImageTask:imageLoadingInfo	Lcom/nostra13/universalimageloader/core/ImageLoader$ImageLoadingInfo;
      //   8: invokestatic 121	com/nostra13/universalimageloader/core/ImageLoader$ImageLoadingInfo:access$700	(Lcom/nostra13/universalimageloader/core/ImageLoader$ImageLoadingInfo;)Ljava/lang/String;
      //   11: invokespecial 180	java/net/URL:<init>	(Ljava/lang/String;)V
      //   14: invokevirtual 193	java/net/URL:openConnection	()Ljava/net/URLConnection;
      //   17: astore_2
      //   18: aload_2
      //   19: aload_0
      //   20: getfield 14	com/nostra13/universalimageloader/core/ImageLoader$DisplayImageTask:this$0	Lcom/nostra13/universalimageloader/core/ImageLoader;
      //   23: invokestatic 91	com/nostra13/universalimageloader/core/ImageLoader:access$600	(Lcom/nostra13/universalimageloader/core/ImageLoader;)Lcom/nostra13/universalimageloader/core/ImageLoaderConfiguration;
      //   26: getfield 197	com/nostra13/universalimageloader/core/ImageLoaderConfiguration:httpConnectTimeout	I
      //   29: invokevirtual 203	java/net/URLConnection:setConnectTimeout	(I)V
      //   32: aload_2
      //   33: aload_0
      //   34: getfield 14	com/nostra13/universalimageloader/core/ImageLoader$DisplayImageTask:this$0	Lcom/nostra13/universalimageloader/core/ImageLoader;
      //   37: invokestatic 91	com/nostra13/universalimageloader/core/ImageLoader:access$600	(Lcom/nostra13/universalimageloader/core/ImageLoader;)Lcom/nostra13/universalimageloader/core/ImageLoaderConfiguration;
      //   40: getfield 206	com/nostra13/universalimageloader/core/ImageLoaderConfiguration:httpReadTimeout	I
      //   43: invokevirtual 209	java/net/URLConnection:setReadTimeout	(I)V
      //   46: new 211	java/io/BufferedInputStream
      //   49: dup
      //   50: aload_2
      //   51: invokevirtual 215	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
      //   54: invokespecial 218	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
      //   57: astore_3
      //   58: new 220	java/io/FileOutputStream
      //   61: dup
      //   62: aload_1
      //   63: invokespecial 222	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   66: astore 4
      //   68: aload_3
      //   69: aload 4
      //   71: invokestatic 228	com/nostra13/universalimageloader/utils/FileUtils:copyStream	(Ljava/io/InputStream;Ljava/io/OutputStream;)V
      //   74: aload 4
      //   76: invokevirtual 233	java/io/OutputStream:close	()V
      //   79: aload_3
      //   80: invokevirtual 234	java/io/BufferedInputStream:close	()V
      //   83: return
      //   84: astore 5
      //   86: aload 4
      //   88: invokevirtual 233	java/io/OutputStream:close	()V
      //   91: aload 5
      //   93: athrow
      //   94: astore 6
      //   96: aload_3
      //   97: invokevirtual 234	java/io/BufferedInputStream:close	()V
      //   100: aload 6
      //   102: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	103	0	this	DisplayImageTask
      //   0	103	1	paramFile	File
      //   17	34	2	localURLConnection	java.net.URLConnection
      //   57	40	3	localBufferedInputStream	java.io.BufferedInputStream
      //   66	21	4	localFileOutputStream	java.io.FileOutputStream
      //   84	8	5	localObject1	Object
      //   94	7	6	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   68	74	84	finally
      //   58	68	94	finally
      //   74	79	94	finally
      //   86	94	94	finally
    }
    
    private void tryRunOnUiThread(Runnable paramRunnable)
    {
      Context localContext = ImageLoader.ImageLoadingInfo.access$1100(this.imageLoadingInfo).getContext();
      if ((localContext instanceof Activity))
      {
        ((Activity)localContext).runOnUiThread(paramRunnable);
        return;
      }
      Log.e(ImageLoader.TAG, "ImageView context must be of Activity typeIf you create ImageView in code you must pass your current activity in ImageView constructor (e.g. new ImageView(MyActivity.this); or new ImageView(getActivity())).");
      ImageLoader.ImageLoadingInfo.access$1000(this.imageLoadingInfo).onLoadingFailed();
    }
    
    public void run()
    {
      if (ImageLoader.this.loggingEnabled)
      {
        String str2 = ImageLoader.TAG;
        Object[] arrayOfObject2 = new Object[1];
        arrayOfObject2[0] = ImageLoader.ImageLoadingInfo.access$400(this.imageLoadingInfo);
        Log.i(str2, String.format("Start display image task [%s]", arrayOfObject2));
      }
      if (!this.imageLoadingInfo.isConsistent()) {}
      Bitmap localBitmap;
      do
      {
        return;
        localBitmap = loadBitmap();
      } while ((localBitmap == null) || (!this.imageLoadingInfo.isConsistent()));
      if (ImageLoader.ImageLoadingInfo.access$500(this.imageLoadingInfo).isCacheInMemory())
      {
        if (ImageLoader.this.loggingEnabled)
        {
          String str1 = ImageLoader.TAG;
          Object[] arrayOfObject1 = new Object[1];
          arrayOfObject1[0] = ImageLoader.ImageLoadingInfo.access$400(this.imageLoadingInfo);
          Log.i(str1, String.format("Cache image in memory [%s]", arrayOfObject1));
        }
        ImageLoader.this.configuration.memoryCache.put(ImageLoader.ImageLoadingInfo.access$400(this.imageLoadingInfo), localBitmap);
      }
      tryRunOnUiThread(new ImageLoader.DisplayBitmapTask(ImageLoader.this, this.imageLoadingInfo, localBitmap));
    }
  }
  
  private class EmptyListener
    implements ImageLoadingListener
  {
    private EmptyListener() {}
    
    public void onLoadingComplete() {}
    
    public void onLoadingFailed() {}
    
    public void onLoadingStarted() {}
  }
  
  private final class ImageLoadingInfo
  {
    private final ImageView imageView;
    private final ImageLoadingListener listener;
    private final String memoryCacheKey;
    private final DisplayImageOptions options;
    private final ImageSize targetSize;
    private final String url;
    
    public ImageLoadingInfo(String paramString, ImageView paramImageView, ImageSize paramImageSize, DisplayImageOptions paramDisplayImageOptions, ImageLoadingListener paramImageLoadingListener)
    {
      this.url = paramString;
      this.imageView = paramImageView;
      this.targetSize = paramImageSize;
      this.options = paramDisplayImageOptions;
      this.listener = paramImageLoadingListener;
      this.memoryCacheKey = MemoryCacheKeyUtil.generateKey(paramString, paramImageSize);
    }
    
    boolean isConsistent()
    {
      String str = (String)ImageLoader.this.cacheKeyForImageView.get(this.imageView);
      return this.memoryCacheKey.equals(str);
    }
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.core.ImageLoader
 * JD-Core Version:    0.7.0.1
 */
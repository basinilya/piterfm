package com.nostra13.universalimageloader.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.FuzzyKeyMemoryCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.util.concurrent.ThreadFactory;

public final class ImageLoaderConfiguration
{
  final DisplayImageOptions defaultDisplayImageOptions;
  final DiscCacheAware discCache;
  final ThreadFactory displayImageThreadFactory;
  final int httpConnectTimeout;
  final int httpReadTimeout;
  final int maxImageHeightForMemoryCache;
  final int maxImageWidthForMemoryCache;
  final MemoryCacheAware<String, Bitmap> memoryCache;
  final int threadPoolSize;
  
  private ImageLoaderConfiguration(final Builder paramBuilder)
  {
    this.maxImageWidthForMemoryCache = paramBuilder.maxImageWidthForMemoryCache;
    this.maxImageHeightForMemoryCache = paramBuilder.maxImageHeightForMemoryCache;
    this.httpConnectTimeout = paramBuilder.httpConnectTimeout;
    this.httpReadTimeout = paramBuilder.httpReadTimeout;
    this.threadPoolSize = paramBuilder.threadPoolSize;
    this.discCache = paramBuilder.discCache;
    this.memoryCache = paramBuilder.memoryCache;
    this.defaultDisplayImageOptions = paramBuilder.defaultDisplayImageOptions;
    this.displayImageThreadFactory = new ThreadFactory()
    {
      public Thread newThread(Runnable paramAnonymousRunnable)
      {
        Thread localThread = new Thread(paramAnonymousRunnable);
        localThread.setPriority(ImageLoaderConfiguration.Builder.access$800(paramBuilder));
        return localThread;
      }
    };
  }
  
  public static ImageLoaderConfiguration createDefault(Context paramContext)
  {
    return new Builder(paramContext).build();
  }
  
  public static class Builder
  {
    public static final int DEFAULT_HTTP_CONNECTION_TIMEOUT = 5000;
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20000;
    public static final int DEFAULT_MEMORY_CACHE_SIZE = 2000000;
    public static final int DEFAULT_THREAD_POOL_SIZE = 5;
    public static final int DEFAULT_THREAD_PRIORITY = 4;
    private boolean allowCacheImageMultipleSizesInMemory = true;
    private Context context;
    private DisplayImageOptions defaultDisplayImageOptions = null;
    private DiscCacheAware discCache = null;
    private int httpConnectTimeout = 5000;
    private int httpReadTimeout = 20000;
    private int maxImageHeightForMemoryCache = 0;
    private int maxImageWidthForMemoryCache = 0;
    private MemoryCacheAware<String, Bitmap> memoryCache = null;
    private int threadPoolSize = 5;
    private int threadPriority = 4;
    
    public Builder(Context paramContext)
    {
      this.context = paramContext;
    }
    
    private void initEmptyFiledsWithDefaultValues()
    {
      if (this.discCache == null) {
        this.discCache = new UnlimitedDiscCache(StorageUtils.getCacheDirectory(this.context));
      }
      if (this.memoryCache == null) {
        this.memoryCache = new UsingFreqLimitedMemoryCache(2000000);
      }
      if (!this.allowCacheImageMultipleSizesInMemory) {
        this.memoryCache = new FuzzyKeyMemoryCache(this.memoryCache, MemoryCacheKeyUtil.createFuzzyKeyComparator());
      }
      if (this.defaultDisplayImageOptions == null) {
        this.defaultDisplayImageOptions = DisplayImageOptions.createSimple();
      }
      DisplayMetrics localDisplayMetrics = this.context.getResources().getDisplayMetrics();
      if (this.maxImageWidthForMemoryCache == 0) {
        this.maxImageWidthForMemoryCache = localDisplayMetrics.widthPixels;
      }
      if (this.maxImageHeightForMemoryCache == 0) {
        this.maxImageHeightForMemoryCache = localDisplayMetrics.heightPixels;
      }
    }
    
    public ImageLoaderConfiguration build()
    {
      initEmptyFiledsWithDefaultValues();
      return new ImageLoaderConfiguration(this, null);
    }
    
    public Builder defaultDisplayImageOptions(DisplayImageOptions paramDisplayImageOptions)
    {
      this.defaultDisplayImageOptions = paramDisplayImageOptions;
      return this;
    }
    
    public Builder denyCacheImageMultipleSizesInMemory()
    {
      this.allowCacheImageMultipleSizesInMemory = false;
      return this;
    }
    
    public Builder discCache(DiscCacheAware paramDiscCacheAware)
    {
      this.discCache = paramDiscCacheAware;
      return this;
    }
    
    public Builder discCacheFileCount(int paramInt)
    {
      this.discCache = new FileCountLimitedDiscCache(StorageUtils.getIndividualCacheDirectory(this.context), paramInt);
      return this;
    }
    
    public Builder discCacheSize(int paramInt)
    {
      this.discCache = new TotalSizeLimitedDiscCache(StorageUtils.getIndividualCacheDirectory(this.context), paramInt);
      return this;
    }
    
    public Builder httpConnectTimeout(int paramInt)
    {
      this.httpConnectTimeout = paramInt;
      return this;
    }
    
    public Builder httpReadTimeout(int paramInt)
    {
      this.httpReadTimeout = paramInt;
      return this;
    }
    
    public Builder maxImageHeightForMemoryCache(int paramInt)
    {
      this.maxImageHeightForMemoryCache = paramInt;
      return this;
    }
    
    public Builder maxImageWidthForMemoryCache(int paramInt)
    {
      this.maxImageWidthForMemoryCache = paramInt;
      return this;
    }
    
    public Builder memoryCache(MemoryCacheAware<String, Bitmap> paramMemoryCacheAware)
    {
      this.memoryCache = paramMemoryCacheAware;
      return this;
    }
    
    public Builder memoryCacheSize(int paramInt)
    {
      this.memoryCache = new UsingFreqLimitedMemoryCache(paramInt);
      return this;
    }
    
    public Builder threadPoolSize(int paramInt)
    {
      this.threadPoolSize = paramInt;
      return this;
    }
    
    public Builder threadPriority(int paramInt)
    {
      if (paramInt < 1)
      {
        this.threadPriority = 1;
        return this;
      }
      if (paramInt > 10) {
        return this;
      }
      this.threadPriority = paramInt;
      return this;
    }
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.core.ImageLoaderConfiguration
 * JD-Core Version:    0.7.0.1
 */
package com.nostra13.universalimageloader.core;

public final class DisplayImageOptions
{
  private final boolean cacheInMemory;
  private final boolean cacheOnDisc;
  private final DecodingType decodingType;
  private final Integer imageForEmptyUrl;
  private final Integer stubImage;
  
  private DisplayImageOptions(Builder paramBuilder)
  {
    this.stubImage = paramBuilder.stubImage;
    this.imageForEmptyUrl = paramBuilder.imageForEmptyUrl;
    this.cacheInMemory = paramBuilder.cacheInMemory;
    this.cacheOnDisc = paramBuilder.cacheOnDisc;
    this.decodingType = paramBuilder.decodingType;
  }
  
  public static DisplayImageOptions createSimple()
  {
    return new Builder().build();
  }
  
  DecodingType getDecodingType()
  {
    return this.decodingType;
  }
  
  Integer getImageForEmptyUrl()
  {
    return this.imageForEmptyUrl;
  }
  
  Integer getStubImage()
  {
    return this.stubImage;
  }
  
  boolean isCacheInMemory()
  {
    return this.cacheInMemory;
  }
  
  boolean isCacheOnDisc()
  {
    return this.cacheOnDisc;
  }
  
  boolean isShowImageForEmptyUrl()
  {
    return this.imageForEmptyUrl != null;
  }
  
  boolean isShowStubImage()
  {
    return this.stubImage != null;
  }
  
  public static class Builder
  {
    private boolean cacheInMemory = false;
    private boolean cacheOnDisc = false;
    private DecodingType decodingType = DecodingType.FAST;
    private Integer imageForEmptyUrl = null;
    private Integer stubImage = null;
    
    public DisplayImageOptions build()
    {
      return new DisplayImageOptions(this, null);
    }
    
    public Builder cacheInMemory()
    {
      this.cacheInMemory = true;
      return this;
    }
    
    public Builder cacheOnDisc()
    {
      this.cacheOnDisc = true;
      return this;
    }
    
    public Builder decodingType(DecodingType paramDecodingType)
    {
      this.decodingType = paramDecodingType;
      return this;
    }
    
    public Builder showImageForEmptyUrl(int paramInt)
    {
      this.imageForEmptyUrl = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder showStubImage(int paramInt)
    {
      this.stubImage = Integer.valueOf(paramInt);
      return this;
    }
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.core.DisplayImageOptions
 * JD-Core Version:    0.7.0.1
 */
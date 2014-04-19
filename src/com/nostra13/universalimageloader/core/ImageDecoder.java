package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

final class ImageDecoder
{
  private DecodingType decodingType;
  private URL imageUrl;
  private ImageSize targetSize;
  
  ImageDecoder(URL paramURL, ImageSize paramImageSize, DecodingType paramDecodingType)
  {
    this.imageUrl = paramURL;
    this.targetSize = paramImageSize;
    this.decodingType = paramDecodingType;
  }
  
  private int computeImageScale(InputStream paramInputStream)
  {
    int i = this.targetSize.width;
    int j = this.targetSize.height;
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(paramInputStream, null, localOptions);
    int k = 1;
    int n;
    int i1;
    switch (1.$SwitchMap$com$nostra13$universalimageloader$core$DecodingType[this.decodingType.ordinal()])
    {
    default: 
      n = localOptions.outWidth;
      i1 = localOptions.outHeight;
      if ((n / 2 >= i) && (i1 / 2 >= j)) {
        break;
      }
    }
    int m;
    do
    {
      return k;
      n /= 2;
      i1 /= 2;
      k *= 2;
      break;
      m = Math.min((int)Math.floor(localOptions.outWidth / i), (int)Math.floor(localOptions.outHeight / j));
    } while (m <= 1);
    return m;
  }
  
  private BitmapFactory.Options getBitmapOptionsForImageDecoding()
    throws IOException
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    InputStream localInputStream = this.imageUrl.openStream();
    try
    {
      localOptions.inSampleSize = computeImageScale(localInputStream);
      return localOptions;
    }
    finally
    {
      localInputStream.close();
    }
  }
  
  public Bitmap decodeFile()
    throws IOException
  {
    BitmapFactory.Options localOptions = getBitmapOptionsForImageDecoding();
    InputStream localInputStream = this.imageUrl.openStream();
    try
    {
      Bitmap localBitmap = BitmapFactory.decodeStream(localInputStream, null, localOptions);
      return localBitmap;
    }
    finally
    {
      localInputStream.close();
    }
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.core.ImageDecoder
 * JD-Core Version:    0.7.0.1
 */
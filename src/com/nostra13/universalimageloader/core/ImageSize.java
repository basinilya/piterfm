package com.nostra13.universalimageloader.core;

class ImageSize
{
  private static final String TO_STRING_PATTERN = "%sx%s";
  int height;
  int width;
  
  public ImageSize(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public String toString()
  {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Integer.valueOf(this.width);
    arrayOfObject[1] = Integer.valueOf(this.height);
    return String.format("%sx%s", arrayOfObject);
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.core.ImageSize
 * JD-Core Version:    0.7.0.1
 */
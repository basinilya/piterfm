package com.nostra13.universalimageloader.core;

public enum DecodingType
{
  static
  {
    DecodingType[] arrayOfDecodingType = new DecodingType[2];
    arrayOfDecodingType[0] = FAST;
    arrayOfDecodingType[1] = MEMORY_SAVING;
    $VALUES = arrayOfDecodingType;
  }
  
  private DecodingType() {}
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.core.DecodingType
 * JD-Core Version:    0.7.0.1
 */
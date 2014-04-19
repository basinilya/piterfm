package com.nostra13.universalimageloader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtils
{
  public static void copyStream(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[1024];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte, 0, 1024);
      if (i == -1) {
        return;
      }
      paramOutputStream.write(arrayOfByte, 0, i);
    }
  }
}


/* Location:
 * Qualified Name:     com.nostra13.universalimageloader.utils.FileUtils
 * JD-Core Version:    0.7.0.1
 */
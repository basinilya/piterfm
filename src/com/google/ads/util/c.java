package com.google.ads.util;

import java.io.UnsupportedEncodingException;

public class c
{
  static
  {
    if (!c.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      a = bool;
      return;
    }
  }
  
  public static byte[] a(String paramString)
  {
    return a(paramString.getBytes(), 0);
  }
  
  public static byte[] a(byte[] paramArrayOfByte, int paramInt)
  {
    return a(paramArrayOfByte, 0, paramArrayOfByte.length, paramInt);
  }
  
  public static byte[] a(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    b localb = new b(paramInt3, new byte[paramInt2 * 3 / 4]);
    if (!localb.a(paramArrayOfByte, paramInt1, paramInt2, true)) {
      throw new IllegalArgumentException("bad base-64");
    }
    if (localb.b == localb.a.length) {
      return localb.a;
    }
    byte[] arrayOfByte = new byte[localb.b];
    System.arraycopy(localb.a, 0, arrayOfByte, 0, localb.b);
    return arrayOfByte;
  }
  
  public static String b(byte[] paramArrayOfByte, int paramInt)
  {
    try
    {
      String str = new String(c(paramArrayOfByte, paramInt), "US-ASCII");
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new AssertionError(localUnsupportedEncodingException);
    }
  }
  
  public static byte[] b(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    c localc = new c(paramInt3, null);
    int i = 4 * (paramInt2 / 3);
    int j;
    if (localc.d)
    {
      if (paramInt2 % 3 > 0) {
        i += 4;
      }
      if ((localc.e) && (paramInt2 > 0))
      {
        j = 1 + (paramInt2 - 1) / 57;
        if (!localc.f) {
          break label167;
        }
      }
    }
    label167:
    for (int k = 2;; k = 1)
    {
      i += k * j;
      localc.a = new byte[i];
      localc.a(paramArrayOfByte, paramInt1, paramInt2, true);
      if ((a) || (localc.b == i)) {
        break label173;
      }
      throw new AssertionError();
      switch (paramInt2 % 3)
      {
      case 0: 
      default: 
        break;
      case 1: 
        i += 2;
        break;
      case 2: 
        i += 3;
        break;
      }
    }
    label173:
    return localc.a;
  }
  
  public static byte[] c(byte[] paramArrayOfByte, int paramInt)
  {
    return b(paramArrayOfByte, 0, paramArrayOfByte.length, paramInt);
  }
  
  public static abstract class a
  {
    public byte[] a;
    public int b;
  }
  
  public static class b
    extends c.a
  {
    private static final int[] c = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
    private static final int[] d = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
    private int e;
    private int f;
    private final int[] g;
    
    public b(int paramInt, byte[] paramArrayOfByte)
    {
      this.a = paramArrayOfByte;
      if ((paramInt & 0x8) == 0) {}
      for (int[] arrayOfInt = c;; arrayOfInt = d)
      {
        this.g = arrayOfInt;
        this.e = 0;
        this.f = 0;
        return;
      }
    }
    
    public boolean a(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (this.e == 6) {
        return false;
      }
      int i = paramInt2 + paramInt1;
      int j = this.e;
      int k = this.f;
      int m = 0;
      byte[] arrayOfByte = this.a;
      int[] arrayOfInt = this.g;
      int n = paramInt1;
      if (n < i) {
        if (j == 0)
        {
          while (n + 4 <= i)
          {
            k = arrayOfInt[(0xFF & paramArrayOfByte[n])] << 18 | arrayOfInt[(0xFF & paramArrayOfByte[(n + 1)])] << 12 | arrayOfInt[(0xFF & paramArrayOfByte[(n + 2)])] << 6 | arrayOfInt[(0xFF & paramArrayOfByte[(n + 3)])];
            if (k < 0) {
              break;
            }
            arrayOfByte[(m + 2)] = ((byte)k);
            arrayOfByte[(m + 1)] = ((byte)(k >> 8));
            arrayOfByte[m] = ((byte)(k >> 16));
            m += 3;
            n += 4;
          }
          if (n < i) {}
        }
      }
      for (int i1 = k;; i1 = k)
      {
        if (!paramBoolean)
        {
          this.e = j;
          this.f = i1;
          this.b = m;
          return true;
          int i4 = n + 1;
          int i5 = arrayOfInt[(0xFF & paramArrayOfByte[n])];
          switch (j)
          {
          }
          label276:
          label559:
          do
          {
            do
            {
              int i6 = j;
              for (;;)
              {
                j = i6;
                n = i4;
                break;
                if (i5 >= 0)
                {
                  i6 = j + 1;
                  k = i5;
                }
                else
                {
                  if (i5 == -1) {
                    break label276;
                  }
                  this.e = 6;
                  return false;
                  if (i5 >= 0)
                  {
                    k = i5 | k << 6;
                    i6 = j + 1;
                  }
                  else
                  {
                    if (i5 == -1) {
                      break label276;
                    }
                    this.e = 6;
                    return false;
                    if (i5 >= 0)
                    {
                      k = i5 | k << 6;
                      i6 = j + 1;
                    }
                    else if (i5 == -2)
                    {
                      int i7 = m + 1;
                      arrayOfByte[m] = ((byte)(k >> 4));
                      i6 = 4;
                      m = i7;
                    }
                    else
                    {
                      if (i5 == -1) {
                        break label276;
                      }
                      this.e = 6;
                      return false;
                      if (i5 >= 0)
                      {
                        k = i5 | k << 6;
                        arrayOfByte[(m + 2)] = ((byte)k);
                        arrayOfByte[(m + 1)] = ((byte)(k >> 8));
                        arrayOfByte[m] = ((byte)(k >> 16));
                        m += 3;
                        i6 = 0;
                      }
                      else if (i5 == -2)
                      {
                        arrayOfByte[(m + 1)] = ((byte)(k >> 2));
                        arrayOfByte[m] = ((byte)(k >> 10));
                        m += 2;
                        i6 = 5;
                      }
                      else
                      {
                        if (i5 == -1) {
                          break label276;
                        }
                        this.e = 6;
                        return false;
                        if (i5 != -2) {
                          break label559;
                        }
                        i6 = j + 1;
                      }
                    }
                  }
                }
              }
            } while (i5 == -1);
            this.e = 6;
            return false;
          } while (i5 == -1);
          this.e = 6;
          return false;
        }
        switch (j)
        {
        case 0: 
        default: 
        case 1: 
        case 2: 
        case 3: 
          for (;;)
          {
            this.e = j;
            this.b = m;
            return true;
            this.e = 6;
            return false;
            int i3 = m + 1;
            arrayOfByte[m] = ((byte)(i1 >> 4));
            m = i3;
            continue;
            int i2 = m + 1;
            arrayOfByte[m] = ((byte)(i1 >> 10));
            m = i2 + 1;
            arrayOfByte[i2] = ((byte)(i1 >> 2));
          }
        }
        this.e = 6;
        return false;
      }
    }
  }
  
  public static class c
    extends c.a
  {
    private static final byte[] h;
    private static final byte[] i;
    public int c;
    public final boolean d;
    public final boolean e;
    public final boolean f;
    private final byte[] j;
    private int k;
    private final byte[] l;
    
    static
    {
      if (!c.class.desiredAssertionStatus()) {}
      for (boolean bool = true;; bool = false)
      {
        g = bool;
        h = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        i = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
        return;
      }
    }
    
    public c(int paramInt, byte[] paramArrayOfByte)
    {
      this.a = paramArrayOfByte;
      boolean bool2;
      boolean bool3;
      label35:
      label47:
      byte[] arrayOfByte;
      if ((paramInt & 0x1) == 0)
      {
        bool2 = bool1;
        this.d = bool2;
        if ((paramInt & 0x2) != 0) {
          break label106;
        }
        bool3 = bool1;
        this.e = bool3;
        if ((paramInt & 0x4) == 0) {
          break label112;
        }
        this.f = bool1;
        if ((paramInt & 0x8) != 0) {
          break label117;
        }
        arrayOfByte = h;
        label64:
        this.l = arrayOfByte;
        this.j = new byte[2];
        this.c = 0;
        if (!this.e) {
          break label125;
        }
      }
      label106:
      label112:
      label117:
      label125:
      for (int m = 19;; m = -1)
      {
        this.k = m;
        return;
        bool2 = false;
        break;
        bool3 = false;
        break label35;
        bool1 = false;
        break label47;
        arrayOfByte = i;
        break label64;
      }
    }
    
    public boolean a(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      byte[] arrayOfByte1 = this.l;
      byte[] arrayOfByte2 = this.a;
      int m = this.k;
      int n = paramInt2 + paramInt1;
      int i1 = -1;
      int i3;
      label59:
      int i4;
      int i38;
      switch (this.c)
      {
      default: 
        i3 = paramInt1;
        i4 = 0;
        if (i1 != -1)
        {
          arrayOfByte2[0] = arrayOfByte1[(0x3F & i1 >> 18)];
          arrayOfByte2[1] = arrayOfByte1[(0x3F & i1 >> 12)];
          arrayOfByte2[2] = arrayOfByte1[(0x3F & i1 >> 6)];
          i4 = 4;
          arrayOfByte2[3] = arrayOfByte1[(i1 & 0x3F)];
          m--;
          if (m == 0)
          {
            if (!this.f) {
              break label1246;
            }
            i38 = 5;
            arrayOfByte2[i4] = 13;
          }
        }
        break;
      }
      for (;;)
      {
        int i39 = i38 + 1;
        arrayOfByte2[i38] = 10;
        int i5 = 19;
        int i6 = i39;
        for (;;)
        {
          label174:
          int i36;
          if (i3 + 3 <= n)
          {
            int i35 = (0xFF & paramArrayOfByte[i3]) << 16 | (0xFF & paramArrayOfByte[(i3 + 1)]) << 8 | 0xFF & paramArrayOfByte[(i3 + 2)];
            arrayOfByte2[i6] = arrayOfByte1[(0x3F & i35 >> 18)];
            arrayOfByte2[(i6 + 1)] = arrayOfByte1[(0x3F & i35 >> 12)];
            arrayOfByte2[(i6 + 2)] = arrayOfByte1[(0x3F & i35 >> 6)];
            arrayOfByte2[(i6 + 3)] = arrayOfByte1[(i35 & 0x3F)];
            i3 += 3;
            i4 = i6 + 4;
            m = i5 - 1;
            if (m != 0) {
              break label1235;
            }
            if (!this.f) {
              break label1228;
            }
            i36 = i4 + 1;
            arrayOfByte2[i4] = 13;
          }
          for (;;)
          {
            int i37 = i36 + 1;
            arrayOfByte2[i36] = 10;
            i5 = 19;
            i6 = i37;
            break label174;
            i3 = paramInt1;
            break label59;
            if (paramInt1 + 2 > n) {
              break;
            }
            int i40 = (0xFF & this.j[0]) << 16;
            int i41 = paramInt1 + 1;
            int i42 = i40 | (0xFF & paramArrayOfByte[paramInt1]) << 8;
            int i43 = i41 + 1;
            i1 = i42 | 0xFF & paramArrayOfByte[i41];
            this.c = 0;
            i3 = i43;
            break label59;
            if (paramInt1 + 1 > n) {
              break;
            }
            int i2 = (0xFF & this.j[0]) << 16 | (0xFF & this.j[1]) << 8;
            i3 = paramInt1 + 1;
            i1 = i2 | 0xFF & paramArrayOfByte[paramInt1];
            this.c = 0;
            break label59;
            int i13;
            int i12;
            label770:
            int i16;
            label811:
            int i20;
            int i21;
            if (paramBoolean)
            {
              if (i3 - this.c == n - 1)
              {
                int i28;
                int i26;
                int i27;
                if (this.c > 0)
                {
                  byte[] arrayOfByte8 = this.j;
                  i28 = 1;
                  i26 = arrayOfByte8[0];
                  i27 = i3;
                }
                for (;;)
                {
                  int i29 = (i26 & 0xFF) << 4;
                  this.c -= i28;
                  int i30 = i6 + 1;
                  arrayOfByte2[i6] = arrayOfByte1[(0x3F & i29 >> 6)];
                  int i31 = i30 + 1;
                  arrayOfByte2[i30] = arrayOfByte1[(i29 & 0x3F)];
                  if (this.d)
                  {
                    int i34 = i31 + 1;
                    arrayOfByte2[i31] = 61;
                    i31 = i34 + 1;
                    arrayOfByte2[i34] = 61;
                  }
                  if (this.e)
                  {
                    if (this.f)
                    {
                      int i33 = i31 + 1;
                      arrayOfByte2[i31] = 13;
                      i31 = i33;
                    }
                    int i32 = i31 + 1;
                    arrayOfByte2[i31] = 10;
                    i31 = i32;
                  }
                  i3 = i27;
                  i6 = i31;
                  if ((g) || (this.c == 0)) {
                    break;
                  }
                  throw new AssertionError();
                  int i25 = i3 + 1;
                  i26 = paramArrayOfByte[i3];
                  i27 = i25;
                  i28 = 0;
                }
              }
              if (i3 - this.c == n - 2) {
                if (this.c > 1)
                {
                  byte[] arrayOfByte7 = this.j;
                  i13 = 1;
                  i12 = arrayOfByte7[0];
                  int i14 = (i12 & 0xFF) << 10;
                  if (this.c <= 0) {
                    break label995;
                  }
                  byte[] arrayOfByte6 = this.j;
                  int i24 = i13 + 1;
                  i16 = arrayOfByte6[i13];
                  i13 = i24;
                  int i17 = i14 | (i16 & 0xFF) << 2;
                  this.c -= i13;
                  int i18 = i6 + 1;
                  arrayOfByte2[i6] = arrayOfByte1[(0x3F & i17 >> 12)];
                  int i19 = i18 + 1;
                  arrayOfByte2[i18] = arrayOfByte1[(0x3F & i17 >> 6)];
                  i20 = i19 + 1;
                  arrayOfByte2[i19] = arrayOfByte1[(i17 & 0x3F)];
                  if (!this.d) {
                    break label1221;
                  }
                  i21 = i20 + 1;
                  arrayOfByte2[i20] = 61;
                }
              }
            }
            for (;;)
            {
              if (this.e)
              {
                if (this.f)
                {
                  int i23 = i21 + 1;
                  arrayOfByte2[i21] = 13;
                  i21 = i23;
                }
                int i22 = i21 + 1;
                arrayOfByte2[i21] = 10;
                i21 = i22;
              }
              i6 = i21;
              break;
              int i11 = i3 + 1;
              i12 = paramArrayOfByte[i3];
              i3 = i11;
              i13 = 0;
              break label770;
              label995:
              int i15 = i3 + 1;
              i16 = paramArrayOfByte[i3];
              i3 = i15;
              break label811;
              if ((!this.e) || (i6 <= 0) || (i5 == 19)) {
                break;
              }
              int i10;
              if (this.f)
              {
                i10 = i6 + 1;
                arrayOfByte2[i6] = 13;
              }
              for (;;)
              {
                i6 = i10 + 1;
                arrayOfByte2[i10] = 10;
                break;
                if ((!g) && (i3 != n))
                {
                  throw new AssertionError();
                  if (i3 != n - 1) {
                    break label1142;
                  }
                  byte[] arrayOfByte5 = this.j;
                  int i9 = this.c;
                  this.c = (i9 + 1);
                  arrayOfByte5[i9] = paramArrayOfByte[i3];
                }
                for (;;)
                {
                  this.b = i6;
                  this.k = i5;
                  return true;
                  label1142:
                  if (i3 == n - 2)
                  {
                    byte[] arrayOfByte3 = this.j;
                    int i7 = this.c;
                    this.c = (i7 + 1);
                    arrayOfByte3[i7] = paramArrayOfByte[i3];
                    byte[] arrayOfByte4 = this.j;
                    int i8 = this.c;
                    this.c = (i8 + 1);
                    arrayOfByte4[i8] = paramArrayOfByte[(i3 + 1)];
                  }
                }
                i10 = i6;
              }
              label1221:
              i21 = i20;
            }
            label1228:
            i36 = i4;
          }
          label1235:
          i5 = m;
          i6 = i4;
        }
        label1246:
        i38 = i4;
      }
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.util.c
 * JD-Core Version:    0.7.0.1
 */
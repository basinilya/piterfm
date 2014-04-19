package com.google.ads;

import android.os.SystemClock;
import java.util.concurrent.TimeUnit;

public class d
{
  private c a = null;
  private long b = -1L;
  
  public void a(c paramc, int paramInt)
  {
    this.a = paramc;
    this.b = (TimeUnit.MILLISECONDS.convert(paramInt, TimeUnit.SECONDS) + SystemClock.elapsedRealtime());
  }
  
  public boolean a()
  {
    return (this.a != null) && (SystemClock.elapsedRealtime() < this.b);
  }
  
  public c b()
  {
    return this.a;
  }
}


/* Location:
 * Qualified Name:     com.google.ads.d
 * JD-Core Version:    0.7.0.1
 */
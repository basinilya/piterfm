package com.google.ads.internal;

import android.os.SystemClock;
import com.google.ads.g.a;
import com.google.ads.util.b;
import java.util.Iterator;
import java.util.LinkedList;

public class g
{
  private static long f = 0L;
  private static long g = 0L;
  private static long i = -1L;
  private LinkedList<Long> a = new LinkedList();
  private long b;
  private long c;
  private long d;
  private LinkedList<Long> e = new LinkedList();
  private String h;
  private boolean j = false;
  private boolean k = false;
  private String l;
  private long m;
  private LinkedList<Long> n = new LinkedList();
  private LinkedList<g.a> o = new LinkedList();
  
  protected g()
  {
    a();
  }
  
  public static long y()
  {
    if (i == -1L)
    {
      i = SystemClock.elapsedRealtime();
      return 0L;
    }
    return SystemClock.elapsedRealtime() - i;
  }
  
  protected void a()
  {
    try
    {
      this.a.clear();
      this.b = 0L;
      this.c = 0L;
      this.d = 0L;
      this.e.clear();
      this.m = -1L;
      this.n.clear();
      this.o.clear();
      this.h = null;
      this.j = false;
      this.k = false;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void a(g.a parama)
  {
    try
    {
      this.n.add(Long.valueOf(SystemClock.elapsedRealtime() - this.m));
      this.o.add(parama);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void a(String paramString)
  {
    b.d("Prior ad identifier = " + paramString);
    this.h = paramString;
  }
  
  public void b()
  {
    try
    {
      this.n.clear();
      this.o.clear();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void b(String paramString)
  {
    b.d("Prior impression ticket = " + paramString);
    this.l = paramString;
  }
  
  public void c()
  {
    try
    {
      this.m = SystemClock.elapsedRealtime();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public String d()
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator = this.n.iterator();
      while (localIterator.hasNext())
      {
        long l1 = ((Long)localIterator.next()).longValue();
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(",");
        }
        localStringBuilder.append(l1);
      }
      str = localStringBuilder.toString();
    }
    finally {}
    String str;
    return str;
  }
  
  public String e()
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator = this.o.iterator();
      while (localIterator.hasNext())
      {
        g.a locala = (g.a)localIterator.next();
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(",");
        }
        localStringBuilder.append(locala.ordinal());
      }
      str = localStringBuilder.toString();
    }
    finally {}
    String str;
    return str;
  }
  
  protected void f()
  {
    b.d("Ad clicked.");
    this.a.add(Long.valueOf(SystemClock.elapsedRealtime()));
  }
  
  protected void g()
  {
    b.d("Ad request loaded.");
    this.b = SystemClock.elapsedRealtime();
  }
  
  protected void h()
  {
    try
    {
      b.d("Ad request before rendering.");
      this.c = SystemClock.elapsedRealtime();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void i()
  {
    b.d("Ad request started.");
    this.d = SystemClock.elapsedRealtime();
    f = 1L + f;
  }
  
  protected long j()
  {
    if (this.a.size() != this.e.size()) {
      return -1L;
    }
    return this.a.size();
  }
  
  protected String k()
  {
    if ((this.a.isEmpty()) || (this.a.size() != this.e.size())) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i1 = 0; i1 < this.a.size(); i1++)
    {
      if (i1 != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(Long.toString(((Long)this.e.get(i1)).longValue() - ((Long)this.a.get(i1)).longValue()));
    }
    return localStringBuilder.toString();
  }
  
  protected String l()
  {
    if (this.a.isEmpty()) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i1 = 0; i1 < this.a.size(); i1++)
    {
      if (i1 != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(Long.toString(((Long)this.a.get(i1)).longValue() - this.b));
    }
    return localStringBuilder.toString();
  }
  
  protected long m()
  {
    return this.b - this.d;
  }
  
  protected long n()
  {
    try
    {
      long l1 = this.c;
      long l2 = this.d;
      long l3 = l1 - l2;
      return l3;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected long o()
  {
    return f;
  }
  
  protected long p()
  {
    return g;
  }
  
  protected void q()
  {
    b.d("Ad request network error");
    g = 1L + g;
  }
  
  protected String r()
  {
    return this.h;
  }
  
  protected boolean s()
  {
    return this.j;
  }
  
  protected void t()
  {
    b.d("Interstitial network error.");
    this.j = true;
  }
  
  protected boolean u()
  {
    return this.k;
  }
  
  protected void v()
  {
    b.d("Interstitial no fill.");
    this.k = true;
  }
  
  public void w()
  {
    b.d("Landing page dismissed.");
    this.e.add(Long.valueOf(SystemClock.elapsedRealtime()));
  }
  
  protected String x()
  {
    return this.l;
  }
}


/* Location:
 * Qualified Name:     com.google.ads.internal.g
 * JD-Core Version:    0.7.0.1
 */
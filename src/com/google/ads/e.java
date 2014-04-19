package com.google.ads;

import android.app.Activity;
import android.os.SystemClock;
import android.view.View;
import com.google.ads.internal.d;
import com.google.ads.internal.g;
import com.google.ads.util.b;
import com.google.ads.util.i.b;
import com.google.ads.util.i.d;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class e
{
  private final d a;
  private h b = null;
  private Object c = new Object();
  private Thread d = null;
  private Object e = new Object();
  private boolean f = false;
  private Object g = new Object();
  
  protected e()
  {
    this.a = null;
  }
  
  public e(d paramd)
  {
    com.google.ads.util.a.b(paramd);
    this.a = paramd;
  }
  
  public static boolean a(c paramc, d paramd)
  {
    if (paramc.j() == null) {
      return true;
    }
    if (paramd.h().b())
    {
      if (!paramc.j().a())
      {
        b.e("InterstitialAd received a mediation response corresponding to a non-interstitial ad. Make sure you specify 'interstitial' as the ad-type in the mediation UI.");
        return false;
      }
      return true;
    }
    AdSize localAdSize1 = ((com.google.ads.internal.h)paramd.h().i.a()).b();
    if (paramc.j().a())
    {
      b.e("AdView received a mediation response corresponding to an interstitial ad. Make sure you specify the banner ad size corresponding to the AdSize you used in your AdView  (" + localAdSize1 + ") in the ad-type field in the mediation UI.");
      return false;
    }
    AdSize localAdSize2 = paramc.j().b();
    if (localAdSize2 != localAdSize1)
    {
      b.e("Mediation server returned ad size: '" + localAdSize2 + "', while the AdView was created with ad size: '" + localAdSize1 + "'. Using the ad-size passed to the AdView on creation.");
      return false;
    }
    return true;
  }
  
  private boolean a(h paramh, String paramString)
  {
    if (e() != paramh)
    {
      b.c("GWController: ignoring callback to " + paramString + " from non showing ambassador with adapter class: '" + paramh.h() + "'.");
      return false;
    }
    return true;
  }
  
  private boolean a(String paramString, Activity paramActivity, AdRequest paramAdRequest, final f paramf, HashMap<String, String> paramHashMap, long paramLong)
  {
    synchronized (new h(this, (com.google.ads.internal.h)this.a.h().i.a(), paramf, paramString, paramAdRequest, paramHashMap))
    {
      ???.a(paramActivity);
      try
      {
        while ((!???.c()) && (paramLong > 0L))
        {
          long l1 = SystemClock.elapsedRealtime();
          ???.wait(paramLong);
          long l2 = SystemClock.elapsedRealtime();
          paramLong -= l2 - l1;
        }
        final View localView;
        localObject = finally;
      }
      catch (InterruptedException localInterruptedException)
      {
        b.a("Interrupted while waiting for ad network to load ad using adapter class: " + paramString);
        this.a.m().a(???.e());
        if ((???.c()) && (???.d()))
        {
          if (this.a.h().b()) {}
          for (localView = null;; localView = ???.f())
          {
            this.a.a(new Runnable()
            {
              public void run()
              {
                if (e.a(e.this, localh))
                {
                  b.a("Trying to switch GWAdNetworkAmbassadors, but GWController().destroy() has been called. Destroying the new ambassador and terminating mediation.");
                  return;
                }
                e.b(e.this).a(localView, localh, paramf, false);
              }
            });
            return true;
          }
        }
        ???.b();
        return false;
      }
    }
  }
  
  private void b(final c paramc, AdRequest paramAdRequest)
  {
    do
    {
      long l;
      HashMap localHashMap;
      f localf;
      String str;
      Activity localActivity;
      do
      {
        for (;;)
        {
          synchronized (this.e)
          {
            com.google.ads.util.a.a(Thread.currentThread(), this.d);
            List localList1 = paramc.f();
            if (paramc.a())
            {
              l = paramc.b();
              Iterator localIterator1 = localList1.iterator();
              if (!localIterator1.hasNext()) {
                break label262;
              }
              a locala = (a)localIterator1.next();
              b.a("Looking to fetch ads from network: " + locala.b());
              List localList2 = locala.c();
              localHashMap = locala.d();
              localf = new f(locala.a(), locala.b(), paramc.c(), paramc.g(), paramc.h(), paramc.i());
              Iterator localIterator2 = localList2.iterator();
              if (!localIterator2.hasNext()) {
                continue;
              }
              str = (String)localIterator2.next();
              localActivity = (Activity)this.a.h().c.a();
              if (localActivity != null) {
                break;
              }
              b.a("Activity is null while mediating.  Terminating mediation thread.");
              return;
            }
          }
          l = 10000L;
        }
        this.a.m().c();
      } while (a(str, localActivity, paramAdRequest, localf, localHashMap, l));
    } while (!d());
    b.a("GWController.destroy() called. Terminating mediation thread.");
    return;
    label262:
    this.a.a(new Runnable()
    {
      public void run()
      {
        e.b(e.this).b(paramc);
      }
    });
  }
  
  private boolean d()
  {
    synchronized (this.g)
    {
      boolean bool = this.f;
      return bool;
    }
  }
  
  private h e()
  {
    synchronized (this.c)
    {
      h localh = this.b;
      return localh;
    }
  }
  
  private boolean e(h paramh)
  {
    synchronized (this.g)
    {
      if (d())
      {
        paramh.b();
        return true;
      }
      return false;
    }
  }
  
  public void a(final c paramc, final AdRequest paramAdRequest)
  {
    synchronized (this.e)
    {
      if (a())
      {
        b.c("Mediation thread is not done executing previous mediation  request. Ignoring new mediation request");
        return;
      }
      a(paramc, this.a);
      this.d = new Thread(new Runnable()
      {
        public void run()
        {
          e.a(e.this, paramc, paramAdRequest);
          synchronized (e.a(e.this))
          {
            e.a(e.this, null);
            return;
          }
        }
      });
      this.d.start();
      return;
    }
  }
  
  public void a(h paramh)
  {
    if (!a(paramh, "onPresentScreen")) {
      return;
    }
    this.a.a(new Runnable()
    {
      public void run()
      {
        e.b(e.this).u();
      }
    });
  }
  
  public void a(h paramh, final View paramView)
  {
    if (e() != paramh)
    {
      b.c("GWController: ignoring onAdRefreshed() callback from non-showing ambassador (adapter class name is '" + paramh.h() + "').");
      return;
    }
    this.a.m().a(g.a.a);
    final f localf = this.b.a();
    this.a.a(new Runnable()
    {
      public void run()
      {
        e.b(e.this).a(paramView, e.c(e.this), localf, true);
      }
    });
  }
  
  public void a(h paramh, final boolean paramBoolean)
  {
    if (!a(paramh, "onAdClicked()")) {
      return;
    }
    final f localf = paramh.a();
    this.a.a(new Runnable()
    {
      public void run()
      {
        e.b(e.this).a(localf, paramBoolean);
      }
    });
  }
  
  public boolean a()
  {
    for (;;)
    {
      synchronized (this.e)
      {
        if (this.d != null)
        {
          bool = true;
          return bool;
        }
      }
      boolean bool = false;
    }
  }
  
  public void b()
  {
    synchronized (this.g)
    {
      this.f = true;
      d(null);
      synchronized (this.e)
      {
        if (this.d != null) {
          this.d.interrupt();
        }
        return;
      }
    }
  }
  
  public void b(h paramh)
  {
    if (!a(paramh, "onDismissScreen")) {
      return;
    }
    this.a.a(new Runnable()
    {
      public void run()
      {
        e.b(e.this).t();
      }
    });
  }
  
  public void c(h paramh)
  {
    if (!a(paramh, "onLeaveApplication")) {
      return;
    }
    this.a.a(new Runnable()
    {
      public void run()
      {
        e.b(e.this).v();
      }
    });
  }
  
  public boolean c()
  {
    com.google.ads.util.a.a(this.a.h().b());
    h localh = e();
    if (localh != null)
    {
      localh.g();
      return true;
    }
    b.b("There is no ad ready to show.");
    return false;
  }
  
  public void d(h paramh)
  {
    synchronized (this.c)
    {
      if (this.b != paramh)
      {
        if (this.b != null) {
          this.b.b();
        }
        this.b = paramh;
      }
      return;
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.e
 * JD-Core Version:    0.7.0.1
 */
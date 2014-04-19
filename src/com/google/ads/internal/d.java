package com.google.ads.internal;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.google.ads.Ad;
import com.google.ads.AdActivity;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.ads.AppEventListener;
import com.google.ads.ab;
import com.google.ads.ac;
import com.google.ads.af;
import com.google.ads.f;
import com.google.ads.l;
import com.google.ads.l.a;
import com.google.ads.m;
import com.google.ads.util.AdUtil;
import com.google.ads.util.i.b;
import com.google.ads.util.i.c;
import com.google.ads.util.i.d;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class d
{
  private static final Object a = new Object();
  private final m b;
  private c c;
  private AdRequest d;
  private g e;
  private AdWebView f;
  private i g;
  private Handler h;
  private long i;
  private boolean j;
  private boolean k;
  private boolean l;
  private boolean m;
  private boolean n;
  private SharedPreferences o;
  private long p;
  private ac q;
  private boolean r;
  private LinkedList<String> s;
  private LinkedList<String> t;
  private int u = -1;
  private Boolean v;
  private com.google.ads.d w;
  private com.google.ads.e x;
  private f y;
  private String z = null;
  
  public d(m paramm, boolean paramBoolean)
  {
    this.b = paramm;
    this.r = paramBoolean;
    this.e = new g();
    this.c = null;
    this.d = null;
    this.k = false;
    this.h = new Handler();
    this.p = 60000L;
    this.l = false;
    this.n = false;
    this.m = true;
    if ((paramm == null) || (paramm.d.a() == null)) {
      return;
    }
    for (;;)
    {
      synchronized (a)
      {
        this.o = ((Context)paramm.d.a()).getSharedPreferences("GoogleAdMobAdsPrefs", 0);
        if (paramBoolean)
        {
          long l1 = this.o.getLong("Timeout" + paramm.b, -1L);
          if (l1 < 0L)
          {
            this.i = 5000L;
            this.q = new ac(this);
            this.s = new LinkedList();
            this.t = new LinkedList();
            a();
            AdUtil.h((Context)paramm.d.a());
            this.w = new com.google.ads.d();
            this.x = new com.google.ads.e(this);
            this.v = null;
            this.y = null;
            return;
          }
          this.i = l1;
        }
      }
      this.i = 60000L;
    }
  }
  
  private void a(f paramf, Boolean paramBoolean)
  {
    Object localObject = paramf.d();
    if (localObject == null)
    {
      localObject = new ArrayList();
      ((List)localObject).add("http://e.admob.com/imp?ad_loc=@gw_adlocid@&qdata=@gw_qdata@&ad_network_id=@gw_adnetid@&js=@gw_sdkver@&session_id=@gw_sessid@&seq_num=@gw_seqnum@&nr=@gw_adnetrefresh@&adt=@gw_adt@&aec=@gw_aec@");
    }
    String str = paramf.b();
    a((List)localObject, paramf.a(), str, paramf.c(), paramBoolean, this.e.d(), this.e.e());
  }
  
  private void a(List<String> paramList, String paramString)
  {
    Object localObject;
    if (paramList == null)
    {
      localObject = new ArrayList();
      ((List)localObject).add("http://e.admob.com/nofill?ad_loc=@gw_adlocid@&qdata=@gw_qdata@&js=@gw_sdkver@&session_id=@gw_sessid@&seq_num=@gw_seqnum@&adt=@gw_adt@&aec=@gw_aec@");
    }
    for (;;)
    {
      a((List)localObject, null, null, paramString, null, this.e.d(), this.e.e());
      return;
      localObject = paramList;
    }
  }
  
  private void a(List<String> paramList, String paramString1, String paramString2, String paramString3, Boolean paramBoolean, String paramString4, String paramString5)
  {
    String str1 = AdUtil.a((Context)this.b.d.a());
    com.google.ads.b localb = com.google.ads.b.a();
    String str2 = localb.b().toString();
    String str3 = localb.c().toString();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext()) {
      new Thread(new ab(com.google.ads.g.a((String)localIterator.next(), (String)this.b.b.a(), paramBoolean, str1, paramString1, paramString2, paramString3, str2, str3, paramString4, paramString5), (Context)this.b.d.a())).start();
    }
    this.e.b();
  }
  
  private void b(f paramf, Boolean paramBoolean)
  {
    Object localObject = paramf.e();
    if (localObject == null)
    {
      localObject = new ArrayList();
      ((List)localObject).add("http://e.admob.com/clk?ad_loc=@gw_adlocid@&qdata=@gw_qdata@&ad_network_id=@gw_adnetid@&js=@gw_sdkver@&session_id=@gw_sessid@&seq_num=@gw_seqnum@&nr=@gw_adnetrefresh@");
    }
    String str = paramf.b();
    a((List)localObject, paramf.a(), str, paramf.c(), paramBoolean, null, null);
  }
  
  public void A()
  {
    try
    {
      if (this.c != null)
      {
        this.c.a();
        this.c = null;
      }
      if (this.f != null) {
        this.f.stopLoading();
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  protected void B()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 64	com/google/ads/internal/d:b	Lcom/google/ads/m;
    //   6: getfield 280	com/google/ads/m:c	Lcom/google/ads/util/i$d;
    //   9: invokevirtual 283	com/google/ads/util/i$d:a	()Ljava/lang/Object;
    //   12: checkcast 285	android/app/Activity
    //   15: astore_2
    //   16: aload_2
    //   17: ifnonnull +12 -> 29
    //   20: ldc_w 287
    //   23: invokestatic 292	com/google/ads/util/b:e	(Ljava/lang/String;)V
    //   26: aload_0
    //   27: monitorexit
    //   28: return
    //   29: aload_0
    //   30: getfield 156	com/google/ads/internal/d:t	Ljava/util/LinkedList;
    //   33: invokevirtual 293	java/util/LinkedList:iterator	()Ljava/util/Iterator;
    //   36: astore_3
    //   37: aload_3
    //   38: invokeinterface 236 1 0
    //   43: ifeq -17 -> 26
    //   46: new 238	java/lang/Thread
    //   49: dup
    //   50: new 240	com/google/ads/ab
    //   53: dup
    //   54: aload_3
    //   55: invokeinterface 243 1 0
    //   60: checkcast 245	java/lang/String
    //   63: aload_2
    //   64: invokevirtual 297	android/app/Activity:getApplicationContext	()Landroid/content/Context;
    //   67: invokespecial 253	com/google/ads/ab:<init>	(Ljava/lang/String;Landroid/content/Context;)V
    //   70: invokespecial 256	java/lang/Thread:<init>	(Ljava/lang/Runnable;)V
    //   73: invokevirtual 259	java/lang/Thread:start	()V
    //   76: goto -39 -> 37
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	d
    //   79	4	1	localObject	Object
    //   15	49	2	localActivity	Activity
    //   36	19	3	localIterator	Iterator
    // Exception table:
    //   from	to	target	type
    //   2	16	79	finally
    //   20	26	79	finally
    //   29	37	79	finally
    //   37	76	79	finally
  }
  
  protected void C()
  {
    try
    {
      this.c = null;
      this.k = true;
      this.f.setVisibility(0);
      if (this.b.a()) {
        a(this.f);
      }
      this.e.g();
      if (this.b.a()) {
        x();
      }
      com.google.ads.util.b.c("onReceiveAd()");
      AdListener localAdListener = (AdListener)this.b.k.a();
      if (localAdListener != null) {
        localAdListener.onReceiveAd((Ad)this.b.f.a());
      }
      return;
    }
    finally {}
  }
  
  public void a()
  {
    try
    {
      this.f = new AdWebView(this.b, ((h)this.b.i.a()).b());
      this.f.setVisibility(8);
      this.g = i.a(this, a.c, true, this.b.b());
      this.f.setWebViewClient(this.g);
      l.a locala = (l.a)((l)this.b.a.a()).a.a();
      if ((AdUtil.a < ((Integer)locala.a.a()).intValue()) && (!((h)this.b.i.a()).a()))
      {
        com.google.ads.util.b.a("Disabling hardware acceleration for a banner.");
        this.f.b();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void a(float paramFloat)
  {
    try
    {
      long l1 = this.p;
      this.p = ((1000.0F * paramFloat));
      if ((s()) && (this.p != l1))
      {
        e();
        f();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void a(int paramInt)
  {
    try
    {
      this.u = paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void a(long paramLong)
  {
    synchronized (a)
    {
      SharedPreferences.Editor localEditor = this.o.edit();
      localEditor.putLong("Timeout" + this.b.b, paramLong);
      localEditor.commit();
      if (this.r) {
        this.i = paramLong;
      }
      return;
    }
  }
  
  public void a(View paramView)
  {
    ((ViewGroup)this.b.e.a()).removeAllViews();
    ((ViewGroup)this.b.e.a()).addView(paramView);
  }
  
  public void a(View paramView, com.google.ads.h paramh, f paramf, boolean paramBoolean)
  {
    try
    {
      com.google.ads.util.b.a("AdManager.onReceiveGWhirlAd() called.");
      this.k = true;
      this.y = paramf;
      if (this.b.a())
      {
        a(paramView);
        a(paramf, Boolean.valueOf(paramBoolean));
      }
      this.x.d(paramh);
      AdListener localAdListener = (AdListener)this.b.k.a();
      if (localAdListener != null) {
        localAdListener.onReceiveAd((Ad)this.b.f.a());
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  public void a(AdRequest.ErrorCode paramErrorCode)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aconst_null
    //   4: putfield 73	com/google/ads/internal/d:c	Lcom/google/ads/internal/c;
    //   7: aload_1
    //   8: getstatic 435	com/google/ads/AdRequest$ErrorCode:NETWORK_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   11: if_acmpne +28 -> 39
    //   14: aload_0
    //   15: invokevirtual 438	com/google/ads/internal/d:m	()Lcom/google/ads/internal/g;
    //   18: invokevirtual 440	com/google/ads/internal/g:q	()V
    //   21: aload_0
    //   22: ldc_w 441
    //   25: invokevirtual 443	com/google/ads/internal/d:a	(F)V
    //   28: aload_0
    //   29: invokevirtual 386	com/google/ads/internal/d:s	()Z
    //   32: ifne +7 -> 39
    //   35: aload_0
    //   36: invokevirtual 444	com/google/ads/internal/d:g	()V
    //   39: aload_0
    //   40: getfield 64	com/google/ads/internal/d:b	Lcom/google/ads/m;
    //   43: invokevirtual 348	com/google/ads/m:b	()Z
    //   46: ifeq +17 -> 63
    //   49: aload_1
    //   50: getstatic 447	com/google/ads/AdRequest$ErrorCode:NO_FILL	Lcom/google/ads/AdRequest$ErrorCode;
    //   53: if_acmpne +80 -> 133
    //   56: aload_0
    //   57: getfield 71	com/google/ads/internal/d:e	Lcom/google/ads/internal/g;
    //   60: invokevirtual 449	com/google/ads/internal/g:v	()V
    //   63: new 114	java/lang/StringBuilder
    //   66: dup
    //   67: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   70: ldc_w 451
    //   73: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: aload_1
    //   77: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   80: ldc_w 453
    //   83: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: invokevirtual 130	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   89: invokestatic 315	com/google/ads/util/b:c	(Ljava/lang/String;)V
    //   92: aload_0
    //   93: getfield 64	com/google/ads/internal/d:b	Lcom/google/ads/m;
    //   96: getfield 318	com/google/ads/m:k	Lcom/google/ads/util/i$c;
    //   99: invokevirtual 321	com/google/ads/util/i$c:a	()Ljava/lang/Object;
    //   102: checkcast 323	com/google/ads/AdListener
    //   105: astore_3
    //   106: aload_3
    //   107: ifnull +23 -> 130
    //   110: aload_3
    //   111: aload_0
    //   112: getfield 64	com/google/ads/internal/d:b	Lcom/google/ads/m;
    //   115: getfield 325	com/google/ads/m:f	Lcom/google/ads/util/i$b;
    //   118: invokevirtual 102	com/google/ads/util/i$b:a	()Ljava/lang/Object;
    //   121: checkcast 327	com/google/ads/Ad
    //   124: aload_1
    //   125: invokeinterface 457 3 0
    //   130: aload_0
    //   131: monitorexit
    //   132: return
    //   133: aload_1
    //   134: getstatic 435	com/google/ads/AdRequest$ErrorCode:NETWORK_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   137: if_acmpne -74 -> 63
    //   140: aload_0
    //   141: getfield 71	com/google/ads/internal/d:e	Lcom/google/ads/internal/g;
    //   144: invokevirtual 459	com/google/ads/internal/g:t	()V
    //   147: goto -84 -> 63
    //   150: astore_2
    //   151: aload_0
    //   152: monitorexit
    //   153: aload_2
    //   154: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	155	0	this	d
    //   0	155	1	paramErrorCode	AdRequest.ErrorCode
    //   150	4	2	localObject	Object
    //   105	6	3	localAdListener	AdListener
    // Exception table:
    //   from	to	target	type
    //   2	39	150	finally
    //   39	63	150	finally
    //   63	106	150	finally
    //   110	130	150	finally
    //   133	147	150	finally
  }
  
  public void a(AdRequest paramAdRequest)
  {
    for (;;)
    {
      try
      {
        if (p())
        {
          com.google.ads.util.b.e("loadAd called while the ad is already loading, so aborting.");
          return;
        }
        if (AdActivity.isShowing())
        {
          com.google.ads.util.b.e("loadAd called while an interstitial or landing page is displayed, so aborting");
          continue;
        }
        if (!AdUtil.c((Context)this.b.d.a())) {
          continue;
        }
      }
      finally {}
      if (AdUtil.b((Context)this.b.d.a()))
      {
        long l1 = this.o.getLong("GoogleAdMobDoritosLife", 60000L);
        if (af.a((Context)this.b.d.a(), l1)) {
          af.a((Activity)this.b.c.a());
        }
        this.k = false;
        this.s.clear();
        this.d = paramAdRequest;
        if (this.w.a())
        {
          this.x.a(this.w.b(), paramAdRequest);
        }
        else
        {
          this.c = new c(this);
          this.c.a(paramAdRequest);
        }
      }
    }
  }
  
  /* Error */
  public void a(com.google.ads.c paramc)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aconst_null
    //   4: putfield 73	com/google/ads/internal/d:c	Lcom/google/ads/internal/c;
    //   7: aload_1
    //   8: invokevirtual 504	com/google/ads/c:d	()Z
    //   11: ifeq +38 -> 49
    //   14: aload_0
    //   15: aload_1
    //   16: invokevirtual 506	com/google/ads/c:e	()I
    //   19: i2f
    //   20: invokevirtual 443	com/google/ads/internal/d:a	(F)V
    //   23: aload_0
    //   24: getfield 88	com/google/ads/internal/d:l	Z
    //   27: ifne +7 -> 34
    //   30: aload_0
    //   31: invokevirtual 390	com/google/ads/internal/d:f	()V
    //   34: aload_0
    //   35: getfield 173	com/google/ads/internal/d:x	Lcom/google/ads/e;
    //   38: aload_1
    //   39: aload_0
    //   40: getfield 75	com/google/ads/internal/d:d	Lcom/google/ads/AdRequest;
    //   43: invokevirtual 496	com/google/ads/e:a	(Lcom/google/ads/c;Lcom/google/ads/AdRequest;)V
    //   46: aload_0
    //   47: monitorexit
    //   48: return
    //   49: aload_0
    //   50: getfield 88	com/google/ads/internal/d:l	Z
    //   53: ifeq -19 -> 34
    //   56: aload_0
    //   57: invokevirtual 388	com/google/ads/internal/d:e	()V
    //   60: goto -26 -> 34
    //   63: astore_2
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_2
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	d
    //   0	68	1	paramc	com.google.ads.c
    //   63	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	34	63	finally
    //   34	46	63	finally
    //   49	60	63	finally
  }
  
  public void a(f paramf, boolean paramBoolean)
  {
    try
    {
      Locale localLocale = Locale.US;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Boolean.valueOf(paramBoolean);
      com.google.ads.util.b.a(String.format(localLocale, "AdManager.onGWhirlAdClicked(%b) called.", arrayOfObject));
      b(paramf, Boolean.valueOf(paramBoolean));
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void a(Runnable paramRunnable)
  {
    this.h.post(paramRunnable);
  }
  
  public void a(String paramString)
  {
    Uri localUri = new Uri.Builder().encodedQuery(paramString).build();
    StringBuilder localStringBuilder = new StringBuilder();
    HashMap localHashMap = AdUtil.b(localUri);
    Iterator localIterator = localHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localStringBuilder.append(str).append(" = ").append((String)localHashMap.get(str)).append("\n");
    }
    this.z = localStringBuilder.toString().trim();
    if (TextUtils.isEmpty(this.z)) {
      this.z = null;
    }
  }
  
  public void a(String paramString1, String paramString2)
  {
    try
    {
      AppEventListener localAppEventListener = (AppEventListener)this.b.l.a();
      if (localAppEventListener != null) {
        localAppEventListener.onAppEvent(paramString1, paramString2);
      }
      return;
    }
    finally {}
  }
  
  protected void a(LinkedList<String> paramLinkedList)
  {
    try
    {
      Iterator localIterator = paramLinkedList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        com.google.ads.util.b.a("Adding a click tracking URL: " + str);
      }
      this.t = paramLinkedList;
    }
    finally {}
  }
  
  public void a(boolean paramBoolean)
  {
    try
    {
      this.j = paramBoolean;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void b()
  {
    try
    {
      if (this.x != null) {
        this.x.b();
      }
      this.b.k.a(null);
      this.b.l.a(null);
      A();
      if (this.f != null) {
        this.f.destroy();
      }
      return;
    }
    finally {}
  }
  
  public void b(long paramLong)
  {
    if (paramLong > 0L) {}
    try
    {
      this.o.edit().putLong("GoogleAdMobDoritosLife", paramLong).commit();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void b(com.google.ads.c paramc)
  {
    try
    {
      com.google.ads.util.b.a("AdManager.onGWhirlNoFill() called.");
      a(paramc.i(), paramc.c());
      AdListener localAdListener = (AdListener)this.b.k.a();
      if (localAdListener != null) {
        localAdListener.onFailedToReceiveAd((Ad)this.b.f.a(), AdRequest.ErrorCode.NO_FILL);
      }
      return;
    }
    finally {}
  }
  
  protected void b(String paramString)
  {
    try
    {
      com.google.ads.util.b.a("Adding a tracking URL: " + paramString);
      this.s.add(paramString);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void b(boolean paramBoolean)
  {
    this.v = Boolean.valueOf(paramBoolean);
  }
  
  public String c()
  {
    return this.z;
  }
  
  public void d()
  {
    try
    {
      this.m = false;
      com.google.ads.util.b.a("Refreshing is no longer allowed on this AdView.");
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  public void e()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 88	com/google/ads/internal/d:l	Z
    //   6: ifeq +28 -> 34
    //   9: ldc_w 602
    //   12: invokestatic 381	com/google/ads/util/b:a	(Ljava/lang/String;)V
    //   15: aload_0
    //   16: getfield 82	com/google/ads/internal/d:h	Landroid/os/Handler;
    //   19: aload_0
    //   20: getfield 149	com/google/ads/internal/d:q	Lcom/google/ads/ac;
    //   23: invokevirtual 605	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   26: aload_0
    //   27: iconst_0
    //   28: putfield 88	com/google/ads/internal/d:l	Z
    //   31: aload_0
    //   32: monitorexit
    //   33: return
    //   34: ldc_w 607
    //   37: invokestatic 381	com/google/ads/util/b:a	(Ljava/lang/String;)V
    //   40: goto -9 -> 31
    //   43: astore_1
    //   44: aload_0
    //   45: monitorexit
    //   46: aload_1
    //   47: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	d
    //   43	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	31	43	finally
    //   34	40	43	finally
  }
  
  public void f()
  {
    for (;;)
    {
      try
      {
        this.n = false;
        if (!this.b.a()) {
          break label110;
        }
        if (this.m)
        {
          if (!this.l)
          {
            com.google.ads.util.b.a("Enabling refreshing every " + this.p + " milliseconds.");
            this.h.postDelayed(this.q, this.p);
            this.l = true;
            return;
          }
          com.google.ads.util.b.a("Refreshing is already enabled.");
          continue;
        }
        com.google.ads.util.b.a("Refreshing disabled on this AdView");
      }
      finally {}
      continue;
      label110:
      com.google.ads.util.b.a("Tried to enable refreshing on something other than an AdView.");
    }
  }
  
  public void g()
  {
    f();
    this.n = true;
  }
  
  public m h()
  {
    return this.b;
  }
  
  public com.google.ads.d i()
  {
    try
    {
      com.google.ads.d locald = this.w;
      return locald;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public c j()
  {
    try
    {
      c localc = this.c;
      return localc;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public AdWebView k()
  {
    try
    {
      AdWebView localAdWebView = this.f;
      return localAdWebView;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public i l()
  {
    try
    {
      i locali = this.g;
      return locali;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public g m()
  {
    return this.e;
  }
  
  public int n()
  {
    try
    {
      int i1 = this.u;
      return i1;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public long o()
  {
    return this.i;
  }
  
  /* Error */
  public boolean p()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 73	com/google/ads/internal/d:c	Lcom/google/ads/internal/c;
    //   6: astore_2
    //   7: aload_2
    //   8: ifnull +9 -> 17
    //   11: iconst_1
    //   12: istore_3
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_3
    //   16: ireturn
    //   17: iconst_0
    //   18: istore_3
    //   19: goto -6 -> 13
    //   22: astore_1
    //   23: aload_0
    //   24: monitorexit
    //   25: aload_1
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	d
    //   22	4	1	localObject	Object
    //   6	2	2	localc	c
    //   12	7	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	7	22	finally
  }
  
  public boolean q()
  {
    try
    {
      boolean bool = this.j;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public boolean r()
  {
    try
    {
      boolean bool = this.k;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public boolean s()
  {
    try
    {
      boolean bool = this.l;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void t()
  {
    try
    {
      this.e.w();
      com.google.ads.util.b.c("onDismissScreen()");
      AdListener localAdListener = (AdListener)this.b.k.a();
      if (localAdListener != null) {
        localAdListener.onDismissScreen((Ad)this.b.f.a());
      }
      return;
    }
    finally {}
  }
  
  public void u()
  {
    try
    {
      com.google.ads.util.b.c("onPresentScreen()");
      AdListener localAdListener = (AdListener)this.b.k.a();
      if (localAdListener != null) {
        localAdListener.onPresentScreen((Ad)this.b.f.a());
      }
      return;
    }
    finally {}
  }
  
  public void v()
  {
    try
    {
      com.google.ads.util.b.c("onLeaveApplication()");
      AdListener localAdListener = (AdListener)this.b.k.a();
      if (localAdListener != null) {
        localAdListener.onLeaveApplication((Ad)this.b.f.a());
      }
      return;
    }
    finally {}
  }
  
  public void w()
  {
    this.e.f();
    B();
  }
  
  /* Error */
  public void x()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 64	com/google/ads/internal/d:b	Lcom/google/ads/m;
    //   6: getfield 280	com/google/ads/m:c	Lcom/google/ads/util/i$d;
    //   9: invokevirtual 283	com/google/ads/util/i$d:a	()Ljava/lang/Object;
    //   12: checkcast 285	android/app/Activity
    //   15: astore_2
    //   16: aload_2
    //   17: ifnonnull +12 -> 29
    //   20: ldc_w 652
    //   23: invokestatic 292	com/google/ads/util/b:e	(Ljava/lang/String;)V
    //   26: aload_0
    //   27: monitorexit
    //   28: return
    //   29: aload_0
    //   30: getfield 154	com/google/ads/internal/d:s	Ljava/util/LinkedList;
    //   33: invokevirtual 293	java/util/LinkedList:iterator	()Ljava/util/Iterator;
    //   36: astore_3
    //   37: aload_3
    //   38: invokeinterface 236 1 0
    //   43: ifeq -17 -> 26
    //   46: new 238	java/lang/Thread
    //   49: dup
    //   50: new 240	com/google/ads/ab
    //   53: dup
    //   54: aload_3
    //   55: invokeinterface 243 1 0
    //   60: checkcast 245	java/lang/String
    //   63: aload_2
    //   64: invokevirtual 297	android/app/Activity:getApplicationContext	()Landroid/content/Context;
    //   67: invokespecial 253	com/google/ads/ab:<init>	(Ljava/lang/String;Landroid/content/Context;)V
    //   70: invokespecial 256	java/lang/Thread:<init>	(Ljava/lang/Runnable;)V
    //   73: invokevirtual 259	java/lang/Thread:start	()V
    //   76: goto -39 -> 37
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	d
    //   79	4	1	localObject	Object
    //   15	49	2	localActivity	Activity
    //   36	19	3	localIterator	Iterator
    // Exception table:
    //   from	to	target	type
    //   2	16	79	finally
    //   20	26	79	finally
    //   29	37	79	finally
    //   37	76	79	finally
  }
  
  public void y()
  {
    for (;;)
    {
      try
      {
        if (this.d == null) {
          break label114;
        }
        if (!this.b.a()) {
          break label105;
        }
        if ((((AdView)this.b.g.a()).isShown()) && (AdUtil.d()))
        {
          com.google.ads.util.b.c("Refreshing ad.");
          a(this.d);
          if (this.n) {
            e();
          }
        }
        else
        {
          com.google.ads.util.b.a("Not refreshing because the ad is not visible.");
          continue;
        }
        this.h.postDelayed(this.q, this.p);
      }
      finally {}
      continue;
      label105:
      com.google.ads.util.b.a("Tried to refresh an ad that wasn't an AdView.");
      continue;
      label114:
      com.google.ads.util.b.a("Tried to refresh before calling loadAd().");
    }
  }
  
  public void z()
  {
    for (;;)
    {
      try
      {
        com.google.ads.util.a.a(this.b.b());
        if (!this.k) {
          break label101;
        }
        this.k = false;
        if (this.v == null)
        {
          com.google.ads.util.b.b("isMediationFlag is null in show() with isReady() true. we should have an ad and know whether this is a mediation request or not. ");
          return;
        }
        if (this.v.booleanValue())
        {
          if (!this.x.c()) {
            continue;
          }
          a(this.y, Boolean.valueOf(false));
          continue;
        }
        AdActivity.launchAdActivity(this, new e("interstitial"));
      }
      finally {}
      x();
      continue;
      label101:
      com.google.ads.util.b.c("Cannot show interstitial because it is not loaded and ready.");
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.internal.d
 * JD-Core Version:    0.7.0.1
 */
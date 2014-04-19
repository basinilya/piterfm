package com.google.ads.internal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.l;
import com.google.ads.l.a;
import com.google.ads.m;
import com.google.ads.searchads.SearchAdRequest;
import com.google.ads.util.AdUtil;
import com.google.ads.util.AdUtil.a;
import com.google.ads.util.i.b;
import com.google.ads.util.i.c;
import com.google.ads.util.i.d;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;

public class c
  implements Runnable
{
  private String a;
  private String b;
  private String c;
  private String d;
  private boolean e;
  private f f;
  private d g;
  private AdRequest h;
  private WebView i;
  private String j;
  private LinkedList<String> k;
  private String l;
  private AdSize m;
  private volatile boolean n;
  private boolean o;
  private AdRequest.ErrorCode p;
  private boolean q;
  private int r;
  private Thread s;
  private boolean t;
  
  protected c() {}
  
  public c(d paramd)
  {
    this.g = paramd;
    this.j = null;
    this.a = null;
    this.b = null;
    this.c = null;
    this.k = new LinkedList();
    this.p = null;
    this.q = false;
    this.r = -1;
    this.e = false;
    this.o = false;
    this.l = null;
    this.m = null;
    if ((Activity)paramd.h().c.a() != null)
    {
      this.i = new AdWebView(paramd.h(), null);
      this.i.setWebViewClient(i.a(paramd, a.b, false, false));
      this.i.setVisibility(8);
      this.i.setWillNotDraw(true);
      this.f = new f(this, paramd);
      return;
    }
    this.i = null;
    this.f = null;
    com.google.ads.util.b.e("activity was null while trying to create an AdLoader.");
  }
  
  static void a(String paramString, com.google.ads.c paramc, com.google.ads.d paramd)
  {
    if (paramString == null) {}
    while ((paramString.contains("no-store")) || (paramString.contains("no-cache"))) {
      return;
    }
    Matcher localMatcher = Pattern.compile("max-age\\s*=\\s*(\\d+)").matcher(paramString);
    if (localMatcher.find()) {
      try
      {
        int i1 = Integer.parseInt(localMatcher.group(1));
        paramd.a(paramc, i1);
        Locale localLocale = Locale.US;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(i1);
        com.google.ads.util.b.c(String.format(localLocale, "Caching gWhirl configuration for: %d seconds", arrayOfObject));
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        com.google.ads.util.b.b("Caught exception trying to parse cache control directive. Overflow?", localNumberFormatException);
        return;
      }
    }
    com.google.ads.util.b.c("Unrecognized cacheControlDirective: '" + paramString + "'. Not caching configuration.");
  }
  
  private String d()
  {
    if ((this.h instanceof SearchAdRequest)) {
      return "<html><head><script src=\"http://www.gstatic.com/safa/sdk-core-v40.js\"></script><script>";
    }
    return "<html><head><script src=\"http://media.admob.com/sdk-core-v40.js\"></script><script>";
  }
  
  private void e()
  {
    AdWebView localAdWebView = this.g.k();
    this.g.l().c(true);
    this.g.m().h();
    this.g.a(new c(localAdWebView, this.a, this.b));
  }
  
  private void f()
  {
    this.g.a(new e(this.g, this.i, this.k, this.r, this.o, this.l, this.m));
  }
  
  private void f(String paramString)
  {
    this.g.a(new c(this.i, null, paramString));
  }
  
  public String a(Map<String, Object> paramMap, Activity paramActivity)
    throws c.b, c.d
  {
    int i1 = 0;
    Context localContext = paramActivity.getApplicationContext();
    g localg = this.g.m();
    long l1 = localg.m();
    if (l1 > 0L) {
      paramMap.put("prl", Long.valueOf(l1));
    }
    long l2 = localg.n();
    if (l2 > 0L) {
      paramMap.put("prnl", Long.valueOf(l2));
    }
    String str1 = localg.l();
    if (str1 != null) {
      paramMap.put("ppcl", str1);
    }
    String str2 = localg.k();
    if (str2 != null) {
      paramMap.put("pcl", str2);
    }
    long l3 = localg.j();
    if (l3 > 0L) {
      paramMap.put("pcc", Long.valueOf(l3));
    }
    paramMap.put("preqs", Long.valueOf(localg.o()));
    paramMap.put("toar", Long.valueOf(localg.p()));
    String str3 = localg.r();
    if (str3 != null) {
      paramMap.put("pai", str3);
    }
    if (localg.s()) {
      paramMap.put("aoi_timeout", "true");
    }
    if (localg.u()) {
      paramMap.put("aoi_nofill", "true");
    }
    String str4 = localg.x();
    if (str4 != null) {
      paramMap.put("pit", str4);
    }
    paramMap.put("ptime", Long.valueOf(g.y()));
    localg.a();
    localg.i();
    if (this.g.h().b()) {
      paramMap.put("format", "interstitial_mb");
    }
    String str7;
    for (;;)
    {
      paramMap.put("slotname", this.g.h().b.a());
      paramMap.put("js", "afma-sdk-a-v6.1.0");
      String str5 = localContext.getPackageName();
      try
      {
        PackageInfo localPackageInfo = localContext.getPackageManager().getPackageInfo(str5, 0);
        int i2 = localPackageInfo.versionCode;
        String str6 = AdUtil.f(localContext);
        if (!TextUtils.isEmpty(str6)) {
          paramMap.put("mv", str6);
        }
        paramMap.put("msid", localContext.getPackageName());
        paramMap.put("app_name", i2 + ".android." + localContext.getPackageName());
        paramMap.put("isu", AdUtil.a(localContext));
        str7 = AdUtil.d(localContext);
        if (str7 != null) {
          break;
        }
        throw new d("NETWORK_ERROR");
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        AdSize localAdSize1;
        HashMap localHashMap;
        throw new b("NameNotFoundException");
      }
      localAdSize1 = ((h)this.g.h().i.a()).b();
      if (localAdSize1.isFullWidth()) {
        paramMap.put("smart_w", "full");
      }
      if (localAdSize1.isAutoHeight()) {
        paramMap.put("smart_h", "auto");
      }
      if (!localAdSize1.isCustomAdSize())
      {
        paramMap.put("format", localAdSize1.toString());
      }
      else
      {
        localHashMap = new HashMap();
        localHashMap.put("w", Integer.valueOf(localAdSize1.getWidth()));
        localHashMap.put("h", Integer.valueOf(localAdSize1.getHeight()));
        paramMap.put("ad_frame", localHashMap);
      }
    }
    paramMap.put("net", str7);
    String str8 = AdUtil.e(localContext);
    if ((str8 != null) && (str8.length() != 0)) {
      paramMap.put("cap", str8);
    }
    paramMap.put("u_audio", Integer.valueOf(AdUtil.g(localContext).ordinal()));
    DisplayMetrics localDisplayMetrics = AdUtil.a(paramActivity);
    paramMap.put("u_sd", Float.valueOf(localDisplayMetrics.density));
    paramMap.put("u_h", Integer.valueOf(AdUtil.a(localContext, localDisplayMetrics)));
    paramMap.put("u_w", Integer.valueOf(AdUtil.b(localContext, localDisplayMetrics)));
    paramMap.put("hl", Locale.getDefault().getLanguage());
    StringBuilder localStringBuilder = new StringBuilder();
    AdSize[] arrayOfAdSize = (AdSize[])this.g.h().j.a();
    if (arrayOfAdSize != null)
    {
      int i3 = arrayOfAdSize.length;
      while (i1 < i3)
      {
        AdSize localAdSize2 = arrayOfAdSize[i1];
        if (localStringBuilder.length() != 0) {
          localStringBuilder.append("|");
        }
        localStringBuilder.append(localAdSize2.getWidth() + "x" + localAdSize2.getHeight());
        i1++;
      }
      paramMap.put("sz", localStringBuilder.toString());
    }
    paramMap.put("carrier", ((TelephonyManager)localContext.getSystemService("phone")).getNetworkOperator());
    if (AdUtil.c()) {
      paramMap.put("simulator", Integer.valueOf(1));
    }
    paramMap.put("session_id", com.google.ads.b.a().b().toString());
    paramMap.put("seq_num", com.google.ads.b.a().c().toString());
    String str9 = AdUtil.a(paramMap);
    if (((Boolean)((l.a)((l)this.g.h().a.a()).a.a()).l.a()).booleanValue()) {}
    for (String str10 = d() + "AFMA_buildAdURL" + "(" + str9 + ");" + "</script></head><body></body></html>";; str10 = d() + "AFMA_getSdkConstants();" + "AFMA_buildAdURL" + "(" + str9 + ");" + "</script></head><body></body></html>")
    {
      com.google.ads.util.b.c("adRequestUrlHtml: " + str10);
      return str10;
    }
  }
  
  protected void a()
  {
    com.google.ads.util.b.a("AdLoader cancelled.");
    if (this.i != null)
    {
      this.i.stopLoading();
      this.i.destroy();
    }
    if (this.s != null)
    {
      this.s.interrupt();
      this.s = null;
    }
    if (this.f != null) {
      this.f.a();
    }
    this.n = true;
  }
  
  public void a(int paramInt)
  {
    try
    {
      this.r = paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void a(AdRequest.ErrorCode paramErrorCode)
  {
    try
    {
      this.p = paramErrorCode;
      notify();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void a(AdRequest.ErrorCode paramErrorCode, boolean paramBoolean)
  {
    this.g.a(new a(this.g, this.i, this.f, paramErrorCode, paramBoolean));
  }
  
  protected void a(AdRequest paramAdRequest)
  {
    this.h = paramAdRequest;
    this.n = false;
    this.s = new Thread(this);
    this.s.start();
  }
  
  public void a(AdSize paramAdSize)
  {
    try
    {
      this.m = paramAdSize;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void a(String paramString)
  {
    try
    {
      this.k.add(paramString);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void a(String paramString1, String paramString2)
  {
    try
    {
      this.a = paramString2;
      this.b = paramString1;
      notify();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void a(boolean paramBoolean)
  {
    try
    {
      this.e = paramBoolean;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void b()
  {
    try
    {
      if (TextUtils.isEmpty(this.d))
      {
        com.google.ads.util.b.b("Got a mediation response with no content type. Aborting mediation.");
        a(AdRequest.ErrorCode.INTERNAL_ERROR, false);
        return;
      }
      if (!this.d.startsWith("application/json"))
      {
        com.google.ads.util.b.b("Got a mediation response with a content type: '" + this.d + "'. Expected something starting with 'application/json'. Aborting mediation.");
        a(AdRequest.ErrorCode.INTERNAL_ERROR, false);
        return;
      }
    }
    catch (JSONException localJSONException)
    {
      com.google.ads.util.b.b("AdLoader can't parse gWhirl server configuration.", localJSONException);
      a(AdRequest.ErrorCode.INTERNAL_ERROR, false);
      return;
    }
    final com.google.ads.c localc = com.google.ads.c.a(this.b);
    a(this.c, localc, this.g.i());
    this.g.a(new Runnable()
    {
      public void run()
      {
        if (c.a(c.this) != null)
        {
          c.a(c.this).stopLoading();
          c.a(c.this).destroy();
        }
        c.c(c.this).a(c.b(c.this));
        if (c.d(c.this) != null) {
          ((h)c.c(c.this).h().i.a()).b(c.d(c.this));
        }
        c.c(c.this).a(localc);
      }
    });
  }
  
  protected void b(String paramString)
  {
    try
    {
      this.d = paramString;
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
    try
    {
      this.o = paramBoolean;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void c()
  {
    try
    {
      this.q = true;
      notify();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void c(String paramString)
  {
    try
    {
      this.c = paramString;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void c(boolean paramBoolean)
  {
    this.t = paramBoolean;
  }
  
  public void d(String paramString)
  {
    try
    {
      this.j = paramString;
      notify();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void e(String paramString)
  {
    try
    {
      this.l = paramString;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 96	com/google/ads/internal/c:i	Landroid/webkit/WebView;
    //   6: ifnull +10 -> 16
    //   9: aload_0
    //   10: getfield 127	com/google/ads/internal/c:f	Lcom/google/ads/internal/f;
    //   13: ifnonnull +20 -> 33
    //   16: ldc_w 677
    //   19: invokestatic 134	com/google/ads/util/b:e	(Ljava/lang/String;)V
    //   22: aload_0
    //   23: getstatic 639	com/google/ads/AdRequest$ErrorCode:INTERNAL_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   26: iconst_0
    //   27: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   30: aload_0
    //   31: monitorexit
    //   32: return
    //   33: aload_0
    //   34: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   37: invokevirtual 77	com/google/ads/internal/d:h	()Lcom/google/ads/m;
    //   40: getfield 82	com/google/ads/m:c	Lcom/google/ads/util/i$d;
    //   43: invokevirtual 87	com/google/ads/util/i$d:a	()Ljava/lang/Object;
    //   46: checkcast 89	android/app/Activity
    //   49: astore_3
    //   50: aload_3
    //   51: ifnonnull +25 -> 76
    //   54: ldc_w 679
    //   57: invokestatic 134	com/google/ads/util/b:e	(Ljava/lang/String;)V
    //   60: aload_0
    //   61: getstatic 639	com/google/ads/AdRequest$ErrorCode:INTERNAL_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   64: iconst_0
    //   65: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   68: aload_0
    //   69: monitorexit
    //   70: return
    //   71: astore_2
    //   72: aload_0
    //   73: monitorexit
    //   74: aload_2
    //   75: athrow
    //   76: aload_0
    //   77: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   80: invokevirtual 680	com/google/ads/internal/d:o	()J
    //   83: lstore 4
    //   85: invokestatic 685	android/os/SystemClock:elapsedRealtime	()J
    //   88: lstore 6
    //   90: aload_0
    //   91: getfield 224	com/google/ads/internal/c:h	Lcom/google/ads/AdRequest;
    //   94: aload_0
    //   95: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   98: invokevirtual 77	com/google/ads/internal/d:h	()Lcom/google/ads/m;
    //   101: getfield 687	com/google/ads/m:d	Lcom/google/ads/util/i$b;
    //   104: invokevirtual 351	com/google/ads/util/i$b:a	()Ljava/lang/Object;
    //   107: checkcast 357	android/content/Context
    //   110: invokevirtual 693	com/google/ads/AdRequest:getRequestMap	(Landroid/content/Context;)Ljava/util/Map;
    //   113: astore 8
    //   115: aload 8
    //   117: ldc_w 695
    //   120: invokeinterface 699 2 0
    //   125: astore 9
    //   127: aload 9
    //   129: instanceof 281
    //   132: ifeq +142 -> 274
    //   135: aload 9
    //   137: checkcast 281	java/util/Map
    //   140: astore 31
    //   142: aload 31
    //   144: ldc_w 701
    //   147: invokeinterface 699 2 0
    //   152: astore 32
    //   154: aload 32
    //   156: instanceof 142
    //   159: ifeq +12 -> 171
    //   162: aload_0
    //   163: aload 32
    //   165: checkcast 142	java/lang/String
    //   168: putfield 49	com/google/ads/internal/c:a	Ljava/lang/String;
    //   171: aload 31
    //   173: ldc_w 703
    //   176: invokeinterface 699 2 0
    //   181: astore 33
    //   183: aload 33
    //   185: instanceof 142
    //   188: ifeq +12 -> 200
    //   191: aload_0
    //   192: aload 33
    //   194: checkcast 142	java/lang/String
    //   197: putfield 47	com/google/ads/internal/c:j	Ljava/lang/String;
    //   200: aload 31
    //   202: ldc_w 705
    //   205: invokeinterface 699 2 0
    //   210: astore 34
    //   212: aload 34
    //   214: instanceof 142
    //   217: ifeq +19 -> 236
    //   220: aload 34
    //   222: ldc_w 706
    //   225: invokevirtual 709	java/lang/Object:equals	(Ljava/lang/Object;)Z
    //   228: ifeq +121 -> 349
    //   231: aload_0
    //   232: iconst_1
    //   233: putfield 64	com/google/ads/internal/c:r	I
    //   236: aload 31
    //   238: ldc_w 711
    //   241: invokeinterface 699 2 0
    //   246: astore 35
    //   248: aload 35
    //   250: instanceof 142
    //   253: ifeq +21 -> 274
    //   256: aload 35
    //   258: ldc_w 712
    //   261: invokevirtual 709	java/lang/Object:equals	(Ljava/lang/Object;)Z
    //   264: ifeq +10 -> 274
    //   267: aload_0
    //   268: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   271: invokevirtual 714	com/google/ads/internal/d:d	()V
    //   274: aload_0
    //   275: getfield 49	com/google/ads/internal/c:a	Ljava/lang/String;
    //   278: ifnonnull +565 -> 843
    //   281: aload_0
    //   282: getfield 47	com/google/ads/internal/c:j	Ljava/lang/String;
    //   285: astore 15
    //   287: aload 15
    //   289: ifnonnull +268 -> 557
    //   292: aload_0
    //   293: aload 8
    //   295: aload_3
    //   296: invokevirtual 716	com/google/ads/internal/c:a	(Ljava/util/Map;Landroid/app/Activity;)Ljava/lang/String;
    //   299: astore 24
    //   301: aload_0
    //   302: aload 24
    //   304: invokespecial 718	com/google/ads/internal/c:f	(Ljava/lang/String;)V
    //   307: invokestatic 685	android/os/SystemClock:elapsedRealtime	()J
    //   310: lstore 25
    //   312: lload 4
    //   314: lload 25
    //   316: lload 6
    //   318: lsub
    //   319: lsub
    //   320: lstore 27
    //   322: lload 27
    //   324: lconst_0
    //   325: lcmp
    //   326: ifle +9 -> 335
    //   329: aload_0
    //   330: lload 27
    //   332: invokevirtual 722	java/lang/Object:wait	(J)V
    //   335: aload_0
    //   336: getfield 604	com/google/ads/internal/c:n	Z
    //   339: istore 29
    //   341: iload 29
    //   343: ifeq +147 -> 490
    //   346: aload_0
    //   347: monitorexit
    //   348: return
    //   349: aload 34
    //   351: ldc_w 723
    //   354: invokevirtual 709	java/lang/Object:equals	(Ljava/lang/Object;)Z
    //   357: ifeq -121 -> 236
    //   360: aload_0
    //   361: iconst_0
    //   362: putfield 64	com/google/ads/internal/c:r	I
    //   365: goto -129 -> 236
    //   368: astore_1
    //   369: ldc_w 725
    //   372: aload_1
    //   373: invokestatic 204	com/google/ads/util/b:b	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   376: aload_0
    //   377: getstatic 639	com/google/ads/AdRequest$ErrorCode:INTERNAL_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   380: iconst_1
    //   381: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   384: aload_0
    //   385: monitorexit
    //   386: return
    //   387: astore 23
    //   389: new 206	java/lang/StringBuilder
    //   392: dup
    //   393: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   396: ldc_w 727
    //   399: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   402: aload 23
    //   404: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   407: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   410: invokestatic 199	com/google/ads/util/b:c	(Ljava/lang/String;)V
    //   413: aload_0
    //   414: getstatic 732	com/google/ads/AdRequest$ErrorCode:NETWORK_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   417: iconst_0
    //   418: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   421: aload_0
    //   422: monitorexit
    //   423: return
    //   424: astore 22
    //   426: new 206	java/lang/StringBuilder
    //   429: dup
    //   430: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   433: ldc_w 734
    //   436: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   439: aload 22
    //   441: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   444: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   447: invokestatic 199	com/google/ads/util/b:c	(Ljava/lang/String;)V
    //   450: aload_0
    //   451: getstatic 639	com/google/ads/AdRequest$ErrorCode:INTERNAL_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   454: iconst_0
    //   455: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   458: aload_0
    //   459: monitorexit
    //   460: return
    //   461: astore 30
    //   463: new 206	java/lang/StringBuilder
    //   466: dup
    //   467: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   470: ldc_w 736
    //   473: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   476: aload 30
    //   478: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   481: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   484: invokestatic 588	com/google/ads/util/b:a	(Ljava/lang/String;)V
    //   487: aload_0
    //   488: monitorexit
    //   489: return
    //   490: aload_0
    //   491: getfield 60	com/google/ads/internal/c:p	Lcom/google/ads/AdRequest$ErrorCode;
    //   494: ifnull +15 -> 509
    //   497: aload_0
    //   498: aload_0
    //   499: getfield 60	com/google/ads/internal/c:p	Lcom/google/ads/AdRequest$ErrorCode;
    //   502: iconst_0
    //   503: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   506: aload_0
    //   507: monitorexit
    //   508: return
    //   509: aload_0
    //   510: getfield 47	com/google/ads/internal/c:j	Ljava/lang/String;
    //   513: ifnonnull +44 -> 557
    //   516: new 206	java/lang/StringBuilder
    //   519: dup
    //   520: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   523: ldc_w 738
    //   526: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   529: lload 4
    //   531: invokevirtual 741	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   534: ldc_w 743
    //   537: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   540: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   543: invokestatic 199	com/google/ads/util/b:c	(Ljava/lang/String;)V
    //   546: aload_0
    //   547: getstatic 732	com/google/ads/AdRequest$ErrorCode:NETWORK_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   550: iconst_0
    //   551: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   554: aload_0
    //   555: monitorexit
    //   556: return
    //   557: aload_0
    //   558: getfield 127	com/google/ads/internal/c:f	Lcom/google/ads/internal/f;
    //   561: aload_0
    //   562: getfield 670	com/google/ads/internal/c:t	Z
    //   565: invokevirtual 745	com/google/ads/internal/f:a	(Z)V
    //   568: aload_0
    //   569: getfield 127	com/google/ads/internal/c:f	Lcom/google/ads/internal/f;
    //   572: aload_0
    //   573: getfield 47	com/google/ads/internal/c:j	Ljava/lang/String;
    //   576: invokevirtual 746	com/google/ads/internal/f:a	(Ljava/lang/String;)V
    //   579: invokestatic 685	android/os/SystemClock:elapsedRealtime	()J
    //   582: lstore 16
    //   584: lload 4
    //   586: lload 16
    //   588: lload 6
    //   590: lsub
    //   591: lsub
    //   592: lstore 18
    //   594: lload 18
    //   596: lconst_0
    //   597: lcmp
    //   598: ifle +9 -> 607
    //   601: aload_0
    //   602: lload 18
    //   604: invokevirtual 722	java/lang/Object:wait	(J)V
    //   607: aload_0
    //   608: getfield 604	com/google/ads/internal/c:n	Z
    //   611: istore 20
    //   613: iload 20
    //   615: ifeq +35 -> 650
    //   618: aload_0
    //   619: monitorexit
    //   620: return
    //   621: astore 21
    //   623: new 206	java/lang/StringBuilder
    //   626: dup
    //   627: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   630: ldc_w 748
    //   633: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   636: aload 21
    //   638: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   641: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   644: invokestatic 588	com/google/ads/util/b:a	(Ljava/lang/String;)V
    //   647: aload_0
    //   648: monitorexit
    //   649: return
    //   650: aload_0
    //   651: getfield 60	com/google/ads/internal/c:p	Lcom/google/ads/AdRequest$ErrorCode;
    //   654: ifnull +15 -> 669
    //   657: aload_0
    //   658: aload_0
    //   659: getfield 60	com/google/ads/internal/c:p	Lcom/google/ads/AdRequest$ErrorCode;
    //   662: iconst_0
    //   663: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   666: aload_0
    //   667: monitorexit
    //   668: return
    //   669: aload_0
    //   670: getfield 51	com/google/ads/internal/c:b	Ljava/lang/String;
    //   673: ifnonnull +44 -> 717
    //   676: new 206	java/lang/StringBuilder
    //   679: dup
    //   680: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   683: ldc_w 738
    //   686: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   689: lload 4
    //   691: invokevirtual 741	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   694: ldc_w 750
    //   697: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   700: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   703: invokestatic 199	com/google/ads/util/b:c	(Ljava/lang/String;)V
    //   706: aload_0
    //   707: getstatic 732	com/google/ads/AdRequest$ErrorCode:NETWORK_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   710: iconst_0
    //   711: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   714: aload_0
    //   715: monitorexit
    //   716: return
    //   717: aload_0
    //   718: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   721: invokevirtual 77	com/google/ads/internal/d:h	()Lcom/google/ads/m;
    //   724: getfield 508	com/google/ads/m:j	Lcom/google/ads/util/i$c;
    //   727: invokevirtual 511	com/google/ads/util/i$c:a	()Ljava/lang/Object;
    //   730: ifnull +95 -> 825
    //   733: aload_0
    //   734: getfield 72	com/google/ads/internal/c:m	Lcom/google/ads/AdSize;
    //   737: ifnonnull +20 -> 757
    //   740: ldc_w 752
    //   743: invokestatic 634	com/google/ads/util/b:b	(Ljava/lang/String;)V
    //   746: aload_0
    //   747: getstatic 639	com/google/ads/AdRequest$ErrorCode:INTERNAL_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   750: iconst_0
    //   751: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   754: aload_0
    //   755: monitorexit
    //   756: return
    //   757: aload_0
    //   758: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   761: invokevirtual 77	com/google/ads/internal/d:h	()Lcom/google/ads/m;
    //   764: getfield 508	com/google/ads/m:j	Lcom/google/ads/util/i$c;
    //   767: invokevirtual 511	com/google/ads/util/i$c:a	()Ljava/lang/Object;
    //   770: checkcast 754	[Ljava/lang/Object;
    //   773: invokestatic 760	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   776: aload_0
    //   777: getfield 72	com/google/ads/internal/c:m	Lcom/google/ads/AdSize;
    //   780: invokeinterface 764 2 0
    //   785: ifne +58 -> 843
    //   788: new 206	java/lang/StringBuilder
    //   791: dup
    //   792: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   795: ldc_w 766
    //   798: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   801: aload_0
    //   802: getfield 72	com/google/ads/internal/c:m	Lcom/google/ads/AdSize;
    //   805: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   808: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   811: invokestatic 634	com/google/ads/util/b:b	(Ljava/lang/String;)V
    //   814: aload_0
    //   815: getstatic 639	com/google/ads/AdRequest$ErrorCode:INTERNAL_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   818: iconst_0
    //   819: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   822: aload_0
    //   823: monitorexit
    //   824: return
    //   825: aload_0
    //   826: getfield 72	com/google/ads/internal/c:m	Lcom/google/ads/AdSize;
    //   829: ifnull +14 -> 843
    //   832: ldc_w 768
    //   835: invokestatic 134	com/google/ads/util/b:e	(Ljava/lang/String;)V
    //   838: aload_0
    //   839: aconst_null
    //   840: putfield 72	com/google/ads/internal/c:m	Lcom/google/ads/AdSize;
    //   843: aload_0
    //   844: getfield 66	com/google/ads/internal/c:e	Z
    //   847: ifeq +18 -> 865
    //   850: aload_0
    //   851: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   854: iconst_1
    //   855: invokevirtual 770	com/google/ads/internal/d:b	(Z)V
    //   858: aload_0
    //   859: invokevirtual 772	com/google/ads/internal/c:b	()V
    //   862: aload_0
    //   863: monitorexit
    //   864: return
    //   865: aload_0
    //   866: getfield 630	com/google/ads/internal/c:d	Ljava/lang/String;
    //   869: ifnull +72 -> 941
    //   872: aload_0
    //   873: getfield 630	com/google/ads/internal/c:d	Ljava/lang/String;
    //   876: ldc_w 643
    //   879: invokevirtual 647	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   882: ifne +16 -> 898
    //   885: aload_0
    //   886: getfield 630	com/google/ads/internal/c:d	Ljava/lang/String;
    //   889: ldc_w 774
    //   892: invokevirtual 647	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   895: ifeq +46 -> 941
    //   898: new 206	java/lang/StringBuilder
    //   901: dup
    //   902: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   905: ldc_w 776
    //   908: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   911: aload_0
    //   912: getfield 630	com/google/ads/internal/c:d	Ljava/lang/String;
    //   915: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   918: ldc_w 778
    //   921: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   924: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   927: invokestatic 634	com/google/ads/util/b:b	(Ljava/lang/String;)V
    //   930: aload_0
    //   931: getstatic 639	com/google/ads/AdRequest$ErrorCode:INTERNAL_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   934: iconst_0
    //   935: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   938: aload_0
    //   939: monitorexit
    //   940: return
    //   941: aload_0
    //   942: getfield 45	com/google/ads/internal/c:g	Lcom/google/ads/internal/d;
    //   945: iconst_0
    //   946: invokevirtual 770	com/google/ads/internal/d:b	(Z)V
    //   949: aload_0
    //   950: invokespecial 780	com/google/ads/internal/c:e	()V
    //   953: invokestatic 685	android/os/SystemClock:elapsedRealtime	()J
    //   956: lstore 10
    //   958: lload 4
    //   960: lload 10
    //   962: lload 6
    //   964: lsub
    //   965: lsub
    //   966: lstore 12
    //   968: lload 12
    //   970: lconst_0
    //   971: lcmp
    //   972: ifle +9 -> 981
    //   975: aload_0
    //   976: lload 12
    //   978: invokevirtual 722	java/lang/Object:wait	(J)V
    //   981: aload_0
    //   982: getfield 62	com/google/ads/internal/c:q	Z
    //   985: ifeq +39 -> 1024
    //   988: aload_0
    //   989: invokespecial 782	com/google/ads/internal/c:f	()V
    //   992: goto -608 -> 384
    //   995: astore 14
    //   997: new 206	java/lang/StringBuilder
    //   1000: dup
    //   1001: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   1004: ldc_w 784
    //   1007: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1010: aload 14
    //   1012: invokevirtual 730	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1015: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1018: invokestatic 588	com/google/ads/util/b:a	(Ljava/lang/String;)V
    //   1021: aload_0
    //   1022: monitorexit
    //   1023: return
    //   1024: new 206	java/lang/StringBuilder
    //   1027: dup
    //   1028: invokespecial 207	java/lang/StringBuilder:<init>	()V
    //   1031: ldc_w 738
    //   1034: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1037: lload 4
    //   1039: invokevirtual 741	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1042: ldc_w 786
    //   1045: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1048: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1051: invokestatic 199	com/google/ads/util/b:c	(Ljava/lang/String;)V
    //   1054: aload_0
    //   1055: getstatic 732	com/google/ads/AdRequest$ErrorCode:NETWORK_ERROR	Lcom/google/ads/AdRequest$ErrorCode;
    //   1058: iconst_1
    //   1059: invokevirtual 641	com/google/ads/internal/c:a	(Lcom/google/ads/AdRequest$ErrorCode;Z)V
    //   1062: goto -678 -> 384
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1065	0	this	c
    //   368	5	1	localThrowable	java.lang.Throwable
    //   71	4	2	localObject1	Object
    //   49	247	3	localActivity	Activity
    //   83	955	4	l1	long
    //   88	875	6	l2	long
    //   113	181	8	localMap1	Map
    //   125	11	9	localObject2	Object
    //   956	5	10	l3	long
    //   966	11	12	l4	long
    //   995	16	14	localInterruptedException1	java.lang.InterruptedException
    //   285	3	15	str1	String
    //   582	5	16	l5	long
    //   592	11	18	l6	long
    //   611	3	20	bool1	boolean
    //   621	16	21	localInterruptedException2	java.lang.InterruptedException
    //   424	16	22	localb	b
    //   387	16	23	locald	d
    //   299	4	24	str2	String
    //   310	5	25	l7	long
    //   320	11	27	l8	long
    //   339	3	29	bool2	boolean
    //   461	16	30	localInterruptedException3	java.lang.InterruptedException
    //   140	97	31	localMap2	Map
    //   152	12	32	localObject3	Object
    //   181	12	33	localObject4	Object
    //   210	140	34	localObject5	Object
    //   246	11	35	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   2	16	71	finally
    //   16	30	71	finally
    //   30	32	71	finally
    //   33	50	71	finally
    //   54	68	71	finally
    //   68	70	71	finally
    //   72	74	71	finally
    //   76	171	71	finally
    //   171	200	71	finally
    //   200	236	71	finally
    //   236	274	71	finally
    //   274	287	71	finally
    //   292	301	71	finally
    //   301	312	71	finally
    //   329	335	71	finally
    //   335	341	71	finally
    //   346	348	71	finally
    //   349	365	71	finally
    //   369	384	71	finally
    //   384	386	71	finally
    //   389	421	71	finally
    //   421	423	71	finally
    //   426	458	71	finally
    //   458	460	71	finally
    //   463	487	71	finally
    //   487	489	71	finally
    //   490	506	71	finally
    //   506	508	71	finally
    //   509	554	71	finally
    //   554	556	71	finally
    //   557	584	71	finally
    //   601	607	71	finally
    //   607	613	71	finally
    //   618	620	71	finally
    //   623	647	71	finally
    //   647	649	71	finally
    //   650	666	71	finally
    //   666	668	71	finally
    //   669	714	71	finally
    //   714	716	71	finally
    //   717	754	71	finally
    //   754	756	71	finally
    //   757	822	71	finally
    //   822	824	71	finally
    //   825	843	71	finally
    //   843	862	71	finally
    //   862	864	71	finally
    //   865	898	71	finally
    //   898	938	71	finally
    //   938	940	71	finally
    //   941	958	71	finally
    //   975	981	71	finally
    //   981	992	71	finally
    //   997	1021	71	finally
    //   1021	1023	71	finally
    //   1024	1062	71	finally
    //   2	16	368	java/lang/Throwable
    //   16	30	368	java/lang/Throwable
    //   33	50	368	java/lang/Throwable
    //   54	68	368	java/lang/Throwable
    //   76	171	368	java/lang/Throwable
    //   171	200	368	java/lang/Throwable
    //   200	236	368	java/lang/Throwable
    //   236	274	368	java/lang/Throwable
    //   274	287	368	java/lang/Throwable
    //   292	301	368	java/lang/Throwable
    //   301	312	368	java/lang/Throwable
    //   329	335	368	java/lang/Throwable
    //   335	341	368	java/lang/Throwable
    //   349	365	368	java/lang/Throwable
    //   389	421	368	java/lang/Throwable
    //   426	458	368	java/lang/Throwable
    //   463	487	368	java/lang/Throwable
    //   490	506	368	java/lang/Throwable
    //   509	554	368	java/lang/Throwable
    //   557	584	368	java/lang/Throwable
    //   601	607	368	java/lang/Throwable
    //   607	613	368	java/lang/Throwable
    //   623	647	368	java/lang/Throwable
    //   650	666	368	java/lang/Throwable
    //   669	714	368	java/lang/Throwable
    //   717	754	368	java/lang/Throwable
    //   757	822	368	java/lang/Throwable
    //   825	843	368	java/lang/Throwable
    //   843	862	368	java/lang/Throwable
    //   865	898	368	java/lang/Throwable
    //   898	938	368	java/lang/Throwable
    //   941	958	368	java/lang/Throwable
    //   975	981	368	java/lang/Throwable
    //   981	992	368	java/lang/Throwable
    //   997	1021	368	java/lang/Throwable
    //   1024	1062	368	java/lang/Throwable
    //   292	301	387	com/google/ads/internal/c$d
    //   292	301	424	com/google/ads/internal/c$b
    //   329	335	461	java/lang/InterruptedException
    //   601	607	621	java/lang/InterruptedException
    //   975	981	995	java/lang/InterruptedException
  }
  
  private static class a
    implements Runnable
  {
    private final d a;
    private final WebView b;
    private final f c;
    private final AdRequest.ErrorCode d;
    private final boolean e;
    
    public a(d paramd, WebView paramWebView, f paramf, AdRequest.ErrorCode paramErrorCode, boolean paramBoolean)
    {
      this.a = paramd;
      this.b = paramWebView;
      this.c = paramf;
      this.d = paramErrorCode;
      this.e = paramBoolean;
    }
    
    public void run()
    {
      if (this.b != null)
      {
        this.b.stopLoading();
        this.b.destroy();
      }
      if (this.c != null) {
        this.c.a();
      }
      if (this.e)
      {
        AdWebView localAdWebView = this.a.k();
        localAdWebView.stopLoading();
        localAdWebView.setVisibility(8);
      }
      this.a.a(this.d);
    }
  }
  
  private class b
    extends Exception
  {
    public b(String paramString)
    {
      super();
    }
  }
  
  private class c
    implements Runnable
  {
    private final String b;
    private final String c;
    private final WebView d;
    
    public c(WebView paramWebView, String paramString1, String paramString2)
    {
      this.d = paramWebView;
      this.b = paramString1;
      this.c = paramString2;
    }
    
    public void run()
    {
      if (this.c != null)
      {
        this.d.loadDataWithBaseURL(this.b, this.c, "text/html", "utf-8", null);
        return;
      }
      this.d.loadUrl(this.b);
    }
  }
  
  private class d
    extends Exception
  {
    public d(String paramString)
    {
      super();
    }
  }
  
  private static class e
    implements Runnable
  {
    private final d a;
    private final WebView b;
    private final LinkedList<String> c;
    private final int d;
    private final boolean e;
    private final String f;
    private final AdSize g;
    
    public e(d paramd, WebView paramWebView, LinkedList<String> paramLinkedList, int paramInt, boolean paramBoolean, String paramString, AdSize paramAdSize)
    {
      this.a = paramd;
      this.b = paramWebView;
      this.c = paramLinkedList;
      this.d = paramInt;
      this.e = paramBoolean;
      this.f = paramString;
      this.g = paramAdSize;
    }
    
    public void run()
    {
      if (this.b != null)
      {
        this.b.stopLoading();
        this.b.destroy();
      }
      this.a.a(this.c);
      this.a.a(this.d);
      this.a.a(this.e);
      this.a.a(this.f);
      if (this.g != null)
      {
        ((h)this.a.h().i.a()).b(this.g);
        this.a.k().setAdSize(this.g);
      }
      this.a.C();
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.internal.c
 * JD-Core Version:    0.7.0.1
 */
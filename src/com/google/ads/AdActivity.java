package com.google.ads;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.google.ads.internal.AdVideoView;
import com.google.ads.internal.AdWebView;
import com.google.ads.internal.a;
import com.google.ads.internal.d;
import com.google.ads.internal.e;
import com.google.ads.internal.i;
import com.google.ads.util.AdUtil;
import com.google.ads.util.b;
import com.google.ads.util.f;
import com.google.ads.util.g;
import com.google.ads.util.i.b;
import com.google.ads.util.i.c;
import com.google.ads.util.i.d;
import java.util.HashMap;
import java.util.Map;

public class AdActivity
  extends Activity
  implements View.OnClickListener
{
  public static final String BASE_URL_PARAM = "baseurl";
  public static final String HTML_PARAM = "html";
  public static final String INTENT_ACTION_PARAM = "i";
  public static final String ORIENTATION_PARAM = "o";
  public static final String TYPE_PARAM = "m";
  public static final String URL_PARAM = "u";
  private static final a a = (a)a.a.b();
  private static final Object b = new Object();
  private static AdActivity c = null;
  private static d d = null;
  private static AdActivity e = null;
  private static AdActivity f = null;
  private static final StaticMethodWrapper g = new StaticMethodWrapper();
  private AdWebView h;
  private ViewGroup i = null;
  private boolean j;
  private long k;
  private RelativeLayout l;
  private AdActivity m = null;
  private boolean n;
  private boolean o;
  private boolean p;
  private boolean q;
  private AdVideoView r;
  
  private RelativeLayout.LayoutParams a(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(paramInt3, paramInt4);
    localLayoutParams.setMargins(paramInt1, paramInt2, 0, 0);
    localLayoutParams.addRule(10);
    localLayoutParams.addRule(9);
    return localLayoutParams;
  }
  
  private void a(String paramString)
  {
    b.b(paramString);
    finish();
  }
  
  private void a(String paramString, Throwable paramThrowable)
  {
    b.b(paramString, paramThrowable);
    finish();
  }
  
  private void d()
  {
    if (!this.j) {
      if (this.h != null)
      {
        a.b(this.h);
        this.h.setAdActivity(null);
        this.h.setIsExpandedMraid(false);
        if ((!this.o) && (this.l != null) && (this.i != null))
        {
          if ((!this.p) || (this.q)) {
            break label238;
          }
          b.a("Disabling hardware acceleration on collapsing MRAID WebView.");
          this.h.b();
        }
      }
    }
    for (;;)
    {
      this.l.removeView(this.h);
      this.i.addView(this.h);
      if (this.r != null)
      {
        this.r.e();
        this.r = null;
      }
      if (this == c) {
        c = null;
      }
      f = this.m;
      synchronized (b)
      {
        if ((d != null) && (this.o) && (this.h != null))
        {
          if (this.h == d.k()) {
            d.a();
          }
          this.h.stopLoading();
        }
        if (this == e)
        {
          e = null;
          if (d != null)
          {
            d.t();
            d = null;
          }
        }
        else
        {
          this.j = true;
          b.a("AdActivity is closing.");
          return;
          label238:
          if ((this.p) || (!this.q)) {
            continue;
          }
          b.a("Re-enabling hardware acceleration on collapsing MRAID WebView.");
          this.h.c();
          continue;
        }
        b.e("currentAdManager is null while trying to destroy AdActivity.");
      }
    }
  }
  
  public static boolean isShowing()
  {
    return g.isShowing();
  }
  
  public static void launchAdActivity(d paramd, e parame)
  {
    g.launchAdActivity(paramd, parame);
  }
  
  protected View a(int paramInt)
  {
    ImageButton localImageButton = new ImageButton(getApplicationContext());
    localImageButton.setImageResource(17301527);
    localImageButton.setBackgroundColor(0);
    localImageButton.setOnClickListener(this);
    localImageButton.setPadding(0, 0, 0, 0);
    int i1 = (int)TypedValue.applyDimension(1, paramInt, getResources().getDisplayMetrics());
    FrameLayout localFrameLayout = new FrameLayout(getApplicationContext());
    localFrameLayout.addView(localImageButton, i1, i1);
    return localFrameLayout;
  }
  
  protected AdVideoView a(Activity paramActivity)
  {
    return new AdVideoView(paramActivity, this.h);
  }
  
  protected void a(AdWebView paramAdWebView, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    requestWindowFeature(1);
    Window localWindow = getWindow();
    localWindow.setFlags(1024, 1024);
    if (AdUtil.a >= 11)
    {
      if (!this.p) {
        break label105;
      }
      b.a("Enabling hardware acceleration on the AdActivity window.");
      g.a(localWindow);
    }
    for (;;)
    {
      ViewParent localViewParent = paramAdWebView.getParent();
      if (localViewParent != null)
      {
        if (!paramBoolean2) {
          break label126;
        }
        if (!(localViewParent instanceof ViewGroup)) {
          break;
        }
        this.i = ((ViewGroup)localViewParent);
        this.i.removeView(paramAdWebView);
      }
      if (paramAdWebView.d() == null) {
        break label134;
      }
      a("Interstitial created with an AdWebView that is already in use by another AdActivity.");
      return;
      label105:
      b.a("Disabling hardware acceleration on the AdActivity WebView.");
      paramAdWebView.b();
    }
    a("MRAID banner was not a child of a ViewGroup.");
    return;
    label126:
    a("Interstitial created with an AdWebView that has a parent.");
    return;
    label134:
    setRequestedOrientation(paramInt);
    paramAdWebView.setAdActivity(this);
    int i1;
    label153:
    View localView;
    RelativeLayout.LayoutParams localLayoutParams;
    if (paramBoolean2)
    {
      i1 = 50;
      localView = a(i1);
      this.l.addView(paramAdWebView, -1, -1);
      localLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
      if (!paramBoolean2) {
        break label262;
      }
      localLayoutParams.addRule(10);
      localLayoutParams.addRule(11);
    }
    for (;;)
    {
      this.l.addView(localView, localLayoutParams);
      this.l.setKeepScreenOn(true);
      setContentView(this.l);
      this.l.getRootView().setBackgroundColor(-16777216);
      if (!paramBoolean1) {
        break;
      }
      a.a(paramAdWebView);
      return;
      i1 = 32;
      break label153;
      label262:
      localLayoutParams.addRule(10);
      localLayoutParams.addRule(9);
    }
  }
  
  protected void a(d paramd)
  {
    this.h = null;
    this.k = SystemClock.elapsedRealtime();
    this.n = true;
    synchronized (b)
    {
      if (c == null)
      {
        c = this;
        paramd.v();
      }
      return;
    }
  }
  
  protected void a(HashMap<String, String> paramHashMap, d paramd)
  {
    Intent localIntent = new Intent();
    localIntent.setComponent(new ComponentName("com.google.android.apps.plus", "com.google.android.apps.circles.platform.PlusOneActivity"));
    localIntent.addCategory("android.intent.category.LAUNCHER");
    localIntent.putExtras(getIntent().getExtras());
    localIntent.putExtra("com.google.circles.platform.intent.extra.ENTITY", (String)paramHashMap.get("u"));
    localIntent.putExtra("com.google.circles.platform.intent.extra.ENTITY_TYPE", ah.b.a.c);
    localIntent.putExtra("com.google.circles.platform.intent.extra.ACTION", (String)paramHashMap.get("a"));
    a(paramd);
    try
    {
      b.a("Launching Google+ intent from AdActivity.");
      startActivityForResult(localIntent, 0);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      a(localActivityNotFoundException.getMessage(), localActivityNotFoundException);
    }
  }
  
  protected void b(HashMap<String, String> paramHashMap, d paramd)
  {
    if (paramHashMap == null)
    {
      a("Could not get the paramMap in launchIntent()");
      return;
    }
    String str1 = (String)paramHashMap.get("u");
    if (str1 == null)
    {
      a("Could not get the URL parameter in launchIntent().");
      return;
    }
    String str2 = (String)paramHashMap.get("i");
    String str3 = (String)paramHashMap.get("m");
    Uri localUri = Uri.parse(str1);
    if (str2 == null) {}
    for (Intent localIntent = new Intent("android.intent.action.VIEW", localUri);; localIntent = new Intent(str2, localUri))
    {
      if (str3 != null) {
        localIntent.setDataAndType(localUri, str3);
      }
      a(paramd);
      try
      {
        b.a("Launching an intent from AdActivity: " + localIntent.getAction() + " - " + localUri);
        startActivity(localIntent);
        return;
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        a(localActivityNotFoundException.getMessage(), localActivityNotFoundException);
        return;
      }
    }
  }
  
  public AdVideoView getAdVideoView()
  {
    return this.r;
  }
  
  public AdWebView getOpeningAdWebView()
  {
    if (this.m != null) {
      return this.m.h;
    }
    synchronized (b)
    {
      if (d == null)
      {
        b.e("currentAdManager was null while trying to get the opening AdWebView.");
        return null;
      }
    }
    AdWebView localAdWebView = d.k();
    if (localAdWebView != this.h) {
      return localAdWebView;
    }
    return null;
  }
  
  public void moveAdVideoView(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.r != null)
    {
      this.r.setLayoutParams(a(paramInt1, paramInt2, paramInt3, paramInt4));
      this.r.requestLayout();
    }
  }
  
  public void newAdVideoView(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.r == null)
    {
      this.r = a(this);
      this.l.addView(this.r, 0, a(paramInt1, paramInt2, paramInt3, paramInt4));
      synchronized (b)
      {
        if (d == null)
        {
          b.e("currentAdManager was null while trying to get the opening AdWebView.");
          return;
        }
        d.l().b(false);
        return;
      }
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    String str2;
    if ((getOpeningAdWebView() != null) && (paramIntent != null) && (paramIntent.getExtras() != null) && (paramIntent.getExtras().getString("com.google.circles.platform.result.extra.CONFIRMATION") != null) && (paramIntent.getExtras().getString("com.google.circles.platform.result.extra.ACTION") != null))
    {
      String str1 = paramIntent.getExtras().getString("com.google.circles.platform.result.extra.CONFIRMATION");
      str2 = paramIntent.getExtras().getString("com.google.circles.platform.result.extra.ACTION");
      if (str1.equals("yes"))
      {
        if (!str2.equals("insert")) {
          break label110;
        }
        af.a(getOpeningAdWebView(), true);
      }
    }
    for (;;)
    {
      finish();
      return;
      label110:
      if (str2.equals("delete")) {
        af.a(getOpeningAdWebView(), false);
      }
    }
  }
  
  public void onClick(View paramView)
  {
    finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.j = false;
    d locald;
    boolean bool1;
    Bundle localBundle;
    for (;;)
    {
      synchronized (b)
      {
        if (d != null)
        {
          locald = d;
          if (e == null)
          {
            e = this;
            locald.u();
          }
          if ((this.m == null) && (f != null)) {
            this.m = f;
          }
          f = this;
          if (((locald.h().a()) && (e == this)) || ((locald.h().b()) && (this.m == e))) {
            locald.w();
          }
          bool1 = locald.q();
          l.a locala = (l.a)((l)locald.h().a.a()).a.a();
          if (AdUtil.a >= ((Integer)locala.a.a()).intValue())
          {
            bool2 = true;
            this.q = bool2;
            if (AdUtil.a < ((Integer)locala.b.a()).intValue()) {
              break label268;
            }
            bool3 = true;
            this.p = bool3;
            this.l = null;
            this.n = false;
            this.o = true;
            this.r = null;
            localBundle = getIntent().getBundleExtra("com.google.ads.AdOpener");
            if (localBundle != null) {
              break;
            }
            a("Could not get the Bundle used to create AdActivity.");
          }
        }
        else
        {
          a("Could not get currentAdManager.");
          return;
        }
      }
      boolean bool2 = false;
      continue;
      label268:
      boolean bool3 = false;
    }
    e locale = new e(localBundle);
    String str1 = locale.b();
    HashMap localHashMap = locale.c();
    if (str1.equals("plusone"))
    {
      a(localHashMap, locald);
      return;
    }
    if (str1.equals("intent"))
    {
      b(localHashMap, locald);
      return;
    }
    this.l = new RelativeLayout(getApplicationContext());
    if (str1.equals("webapp"))
    {
      this.h = new AdWebView(locald.h(), null);
      Map localMap = a.c;
      boolean bool4;
      String str3;
      String str4;
      label483:
      String str5;
      int i2;
      if (!bool1)
      {
        bool4 = true;
        i locali = i.a(locald, localMap, true, bool4);
        locali.d(true);
        if (bool1) {
          locali.a(true);
        }
        this.h.setWebViewClient(locali);
        String str2 = (String)localHashMap.get("u");
        str3 = (String)localHashMap.get("baseurl");
        str4 = (String)localHashMap.get("html");
        if (str2 == null) {
          break label531;
        }
        this.h.loadUrl(str2);
        str5 = (String)localHashMap.get("o");
        if (!"p".equals(str5)) {
          break label565;
        }
        i2 = AdUtil.b();
      }
      for (;;)
      {
        a(this.h, false, i2, bool1);
        return;
        bool4 = false;
        break;
        label531:
        if (str4 != null)
        {
          this.h.loadDataWithBaseURL(str3, str4, "text/html", "utf-8", null);
          break label483;
        }
        a("Could not get the URL or HTML parameter to show a web app.");
        return;
        label565:
        if ("l".equals(str5)) {
          i2 = AdUtil.a();
        } else if (this == e) {
          i2 = locald.n();
        } else {
          i2 = -1;
        }
      }
    }
    if ((str1.equals("interstitial")) || (str1.equals("expand")))
    {
      this.h = locald.k();
      int i1 = locald.n();
      if (str1.equals("expand"))
      {
        this.h.setIsExpandedMraid(true);
        this.o = false;
        if ((this.p) && (!this.q))
        {
          b.a("Re-enabling hardware acceleration on expanding MRAID WebView.");
          this.h.c();
        }
      }
      a(this.h, true, i1, bool1);
      return;
    }
    a("Unknown AdOpener, <action: " + str1 + ">");
  }
  
  public void onDestroy()
  {
    if (this.l != null) {
      this.l.removeAllViews();
    }
    if (isFinishing())
    {
      d();
      if ((this.o) && (this.h != null))
      {
        this.h.stopLoading();
        this.h.destroy();
        this.h = null;
      }
    }
    super.onDestroy();
  }
  
  public void onPause()
  {
    if (isFinishing()) {
      d();
    }
    super.onPause();
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    if ((this.n) && (paramBoolean) && (SystemClock.elapsedRealtime() - this.k > 250L))
    {
      b.d("Launcher AdActivity got focus and is closing.");
      finish();
    }
    super.onWindowFocusChanged(paramBoolean);
  }
  
  public static class StaticMethodWrapper
  {
    public boolean isShowing()
    {
      for (;;)
      {
        synchronized ()
        {
          if (AdActivity.b() != null)
          {
            bool = true;
            return bool;
          }
        }
        boolean bool = false;
      }
    }
    
    public void launchAdActivity(d paramd, e parame)
    {
      Activity localActivity;
      synchronized ()
      {
        if (AdActivity.c() == null) {
          AdActivity.b(paramd);
        }
        while (AdActivity.c() == paramd)
        {
          localActivity = (Activity)paramd.h().c.a();
          if (localActivity != null) {
            break;
          }
          b.e("activity was null while launching an AdActivity.");
          return;
        }
        b.b("Tried to launch a new AdActivity with a different AdManager.");
        return;
      }
      Intent localIntent = new Intent(localActivity.getApplicationContext(), AdActivity.class);
      localIntent.putExtra("com.google.ads.AdOpener", parame.a());
      try
      {
        b.a("Launching AdActivity.");
        localActivity.startActivity(localIntent);
        return;
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        b.b("Activity not found.", localActivityNotFoundException);
      }
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.AdActivity
 * JD-Core Version:    0.7.0.1
 */
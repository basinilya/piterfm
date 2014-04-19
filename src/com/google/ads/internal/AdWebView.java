package com.google.ads.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View.MeasureSpec;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.ads.AdActivity;
import com.google.ads.AdSize;
import com.google.ads.m;
import com.google.ads.util.AdUtil;
import com.google.ads.util.b;
import com.google.ads.util.g;
import com.google.ads.util.g.a;
import com.google.ads.util.h.a;
import com.google.ads.util.i.b;
import java.lang.ref.WeakReference;

public class AdWebView
  extends WebView
{
  private WeakReference<AdActivity> a;
  private AdSize b;
  private boolean c;
  private boolean d;
  
  public AdWebView(m paramm, AdSize paramAdSize)
  {
    super((Context)paramm.d.a());
    this.b = paramAdSize;
    this.a = null;
    this.c = false;
    this.d = false;
    setBackgroundColor(0);
    AdUtil.a(this);
    WebSettings localWebSettings = getSettings();
    localWebSettings.setSupportMultipleWindows(false);
    localWebSettings.setJavaScriptEnabled(true);
    localWebSettings.setSavePassword(false);
    setDownloadListener(new DownloadListener()
    {
      public void onDownloadStart(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4, long paramAnonymousLong)
      {
        try
        {
          Intent localIntent = new Intent("android.intent.action.VIEW");
          localIntent.setDataAndType(Uri.parse(paramAnonymousString1), paramAnonymousString4);
          AdActivity localAdActivity = AdWebView.this.d();
          if ((localAdActivity != null) && (AdUtil.a(localIntent, localAdActivity))) {
            localAdActivity.startActivity(localIntent);
          }
          return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          b.a("Couldn't find an Activity to view url/mimetype: " + paramAnonymousString1 + " / " + paramAnonymousString4);
          return;
        }
        catch (Throwable localThrowable)
        {
          b.b("Unknown error trying to start activity to view URL: " + paramAnonymousString1, localThrowable);
        }
      }
    });
    if (AdUtil.a >= 11) {
      g.a(localWebSettings, paramm);
    }
    setScrollBarStyle(33554432);
    if (AdUtil.a >= 14) {
      setWebChromeClient(new h.a(paramm));
    }
    while (AdUtil.a < 11) {
      return;
    }
    setWebChromeClient(new g.a(paramm));
  }
  
  public void a()
  {
    AdActivity localAdActivity = d();
    if (localAdActivity != null) {
      localAdActivity.finish();
    }
  }
  
  public void b()
  {
    if (AdUtil.a >= 11) {
      g.a(this);
    }
    this.d = true;
  }
  
  public void c()
  {
    if ((this.d) && (AdUtil.a >= 11)) {
      g.b(this);
    }
    this.d = false;
  }
  
  public AdActivity d()
  {
    if (this.a != null) {
      return (AdActivity)this.a.get();
    }
    return null;
  }
  
  public void destroy()
  {
    try
    {
      super.destroy();
      setWebViewClient(new WebViewClient());
      return;
    }
    catch (Throwable localThrowable)
    {
      b.b("An error occurred while destroying an AdWebView:", localThrowable);
    }
  }
  
  public boolean e()
  {
    return this.d;
  }
  
  public void loadDataWithBaseURL(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    try
    {
      super.loadDataWithBaseURL(paramString1, paramString2, paramString3, paramString4, paramString5);
      return;
    }
    catch (Throwable localThrowable)
    {
      b.b("An error occurred while loading data in AdWebView:", localThrowable);
    }
  }
  
  public void loadUrl(String paramString)
  {
    try
    {
      super.loadUrl(paramString);
      return;
    }
    catch (Throwable localThrowable)
    {
      b.b("An error occurred while loading a URL in AdWebView:", localThrowable);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 2147483647;
    try
    {
      if (isInEditMode()) {
        super.onMeasure(paramInt1, paramInt2);
      }
      for (;;)
      {
        return;
        if ((this.b != null) && (!this.c)) {
          break;
        }
        super.onMeasure(paramInt1, paramInt2);
      }
      j = View.MeasureSpec.getMode(paramInt1);
    }
    finally {}
    int j;
    int k = View.MeasureSpec.getSize(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    int n = View.MeasureSpec.getSize(paramInt2);
    float f = getContext().getResources().getDisplayMetrics().density;
    int i1 = (int)(f * this.b.getWidth());
    int i2 = (int)(f * this.b.getHeight());
    if (j != -2147483648) {
      if (j == 1073741824) {
        break label227;
      }
    }
    for (;;)
    {
      label135:
      b.e("Not enough space to show ad! Wants: <" + i1 + ", " + i2 + ">, Has: <" + k + ", " + n + ">");
      setVisibility(8);
      setMeasuredDimension(k, n);
      break;
      label227:
      label231:
      do
      {
        setMeasuredDimension(i1, i2);
        break;
        int i3 = i;
        break label231;
        i3 = k;
        if ((m == -2147483648) || (m == 1073741824)) {
          i = n;
        }
        if (i1 - f * 6.0F > i3) {
          break label135;
        }
      } while (i2 <= i);
    }
  }
  
  public void setAdActivity(AdActivity paramAdActivity)
  {
    this.a = new WeakReference(paramAdActivity);
  }
  
  public void setAdSize(AdSize paramAdSize)
  {
    try
    {
      this.b = paramAdSize;
      requestLayout();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void setIsExpandedMraid(boolean paramBoolean)
  {
    this.c = paramBoolean;
  }
  
  public void stopLoading()
  {
    try
    {
      super.stopLoading();
      return;
    }
    catch (Throwable localThrowable)
    {
      b.d("An error occurred while stopping loading in AdWebView:", localThrowable);
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.internal.AdWebView
 * JD-Core Version:    0.7.0.1
 */
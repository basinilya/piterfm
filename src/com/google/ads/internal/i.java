package com.google.ads.internal;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.ads.AdActivity;
import com.google.ads.n;
import com.google.ads.util.AdUtil;
import com.google.ads.util.b;
import com.google.ads.util.f;
import com.google.ads.util.g.b;
import java.util.HashMap;
import java.util.Map;

public class i
  extends WebViewClient
{
  private static final a c = (a)a.a.b();
  protected d a;
  protected boolean b;
  private Map<String, n> d;
  private boolean e;
  private boolean f;
  private boolean g;
  private boolean h;
  
  protected i(d paramd, Map<String, n> paramMap, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.a = paramd;
    this.d = paramMap;
    this.e = paramBoolean1;
    this.f = paramBoolean2;
    this.b = false;
    this.g = false;
    this.h = false;
  }
  
  public static i a(d paramd, Map<String, n> paramMap, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (AdUtil.a >= 11) {
      return new g.b(paramd, paramMap, paramBoolean1, paramBoolean2);
    }
    return new i(paramd, paramMap, paramBoolean1, paramBoolean2);
  }
  
  public void a(boolean paramBoolean)
  {
    this.b = paramBoolean;
  }
  
  public void b(boolean paramBoolean)
  {
    this.f = paramBoolean;
  }
  
  public void c(boolean paramBoolean)
  {
    this.g = paramBoolean;
  }
  
  public void d(boolean paramBoolean)
  {
    this.h = paramBoolean;
  }
  
  public void onPageFinished(WebView paramWebView, String paramString)
  {
    if (this.g)
    {
      c localc = this.a.j();
      if (localc == null) {
        break label48;
      }
      localc.c();
    }
    for (;;)
    {
      this.g = false;
      if (this.h)
      {
        c.a(paramWebView);
        this.h = false;
      }
      return;
      label48:
      b.a("adLoader was null while trying to setFinishedLoadingHtml().");
    }
  }
  
  public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
  {
    b.a("shouldOverrideUrlLoading(\"" + paramString + "\")");
    Uri localUri = Uri.parse(paramString);
    HashMap localHashMap1 = AdUtil.b(localUri);
    if (localHashMap1 != null)
    {
      String str = (String)localHashMap1.get("ai");
      if (str != null) {
        this.a.m().a(str);
      }
    }
    if (c.a(localUri))
    {
      c.a(this.a, this.d, localUri, paramWebView);
      return true;
    }
    if (this.f)
    {
      if (AdUtil.a(localUri)) {
        return super.shouldOverrideUrlLoading(paramWebView, paramString);
      }
      HashMap localHashMap3 = new HashMap();
      localHashMap3.put("u", paramString);
      AdActivity.launchAdActivity(this.a, new e("intent", localHashMap3));
      return true;
    }
    if (this.e)
    {
      HashMap localHashMap2 = new HashMap();
      localHashMap2.put("u", localUri.toString());
      AdActivity.launchAdActivity(this.a, new e("intent", localHashMap2));
      return true;
    }
    b.e("URL is not a GMSG and can't handle URL: " + paramString);
    return true;
  }
}


/* Location:
 * Qualified Name:     com.google.ads.internal.i
 * JD-Core Version:    0.7.0.1
 */
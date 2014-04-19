package com.google.ads;

import android.webkit.WebView;
import com.google.ads.internal.AdWebView;
import com.google.ads.internal.a;
import com.google.ads.internal.d;
import com.google.ads.util.b;
import com.google.ads.util.f;
import java.util.HashMap;

public class s
  implements n
{
  private static final a a = (a)a.a.b();
  
  public void a(d paramd, HashMap<String, String> paramHashMap, WebView paramWebView)
  {
    String str = (String)paramHashMap.get("js");
    if (str == null)
    {
      b.b("Could not get the JS to evaluate.");
      return;
    }
    AdActivity localAdActivity;
    if ((paramWebView instanceof AdWebView))
    {
      localAdActivity = ((AdWebView)paramWebView).d();
      if (localAdActivity == null) {
        b.b("Could not get the AdActivity from the AdWebView.");
      }
    }
    else
    {
      b.b("Trying to evaluate JS in a WebView that isn't an AdWebView");
      return;
    }
    AdWebView localAdWebView = localAdActivity.getOpeningAdWebView();
    if (localAdWebView == null)
    {
      b.b("Could not get the opening WebView.");
      return;
    }
    a.a(localAdWebView, str);
  }
}


/* Location:
 * Qualified Name:     com.google.ads.s
 * JD-Core Version:    0.7.0.1
 */
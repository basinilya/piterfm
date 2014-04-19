package com.google.ads;

import android.content.Context;
import android.webkit.WebView;
import com.google.ads.internal.d;
import com.google.ads.util.b;
import java.util.HashMap;

public class t
  implements n
{
  protected Runnable a(String paramString, WebView paramWebView)
  {
    return new ab(paramString, paramWebView.getContext().getApplicationContext());
  }
  
  public void a(d paramd, HashMap<String, String> paramHashMap, WebView paramWebView)
  {
    String str = (String)paramHashMap.get("u");
    if (str == null)
    {
      b.e("Could not get URL from click gmsg.");
      return;
    }
    new Thread(a(str, paramWebView)).start();
  }
}


/* Location:
 * Qualified Name:     com.google.ads.t
 * JD-Core Version:    0.7.0.1
 */
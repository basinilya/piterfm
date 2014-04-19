package com.google.ads;

import android.net.Uri;
import android.webkit.WebView;
import com.google.ads.internal.d;
import com.google.ads.internal.g;
import com.google.ads.util.b;
import java.util.HashMap;
import java.util.Locale;

public class q
  extends t
{
  public void a(d paramd, HashMap<String, String> paramHashMap, WebView paramWebView)
  {
    String str1 = (String)paramHashMap.get("u");
    if (str1 == null)
    {
      b.e("Could not get URL from click gmsg.");
      return;
    }
    g localg = paramd.m();
    if (localg != null)
    {
      Uri localUri = Uri.parse(str1);
      String str2 = localUri.getHost();
      if ((str2 != null) && (str2.toLowerCase(Locale.US).endsWith(".admob.com")))
      {
        String str3 = localUri.getPath();
        String str4 = null;
        if (str3 != null)
        {
          String[] arrayOfString = str3.split("/");
          int i = arrayOfString.length;
          str4 = null;
          if (i >= 4) {
            str4 = arrayOfString[2] + "/" + arrayOfString[3];
          }
        }
        localg.b(str4);
      }
    }
    super.a(paramd, paramHashMap, paramWebView);
  }
}


/* Location:
 * Qualified Name:     com.google.ads.q
 * JD-Core Version:    0.7.0.1
 */
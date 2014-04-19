package com.google.ads;

import android.webkit.WebView;
import com.google.ads.internal.c;
import com.google.ads.internal.d;
import com.google.ads.util.b;
import java.util.HashMap;

public class v
  implements n
{
  public void a(d paramd, HashMap<String, String> paramHashMap, WebView paramWebView)
  {
    String str1 = (String)paramHashMap.get("url");
    String str2 = (String)paramHashMap.get("type");
    String str3 = (String)paramHashMap.get("afma_notify_dt");
    boolean bool = "1".equals(paramHashMap.get("drt_include"));
    b.c("Received ad url: <url: \"" + str1 + "\" type: \"" + str2 + "\" afmaNotifyDt: \"" + str3 + "\">");
    c localc = paramd.j();
    if (localc != null)
    {
      localc.c(bool);
      localc.d(str1);
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.v
 * JD-Core Version:    0.7.0.1
 */
package com.google.ads;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import com.google.ads.internal.d;
import com.google.ads.internal.e;
import com.google.ads.util.i.b;
import com.google.ads.util.i.d;
import java.util.HashMap;

public class ah
  implements n
{
  public void a(d paramd, HashMap<String, String> paramHashMap, WebView paramWebView)
  {
    Context localContext = (Context)paramd.h().d.a();
    String str = (String)paramHashMap.get("a");
    if (str != null) {
      if (str.equals("resize")) {
        af.a(paramWebView, (String)paramHashMap.get("u"));
      }
    }
    do
    {
      return;
      if (str.equals("state"))
      {
        af.a((Activity)paramd.h().c.a(), paramWebView, (String)paramHashMap.get("u"));
        return;
      }
      Intent localIntent = new Intent();
      localIntent.setComponent(new ComponentName("com.google.android.apps.plus", "com.google.android.apps.circles.platform.PlusOneActivity"));
      if (ag.a(localIntent, localContext)) {
        break;
      }
    } while (!ag.a(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.plus")), localContext));
    if ((!TextUtils.isEmpty((CharSequence)paramHashMap.get("d"))) && (!TextUtils.isEmpty((CharSequence)paramHashMap.get("o"))) && (!TextUtils.isEmpty((CharSequence)paramHashMap.get("c"))))
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localContext);
      localBuilder.setMessage((CharSequence)paramHashMap.get("d")).setPositiveButton((CharSequence)paramHashMap.get("o"), new c(paramd)).setNegativeButton((CharSequence)paramHashMap.get("c"), new a());
      localBuilder.create().show();
      return;
    }
    HashMap localHashMap = new HashMap();
    localHashMap.put("u", "market://details?id=com.google.android.apps.plus");
    AdActivity.launchAdActivity(paramd, new e("intent", localHashMap));
    return;
    AdActivity.launchAdActivity(paramd, new e("plusone", paramHashMap));
  }
  
  private static class a
    implements DialogInterface.OnClickListener
  {
    public void onClick(DialogInterface paramDialogInterface, int paramInt) {}
  }
  
  public static enum b
  {
    public String c;
    
    static
    {
      b[] arrayOfb = new b[2];
      arrayOfb[0] = a;
      arrayOfb[1] = b;
      d = arrayOfb;
    }
    
    private b(String paramString)
    {
      this.c = paramString;
    }
  }
  
  private static class c
    implements DialogInterface.OnClickListener
  {
    private d a;
    
    public c(d paramd)
    {
      this.a = paramd;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("u", "market://details?id=com.google.android.apps.plus");
      AdActivity.launchAdActivity(this.a, new e("intent", localHashMap));
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.ah
 * JD-Core Version:    0.7.0.1
 */
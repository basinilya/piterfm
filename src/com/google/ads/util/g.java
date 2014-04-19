package com.google.ads.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.ads.AdActivity;
import com.google.ads.internal.AdWebView;
import com.google.ads.internal.d;
import com.google.ads.internal.i;
import com.google.ads.l;
import com.google.ads.l.a;
import com.google.ads.m;
import com.google.ads.n;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@TargetApi(11)
public class g
{
  public static void a(View paramView)
  {
    paramView.setLayerType(1, null);
  }
  
  public static void a(Window paramWindow)
  {
    paramWindow.setFlags(16777216, 16777216);
  }
  
  public static void a(WebSettings paramWebSettings, m paramm)
  {
    Context localContext = (Context)paramm.d.a();
    l.a locala = (l.a)((l)paramm.a.a()).a.a();
    paramWebSettings.setAppCacheEnabled(true);
    paramWebSettings.setAppCacheMaxSize(((Long)locala.f.a()).longValue());
    paramWebSettings.setAppCachePath(new File(localContext.getCacheDir(), "admob").getAbsolutePath());
    paramWebSettings.setDatabaseEnabled(true);
    paramWebSettings.setDatabasePath(localContext.getDatabasePath("admob").getAbsolutePath());
    paramWebSettings.setDomStorageEnabled(true);
    paramWebSettings.setSupportZoom(true);
    paramWebSettings.setBuiltInZoomControls(true);
    paramWebSettings.setDisplayZoomControls(false);
  }
  
  public static void b(View paramView)
  {
    paramView.setLayerType(0, null);
  }
  
  public static class a
    extends WebChromeClient
  {
    private final m a;
    
    public a(m paramm)
    {
      this.a = paramm;
    }
    
    private static void a(AlertDialog.Builder paramBuilder, Context paramContext, String paramString1, String paramString2, JsPromptResult paramJsPromptResult)
    {
      LinearLayout localLinearLayout = new LinearLayout(paramContext);
      localLinearLayout.setOrientation(1);
      TextView localTextView = new TextView(paramContext);
      localTextView.setText(paramString1);
      final EditText localEditText = new EditText(paramContext);
      localEditText.setText(paramString2);
      localLinearLayout.addView(localTextView);
      localLinearLayout.addView(localEditText);
      paramBuilder.setView(localLinearLayout).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          this.a.confirm(localEditText.getText().toString());
        }
      }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          this.a.cancel();
        }
      }).setOnCancelListener(new DialogInterface.OnCancelListener()
      {
        public void onCancel(DialogInterface paramAnonymousDialogInterface)
        {
          this.a.cancel();
        }
      }).create().show();
    }
    
    private static void a(AlertDialog.Builder paramBuilder, String paramString, JsResult paramJsResult)
    {
      paramBuilder.setMessage(paramString).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          this.a.confirm();
        }
      }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          this.a.cancel();
        }
      }).setOnCancelListener(new DialogInterface.OnCancelListener()
      {
        public void onCancel(DialogInterface paramAnonymousDialogInterface)
        {
          this.a.cancel();
        }
      }).create().show();
    }
    
    private static boolean a(WebView paramWebView, String paramString1, String paramString2, String paramString3, JsResult paramJsResult, JsPromptResult paramJsPromptResult, boolean paramBoolean)
    {
      if ((paramWebView instanceof AdWebView))
      {
        AdActivity localAdActivity = ((AdWebView)paramWebView).d();
        if (localAdActivity != null)
        {
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(localAdActivity);
          localBuilder.setTitle(paramString1);
          if (paramBoolean) {
            a(localBuilder, localAdActivity, paramString2, paramString3, paramJsPromptResult);
          }
          for (;;)
          {
            return true;
            a(localBuilder, paramString2, paramJsResult);
          }
        }
      }
      return false;
    }
    
    public void onCloseWindow(WebView paramWebView)
    {
      if ((paramWebView instanceof AdWebView)) {
        ((AdWebView)paramWebView).a();
      }
    }
    
    public boolean onConsoleMessage(ConsoleMessage paramConsoleMessage)
    {
      String str = "JS: " + paramConsoleMessage.message() + " (" + paramConsoleMessage.sourceId() + ":" + paramConsoleMessage.lineNumber() + ")";
      switch (g.1.a[paramConsoleMessage.messageLevel().ordinal()])
      {
      }
      for (;;)
      {
        return super.onConsoleMessage(paramConsoleMessage);
        b.b(str);
        continue;
        b.e(str);
        continue;
        b.c(str);
        continue;
        b.a(str);
      }
    }
    
    public void onExceededDatabaseQuota(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3, WebStorage.QuotaUpdater paramQuotaUpdater)
    {
      l.a locala = (l.a)((l)this.a.a.a()).a.a();
      long l = ((Long)locala.i.a()).longValue() - paramLong3;
      if (l <= 0L)
      {
        paramQuotaUpdater.updateQuota(paramLong1);
        return;
      }
      if (paramLong1 == 0L) {
        if ((paramLong2 > l) || (paramLong2 > ((Long)locala.j.a()).longValue())) {}
      }
      for (;;)
      {
        paramQuotaUpdater.updateQuota(paramLong2);
        return;
        paramLong2 = 0L;
        continue;
        if (paramLong2 == 0L)
        {
          paramLong2 = Math.min(paramLong1 + Math.min(((Long)locala.k.a()).longValue(), l), ((Long)locala.j.a()).longValue());
        }
        else
        {
          if (paramLong2 <= Math.min(((Long)locala.j.a()).longValue() - paramLong1, l)) {
            paramLong1 += paramLong2;
          }
          paramLong2 = paramLong1;
        }
      }
    }
    
    public boolean onJsAlert(WebView paramWebView, String paramString1, String paramString2, JsResult paramJsResult)
    {
      return a(paramWebView, paramString1, paramString2, null, paramJsResult, null, false);
    }
    
    public boolean onJsBeforeUnload(WebView paramWebView, String paramString1, String paramString2, JsResult paramJsResult)
    {
      return a(paramWebView, paramString1, paramString2, null, paramJsResult, null, false);
    }
    
    public boolean onJsConfirm(WebView paramWebView, String paramString1, String paramString2, JsResult paramJsResult)
    {
      return a(paramWebView, paramString1, paramString2, null, paramJsResult, null, false);
    }
    
    public boolean onJsPrompt(WebView paramWebView, String paramString1, String paramString2, String paramString3, JsPromptResult paramJsPromptResult)
    {
      return a(paramWebView, paramString1, paramString2, paramString3, null, paramJsPromptResult, true);
    }
    
    public void onReachedMaxAppCacheSize(long paramLong1, long paramLong2, WebStorage.QuotaUpdater paramQuotaUpdater)
    {
      l.a locala = (l.a)((l)this.a.a.a()).a.a();
      long l1 = ((Long)locala.h.a()).longValue() - paramLong2;
      long l2 = paramLong1 + ((Long)locala.g.a()).longValue();
      if (l1 < l2)
      {
        paramQuotaUpdater.updateQuota(0L);
        return;
      }
      paramQuotaUpdater.updateQuota(l2);
    }
    
    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
    {
      paramCustomViewCallback.onCustomViewHidden();
    }
  }
  
  public static class b
    extends i
  {
    public b(d paramd, Map<String, n> paramMap, boolean paramBoolean1, boolean paramBoolean2)
    {
      super(paramMap, paramBoolean1, paramBoolean2);
    }
    
    private static WebResourceResponse a(String paramString, Context paramContext)
      throws IOException
    {
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(paramString).openConnection();
      try
      {
        AdUtil.a(localHttpURLConnection, paramContext.getApplicationContext());
        localHttpURLConnection.connect();
        WebResourceResponse localWebResourceResponse = new WebResourceResponse("application/javascript", "UTF-8", new ByteArrayInputStream(AdUtil.a(new InputStreamReader(localHttpURLConnection.getInputStream())).getBytes("UTF-8")));
        return localWebResourceResponse;
      }
      finally
      {
        localHttpURLConnection.disconnect();
      }
    }
    
    /* Error */
    public WebResourceResponse shouldInterceptRequest(WebView paramWebView, String paramString)
    {
      // Byte code:
      //   0: ldc 79
      //   2: new 81	java/io/File
      //   5: dup
      //   6: aload_2
      //   7: invokespecial 82	java/io/File:<init>	(Ljava/lang/String;)V
      //   10: invokevirtual 86	java/io/File:getName	()Ljava/lang/String;
      //   13: invokevirtual 90	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
      //   16: ifeq +141 -> 157
      //   19: aload_0
      //   20: getfield 93	com/google/ads/util/g$b:a	Lcom/google/ads/internal/d;
      //   23: invokevirtual 99	com/google/ads/internal/d:j	()Lcom/google/ads/internal/c;
      //   26: astore 5
      //   28: aload 5
      //   30: ifnull +107 -> 137
      //   33: aload 5
      //   35: iconst_1
      //   36: invokevirtual 105	com/google/ads/internal/c:b	(Z)V
      //   39: aload_0
      //   40: getfield 93	com/google/ads/util/g$b:a	Lcom/google/ads/internal/d;
      //   43: invokevirtual 109	com/google/ads/internal/d:h	()Lcom/google/ads/m;
      //   46: getfield 114	com/google/ads/m:a	Lcom/google/ads/util/i$b;
      //   49: invokevirtual 119	com/google/ads/util/i$b:a	()Ljava/lang/Object;
      //   52: checkcast 121	com/google/ads/l
      //   55: getfield 122	com/google/ads/l:a	Lcom/google/ads/util/i$b;
      //   58: invokevirtual 119	com/google/ads/util/i$b:a	()Ljava/lang/Object;
      //   61: checkcast 124	com/google/ads/l$a
      //   64: astore 6
      //   66: aload_0
      //   67: getfield 93	com/google/ads/util/g$b:a	Lcom/google/ads/internal/d;
      //   70: invokevirtual 109	com/google/ads/internal/d:h	()Lcom/google/ads/m;
      //   73: invokevirtual 127	com/google/ads/m:b	()Z
      //   76: ifne +139 -> 215
      //   79: aload_0
      //   80: getfield 130	com/google/ads/util/g$b:b	Z
      //   83: ifeq +81 -> 164
      //   86: aload 6
      //   88: getfield 134	com/google/ads/l$a:d	Lcom/google/ads/util/i$c;
      //   91: invokevirtual 137	com/google/ads/util/i$c:a	()Ljava/lang/Object;
      //   94: checkcast 60	java/lang/String
      //   97: astore 10
      //   99: new 139	java/lang/StringBuilder
      //   102: dup
      //   103: invokespecial 141	java/lang/StringBuilder:<init>	()V
      //   106: ldc 143
      //   108: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   111: aload 10
      //   113: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   116: ldc 149
      //   118: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   121: invokevirtual 152	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   124: invokestatic 156	com/google/ads/util/b:a	(Ljava/lang/String;)V
      //   127: aload 10
      //   129: aload_1
      //   130: invokevirtual 161	android/webkit/WebView:getContext	()Landroid/content/Context;
      //   133: invokestatic 163	com/google/ads/util/g$b:a	(Ljava/lang/String;Landroid/content/Context;)Landroid/webkit/WebResourceResponse;
      //   136: areturn
      //   137: aload_0
      //   138: getfield 93	com/google/ads/util/g$b:a	Lcom/google/ads/internal/d;
      //   141: iconst_1
      //   142: invokevirtual 165	com/google/ads/internal/d:a	(Z)V
      //   145: goto -106 -> 39
      //   148: astore 4
      //   150: ldc 167
      //   152: aload 4
      //   154: invokestatic 170	com/google/ads/util/b:d	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   157: aload_0
      //   158: aload_1
      //   159: aload_2
      //   160: invokespecial 172	com/google/ads/internal/i:shouldInterceptRequest	(Landroid/webkit/WebView;Ljava/lang/String;)Landroid/webkit/WebResourceResponse;
      //   163: areturn
      //   164: aload 6
      //   166: getfield 175	com/google/ads/l$a:c	Lcom/google/ads/util/i$c;
      //   169: invokevirtual 137	com/google/ads/util/i$c:a	()Ljava/lang/Object;
      //   172: checkcast 60	java/lang/String
      //   175: astore 9
      //   177: new 139	java/lang/StringBuilder
      //   180: dup
      //   181: invokespecial 141	java/lang/StringBuilder:<init>	()V
      //   184: ldc 143
      //   186: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   189: aload 9
      //   191: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   194: ldc 149
      //   196: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   199: invokevirtual 152	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   202: invokestatic 156	com/google/ads/util/b:a	(Ljava/lang/String;)V
      //   205: aload 9
      //   207: aload_1
      //   208: invokevirtual 161	android/webkit/WebView:getContext	()Landroid/content/Context;
      //   211: invokestatic 163	com/google/ads/util/g$b:a	(Ljava/lang/String;Landroid/content/Context;)Landroid/webkit/WebResourceResponse;
      //   214: areturn
      //   215: aload 6
      //   217: getfield 178	com/google/ads/l$a:e	Lcom/google/ads/util/i$c;
      //   220: invokevirtual 137	com/google/ads/util/i$c:a	()Ljava/lang/Object;
      //   223: checkcast 60	java/lang/String
      //   226: astore 7
      //   228: new 139	java/lang/StringBuilder
      //   231: dup
      //   232: invokespecial 141	java/lang/StringBuilder:<init>	()V
      //   235: ldc 143
      //   237: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   240: aload 7
      //   242: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   245: ldc 149
      //   247: invokevirtual 147	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   250: invokevirtual 152	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   253: invokestatic 156	com/google/ads/util/b:a	(Ljava/lang/String;)V
      //   256: aload 7
      //   258: aload_1
      //   259: invokevirtual 161	android/webkit/WebView:getContext	()Landroid/content/Context;
      //   262: invokestatic 163	com/google/ads/util/g$b:a	(Ljava/lang/String;Landroid/content/Context;)Landroid/webkit/WebResourceResponse;
      //   265: astore 8
      //   267: aload 8
      //   269: areturn
      //   270: astore_3
      //   271: ldc 180
      //   273: aload_3
      //   274: invokestatic 182	com/google/ads/util/b:b	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   277: goto -120 -> 157
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	280	0	this	b
      //   0	280	1	paramWebView	WebView
      //   0	280	2	paramString	String
      //   270	4	3	localThrowable	java.lang.Throwable
      //   148	5	4	localIOException	IOException
      //   26	8	5	localc	com.google.ads.internal.c
      //   64	152	6	locala	l.a
      //   226	31	7	str1	String
      //   265	3	8	localWebResourceResponse	WebResourceResponse
      //   175	31	9	str2	String
      //   97	31	10	str3	String
      // Exception table:
      //   from	to	target	type
      //   0	28	148	java/io/IOException
      //   33	39	148	java/io/IOException
      //   39	137	148	java/io/IOException
      //   137	145	148	java/io/IOException
      //   164	215	148	java/io/IOException
      //   215	267	148	java/io/IOException
      //   0	28	270	java/lang/Throwable
      //   33	39	270	java/lang/Throwable
      //   39	137	270	java/lang/Throwable
      //   137	145	270	java/lang/Throwable
      //   164	215	270	java/lang/Throwable
      //   215	267	270	java/lang/Throwable
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.util.g
 * JD-Core Version:    0.7.0.1
 */
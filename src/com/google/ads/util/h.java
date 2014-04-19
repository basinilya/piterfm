package com.google.ads.util;

import android.annotation.TargetApi;
import android.view.View;
import android.webkit.WebChromeClient.CustomViewCallback;
import com.google.ads.m;

@TargetApi(14)
public class h
{
  public static class a
    extends g.a
  {
    public a(m paramm)
    {
      super();
    }
    
    public void onShowCustomView(View paramView, int paramInt, WebChromeClient.CustomViewCallback paramCustomViewCallback)
    {
      paramCustomViewCallback.onCustomViewHidden();
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.util.h
 * JD-Core Version:    0.7.0.1
 */
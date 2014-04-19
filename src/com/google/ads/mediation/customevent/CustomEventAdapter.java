package com.google.ads.mediation.customevent;

import android.app.Activity;
import android.view.View;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.g;
import com.google.ads.mediation.EmptyNetworkExtras;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationBannerListener;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationInterstitialListener;
import com.google.ads.util.a;
import com.google.ads.util.b;

public class CustomEventAdapter
  implements MediationBannerAdapter<EmptyNetworkExtras, CustomEventServerParameters>, MediationInterstitialAdapter<EmptyNetworkExtras, CustomEventServerParameters>
{
  private String a;
  private a b = null;
  private CustomEventInterstitial c = null;
  
  private <T> T a(String paramString1, Class<T> paramClass, String paramString2)
  {
    try
    {
      Object localObject = g.a(paramString1, paramClass);
      return localObject;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      a("Make sure you created a visible class named: " + paramString1 + ". ", localClassNotFoundException);
      return null;
    }
    catch (ClassCastException localClassCastException)
    {
      for (;;)
      {
        a("Make sure your custom event implements the " + paramClass.getName() + " interface.", localClassCastException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        a("Make sure the default constructor for class " + paramString1 + " is visible. ", localIllegalAccessException);
      }
    }
    catch (InstantiationException localInstantiationException)
    {
      for (;;)
      {
        a("Make sure the name " + paramString1 + " does not denote an abstract class or an interface.", localInstantiationException);
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        a("", localThrowable);
      }
    }
  }
  
  private void a(String paramString, Throwable paramThrowable)
  {
    b.b("Error during processing of custom event with label: '" + this.a + "'. Skipping custom event. " + paramString, paramThrowable);
  }
  
  public void destroy() {}
  
  public Class<EmptyNetworkExtras> getAdditionalParametersType()
  {
    return EmptyNetworkExtras.class;
  }
  
  public View getBannerView()
  {
    a.b(this.b);
    return this.b.a();
  }
  
  public Class<CustomEventServerParameters> getServerParametersType()
  {
    return CustomEventServerParameters.class;
  }
  
  public void requestBannerAd(MediationBannerListener paramMediationBannerListener, Activity paramActivity, CustomEventServerParameters paramCustomEventServerParameters, AdSize paramAdSize, MediationAdRequest paramMediationAdRequest, EmptyNetworkExtras paramEmptyNetworkExtras)
  {
    a.a(this.a);
    this.a = paramCustomEventServerParameters.label;
    String str1 = paramCustomEventServerParameters.className;
    String str2 = paramCustomEventServerParameters.parameter;
    CustomEventBanner localCustomEventBanner = (CustomEventBanner)a(str1, CustomEventBanner.class, this.a);
    if (localCustomEventBanner == null)
    {
      paramMediationBannerListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
      return;
    }
    a.a(this.b);
    this.b = new a(paramMediationBannerListener);
    try
    {
      localCustomEventBanner.requestBannerAd(this.b, paramActivity, this.a, str2, paramAdSize, paramMediationAdRequest);
      return;
    }
    catch (Throwable localThrowable)
    {
      a("", localThrowable);
      paramMediationBannerListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
    }
  }
  
  public void requestInterstitialAd(MediationInterstitialListener paramMediationInterstitialListener, Activity paramActivity, CustomEventServerParameters paramCustomEventServerParameters, MediationAdRequest paramMediationAdRequest, EmptyNetworkExtras paramEmptyNetworkExtras)
  {
    a.a(this.a);
    this.a = paramCustomEventServerParameters.label;
    String str1 = paramCustomEventServerParameters.className;
    String str2 = paramCustomEventServerParameters.parameter;
    this.c = ((CustomEventInterstitial)a(str1, CustomEventInterstitial.class, this.a));
    if (this.c == null)
    {
      paramMediationInterstitialListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
      return;
    }
    try
    {
      this.c.requestInterstitialAd(new b(paramMediationInterstitialListener), paramActivity, this.a, str2, paramMediationAdRequest);
      return;
    }
    catch (Throwable localThrowable)
    {
      a("", localThrowable);
      paramMediationInterstitialListener.onFailedToReceiveAd(this, AdRequest.ErrorCode.INTERNAL_ERROR);
    }
  }
  
  public void showInterstitial()
  {
    a.b(this.c);
    try
    {
      this.c.showInterstitial();
      return;
    }
    catch (Throwable localThrowable)
    {
      b.b("Exception when showing custom event labeled '" + this.a + "'.", localThrowable);
    }
  }
  
  private class a
    implements CustomEventBannerListener
  {
    private View b;
    private final MediationBannerListener c;
    
    public a(MediationBannerListener paramMediationBannerListener)
    {
      this.c = paramMediationBannerListener;
    }
    
    private String b()
    {
      return "Banner custom event labeled '" + CustomEventAdapter.a(CustomEventAdapter.this) + "'";
    }
    
    public View a()
    {
      try
      {
        View localView = this.b;
        return localView;
      }
      finally
      {
        localObject = finally;
        throw localObject;
      }
    }
    
    public void onClick()
    {
      b.a(b() + " called onClick().");
      this.c.onClick(CustomEventAdapter.this);
    }
    
    public void onDismissScreen()
    {
      b.a(b() + " called onDismissScreen().");
      this.c.onDismissScreen(CustomEventAdapter.this);
    }
    
    public void onFailedToReceiveAd()
    {
      b.a(b() + " called onFailedToReceiveAd().");
      this.c.onFailedToReceiveAd(CustomEventAdapter.this, AdRequest.ErrorCode.NO_FILL);
    }
    
    public void onLeaveApplication()
    {
      try
      {
        b.a(b() + " called onLeaveApplication().");
        this.c.onLeaveApplication(CustomEventAdapter.this);
        return;
      }
      finally
      {
        localObject = finally;
        throw localObject;
      }
    }
    
    public void onPresentScreen()
    {
      b.a(b() + " called onPresentScreen().");
      this.c.onPresentScreen(CustomEventAdapter.this);
    }
    
    public void onReceivedAd(View paramView)
    {
      try
      {
        b.a(b() + " called onReceivedAd.");
        this.b = paramView;
        this.c.onReceivedAd(CustomEventAdapter.this);
        return;
      }
      finally
      {
        localObject = finally;
        throw localObject;
      }
    }
  }
  
  private class b
    implements CustomEventInterstitialListener
  {
    private final MediationInterstitialListener b;
    
    public b(MediationInterstitialListener paramMediationInterstitialListener)
    {
      this.b = paramMediationInterstitialListener;
    }
    
    private String a()
    {
      return "Interstitial custom event labeled '" + CustomEventAdapter.a(CustomEventAdapter.this) + "'";
    }
    
    public void onDismissScreen()
    {
      b.a(a() + " called onDismissScreen().");
      this.b.onDismissScreen(CustomEventAdapter.this);
    }
    
    public void onFailedToReceiveAd()
    {
      b.a(a() + " called onFailedToReceiveAd().");
      this.b.onFailedToReceiveAd(CustomEventAdapter.this, AdRequest.ErrorCode.NO_FILL);
    }
    
    public void onLeaveApplication()
    {
      try
      {
        b.a(a() + " called onLeaveApplication().");
        this.b.onLeaveApplication(CustomEventAdapter.this);
        return;
      }
      finally
      {
        localObject = finally;
        throw localObject;
      }
    }
    
    public void onPresentScreen()
    {
      b.a(a() + " called onPresentScreen().");
      this.b.onPresentScreen(CustomEventAdapter.this);
    }
    
    public void onReceivedAd()
    {
      b.a(a() + " called onReceivedAd.");
      this.b.onReceivedAd(CustomEventAdapter.this);
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.mediation.customevent.CustomEventAdapter
 * JD-Core Version:    0.7.0.1
 */
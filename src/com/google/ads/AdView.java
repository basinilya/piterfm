package com.google.ads;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.ads.internal.d;
import com.google.ads.internal.j;
import com.google.ads.util.AdUtil;
import com.google.ads.util.i.c;
import java.util.HashSet;
import java.util.Set;

public class AdView
  extends RelativeLayout
  implements Ad
{
  private m a;
  private d b;
  
  public AdView(Activity paramActivity, AdSize paramAdSize, String paramString)
  {
    super(paramActivity.getApplicationContext());
    try
    {
      a(paramActivity, paramAdSize, null);
      b(paramActivity, paramAdSize, null);
      a(paramActivity, paramAdSize, paramString);
      return;
    }
    catch (com.google.ads.internal.b localb)
    {
      a(paramActivity, localb.c("Could not initialize AdView"), paramAdSize, null);
      localb.a("Could not initialize AdView");
    }
  }
  
  protected AdView(Activity paramActivity, AdSize[] paramArrayOfAdSize, String paramString)
  {
    this(paramActivity, new AdSize(0, 0), paramString);
    a(paramArrayOfAdSize);
  }
  
  public AdView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    a(paramContext, paramAttributeSet);
  }
  
  public AdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet);
  }
  
  private int a(Context paramContext, int paramInt)
  {
    return (int)TypedValue.applyDimension(1, paramInt, paramContext.getResources().getDisplayMetrics());
  }
  
  private String a(String paramString, Context paramContext, AttributeSet paramAttributeSet, boolean paramBoolean1, boolean paramBoolean2)
    throws com.google.ads.internal.b
  {
    String str1 = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/lib/com.google.ads", paramString);
    String str3;
    String str4;
    TypedValue localTypedValue;
    if ((str1 != null) && (str1.startsWith("@string/")) && (paramBoolean1))
    {
      str3 = str1.substring("@string/".length());
      str4 = paramContext.getPackageName();
      localTypedValue = new TypedValue();
    }
    for (;;)
    {
      try
      {
        getResources().getValue(str4 + ":string/" + str3, localTypedValue, true);
        if (localTypedValue.string != null)
        {
          str2 = localTypedValue.string.toString();
          if ((!paramBoolean2) || (str2 != null)) {
            break label234;
          }
          throw new com.google.ads.internal.b("Required XML attribute \"" + paramString + "\" missing", true);
        }
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        throw new com.google.ads.internal.b("Could not find resource for " + paramString + ": " + str1, true, localNotFoundException);
      }
      throw new com.google.ads.internal.b("Resource " + paramString + " was not a string: " + localTypedValue, true);
      label234:
      return str2;
      String str2 = str1;
    }
  }
  
  private void a(Activity paramActivity, AdSize paramAdSize, String paramString)
    throws com.google.ads.internal.b
  {
    FrameLayout localFrameLayout = new FrameLayout(paramActivity);
    localFrameLayout.setFocusable(false);
    this.a = m.a(this, paramString, paramActivity, localFrameLayout, paramAdSize);
    this.b = new d(this.a, false);
    setGravity(17);
    try
    {
      ViewGroup localViewGroup = j.a(paramActivity, this.b);
      if (localViewGroup != null)
      {
        localViewGroup.addView(localFrameLayout, -2, -2);
        addView(localViewGroup, -2, -2);
        return;
      }
      addView(localFrameLayout, -2, -2);
      return;
    }
    catch (VerifyError localVerifyError)
    {
      com.google.ads.util.b.a("Gestures disabled: Not supported on this version of Android.", localVerifyError);
      addView(localFrameLayout, -2, -2);
    }
  }
  
  private void a(Context paramContext, AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == null) {
      return;
    }
    try
    {
      String str2 = a("adSize", paramContext, paramAttributeSet, false, true);
      arrayOfAdSize2 = a(str2);
      if (arrayOfAdSize2 != null) {}
      try
      {
        if (arrayOfAdSize2.length != 0) {
          break label133;
        }
        throw new com.google.ads.internal.b("Attribute \"adSize\" invalid: " + str2, true);
      }
      catch (com.google.ads.internal.b localb2)
      {
        localObject = localb2;
        arrayOfAdSize1 = arrayOfAdSize2;
      }
    }
    catch (com.google.ads.internal.b localb1)
    {
      for (;;)
      {
        AdSize[] arrayOfAdSize2;
        String str1;
        AdSize localAdSize;
        Object localObject = localb1;
        AdSize[] arrayOfAdSize1 = null;
      }
    }
    str1 = ((com.google.ads.internal.b)localObject).c("Could not initialize AdView");
    if ((arrayOfAdSize1 != null) && (arrayOfAdSize1.length > 0)) {}
    for (localAdSize = arrayOfAdSize1[0];; localAdSize = AdSize.BANNER)
    {
      a(paramContext, str1, localAdSize, paramAttributeSet);
      ((com.google.ads.internal.b)localObject).a("Could not initialize AdView");
      if (isInEditMode()) {
        break;
      }
      ((com.google.ads.internal.b)localObject).b("Could not initialize AdView");
      return;
      label133:
      if (!a("adUnitId", paramAttributeSet)) {
        throw new com.google.ads.internal.b("Required XML attribute \"adUnitId\" missing", true);
      }
      if (isInEditMode())
      {
        a(paramContext, "Ads by Google", -1, arrayOfAdSize2[0], paramAttributeSet);
        return;
      }
      String str3 = a("adUnitId", paramContext, paramAttributeSet, true, true);
      boolean bool = paramAttributeSet.getAttributeBooleanValue("http://schemas.android.com/apk/lib/com.google.ads", "loadAdOnCreate", false);
      if ((paramContext instanceof Activity))
      {
        Activity localActivity = (Activity)paramContext;
        a(localActivity, arrayOfAdSize2[0], paramAttributeSet);
        b(localActivity, arrayOfAdSize2[0], paramAttributeSet);
        if (arrayOfAdSize2.length == 1) {
          a(localActivity, arrayOfAdSize2[0], str3);
        }
        while (bool)
        {
          Set localSet = b("testDevices", paramContext, paramAttributeSet, false, false);
          if (localSet.contains("TEST_EMULATOR"))
          {
            localSet.remove("TEST_EMULATOR");
            localSet.add(AdRequest.TEST_EMULATOR);
          }
          loadAd(new AdRequest().setTestDevices(localSet).setKeywords(b("keywords", paramContext, paramAttributeSet, false, false)));
          return;
          a(localActivity, new AdSize(0, 0), str3);
          a(arrayOfAdSize2);
        }
        break;
      }
      throw new com.google.ads.internal.b("AdView was initialized with a Context that wasn't an Activity.", true);
    }
  }
  
  private void a(Context paramContext, String paramString, AdSize paramAdSize, AttributeSet paramAttributeSet)
  {
    com.google.ads.util.b.b(paramString);
    a(paramContext, paramString, -65536, paramAdSize, paramAttributeSet);
  }
  
  private void a(AdSize... paramVarArgs)
  {
    AdSize[] arrayOfAdSize = new AdSize[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++) {
      arrayOfAdSize[i] = AdSize.createAdSize(paramVarArgs[i], getContext());
    }
    this.a.j.a(arrayOfAdSize);
  }
  
  private boolean a(Context paramContext, AdSize paramAdSize, AttributeSet paramAttributeSet)
  {
    if (!AdUtil.c(paramContext))
    {
      a(paramContext, "You must have AdActivity declared in AndroidManifest.xml with configChanges.", paramAdSize, paramAttributeSet);
      return false;
    }
    return true;
  }
  
  private boolean a(String paramString, AttributeSet paramAttributeSet)
  {
    return paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/lib/com.google.ads", paramString) != null;
  }
  
  private AdSize[] a(String paramString)
  {
    String[] arrayOfString = paramString.split(",");
    AdSize[] arrayOfAdSize = new AdSize[arrayOfString.length];
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String str = arrayOfString[i].trim();
      AdSize localAdSize;
      if ("BANNER".equals(str)) {
        localAdSize = AdSize.BANNER;
      }
      while (localAdSize == null)
      {
        return null;
        if ("SMART_BANNER".equals(str)) {
          localAdSize = AdSize.SMART_BANNER;
        } else if ("IAB_MRECT".equals(str)) {
          localAdSize = AdSize.IAB_MRECT;
        } else if ("IAB_BANNER".equals(str)) {
          localAdSize = AdSize.IAB_BANNER;
        } else if ("IAB_LEADERBOARD".equals(str)) {
          localAdSize = AdSize.IAB_LEADERBOARD;
        } else if ("IAB_WIDE_SKYSCRAPER".equals(str)) {
          localAdSize = AdSize.IAB_WIDE_SKYSCRAPER;
        } else {
          localAdSize = null;
        }
      }
      arrayOfAdSize[i] = localAdSize;
    }
    return arrayOfAdSize;
  }
  
  private Set<String> b(String paramString, Context paramContext, AttributeSet paramAttributeSet, boolean paramBoolean1, boolean paramBoolean2)
    throws com.google.ads.internal.b
  {
    String str1 = a(paramString, paramContext, paramAttributeSet, paramBoolean1, paramBoolean2);
    HashSet localHashSet = new HashSet();
    if (str1 != null)
    {
      String[] arrayOfString = str1.split(",");
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++)
      {
        String str2 = arrayOfString[j].trim();
        if (str2.length() != 0) {
          localHashSet.add(str2);
        }
      }
    }
    return localHashSet;
  }
  
  private boolean b(Context paramContext, AdSize paramAdSize, AttributeSet paramAttributeSet)
  {
    if (!AdUtil.b(paramContext))
    {
      a(paramContext, "You must have INTERNET and ACCESS_NETWORK_STATE permissions in AndroidManifest.xml.", paramAdSize, paramAttributeSet);
      return false;
    }
    return true;
  }
  
  void a(Context paramContext, String paramString, int paramInt, AdSize paramAdSize, AttributeSet paramAttributeSet)
  {
    if (paramAdSize == null) {
      paramAdSize = AdSize.BANNER;
    }
    AdSize localAdSize = AdSize.createAdSize(paramAdSize, paramContext.getApplicationContext());
    TextView localTextView;
    LinearLayout localLinearLayout1;
    if (getChildCount() == 0)
    {
      if (paramAttributeSet != null) {
        break label177;
      }
      localTextView = new TextView(paramContext);
      localTextView.setGravity(17);
      localTextView.setText(paramString);
      localTextView.setTextColor(paramInt);
      localTextView.setBackgroundColor(-16777216);
      if (paramAttributeSet != null) {
        break label192;
      }
      localLinearLayout1 = new LinearLayout(paramContext);
      label85:
      localLinearLayout1.setGravity(17);
      if (paramAttributeSet != null) {
        break label207;
      }
    }
    label177:
    label192:
    label207:
    for (LinearLayout localLinearLayout2 = new LinearLayout(paramContext);; localLinearLayout2 = new LinearLayout(paramContext, paramAttributeSet))
    {
      localLinearLayout2.setGravity(17);
      localLinearLayout2.setBackgroundColor(paramInt);
      int i = a(paramContext, localAdSize.getWidth());
      int j = a(paramContext, localAdSize.getHeight());
      localLinearLayout1.addView(localTextView, i - 2, j - 2);
      localLinearLayout2.addView(localLinearLayout1);
      addView(localLinearLayout2, i, j);
      return;
      localTextView = new TextView(paramContext, paramAttributeSet);
      break;
      localLinearLayout1 = new LinearLayout(paramContext, paramAttributeSet);
      break label85;
    }
  }
  
  public void destroy()
  {
    this.b.b();
  }
  
  public boolean isReady()
  {
    if (this.b == null) {
      return false;
    }
    return this.b.r();
  }
  
  public boolean isRefreshing()
  {
    if (this.b == null) {
      return false;
    }
    return this.b.s();
  }
  
  public void loadAd(AdRequest paramAdRequest)
  {
    if (this.b != null)
    {
      if (isRefreshing()) {
        this.b.e();
      }
      this.b.a(paramAdRequest);
    }
  }
  
  public void setAdListener(AdListener paramAdListener)
  {
    this.a.k.a(paramAdListener);
  }
  
  protected void setAppEventListener(AppEventListener paramAppEventListener)
  {
    this.a.l.a(paramAppEventListener);
  }
  
  protected void setSupportedAdSizes(AdSize... paramVarArgs)
  {
    if (this.a.j.a() == null)
    {
      com.google.ads.util.b.b("Error: Tried to set supported ad sizes on a single-size AdView.");
      return;
    }
    a(paramVarArgs);
  }
  
  public void stopLoading()
  {
    if (this.b != null) {
      this.b.A();
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.AdView
 * JD-Core Version:    0.7.0.1
 */
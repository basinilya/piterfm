package com.google.ads;

import com.google.ads.internal.h;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class c
{
  private static final Map<String, AdSize> a = Collections.unmodifiableMap(new HashMap() {});
  private final String b;
  private final String c;
  private final List<a> d;
  private final Integer e;
  private final Integer f;
  private final List<String> g;
  private final List<String> h;
  private final List<String> i;
  
  private c(String paramString1, String paramString2, List<a> paramList, Integer paramInteger1, Integer paramInteger2, List<String> paramList1, List<String> paramList2, List<String> paramList3)
  {
    com.google.ads.util.a.a(paramString1);
    this.b = paramString1;
    this.c = paramString2;
    this.d = paramList;
    this.e = paramInteger1;
    this.f = paramInteger2;
    this.g = paramList1;
    this.h = paramList2;
    this.i = paramList3;
  }
  
  private static a a(JSONObject paramJSONObject)
    throws JSONException
  {
    String str1 = paramJSONObject.getString("id");
    String str2 = paramJSONObject.optString("allocation_id", null);
    JSONArray localJSONArray = paramJSONObject.getJSONArray("adapters");
    ArrayList localArrayList = new ArrayList(localJSONArray.length());
    for (int j = 0; j < localJSONArray.length(); j++) {
      localArrayList.add(localJSONArray.getString(j));
    }
    JSONObject localJSONObject = paramJSONObject.optJSONObject("data");
    Object localObject = new HashMap(0);
    if (localJSONObject != null)
    {
      HashMap localHashMap = new HashMap(localJSONObject.length());
      Iterator localIterator = localJSONObject.keys();
      while (localIterator.hasNext())
      {
        String str3 = (String)localIterator.next();
        localHashMap.put(str3, localJSONObject.getString(str3));
      }
      localObject = localHashMap;
    }
    return new a(str2, str1, localArrayList, (HashMap)localObject);
  }
  
  public static c a(String paramString)
    throws JSONException
  {
    JSONObject localJSONObject1 = new JSONObject(paramString);
    String str1 = localJSONObject1.getString("qdata");
    if (localJSONObject1.has("ad_type")) {}
    ArrayList localArrayList;
    for (String str2 = localJSONObject1.getString("ad_type");; str2 = null)
    {
      JSONArray localJSONArray = localJSONObject1.getJSONArray("ad_networks");
      localArrayList = new ArrayList(localJSONArray.length());
      for (int j = 0; j < localJSONArray.length(); j++) {
        localArrayList.add(a(localJSONArray.getJSONObject(j)));
      }
    }
    JSONObject localJSONObject2 = localJSONObject1.optJSONObject("settings");
    Integer localInteger1;
    List localList1;
    List localList2;
    List localList3;
    Integer localInteger2;
    if (localJSONObject2 != null) {
      if (localJSONObject2.has("refresh"))
      {
        localInteger1 = Integer.valueOf(localJSONObject2.getInt("refresh"));
        boolean bool = localJSONObject2.has("ad_network_timeout_millis");
        Integer localInteger3 = null;
        if (bool) {
          localInteger3 = Integer.valueOf(localJSONObject2.getInt("ad_network_timeout_millis"));
        }
        localList1 = a(localJSONObject2, "imp_urls");
        localList2 = a(localJSONObject2, "click_urls");
        localList3 = a(localJSONObject2, "nofill_urls");
        localInteger2 = localInteger3;
      }
    }
    for (;;)
    {
      return new c(str1, str2, localArrayList, localInteger1, localInteger2, localList1, localList2, localList3);
      localInteger1 = null;
      break;
      localInteger1 = null;
      localInteger2 = null;
      localList1 = null;
      localList2 = null;
      localList3 = null;
    }
  }
  
  private static List<String> a(JSONObject paramJSONObject, String paramString)
    throws JSONException
  {
    JSONArray localJSONArray = paramJSONObject.optJSONArray(paramString);
    if (localJSONArray != null)
    {
      ArrayList localArrayList = new ArrayList(localJSONArray.length());
      for (int j = 0; j < localJSONArray.length(); j++) {
        localArrayList.add(localJSONArray.getString(j));
      }
      return localArrayList;
    }
    return null;
  }
  
  public boolean a()
  {
    return this.f != null;
  }
  
  public int b()
  {
    return this.f.intValue();
  }
  
  public String c()
  {
    return this.b;
  }
  
  public boolean d()
  {
    return this.e != null;
  }
  
  public int e()
  {
    return this.e.intValue();
  }
  
  public List<a> f()
  {
    return this.d;
  }
  
  public List<String> g()
  {
    return this.g;
  }
  
  public List<String> h()
  {
    return this.h;
  }
  
  public List<String> i()
  {
    return this.i;
  }
  
  public h j()
  {
    if (this.c == null) {
      return null;
    }
    if ("interstitial".equals(this.c)) {
      return h.a;
    }
    AdSize localAdSize = (AdSize)a.get(this.c);
    if (localAdSize != null) {
      return h.a(localAdSize);
    }
    return null;
  }
}


/* Location:
 * Qualified Name:     com.google.ads.c
 * JD-Core Version:    0.7.0.1
 */
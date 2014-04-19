package com.actionbarsherlock.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.Handler;
import android.text.TextUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

class ActivityChooserModel
  extends DataSetObservable
{
  private static final String ATTRIBUTE_ACTIVITY = "activity";
  private static final String ATTRIBUTE_TIME = "time";
  private static final String ATTRIBUTE_WEIGHT = "weight";
  private static final boolean DEBUG = false;
  private static final int DEFAULT_ACTIVITY_INFLATION = 5;
  private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0F;
  public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
  public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
  private static final String HISTORY_FILE_EXTENSION = ".xml";
  private static final int INVALID_INDEX = -1;
  private static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
  private static final SerialExecutor SERIAL_EXECUTOR = new SerialExecutor(null);
  private static final String TAG_HISTORICAL_RECORD = "historical-record";
  private static final String TAG_HISTORICAL_RECORDS = "historical-records";
  private static final Map<String, ActivityChooserModel> sDataModelRegistry;
  private static final Object sRegistryLock = new Object();
  private final List<ActivityResolveInfo> mActivites = new ArrayList();
  private OnChooseActivityListener mActivityChoserModelPolicy;
  private ActivitySorter mActivitySorter = new DefaultSorter(null);
  private boolean mCanReadHistoricalData = true;
  private final Context mContext;
  private final Handler mHandler = new Handler();
  private final List<HistoricalRecord> mHistoricalRecords = new ArrayList();
  private boolean mHistoricalRecordsChanged = true;
  private final String mHistoryFileName;
  private int mHistoryMaxSize = 50;
  private final Object mInstanceLock = new Object();
  private Intent mIntent;
  private boolean mReadShareHistoryCalled = false;
  
  static
  {
    sDataModelRegistry = new HashMap();
  }
  
  private ActivityChooserModel(Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    if ((!TextUtils.isEmpty(paramString)) && (!paramString.endsWith(".xml")))
    {
      this.mHistoryFileName = (paramString + ".xml");
      return;
    }
    this.mHistoryFileName = paramString;
  }
  
  private boolean addHisoricalRecord(HistoricalRecord paramHistoricalRecord)
  {
    synchronized (this.mInstanceLock)
    {
      boolean bool = this.mHistoricalRecords.add(paramHistoricalRecord);
      if (bool)
      {
        this.mHistoricalRecordsChanged = true;
        pruneExcessiveHistoricalRecordsLocked();
        persistHistoricalData();
        sortActivities();
      }
      return bool;
    }
  }
  
  public static ActivityChooserModel get(Context paramContext, String paramString)
  {
    synchronized (sRegistryLock)
    {
      ActivityChooserModel localActivityChooserModel = (ActivityChooserModel)sDataModelRegistry.get(paramString);
      if (localActivityChooserModel == null)
      {
        localActivityChooserModel = new ActivityChooserModel(paramContext, paramString);
        sDataModelRegistry.put(paramString, localActivityChooserModel);
      }
      localActivityChooserModel.readHistoricalData();
      return localActivityChooserModel;
    }
  }
  
  private void loadActivitiesLocked()
  {
    this.mActivites.clear();
    if (this.mIntent != null)
    {
      List localList = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
      int i = localList.size();
      for (int j = 0; j < i; j++)
      {
        ResolveInfo localResolveInfo = (ResolveInfo)localList.get(j);
        this.mActivites.add(new ActivityResolveInfo(localResolveInfo));
      }
      sortActivities();
      return;
    }
    notifyChanged();
  }
  
  private void persistHistoricalData()
  {
    synchronized (this.mInstanceLock)
    {
      if (!this.mReadShareHistoryCalled) {
        throw new IllegalStateException("No preceding call to #readHistoricalData");
      }
    }
    if (!this.mHistoricalRecordsChanged) {
      return;
    }
    this.mHistoricalRecordsChanged = false;
    this.mCanReadHistoricalData = true;
    if (!TextUtils.isEmpty(this.mHistoryFileName)) {
      SERIAL_EXECUTOR.execute(new HistoryPersister(null));
    }
  }
  
  private void pruneExcessiveHistoricalRecordsLocked()
  {
    List localList = this.mHistoricalRecords;
    int i = localList.size() - this.mHistoryMaxSize;
    if (i <= 0) {}
    for (;;)
    {
      return;
      this.mHistoricalRecordsChanged = true;
      for (int j = 0; j < i; j++) {
        ((HistoricalRecord)localList.remove(0));
      }
    }
  }
  
  private void readHistoricalData()
  {
    synchronized (this.mInstanceLock)
    {
      if ((!this.mCanReadHistoricalData) || (!this.mHistoricalRecordsChanged)) {
        return;
      }
      this.mCanReadHistoricalData = false;
      this.mReadShareHistoryCalled = true;
      if (!TextUtils.isEmpty(this.mHistoryFileName)) {
        SERIAL_EXECUTOR.execute(new HistoryLoader(null));
      }
      return;
    }
  }
  
  private void sortActivities()
  {
    synchronized (this.mInstanceLock)
    {
      if ((this.mActivitySorter != null) && (!this.mActivites.isEmpty()))
      {
        this.mActivitySorter.sort(this.mIntent, this.mActivites, Collections.unmodifiableList(this.mHistoricalRecords));
        notifyChanged();
      }
      return;
    }
  }
  
  public Intent chooseActivity(int paramInt)
  {
    ActivityResolveInfo localActivityResolveInfo = (ActivityResolveInfo)this.mActivites.get(paramInt);
    ComponentName localComponentName = new ComponentName(localActivityResolveInfo.resolveInfo.activityInfo.packageName, localActivityResolveInfo.resolveInfo.activityInfo.name);
    Intent localIntent1 = new Intent(this.mIntent);
    localIntent1.setComponent(localComponentName);
    if (this.mActivityChoserModelPolicy != null)
    {
      Intent localIntent2 = new Intent(localIntent1);
      if (this.mActivityChoserModelPolicy.onChooseActivity(this, localIntent2)) {
        return null;
      }
    }
    addHisoricalRecord(new HistoricalRecord(localComponentName, System.currentTimeMillis(), 1.0F));
    return localIntent1;
  }
  
  public ResolveInfo getActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivites.get(paramInt)).resolveInfo;
      return localResolveInfo;
    }
  }
  
  public int getActivityCount()
  {
    synchronized (this.mInstanceLock)
    {
      int i = this.mActivites.size();
      return i;
    }
  }
  
  public int getActivityIndex(ResolveInfo paramResolveInfo)
  {
    List localList = this.mActivites;
    int i = localList.size();
    for (int j = 0; j < i; j++) {
      if (((ActivityResolveInfo)localList.get(j)).resolveInfo == paramResolveInfo) {
        return j;
      }
    }
    return -1;
  }
  
  public ResolveInfo getDefaultActivity()
  {
    synchronized (this.mInstanceLock)
    {
      if (!this.mActivites.isEmpty())
      {
        ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivites.get(0)).resolveInfo;
        return localResolveInfo;
      }
      return null;
    }
  }
  
  public int getHistoryMaxSize()
  {
    synchronized (this.mInstanceLock)
    {
      int i = this.mHistoryMaxSize;
      return i;
    }
  }
  
  public int getHistorySize()
  {
    synchronized (this.mInstanceLock)
    {
      int i = this.mHistoricalRecords.size();
      return i;
    }
  }
  
  public Intent getIntent()
  {
    synchronized (this.mInstanceLock)
    {
      Intent localIntent = this.mIntent;
      return localIntent;
    }
  }
  
  public void setActivitySorter(ActivitySorter paramActivitySorter)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mActivitySorter == paramActivitySorter) {
        return;
      }
      this.mActivitySorter = paramActivitySorter;
      sortActivities();
      return;
    }
  }
  
  public void setDefaultActivity(int paramInt)
  {
    ActivityResolveInfo localActivityResolveInfo1 = (ActivityResolveInfo)this.mActivites.get(paramInt);
    ActivityResolveInfo localActivityResolveInfo2 = (ActivityResolveInfo)this.mActivites.get(0);
    if (localActivityResolveInfo2 != null) {}
    for (float f = 5.0F + (localActivityResolveInfo2.weight - localActivityResolveInfo1.weight);; f = 1.0F)
    {
      addHisoricalRecord(new HistoricalRecord(new ComponentName(localActivityResolveInfo1.resolveInfo.activityInfo.packageName, localActivityResolveInfo1.resolveInfo.activityInfo.name), System.currentTimeMillis(), f));
      return;
    }
  }
  
  public void setHistoryMaxSize(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mHistoryMaxSize == paramInt) {
        return;
      }
      this.mHistoryMaxSize = paramInt;
      pruneExcessiveHistoricalRecordsLocked();
      sortActivities();
      return;
    }
  }
  
  public void setIntent(Intent paramIntent)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mIntent == paramIntent) {
        return;
      }
      this.mIntent = paramIntent;
      loadActivitiesLocked();
      return;
    }
  }
  
  public void setOnChooseActivityListener(OnChooseActivityListener paramOnChooseActivityListener)
  {
    this.mActivityChoserModelPolicy = paramOnChooseActivityListener;
  }
  
  public static abstract interface ActivityChooserModelClient
  {
    public abstract void setActivityChooserModel(ActivityChooserModel paramActivityChooserModel);
  }
  
  public final class ActivityResolveInfo
    implements Comparable<ActivityResolveInfo>
  {
    public final ResolveInfo resolveInfo;
    public float weight;
    
    public ActivityResolveInfo(ResolveInfo paramResolveInfo)
    {
      this.resolveInfo = paramResolveInfo;
    }
    
    public int compareTo(ActivityResolveInfo paramActivityResolveInfo)
    {
      return Float.floatToIntBits(paramActivityResolveInfo.weight) - Float.floatToIntBits(this.weight);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      ActivityResolveInfo localActivityResolveInfo;
      do
      {
        return true;
        if (paramObject == null) {
          return false;
        }
        if (getClass() != paramObject.getClass()) {
          return false;
        }
        localActivityResolveInfo = (ActivityResolveInfo)paramObject;
      } while (Float.floatToIntBits(this.weight) == Float.floatToIntBits(localActivityResolveInfo.weight));
      return false;
    }
    
    public int hashCode()
    {
      return 31 + Float.floatToIntBits(this.weight);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("resolveInfo:").append(this.resolveInfo.toString());
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface ActivitySorter
  {
    public abstract void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1);
  }
  
  private final class DefaultSorter
    implements ActivityChooserModel.ActivitySorter
  {
    private static final float WEIGHT_DECAY_COEFFICIENT = 0.95F;
    private final Map<String, ActivityChooserModel.ActivityResolveInfo> mPackageNameToActivityMap = new HashMap();
    
    private DefaultSorter() {}
    
    public void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1)
    {
      Map localMap = this.mPackageNameToActivityMap;
      localMap.clear();
      int i = paramList.size();
      for (int j = 0; j < i; j++)
      {
        ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo2 = (ActivityChooserModel.ActivityResolveInfo)paramList.get(j);
        localActivityResolveInfo2.weight = 0.0F;
        localMap.put(localActivityResolveInfo2.resolveInfo.activityInfo.packageName, localActivityResolveInfo2);
      }
      int k = -1 + paramList1.size();
      float f = 1.0F;
      for (int m = k; m >= 0; m--)
      {
        ActivityChooserModel.HistoricalRecord localHistoricalRecord = (ActivityChooserModel.HistoricalRecord)paramList1.get(m);
        ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo1 = (ActivityChooserModel.ActivityResolveInfo)localMap.get(localHistoricalRecord.activity.getPackageName());
        if (localActivityResolveInfo1 != null)
        {
          localActivityResolveInfo1.weight += f * localHistoricalRecord.weight;
          f *= 0.95F;
        }
      }
      Collections.sort(paramList);
    }
  }
  
  public static final class HistoricalRecord
  {
    public final ComponentName activity;
    public final long time;
    public final float weight;
    
    public HistoricalRecord(ComponentName paramComponentName, long paramLong, float paramFloat)
    {
      this.activity = paramComponentName;
      this.time = paramLong;
      this.weight = paramFloat;
    }
    
    public HistoricalRecord(String paramString, long paramLong, float paramFloat)
    {
      this(ComponentName.unflattenFromString(paramString), paramLong, paramFloat);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      HistoricalRecord localHistoricalRecord;
      do
      {
        return true;
        if (paramObject == null) {
          return false;
        }
        if (getClass() != paramObject.getClass()) {
          return false;
        }
        localHistoricalRecord = (HistoricalRecord)paramObject;
        if (this.activity == null)
        {
          if (localHistoricalRecord.activity != null) {
            return false;
          }
        }
        else if (!this.activity.equals(localHistoricalRecord.activity)) {
          return false;
        }
        if (this.time != localHistoricalRecord.time) {
          return false;
        }
      } while (Float.floatToIntBits(this.weight) == Float.floatToIntBits(localHistoricalRecord.weight));
      return false;
    }
    
    public int hashCode()
    {
      if (this.activity == null) {}
      for (int i = 0;; i = this.activity.hashCode()) {
        return 31 * (31 * (i + 31) + (int)(this.time ^ this.time >>> 32)) + Float.floatToIntBits(this.weight);
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("; activity:").append(this.activity);
      localStringBuilder.append("; time:").append(this.time);
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private final class HistoryLoader
    implements Runnable
  {
    private HistoryLoader() {}
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   4: invokestatic 31	com/actionbarsherlock/widget/ActivityChooserModel:access$500	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Landroid/content/Context;
      //   7: aload_0
      //   8: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   11: invokestatic 35	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   14: invokevirtual 41	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
      //   17: astore_2
      //   18: invokestatic 47	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
      //   21: astore 11
      //   23: aload 11
      //   25: aload_2
      //   26: aconst_null
      //   27: invokeinterface 53 3 0
      //   32: iconst_0
      //   33: istore 12
      //   35: iload 12
      //   37: iconst_1
      //   38: if_icmpeq +21 -> 59
      //   41: iload 12
      //   43: iconst_2
      //   44: if_icmpeq +15 -> 59
      //   47: aload 11
      //   49: invokeinterface 57 1 0
      //   54: istore 12
      //   56: goto -21 -> 35
      //   59: ldc 59
      //   61: aload 11
      //   63: invokeinterface 63 1 0
      //   68: invokevirtual 69	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   71: ifne +61 -> 132
      //   74: new 23	org/xmlpull/v1/XmlPullParserException
      //   77: dup
      //   78: ldc 71
      //   80: invokespecial 74	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
      //   83: athrow
      //   84: astore 8
      //   86: invokestatic 77	com/actionbarsherlock/widget/ActivityChooserModel:access$1200	()Ljava/lang/String;
      //   89: new 79	java/lang/StringBuilder
      //   92: dup
      //   93: invokespecial 80	java/lang/StringBuilder:<init>	()V
      //   96: ldc 82
      //   98: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   101: aload_0
      //   102: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   105: invokestatic 35	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   108: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   111: invokevirtual 89	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   114: aload 8
      //   116: invokestatic 95	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   119: pop
      //   120: aload_2
      //   121: ifnull +365 -> 486
      //   124: aload_2
      //   125: invokevirtual 100	java/io/FileInputStream:close	()V
      //   128: return
      //   129: astore 10
      //   131: return
      //   132: new 102	java/util/ArrayList
      //   135: dup
      //   136: invokespecial 103	java/util/ArrayList:<init>	()V
      //   139: astore 13
      //   141: aload 11
      //   143: invokeinterface 57 1 0
      //   148: istore 14
      //   150: iload 14
      //   152: iconst_1
      //   153: if_icmpne +77 -> 230
      //   156: aload_0
      //   157: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   160: invokestatic 107	com/actionbarsherlock/widget/ActivityChooserModel:access$600	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/Object;
      //   163: astore 15
      //   165: aload 15
      //   167: monitorenter
      //   168: new 109	java/util/LinkedHashSet
      //   171: dup
      //   172: aload 13
      //   174: invokespecial 112	java/util/LinkedHashSet:<init>	(Ljava/util/Collection;)V
      //   177: astore 16
      //   179: aload_0
      //   180: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   183: invokestatic 116	com/actionbarsherlock/widget/ActivityChooserModel:access$700	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/util/List;
      //   186: astore 18
      //   188: iconst_m1
      //   189: aload 18
      //   191: invokeinterface 121 1 0
      //   196: iadd
      //   197: istore 19
      //   199: iload 19
      //   201: iflt +179 -> 380
      //   204: aload 16
      //   206: aload 18
      //   208: iload 19
      //   210: invokeinterface 125 2 0
      //   215: checkcast 127	com/actionbarsherlock/widget/ActivityChooserModel$HistoricalRecord
      //   218: invokeinterface 132 2 0
      //   223: pop
      //   224: iinc 19 255
      //   227: goto -28 -> 199
      //   230: iload 14
      //   232: iconst_3
      //   233: if_icmpeq -92 -> 141
      //   236: iload 14
      //   238: iconst_4
      //   239: if_icmpeq -98 -> 141
      //   242: ldc 134
      //   244: aload 11
      //   246: invokeinterface 63 1 0
      //   251: invokevirtual 69	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   254: ifne +61 -> 315
      //   257: new 23	org/xmlpull/v1/XmlPullParserException
      //   260: dup
      //   261: ldc 136
      //   263: invokespecial 74	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
      //   266: athrow
      //   267: astore 5
      //   269: invokestatic 77	com/actionbarsherlock/widget/ActivityChooserModel:access$1200	()Ljava/lang/String;
      //   272: new 79	java/lang/StringBuilder
      //   275: dup
      //   276: invokespecial 80	java/lang/StringBuilder:<init>	()V
      //   279: ldc 82
      //   281: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   284: aload_0
      //   285: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   288: invokestatic 35	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   291: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   294: invokevirtual 89	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   297: aload 5
      //   299: invokestatic 95	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   302: pop
      //   303: aload_2
      //   304: ifnull +182 -> 486
      //   307: aload_2
      //   308: invokevirtual 100	java/io/FileInputStream:close	()V
      //   311: return
      //   312: astore 7
      //   314: return
      //   315: aload 13
      //   317: new 127	com/actionbarsherlock/widget/ActivityChooserModel$HistoricalRecord
      //   320: dup
      //   321: aload 11
      //   323: aconst_null
      //   324: ldc 138
      //   326: invokeinterface 142 3 0
      //   331: aload 11
      //   333: aconst_null
      //   334: ldc 144
      //   336: invokeinterface 142 3 0
      //   341: invokestatic 150	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   344: aload 11
      //   346: aconst_null
      //   347: ldc 152
      //   349: invokeinterface 142 3 0
      //   354: invokestatic 158	java/lang/Float:parseFloat	(Ljava/lang/String;)F
      //   357: invokespecial 161	com/actionbarsherlock/widget/ActivityChooserModel$HistoricalRecord:<init>	(Ljava/lang/String;JF)V
      //   360: invokeinterface 162 2 0
      //   365: pop
      //   366: goto -225 -> 141
      //   369: astore_3
      //   370: aload_2
      //   371: ifnull +7 -> 378
      //   374: aload_2
      //   375: invokevirtual 100	java/io/FileInputStream:close	()V
      //   378: aload_3
      //   379: athrow
      //   380: aload 18
      //   382: invokeinterface 121 1 0
      //   387: aload 16
      //   389: invokeinterface 163 1 0
      //   394: if_icmpne +18 -> 412
      //   397: aload 15
      //   399: monitorexit
      //   400: aload_2
      //   401: ifnull +85 -> 486
      //   404: aload_2
      //   405: invokevirtual 100	java/io/FileInputStream:close	()V
      //   408: return
      //   409: astore 25
      //   411: return
      //   412: aload 18
      //   414: invokeinterface 166 1 0
      //   419: aload 18
      //   421: aload 16
      //   423: invokeinterface 170 2 0
      //   428: pop
      //   429: aload_0
      //   430: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   433: iconst_1
      //   434: invokestatic 174	com/actionbarsherlock/widget/ActivityChooserModel:access$802	(Lcom/actionbarsherlock/widget/ActivityChooserModel;Z)Z
      //   437: pop
      //   438: aload_0
      //   439: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   442: invokestatic 178	com/actionbarsherlock/widget/ActivityChooserModel:access$1100	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Landroid/os/Handler;
      //   445: new 180	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader$1
      //   448: dup
      //   449: aload_0
      //   450: invokespecial 183	com/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader$1:<init>	(Lcom/actionbarsherlock/widget/ActivityChooserModel$HistoryLoader;)V
      //   453: invokevirtual 189	android/os/Handler:post	(Ljava/lang/Runnable;)Z
      //   456: pop
      //   457: aload 15
      //   459: monitorexit
      //   460: aload_2
      //   461: ifnull +25 -> 486
      //   464: aload_2
      //   465: invokevirtual 100	java/io/FileInputStream:close	()V
      //   468: return
      //   469: astore 24
      //   471: return
      //   472: astore 17
      //   474: aload 15
      //   476: monitorexit
      //   477: aload 17
      //   479: athrow
      //   480: astore 4
      //   482: goto -104 -> 378
      //   485: astore_1
      //   486: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	487	0	this	HistoryLoader
      //   485	1	1	localFileNotFoundException	java.io.FileNotFoundException
      //   17	448	2	localFileInputStream	java.io.FileInputStream
      //   369	10	3	localObject1	Object
      //   480	1	4	localIOException1	java.io.IOException
      //   267	31	5	localIOException2	java.io.IOException
      //   312	1	7	localIOException3	java.io.IOException
      //   84	31	8	localXmlPullParserException	org.xmlpull.v1.XmlPullParserException
      //   129	1	10	localIOException4	java.io.IOException
      //   21	324	11	localXmlPullParser	org.xmlpull.v1.XmlPullParser
      //   33	22	12	i	int
      //   139	177	13	localArrayList	ArrayList
      //   148	92	14	j	int
      //   177	245	16	localLinkedHashSet	java.util.LinkedHashSet
      //   472	6	17	localObject3	Object
      //   186	234	18	localList	List
      //   197	28	19	k	int
      //   469	1	24	localIOException5	java.io.IOException
      //   409	1	25	localIOException6	java.io.IOException
      // Exception table:
      //   from	to	target	type
      //   18	32	84	org/xmlpull/v1/XmlPullParserException
      //   47	56	84	org/xmlpull/v1/XmlPullParserException
      //   59	84	84	org/xmlpull/v1/XmlPullParserException
      //   132	141	84	org/xmlpull/v1/XmlPullParserException
      //   141	150	84	org/xmlpull/v1/XmlPullParserException
      //   156	168	84	org/xmlpull/v1/XmlPullParserException
      //   242	267	84	org/xmlpull/v1/XmlPullParserException
      //   315	366	84	org/xmlpull/v1/XmlPullParserException
      //   477	480	84	org/xmlpull/v1/XmlPullParserException
      //   124	128	129	java/io/IOException
      //   18	32	267	java/io/IOException
      //   47	56	267	java/io/IOException
      //   59	84	267	java/io/IOException
      //   132	141	267	java/io/IOException
      //   141	150	267	java/io/IOException
      //   156	168	267	java/io/IOException
      //   242	267	267	java/io/IOException
      //   315	366	267	java/io/IOException
      //   477	480	267	java/io/IOException
      //   307	311	312	java/io/IOException
      //   18	32	369	finally
      //   47	56	369	finally
      //   59	84	369	finally
      //   86	120	369	finally
      //   132	141	369	finally
      //   141	150	369	finally
      //   156	168	369	finally
      //   242	267	369	finally
      //   269	303	369	finally
      //   315	366	369	finally
      //   477	480	369	finally
      //   404	408	409	java/io/IOException
      //   464	468	469	java/io/IOException
      //   168	199	472	finally
      //   204	224	472	finally
      //   380	400	472	finally
      //   412	460	472	finally
      //   474	477	472	finally
      //   374	378	480	java/io/IOException
      //   0	18	485	java/io/FileNotFoundException
    }
  }
  
  private final class HistoryPersister
    implements Runnable
  {
    private HistoryPersister() {}
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   4: invokestatic 33	com/actionbarsherlock/widget/ActivityChooserModel:access$600	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/Object;
      //   7: astore_1
      //   8: aload_1
      //   9: monitorenter
      //   10: new 35	java/util/ArrayList
      //   13: dup
      //   14: aload_0
      //   15: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   18: invokestatic 39	com/actionbarsherlock/widget/ActivityChooserModel:access$700	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/util/List;
      //   21: invokespecial 42	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
      //   24: astore_2
      //   25: aload_1
      //   26: monitorexit
      //   27: aload_0
      //   28: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   31: invokestatic 46	com/actionbarsherlock/widget/ActivityChooserModel:access$500	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Landroid/content/Context;
      //   34: aload_0
      //   35: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   38: invokestatic 50	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   41: iconst_0
      //   42: invokevirtual 56	android/content/Context:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
      //   45: astore 6
      //   47: invokestatic 62	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
      //   50: astore 7
      //   52: aload 7
      //   54: aload 6
      //   56: aconst_null
      //   57: invokeinterface 68 3 0
      //   62: aload 7
      //   64: ldc 70
      //   66: iconst_1
      //   67: invokestatic 76	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   70: invokeinterface 80 3 0
      //   75: aload 7
      //   77: aconst_null
      //   78: ldc 82
      //   80: invokeinterface 86 3 0
      //   85: pop
      //   86: aload_2
      //   87: invokeinterface 92 1 0
      //   92: istore 20
      //   94: iconst_0
      //   95: istore 21
      //   97: iload 21
      //   99: iload 20
      //   101: if_icmpge +142 -> 243
      //   104: aload_2
      //   105: iconst_0
      //   106: invokeinterface 96 2 0
      //   111: checkcast 98	com/actionbarsherlock/widget/ActivityChooserModel$HistoricalRecord
      //   114: astore 22
      //   116: aload 7
      //   118: aconst_null
      //   119: ldc 100
      //   121: invokeinterface 86 3 0
      //   126: pop
      //   127: aload 7
      //   129: aconst_null
      //   130: ldc 102
      //   132: aload 22
      //   134: getfield 105	com/actionbarsherlock/widget/ActivityChooserModel$HistoricalRecord:activity	Landroid/content/ComponentName;
      //   137: invokevirtual 111	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   140: invokeinterface 115 4 0
      //   145: pop
      //   146: aload 7
      //   148: aconst_null
      //   149: ldc 117
      //   151: aload 22
      //   153: getfield 120	com/actionbarsherlock/widget/ActivityChooserModel$HistoricalRecord:time	J
      //   156: invokestatic 125	java/lang/String:valueOf	(J)Ljava/lang/String;
      //   159: invokeinterface 115 4 0
      //   164: pop
      //   165: aload 7
      //   167: aconst_null
      //   168: ldc 127
      //   170: aload 22
      //   172: getfield 130	com/actionbarsherlock/widget/ActivityChooserModel$HistoricalRecord:weight	F
      //   175: invokestatic 133	java/lang/String:valueOf	(F)Ljava/lang/String;
      //   178: invokeinterface 115 4 0
      //   183: pop
      //   184: aload 7
      //   186: aconst_null
      //   187: ldc 100
      //   189: invokeinterface 136 3 0
      //   194: pop
      //   195: iinc 21 1
      //   198: goto -101 -> 97
      //   201: astore_3
      //   202: aload_1
      //   203: monitorexit
      //   204: aload_3
      //   205: athrow
      //   206: astore 4
      //   208: invokestatic 139	com/actionbarsherlock/widget/ActivityChooserModel:access$1200	()Ljava/lang/String;
      //   211: new 141	java/lang/StringBuilder
      //   214: dup
      //   215: invokespecial 142	java/lang/StringBuilder:<init>	()V
      //   218: ldc 144
      //   220: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   223: aload_0
      //   224: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   227: invokestatic 50	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   230: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   233: invokevirtual 151	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   236: aload 4
      //   238: invokestatic 157	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   241: pop
      //   242: return
      //   243: aload 7
      //   245: aconst_null
      //   246: ldc 82
      //   248: invokeinterface 136 3 0
      //   253: pop
      //   254: aload 7
      //   256: invokeinterface 160 1 0
      //   261: aload 6
      //   263: ifnull -21 -> 242
      //   266: aload 6
      //   268: invokevirtual 165	java/io/FileOutputStream:close	()V
      //   271: return
      //   272: astore 29
      //   274: return
      //   275: astore 16
      //   277: invokestatic 139	com/actionbarsherlock/widget/ActivityChooserModel:access$1200	()Ljava/lang/String;
      //   280: new 141	java/lang/StringBuilder
      //   283: dup
      //   284: invokespecial 142	java/lang/StringBuilder:<init>	()V
      //   287: ldc 144
      //   289: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   292: aload_0
      //   293: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   296: invokestatic 50	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   299: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   302: invokevirtual 151	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   305: aload 16
      //   307: invokestatic 157	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   310: pop
      //   311: aload 6
      //   313: ifnull -71 -> 242
      //   316: aload 6
      //   318: invokevirtual 165	java/io/FileOutputStream:close	()V
      //   321: return
      //   322: astore 18
      //   324: return
      //   325: astore 13
      //   327: invokestatic 139	com/actionbarsherlock/widget/ActivityChooserModel:access$1200	()Ljava/lang/String;
      //   330: new 141	java/lang/StringBuilder
      //   333: dup
      //   334: invokespecial 142	java/lang/StringBuilder:<init>	()V
      //   337: ldc 144
      //   339: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   342: aload_0
      //   343: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   346: invokestatic 50	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   349: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   352: invokevirtual 151	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   355: aload 13
      //   357: invokestatic 157	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   360: pop
      //   361: aload 6
      //   363: ifnull -121 -> 242
      //   366: aload 6
      //   368: invokevirtual 165	java/io/FileOutputStream:close	()V
      //   371: return
      //   372: astore 15
      //   374: return
      //   375: astore 10
      //   377: invokestatic 139	com/actionbarsherlock/widget/ActivityChooserModel:access$1200	()Ljava/lang/String;
      //   380: new 141	java/lang/StringBuilder
      //   383: dup
      //   384: invokespecial 142	java/lang/StringBuilder:<init>	()V
      //   387: ldc 144
      //   389: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   392: aload_0
      //   393: getfield 12	com/actionbarsherlock/widget/ActivityChooserModel$HistoryPersister:this$0	Lcom/actionbarsherlock/widget/ActivityChooserModel;
      //   396: invokestatic 50	com/actionbarsherlock/widget/ActivityChooserModel:access$400	(Lcom/actionbarsherlock/widget/ActivityChooserModel;)Ljava/lang/String;
      //   399: invokevirtual 148	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   402: invokevirtual 151	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   405: aload 10
      //   407: invokestatic 157	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   410: pop
      //   411: aload 6
      //   413: ifnull -171 -> 242
      //   416: aload 6
      //   418: invokevirtual 165	java/io/FileOutputStream:close	()V
      //   421: return
      //   422: astore 12
      //   424: return
      //   425: astore 8
      //   427: aload 6
      //   429: ifnull +8 -> 437
      //   432: aload 6
      //   434: invokevirtual 165	java/io/FileOutputStream:close	()V
      //   437: aload 8
      //   439: athrow
      //   440: astore 9
      //   442: goto -5 -> 437
      //   445: astore_3
      //   446: goto -244 -> 202
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	449	0	this	HistoryPersister
      //   7	196	1	localObject1	Object
      //   24	81	2	localArrayList	ArrayList
      //   201	4	3	localObject2	Object
      //   445	1	3	localObject3	Object
      //   206	31	4	localFileNotFoundException	java.io.FileNotFoundException
      //   45	388	6	localFileOutputStream	java.io.FileOutputStream
      //   50	205	7	localXmlSerializer	org.xmlpull.v1.XmlSerializer
      //   425	13	8	localObject4	Object
      //   440	1	9	localIOException1	java.io.IOException
      //   375	31	10	localIOException2	java.io.IOException
      //   422	1	12	localIOException3	java.io.IOException
      //   325	31	13	localIllegalStateException	IllegalStateException
      //   372	1	15	localIOException4	java.io.IOException
      //   275	31	16	localIllegalArgumentException	java.lang.IllegalArgumentException
      //   322	1	18	localIOException5	java.io.IOException
      //   92	10	20	i	int
      //   95	101	21	j	int
      //   114	57	22	localHistoricalRecord	ActivityChooserModel.HistoricalRecord
      //   272	1	29	localIOException6	java.io.IOException
      // Exception table:
      //   from	to	target	type
      //   10	25	201	finally
      //   202	204	201	finally
      //   27	47	206	java/io/FileNotFoundException
      //   266	271	272	java/io/IOException
      //   52	94	275	java/lang/IllegalArgumentException
      //   104	195	275	java/lang/IllegalArgumentException
      //   243	261	275	java/lang/IllegalArgumentException
      //   316	321	322	java/io/IOException
      //   52	94	325	java/lang/IllegalStateException
      //   104	195	325	java/lang/IllegalStateException
      //   243	261	325	java/lang/IllegalStateException
      //   366	371	372	java/io/IOException
      //   52	94	375	java/io/IOException
      //   104	195	375	java/io/IOException
      //   243	261	375	java/io/IOException
      //   416	421	422	java/io/IOException
      //   52	94	425	finally
      //   104	195	425	finally
      //   243	261	425	finally
      //   277	311	425	finally
      //   327	361	425	finally
      //   377	411	425	finally
      //   432	437	440	java/io/IOException
      //   25	27	445	finally
    }
  }
  
  public static abstract interface OnChooseActivityListener
  {
    public abstract boolean onChooseActivity(ActivityChooserModel paramActivityChooserModel, Intent paramIntent);
  }
  
  private static class SerialExecutor
    implements Executor
  {
    Runnable mActive;
    final LinkedList<Runnable> mTasks = new LinkedList();
    
    public void execute(final Runnable paramRunnable)
    {
      try
      {
        this.mTasks.offer(new Runnable()
        {
          public void run()
          {
            try
            {
              paramRunnable.run();
              return;
            }
            finally
            {
              ActivityChooserModel.SerialExecutor.this.scheduleNext();
            }
          }
        });
        if (this.mActive == null) {
          scheduleNext();
        }
        return;
      }
      finally
      {
        localObject = finally;
        throw localObject;
      }
    }
    
    protected void scheduleNext()
    {
      try
      {
        Runnable localRunnable = (Runnable)this.mTasks.poll();
        this.mActive = localRunnable;
        if (localRunnable != null) {
          this.mActive.run();
        }
        return;
      }
      finally {}
    }
  }
}


/* Location:
 * Qualified Name:     com.actionbarsherlock.widget.ActivityChooserModel
 * JD-Core Version:    0.7.0.1
 */
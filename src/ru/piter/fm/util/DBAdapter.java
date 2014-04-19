package ru.piter.fm.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Iterator;
import java.util.List;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;

public class DBAdapter
{
  private static String CREATE_TABLE = "CREATE TABLE  %1$s  ( id TEXT NOT NULL,name TEXT NOT NULL,range TEXT NOT NULL,radio TEXT NOT NULL,logo TEXT)";
  private static final String DATABASE_NAME = "PITERFM";
  private static final int DATABASE_VERSION = 2;
  private static String DROP_TABLE = "DROP TABLE IF EXISTS %1$s";
  public static String FAVOURITES_TABLE;
  private static String FIELD_ID = "id";
  private static String FIELD_LOGO;
  private static String FIELD_NAME = "name";
  private static String FIELD_RADIO;
  private static String FIELD_RANGE = "range";
  public static String MOSKVA_TABLE;
  public static String PITER_TABLE;
  private DatabaseHelper dbHelper;
  
  static
  {
    FIELD_RADIO = "radio";
    FIELD_LOGO = "logo";
    PITER_TABLE = "PiterFM";
    MOSKVA_TABLE = "MoskvaFM";
    FAVOURITES_TABLE = "Favourite";
  }
  
  public DBAdapter(Context paramContext)
  {
    this.dbHelper = new DatabaseHelper(paramContext, null);
  }
  
  public void addChannel(Channel paramChannel, Radio paramRadio)
  {
    SQLiteDatabase localSQLiteDatabase = getHelper().getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(FIELD_ID, paramChannel.getChannelId());
    localContentValues.put(FIELD_NAME, paramChannel.getName());
    localContentValues.put(FIELD_RANGE, paramChannel.getRange());
    localContentValues.put(FIELD_RADIO, paramChannel.getRadio().getName());
    localContentValues.put(FIELD_LOGO, paramChannel.getLogoUrl());
    localSQLiteDatabase.insert(paramRadio.getName(), null, localContentValues);
    Log.d("", "Add channel  " + paramChannel.getName() + " to " + paramRadio.getName() + " table");
  }
  
  public void deleteChannel(Channel paramChannel, Radio paramRadio)
  {
    SQLiteDatabase localSQLiteDatabase = getHelper().getWritableDatabase();
    String str = paramRadio.getName();
    localSQLiteDatabase.execSQL("delete from " + str + " where id = " + paramChannel.getChannelId());
    Log.d("PiterFM", "Delete channel " + paramChannel.getName() + " from " + str + " table");
  }
  
  public void deleteChannels(Radio paramRadio)
  {
    SQLiteDatabase localSQLiteDatabase = getHelper().getWritableDatabase();
    String str = paramRadio.getName();
    localSQLiteDatabase.execSQL("delete from " + str);
    Log.d("PiterFM", "Delete all channels from " + str + " table");
  }
  
  /* Error */
  public Channel getChannel(String paramString, Radio paramRadio)
  {
    // Byte code:
    //   0: aload_2
    //   1: invokevirtual 114	ru/piter/fm/radio/Radio:getName	()Ljava/lang/String;
    //   4: astore_3
    //   5: aconst_null
    //   6: astore 4
    //   8: aload_0
    //   9: invokevirtual 85	ru/piter/fm/util/DBAdapter:getHelper	()Lru/piter/fm/util/DBAdapter$DatabaseHelper;
    //   12: invokevirtual 89	ru/piter/fm/util/DBAdapter$DatabaseHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: astore 5
    //   17: aload 5
    //   19: iconst_1
    //   20: aload_3
    //   21: aconst_null
    //   22: ldc 170
    //   24: iconst_1
    //   25: anewarray 172	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: aload_1
    //   31: aastore
    //   32: aconst_null
    //   33: aconst_null
    //   34: aconst_null
    //   35: aconst_null
    //   36: invokevirtual 176	android/database/sqlite/SQLiteDatabase:query	(ZLjava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   39: astore 4
    //   41: aload 4
    //   43: invokeinterface 182 1 0
    //   48: istore 9
    //   50: aconst_null
    //   51: astore 10
    //   53: iload 9
    //   55: ifeq +97 -> 152
    //   58: aload 10
    //   60: pop
    //   61: new 94	ru/piter/fm/radio/Channel
    //   64: dup
    //   65: invokespecial 183	ru/piter/fm/radio/Channel:<init>	()V
    //   68: astore 10
    //   70: aload 10
    //   72: aload 4
    //   74: iconst_0
    //   75: invokeinterface 187 2 0
    //   80: invokevirtual 190	ru/piter/fm/radio/Channel:setChannelId	(Ljava/lang/String;)V
    //   83: aload 10
    //   85: aload 4
    //   87: iconst_1
    //   88: invokeinterface 187 2 0
    //   93: invokevirtual 193	ru/piter/fm/radio/Channel:setName	(Ljava/lang/String;)V
    //   96: aload 10
    //   98: aload 4
    //   100: iconst_2
    //   101: invokeinterface 187 2 0
    //   106: invokevirtual 196	ru/piter/fm/radio/Channel:setRange	(Ljava/lang/String;)V
    //   109: aload 10
    //   111: aload 4
    //   113: iconst_3
    //   114: invokeinterface 187 2 0
    //   119: invokestatic 201	ru/piter/fm/radio/RadioFactory:getRadio	(Ljava/lang/String;)Lru/piter/fm/radio/Radio;
    //   122: invokevirtual 204	ru/piter/fm/radio/Channel:setRadio	(Lru/piter/fm/radio/Radio;)V
    //   125: aload 10
    //   127: aload 4
    //   129: iconst_4
    //   130: invokeinterface 187 2 0
    //   135: invokevirtual 207	ru/piter/fm/radio/Channel:setLogoUrl	(Ljava/lang/String;)V
    //   138: aload 4
    //   140: invokeinterface 210 1 0
    //   145: istore 12
    //   147: iload 12
    //   149: ifne -91 -> 58
    //   152: aload 4
    //   154: ifnull +10 -> 164
    //   157: aload 4
    //   159: invokeinterface 213 1 0
    //   164: aload 10
    //   166: astore 8
    //   168: aload 8
    //   170: areturn
    //   171: astore 7
    //   173: aload 7
    //   175: invokevirtual 216	android/database/sqlite/SQLiteException:printStackTrace	()V
    //   178: aconst_null
    //   179: astore 8
    //   181: aload 4
    //   183: ifnull -15 -> 168
    //   186: aload 4
    //   188: invokeinterface 213 1 0
    //   193: aconst_null
    //   194: areturn
    //   195: astore 6
    //   197: aload 4
    //   199: ifnull +10 -> 209
    //   202: aload 4
    //   204: invokeinterface 213 1 0
    //   209: aload 6
    //   211: athrow
    //   212: astore 6
    //   214: goto -17 -> 197
    //   217: astore 7
    //   219: goto -46 -> 173
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	this	DBAdapter
    //   0	222	1	paramString	String
    //   0	222	2	paramRadio	Radio
    //   4	17	3	str	String
    //   6	197	4	localCursor	android.database.Cursor
    //   15	3	5	localSQLiteDatabase	SQLiteDatabase
    //   195	15	6	localObject1	Object
    //   212	1	6	localObject2	Object
    //   171	3	7	localSQLiteException1	android.database.sqlite.SQLiteException
    //   217	1	7	localSQLiteException2	android.database.sqlite.SQLiteException
    //   166	14	8	localChannel1	Channel
    //   48	6	9	bool1	boolean
    //   51	114	10	localChannel2	Channel
    //   145	3	12	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   17	50	171	android/database/sqlite/SQLiteException
    //   70	147	171	android/database/sqlite/SQLiteException
    //   17	50	195	finally
    //   70	147	195	finally
    //   173	178	195	finally
    //   61	70	212	finally
    //   61	70	217	android/database/sqlite/SQLiteException
  }
  
  public DatabaseHelper getHelper()
  {
    try
    {
      DatabaseHelper localDatabaseHelper = this.dbHelper;
      return localDatabaseHelper;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void insertChannels(List<Channel> paramList, Radio paramRadio)
  {
    SQLiteDatabase localSQLiteDatabase = getHelper().getWritableDatabase();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Channel localChannel = (Channel)localIterator.next();
      ContentValues localContentValues = new ContentValues();
      localContentValues.put(FIELD_ID, localChannel.getChannelId());
      localContentValues.put(FIELD_NAME, localChannel.getName());
      localContentValues.put(FIELD_RANGE, localChannel.getRange());
      localContentValues.put(FIELD_RADIO, localChannel.getRadio().getName());
      localContentValues.put(FIELD_LOGO, localChannel.getLogoUrl());
      localSQLiteDatabase.insert(paramRadio.getName(), null, localContentValues);
    }
  }
  
  /* Error */
  public List<Channel> selectAllChannels(Radio paramRadio)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 114	ru/piter/fm/radio/Radio:getName	()Ljava/lang/String;
    //   4: astore_2
    //   5: ldc 125
    //   7: new 127	java/lang/StringBuilder
    //   10: dup
    //   11: invokespecial 128	java/lang/StringBuilder:<init>	()V
    //   14: ldc 237
    //   16: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: aload_2
    //   20: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: invokevirtual 141	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   26: invokestatic 147	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   29: pop
    //   30: aconst_null
    //   31: astore 4
    //   33: aload_0
    //   34: invokevirtual 85	ru/piter/fm/util/DBAdapter:getHelper	()Lru/piter/fm/util/DBAdapter$DatabaseHelper;
    //   37: invokevirtual 89	ru/piter/fm/util/DBAdapter$DatabaseHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   40: astore 5
    //   42: aload 5
    //   44: iconst_1
    //   45: aload_2
    //   46: aconst_null
    //   47: aconst_null
    //   48: aconst_null
    //   49: aconst_null
    //   50: aconst_null
    //   51: aconst_null
    //   52: aconst_null
    //   53: invokevirtual 176	android/database/sqlite/SQLiteDatabase:query	(ZLjava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   56: astore 4
    //   58: aload 4
    //   60: invokeinterface 182 1 0
    //   65: istore 9
    //   67: aconst_null
    //   68: astore 10
    //   70: iload 9
    //   72: ifeq +117 -> 189
    //   75: new 239	java/util/ArrayList
    //   78: dup
    //   79: invokespecial 240	java/util/ArrayList:<init>	()V
    //   82: astore 11
    //   84: new 94	ru/piter/fm/radio/Channel
    //   87: dup
    //   88: invokespecial 183	ru/piter/fm/radio/Channel:<init>	()V
    //   91: astore 12
    //   93: aload 12
    //   95: aload 4
    //   97: iconst_0
    //   98: invokeinterface 187 2 0
    //   103: invokevirtual 190	ru/piter/fm/radio/Channel:setChannelId	(Ljava/lang/String;)V
    //   106: aload 12
    //   108: aload 4
    //   110: iconst_1
    //   111: invokeinterface 187 2 0
    //   116: invokevirtual 193	ru/piter/fm/radio/Channel:setName	(Ljava/lang/String;)V
    //   119: aload 12
    //   121: aload 4
    //   123: iconst_2
    //   124: invokeinterface 187 2 0
    //   129: invokevirtual 196	ru/piter/fm/radio/Channel:setRange	(Ljava/lang/String;)V
    //   132: aload 12
    //   134: aload 4
    //   136: iconst_3
    //   137: invokeinterface 187 2 0
    //   142: invokestatic 201	ru/piter/fm/radio/RadioFactory:getRadio	(Ljava/lang/String;)Lru/piter/fm/radio/Radio;
    //   145: invokevirtual 204	ru/piter/fm/radio/Channel:setRadio	(Lru/piter/fm/radio/Radio;)V
    //   148: aload 12
    //   150: aload 4
    //   152: iconst_4
    //   153: invokeinterface 187 2 0
    //   158: invokevirtual 207	ru/piter/fm/radio/Channel:setLogoUrl	(Ljava/lang/String;)V
    //   161: aload 11
    //   163: aload 12
    //   165: invokeinterface 244 2 0
    //   170: pop
    //   171: aload 4
    //   173: invokeinterface 210 1 0
    //   178: istore 14
    //   180: iload 14
    //   182: ifne -98 -> 84
    //   185: aload 11
    //   187: astore 10
    //   189: aload 4
    //   191: ifnull +10 -> 201
    //   194: aload 4
    //   196: invokeinterface 213 1 0
    //   201: aload 10
    //   203: astore 8
    //   205: aload 8
    //   207: areturn
    //   208: astore 7
    //   210: aload 7
    //   212: invokevirtual 216	android/database/sqlite/SQLiteException:printStackTrace	()V
    //   215: aconst_null
    //   216: astore 8
    //   218: aload 4
    //   220: ifnull -15 -> 205
    //   223: aload 4
    //   225: invokeinterface 213 1 0
    //   230: aconst_null
    //   231: areturn
    //   232: astore 6
    //   234: aload 4
    //   236: ifnull +10 -> 246
    //   239: aload 4
    //   241: invokeinterface 213 1 0
    //   246: aload 6
    //   248: athrow
    //   249: astore 6
    //   251: goto -17 -> 234
    //   254: astore 7
    //   256: goto -46 -> 210
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	259	0	this	DBAdapter
    //   0	259	1	paramRadio	Radio
    //   4	42	2	str	String
    //   31	209	4	localCursor	android.database.Cursor
    //   40	3	5	localSQLiteDatabase	SQLiteDatabase
    //   232	15	6	localObject1	Object
    //   249	1	6	localObject2	Object
    //   208	3	7	localSQLiteException1	android.database.sqlite.SQLiteException
    //   254	1	7	localSQLiteException2	android.database.sqlite.SQLiteException
    //   203	14	8	localObject3	Object
    //   65	6	9	bool1	boolean
    //   68	134	10	localObject4	Object
    //   82	104	11	localArrayList	java.util.ArrayList
    //   91	73	12	localChannel	Channel
    //   178	3	14	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   42	67	208	android/database/sqlite/SQLiteException
    //   75	84	208	android/database/sqlite/SQLiteException
    //   42	67	232	finally
    //   75	84	232	finally
    //   210	215	232	finally
    //   84	180	249	finally
    //   84	180	254	android/database/sqlite/SQLiteException
  }
  
  public void updateChannels(List<Channel> paramList, Radio paramRadio)
  {
    SQLiteDatabase localSQLiteDatabase = getHelper().getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    String str1 = DROP_TABLE;
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = paramRadio.getName();
    localSQLiteDatabase.execSQL(String.format(str1, arrayOfObject1));
    String str2 = CREATE_TABLE;
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = paramRadio.getName();
    localSQLiteDatabase.execSQL(String.format(str2, arrayOfObject2));
    insertChannels(paramList, paramRadio);
    localSQLiteDatabase.setTransactionSuccessful();
    localSQLiteDatabase.endTransaction();
  }
  
  private static class DatabaseHelper
    extends SQLiteOpenHelper
  {
    private DatabaseHelper(Context paramContext)
    {
      super("PITERFM", null, 2);
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      Log.d("PiterFM", "Create tables!");
      String str1 = DBAdapter.CREATE_TABLE;
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = DBAdapter.PITER_TABLE;
      paramSQLiteDatabase.execSQL(String.format(str1, arrayOfObject1));
      String str2 = DBAdapter.CREATE_TABLE;
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = DBAdapter.MOSKVA_TABLE;
      paramSQLiteDatabase.execSQL(String.format(str2, arrayOfObject2));
      String str3 = DBAdapter.CREATE_TABLE;
      Object[] arrayOfObject3 = new Object[1];
      arrayOfObject3[0] = DBAdapter.FAVOURITES_TABLE;
      paramSQLiteDatabase.execSQL(String.format(str3, arrayOfObject3));
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      if (paramInt2 == 2)
      {
        Log.d("DBAdapter : ", "On upgrade database, drop old tables: PITER_CHANNELS, MOSKVA_CHANNELS, FAVOURITES_CHANNELS");
        paramSQLiteDatabase.execSQL(String.format(DBAdapter.DROP_TABLE, new Object[] { "PITER_CHANNELS" }));
        paramSQLiteDatabase.execSQL(String.format(DBAdapter.DROP_TABLE, new Object[] { "MOSKVA_CHANNELS" }));
        paramSQLiteDatabase.execSQL(String.format(DBAdapter.DROP_TABLE, new Object[] { "FAVOURITES_CHANNELS" }));
      }
      String str1 = DBAdapter.DROP_TABLE;
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = DBAdapter.PITER_TABLE;
      paramSQLiteDatabase.execSQL(String.format(str1, arrayOfObject1));
      String str2 = DBAdapter.DROP_TABLE;
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = DBAdapter.MOSKVA_TABLE;
      paramSQLiteDatabase.execSQL(String.format(str2, arrayOfObject2));
      String str3 = DBAdapter.DROP_TABLE;
      Object[] arrayOfObject3 = new Object[1];
      arrayOfObject3[0] = DBAdapter.FAVOURITES_TABLE;
      paramSQLiteDatabase.execSQL(String.format(str3, arrayOfObject3));
      onCreate(paramSQLiteDatabase);
    }
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.util.DBAdapter
 * JD-Core Version:    0.7.0.1
 */
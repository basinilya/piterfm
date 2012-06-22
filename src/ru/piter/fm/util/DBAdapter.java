package ru.piter.fm.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ru.piter.fm.radio.Channel;
import ru.piter.fm.radio.Radio;
import ru.piter.fm.radio.RadioFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 16.09.2010
 * Time: 0:17:36
 * To change this template use File | SettingsActivity | File Templates.
 */
public class DBAdapter {

    private static final String DATABASE_NAME = "PITERFM";
    private static final int DATABASE_VERSION = 2;

    private static String FIELD_ID = "id";
    private static String FIELD_NAME = "name";
    private static String FIELD_RANGE = "range";
    private static String FIELD_RADIO = "radio";
    private static String FIELD_LOGO = "logo";

    public static String PITER_TABLE = RadioFactory.PITER_FM;
    public static String MOSKVA_TABLE = RadioFactory.MOSKVA_FM;
    public static String FAVOURITES_TABLE = RadioFactory.FAVOURITE;

    private static String CREATE_TABLE = "CREATE TABLE " + " %1$s " + " ( id TEXT NOT NULL," +
            "name TEXT NOT NULL," +
            "range TEXT NOT NULL," +
            "radio TEXT NOT NULL," +
            "logo TEXT)";

    private static String DROP_TABLE = "DROP TABLE IF EXISTS %1$s";


    private DatabaseHelper dbHelper;


    public DBAdapter(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public synchronized DatabaseHelper getHelper() {
        return dbHelper;
    }

    public void updateChannels(List<Channel> channels, Radio radio) {
        SQLiteDatabase db = getHelper().getWritableDatabase();
        db.beginTransaction();
        db.execSQL(String.format(DROP_TABLE, radio.getName()));
        db.execSQL(String.format(CREATE_TABLE,radio.getName()));
        insertChannels(channels, radio);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }

    public void deleteChannel(Channel channel, Radio radio) {
        SQLiteDatabase db = getHelper().getWritableDatabase();
        String table = radio.getName();
        db.execSQL("delete from " + table + " where id = " + channel.getChannelId());
        Log.d("", "Delete channel " + channel.getName() + " from " + table + " table");
    }

    public void insertChannels(List<Channel> channels, Radio radio) {
        SQLiteDatabase db = getHelper().getWritableDatabase();
        for (Channel channel : channels) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(FIELD_ID, channel.getChannelId());
            initialValues.put(FIELD_NAME, channel.getName());
            initialValues.put(FIELD_RANGE, channel.getRange());
            initialValues.put(FIELD_RADIO, channel.getRadio().getName());
            initialValues.put(FIELD_LOGO, channel.getLogoUrl());
            db.insert(radio.getName(), null, initialValues);
        }
        // if (!db.inTransaction())
        // db.close();
    }

    public void addChannel(Channel channel, Radio radio) {
        SQLiteDatabase db = getHelper().getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(FIELD_ID, channel.getChannelId());
        initialValues.put(FIELD_NAME, channel.getName());
        initialValues.put(FIELD_RANGE, channel.getRange());
        initialValues.put(FIELD_RADIO, channel.getRadio().getName());
        initialValues.put(FIELD_LOGO, channel.getLogoUrl());
        db.insert(radio.getName(), null, initialValues);
        Log.d("", "Add channel  " + channel.getName() + " to " + radio.getName() + " table");
        // if (!db.inTransaction())
        // db.close();
    }


    public Channel getChannel(String channelId, Radio radio) {
        String table = radio.getName();
        Channel ch = null;
        Cursor cursor = null;
        SQLiteDatabase db = getHelper().getWritableDatabase();
        try {
            cursor = db.query(true, table, null, "id=?", new String[]{channelId}, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    ch = new Channel();
                    ch.setChannelId(cursor.getString(0));
                    ch.setName(cursor.getString(1));
                    ch.setRange(cursor.getString(2));
                    ch.setRadio(RadioFactory.getRadio(cursor.getString(3)));
                    ch.setLogoUrl(cursor.getString(4));
                } while (cursor.moveToNext());

            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
          //  db.close();
        }
        return ch;
    }


    public List<Channel> selectAllChannels(Radio radio) {
        String table = radio.getName();
        Log.d("", "get all channels for table " + table);
        Cursor cursor = null;
        List<Channel> channels = null;
        SQLiteDatabase db = getHelper().getWritableDatabase();
        try {
            cursor = db.query(true, table, null, null, null, null, null, null, null);
            // maybe statements will be better?
            if (cursor.moveToFirst()) {
                channels = new ArrayList<Channel>();
                do {
                    Channel ch = new Channel();
                    ch.setChannelId(cursor.getString(0));
                    ch.setName(cursor.getString(1));
                    ch.setRange(cursor.getString(2));
                    ch.setRadio(RadioFactory.getRadio(cursor.getString(3)));
                    ch.setLogoUrl(cursor.getString(4));
                    channels.add(ch);
                } while (cursor.moveToNext());

            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
            // db.close();
        }
        return channels;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {


        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            System.out.println("Create tables!");
            sqLiteDatabase.execSQL(String.format(CREATE_TABLE, PITER_TABLE));
            sqLiteDatabase.execSQL(String.format(CREATE_TABLE, MOSKVA_TABLE));
            sqLiteDatabase.execSQL(String.format(CREATE_TABLE, FAVOURITES_TABLE));

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            if (newVersion == 2){
                Log.d("DBAdapter : ", "On upgrade database, drop old tables: PITER_CHANNELS, MOSKVA_CHANNELS, FAVOURITES_CHANNELS");
                sqLiteDatabase.execSQL(String.format(DROP_TABLE, "PITER_CHANNELS"));
                sqLiteDatabase.execSQL(String.format(DROP_TABLE, "MOSKVA_CHANNELS"));
                sqLiteDatabase.execSQL(String.format(DROP_TABLE, "FAVOURITES_CHANNELS"));
            }
            sqLiteDatabase.execSQL(String.format(DROP_TABLE, PITER_TABLE));
            sqLiteDatabase.execSQL(String.format(DROP_TABLE, MOSKVA_TABLE));
            sqLiteDatabase.execSQL(String.format(DROP_TABLE, FAVOURITES_TABLE));
            onCreate(sqLiteDatabase);
        }

    }


}

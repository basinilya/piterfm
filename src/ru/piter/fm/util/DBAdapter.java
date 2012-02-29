package ru.piter.fm.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import ru.piter.fm.radio.Channel;

import java.io.ByteArrayOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 16.09.2010
 * Time: 0:17:36
 * To change this template use File | Settings | File Templates.
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "PITERFM";

    private static String FIELD_CHANNEL_ID = "channelId";
    private static String FIELD_NAME = "channelName";
    private static String FIELD_RANGE = "range";
    private static String FIELD_LOGO = "logo";

    public static String PITER_TABLE = "PITER_CHANNELS";
    public static String MOSKVA_TABLE = "MOSKVA_CHANNELS";

    private static String CREATE_TABLE_PITER = "CREATE TABLE " + PITER_TABLE + " ( channelId TEXT NOT NULL, channelName TEXT NOT NULL, range TEXT NOT NULL, logo BLOB)";
    private static String CREATE_TABLE_MOSKVA = "CREATE TABLE " + MOSKVA_TABLE + " ( channelId TEXT NOT NULL, channelName TEXT NOT NULL, range TEXT NOT NULL, logo BLOB)";
    private static String DROP_TABLE_PITER = "DROP TABLE IF EXISTS " + PITER_TABLE;
    private static String DROP_TABLE_MOSKVA = "DROP TABLE IF EXISTS " + MOSKVA_TABLE;

    private Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }


    public DBAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();

    }


    public void updateChannels(List<Channel> channels, long radioId) {
        open();
        db.beginTransaction();
        db.execSQL(getDropSql(radioId));
        db.execSQL(getCreateSql(radioId));
        insertChannels(channels, radioId);
        db.setTransactionSuccessful();
        db.endTransaction();
        close();
    }

    public void insertChannels(List<Channel> channels, long radioId) {
        open();
        for (Channel channel : channels) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(FIELD_CHANNEL_ID, channel.getChannelId());
            initialValues.put(FIELD_NAME, channel.getName());
            initialValues.put(FIELD_RANGE, channel.getRange());
            initialValues.put(FIELD_LOGO, bitmapToBytes(channel.getLogo()));
            db.insert(getTableName(radioId), null, initialValues);
        }
        if (!db.inTransaction())
            close();
    }


    private String getTableName(long radioId) {
        if (radioId == 1L) return PITER_TABLE;
        return MOSKVA_TABLE;
    }

    private String getCreateSql(long radioId) {
        if (radioId == 1L) return CREATE_TABLE_PITER;
        return CREATE_TABLE_MOSKVA;
    }

    private String getDropSql(long radioId) {
        if (radioId == 1L) return DROP_TABLE_PITER;
        return DROP_TABLE_MOSKVA;
    }


    public List<Channel> selectAllChannels(long radioId) {
       // Log.d(TAG, "get all channels for table " + getTableName(radioId));
        Cursor cursor = null;
        List<Channel> channels = null;
        try {
            open();
            cursor = db.query(true, getTableName(radioId), null, null, null, null, null, null, null);
            // maybe statements will be better?
            if (cursor.moveToFirst()) {
                channels = new ArrayList<Channel>();
                do {
                    Channel ch = new Channel();
                    ch.setChannelId(cursor.getString(0));
                    ch.setName(cursor.getString(1));
                    ch.setRange(cursor.getString(2));
                    ch.setLogo(bytesToBitmap(cursor.getBlob(3)));
                    channels.add(ch);
                } while (cursor.moveToNext());

            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            cursor.close();
            close();
        }


        return channels;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {


        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_PITER);
            sqLiteDatabase.execSQL(CREATE_TABLE_MOSKVA);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL(DROP_TABLE_PITER);
            sqLiteDatabase.execSQL(DROP_TABLE_MOSKVA);
            onCreate(sqLiteDatabase);
        }

    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}

package com.guilherme.recordphonecall;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dell on 14/08/2018.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static DBOpenHelper dbOpen;
    private static SQLiteDatabase dbSql;

    public static SQLiteDatabase getDataBase()
    {
        return dbSql;
    }

    public static void initDataBase(Context context)
    {
        if (dbOpen == null)
        {
            dbOpen = new DBOpenHelper(context ,"db", null, 1);
            dbSql = dbOpen.getWritableDatabase();
        }

    }

    private static final String	records_logs =	"create	table records_logs(" +
            "ID	integer	primary	key	autoincrement,	" +
            "PHONE	string," +
            "FILE_NAME	string," +
            "DURATION	NUMERIC,	" +
            "CREATION_DATE	long," +
            "SYNC_DATE	long," +
            "REC_SYNC integer" +
            ")";
    private static final String	syc_logs =	"create	table sync_token (" +
            "ID	integer	primary	key	autoincrement,	" +
            "CREATION_DATE	long, " +
            "user	string, " +
            "password	string, " +
            "TOKEN	string " +
            ")";



    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(records_logs);
        sqLiteDatabase.execSQL(syc_logs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}

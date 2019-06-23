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
            "id	integer	primary	key	autoincrement,	" +
            "phone	string," +
            "file_name_path	string," +
            "file_Name	string," +
            "duration	NUMERIC,	" +
            "creation_date	long," +
            "sync_date	long," +
            "is_email_sent	integer," +
            "is_transcripted	long," +
            "error_on_transcripted	long," +
            "is_rec_sync integer, " +
            "id_audio_server	long," +
            "transcripted_text	string," +
            "score	string, " +
            "magnitude	string, " +
            "contact_name	string " +
            ")";
    private static final String	syc_logs =	"create	table sync_token (" +
            "id	integer	primary	key	autoincrement,	" +
            "creation_date	long, " +
            "user	string, " +
            "password	string, " +
            "token	string " +
            ")";

    private static final String	configurations =	"create	table appconfigurations (" +
            "id	integer	primary	key	autoincrement,	" +
            "smtp_host	string, " +
            "user	string, " +
            "password	string, " +
            "email_address	integer, " +
            "email_subject	string, " +
            "email_body	string, " +
            "port	string, " +
            "sync_with_server	integer, " +
            "url_server	string " +

            ")";

    private static final String	notes =	"create	table notes (" +
            "id	integer	primary	key	autoincrement,	" +
            "phone	string, " +
            "creation_date	long," +
            "display_once integer, " +
            "enabled integer, " +
            "text_notification	string" +
            ")";


    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(records_logs);
        sqLiteDatabase.execSQL(syc_logs);
        sqLiteDatabase.execSQL(configurations);
        sqLiteDatabase.execSQL(notes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}

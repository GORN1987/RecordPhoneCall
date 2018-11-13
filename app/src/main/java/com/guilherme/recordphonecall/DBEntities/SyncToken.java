package com.guilherme.recordphonecall.DBEntities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guilherme.recordphonecall.DBOpenHelper;

import java.io.File;
import java.util.Calendar;

/**
 * Created by dell on 06/09/2018.
 */

public class SyncToken {

    public Integer ID = 0;
    public Long CREATION_DATE;
    public String TOKEN;
    public String USER;
    public String PASSWORD;

    public SyncToken(Integer id)
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT ID, CREATION_DATE, TOKEN, USER, PASSWORD FROM sync_token WHERE ID = '" + id.toString() +"'", null);
        records.moveToFirst();

        ID = records.getInt(0);

        CREATION_DATE = records.getLong(1);
        TOKEN = records.getString(2);
        USER = records.getString(3);
        PASSWORD = records.getString(4);
    }

    public SyncToken()
    {

    }

    public static void DeleteTokensAndLogOut()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("sync_token","", null);
    }

    public static SyncToken getLastSyncToken()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT ID, CREATION_DATE, TOKEN, USER, PASSWORD FROM sync_token WHERE CREATION_DATE = (SELECT MAX(CREATION_DATE) FROM sync_token)", null);
        records.moveToFirst();
        if (records.getCount() > 0)
        {
            SyncToken recToken = new SyncToken();
            recToken.CREATION_DATE = records.getLong(1);
            recToken.TOKEN = records.getString(2);
            recToken.USER = records.getString(3);
            recToken.PASSWORD = records.getString(4);
            recToken.Update();

            return recToken;
        }
        else
        {
            return null;
        }

    }

    public void Update()
    {

        SQLiteDatabase db = DBOpenHelper.getDataBase();

        ContentValues values = new ContentValues();

        values.put("USER",USER);
        values.put("PASSWORD",PASSWORD);

        values.put("CREATION_DATE", Calendar.getInstance().getTimeInMillis());
        if (ID == 0)
        {

            db.insert("sync_token","", values);
            Cursor records = db.rawQuery("SELECT MAX(ID) MAX_ID FROM sync_token",null);

            records.moveToFirst();
            ID  = records.getInt(0);
        }
        else
        {
            db.update("sync_token",values,"ID = ?", new String[] {ID.toString()});
        }

    }


    public void Delete()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("sync_token", "ID = " + ID.toString(), null);

    }

}

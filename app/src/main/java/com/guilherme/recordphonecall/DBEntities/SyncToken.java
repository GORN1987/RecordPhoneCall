package com.guilherme.recordphonecall.DBEntities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guilherme.recordphonecall.DBOpenHelper;

import java.util.Calendar;

/**
 * Created by dell on 06/09/2018.
 */

public class SyncToken {

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private Integer id = 0;
    private Long creationDate;
    private String token;
    private String user;
    private String password;

    public SyncToken(Integer id)
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT id, creation_date, token, user, password FROM sync_token WHERE id = '" + id.toString() +"'", null);
        records.moveToFirst();

        this.id = records.getInt(0);

        creationDate = records.getLong(1);
        token = records.getString(2);
        user = records.getString(3);
        password = records.getString(4);
    }

    public SyncToken()
    {

    }

    public static void deleteTokensAndLogOut()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("sync_token","", null);
    }

    public static SyncToken getLastSyncToken()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT id, creation_date, token, user, password FROM sync_token WHERE creation_date = (SELECT MAX(creation_date) FROM sync_token)", null);
        records.moveToFirst();
        if (records.getCount() > 0)
        {
            SyncToken recToken = new SyncToken();
            recToken.creationDate = records.getLong(1);
            recToken.token = records.getString(2);
            recToken.user = records.getString(3);
            recToken.password = records.getString(4);
            recToken.update();

            return recToken;
        }
        else
        {
            return null;
        }

    }

    public void update()
    {

        SQLiteDatabase db = DBOpenHelper.getDataBase();

        ContentValues values = new ContentValues();

        values.put("user", user);
        values.put("password", password);

        values.put("creation_date", Calendar.getInstance().getTimeInMillis());
        if (id == 0)
        {

            db.insert("sync_token","", values);
            Cursor records = db.rawQuery("SELECT MAX(ID) MAX_ID FROM sync_token",null);

            records.moveToFirst();
            id = records.getInt(0);
        }
        else
        {
            db.update("sync_token",values,"id = ?", new String[] {id.toString()});
        }

    }


    public void delete()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("sync_token", "id = " + id.toString(), null);

    }

}

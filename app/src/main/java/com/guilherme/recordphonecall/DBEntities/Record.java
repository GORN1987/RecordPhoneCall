package com.guilherme.recordphonecall.DBEntities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guilherme.recordphonecall.DBOpenHelper;

import java.io.File;
import java.util.Calendar;

/**
 * Created by dell on 21/08/2018.
 */

public class Record {
    public Integer ID = 0;
    public String PHONE;
    public String FILE_NAME;
    public Float DURATION;
    public Long CREATION_DATE;
    public int REC_SYNC;


    public Record(Integer id)
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT ID, PHONE, FILE_NAME, DURATION, CREATION_DATE, REC_SYNC FROM records_logs WHERE ID = '" + id.toString() +"'", null);
        records.moveToFirst();

        ID = records.getInt(0);
        PHONE = records.getString(1);
        FILE_NAME = records.getString(2);
        DURATION = records.getFloat(3);
        CREATION_DATE = records.getLong(4);
        REC_SYNC = records.getInt(5);
    }

    public Record()
    {

    }

    public void Update()
    {

        SQLiteDatabase db = DBOpenHelper.getDataBase();

        ContentValues values = new ContentValues();
        values.put("PHONE",PHONE);
        values.put("DURATION",DURATION);
        values.put("FILE_NAME",FILE_NAME);
        values.put("REC_SYNC",REC_SYNC);
        values.put("CREATION_DATE", Calendar.getInstance().getTimeInMillis());
        if (ID == 0)
        {

            db.insert("records_logs","", values);
            Cursor records = db.rawQuery("SELECT MAX(ID) MAX_ID FROM records_logs",null);

            records.moveToFirst();
            ID  = records.getInt(0);
        }
        else
        {
            db.update("records_logs",values,"ID = ?", new String[] {ID.toString()});
        }

    }


    public void Delete()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("records_logs", "ID = " + ID.toString(), null);
        File file = new File(FILE_NAME);
        file.delete();
    }

}

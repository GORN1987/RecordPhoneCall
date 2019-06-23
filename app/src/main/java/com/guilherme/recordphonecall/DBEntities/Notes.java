package com.guilherme.recordphonecall.DBEntities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guilherme.recordphonecall.DBOpenHelper;

/**
 * Created by dell on 01/02/2019.
 */

public class Notes {


    private Integer id = 0;
    private Integer displayOnce = 0;
    private String phone;
    private Integer enabled;
    private Long creationDate;
    private String textNotification = "";

    private Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDisplayOnce(Integer displayOnce) {
        this.displayOnce = displayOnce;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
    }

    public Integer getDisplayOnce() {
        return displayOnce;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public String getTextNotification() {
        return textNotification;
    }


    public Notes() {

    }


    public Notes(String phone) {


        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT id,phone, display_once,enabled, creation_date, text_notification  FROM notes WHERE phone = '" + phone.toString() + "'", null);
        fillData(records);
    }

    private void fillData(Cursor records) {
        if (records.getCount() > 0) {
            records.moveToFirst();

            id = records.getInt(0);

            phone = records.getString(1);
            displayOnce = records.getInt(2);
            enabled = records.getInt(3);
            creationDate = records.getLong(4);
            textNotification = records.getString(5);

        }

    }


    public static void deleteConfiguration() {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("notes", "", null);
    }


    public void update() {

        SQLiteDatabase db = DBOpenHelper.getDataBase();

        ContentValues values = new ContentValues();

        values.put("phone", phone);
        values.put("display_once", displayOnce);
        values.put("enabled", enabled);
        values.put("creation_date", creationDate);
        values.put("text_notification", textNotification);
        if (id == 0) {

            db.insert("notes", "", values);
            Cursor records = db.rawQuery("SELECT MAX(ID) MAX_ID FROM notes", null);

            records.moveToFirst();
            id = records.getInt(0);

        } else {
            db.update("notes", values, "id = ?", new String[]{id.toString()});
        }

    }


    public void delete() {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("notes", "id = " + id.toString(), null);

    }
}

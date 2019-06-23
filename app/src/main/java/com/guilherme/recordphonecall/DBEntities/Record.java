package com.guilherme.recordphonecall.DBEntities;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.content.Context;

import com.guilherme.recordphonecall.DBOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dell on 21/08/2018.
 */

public class Record {

    public Integer id = 0;

    public Integer getId() {
        return id;
    }

    public String getPHONE() {
        return phone;
    }

    public String getContactName() {
        return contactName;
    }

    public String getFileNamePath() {
        return fileNamePath;
    }

    public String getFileName() {
        return fileName;
    }

    public Float getDuration() {
        return duration;
    }

    public Long getCreationDate() {
        return creationDate;
    }



    public int getIsTranscripted() {
        return isTranscripted;
    }

    public int getErrorOnTranscripted() {
        return errorOnTranscripted;
    }

    public Long getSyncDate() {
        return syncDate;
    }

    public Boolean getRenderGroup() {
        return renderGroup;
    }

    public Long getIdAudioServer() {
        return idAudioServer;
    }

    public String getTranscriptedText() {
        return transcriptedText;
    }

    public String getScore() {
        return score;
    }


    public void setFileNamePath(String fileNamePath) {
        this.fileNamePath = fileNamePath;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }




    public void setIsTranscripted(int isTranscripted) {
        this.isTranscripted = isTranscripted;
    }

    public void setErrorOnTranscripted(int errorOnTranscripted) {
        this.errorOnTranscripted = errorOnTranscripted;
    }

    public void setSyncDate(Long syncDate) {
        this.syncDate = syncDate;
    }

    public void setRenderGroup(Boolean renderGroup) {
        this.renderGroup = renderGroup;
    }

    public void setIdAudioServer(Long idAudioServer) {
        this.idAudioServer = idAudioServer;
    }

    public void setTranscriptedText(String transcriptedText) {
        this.transcriptedText = transcriptedText;
    }

    public void setScore(String score) {
        this.score = score;
    }
    public void setIsEmailSent(Boolean isEmailSent) {
        this.isEmailSent = isEmailSent;
    }

    public Boolean getIsEmailSent() {
        return isEmailSent;
    }
    public String getPhone() {
        return phone;
    }
    public void setRecSync(Boolean recSync) {
        isRecSync = recSync;
    }

    public Boolean getRecSync() {
        return isRecSync;
    }

    public Boolean getEmailSent() {
        return isEmailSent;
    }

    public void setEmailSent(Boolean emailSent) {
        isEmailSent = emailSent;
    }



    private String fileName;
    private Float duration;
    private Long creationDate;
    private Boolean isRecSync = false;
    private Boolean isEmailSent = false;
    public int isTranscripted;
    private int errorOnTranscripted;
    private Long syncDate;
    private Boolean renderGroup;
    private Long idAudioServer;
    private String transcriptedText;
    private String score;
    private String phone;
    private String contactName;
    private String fileNamePath;


    public Record(Integer id)
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT id, phone, file_name_path,file_name, duration, creation_date,sync_date, is_email_sent, is_transcripted, error_on_transcripted, is_rec_sync is_rec_sync, id_audio_server, transcripted_text, score, contact_name FROM records_logs WHERE id = '" + id.toString() +"'", null);
        records.moveToFirst();

        this.id = records.getInt(0);
        phone = records.getString(1);
        fileNamePath = records.getString(2);
        fileName = records.getString(3);
        duration = records.getFloat(4);
        creationDate = records.getLong(5);

        syncDate = records.getLong(6);
        isEmailSent = records.getInt(7) == 1;
        isTranscripted = records.getInt(8);
        errorOnTranscripted = records.getInt(9);
        isRecSync = records.getInt(10) == 0;
        idAudioServer = records.getLong(11);
        transcriptedText = records.getString(12);
        score = records.getString(13);
        contactName = records.getString(14);
    }

    public Record()
    {

    }

    public void update()
    {

        SQLiteDatabase db = DBOpenHelper.getDataBase();

        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("duration", duration);
        values.put("file_name_path", fileNamePath);
        values.put("file_name", fileName);
        values.put("is_rec_sync", isRecSync);
        values.put("is_email_sent", isEmailSent);
        values.put("id_audio_server", idAudioServer);
        values.put("transcripted_text", transcriptedText);
        values.put("score", score);
        values.put("contact_name", contactName);
        values.put("creation_date", Calendar.getInstance().getTimeInMillis());
        if (id == 0)
        {

            db.insert("records_logs","", values);
            Cursor records = db.rawQuery("SELECT MAX(ID) MAX_ID FROM records_logs",null);

            records.moveToFirst();
            id = records.getInt(0);
        }
        else
        {
            db.update("records_logs",values,"id = ?", new String[] {id.toString()});
        }

    }


    public void delete()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("records_logs", "id = " + id.toString(), null);
        File file = new File(fileNamePath);
        file.delete();
    }

    public static ArrayList<Record> getAllRecords(Boolean sync, Boolean orderByPhone, Context context) {


        SQLiteDatabase db = DBOpenHelper.getDataBase();
        String sql = "SELECT id, phone, file_name,file_name_path ,duration, creation_date, is_email_sent, contact_name FROM records_logs  ";
        Boolean orderPhone;



        if (orderByPhone)
        {
            sql = sql + " ORDER BY phone ";
        }
        else
        {
            sql = sql + " ORDER BY creation_date ";
        }

        Cursor records;
        if (sync) {
            records = db.rawQuery(sql, null);

        } else {
            records = db.rawQuery(sql, null);

        }


        ArrayList<Record> list = new ArrayList<Record>();
        records.moveToFirst();
        String currentPhone = "";
        for (int i = 0; i < records.getCount(); i++) {
            Record record = new Record();
            record.setId(records.getInt(0));

            if (!currentPhone.equals(records.getString(1)))
            {
                record.renderGroup = true;
            }
            else
            {
                record.renderGroup = false;
            }
            currentPhone =  records.getString(1);
            record.setPhone(currentPhone);

            record.setFileName(records.getString(2));
            record.setFileNamePath(records.getString(3));
            record.setDuration(records.getFloat(4));
            record.setCreationDate(records.getLong(5));
            record.setIsEmailSent(records.getInt(6) == 1);
            record.setContactName(records.getString(7));
            list.add(record);
            records.moveToNext();
        }

        return list;
    }
}

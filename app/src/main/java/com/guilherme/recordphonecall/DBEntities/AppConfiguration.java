package com.guilherme.recordphonecall.DBEntities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guilherme.recordphonecall.DBOpenHelper;

/**
 * Created by dell on 01/02/2019.
 */

public class AppConfiguration {


    private Integer id = 0;
    private String port;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public void setSyncWithServer(Boolean syncWithServer) {
        this.syncWithServer = syncWithServer;
    }

    private String smtpHost;
    private String user;
    private String password;
    private String email;
    private String emailSubject;
    private String emailBody;
    private Boolean syncWithServer = false;
    private String urlServer;


    public String getUrlServer() {
        return urlServer;
    }

    public void setUrlServer(String urlServer) {
        this.urlServer = urlServer;
    }




    public Integer getId() {
        return id;
    }

    public String getPort() {
        return port;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }



    public Boolean getSyncWithServer() {
        return syncWithServer;
    }




public AppConfiguration() {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT id,port, smtp_host, user, password,email_address, email_subject,email_body, sync_with_server, url_server FROM appconfigurations ", null);
        FillData(records);
    }


    public AppConfiguration(Integer id)
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        Cursor records = db.rawQuery("SELECT id,port, smtp_host, user, password,email_address, email_subject,email_body, sync_with_server, url_server  FROM appconfigurations WHERE id = '" + id.toString() +"'", null);
        FillData(records);
    }

    private void FillData(Cursor records)
    {
        if (records.getCount() > 0)
        {
            records.moveToFirst();

            id = records.getInt(0);

            port = records.getString(1);
            smtpHost = records.getString(2);
            user = records.getString(3);
            password = records.getString(4);
            email = records.getString(5);
            emailSubject = records.getString(6);
            emailBody = records.getString(7);
            syncWithServer = records.getInt(8)  == 1;
            urlServer = records.getString(9);
        }

    }


    public static void DeleteConfiguratio()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("appconfigurations","", null);
    }



    public void update()
    {

        SQLiteDatabase db = DBOpenHelper.getDataBase();

        ContentValues values = new ContentValues();

        values.put("user", user);
        values.put("password", password);
        values.put("port", port);
        values.put("smtp_host", smtpHost);
        values.put("email_address", email);
        values.put("email_subject", emailSubject);
        values.put("email_body", emailBody);
        values.put("url_server", urlServer);
        if (syncWithServer)
        {
            values.put("sync_with_server",1);
        }
        else
        {
            values.put("sync_with_server",0);
        }


        if (id == 0)
        {

            db.insert("appconfigurations","", values);
            Cursor records = db.rawQuery("SELECT MAX(ID) MAX_ID FROM appconfigurations",null);

            records.moveToFirst();
            id = records.getInt(0);

        }
        else
        {
            db.update("appconfigurations",values,"id = ?", new String[] {id.toString()});
        }

    }


    public void delete()
    {
        SQLiteDatabase db = DBOpenHelper.getDataBase();
        db.delete("configurations", "id = " + id.toString(), null);

    }
}

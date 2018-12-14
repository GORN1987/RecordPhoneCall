package com.guilherme.recordphonecall;

import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.Record;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class VerifyQueue extends IntentService  {
    public VerifyQueue() {
        super("VerifyQueue");
    }




    /*public boolean onStartJob(JobParameters jobParameters) {
        Log.i("Service","Job Queue");
        Log.d("Service","Job Queue");
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Message Service",Toast.LENGTH_SHORT).show();

            }
        });

        DBOpenHelper dbOpen = new DBOpenHelper(getBaseContext(), "db", null, 1);
        SQLiteDatabase dbSql = dbOpen.getWritableDatabase();
        final Cursor records = dbSql.rawQuery("SELECT ID  FROM records_logs WHERE REC_SYNC = 0", null);

        records.moveToFirst();
        for(int x = 0;x < records.getCount(); x++)
        {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Record rec = new Record(records.getInt(0));
                    RemoteCalls.SendAudio(getBaseContext(),rec, false);
                    records.moveToNext();
                }
            });


        }
        Intent receiver = new Intent("com.guilherme.recordphonecall.VerifyQueueReceiver");
        sendBroadcast(receiver);

        jobFinished(jobParameters, true);
        return true;
    }*/



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try
        {
            final Context _context = getApplicationContext();

            Handler mHandler = new Handler(_context.getMainLooper());

            DBOpenHelper.initDataBase(_context);
            SQLiteDatabase dbSql = DBOpenHelper.getDataBase();
            final Cursor records = dbSql.rawQuery("SELECT ID  FROM records_logs WHERE REC_SYNC = 0", null);

            FileWriter textFile = null;

            try {
                textFile = new FileWriter( "/sdcard/testExecution.txt", true);
                textFile.write(Calendar.getInstance().getTime().toString());
                textFile.flush();
                textFile.close();

            } catch (IOException e) {

                e.printStackTrace();
            }

            records.moveToFirst();
            for(int x = 0;x < records.getCount(); x++)
            {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(_context,"test1",Toast.LENGTH_SHORT).show();
                        Record rec = new Record(records.getInt(0));
                        RemoteCalls.SendAudio(_context,rec, false);
                        records.moveToNext();
                    }
                });


            }
            Intent receiver = new Intent("com.guilherme.recordphonecall.VerifyQueueReceiver");
            receiver.putExtra("informExecution","1");
            _context.sendBroadcast(receiver);
        }
        catch (Exception e)
        {
            try {
                FileWriter textFileExc = new FileWriter( "/sdcard/testException.txt", true);
                textFileExc.write(e.getMessage());
                textFileExc.flush();
                textFileExc.close();

            } catch (IOException ex) {

                e.printStackTrace();
            }
        }

        //Intent receiver = new Intent("com.guilherme.recordphonecall.VerifyQueueReceiver");
        //sendBroadcast(receiver);


    }
}

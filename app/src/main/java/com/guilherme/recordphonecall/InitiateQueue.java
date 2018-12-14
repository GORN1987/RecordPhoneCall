package com.guilherme.recordphonecall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.Record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class InitiateQueue extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        try
        {
            final Context _context = context;

            Handler mHandler = new Handler(context.getMainLooper());

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
                        Toast.makeText(context,"test1",Toast.LENGTH_SHORT).show();
                        Record rec = new Record(records.getInt(0));
                        RemoteCalls.SendAudio(_context,rec, false);
                        records.moveToNext();
                    }
                });


            }

            Intent toastIntent= new Intent(context,InitiateQueue.class);
            PendingIntent toastAlarmIntent = PendingIntent.getBroadcast(context, 0, toastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            long startTime=System.currentTimeMillis(); //alarm starts immediately
            AlarmManager backupAlarmMgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            backupAlarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,new Date().getTime() + 150000, toastAlarmIntent);
            Intent receiver = new Intent("com.guilherme.recordphonecall.VerifyQueueReceiver");
            receiver.putExtra("informExecution","1");
            context.sendBroadcast(receiver);
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


    }
}

package com.guilherme.recordphonecall;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.guilherme.recordphonecall.DBEntities.Record;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class VerifyQueue extends IntentService {

    public VerifyQueue() {
        super("VerifyQueue");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBOpenHelper dbOpen = new DBOpenHelper(getBaseContext(), "db", null, 1);
        SQLiteDatabase dbSql = dbOpen.getWritableDatabase();
        final Cursor records = dbSql.rawQuery("SELECT ID  FROM records_logs WHERE REC_SYNC = 0", null);

        records.moveToFirst();
        for(int x = 0;x < records.getCount(); x++)
        {
            Handler mHandler = new Handler(getMainLooper());
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
    }


}

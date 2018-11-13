package com.guilherme.recordphonecall.DBEntities;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBOpenHelper;
import com.guilherme.recordphonecall.RemoteCalls;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class QueueControll extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.guilherme.recordphonecall.DBEntities.action.FOO";
    private static final String ACTION_BAZ = "com.guilherme.recordphonecall.DBEntities.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.guilherme.recordphonecall.DBEntities.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.guilherme.recordphonecall.DBEntities.extra.PARAM2";

    public QueueControll() {
        super("QueueControll");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionVerifyQueue(Context context) {
        Intent intent = new Intent(context, QueueControll.class);
        //intent.setAction(ACTION_FOO);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, QueueControll.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            while (true)
            {
                Toast.makeText(getBaseContext(),"Message Service",Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /*final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
            //intent.
            SQLiteDatabase db = DBOpenHelper.getDataBase();
            Cursor records = db.rawQuery("SELECT ID FROM records_logs WHERE REC_SYNC = 0", null);
            records.moveToFirst();

            for (int i = 0; i < records.getCount(); i++) {
                Record rec = new Record(records.getInt(0));
                RemoteCalls.SendAudio(getApplicationContext(), rec);
            }*/
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
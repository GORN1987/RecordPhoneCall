package com.guilherme.recordphonecall;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.DateInterval;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.QueueControll;
import com.guilherme.recordphonecall.DBEntities.Record;
import com.guilherme.recordphonecall.DBEntities.SyncToken;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class MainActivity extends AppCompatActivity  {

    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};


    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent toastIntent= new Intent(getApplicationContext(),InitiateQueue.class);
        PendingIntent toastAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, toastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        long startTime=System.currentTimeMillis(); //alarm starts immediately
        AlarmManager backupAlarmMgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        backupAlarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,startTime,5000l,toastAlarmIntent); // alarm will repeat after every 15 minutes

        VerifyQueueReceiver broadcastReceiver = new VerifyQueueReceiver();
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction("com.guilherme.recordphonecall.VerifyQueueReceiver");
        registerReceiver(broadcastReceiver,intentFilter);

        DBOpenHelper.initDataBase(getApplicationContext());

        SyncToken recToken = SyncToken.getLastSyncToken();
        if ((recToken != null) && (RemoteCalls.ValidUser))
        {
            RemoteCalls.Authenticate(this,recToken.USER,recToken.PASSWORD,true);
        }
        else
        {
            Intent serviceIntent = new Intent(this, QueueControll.class);
            startService(serviceIntent);

            ActivityCompat.requestPermissions(this,
                    permissions,
                    123);


            setContentView(R.layout.activity_main);



            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
            setSupportActionBar(toolbar);

            final Button btnLogin = (Button)findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    //progressDialog.dismiss();

                    TextView txtLogin = (TextView) findViewById(R.id.txtLogin);
                    TextView txtPassword = (TextView) findViewById(R.id.txtPassword);
                    RemoteCalls.Authenticate(MainActivity.this,txtLogin.getText().toString(), txtPassword.getText().toString(), true);


                }
            });
        }



    }


}

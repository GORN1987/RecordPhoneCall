package com.guilherme.recordphonecall;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guilherme.recordphonecall.DBEntities.AppConfiguration;
import com.guilherme.recordphonecall.DBEntities.SyncToken;
import com.loopj.android.http.*;


public class MainActivity extends AppCompatActivity {

    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT,
            Manifest.permission.INTERNET};


    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent toastIntent = new Intent(getApplicationContext(), InitiateQueue.class);
        PendingIntent toastAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager backupAlarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        backupAlarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, toastAlarmIntent);

        VerifyQueueReceiver broadcastReceiver = new VerifyQueueReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.guilherme.recordphonecall.VerifyQueueReceiver");
        registerReceiver(broadcastReceiver, intentFilter);

        DBOpenHelper.initDataBase(getApplicationContext());
        ActivityCompat.requestPermissions(this,
                permissions,
                123);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        TextView txtLink =  (TextView)findViewById(R.id.txtLink);
        txtLink.setPaintFlags(txtLink.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri url = Uri.parse("https://advisor-speach.herokuapp.com/users/new");
                Intent urlAddNewUser = new Intent(Intent.ACTION_VIEW, url);
                startActivity(urlAddNewUser);
            }

        });

        SyncToken recToken;
        AppConfiguration appConf =  new AppConfiguration();
        if (appConf.getSyncWithServer())
        {
            recToken = SyncToken.getLastSyncToken();

            if ((recToken != null) && (RemoteCalls.ValidUser)) {
                RemoteCalls.authenticate(this, recToken.getUser(), recToken.getPassword(), true);
            }
            final Button btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    TextView txtLogin = (TextView) findViewById(R.id.txtLogin);
                    TextView txtPassword = (TextView) findViewById(R.id.txtPassword);
                    RemoteCalls.authenticate(MainActivity.this, txtLogin.getText().toString(), txtPassword.getText().toString(), true);
                }


            });
        }
        else
        {
            Intent summary = new Intent(this, SummaryActivity.class);
            summary.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity( summary, null);

        }


    }


}

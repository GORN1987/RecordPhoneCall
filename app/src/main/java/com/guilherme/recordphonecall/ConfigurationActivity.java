package com.guilherme.recordphonecall;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.AppConfiguration;

public class ConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        final TextView txtSmtp = (TextView) findViewById(R.id.txtSmtp);
        final TextView txtUser = (TextView) findViewById(R.id.txtUserEmail);
        final TextView txtPassword = (TextView) findViewById(R.id.txtEmailPassword);
        final TextView txtPort = (TextView) findViewById(R.id.txtPort);
        final TextView txtDestinyEmail = (TextView) findViewById(R.id.txtDestinyEmail);
        final TextView txtUrlServer = (TextView) findViewById(R.id.txtUrlServer);


        final CheckBox chkSyncToRemoteServer = (CheckBox) findViewById(R.id.chkSyncToRemoteServer);

        final TextView txtSubject = (TextView) findViewById(R.id.txtSubject);
        final TextView txtBodyText = (TextView) findViewById(R.id.txtBody);

        final AppConfiguration appConf = new AppConfiguration();

        txtSmtp.setText(appConf.getSmtpHost());
        txtUser.setText(appConf.getUser());
        txtPassword.setText(appConf.getPassword());
        txtPort.setText(appConf.getPort());
        txtDestinyEmail.setText(appConf.getEmail());
        txtSubject.setText(appConf.getEmailSubject());
        txtBodyText.setText(appConf.getEmailBody());
        if (!appConf.getUrlServer().isEmpty())
        {
            txtUrlServer.setText(appConf.getUrlServer());
        }

        chkSyncToRemoteServer.setChecked(appConf.getSyncWithServer());

        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                appConf.setSmtpHost(txtSmtp.getText().toString());
                appConf.setUser(txtUser.getText().toString());
                appConf.setPassword(txtPassword.getText().toString());
                appConf.setPort(txtPort.getText().toString());
                appConf.setEmail(txtDestinyEmail.getText().toString());
                appConf.setEmailSubject(txtSubject.getText().toString());
                appConf.setEmailBody(txtBodyText.getText().toString());
                appConf.setUrlServer(txtUrlServer.getText().toString());
                appConf.setSyncWithServer(chkSyncToRemoteServer.isChecked());
                appConf.update();

                Toast.makeText(getApplicationContext(),R.string.record_saved_succefully,Toast.LENGTH_LONG).show();
                finish();
            }


        });

    }

}

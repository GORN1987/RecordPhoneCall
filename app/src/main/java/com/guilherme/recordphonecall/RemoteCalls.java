package com.guilherme.recordphonecall;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.Record;
import com.guilherme.recordphonecall.DBEntities.SyncToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import android.app.*;


import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.content.ContextCompat.startActivity;
//import static android.support.v4.media.MediaMetadataCompatApi21.getText;


/**
 * Created by dell on 25/08/2018.
 */

public class RemoteCalls {




    public static String Token;
    public static Boolean ValidUser = true;

    //private static Context ctx;
    public static void Authenticate(final Context ctx, final String login, final String password, final Boolean showActivity) {
        StringEntity entity;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("email", login);
            jsonParams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject jsonParams2 = new JSONObject();
        try {
            jsonParams2.put("auth", jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            final ProgressDialog progressDialog;

            //Create a new progress dialog
            progressDialog = new ProgressDialog(ctx);
            //Set the progress dialog to display a horizontal progress bar
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //Set the dialog title to 'Loading...'
            progressDialog.setTitle("Loading...");
            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage("Loading application credentials, please wait...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
            //This dialog isn't indeterminate
            progressDialog.setIndeterminate(true);
            //The maximum number of items is 100
            //progressDialog.setMax(100);
            //Set the current progress to zero
            //progressDialog.setProgress(0);
            //Display the progress dialog
            progressDialog.show();



            entity = new StringEntity(jsonParams2.toString());

            //final Intent waitAct = new Intent(ctx, WaitActivity.class);
            //waitAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(ctx, waitAct, null);

            AsyncHttpClient client = new AsyncHttpClient();

            client.setTimeout(90000);
            client.post(ctx, "https://advisor-speach.herokuapp.com/user_token", entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    if (showActivity){
                        Toast.makeText(ctx, "Invalid User", Toast.LENGTH_LONG).show();
                    }

                    RemoteCalls.ValidUser = false;

                    Intent summary = new Intent(ctx, MainActivity.class);
                    summary.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(ctx, summary, null);
                    progressDialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    try {
                        JSONObject json = new JSONObject(responseString);
                        RemoteCalls.Token = json.getString("jwt");
                        RemoteCalls.ValidUser = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SyncToken recToken = new SyncToken();
                    recToken.PASSWORD = password;
                    recToken.USER = login;
                    recToken.TOKEN = RemoteCalls.Token;
                    recToken.Update();

                    if (showActivity) {
                        Intent summary = new Intent(ctx, SummaryActivity.class);
                        summary.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(ctx, summary, null);
                    }


                    try {
                        this.finalize();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public static void SendAudio(final Context ctx, final Record rec,final boolean refresh_list)  {

        AsyncHttpClient client = new AsyncHttpClient();

        if ((RemoteCalls.Token == "") || (RemoteCalls.Token == null)) {
            SyncToken recToken = SyncToken.getLastSyncToken();
            if (recToken != null) {
                RemoteCalls.Authenticate(ctx, recToken.USER, recToken.PASSWORD, false);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {

            }

        }

        client.addHeader("Authorization", "Bearer " + RemoteCalls.Token);

        File fileAudio = new File(rec.FILE_NAME);
        RequestParams params = new RequestParams();
        try {
            params.put("audio", fileAudio, "audio/mpeg3");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.setTimeout(90000);
        client.post("https://advisor-speach.herokuapp.com/api/ReceiveAudio", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                if (refresh_list)
                {
                    BroadcastObserver obs = BroadcastObserver.getIntance();
                    obs.change();
                }
                RemoteCalls.showNotification(ctx, (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE), "Error on sync audio", "Audio was not sync");

                if (statusCode == 401) {
                    SyncToken recToken = SyncToken.getLastSyncToken();
                    if (recToken != null) {
                        RemoteCalls.Authenticate(ctx, recToken.USER, recToken.PASSWORD, false);
                    }

                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {


                RemoteCalls.showNotification(ctx, (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE), "Audio synchronized", "Audio sychronized sucefully");
                rec.REC_SYNC = 1;
                rec.Update();

                if (refresh_list)
                {
                    BroadcastObserver obs = BroadcastObserver.getIntance();
                    obs.change();
                }

            }
        });
    }


    private static void showNotification(Context ctx, NotificationManager mNM, String titleMessage, String message) {

        Notification notification;

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, SummaryActivity.class), 0);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("123", "NotificationGuilherme", importance);
            // Set the info for the views that show in the notification panel.
            notification = new Notification.Builder(ctx)
                    .setSmallIcon(R.mipmap.ic_main_icon)  // the status icon
                    .setTicker(titleMessage)  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(titleMessage)  // the label of the entry
                    .setContentText(message)  // the contents of the entry
                    .setChannelId("123")
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .build();

            mNM.createNotificationChannel(channel);

        } else {
            // Set the info for the views that show in the notification panel.
            notification = new Notification.Builder(ctx)
                    .setSmallIcon(R.mipmap.ic_main_icon)   // the status icon
                    .setTicker(titleMessage)  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(titleMessage)  // the label of the entry
                    .setContentText(message)  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .build();
        }


        // Send the notification.
        mNM.notify(123, notification);
    }


}

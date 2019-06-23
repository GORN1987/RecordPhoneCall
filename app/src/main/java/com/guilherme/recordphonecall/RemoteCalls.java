package com.guilherme.recordphonecall;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.Record;
import com.guilherme.recordphonecall.DBEntities.AppConfiguration;
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


/**
 * Created by dell on 25/08/2018.
 */

public class RemoteCalls {

    public static String Token;
    public static Boolean ValidUser = true;
    public static Boolean IsProcessingAuthentication = false;

    public static void authenticate(final Context ctx, final String login, final String password, final Boolean showActivity) {
        authenticate(ctx, login, password, showActivity, null);
    }


    public static void authenticate(final Context ctx, final String login, final String password, final Boolean showActivity, final Runnable func) {
        StringEntity entity;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("email", login);
            jsonParams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Credentials: ", " Login: " + login + " " + " Password: " + password);

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
            if (showActivity) {

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
            }


            entity = new StringEntity(jsonParams2.toString());


            AsyncHttpClient client = new AsyncHttpClient();

            client.setTimeout(90000);
            RemoteCalls.IsProcessingAuthentication = true;
            client.post(ctx, "https://advisor-speach.herokuapp.com/user_token", entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    if (showActivity) {
                        Toast.makeText(ctx, ctx.getText(R.string.invalid_user), Toast.LENGTH_LONG).show();
                    }
                    Log.i("Error Authentication", String.valueOf(statusCode) + " " + responseString);
                    RemoteCalls.ValidUser = false;

                    if (showActivity) {
                        progressDialog.dismiss();
                    }
                    RemoteCalls.IsProcessingAuthentication = false;
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.i("Authentication", String.valueOf(statusCode) + " " + responseString);
                    try {
                        JSONObject json = new JSONObject(responseString);
                        RemoteCalls.Token = json.getString("jwt");
                        RemoteCalls.ValidUser = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SyncToken recToken = new SyncToken();
                    recToken.setPassword(password);
                    recToken.setUser(login);
                    recToken.setToken(RemoteCalls.Token);
                    recToken.update();

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

                    if (showActivity) {
                        progressDialog.dismiss();
                    }
                    RemoteCalls.IsProcessingAuthentication = false;
                    if (func != null) {
                        func.run();
                    }

                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public static void tryToAuthenticateAndSendAudio(final Context ctx, final Record rec, final boolean refresh_list) {

        if ((RemoteCalls.Token == "") || (RemoteCalls.Token == null)) {

            SyncToken recToken = SyncToken.getLastSyncToken();

            // It verifies if the user is logged, if is not, exit the function
            if (recToken != null) {
                RemoteCalls.authenticate(ctx, recToken.getUser(), recToken.getPassword(), false, new Runnable() {
                    @Override
                    public void run() {
                        sendAudio(ctx, rec, refresh_list);
                    }
                });
            }

        } else {
            sendAudio(ctx, rec, refresh_list);
        }
    }

    public static void sendEmail(final Context ctx, final Record rec, final boolean refresh_list) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    AppConfiguration appconf = new AppConfiguration();

                    Mail m = new Mail(appconf.getUser(), appconf.getPassword(), appconf.getSmtpHost(), appconf.getPort());
                    m.attachFile(rec.getFileNamePath(), rec.getFileName());

                    String[] toArr = {appconf.getEmail()};
                    m.setTo(toArr);
                    m.setFrom(appconf.getEmail());
                    m.setSubject(appconf.getEmailSubject());
                    m.setBody(appconf.getEmailBody());


                    if (m.send()) {
                        RemoteCalls.showNotification(ctx, (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE), "Success", "Email sent succefully");
                        rec.setIsEmailSent(true);
                        rec.update();

                        if (refresh_list) {
                            BroadcastObserver obs = BroadcastObserver.getIntance();
                            obs.change();
                        }

                    } else {
                        RemoteCalls.showNotification(ctx, (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE), "Error", "Error on sent email");
                    }
                } catch (Exception e) {
                    Log.e("MailApp", "Could not send email", e);
                }
                ;

            }
        }).start();

    }
    public static void sendAudio(final Context ctx, final Record rec, final boolean refresh_list) {

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader("Authorization", "Bearer " + RemoteCalls.Token);

        File fileAudio = new File(rec.getFileNamePath());
        RequestParams params = new RequestParams();
        try {
            params.put("audio", fileAudio, "audio/mpeg3");
            params.put("duration", rec.getDuration());
            params.put("contact_name", rec.getContactName());
            params.put("phone", rec.getPHONE());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.setTimeout(90000);
        client.post("https://advisor-speach.herokuapp.com/api/receive_audio", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                if (refresh_list) {
                    BroadcastObserver obs = BroadcastObserver.getIntance();
                    obs.change();
                }

                RemoteCalls.showNotification(ctx, (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE), "Error on sync audio", String.valueOf(statusCode) );

                if (statusCode == 401) {
                    SyncToken recToken = SyncToken.getLastSyncToken();
                    if (recToken != null) {
                        RemoteCalls.authenticate(ctx, recToken.getUser(), recToken.getPassword(), false);
                    }

                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                RemoteCalls.showNotification(ctx, (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE), "Audio synchronized", "Audio sychronized sucefully");

                try {
                    JSONObject jObject = new JSONObject(responseString);
                    //String aJsonString = jObject.getString("id");
                    rec.setIdAudioServer(jObject.getLong("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rec.setRecSync(true);
                rec.update();

                if (refresh_list) {
                    BroadcastObserver obs = BroadcastObserver.getIntance();
                    obs.change();
                }

            }
        });
    }

    public static Record getTranscriptAudio(final Record currentAudio, Long idAudio,final Activity act) {

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader("Authorization", "Bearer " + RemoteCalls.Token);

        RequestParams params = new RequestParams();


        client.setTimeout(90000);
        client.get("https://advisor-speach.herokuapp.com/api/get_transcripted/" + idAudio.toString(), params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    JSONObject jObject = new JSONObject(responseString);
                    currentAudio.setScore(jObject.getString("sentiment"));
                    currentAudio.setTranscriptedText(jObject.getString("transcript_audio"));
                    currentAudio.update();
                    EditText txtTranscriptAudio = act.findViewById(R.id.txtTranscriptAudio);
                    txtTranscriptAudio.setText(currentAudio.getTranscriptedText());

                    EditText txtScore = act.findViewById(R.id.txtScore);
                    txtScore.setText(currentAudio.getScore());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return currentAudio;
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

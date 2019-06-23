package com.guilherme.recordphonecall;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.AppConfiguration;
import com.guilherme.recordphonecall.DBEntities.Notes;
import com.guilherme.recordphonecall.DBEntities.Record;
import com.loopj.android.http.AsyncHttpClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dell on 05/06/2018.
 */

public class CallReceiver extends PhonecallReceiver {
    private static MediaRecorder mRecorder;
    private static String path;
    private static String file_name;
    private static AsyncHttpClient client = new AsyncHttpClient();


    private void endRecord(final Context ctx, String number, Date start, Date end)
    {
        if (number != null)
        {
            mRecorder.stop();

            mRecorder.release();
            mRecorder = null;
            DBOpenHelper.initDataBase(ctx);

            final Record rec = new Record();
            rec.setPhone(number);
            rec.setFileNamePath(path);
            rec.setFileName(file_name);
            rec.setRecSync(false);

            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Cursor phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
                while (phones.moveToNext())
                {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (phoneNumber.equals(rec.getPHONE().trim()))
                    {
                        String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        rec.setContactName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    }

                }
                phones.close();
            }

            rec.setDuration (  Float.valueOf (end.getTime() - start.getTime()) / 1000);
            rec.update();

            AppConfiguration appConf =  new AppConfiguration();
            if (appConf.getSyncWithServer())
            {
                RemoteCalls.tryToAuthenticateAndSendAudio(ctx, rec, true);

            }
            RemoteCalls.sendEmail(ctx, rec, true);

            BroadcastObserver obs = BroadcastObserver.getIntance();
            obs.change();
        }

    }

    private  void initiateRecord()
    {
        mRecorder = new MediaRecorder();

        Date curDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss");
        file_name = df.format(curDate) + "record.mp3";
        path = Environment.getExternalStorageDirectory().toString() + "/" + file_name;

        File gpxFile = new File(path);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioEncodingBitRate(8000);
        mRecorder.setAudioSamplingRate(8000);

        mRecorder.setOutputFile(path);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Notes note = new Notes(number);
        Toast.makeText(ctx, note.getTextNotification(), Toast.LENGTH_LONG).show();
        initiateRecord();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

        if (number != null)
        {
            Notes note = new Notes(number);
            if (!note.getTextNotification().equals(""))
            {
                Toast.makeText(ctx, note.getTextNotification(), Toast.LENGTH_LONG).show();
            }
            initiateRecord();
        }
        else
        {
            Toast.makeText(ctx, R.string.phone_could_not_be_gotten, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onIncomingCallEnded(final Context ctx, String number, Date start, Date end) {
        endRecord(ctx, number, start, end);
    }

    @Override
    protected void onOutgoingCallEnded(final Context ctx, String number, Date start, Date end) {

        endRecord(ctx, number, start, end);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }

}
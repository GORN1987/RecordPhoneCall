package com.guilherme.recordphonecall;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.Record;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dell on 05/06/2018.
 */

public class CallReceiver extends PhonecallReceiver {
    private static MediaRecorder mRecorder;
    private static String path;
    private static AsyncHttpClient client = new AsyncHttpClient();


    private void endRecord(final Context ctx, String number, Date start, Date end)
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        DBOpenHelper.initDataBase(ctx);

        final Record rec = new Record();
        rec.PHONE = number;
        rec.FILE_NAME = path;
        rec.REC_SYNC = 0;
        rec.DURATION = (  Float.valueOf (end.getTime() - start.getTime()) / 1000);
        rec.Update();
        RemoteCalls.SendAudio(ctx, rec, true);
    }

    private  void initiateRecord()
    {
        mRecorder = new MediaRecorder();

        Date curDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss");

        path = Environment.getExternalStorageDirectory().toString() + "/" + df.format(curDate) + "record.mp3";

        File gpxFile = new File(path);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
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
        initiateRecord();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        initiateRecord();
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
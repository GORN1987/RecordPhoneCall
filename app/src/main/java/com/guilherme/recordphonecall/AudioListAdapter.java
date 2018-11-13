package com.guilherme.recordphonecall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guilherme.recordphonecall.DBEntities.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dell on 17/08/2018.
 */

public class AudioListAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<Record> audioList;
    public AudioListAdapter(Context ctx, ArrayList<Record> audioList)
    {
        this.ctx = ctx;
        this.audioList = audioList;
    }

    @Override
    public int getCount() {
        return audioList.size();
    }

    @Override
    public Object getItem(int i) {

        return audioList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inf = LayoutInflater.from(ctx);
        view =inf.inflate(R.layout.audio_record_row, null);

        TextView lblPhone = view.findViewById(R.id.lblPhone);
        lblPhone.setText(audioList.get(i).PHONE);

        TextView lblDuration = view.findViewById(R.id.lblDuration);
        lblDuration.setText(audioList.get(i).DURATION.toString());

        //Calendar calendar = Calendar.getInstance();

        TextView lblDate = view.findViewById(R.id.lblDate);
        //lblDate.setText();
        //calendar.setTimeInMillis(  audioList.get(i).CREATION_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(audioList.get(i).CREATION_DATE);

        SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dtFormat.setCalendar(calendar);
        //dtFormat.format(calendar.)
        lblDate.setText(dtFormat.format(calendar.getTime()));
        //LayoutInflater inf = LayoutInflater.from();
        return view;
    }
}

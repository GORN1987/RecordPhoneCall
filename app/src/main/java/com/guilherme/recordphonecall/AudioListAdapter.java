package com.guilherme.recordphonecall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    public AudioListAdapter(Context ctx, ArrayList<Record> audioList) {
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
        view = inf.inflate(R.layout.audio_record_row, null);

        TextView lblPhone = view.findViewById(R.id.lblPhone);

        if (audioList.get(i).getContactName() != null)
        {
            lblPhone.setText(audioList.get(i).getContactName());
        }
        else
        {
            lblPhone.setText(audioList.get(i).getPhone());
        }


        TextView lblDuration = view.findViewById(R.id.lblDuration);
        lblDuration.setText(audioList.get(i).getDuration().toString());

        TextView lblDate = view.findViewById(R.id.lblDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(audioList.get(i).getCreationDate());

        SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dtFormat.setCalendar(calendar);

        lblDate.setText(dtFormat.format(calendar.getTime()));

        ImageView imgStatusEmail = (ImageView) view.findViewById(R.id.imgStatusEmail);
        if (audioList.get(i).getIsEmailSent()) {
            imgStatusEmail.setBackgroundResource(R.drawable.ic_mail_outline_black_56dp);
        } else {
            imgStatusEmail.setBackgroundResource(R.drawable.ic_sync_problem_black_56dp);
        }

        TextView lblAsync = view.findViewById(R.id.lblAsycAudioView);
        if (audioList.get(i).getRecSync())
        {
            lblAsync.setText(ctx.getString(R.string.sync));
        }
        else
        {
            lblAsync.setText(ctx.getString(R.string.wait_sync));
        }


        return view;
    }
}

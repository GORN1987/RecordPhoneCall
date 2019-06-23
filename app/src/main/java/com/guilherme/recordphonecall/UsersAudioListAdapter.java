package com.guilherme.recordphonecall;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.guilherme.recordphonecall.DBEntities.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dell on 17/08/2018.
 */

public class UsersAudioListAdapter extends BaseAdapter {

    public Context ctx;
    private ArrayList<Record> audioList;
    private int countAudio = 0;
    private Boolean playAudio = false;

    public UsersAudioListAdapter(Context ctx, ArrayList<Record> audioList) {
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
        final Context _ctx = this.ctx;
        final UsersAudioListAdapter _self = this;
        final String currentUserPhone;
        final Integer positionList = i;

        if (audioList.get(i).getContactName() != null) {
            currentUserPhone = audioList.get(i).getContactName();
        } else {
            currentUserPhone = audioList.get(i).getPHONE();
        }

        view = inf.inflate(R.layout.audio_record_user_row, null);
        LinearLayout layout_group = view.findViewById(R.id.layout_group);


        if (audioList.get(i).getRenderGroup()) {
            layout_group.getLayoutParams().height = 120;
            layout_group.getParent().requestLayout();

            TextView lblPhone = view.findViewById(R.id.lblPhone);
            lblPhone.setText(currentUserPhone);
            ImageView menu = view.findViewById(R.id.btnListOption);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(_ctx, view);
                    popupMenu.inflate(R.menu.popup_menu_user_group);
                    final SummaryActivity sumActivity = ((SummaryActivity) _ctx);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.menuPlayAllAudio) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        _self.countAudio = 0;
                                        while (_self.countAudio < audioList.size()) {

                                            if (audioList.get(positionList).getPhone().equals(audioList.get(_self.countAudio).getPHONE())) {

                                                if (!_self.playAudio) {
                                                    _self.playAudio = true;

                                                    MediaPlayer media = sumActivity.playAudio(audioList.get(_self.countAudio));

                                                    try
                                                    {
                                                        Thread.sleep(100);
                                                    } catch (InterruptedException e)

                                                    {
                                                        e.printStackTrace();
                                                    }
                                                    while (media.isPlaying() || sumActivity.AudioPaused)
                                                    {
                                                        //if (media.getCurrentPosition() != media.getDuration())
                                                        try
                                                        {
                                                            Thread.sleep(100);
                                                        } catch (InterruptedException e)

                                                        {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                    _self.countAudio++;
                                                    _self.playAudio = false;

                                                } else {
                                                    _self.countAudio++;
                                                }
                                                try
                                                {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e)

                                                {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }

                                    }
                                }).start();

                            }
                            else if (menuItem.getItemId() == R.id.menuAddNote)
                            {
                                Intent notesActivity = new Intent( ((SummaryActivity) _ctx).getBaseContext(),NotesActivity.class );
                                notesActivity.putExtra("phone", audioList.get(positionList).getPHONE());
                                _ctx.startActivity(notesActivity);


                            }
                            return false;
                        }
                    });

                    popupMenu.show();
                }
            });


        } else {
            layout_group.getLayoutParams().height = 0;
            layout_group.getParent().requestLayout();
        }

        TextView lblDuration = view.findViewById(R.id.lblDuration);
        lblDuration.setText(audioList.get(i).getDuration().toString());


        TextView lblDate = view.findViewById(R.id.lblDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(audioList.get(i).getCreationDate());

        SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dtFormat.setCalendar(calendar);

        lblDate.setText(dtFormat.format(calendar.getTime()));
        TextView lblAsync = view.findViewById(R.id.lblAsycUserView);
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

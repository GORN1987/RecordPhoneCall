package com.guilherme.recordphonecall;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.Record;
import com.guilherme.recordphonecall.DBEntities.SyncToken;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.support.v4.content.ContextCompat.startActivity;

public class SummaryActivity extends AppCompatActivity implements Observer, MediaPlayer.OnPreparedListener {


    public final Context _self = this;
    private boolean _sync = false;
    final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }



    public void PopulateList(Boolean sync) {
        DBOpenHelper dbOpen = new DBOpenHelper(getBaseContext(), "db", null, 1);
        SQLiteDatabase dbSql = dbOpen.getWritableDatabase();
        Cursor records;

        if (sync) {
            records = dbSql.rawQuery("SELECT ID, PHONE, FILE_NAME, DURATION, CREATION_DATE FROM records_logs WHERE REC_SYNC = 1", null);

        } else {
            records = dbSql.rawQuery("SELECT ID, PHONE, FILE_NAME, DURATION, CREATION_DATE FROM records_logs WHERE REC_SYNC = 0", null);

        }


        ArrayList<Record> list = new ArrayList<Record>();
        ListView listAudioRecord = (ListView) findViewById(R.id.lstAudioRecord);

        records.moveToFirst();

        for (int i = 0; i < records.getCount(); i++) {
            Record record = new Record();
            record.ID = records.getInt(0);
            record.PHONE = records.getString(1);
            record.FILE_NAME = records.getString(2);
            record.DURATION = records.getFloat(3);
            record.CREATION_DATE = records.getLong(4);
            list.add(record);
            records.moveToNext();
        }
        final ArrayList<Record> list_final = list;

        listAudioRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showMenu(view, list_final.get(i));
            }
        });

        AudioListAdapter adapter = new AudioListAdapter(getBaseContext(), list);
        listAudioRecord.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BroadcastObserver.getIntance().addObserver(this);

        PopulateList(_sync);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.summary_menu, menu);
        menu.findItem(R.id.menuNoSync).setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSync) {
            PopulateList(true);
            item.setChecked(true);


        } else if (item.getItemId() == R.id.menuNoSync) {
            PopulateList(false);
            item.setChecked(true);

        } else if (item.getItemId() == R.id.menuLogOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log out");
            builder.setMessage("Do you want log out?");
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SyncToken.DeleteTokensAndLogOut();
                    RemoteCalls.Token = "";
                    Intent main = new Intent(getBaseContext(), MainActivity.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main);
                    finish();
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void ResumeMediaProgress()
    {
        new Thread(new Runnable() {

            @Override
            public void run() {

                Handler handler = new Handler(Looper.getMainLooper());

                while (!mediaPlayer.isPlaying()) {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                final SeekBar seekBarAudio = findViewById(R.id.seekBarAudio);
                seekBarAudio.setMax(mediaPlayer.getDuration());

                while (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()) {
                    if (!mediaPlayer.isPlaying())
                    {
                        return;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            seekBarAudio.setProgress(mediaPlayer.getCurrentPosition());

                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void showMenu(View v, final Record rec) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menuPlayAudio) {
                    final SeekBar seekBarAudio = findViewById(R.id.seekBarAudio);
                    final LinearLayout audioPainel = (LinearLayout) findViewById(R.id.painel_navigation);
                    final Button btnAudioController = (Button) findViewById(R.id.btnControlAudio);
                    btnAudioController.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));


                    while (mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        btnAudioController.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));
                    }
                    mediaPlayer.reset();
                    seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    });

                    audioPainel.getLayoutParams().height = 100;
                    audioPainel.getParent().requestLayout();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            audioPainel.getLayoutParams().height = 0;
                            audioPainel.getParent().requestLayout();
                        }
                    });



                    View.OnClickListener pauseAction;

                    pauseAction = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            btnAudioController.setBackground(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                            mediaPlayer.pause();
                            final View.OnClickListener _self = this;
                            View.OnClickListener resumeAction = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    btnAudioController.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));
                                    mediaPlayer.start();
                                    btnAudioController.setOnClickListener(_self);
                                    ResumeMediaProgress();
                                }
                            };

                            btnAudioController.setOnClickListener(resumeAction);

                        }
                    };

                    btnAudioController.setOnClickListener(pauseAction);

                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                mediaPlayer.setDataSource(rec.FILE_NAME);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                            }
                            mediaPlayer.start();
                            seekBarAudio.setMax(mediaPlayer.getDuration());

                        }
                    }).start();

                    ResumeMediaProgress();

                } else if (menuItem.getItemId() == R.id.menuDeleteAudio) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(_self);
                    builder.setTitle(getResources().getString(R.string.confirmation));
                    builder.setMessage(getResources().getString(R.string.question_delete_record));
                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rec.Delete();
                            PopulateList(_sync);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                }
                return false;
            }
        });
        popup.inflate(R.menu.popup_menu_content);
        popup.show();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu_content, menu);
        menu.setHeaderTitle("Select The Action");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDeleteAudio) {
            Toast.makeText(getApplicationContext(), "Delete Audio", Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.menuPlayAudio) {
            Toast.makeText(getApplicationContext(), "Play Audio", Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
    }


    @Override
    public void update(Observable observable, Object o) {
        PopulateList(_sync);
    }
}

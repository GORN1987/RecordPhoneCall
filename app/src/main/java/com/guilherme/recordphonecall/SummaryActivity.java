package com.guilherme.recordphonecall;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.SeekBar;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.AppConfiguration;
import com.guilherme.recordphonecall.DBEntities.Record;
import com.guilherme.recordphonecall.DBEntities.SyncToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class SummaryActivity extends AppCompatActivity implements Observer {


    public final Context selfActivityContext = this;
    final MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean sync = false;
    public boolean AudioPaused = false;


    public ArrayList<Record> populateList(Boolean viewByUser) {

        ListView listAudioRecord = (ListView) findViewById(R.id.lstAudioRecord);
        final ArrayList<Record> list;
        if (viewByUser)
        {
            list = Record.getAllRecords(true, true, getApplicationContext());


            UsersAudioListAdapter adapter = new UsersAudioListAdapter(this, list);
            listAudioRecord.setAdapter(adapter);
        }
        else
        {
            list = Record.getAllRecords(true, false, getApplicationContext());
            AudioListAdapter adapter = new AudioListAdapter(this, list);
            listAudioRecord.setAdapter(adapter);

        }


        listAudioRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showMenu(view, list.get(i));
            }
        });

        return list;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                populateList(tab.getText().toString() == getString(R.string.calls_by_users));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        BroadcastObserver.getIntance().addObserver(this);

        if (savedInstanceState != null) {
            TabLayout.Tab tab = tabLayout.getTabAt(savedInstanceState.getInt("selectedTab"));
            tab.select();

            populateList(tab.getText().toString() == getString(R.string.calls_by_users));
        }
        else
        {
            populateList(false);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        outState.putInt("selectedTab", tabLayout.getSelectedTabPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        AppConfiguration appConf =  new AppConfiguration();


        getMenuInflater().inflate(R.menu.summary_menu, menu);
        if (!appConf.getSyncWithServer())
        {
            menu.findItem(R.id.menuLogOut).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuConf) {
            Intent confActivity = new Intent(this.getBaseContext(), ConfigurationActivity.class);
            startActivity(confActivity);

        } else if (item.getItemId() == R.id.menuLogOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.logout);
            builder.setMessage(R.string.want_logout);
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SyncToken.deleteTokensAndLogOut();
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

    private void resumeMediaProgress() {
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

                while ((mediaPlayer.isPlaying()) && (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration())) {

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

    public MediaPlayer playAudio(final Record rec)
    {
        final SeekBar seekBarAudio = findViewById(R.id.seekBarAudio);
        final LinearLayout audioPainel = (LinearLayout) findViewById(R.id.painel_navigation);
        final Button btnAudioController = (Button) findViewById(R.id.btnControlAudio);
        btnAudioController.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));


        while (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.setOnPreparedListener(null);
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


        View.OnClickListener pauseAction;

        pauseAction = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAudioController.setBackground(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                mediaPlayer.pause();
                ((SummaryActivity) selfActivityContext).AudioPaused = true;
                final View.OnClickListener _self = this;
                View.OnClickListener resumeAction = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnAudioController.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));
                        ((SummaryActivity) selfActivityContext).AudioPaused = false;
                        mediaPlayer.start();
                        btnAudioController.setOnClickListener(_self);
                        resumeMediaProgress();
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
                    mediaPlayer.setDataSource(rec.getFileNamePath());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            audioPainel.getLayoutParams().height = 100;
                            audioPainel.getParent().requestLayout();
                            mediaPlayer.start();

                            SummaryActivity.this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    audioPainel.getLayoutParams().height = 0;
                                    audioPainel.getParent().requestLayout();
                                }
                            });
                        }
                    });

                    seekBarAudio.setMax(mediaPlayer.getDuration());


                } catch (IOException e) {
                    Handler handler = new Handler(getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), R.string.file_not_load, Toast.LENGTH_LONG).show();

                        }
                    });


                }

            }
        }).start();

        resumeMediaProgress();
        return mediaPlayer;
    }

    public void showMenu(View v, final Record rec) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menuPlayAudio) {

                    playAudio(rec);

                } else if (menuItem.getItemId() == R.id.menuDeleteAudio) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(selfActivityContext);
                    builder.setTitle(getResources().getString(R.string.confirmation));
                    builder.setMessage(getResources().getString(R.string.question_delete_record));
                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rec.delete();
                            populateList(sync);
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
                else if (menuItem.getItemId() == R.id.menuTranscriptAudio)
                {
                    Intent analysedText = new Intent(getBaseContext(), AnalysedTextActivity.class);
                    analysedText.putExtra("ID_AUDIO",rec.id);
                    analysedText.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(analysedText);
                }
                return false;
            }
        });
        popup.inflate(R.menu.popup_menu_content);

        AppConfiguration appConf =  new AppConfiguration();
        if (!appConf.getSyncWithServer())
        {
            popup.getMenu().findItem(R.id.menuTranscriptAudio).setVisible(false);

        }

        popup.show();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu_content, menu);
        menu.setHeaderTitle(getString(R.string.select_action));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDeleteAudio) {
            Toast.makeText(getApplicationContext(), getString(R.string.delete_audio), Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.menuPlayAudio) {
            Toast.makeText(getApplicationContext(), getString(R.string.play_audio), Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
    }


    @Override
    public void update(Observable observable, Object o) {

        recreate();
    }
}

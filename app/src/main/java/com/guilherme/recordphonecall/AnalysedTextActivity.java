package com.guilherme.recordphonecall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.guilherme.recordphonecall.DBEntities.Record;

public class AnalysedTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysed_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Integer idAudio = getIntent().getIntExtra("ID_AUDIO", 0);
        Record rec = new Record(idAudio);


        EditText txtTranscriptAudio = findViewById(R.id.txtTranscriptAudio);
        EditText txtScore = findViewById(R.id.txtScore);

        if  ((rec.getTranscriptedText() == null) ||(rec.getTranscriptedText().equals("")))
        {
            RemoteCalls.getTranscriptAudio(rec,rec.getIdAudioServer(), this);

        }
        else
        {
            txtTranscriptAudio.setText(rec.getTranscriptedText());
            txtScore.setText(rec.getScore());
        }
        final Button btnClose = (Button) findViewById(R.id.btnCloseContent);
        btnClose.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                finish();
            }


        });
    }

}

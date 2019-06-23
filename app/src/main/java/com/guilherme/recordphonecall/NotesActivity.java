package com.guilherme.recordphonecall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.guilherme.recordphonecall.DBEntities.Notes;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_notes);
        setSupportActionBar(toolbar);

        final TextView txtNote = (TextView) findViewById(R.id.txtNotes);
        final CheckBox chkDisplayOnce = (CheckBox) findViewById(R.id.chkDisplayOnce);

;       final String phone = getIntent().getStringExtra("phone");

        final Notes note = new Notes(getIntent().getStringExtra("phone"));

        txtNote.setText(note.getTextNotification());
        chkDisplayOnce.setChecked(note.getDisplayOnce() == 1);
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                note.setTextNotification(txtNote.getText().toString());
                note.setPhone(phone);

                if (chkDisplayOnce.isChecked())
                {
                    note.setDisplayOnce(1);
                }
                else
                {
                    note.setDisplayOnce(0);
                }



                note.update();

                Toast.makeText(getApplicationContext(),getText(R.string.record_saved_succefully),Toast.LENGTH_LONG).show();
                finish();
            }


        });

    }

}

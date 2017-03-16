package com.zzj.notes.widget.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.zzj.notes.R;
import com.zzj.notes.widget.base.NoteBaseActivity;

public class WriteNoteActivity extends NoteBaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_write_note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.id_write_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


}

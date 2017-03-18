package com.zzj.notes.widget.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zzj.notes.R;
import com.zzj.notes.model.NoteModel;
import com.zzj.notes.utils.NoteDataUtil;
import com.zzj.notes.widget.base.NoteBaseActivity;

public class WriteNoteActivity extends NoteBaseActivity implements View.OnClickListener {
    private Button saveNote;
    private EditText editText_input_title;
    private EditText editText_input_lable;
    private EditText editText_input_content;
    private NoteModel noteModel;

    @Override
    public int getLayoutId() {
        return R.layout.activity_write_note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initView() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.id_write_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        saveNote = (Button) findViewById(R.id.id_save_note);
        editText_input_title = (EditText) findViewById(R.id.editText_input_title);
        editText_input_lable = (EditText) findViewById(R.id.editText_input_lable);
        editText_input_content = (EditText) findViewById(R.id.editText_input_content);
        //editText_input_content.addTextChangedListener();
        saveNote.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_save_note:
                saveNewNote();
                break;
            default:
                break;

        }
    }

    /**
     * 保存一条新笔记
     */
    private synchronized void saveNewNote() {
        noteModel = new NoteModel();
        noteModel.setNote_id(System.currentTimeMillis());
        if (!TextUtils.isEmpty(editText_input_title.getText().toString())) {
            noteModel.setNote_title(editText_input_title.getText().toString());
        } else {
            Toast.makeText(this, "请输入标题！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(editText_input_lable.getText().toString())) {
            noteModel.setNote_lable(editText_input_lable.getText().toString());
        }
        if (!TextUtils.isEmpty(editText_input_content.getText().toString())) {
            noteModel.setNote_content(editText_input_content.getText().toString());
        } else {
            Toast.makeText(this, "请输入笔记内容！", Toast.LENGTH_SHORT).show();
            return;
        }
        noteModel.setRecorder_time(System.currentTimeMillis());
        NoteDataUtil.getNoteDataUtilInstance().saveNote(noteModel);
        Toast.makeText(this, "已经保存笔记内容！", Toast.LENGTH_SHORT).show();
        finish();
    }

//    private static class InputTitleEditeTextListener implements TextWatcher {
//
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//
//        }
//    }
}

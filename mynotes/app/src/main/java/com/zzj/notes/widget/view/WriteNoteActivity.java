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
    private Button noteSave;
    private NoteModel noteModel;

    @Override
    public int getLayoutId() {
        return R.layout.activity_write_note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            noteModel = (NoteModel) bundle.getSerializable("note");
        }
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
        noteSave = (Button) findViewById(R.id.id_back_note);
        editText_input_title = (EditText) findViewById(R.id.editText_input_title);
        editText_input_lable = (EditText) findViewById(R.id.editText_input_lable);
        editText_input_content = (EditText) findViewById(R.id.editText_input_content);
        //editText_input_content.addTextChangedListener();
        saveNote.setOnClickListener(this);
        noteSave.setOnClickListener(this);
        if (noteModel != null) {
            editText_input_title.setText(noteModel.getNote_title());
            editText_input_lable.setText(noteModel.getNote_lable());
            editText_input_content.setText(noteModel.getNote_content());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_save_note:
                if (noteModel != null) {
                    updateNoteModel();
                } else {
                    saveNewNote();
                }
                break;
            case R.id.id_back_note:
                finish();
                break;
            default:
                break;

        }
    }

    /**
     *
     */
    private synchronized void updateNoteModel() {
        if (noteModel != null) {
            NoteModel newNoteModel = new NoteModel();
            newNoteModel.setNote_id(noteModel.getNote_id());
            if (!TextUtils.isEmpty(editText_input_title.getText().toString())) {
                newNoteModel.setNote_title(editText_input_title.getText().toString());
            } else {
                Toast.makeText(this, "请输入标题！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(editText_input_lable.getText().toString())) {
                newNoteModel.setNote_lable(editText_input_lable.getText().toString());
            }
            if (!TextUtils.isEmpty(editText_input_content.getText().toString())) {
                newNoteModel.setNote_content(editText_input_content.getText().toString());
            } else {
                Toast.makeText(this, "请输入笔记内容！", Toast.LENGTH_SHORT).show();
                return;
            }
            newNoteModel.setRecorder_time(noteModel.getRecorder_time());
            if (noteModel.equals(newNoteModel)) {
                Toast.makeText(this, "没有修改笔记内容！", Toast.LENGTH_SHORT).show();
                return;
            } else {
                newNoteModel.setRecorder_time(System.currentTimeMillis());
            }
            NoteDataUtil.getNoteDataUtilInstance().updateNote(newNoteModel);
            Toast.makeText(this, "已经更新笔记内容！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 保存一条新笔记
     */
    private synchronized void saveNewNote() {
        NoteModel newNoteModel = new NoteModel();
        newNoteModel.setNote_id(System.currentTimeMillis());
        if (!TextUtils.isEmpty(editText_input_title.getText().toString())) {
            newNoteModel.setNote_title(editText_input_title.getText().toString());
        } else {
            Toast.makeText(this, "请输入标题！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(editText_input_lable.getText().toString())) {
            newNoteModel.setNote_lable(editText_input_lable.getText().toString());
        }
        if (!TextUtils.isEmpty(editText_input_content.getText().toString())) {
            newNoteModel.setNote_content(editText_input_content.getText().toString());
        } else {
            Toast.makeText(this, "请输入笔记内容！", Toast.LENGTH_SHORT).show();
            return;
        }
        newNoteModel.setRecorder_time(System.currentTimeMillis());
        NoteDataUtil.getNoteDataUtilInstance().saveNote(newNoteModel);
        Toast.makeText(this, "已经保存笔记内容！", Toast.LENGTH_SHORT).show();
        finish();
    }

}

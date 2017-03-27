package com.zzj.notes.widget.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzj.notes.NoteApplication;
import com.zzj.notes.R;
import com.zzj.notes.model.NoteModel;
import com.zzj.notes.utils.NoteDataUtil;
import com.zzj.notes.utils.SystemUtils;
import com.zzj.notes.widget.base.NoteBaseActivity;

import java.util.List;

public class WriteNoteActivity extends NoteBaseActivity implements View.OnClickListener {
    private Button saveNote;
    private EditText editText_input_title;
    //private EditText editText_input_lable;
    private EditText editText_input_content;
    private Button noteSave;
    private NoteModel noteModel;
    private TextView textviewLabel;
    private Dialog mChoiceDialog;
    private ChoiceLabelAdapter choiceLabelAdapter;
    private List<String> labelList;

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
       // editText_input_lable = (EditText) findViewById(R.id.editText_input_label);
        editText_input_content = (EditText) findViewById(R.id.editText_input_content);
        textviewLabel = (TextView) findViewById(R.id.textview_label);
        textviewLabel.setOnClickListener(this);
        //editText_input_content.addTextChangedListener();
        saveNote.setOnClickListener(this);
        noteSave.setOnClickListener(this);
        if (noteModel != null) {
            editText_input_title.setText(noteModel.getNote_title());
            textviewLabel.setText(noteModel.getNote_lable());
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
            case R.id.textview_label:
                showChoiceDialog();
                break;
            default:
                break;

        }
    }

    /**
     * 更新笔记内容
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
            if (!TextUtils.isEmpty(textviewLabel.getText().toString())) {
                newNoteModel.setNote_lable(textviewLabel.getText().toString());
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
        if (!TextUtils.isEmpty(textviewLabel.getText().toString())) {
            newNoteModel.setNote_lable(textviewLabel.getText().toString());
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

    private void showChoiceDialog() {
        if (mChoiceDialog == null) {
            mChoiceDialog = new Dialog(this, R.style.choiceLabelDialog);
            LinearLayout rootDialog = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.layout_choice_label, null);
            Window dialogWindow = mChoiceDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.noteDialogDStyle); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6);
            lp.width = getWindowManager().getDefaultDisplay().getWidth();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            GridView labelView = (GridView) rootDialog.findViewById(R.id.view_label);
            choiceLabelAdapter = new ChoiceLabelAdapter();
            labelView.setAdapter(choiceLabelAdapter);
            mChoiceDialog.setContentView(rootDialog);

        }
        labelList = SettingActivity.getLabels();
        mChoiceDialog.show();
    }

    private class ChoiceLabelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (labelList == null) {
                return 1;
            } else {
                return labelList.size() + 1;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if (labelList == null || labelList.size() < 1) {
                return null;
            }
            LableHolder holder;

            if (view == null) {
                view = View.inflate(NoteApplication.getNoteApplication(), R.layout.item_label, null);
                holder = new LableHolder();
                holder.text_label_name = (TextView) view.findViewById(R.id.text_label_name);
                view.setTag(holder);
            } else {
                holder = (LableHolder) view.getTag();
            }
            if (holder != null) {
                if (position < labelList.size()) {
                    holder.text_label_name.setText(labelList.get(position));
                    holder.text_label_name.setBackgroundColor(Color.parseColor("#00000000"));
                    holder.text_label_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textviewLabel.setText(labelList.get(position));
                            if (mChoiceDialog != null) {
                                mChoiceDialog.dismiss();
                            }
                        }
                    });
                } else {
                    holder.text_label_name.setText("");
                    holder.text_label_name.setBackgroundResource(R.mipmap.icon_add_label);
                    holder.text_label_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("addNote", 10001);
                            SystemUtils.jumpActivityForResult(WriteNoteActivity.this,
                                    SettingActivity.class, 10000, bundle);
                        }
                    });
                }

            }
            return view;
        }
    }

    private class LableHolder {
        public TextView text_label_name;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("yjl", "requestCode:" + requestCode + "====resultCode:" + resultCode);
        if (requestCode == 10000 && resultCode == 10002) {
            labelList = SettingActivity.getLabels();
//            Log.e("yjl", "labelList:" + labelList.size());
            if (choiceLabelAdapter != null) {
                choiceLabelAdapter.notifyDataSetChanged();
            }
        }
    }
}

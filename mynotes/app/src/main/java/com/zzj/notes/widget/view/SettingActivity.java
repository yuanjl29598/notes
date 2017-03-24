package com.zzj.notes.widget.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.zzj.notes.NoteApplication;
import com.zzj.notes.R;
import com.zzj.notes.utils.PreferenceHelper;
import com.zzj.notes.widget.base.NoteBaseActivity;

public class SettingActivity extends NoteBaseActivity implements View.OnClickListener {

    private GridView addLabelView;
    private String[] labels;
    private GridViewAdapter gridAdapter;
    private TextView backView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        addLabelView = (GridView) findViewById(R.id.view_add_label);
        backView = (TextView) findViewById(R.id.id_back_note);
        backView.setOnClickListener(this);
        labels = getLabels();
        gridAdapter = new GridViewAdapter(labels);
        addLabelView.setAdapter(gridAdapter);
    }


    /**
     * 保存标签
     *
     * @param noteLabel
     */
    public void saveLable(String noteLabel) {
        PreferenceHelper.write(this, "noteKey", "note_lable", noteLabel);
    }

    /**
     * 读取本地标签
     *
     * @return
     */
    public String readLabel() {
        return PreferenceHelper.readString(this, "noteKey", "note_lable", "任务-计划-生活-工作-账号密码");
    }

    public void buildLablesString(String label) {
        String labels = readLabel();
        if (!TextUtils.isEmpty(labels) && TextUtils.isEmpty(label)) {
            StringBuilder strBuild = new StringBuilder(labels);
            strBuild.append("-");
            strBuild.append(label);
        }

    }

    public String[] getLabels() {
        String str = readLabel();
        String[] labels = null;
        if (!TextUtils.isEmpty(str)) {
            labels = str.split("-");
        }
        return labels;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_back_note:
                finish();
                break;
            default:
                break;
        }
    }


    private class GridViewAdapter extends BaseAdapter {

        private String[] labelsName = null;

        public GridViewAdapter(String[] labelsName) {
            this.labelsName = labelsName;
        }

        @Override
        public int getCount() {
            if (labelsName != null) {
                return labelsName.length + 1;
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return labelsName[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (labelsName == null || labelsName.length < 1) {
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
                if (i == 0) {
                    holder.text_label_name.setText("");
                    holder.text_label_name.setBackgroundResource(R.mipmap.icon_add_label);
                } else {
                    holder.text_label_name.setBackgroundColor(Color.parseColor("#00000000"));
                    holder.text_label_name.setText(labelsName[i - 1]);
                }
            }
            return view;
        }
    }

    private static class LableHolder {
        public TextView text_label_name;
    }
}

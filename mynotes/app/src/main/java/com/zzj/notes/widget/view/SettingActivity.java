package com.zzj.notes.widget.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzj.notes.NoteApplication;
import com.zzj.notes.R;
import com.zzj.notes.utils.PreferenceHelper;
import com.zzj.notes.widget.base.NoteBaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends NoteBaseActivity implements View.OnClickListener {

    private GridView addLabelView;
    private List<String> labelsList;
    private GridViewAdapter gridAdapter;
    private TextView backView;
    private String inputLabel;

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
        labelsList = getLabels();
        gridAdapter = new GridViewAdapter();
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

    public String addLablesString(String label) {
        String labels = readLabel();
        if (!TextUtils.isEmpty(labels) && !TextUtils.isEmpty(label)) {
            StringBuilder strBuild = new StringBuilder(labels);
            strBuild.append("-");
            strBuild.append(label);
            return strBuild.toString();
        }
        return labels;
    }

    public List<String> getLabels() {
        List<String> strList = new ArrayList<>();
        String str = readLabel();
        String[] labels = null;
        if (!TextUtils.isEmpty(str)) {
            labels = str.split("-");
        }
        for (String strLab : labels) {
            if (!TextUtils.isEmpty(strLab)) {
                strList.add(strLab);
            }
        }
        return strList;
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

        //private String[] labelsName = null;
//
//        public GridViewAdapter(String[] labelsName) {
//            this.labelsName = labelsName;
//        }

        @Override
        public int getCount() {
            if (labelsList != null) {
                return labelsList.size() + 1;
            }
            return 0;
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
            if (labelsList == null || labelsList.size() < 1) {
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
                if (position == 0) {
                    holder.text_label_name.setText("");
                    holder.text_label_name.setBackgroundResource(R.mipmap.icon_add_label);
                    holder.text_label_name.setOnLongClickListener(null);
                    holder.text_label_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showAddLabelDialog();
                        }
                    });
                } else {
                    holder.text_label_name.setBackgroundColor(Color.parseColor("#00000000"));
                    holder.text_label_name.setText(labelsList.get(position - 1));
                    holder.text_label_name.setOnClickListener(null);
                    holder.text_label_name.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            final String deleLable = labelsList.get(position - 1);
                            AlertDialog.Builder deleDialog = new AlertDialog.Builder(SettingActivity.this);
                            deleDialog.setTitle("确定删除标签 " + deleLable + " 吗？");
                            deleDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(SettingActivity.this, "取消删除~", Toast.LENGTH_SHORT).show();
                                }
                            });
                            deleDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    labelsList.remove(position - 1);
                                    StringBuilder builder = new StringBuilder();
                                    for (String strLab : labelsList) {
                                        if (builder.length() == 0) {
                                            builder.append(strLab);
                                        } else {
                                            builder.append("-");
                                            builder.append(strLab);
                                        }
                                    }
                                    saveLable(builder.toString());
                                    Toast.makeText(SettingActivity.this,
                                            "删除标签 " + deleLable + " 成功!", Toast.LENGTH_SHORT).show();
                                    gridAdapter.notifyDataSetChanged();
                                }
                            });
                            deleDialog.show();
                            return false;
                        }
                    });
                }
            }
            return view;
        }
    }

    private static class LableHolder {
        public TextView text_label_name;
    }

    public void showAddLabelDialog() {
        final EditText editText = new EditText(SettingActivity.this);
        editText.setHint("输入标签，最多四个字");
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputLabel = editable.toString().trim();
            }
        });
        final AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(SettingActivity.this);
        inputDialog.setTitle("添加一个标签").setView(editText);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!TextUtils.isEmpty(inputLabel)) {
                    saveLable(addLablesString(inputLabel));
                    Toast.makeText(SettingActivity.this, "添加标签成功~", Toast.LENGTH_SHORT).show();
                    labelsList = getLabels();
                    gridAdapter.notifyDataSetChanged();
                }
            }
        });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(SettingActivity.this, "取消添加~", Toast.LENGTH_SHORT).show();
            }
        });
        inputDialog.setCancelable(false);
        inputDialog.show();
    }

}

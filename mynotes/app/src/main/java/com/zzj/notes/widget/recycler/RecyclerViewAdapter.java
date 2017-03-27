package com.zzj.notes.widget.recycler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zzj.notes.R;
import com.zzj.notes.model.NoteModel;
import com.zzj.notes.utils.MyTimeUtils;
import com.zzj.notes.utils.NoteDataUtil;
import com.zzj.notes.widget.view.WriteNoteActivity;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NoteViewHolder> {

    private Context mContext;
    private ArrayList<NoteModel> noteList;

    public RecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setNoteList(ArrayList<NoteModel> noteList) {
        this.noteList = noteList;
    }

    @Override
    public RecyclerViewAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.fragment_item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.NoteViewHolder holder, final int position) {
        final View view = holder.mView;
        if (noteList != null && noteList.size() > position) {
            final NoteModel noteModel = noteList.get(position);
            holder.noteContent.setText(noteModel.getNote_content());
            holder.noteTitle.setText(noteModel.getNote_title());
            holder.noteLable.setText(noteModel.getNote_lable());
            String week = MyTimeUtils.getWeekOfDate(new Date(noteModel.recorder_time));
            holder.noteWeek.setText(week);
            String creatTime = MyTimeUtils.formatDate(new Date(noteModel.recorder_time), MyTimeUtils.FORMAT_LONG_CN);
            holder.noteTime.setText(creatTime);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationZ", 20, 0);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //点击item后打开的页面
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("note", noteModel);
                            intent.putExtras(bundle);
                            intent.setClass(mContext, WriteNoteActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                    animator.start();
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder deleDialog = new AlertDialog.Builder(mContext);
                    deleDialog.setTitle("确定删除笔记" + noteModel.getNote_title() + " 吗？");
                    deleDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(mContext, "取消删除~", Toast.LENGTH_SHORT).show();
                        }
                    });
                    deleDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (NoteDataUtil.getNoteDataUtilInstance().deleteNote(noteModel.getNote_id())) {
                                noteList.remove(position);
                                Toast.makeText(mContext,
                                        "删除标签 " + noteModel.getNote_title() + " 成功!", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        }
                    });
                    deleDialog.show();
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (noteList != null) {
            return noteList.size();
        } else {
            return -1;
        }
    }

    //显示每条笔记的属性的地方
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public final View mView; //主条目

        @InjectView(R.id.note_week)
        public TextView noteWeek;

        @InjectView(R.id.note_create_time)
        public TextView noteTime;

        @InjectView(R.id.note_lable)
        public TextView noteLable;

        @InjectView(R.id.note_title)
        public TextView noteTitle;

        @InjectView(R.id.note_content)
        public TextView noteContent;

        public NoteViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            mView = view;
        }
    }
}

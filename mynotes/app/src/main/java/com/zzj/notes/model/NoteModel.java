package com.zzj.notes.model;

import com.zzj.notes.utils.NoteDataUtil;
import com.zzj.notes.utils.annotation.Column;
import com.zzj.notes.utils.annotation.Id;
import com.zzj.notes.utils.annotation.Table;

import java.io.Serializable;

/**
 * Created by yjl on 2017/3/15.
 */
@Table(name = NoteDataUtil.TABLE_NAME)
public class NoteModel implements Serializable {

    @Id
    @Column(name = "note_id")
    private long note_id; //笔记id,创建时间

    @Column(name = "note_title")
    public String note_title; //标题

    @Column(name = "note_content")
    public String note_content; //笔记内容

    @Column(name = "note_lable")
    public String note_lable; //笔记标签

    @Column(name = "recorder_time")
    public long recorder_time; //笔记最后修改时间

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }

    public String getNote_lable() {
        return note_lable;
    }

    public void setNote_lable(String note_lable) {
        this.note_lable = note_lable;
    }

    public long getRecorder_time() {
        return recorder_time;
    }

    public void setRecorder_time(long recorder_time) {
        this.recorder_time = recorder_time;
    }


    //重写equals方法，比较两个NoteModel对象是否一样
    public boolean equals(NoteModel newNote) {
        if (newNote == null) {
            return false;
        }
        if ((newNote.getNote_id() != this.getNote_id())
                || !newNote.getNote_title().equals(this.getNote_title())
                || !newNote.getNote_lable().equals(this.getNote_lable())
                || !newNote.getNote_content().equals(this.getNote_content())
                || newNote.getRecorder_time() != this.getRecorder_time()) {
            return false;
        }
        return true;
    }
}

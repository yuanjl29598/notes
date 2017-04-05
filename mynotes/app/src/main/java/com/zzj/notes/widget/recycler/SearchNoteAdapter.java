package com.zzj.notes.widget.recycler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zzj.notes.model.NoteModel;

import java.util.List;

/**
 * Created by yjl on 2017/4/5.
 */

public class SearchNoteAdapter extends BaseAdapter {

    private List<NoteModel> contentList;

    public SearchNoteAdapter() {
        super();
    }

    public void setContentString(List<NoteModel> noteModelList) {
        contentList = noteModelList;
    }

    @Override
    public int getCount() {
        if (contentList != null) {
            return contentList.size();
        } else return 0;
    }

    @Override
    public NoteModel getItem(int i) {
        if (contentList != null) {
            return contentList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}

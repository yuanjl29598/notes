package com.zzj.notes;

import android.app.Application;

import com.zzj.notes.utils.NoteDataUtil;

/**
 * Created by yjl on 2017/3/16.
 */

public class NoteApplication extends Application {
    private static NoteApplication noteApplication;

    public static NoteApplication getNoteApplication() {
        return noteApplication;
    }

    public NoteApplication() {
        if (noteApplication == null) {
            noteApplication = this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        NoteDataUtil.init(getNoteApplication());
    }
}

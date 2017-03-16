package com.zzj.notes.widget.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yjl on 2017/3/16.
 */

public abstract class NoteBaseActivity extends AppCompatActivity {
    public int getLayoutId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
    }

    public void initView() {

    }
}

package com.zzj.notes.widget.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by yjl on 2017/3/16.
 */

public abstract class NoteBaseActivity extends Activity {
    public abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        setContentView(getLayoutId());
    }
}

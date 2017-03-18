package com.zzj.notes.widget.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yjl on 2017/3/16.
 */

public class BaseFragment extends Fragment {
    protected NoteBaseActivity mActivity;
    private View mView;

    public View getRootView() {
        return mView;
    }

    public int getLayoutId() {
        return 0;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (NoteBaseActivity) getActivity();
    }

    public void initViews() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), container, false);
        initViews();
        return mView;
    }

    protected View findViewById(int id) {
        return mView.findViewById(id);
    }

    protected void runUiThread(Runnable action) {
        mActivity.runOnUiThread(action);
    }
}

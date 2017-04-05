package com.zzj.notes.widget.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zzj.notes.R;
import com.zzj.notes.model.NoteModel;
import com.zzj.notes.utils.MyTimeUtils;
import com.zzj.notes.utils.NoteDataUtil;
import com.zzj.notes.widget.base.BaseFragment;
import com.zzj.notes.widget.recycler.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LookNoteFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private ArrayList<NoteModel> noteModelList;
    private SwipeRefreshLayout refreshLayout;
    private boolean loading = false;
    private LoadNoteAsyktask loadnoteAsyktask;
    private RecyclerViewAdapter adapterRecy;
    private int noteNum = 0; //获取笔记的页数，一页最多20条
    private SearchView searchView;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initViews() {
        super.initViews();
        loadData(noteNum);
        mRecyclerView = (RecyclerView) getRootView().findViewById(R.id.list_note);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        adapterRecy = new RecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(adapterRecy);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout_note);
        refreshLayout.setColorSchemeColors(getContext().getColor(R.color.colorAccent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                Log.e("yjl", "onRefresh");
                noteNum = 0;
                loadData(noteNum);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mRecyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

                int totalItemCount = layoutManager.getItemCount();

                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int VISIBLE_THRESHOLD = 2;//还剩多少个的时候进行加载
                if (!loading && totalItemCount < (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    Log.e("yjl", "onScrollChange");
                    loadData(noteNum);
                }
            }
        });
        searchView = (android.support.v7.widget.SearchView) findViewById(R.id.id_note_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("yjl", "------onQueryTextSubmit");
                if (searchView != null) {
                    // 得到输入管理对象
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                    }
                    searchView.clearFocus(); // 不获取焦点
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
                return false;
            }
        });
        //是否显示提交按钮
        searchView.setSubmitButtonEnabled(true);
    }

    private void doSearch(String searchStr) {
        if (TextUtils.isEmpty(searchStr)
                || noteModelList == null
                || noteModelList.size() == 0) {
            adapterRecy.setNoteList(noteModelList);
            adapterRecy.notifyDataSetChanged();
            return;
        }
        ArrayList<NoteModel> listModel = new ArrayList<>();
        for (NoteModel model : noteModelList) {
            if (model.getNote_title().contains(searchStr)
                    || model.getNote_lable().contains(searchStr)
                    || model.getNote_content().contains(searchStr)) {
                listModel.add(model);
            }
        }
        if (listModel.size() > 0) {
            adapterRecy.setNoteList(listModel);
            adapterRecy.notifyDataSetChanged();
        } else {
            adapterRecy.setNoteList(noteModelList);
            adapterRecy.notifyDataSetChanged();
        }

    }

    /**
     * 分页拉取
     */
    private void loadData(int num) {
        if (loadnoteAsyktask != null
                && !loadnoteAsyktask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            loadnoteAsyktask.cancel(true);
        }
        Log.e("yjl", "loading：" + loading);
        if (!loading) {
            loading = true;
            Log.e("yjl", "加载数据～～");
            loadnoteAsyktask = new LoadNoteAsyktask();
            loadnoteAsyktask.execute(num);
        }
    }

    @Override
    public int getLayoutId() {
        //return R.layout.fragment_item_look_note_list;
        return R.layout.fragment_look_note;
    }

    private class LoadNoteAsyktask extends AsyncTask<Integer, Void, List<NoteModel>> {

        @Override
        protected List<NoteModel> doInBackground(Integer... integers) {
            Log.e("yjl", "doInBackground");
            int num = integers[0];
            ArrayList<NoteModel> listNote = NoteDataUtil.getNoteDataUtilInstance().selectNote(20, num);
            if (listNote != null) {
                Log.e("yjl", "num:" + num + "===加载数据条数：" + listNote.size());
                for (NoteModel notemodel : listNote) {
                    Log.e("yjl", "笔记标题：" + notemodel.getNote_title() + "====time:"
                            + MyTimeUtils.formatDate(new Date(notemodel.getRecorder_time()), MyTimeUtils.FORMAT_LONG_CN) +
                            "====lable:" + notemodel.getNote_lable());
                }
            } else {
                Log.e("yjl", "num" + num + "====加载数据条数：" + 0);
            }

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return listNote;
        }

        @Override
        protected void onPostExecute(List<NoteModel> moreNoteModels) {
            super.onPostExecute(moreNoteModels);
            Log.e("yjl", "onPostExecute");
            loading = false;
            refreshLayout.setRefreshing(false);
            if (moreNoteModels == null || moreNoteModels.size() == 0) {
                Toast.makeText(getContext(), "就只有这么多笔记了", Toast.LENGTH_SHORT).show();
            } else {
                if (noteModelList == null) {
                    noteModelList = new ArrayList<>();
                }
                if (noteNum == 0) {
                    noteModelList.clear();
                }
                noteModelList.addAll(moreNoteModels);
                noteNum++;
                adapterRecy.setNoteList(noteModelList);
                adapterRecy.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (loadnoteAsyktask != null) {
            loadnoteAsyktask.cancel(true);
        }
        super.onDestroy();
    }
}

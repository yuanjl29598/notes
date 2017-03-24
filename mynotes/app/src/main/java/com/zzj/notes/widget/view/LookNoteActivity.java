package com.zzj.notes.widget.view;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zzj.notes.R;
import com.zzj.notes.utils.SystemUtils;
import com.zzj.notes.widget.base.BaseFragment;
import com.zzj.notes.widget.base.NoteBaseActivity;
import com.zzj.notes.widget.fragment.FragmentAdapter;
import com.zzj.notes.widget.fragment.LookNoteFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yjl on 2017/3/16.
 * 查看笔记的主界面
 */

public class LookNoteActivity extends NoteBaseActivity {
    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public int getLayoutId() {
        return R.layout.layout_look_note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer);
        NavigationView navigationView =
                (NavigationView) findViewById(R.id.nv_main_navigation);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加新笔记
                Intent intent = new Intent();
                intent.setClass(LookNoteActivity.this, WriteNoteActivity.class);
                startActivity(intent);
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.viewpager_note_main);
        //NoteDataUtil.getNoteDataUtilInstance().selectNote(10, 0);
        setupViewPager();
    }

    private void setupViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.tabs_note_main);
        //可在此处设置tab的标题
        // List<String> titles = new ArrayList<>();
        //titles.add("Page One");
        mTabLayout.addTab(mTabLayout.newTab());
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new LookNoteFragment());
        FragmentAdapter adapter =
                new FragmentAdapter(getSupportFragmentManager(), fragments, null);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overaction, menu);
        return true;
    }

    @TargetApi(21)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_setting:
                Toast.makeText(this, "点击设置", Toast.LENGTH_SHORT).show();
                SystemUtils.jumpActivity(LookNoteActivity.this, SettingActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
//                        Toast.makeText(NoteApplication.getNoteApplication(), "点击设置", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}

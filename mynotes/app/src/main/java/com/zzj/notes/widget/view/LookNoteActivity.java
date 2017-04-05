package com.zzj.notes.widget.view;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zzj.notes.R;
import com.zzj.notes.utils.FileUtils;
import com.zzj.notes.utils.SystemUtils;
import com.zzj.notes.widget.base.BaseFragment;
import com.zzj.notes.widget.base.NoteBaseActivity;
import com.zzj.notes.widget.fragment.FragmentAdapter;
import com.zzj.notes.widget.fragment.LookNoteFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zzj.notes.utils.FileUtils.getAbsoluteImagePath;
import static com.zzj.notes.utils.FileUtils.isFileExist;


/**
 * Created by yjl on 2017/3/16.
 * 查看笔记的主界面
 */

public class LookNoteActivity extends NoteBaseActivity {
    public final static int RESULT_PICK_FROM_CAMERA = 1;
    public final static int RESULT_SELECT_PICTURE = 2;
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private File tempFile;
    private Bitmap bitmap;
    private String tempFilePath = "";
    private long time;


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
        //侧边栏部分
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
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


    // 裁剪图片的方法
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private void pickImageFromCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            time = System.currentTimeMillis();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory(), time + PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, RESULT_PICK_FROM_CAMERA);
    }

    // 判断是否有sd卡
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void showChoicePhoto() {
        AlertDialog.Builder deleDialog = new AlertDialog.Builder(this);
        deleDialog.setTitle("选择头像");
        deleDialog.setNegativeButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pickImageFromCamera();
            }
        });
        deleDialog.setPositiveButton("相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(
                        intent, RESULT_SELECT_PICTURE);
            }
        });
        deleDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent it) {
        if (requestCode == RESULT_PICK_FROM_CAMERA) {
            if (hasSdcard()) {
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        time + PHOTO_FILE_NAME);
                String filePath = tempFile.getAbsolutePath();
                Log.e("yjl", "tempFile++++" + tempFile.length());
                if (!tempFile.exists() || tempFile.length() == 0) {
                    return;
                }
                crop(Uri.fromFile(tempFile));
                if (filePath != null && isFileExist(filePath) != null) {
                    tempFilePath = filePath;

                }
            }
            /*
             * if(it!=null){ crop(it.getData()); }
			 */

        } else if (requestCode == RESULT_SELECT_PICTURE) {
            if (it == null) {
                return;
            }

            String filePath = getAbsoluteImagePath(this,
                    it.getData());
            crop(it.getData());
            Log.e("yjl", "filePath = " + filePath);
            if (filePath != null && isFileExist(filePath) != null) {
                tempFilePath = filePath;
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            if (it != null) {
                bitmap = it.getParcelableExtra("data");
                //iv_hread.setImageBitmap(bitmap);
                if (bitmap != null) {
                    String fileName = "headphoto" + "_"
                            + System.currentTimeMillis() + 0 + ".jpg";

                    String filePath = FileUtils
                            .saveBitmapFileWithFormat(bitmap, fileName, ".jpg");
                    // request(filePath);
                }
            }
        }

    }


}

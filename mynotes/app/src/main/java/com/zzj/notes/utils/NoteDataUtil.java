package com.zzj.notes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zzj.notes.model.NoteModel;
import com.zzj.notes.utils.annotation.Table;
import com.zzj.notes.utils.dataUtil.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by yjl on 2017/3/16.
 */
@Table(name = "")
public class NoteDataUtil {
    private DatabaseHelper helper;
    private static NoteDataUtil dataUtil = new NoteDataUtil();
    private static final String DBNAME = "zzj_note.db"; // 数据库名称
    public static final String TABLE_NAME = "zzj_note_table";// 表名

    private NoteDataUtil() {
    }

    /**
     * 初始化的context 只用于初始化
     *
     * @param context
     * @return
     */
    public static void init(Context context) {
        if (dataUtil.helper == null) {
            dataUtil.helper = new DatabaseHelper(context, DBNAME, null, 1, NoteModel.class);
        }
        //return dataUtil;
    }

    public static NoteDataUtil getNoteDataUtilInstance() {
        return dataUtil;
    }

    /**
     * 将笔记存储至本地数据库
     */
    public void saveNote(NoteModel noteModel) {
        // LogUtil.e("yjl", "保存的数据:" + str);
        if (noteModel == null) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            ContentValues cv = new ContentValues();
            cv.put("note_id", noteModel.getNote_id());
            cv.put("note_title", noteModel.getNote_title());
            cv.put("note_content", noteModel.note_content);
            cv.put("note_lable", noteModel.note_lable);
            cv.put("recorder_time", noteModel.recorder_time);
            // 按照时间戳来保存
            db = this.helper.getWritableDatabase();
            db.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * 分页查询
     *
     * @param limitNum  查询的条数
     * @param offsetNum 查询的偏移量
     */
    public ArrayList<NoteModel> selectNote(int limitNum, int offsetNum) {

        SQLiteDatabase db = null;
        Cursor cu = null;
        ArrayList<NoteModel> modelList = null;
        try {
            db = this.helper.getWritableDatabase();
            //分页查找所有数据，根据创建时间
            //cu = db.rawQuery("select * from " + TABLE_NAME + " limit " + offsetNum * limitNum + " , " + limitNum, new String[]{});

            //分页查询，按最后修改时间查询  ASC 为升序，DESC 为降序
            cu = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY recorder_time DESC"
                    + " limit " + offsetNum * limitNum + " , "
                    + limitNum, new String[]{});

            //Log.e("yjl", "笔记的条数：" + cu.getCount());
            int idNum = cu.getColumnIndexOrThrow("note_id");
            int titleNum = cu.getColumnIndexOrThrow("note_title");
            int lableNum = cu.getColumnIndexOrThrow("note_lable");
            int contentNum = cu.getColumnIndexOrThrow("note_content");
            int recordeNum = cu.getColumnIndexOrThrow("recorder_time");
            modelList = new ArrayList<>();
            while (cu.moveToNext()) {
                NoteModel model = new NoteModel();
                model.setNote_id(cu.getLong(idNum));
                model.setNote_title(cu.getString(titleNum));
                model.setNote_content(cu.getString(contentNum));
                model.setNote_lable(cu.getString(lableNum));
                model.setRecorder_time(cu.getLong(recordeNum));
                modelList.add(model);
                //Log.e("yjl", "查询笔记的标题：" + model.getNote_title());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cu != null) {
                cu.close();
            }
            if (db != null) db.close();
        }
        return modelList;
    }

    /**
     * 查询所有记录降序
     */
    public void selectAllNote() {

        SQLiteDatabase db = null;
        Cursor cu = null;
        try {
            db = this.helper.getWritableDatabase();
            cu = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY recorder_time DESC", new String[]{});
            Log.e("yjl", "笔记的条数：" + cu.getCount());
            int idNum = cu.getColumnIndexOrThrow("note_id");
            int titleNum = cu.getColumnIndexOrThrow("note_title");
            int lableNum = cu.getColumnIndexOrThrow("note_lable");
            int contentNum = cu.getColumnIndexOrThrow("note_content");
            int recordeNum = cu.getColumnIndexOrThrow("recorder_time");
            ArrayList<NoteModel> modelList = new ArrayList<>();
            while (cu.moveToNext()) {
                NoteModel model = new NoteModel();
                model.setNote_id(cu.getLong(idNum));
                model.setNote_title(cu.getString(titleNum));
                model.setNote_content(cu.getString(contentNum));
                model.setNote_lable(cu.getString(lableNum));
                model.setRecorder_time(cu.getLong(recordeNum));
                modelList.add(model);
                Log.e("yjl", "查询笔记的标题：" + model.getNote_title());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cu != null) {
                cu.close();
            }
            if (db != null) db.close();
        }
    }

    /**
     * 更新本地数据库笔记
     *
     * @param noteModel //修改过的笔记
     */
    public void updateNote(NoteModel noteModel) {
        if (noteModel == null) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            db = this.helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("note_title", noteModel.getNote_title());
            cv.put("note_content", noteModel.note_content);
            cv.put("note_lable", noteModel.note_lable);
            cv.put("recorder_time", noteModel.recorder_time);
            db.update(TABLE_NAME, cv, "note_id=?", new String[]{"" + noteModel.getNote_id()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
    }

    //获取笔记数目
    public int getContentCount() {
        SQLiteDatabase db = null;
        int count = 0;
        try {
            db = this.helper.getReadableDatabase();
            Cursor cu = db.rawQuery("select id, content from " + TABLE_NAME, new String[]{});
            count = cu.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
        return count;
    }
}

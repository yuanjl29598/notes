package com.zzj.notes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzj.notes.model.NoteModel;
import com.zzj.notes.utils.annotation.Table;
import com.zzj.notes.utils.dataUtil.DatabaseHelper;

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

    public NoteDataUtil getNoteDataUtilInstance() {
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

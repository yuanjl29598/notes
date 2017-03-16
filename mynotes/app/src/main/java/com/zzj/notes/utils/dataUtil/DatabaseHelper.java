package com.zzj.notes.utils.dataUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private Class<?>[] modelClasses;

	public DatabaseHelper(Context context, String name,
			CursorFactory cursorFactory, int version, Class<?>... modelClasses) {
		super(context, name, cursorFactory, version);
		this.modelClasses = modelClasses;
	}

	public void onCreate(SQLiteDatabase db) {
		TableHelper.createTablesByClasses(db, this.modelClasses);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		TableHelper.dropTablesByClasses(db, this.modelClasses);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
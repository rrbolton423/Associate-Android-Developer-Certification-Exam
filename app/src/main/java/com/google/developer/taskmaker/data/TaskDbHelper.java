package com.google.developer.taskmaker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.developer.taskmaker.R;
import com.google.developer.taskmaker.data.DatabaseContract.TaskColumns;

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_TASKS = String.format("CREATE TABLE %s"
            +" (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER)",
            DatabaseContract.TABLE_TASKS,
            TaskColumns._ID,
            TaskColumns.DESCRIPTION,
            TaskColumns.IS_COMPLETE,
            TaskColumns.IS_PRIORITY,
            TaskColumns.DUE_DATE
    );

    private final Context mContext;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASKS);
        loadDemoTask(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_TASKS);
        onCreate(db);
    }

    private void loadDemoTask(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TaskColumns.DESCRIPTION, mContext.getResources().getString(R.string.demo_task));
        values.put(TaskColumns.IS_COMPLETE, 0);
        values.put(TaskColumns.IS_PRIORITY, 1);
        values.put(TaskColumns.DUE_DATE, Long.MAX_VALUE);

        db.insertOrThrow(DatabaseContract.TABLE_TASKS, null, values);
    }
}

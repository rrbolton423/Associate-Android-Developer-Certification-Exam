package com.google.developer.taskmaker.data;

import android.database.Cursor;

import static com.google.developer.taskmaker.data.DatabaseContract.*;

/**
 * Helpful data model for holding attributes related to a task.
 */
public class Task {

    /* Constants representing missing data */
    public static final long NO_DATE = Long.MAX_VALUE;
    public static final long NO_ID = -1;

    //Unique identifier in database
    public long id;
    //Task description
    public final String description;
    //Marked if task is done
    public final boolean isComplete;
    //Marked if task is priority
    public final boolean isPriority;
    //Optional due date for the task
    public final long dueDateMillis;

    /**
     * Create a new Task from discrete items
     */
    public Task(String description, boolean isComplete, boolean isPriority, long dueDateMillis) {
        this.id = NO_ID; //Not set
        this.description = description;
        this.isComplete = isComplete;
        this.isPriority = isPriority;
        this.dueDateMillis = dueDateMillis;
    }

    /**
     * Create a new Task with no due date
     */
    public Task(String description, boolean isComplete, boolean isPriority) {
        this(description, isComplete, isPriority, NO_DATE);
    }

    /**
     * Create a new task from a database Cursor
     */
    public Task(Cursor cursor) {
        this.id = getColumnLong(cursor, TaskColumns._ID);
        this.description = getColumnString(cursor, TaskColumns.DESCRIPTION);
        this.isComplete = getColumnInt(cursor, TaskColumns.IS_COMPLETE) == 1;
        this.isPriority = getColumnInt(cursor, TaskColumns.IS_PRIORITY) == 1;
        this.dueDateMillis = getColumnLong(cursor, TaskColumns.DUE_DATE);
    }

    /**
     * Return true if a due date has been set on this task.
     */
    public boolean hasDueDate() {
        return this.dueDateMillis != Long.MAX_VALUE;
    }

}

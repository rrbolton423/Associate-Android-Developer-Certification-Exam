package com.google.developer.taskmaker.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.developer.taskmaker.R;
import com.google.developer.taskmaker.views.TaskTitleView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    /* Callback for list item click events */
    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onItemToggled(boolean active, int position);
    }

    /* ViewHolder for each task item */
    public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TaskTitleView nameView;
        public TextView dateView;
        public ImageView priorityView;
        public CheckBox checkBox;

        public TaskHolder(View itemView) {
            super(itemView);

            nameView = (TaskTitleView) itemView.findViewById(R.id.text_description);
            dateView = (TextView) itemView.findViewById(R.id.text_date);
            priorityView = (ImageView) itemView.findViewById(R.id.priority);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == checkBox) {
                completionToggled(this);
            } else {
                postItemClick(this);
            }
        }
    }

    private Cursor mCursor;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public TaskAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void completionToggled(TaskHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemToggled(holder.checkBox.isChecked(), holder.getAdapterPosition());
        }
    }

    private void postItemClick(TaskHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
        }
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_task, parent, false);

        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {

        //TODO: Bind the task data to the views

        // Get the column index of the id
        int idIndex = mCursor.getColumnIndex(DatabaseContract.TaskColumns._ID);

        // Get the column index of the checkbox
        int checkboxIndex = mCursor.getColumnIndex(DatabaseContract.TaskColumns.IS_COMPLETE);

        // Get the column index of the priority
        int priorityIndex = mCursor.getColumnIndex(DatabaseContract.TaskColumns.IS_PRIORITY);

        // Get the column index of the dueDate
        int dueDateIndex = mCursor.getColumnIndex(DatabaseContract.TaskColumns.DUE_DATE);

        // Get the column index of the taskDescription
        int taskDescriptionIndex = mCursor.getColumnIndex(DatabaseContract.TaskColumns.DESCRIPTION);

        // Move cursor to position
        mCursor.moveToPosition(position);

        // Get the value of id from the cursor
        final int id = mCursor.getInt(idIndex);

        // Get the value of isComplete from the cursor
        int isComplete = mCursor.getInt(checkboxIndex);

        // Get the value of priority from the cursor
        int priority = mCursor.getInt(priorityIndex);

        // Get the value of dueDate from the cursor
        long dueDate = Long.parseLong(mCursor.getString(dueDateIndex));

        // Get the value of taskDescription from the cursor
        String taskDescription = mCursor.getString(taskDescriptionIndex);

        // Set a tag on the itemView with the id of the Task
        holder.itemView.setTag(id);

        // If the Task is done...
        if (isComplete == 1) {

            // Check the checkbox
            holder.checkBox.setChecked(true);

            // Set the state of the nameView to "DONE"
            holder.nameView.setState(TaskTitleView.DONE);

            // Strike through the text of th nameView
            holder.nameView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            // If the Task is overdue...
        } else if (dueDate < System.currentTimeMillis()) {

            // Set the state of the nameView to "OVERDUE"
            holder.nameView.setState(TaskTitleView.OVERDUE);

            // un-Check the checkbox
            holder.checkBox.setChecked(false);

        } else { // If the Task is normal...

            // Set the state of the nameView to "NORMAL"
            holder.nameView.setState(TaskTitleView.NORMAL);

            // un-Check the checkbox
            holder.checkBox.setChecked(false);
        }

        // If the Task is a priority...
        if (priority == 1) {

            // Set the priorityView ImageView with the priority icon
            holder.priorityView.setImageResource(R.drawable.ic_priority);

        } else { // If the Task is not a priority...

            // Set the priorityView ImageView with the non-priority icon
            holder.priorityView.setImageResource(R.drawable.ic_not_priority);
        }

        // If there is no due date...
        if (dueDate == Long.MAX_VALUE) {

            // Display the "Not Set" text in the dateView TextView
            holder.dateView.setText(R.string.date_empty);

        } else { // If there is a due date...

            // Format the dueDate
            CharSequence formatted = DateUtils.getRelativeTimeSpanString(mContext, dueDate);

            // Make the dateView TextView visible
            holder.dateView.setVisibility(View.VISIBLE);

            // Set the date of the Task on the dateView TextView
            holder.dateView.setText(formatted);
        }

        // Set the description of the Task on the nameView TextView
        holder.nameView.setText(taskDescription);

    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    /**
     * Retrieve a {@link Task} for the data at the given position.
     *
     * @param position Adapter item position.
     *
     * @return A new {@link Task} filled with the position's attributes.
     */
    public Task getItem(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid item position requested");
        }

        return new Task(mCursor);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public void swapCursor(Cursor cursor) {

        // Get the new cursor object passed into the method
        // and persist it as a field of the class
        mCursor = cursor;

        // Notify the adapter that the data has changed
        notifyDataSetChanged();
    }
}

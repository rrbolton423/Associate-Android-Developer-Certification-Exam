package com.google.developer.taskmaker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.developer.taskmaker.data.DatabaseContract;
import com.google.developer.taskmaker.data.TaskAdapter;
import com.google.developer.taskmaker.data.TaskUpdateService;

// Make the Activity implement the LoaderCallbacks interface
public class MainActivity extends AppCompatActivity implements
        TaskAdapter.OnItemClickListener,
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    // Create TAG for logging
    private static final String TAG = "MainActivity";

    private TaskAdapter mAdapter;

    // Create ID for the specific Loader in this Activity
    private static final int ID_TASK_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new TaskAdapter(null);
        mAdapter.setOnItemClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the Loader
        getSupportLoaderManager().initLoader(ID_TASK_LOADER, null, this);
    }

    // Override the onResume() lifecycle method
    @Override
    protected void onResume() {
        super.onResume();

        // Restart the Loader
        getSupportLoaderManager().restartLoader(ID_TASK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Click events in Floating Action Button */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    /* Click events in RecyclerView items */
    @Override
    public void onItemClick(View v, int position) {
        //TODO: Handle list item click event
        // Create an Intent to navigate to the TaskDetailActivity
        Intent detailIntent = new Intent(getBaseContext(), TaskDetailActivity.class);

        // Set the data (URI and item Id) in the Intent
        detailIntent.setData(ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, mAdapter.getItemId(position)));

        // Start the Activity, passing the Intent
        startActivity(detailIntent);
    }

    /* Click events on RecyclerView item checkboxes */
    @Override
    public void onItemToggled(boolean active, int position) {
        //TODO: Handle task item checkbox event
        // Create a ContentValues object
        ContentValues cv = new ContentValues();

        // If the Task is checked...
        if (active) {

            // Store the Task as inactive in the IS_COMPLETE column
            cv.put(DatabaseContract.TaskColumns.IS_COMPLETE, "1");

            // If the Task is not checked...
        } else {

            // Store the Task as active in the IS_COMPLETE column
            cv.put(DatabaseContract.TaskColumns.IS_COMPLETE, "0");
        }

        // Update the Task, passing the context, the ContentURI, the Task's Id,
        // and the ContentValues object
        TaskUpdateService.updateTask(this,
                ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, mAdapter.getItemId(position)),
                cv);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Return a new CursorLoader object, passing the ContentURI,
        // and the sort order
        return new CursorLoader(this,
                DatabaseContract.CONTENT_URI,
                null,
                null,
                null,
                getOrder());
    }

    private final String getOrder() {

        // Retrieve the order from SharedPreferences
        String order = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_sortBy_key), getString(R.string.pref_sortBy_default));

        // Create orderToSend String
        String orderToSend = null;

        // If the order is "default"...
        if (order.equals(getString(R.string.pref_sortBy_default))) {

            // Set the sort order as "DEFAULT_SORT"
            orderToSend = DatabaseContract.DEFAULT_SORT;


        } else { // If the order is "default"...

            // Set the sort order as "DATE_SORT"
            orderToSend = DatabaseContract.DATE_SORT;

        }

        // Return the sort order to the query
        return orderToSend;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Swap the old cursor in the adapter with a new cursor
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Swap the old cursor in the adapter with a null cursor
        mAdapter.swapCursor(null);
    }
}

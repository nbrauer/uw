package nbrauer.homework252;

import nbrauer.homework252.R;
import nbrauer.homework252.provider.TaskContract.TaskColumns;
import nbrauer.homework252.provider.TaskContract;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple fragment to represent a task.
 * 
 * In full screen mode this is loaded by TaskActivity(). In split pane mode this is loaded by MainActivity. 
 * 
 * Because there is no save button we always save when we close the fragment. I am assuming this is by design for the assignment, 
 * so that we would do things a certain way. A save button would simplify certain things. 
 * 
 * @author Nik
 *
 */

public class TaskFragment extends Fragment {
	
	private static final String TAG = TaskFragment.class.getSimpleName();
	private boolean newTask = false; //whether or not we have been saved
	private long taskId = -1;
	private boolean isDeleted = false; //kind of hack-y, but because thge
	
	//Bundle keys
	public static final String KEY_TASK_ID = "ID";
	protected static final String KEY_TITLE_SUGGESTION = "TITLE_SUGGESTION";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.task_fragment, container, false);
		
		Bundle args = getArguments(); //pass in the index of the existing task if we are opening and existing one
		
		if(args != null) {
			taskId = args.getLong(KEY_TASK_ID, -1);
			
			if(taskId == -1)
				throw new IllegalArgumentException(String.format("Task ID was not found or was invalid (%s)", taskId));
			
			ContentResolver resolver = getActivity().getContentResolver();
			
			Cursor cursor = resolver.query(TaskContract.TASK_TABLE_URI, 
								TaskContract.FULL_PROJECTION, TaskContract.TaskColumns._ID + "=?", new String[] { taskId + "" }, null);
			
			if(cursor.moveToFirst()) {
				EditText taskTitle = (EditText)view.findViewById(R.id.task_title);
				taskTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskColumns.TASK_TITLE)));
				
				EditText taskBody = (EditText)view.findViewById(R.id.task_body);
				taskBody.setText(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskColumns.TASK_BODY)));
			} else {
				Log.e(TAG, String.format("An id (%s) was provided, but no record was found", taskId));
			}
			
		} else {
			newTask = true;
		}
				
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		//all we need is the ID, onStop() will save the data
		if(taskId != -1)
			outState.putLong(KEY_TASK_ID, taskId);
		else
			Log.e(TAG, "Invalid task id (-1)");
	}
	
	/**
	 * If we are saving a new task, then taskId will be updated with the id of the task produced by resolver.insert()
	 */
	
	private void saveTask() {
		
		ContentResolver resolver =  this.getActivity().getContentResolver();

		EditText taskTitle = (EditText) getView().findViewById(R.id.task_title);
		EditText taskBody = (EditText) getView().findViewById(R.id.task_body);

		//only save is we are not empty
		if(!taskTitle.getText().toString().isEmpty() || !taskBody.getText().toString().isEmpty()) {

			ContentValues cv = new ContentValues();
			cv.put(TaskContract.TaskColumns.TASK_TITLE, taskTitle.getText().toString());
			cv.put(TaskContract.TaskColumns.TASK_BODY, taskBody.getText().toString());

			if(newTask) {
				//insert the new data into the content resolver
				Uri newUri = resolver.insert(TaskContract.TASK_TABLE_URI, cv);
				taskId = ContentUris.parseId(newUri);
				newTask = false; //it's been saved, so we're no longer new
			} else {
				//update the existing record into the content resolver
				long numUpdated = resolver.update(TaskContract.TASK_TABLE_URI, cv, TaskContract.TaskColumns._ID + "=?", new String[] { taskId + "" });

				if(numUpdated == 0)
					Log.e(TAG, String.format("No records were updated using Uri %s", numUpdated));
			}
		}
	}
		
	public void deleteTaskContent() {
		if(!newTask) {
			//newTasks won't have been updated in the ContentProvider yet, so no need to delete them from storage
			ContentResolver resolver = getActivity().getContentResolver();
			resolver.delete(TaskContract.TASK_TABLE_URI, TaskContract.TaskColumns.ID + "=?", new String[] { taskId + "" });
		}
		
		isDeleted = true; //set so we don't save over ourselves in onStop()
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		if(!isDeleted)
			saveTask(); //push the data out to storage
	}
}

package nbrauer.homework252;

import nbrauer.homework252.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This is a container activity to display the tasks form when 
 * we are in single pane layout in the application. 
 * 
 * @author Nik
 *
 */

public class TaskActivity extends Activity {
	
	private static final String TAG = TaskActivity.class.getSimpleName();
	TaskFragment taskFragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_activity);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		//add the task fragment to the activity, which has all of the ui guts
		FragmentManager fm = getFragmentManager();
		taskFragment = new TaskFragment();
		
		if(extras != null) {
			//in this case we're just a wrapper for the fragment, so just pass everything through
			taskFragment.setArguments(extras);
		}
		
		getActionBar().setTitle(R.string.tasks_title);
		
		fm.beginTransaction().add(R.id.task_activity_fragment_container, taskFragment).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu_fragment_task, menu);
		
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		boolean handled = false;

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {

		case R.id.menu_delete_task:
			taskFragment.deleteTaskContent();
			finish();
			break;

		default:
			Log.e(TAG, "Unrecognized menu selection");

		}

		return handled;
	}
}

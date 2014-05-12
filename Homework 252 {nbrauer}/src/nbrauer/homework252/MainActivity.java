package nbrauer.homework252;

import nbrauer.homework252.provider.TaskContract.TaskColumns;
import nbrauer.homework252.provider.TaskContract;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;

/**
 * Implements dual frame and full screen modes. Since the older method for managing cursor state according to Activity lifecycle is 
 * deprecated, you pretty much have to use a CursorLoader to dynamically manage data from an SQLite database (or use the deprecated method).
 * 
 * This Activity implements a LoaderCallback<Cursor>, so that a CursorLoader can be used with the ListFragment
 * 
 * @author Nik
 *
 */

public class MainActivity extends Activity implements LoaderCallbacks<Cursor> {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int LOAD_URL_ID = 0;
	SimpleCursorAdapter _adapter = null;
	
	boolean fullScreen = false; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TODO - change this back to activity_main and put it in the proper res folder 
        setContentView(R.layout.activity_main);
       
        View v = findViewById(R.id.main_activity_task_fragment_container);
        
        if(v != null)
        	fullScreen = false;
        else
        	fullScreen = true;
        
    	String[] from = { TaskContract.TaskColumns.TASK_TITLE };
    	int[] to = { android.R.id.text1 };
    	int flags = 0;
        
        ActionBar actionBar = getActionBar();
        
        if(fullScreen)
        	actionBar.setHomeButtonEnabled(true);
        
        FragmentManager fm = getFragmentManager();
        ListFragment lf = (ListFragment)fm.findFragmentById(R.id.listFragment);
        
        if(lf == null) {
        	Log.e(TAG, "List fragment could not be found!");
        } else {
        	lf.setEmptyText(getString(R.string.empty_list));
        }
        
        //setup the list adapter, which reads from a content store
        _adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, flags);
    	lf.setListAdapter(_adapter);
    	
    	lf.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
				
				if(fullScreen) {
					//start a new Activity
					Intent intent = new Intent(MainActivity.this, TaskActivity.class);
					intent.putExtra(TaskFragment.KEY_TASK_ID, id);
					startActivity(intent);
				} else {
					FragmentManager fm = getFragmentManager();
	
					TaskFragment taskFragment = new TaskFragment();
					Bundle args = new Bundle();
					
					args.putLong(TaskFragment.KEY_TASK_ID, id);
					taskFragment.setArguments(args);
					
					//if we are clicking through the list I won't want to leave a large number of FragmentTransactions in my wake
					//otherwise we end up with things like deleting a Task and having it still show up when we navigate backwards
					fm.popBackStackImmediate();
					
					//replace what's in the frame, but record the transaction so the delete button makes the Fragment disappear
					fm.beginTransaction().addToBackStack(null).replace(R.id.main_activity_task_fragment_container, taskFragment).commit();
				}
			}
		});
    	

        getLoaderManager().initLoader(LOAD_URL_ID, null, this); 
    }
    
    
    /*
     * OK, the menus are a little messy here.
     * 
     * If we are in full screen then the ListFragment gets an create button and a TaskFragment gets a delete button.
     * If we dual frame then both the create and delete buttons are visible 
     * 
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	if(fullScreen) {
    		getMenuInflater().inflate(R.menu.menu_single_frame_list, menu); //the menu for the ListFragment
    	} else {
    		 getMenuInflater().inflate(R.menu.menu_dual_frame, menu);    	  
    	}
    	
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	boolean handled = false;
    	
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
        
        case R.id.menu_single_frame_create_task:
        {
    		Intent intent = new Intent(this, TaskActivity.class);
    		startActivity(intent);
    		handled = true;
        }
        break;
        
        case R.id.menu_dual_frame_create_task:
        {
        	FragmentManager fm = getFragmentManager();
        	
			TaskFragment newTaskFragment = new TaskFragment();
			//we ourselves to the back stack, so we can get back to where we were before
			fm.beginTransaction().addToBackStack(null).add(R.id.main_activity_task_fragment_container, newTaskFragment).commit();
        }
        break;
        
        case R.id.menu_dual_frame_delete_task:
        {
        	FragmentManager fm = getFragmentManager();
        	TaskFragment taskFragment = (TaskFragment)fm.findFragmentById(R.id.main_activity_task_fragment_container);
        	
        	if(taskFragment != null) {
        		taskFragment.deleteTaskContent();
        		fm.popBackStackImmediate();    	
        	}
        }
        break;
        
        default:
        	Log.e(TAG, "Unrecognized menu selection");
        }
        
        return handled;
    }
    
    //Loader Stuff
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	
    	if(id == LOAD_URL_ID) {
    		Log.d(TAG, "Loading task table data");
    		return new CursorLoader(this, TaskContract.TASK_TABLE_URI, TaskContract.TASK_TITLE_PROJECTION, null, null, null);
    	}
    	
    	return null;
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    	
    	//once the loader finishes, we replace the cursor on the SimpleCursorAdapter with the new one with data
    	_adapter.changeCursor(data);
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    	_adapter.changeCursor(null);
    }
}

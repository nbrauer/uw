package nbrauer.homework252.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A database helper class. The database created here is only manipulated throw the ContentProvider
 * @author Nik
 *
 */

public class TaskSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final String TAG = TaskSQLiteOpenHelper.class.getSimpleName();
	private static final String PRIMARY_KEY_STUFF = "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";	
	
	private final static int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "tasks";
	
	private static volatile TaskSQLiteOpenHelper _instance = null;
	
	private TaskSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static TaskSQLiteOpenHelper getInstance(Context context) {
		if(_instance == null)
			_instance = new TaskSQLiteOpenHelper(context);
		
		return _instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		final String createTable = String.format("CREATE TABLE IF NOT EXISTS %s(%s %s, %s TEXT, %s TEXT);", 
				TaskContract.TaskColumns.TASK_TABLE_NAME, TaskContract.TaskColumns.ID, PRIMARY_KEY_STUFF, TaskContract.TaskColumns.TASK_TITLE, TaskContract.TaskColumns.TASK_BODY);
		
		Log.d(TAG, "Creating table with table query string: " + createTable);
		
		db.execSQL(createTable);

		Log.d(TAG, "Table created for you!");

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		Log.d(TAG, "onUpgrade called");
	}

}

package nbrauer.homework252.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Some helper stuff for the TaskContentProvider
 * @author Nik
 *
 */

public final class TaskContract {

	public static final String AUTHORITY = "nbrauer.homework252.provider";
	public static final Uri TASK_TABLE_URI = Uri.parse(String.format("content://%s/%s", TaskContract.AUTHORITY, TaskColumns.TASK_TABLE_NAME));
	
	public static final String[] TASK_TITLE_PROJECTION = { TaskColumns._ID, TaskColumns.TASK_TITLE };
	public static final String[] TASK_BODY_PROJECTION = { TaskColumns._ID, TaskColumns.TASK_BODY };
	public static final String[] FULL_PROJECTION = new String[] {
		TaskColumns.ID, 
		TaskColumns.TASK_TITLE,
		TaskColumns.TASK_BODY };
	
	public class TaskColumns implements BaseColumns {
		
		public static final String TASK_TABLE_NAME = "TASKS";
		public static final String ID = BaseColumns._ID;
		public static final String TASK_TITLE = "_title";
		public static final String TASK_BODY = "_body";
		
	
	}
}

package nbrauer.homework252.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Implemented a contract provider because it's needed to implement a CursorLoader. 
 * The startManagingCursor() method is deprecated and the alternative is to implement a CursorLoader. 
 * A CursorLoader requires a ContentProvider, so here I am. 
 * 
 * @author Nik
 *
 */

public class TaskContentProvider extends ContentProvider {

	private static final String TAG = TaskContentProvider.class.getSimpleName();
	private TaskSQLiteOpenHelper _sqlLiteHelper = null;
	private static final UriMatcher _uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	/* Uri Matcher return codes. Probably better suited for an enum*/
	private static final int TASK_TABLE_ID = 0;
	private static final int TASK_ROW_ID = 1;

	static {
		//define what patterns the UriMatcher can recognize 
		_uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.TaskColumns.TASK_TABLE_NAME, TASK_TABLE_ID);  //content://<provider>/<tasks_table>
		_uriMatcher.addURI(TaskContract.AUTHORITY,  TaskContract.TaskColumns.TASK_TABLE_NAME+ "/#", TASK_ROW_ID); //content://<provider>/<tasks_table>/rownum		
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		Log.d(TAG, "delete()");
		int numRows = -1;
		Uri retUri = null;

		synchronized(_sqlLiteHelper) {
			SQLiteDatabase db = null;

			try {

				db = _sqlLiteHelper.getWritableDatabase();

				switch (_uriMatcher.match(uri)) {
				case TASK_TABLE_ID:
					numRows = db.delete(TaskContract.TaskColumns.TASK_TABLE_NAME, where, whereArgs);

					if (numRows == 0) { //failed?
						Log.d(TAG,
								String.format(
										"Delete in table %s affected 0 rows. Where=%s, whereArgs=%s",
										TaskContract.TaskColumns.TASK_TABLE_NAME, where, whereArgs.toString()));

					} else {
						//success
						getContext().getContentResolver().notifyChange(uri, null); //tell any ContentResolvers to refresh
					}
					break;

				default:
					Log.d(TAG,
							String.format("Uri didn't match anything %s",
									uri.toString()));
				}

			} catch(SQLiteException e) {
				Log.d(TAG, "A problem happened in delete()", e);
			}
			finally {
				if(db != null)
					db.close();
			}


		}

		return numRows;
	}

	@Override
	public String getType(Uri uri) {
		final String single = "vnd.android.cursor.item";
		final String multi = "vnd.android.cursor.dir";	
		String retString = "";
		
		
		switch(_uriMatcher.match(uri)) {
		
		case TASK_TABLE_ID:
			retString = multi + "/vnd." + TaskContract.AUTHORITY + "/" + TaskContract.TaskColumns.TASK_TABLE_NAME;
		break;
		
		case TASK_ROW_ID:
			retString = single + "/vnd." + TaskContract.AUTHORITY;
		break;	
		}
		
		return retString;
	}

	@Override
	public Uri insert(Uri uri, ContentValues cv) {
		Log.d(TAG, "insert called");
		long row = -1;
		Uri retUri = null;

		synchronized(_sqlLiteHelper) {

			switch (_uriMatcher.match(uri)) {
			case TASK_TABLE_ID:
				SQLiteDatabase db = null;
				try {

					db = _sqlLiteHelper.getWritableDatabase();

					row = db.insert(TaskContract.TaskColumns.TASK_TABLE_NAME, null, cv);
					if (row == -1) { //failed
						Log.d(TAG,
								String.format(
										"Insert into table %s failed. ContentValues were %s",
										TaskContract.TaskColumns.TASK_TABLE_NAME, cv.toString()));
					} else { //success, return the Uri with the ID appended
						retUri = ContentUris.withAppendedId(uri, row);
						getContext().getContentResolver().notifyChange(uri, null);
					}

				} catch(SQLiteException e) {
					Log.d(TAG, "A problem happened on insert()", e);
				}
				finally {
					db.close();
				}


				break;

			default:
				Log.d(TAG,
						String.format("Uri didn't match anything %s",
								uri.toString()));
			}
		}

		Log.d(TAG, String.format("Returning URI %s", retUri.toString()));

		return retUri;
	}

	@Override
	public boolean onCreate() {
		Log.i(TAG, "onCreate()");

		//only create an instance of the SQLite database. Accessing it comes later
		_sqlLiteHelper = TaskSQLiteOpenHelper.getInstance(getContext());

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query()");

		SQLiteDatabase db = null;
		Cursor c = null;
		SQLiteQueryBuilder queryBuilder = null;

		try {

			db = _sqlLiteHelper.getReadableDatabase();
			queryBuilder = new SQLiteQueryBuilder();

			queryBuilder.setTables(TaskContract.TaskColumns.TASK_TABLE_NAME);

			//if we're looking for a specific row
			if(_uriMatcher.match(uri) == TASK_ROW_ID)
				queryBuilder.appendWhere(String.format("%s=%d", TaskContract.TaskColumns.ID, ContentUris.parseId(uri)));

			synchronized(_sqlLiteHelper) {

				c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				//if there are changes then the observer of this data source will be notified
				c.setNotificationUri(getContext().getContentResolver(), uri); 
			}

		}  catch(SQLiteException e) {
			Log.d(TAG, "A problem happened on query()", e);
		}
		finally {
			//Since we are returning a Cursor, closing the database causes an exception. 
			//if(db != null)
				//db.close();
		}

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues cv, String where, String[] whereArgs) {
		Log.d(TAG, "update()");
		int numRows = -1;
		Uri retUri = null;

		synchronized(_sqlLiteHelper) {
			SQLiteDatabase db = null;

			try {

				db = _sqlLiteHelper.getWritableDatabase();

				switch (_uriMatcher.match(uri)) {
				case TASK_TABLE_ID:
					numRows = db.update(TaskContract.TaskColumns.TASK_TABLE_NAME, cv, where, whereArgs);

					if (numRows == 0) { //failed
						Log.d(TAG,
								String.format(
										"Update of table %s affected 0 rows. ContentValues=%s, where=%s, whereArgs=%s",
										TaskContract.TaskColumns.TASK_TABLE_NAME, cv.toString(), where, whereArgs.toString()));

					} else {
						getContext().getContentResolver().notifyChange(uri, null);
					}
					break;

				default:
					Log.d(TAG,
							String.format("Uri didn't match anything %s",
									uri.toString()));
				}

			} catch(SQLiteException e) {
				Log.d(TAG, "A problem happened on update()", e);
			}
			finally {
				if(db != null)
					db.close();
			}


		}

		return numRows;
	}

}

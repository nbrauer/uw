package nbrauer.homework253;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * The MainActivity is responsible for starting and stopping the activity as well as managing the notifications. When the button 
 * is pushed we start the service in the background. On onStop() we display the notification and on onResume() we remove the notification
 * if it is present. 
 * 
 * @author Nik Brauer
 *
 */

public class MainActivity extends Activity {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String BUNDLE_IS_BLEEPING = "bundle";
	private static final String KEY_IS_BLEEPING = "isbleeping";
	
	private static int BLEEPING_ID = 0;
	private NotificationManager mNotificationManager = null;
	private boolean mServiceStarted; 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		Bundle extras = this.getIntent().getExtras();
		if(extras != null) {
			
				if(!extras.containsKey(KEY_IS_BLEEPING))
					Log.e(TAG, "Extras doesn't contain expected key!");
				else {
					mServiceStarted = extras.getBoolean(KEY_IS_BLEEPING);
				}
		}

		setContentView(R.layout.activity_main);
		Button button = (Button)findViewById(R.id.button_start_service);
		setButtonText(button); 

		button.setOnClickListener(new OnClickListener() {	

			@Override
			public void onClick(View view) {
				if(!mServiceStarted) {
					startService(new Intent(MainActivity.this, BleepingService.class));
					mServiceStarted = true;
					
				} else {
					stopService(new Intent(MainActivity.this, BleepingService.class));
					mServiceStarted = false;
				}
				
				setButtonText((Button)view);
			}
		});
		
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}
	
	private void setButtonText(Button button) {
		if(!mServiceStarted) {
			button.setText(R.string.label_buttonstart);
		} else {
			button.setText(R.string.label_buttonstop);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mServiceStarted) {
			mNotificationManager.cancel(BLEEPING_ID);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(mServiceStarted) {
			//Bundle bundle = new Bundle();
			//bundle.putBoolean(MainActivity.KEY_IS_BLEEPING, true);
			
			Intent intent = new Intent(MainActivity.this, MainActivity.class);
			intent.putExtra(MainActivity.KEY_IS_BLEEPING, true);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
//			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_bell);
			
			//this means we're making noise
			Notification bleepingNotification = new Notification.Builder(MainActivity.this)
												.setContentText(getString(R.string.notification_alarm_content_text))
												.setContentIntent(pendingIntent)
												.setSmallIcon(R.drawable.ic_action_bell)
												.build();
			
			//keep the user from canceling the notification, otherwise the state of the activity on restore will be out of wack
			bleepingNotification.flags = Notification.FLAG_ONGOING_EVENT; 
			
			mNotificationManager.notify(BLEEPING_ID, bleepingNotification);									
		}
	}
}

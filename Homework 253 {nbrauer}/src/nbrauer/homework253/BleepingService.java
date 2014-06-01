package nbrauer.homework253;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * A pretty simple service, it just beeps until someone stops it. It knows nothing of notifications or the state of the activity. 
 * 
 * @author Nik Brauer
 *
 */

public class BleepingService extends Service {

	private static final String TAG = BleepingService.class.getSimpleName();
	private boolean mServiceRunning = false;
	private MediaPlayer mMediaPlayer = null;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return START_STICKY; //if we get killed, then come back. Wasn't able to simulate this. 
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
					
			mServiceRunning = true;
			// Start a new thread and bleep away
			//I used to clean up the media player onDestroy(), but I decided to keep this all on the same thread, so I didn't have to bother synchronizing
			Thread bleepingThread = new Thread(new Runnable() {
				public void run() {
					
						mMediaPlayer = new MediaPlayer();
						AssetFileDescriptor fd = BleepingService.this.getResources().openRawResourceFd(R.raw.beep);
						try {
							mMediaPlayer.setDataSource(fd.getFileDescriptor());
						
						fd.close();
						mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
						mMediaPlayer.prepare();
						} catch (IllegalArgumentException | IllegalStateException | IOException e) {
							Log.e(TAG, e.getMessage(), e);
						}
					
					Log.i(TAG, "Commence Bleeping!");

					try {

						while (mServiceRunning) {
							mMediaPlayer.start();
							Thread.sleep(5000); // sleep 5 seconds
							mMediaPlayer.seekTo(0);;							
						}

					} catch (InterruptedException e) {
						Log.e(TAG, e.getMessage(), e);
					} finally {
						mMediaPlayer.stop();
						mMediaPlayer.release();
					}
				}
			});

			bleepingThread.start();
	}
	
	@Override
	public void onDestroy() {
		
		//synchronize to avoid being able to destroy the MediaPlayer while its still being initialized, if the user hits the button quickly
			mServiceRunning = false; //cause the thread playing the sound to stop and the media player should get cleaned up
			Log.i(TAG, "Bleeping Service Processed Stop Command");
	
		super.onDestroy();
	}
}

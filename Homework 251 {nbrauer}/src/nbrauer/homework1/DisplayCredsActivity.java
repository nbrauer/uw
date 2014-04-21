package nbrauer.homework1;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * A form to display the username and password provided by the LoginActivity. 
 * 
 * These credentials are passed via a bundle. That's about it. 
 * 
 * @author Nik
 *
 */

public class DisplayCredsActivity extends Activity {
	
	public final static String BUNDLE_USERNAME_KEY = "username"; //bundle key for user name
	public final static String BUNDLE_PASSWORD_KEY = "password"; //bundle key for password
	
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);        
	        setContentView(R.layout.display_creds_activity);
	        
	        ActionBar actionBar = getActionBar();
	        actionBar.hide();
	  
	        Bundle bundle = null;
	        
	        if(savedInstanceState != null)
	        	bundle = savedInstanceState;
	        else
	        	bundle = getIntent().getExtras();
	        
	        //Pull the username and the password out of the bundle, sent from the LoginActivity
	        
	        String displayUsernameStr = bundle.getString(DisplayCredsActivity.BUNDLE_USERNAME_KEY, "");
	        String displayPasswordStr = bundle.getString(DisplayCredsActivity.BUNDLE_PASSWORD_KEY, "");
	        
	        TextView displayUsernameField = (TextView)findViewById(R.id.display_username);
	        displayUsernameField.setText(displayUsernameStr);
	        
	        TextView displayPasswordField = (TextView)findViewById(R.id.display_password);
	        displayPasswordField.setText(displayPasswordStr);
	 }
	 
	 @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		TextView displayUsernameField = (TextView)findViewById(R.id.display_username);
		TextView displayPasswordField = (TextView)findViewById(R.id.display_password);
		
		outState.putString(DisplayCredsActivity.BUNDLE_USERNAME_KEY, displayUsernameField.getText().toString());
		outState.putString(DisplayCredsActivity.BUNDLE_PASSWORD_KEY, displayPasswordField.getText().toString());
	}
}

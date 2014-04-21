package nbrauer.homework1;

import java.util.regex.Matcher;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity for this project. 
 * 
 * Action bar is hidden programmatically and all input validation is performed in the sign-in form's 
 * onclick event. All errors are displayed as Toasts to the user. This is not ideal, since they display over the keyboard. 
 * 
 * @author Nik
 *
 */

public class LoginActivity extends Activity {

	//keys to store values to support rotation (which is why they are private)
	private static final String BUNDLE_SAVE_USERNAME_KEY = "username";
	private static final String BUNDLE_SAVE_PASSWORD_KEY = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_login);
        
        ActionBar actionBar = getActionBar();
        actionBar.hide(); //hide the action bar
        
        if(savedInstanceState != null)
        {
        	//then we were restored from a rotate event
        	
    		TextView emailAddressField = (TextView)findViewById(R.id.email);
    		TextView passwordField = (TextView)findViewById(R.id.password);
    		
    		emailAddressField.setText(savedInstanceState.getString(LoginActivity.BUNDLE_SAVE_USERNAME_KEY));
    		passwordField.setText(savedInstanceState.getString(LoginActivity.BUNDLE_SAVE_PASSWORD_KEY));
        }
        
        Button loginButton = (Button)findViewById(R.id.loginbutton);
        
        loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				TextView emailAddressField = (TextView)LoginActivity.this.findViewById(R.id.email);
				TextView passwordField = (TextView)LoginActivity.this.findViewById(R.id.password);
				
				String passwordStr = passwordField.getText().toString();
				String emailAddressStr = emailAddressField.getText().toString();
				Matcher emailAddressMatcher = android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressStr);
				
				/* validate that the email field is not empty 
				 * and that it is a valid email address in the form user@domain.com
				 */
				
				//TODO - change from toasts to something more easily seen by the user
				
				if(emailAddressStr.isEmpty()) {
					//display error stating that email address is required
					Toast.makeText(LoginActivity.this, R.string.error_email_required, Toast.LENGTH_SHORT).show();
					return;
					
				} else if(!emailAddressMatcher.matches()) {
        			//display error stating that email address must be in user@domain.com format
        			Toast.makeText(LoginActivity.this, R.string.error_email_format, Toast.LENGTH_SHORT).show();
        			return;
				}
				
        		if(passwordStr.isEmpty()) {
        			//display error stating that password is required
        			Toast.makeText(LoginActivity.this, R.string.error_password_required , Toast.LENGTH_SHORT).show();
        			return;
        		}
				
        		//Open the next activity, since we've passed validation
        		
        		Intent displayCreds = new Intent(LoginActivity.this.getApplicationContext(), DisplayCredsActivity.class);
        		displayCreds.putExtra(DisplayCredsActivity.BUNDLE_USERNAME_KEY, emailAddressStr);
        		displayCreds.putExtra(DisplayCredsActivity.BUNDLE_PASSWORD_KEY, passwordStr);
        		startActivity(displayCreds);
        		
			}
        });
      
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	//support rotation
    	
		TextView emailAddressField = (TextView)LoginActivity.this.findViewById(R.id.email);
		TextView passwordField = (TextView)LoginActivity.this.findViewById(R.id.password);
		
		String passwordStr = passwordField.getText().toString();
		String emailAddressStr = emailAddressField.getText().toString();
		
		outState.putString(LoginActivity.BUNDLE_SAVE_USERNAME_KEY, emailAddressField.getText().toString());
		outState.putString(LoginActivity.BUNDLE_SAVE_PASSWORD_KEY, passwordField.getText().toString());
    }
}

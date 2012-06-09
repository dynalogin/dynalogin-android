package org.dynalogin.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Home extends Activity {
	
	SharedPreferences preferences;
	
	TextView keyTextView;
	private static Button generateButton;
	
	HOTPProvider hotp = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ProfileStore profileStore = new ProfileStore(this);
        hotp = new HOTPProvider(profileStore);
        
        preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int profileId = preferences.getInt("profileId", -1);
        if(profileId == -1) {
        	// no remembered profile, go to the list
        	final Intent intent = new Intent(Home.this, ProfileList.class);
            startActivity(intent);
            finish();
            return;
        }
        hotp.selectProfile(profileId);
        
        setContentView(R.layout.main);
        TextView profTextView = (TextView) findViewById(R.id.profNameText);
        profTextView.setFocusable(false);
        profTextView.setText(hotp.getProfileName());
        
        keyTextView = (TextView) findViewById(R.id.codeText);
        keyTextView.setFocusable(false);
        
        
        generateButton = (Button) findViewById(R.id.generateButton);
        generateButton.setOnClickListener(generateHOTPListener);
        
               
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.profiles:
        	final Intent intent = new Intent(Home.this, ProfileList.class);
            startActivity(intent);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private OnClickListener generateHOTPListener = new OnClickListener() {
        public void onClick(View v)
        {
            String key = hotp.getNextCode();
            keyTextView.setText(key);
            keyTextView.setVisibility(View.VISIBLE);
            
        }

		
    };
}
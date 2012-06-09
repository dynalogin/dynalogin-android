package org.dynalogin.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileSetup extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.profilesetup);
        /* Button expressButton = (Button) findViewById(R.id.b_profile_setup_express);
        expressButton.setOnClickListener(buttonListener); */
        Button manualButton = (Button) findViewById(R.id.b_profile_setup_manual);
        manualButton.setOnClickListener(buttonListener);
        
    }
    
    private OnClickListener buttonListener = new OnClickListener() {
        public void onClick(View v) {
        	switch(v.getId()) {
        	/* case R.id.b_profile_setup_express:
        		final Intent intent1 = new Intent(ProfileSetup.this, ProfileSetupExpress.class);
                startActivity(intent1);
                finish();
        		break; */
        	case R.id.b_profile_setup_manual:
        		final Intent intent2 = new Intent(ProfileSetup.this, ProfileSetupManual.class);
                startActivity(intent2);
                finish();
        		break;
        	default:
        	}
        }

		
    };
    
}
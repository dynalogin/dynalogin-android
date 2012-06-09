package org.dynalogin.android;

import java.io.ObjectOutputStream.PutField;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ProfileList extends ListActivity {
	private final static String TAG = ProfileList.class.getName();
    private Cursor            profilesCursor;
    private ProfileStore         profileStore;
    //private ListView          profilesList;
    private SharedPreferences preferences;
    private int               count;
    
    SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileStore = new ProfileStore(this);
        profileStore.open();
        Cursor t = profileStore.getAllProfiles();
        if (t.getCount() == 0)
        {
            setResult(RESULT_CANCELED);
            t.close();
            profileStore.close();
            final Intent intent = new Intent(ProfileList.this, ProfileSetupManual.class);
      	    Log.v(TAG, "no profiles detected, going to add one...");
            startActivity(intent);
            finish();
        }
        t.close();
        profileStore.close();
        setResult(RESULT_OK);
        preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        //setContentView(R.layout.profilelist);
        createList();
    }

    @Override
    public void onResume() {
        super.onResume();
        /* profileStore = new ProfileStore(this);
        profileStore.open();
        Cursor t = profileStore.getAllProfiles();
        if (t.getCount() == 0)
        {
            setResult(RESULT_CANCELED);
            t.close();
            profileStore.close();
            finish();
        }
        t.close();
        profileStore.close(); */
        setResult(RESULT_OK);
        //setContentView(R.layout.profilelist);
        //createList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profiles_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.add_profile:
        	// For the moment, do manual setup only
        	// In a subsequent release, the user can choose express setup
        	final Intent intent = new Intent(ProfileList.this, ProfileSetupManual.class);
            startActivity(intent);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void createList() {
        profileStore.open();
        profilesCursor = profileStore.getAllProfiles();
        count = profilesCursor.getCount();
        
        startManagingCursor(profilesCursor);
        
        
        String[] columns = new String[] { "name" };
		int[] to = new int[] { R.id.col_name };
		
		adapter =
			new SimpleCursorAdapter(this, R.layout.profilelist_entry,
					profilesCursor, columns, to);
		setListAdapter(adapter);
		
		getListView().setOnItemClickListener(profilesGridListener);
		registerForContextMenu(getListView());
        
        /* profilesCursor.close();
        profileStore.close();
        profilesList = (ListView) findViewById(R.id.profileslist);
        profilesList.setAdapter(new ProfilesAdapter(getApplicationContext()));
        profilesList.setOnItemClickListener(profilesGridListener);
        //profilesList.setOnCreateContextMenuListener(profilesContextListener);
        registerForContextMenu(profilesList); */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 27)
        {
            Log.v("Profiles", "I got the result code I needed there has been an empty screen");
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private OnItemClickListener profilesGridListener = new OnItemClickListener() {
    	public void onItemClick(
    			AdapterView<?> parent,
    			View v,
    			int position,
    			long id)
    	{
    		profileStore.open();
    		profilesCursor = profileStore.getAllProfiles();
    		profilesCursor.moveToPosition(position);
    		SharedPreferences.Editor ed = preferences.edit();
    		int col_index = profilesCursor.getColumnIndexOrThrow(ProfileStore.KEY_ROWID);
    		ed.putInt("profileId", profilesCursor.getInt(col_index));
    		Log.v(TAG, "selected profile = " + profilesCursor.getInt(col_index));
    		ed.commit();
    		profilesCursor.close();
    		profileStore.close();
    		Intent intent = new Intent(ProfileList.this, Home.class);
    		startActivity(intent);
    		finish();
    	}
    };
    
    @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenuInfo menuInfo) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.profile_context_menu, menu);
        }

        
        @Override
        public boolean onContextItemSelected(MenuItem item) {
          AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
          Cursor c = (Cursor) getListAdapter().getItem(info.position);
          int rowId = c.getInt(c.getColumnIndex(ProfileStore.KEY_ROWID));
          switch (item.getItemId()) {
          
          case R.id.profiles_context_edit:
        	  final Intent intent = new Intent(ProfileList.this, ProfileSetupManual.class);
        	  intent.putExtra("row_id", rowId);
        	  String profileName = c.getString(c.getColumnIndex(ProfileStore.KEY_PROF_NAME));
        	  String secret = c.getString(c.getColumnIndex(ProfileStore.KEY_SECRET));
        	  intent.putExtra(ProfileStore.KEY_PROF_NAME, profileName);
        	  intent.putExtra(ProfileStore.KEY_SECRET, secret);
        	  startActivity(intent);
              finish();
        	  return true;
          case R.id.profiles_context_delete:
            //deleteNote(info.id);
        	  // Must confirm deletion first...
        	  Log.v(TAG, "deleting, rowId = " + rowId);
        	  ProfileStore ps = new ProfileStore(this);
        	  ps.open();
        	  ps.deleteProfile(rowId);
        	  ps.close();
        	  
        	  profilesCursor.requery();

            return true;
          default:
            return super.onContextItemSelected(item);
          }
        }
        
}

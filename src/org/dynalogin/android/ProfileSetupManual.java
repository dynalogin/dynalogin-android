package org.dynalogin.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ProfileSetupManual extends Activity
{
	private SharedPreferences preferences;
	private static final int  MENU_CANCEL   = 1;
	private static boolean    editing       = false;
	private static int        editingRowID  = -1;
	private static boolean    delQuest      = false;
	Builder                   builder;
	ProfileStore              profileStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
		profileStore = new ProfileStore(this);
		setContentView(R.layout.profilesetupmanual);
		Button saveProfileButton = (Button) findViewById(R.id.saveProfileButton);
		builder = new AlertDialog.Builder(this);
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			editing = true;
			editingRowID = extras.getInt("row_id");
			EditText prof = (EditText) findViewById(R.id.profText);
			prof.setText(extras.getString(ProfileStore.KEY_PROF_NAME));
			EditText secret = (EditText) findViewById(R.id.secretText);
			secret.setText(extras.getString(ProfileStore.KEY_SECRET));
		} else {
			editing = false;
		}
		saveProfileButton.setOnClickListener(saveProfileListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		profileStore.open();
		Cursor c = profileStore.getAllProfiles();
		int count = c.getCount();
		c.close();
		profileStore.close();
		if (count != 0)
		{
			menu.add(0, MENU_CANCEL, 0, R.string.cancel_info).setIcon(
					R.drawable.ic_menu_cancel);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		switch (item.getItemId())
		{
		case MENU_CANCEL:
			final Intent intent = new Intent(ProfileSetupManual.this, Home.class);
			startActivityForResult(intent, 0);
			return true;
		}
		return false;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// got here from profiles (empty) send back del signal
			if (delQuest == true) {
				setResult(RESULT_CANCELED);
				finish();
			}
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnClickListener saveProfileListener = new OnClickListener() {
		
		protected void showError() {
			builder.setTitle(getString(R.string.error_title));
			builder.setMessage(getString(R.string.error));
			builder.setPositiveButton(getString(R.string.ok), null);
			builder.show();
		}
		
		public void onClick(View v)	{
			TextView profName = (TextView) findViewById(R.id.profText);
			TextView profSecret = (TextView) findViewById(R.id.secretText);
			String name = profName.getText().toString();
			String secret = profSecret.getText().toString();
			
			if (name.length() < 4) {
				showError();
				return;
			}

			profileStore.open();
			Cursor c = profileStore.getAllProfiles();
			int count = c.getCount();
			if (count != 0)	{
				c.moveToFirst();
				while (c.isAfterLast() == false) {
					if (c.getString(1).equals(name)	&& editing == false) {
						showError();
						c.close();
						profileStore.close();
						return;
					}
					c.moveToNext();
				}
			}
			int rowId = -1;
			int seq = 0;
			if (editing == false) {
				rowId = (int) profileStore.insertProfile(name, secret);
				c.close();
				profileStore.close();
			} else {
				Cursor current = profileStore.getProfile(editingRowID);
				seq = current.getInt(current.getColumnIndex(ProfileStore.KEY_SEQ));
				profileStore.deleteProfile(editingRowID);
				rowId = (int) profileStore.insertProfile(name, secret);
				profileStore.updateCount(rowId,	seq);
				current.close();
				c.close();
				profileStore.close();
			}
			SharedPreferences.Editor ed = preferences.edit();
			ed.putInt("profileId", rowId);
			ed.commit();
			final Intent intent = new Intent(ProfileSetupManual.this, Home.class);
			startActivity(intent);
			finish();
		}
	};
}
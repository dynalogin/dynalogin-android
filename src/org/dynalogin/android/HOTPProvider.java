package org.dynalogin.android;


import android.database.Cursor;


public class HOTPProvider {
	
	HOTP hotp = new HOTP();
	ProfileStore profileStore;
	
	private final static int CODE_LENGTH = 6;
	
	private int profileId = -1;
	private String profileName = null;
	private String secret = null;
	private int seq = -1;
	
	public HOTPProvider(ProfileStore _profileStore) {
		profileStore = _profileStore;
	}
	
	public void selectProfile(int _id) {
		profileStore.open();
		//Cursor profilesCursor = profileStore.getAllProfiles();
		//profilesCursor.moveToPosition(_id);

		Cursor profilesCursor = profileStore.getProfile(_id);
		
		int col_index = profilesCursor.getColumnIndexOrThrow(ProfileStore.KEY_PROF_NAME);
		profileName = profilesCursor.getString(col_index);
		
		col_index = profilesCursor.getColumnIndexOrThrow(ProfileStore.KEY_SECRET);
		secret = profilesCursor.getString(col_index);
		
		col_index = profilesCursor.getColumnIndexOrThrow(ProfileStore.KEY_SEQ);
		seq = profilesCursor.getInt(col_index);
		
		profilesCursor.close();
		profileStore.close();
		
		profileId = _id;

	}
	
	public String getProfileName() {
		return profileName;
	}
	
	public String getNextCode() {
		
		if(profileId == -1)
			return null;
        
        String code = hotp.gen(
                secret,
                seq,
                CODE_LENGTH);
        
        seq ++;
        profileStore.open();
        profileStore.updateCount(profileId, seq);
        profileStore.close();
        
        return code;
	}
	
	

}

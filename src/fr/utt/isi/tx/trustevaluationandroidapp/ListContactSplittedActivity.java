package fr.utt.isi.tx.trustevaluationandroidapp;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListContactSplittedActivity extends Activity {
	
	//tag for log
	public static final String TAG = "TrustEvaluation.ListContactSplitted";
	
	//contact types
	public static final int LOCAL    = 1;
	public static final int FACEBOOK = 2;
	public static final int TWITTER  = 3;
	public static final int LINKEDIN = 4;
	
	private ListView contactList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "activity state: onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_contact_splitted);
		
		//TODO: add progress bar
		
		//obtain handle of UI list view object
		contactList = (ListView) findViewById(R.id.contactList);
		
		//populate contact list
		populateContactList(LOCAL);
	}
	
	private void populateContactList(int contactType) {
		Log.v(TAG, "contact type: " + contactType);
		switch (contactType) {
			
			//contacts from facebook
			case FACEBOOK:
				//TODO
				break;
			
			//contacts from twitter
			case TWITTER:
				//TODO
				break;
			
			//contacts from linkedin
			case LINKEDIN:
				//TODO
				break;
			
			//contacts from local contact book
			case LOCAL:
				//cursor of contact entries
		        Cursor cursor = getLocalContacts();
		        
		        //display only the display name
		        String[] fields = new String[] {ContactsContract.Data.DISPLAY_NAME};
		        
		        //adapter based on api before lv11
		        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contact_entry, cursor, fields, new int[] {R.id.contactEntryText});
		        contactList.setAdapter(adapter);
				break;
		}
	}
	
	private Cursor getLocalContacts() {
		//query arguments
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        String selection = null;
        //String selection = ContactsContract.CommonDataKinds.Contactables.HAS_PHONE_NUMBER + " = " + 1;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        
        //query based on api before lv11
        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_contact_splitted, menu);
		return true;
	}
	
}

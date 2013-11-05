package fr.utt.isi.tx.trustevaluationandroidapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	public static final String EXTRA_CONTACT_TYPE = "contact_type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "creating activity...");
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void getLocalPhoneContacts(View view) {
		Intent intentToListContactSplitted = new Intent(this,
				ListContactSplittedActivity.class);
		intentToListContactSplitted.putExtra(EXTRA_CONTACT_TYPE,
				ListContactSplittedActivity.LOCAL_PHONE);
		startActivity(intentToListContactSplitted);
	}

	public void getLocalEmailContacts(View view) {
		Intent intentToListContactSplitted = new Intent(this,
				ListContactSplittedActivity.class);
		intentToListContactSplitted.putExtra(EXTRA_CONTACT_TYPE,
				ListContactSplittedActivity.LOCAL_EMAIL);
		startActivity(intentToListContactSplitted);
	}

	public void getFacebookContacts(View view) {
		Intent intentToListContactSplitted = new Intent(this,
				ListContactSplittedActivity.class);
		intentToListContactSplitted.putExtra(EXTRA_CONTACT_TYPE,
				ListContactSplittedActivity.FACEBOOK);
		startActivity(intentToListContactSplitted);
	}

	public void getTwitterContacts(View view) {
		Intent intentToListContactSplitted = new Intent(this,
				ListContactSplittedActivity.class);
		intentToListContactSplitted.putExtra(EXTRA_CONTACT_TYPE,
				ListContactSplittedActivity.TWITTER);
		startActivity(intentToListContactSplitted);
	}

	public void getLinkedinContacts(View view) {
		Intent intentToListContactSplitted = new Intent(this,
				ListContactSplittedActivity.class);
		// intentToListContactSplitted.putExtra(EXTRA_CONTACT_TYPE,
		// ListContactSplittedActivity.LINKEDIN);
		startActivity(intentToListContactSplitted);
	}

}

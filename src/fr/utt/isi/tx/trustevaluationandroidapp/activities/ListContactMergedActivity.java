package fr.utt.isi.tx.trustevaluationandroidapp.activities;

import fr.utt.isi.tx.trustevaluationandroidapp.MainActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.R.layout;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;

public class ListContactMergedActivity extends ActionBarActivity {
	
	// tag for log
	private static final String TAG = "ListContactMergedActivity";
	
	// progress dialog for all fragments
	public static ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "activity state: onCreate");		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_contact_merged);
		
		// setup the progress dialog
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.setMessage("Loading...");
		
		// get action bar by support library
		final ActionBar actionBar = getSupportActionBar();

		// set navigation mode to tab mode
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayShowHomeEnabled(true);
		
		MainActivity.mProgressDialog.dismiss();
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}

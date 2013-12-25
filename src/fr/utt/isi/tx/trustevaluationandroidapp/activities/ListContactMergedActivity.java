package fr.utt.isi.tx.trustevaluationandroidapp.activities;

import fr.utt.isi.tx.trustevaluationandroidapp.MainActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.SearchView;

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
		actionBar.setDisplayShowHomeEnabled(true);

		MainActivity.mProgressDialog.dismiss();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_contact_merged, menu);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(true);
        }
		
		return true;
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

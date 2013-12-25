package fr.utt.isi.tx.trustevaluationandroidapp.activities;

import fr.utt.isi.tx.trustevaluationandroidapp.MainActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

public class ListContactIndexedActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_contact_indexed);

		// get action bar by support library
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		
		MainActivity.mProgressDialog.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_contact_indexed, menu);
		return super.onCreateOptionsMenu(menu);
	}

}

package fr.utt.isi.tx.trustevaluationandroidapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";

	public static final String EXTRA_CONTACT_TYPE = "contact_type";
	
	public static ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "creating activity...");
		setContentView(R.layout.activity_main);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.setMessage("Loading...");
		
		Button button_list_1 = (Button) findViewById(R.id.button_list_1);
		button_list_1.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		mProgressDialog.show();
		switch (view.getId()) {
		case R.id.button_list_1:
			startActivity(new Intent(this, ListContactSplittedActivity.class));
			break;
		default:
			break;
		}
	}

}

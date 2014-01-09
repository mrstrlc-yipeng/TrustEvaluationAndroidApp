package fr.utt.isi.tx.trustevaluationandroidapp;

import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactIndexedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactMergedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";

	public static final String EXTRA_CONTACT_TYPE = "contact_type";

	public static ProgressDialog mProgressDialog;

	Button buttonList1;
	Button buttonList2;
	Button buttonList3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "creating activity...");
		setContentView(R.layout.activity_main);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.setMessage("Loading...");

		buttonList1 = (Button) findViewById(R.id.button_list_1);
		buttonList2 = (Button) findViewById(R.id.button_list_2);
		buttonList3 = (Button) findViewById(R.id.button_list_3);
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
		case R.id.button_list_2:
			startActivity(new Intent(this, ListContactMergedActivity.class));
			break;
		case R.id.button_list_3:
			startActivity(new Intent(this, ListContactIndexedActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// check network state
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
			// display alert when the device is not connected to network
			Toast.makeText(this,
					"Network connection failed. Please check your network.",
					Toast.LENGTH_LONG).show();
		} else {
			// enable the button to be clicked by adding listeners
			buttonList1.setOnClickListener(this);
			buttonList2.setOnClickListener(this);
			buttonList3.setOnClickListener(this);
		}
	}

}

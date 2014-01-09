package fr.utt.isi.tx.trustevaluationandroidapp.tasks;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class TweetTrustIndexTask extends
		AsyncTask<String, Void, Void> {

	//private static final String TAG = "TweetTrustIndexTask";
	
	private Context context;

	private SocialAuthAdapter adapter;
	
	private String status;

	public TweetTrustIndexTask(Context context) {
		super();
		
		this.context = context;

		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addCallBack(Provider.TWITTER, "http://www.utt.fr");
	}

	@Override
	protected Void doInBackground(String... params) {
		status = params[0];
		
		adapter.authorize(context, Provider.TWITTER);
		return null;
	}

	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			adapter.updateStatus(status, new MessageListener(), false);
		}

		@Override
		public void onError(SocialAuthError error) {
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onBack() {
		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201
					|| status.intValue() == 204)
				Toast.makeText(context, "Message posted on " + provider,
						Toast.LENGTH_LONG).show();
			else
				Toast.makeText(context, "Message not posted " + provider,
						Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

}

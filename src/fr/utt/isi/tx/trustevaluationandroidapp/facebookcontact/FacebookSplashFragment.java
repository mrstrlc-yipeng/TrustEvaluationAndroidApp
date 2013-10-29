package fr.utt.isi.tx.trustevaluationandroidapp.facebookcontact;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.R.layout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FacebookSplashFragment extends Fragment {

	private static final String TAG = "FacebookSplashFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "creating view...");
		
		View view = inflater.inflate(R.layout.fragment_facebook_splash, container, false);
		return view;
	}
}

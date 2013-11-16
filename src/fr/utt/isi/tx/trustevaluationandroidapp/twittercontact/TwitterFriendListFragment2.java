package fr.utt.isi.tx.trustevaluationandroidapp.twittercontact;

import java.util.List;

import org.brickred.customadapter.ImageLoader;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TwitterFriendListFragment2 extends Fragment implements
		OnClickListener {

	// Tag for debug
	private static final String TAG = "TwitterFriendListFragment2";

	// database helper
	private static TrustEvaluationDbHelper mDbHelper = null;

	// shared preferences
	private static SharedPreferences mSharedPreferences;
	private static final String PREF_NAME = "twitter_fragment_preferences";
	private static final String PREF_IS_FIRST_VISIT = "is_first_visit";

	// is first visit
	private boolean isFirstVisit = true;

	// adapter by socialAuth lib
	private SocialAuthAdapter adapter;

	// login button view
	private Button loginButton;

	// friend list view
	private ListView friendList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "Creating fragment...");

		// get shared preferences
		mSharedPreferences = getActivity().getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		// get boolean "is_first_visit" from shared preferences
		isFirstVisit = mSharedPreferences.getBoolean(PREF_IS_FIRST_VISIT, true);

		// create SocialAuth adapter
		adapter = new SocialAuthAdapter(new ResponseListener());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_twitter_friend_list,
				container, false);

		Log.v(TAG, "Creating view...");

		// get login button view
		loginButton = (Button) view.findViewById(R.id.login_button);
		loginButton.setOnClickListener(this);
		// get friend list view
		friendList = (ListView) view.findViewById(R.id.twitter_friend_list);

		toggleView();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// create db helper
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}

		if (!isFirstVisit) {
			// get contact list from database
			List<Contact> contactList = mDbHelper.getTwitterContacts(null);
			if (contactList != null) {
				friendList.setAdapter(new TwitterContactAdapter(getActivity(),
						R.layout.twitter_friend_list, contactList));
				return;
			}

			if (adapter == null) {
				adapter = new SocialAuthAdapter(new ResponseListener());
			}
			adapter.addCallBack(Provider.TWITTER,
					"http://txtrustevaluation.easyredmine.com");
			adapter.authorize(getActivity(), Provider.TWITTER);
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.login_button) {
			if (adapter == null) {
				adapter = new SocialAuthAdapter(new ResponseListener());
			}
			adapter.addCallBack(Provider.TWITTER,
					"http://txtrustevaluation.easyredmine.com");
			adapter.authorize(getActivity(), Provider.TWITTER);
		}
	}

	private void toggleView() {
		if (isFirstVisit) {
			loginButton.setVisibility(View.VISIBLE);
			friendList.setVisibility(View.INVISIBLE);
		} else {
			loginButton.setVisibility(View.GONE);
			friendList.setVisibility(View.VISIBLE);
		}
	}

	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// set "is_first_visit" to false
			isFirstVisit = false;
			Editor e = mSharedPreferences.edit();
			e.putBoolean(PREF_IS_FIRST_VISIT, isFirstVisit);
			e.commit();

			toggleView();

			// contact list
			adapter.getContactListAsync(new ContactDataListener());
		}

		@Override
		public void onError(SocialAuthError error) {
			Log.d(TAG, "Error");
			error.printStackTrace();
		}

		@Override
		public void onCancel() {
			Log.d(TAG, "Cancelled");
		}

		@Override
		public void onBack() {
			Log.d(TAG, "Dialog Closed by pressing Back Key");

		}
	}

	private final class ContactDataListener implements
			SocialAuthListener<List<Contact>> {

		@Override
		public void onExecute(String provider, List<Contact> t) {
			Log.d(TAG, "Receiving Data");
			List<Contact> contactsList = t;

			if (contactsList != null && contactsList.size() > 0) {
				mDbHelper.insertTwitterContact(contactsList);
				friendList.setAdapter(new TwitterContactAdapter(getActivity(),
						R.layout.twitter_friend_list, contactsList));
			} else {
				Log.d(TAG, "Contact List Empty");
			}
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	private class TwitterContactAdapter extends ArrayAdapter<Contact> {
		List<Contact> contacts;
		ImageLoader imageLoader;

		public TwitterContactAdapter(Context context, int textViewResourceId,
				List<Contact> contacts) {
			super(context, textViewResourceId);

			this.contacts = contacts;
			imageLoader = new ImageLoader(context);
		}

		@Override
		public int getCount() {
			return contacts.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v(TAG, "Creating view...");
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.twitter_friend_list, null);
			}

			Contact listElement = contacts.get(position);
			if (listElement != null) {
				// profile image
				ImageView i = (ImageView) view.findViewById(R.id.contact_image);
				imageLoader.DisplayImage(listElement.getProfileImageURL(), i);

				// profile full name
				TextView t = (TextView) view.findViewById(R.id.contact_name);
				t.setText(listElement.getFirstName());
			}
			Log.v(TAG, "element ok");

			return view;
		}

	}

}

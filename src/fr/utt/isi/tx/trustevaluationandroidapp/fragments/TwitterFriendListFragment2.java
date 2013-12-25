package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

//import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
//import org.brickred.socialauth.util.Response;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.SocialAuthContactListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.tasks.MatchingTask;
//import fr.utt.isi.tx.trustevaluationandroidapp.utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

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
	//private static final String PREF_FRIEND_LIST_JSON = "friend_list_json";

	// is first visit
	private boolean isFirstVisit = true;

	// adapter by socialAuth lib
	private SocialAuthAdapter adapter;

	// login button view
	private Button loginButton;

	// friend list view
	private ListView friendList;

	// update button view
	private Button updateButton;

	// whether the authorization (adapter.authorize(...)) is for retrieving
	// contacts or just for the assignment of adapter (used in logout flow)
	private boolean isAuthorizationForContacts = true;

	// Twitter REST API rate limit between every 2 requests (average in
	// millisecond, for example, a bucket that limit 15 requests in 15 minutes
	// means the average rate limit is 1 request per minute, this rate limit
	// equals 60000 millisecond between 2 requests)
	//private static final long API_RATE_LIMIT = 60000;

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

		// notify that the fragment has options menu
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_twitter_friend_list,
				container, false);

		// get login button view
		loginButton = (Button) view.findViewById(R.id.login_button);
		loginButton.setOnClickListener(this);

		// get friend list view
		friendList = (ListView) view.findViewById(R.id.twitter_friend_list);

		// get update button view
		updateButton = (Button) view.findViewById(R.id.update_button);
		updateButton.setOnClickListener(this);

		toggleView();

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// only add the menu when the fragment is showing
		if (this.isVisible() && !isFirstVisit) {
			if (menu.size() <= 1) {
				menu.add(R.string.logout);
			}
		} else {
			menu.clear();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getResources().getString(R.string.logout))) {
			// log out
			proceedLogout();

			// re-create options menu
			getActivity().supportInvalidateOptionsMenu();

			return true;
		}
		return false;
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
				SocialAuthContactListAdapter mAdapter = new SocialAuthContactListAdapter(
						getActivity(), R.layout.twitter_friend_list,
						contactList);
				mAdapter.setProvider(Provider.TWITTER);
				friendList.setAdapter(mAdapter);
				return;
			}

			proceed();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.login_button:
			proceed();
			break;
		case R.id.update_button:
			// update flow
			if (mDbHelper == null) {
				mDbHelper = new TrustEvaluationDbHelper(getActivity());
			}
			// clear table and re-proceed the data retrieve and data insert
			mDbHelper
					.clearTable(TrustEvaluationDataContract.TwitterContact.TABLE_NAME);
			proceed();
			break;
		default:
			break;
		}
	}

	private void toggleView() {
		if (isFirstVisit) {
			loginButton.setVisibility(View.VISIBLE);
			friendList.setVisibility(View.INVISIBLE);
			updateButton.setVisibility(View.INVISIBLE);
		} else {
			loginButton.setVisibility(View.GONE);
			friendList.setVisibility(View.VISIBLE);
			updateButton.setVisibility(View.VISIBLE);
		}
	}

	private void proceed() {
		if (adapter == null) {
			adapter = new SocialAuthAdapter(new ResponseListener());
		}
		adapter.addCallBack(Provider.TWITTER, "http://www.utt.fr");
		adapter.authorize(getActivity(), Provider.TWITTER);
	}

	private void proceedLogout() {
		// re-assign the adapter by sending authorization request without
		// retrieving contacts
		isAuthorizationForContacts = false;
		proceed();

		ListContactSplittedActivity.mProgressDialog.dismiss();

		// sign out via adapter
		adapter.signOut(getActivity(), Provider.TWITTER.toString());

		// set "is_first_visit" to true
		isFirstVisit = true;
		Editor e = mSharedPreferences.edit();
		e.putBoolean(PREF_IS_FIRST_VISIT, isFirstVisit);
		e.commit();

		toggleView();

		// clear table
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}
		mDbHelper
				.clearTable(TrustEvaluationDataContract.TwitterContact.TABLE_NAME);
	}

	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			if (isAuthorizationForContacts) {
				Log.v(TAG, "twitter fragment progress dialog shown");
				ListContactSplittedActivity.mProgressDialog.show();

				// set "is_first_visit" to false
				isFirstVisit = false;
				Editor e = mSharedPreferences.edit();
				e.putBoolean(PREF_IS_FIRST_VISIT, isFirstVisit);
				e.commit();

				toggleView();

				getActivity().supportInvalidateOptionsMenu();

				// contact list
				adapter.getContactListAsync(new ContactDataListener());
			}
			isAuthorizationForContacts = true;
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
			List<Contact> contactsList = t;

			if (contactsList != null && contactsList.size() > 0) {
				// update database
				mDbHelper.insertTwitterContact(contactsList);

				// set up the list view
				SocialAuthContactListAdapter mAdapter = new SocialAuthContactListAdapter(
						getActivity(), R.layout.twitter_friend_list,
						contactsList);
				friendList.setAdapter(mAdapter);
				
				// do matching in background
				new MatchingTask(getActivity()).execute(ListContactSplittedActivity.TWITTER);
			} else {
				Log.d(TAG, "Contact List Empty");
			}

			ListContactSplittedActivity.mProgressDialog.dismiss();

			if (contactsList != null) {
				// new TwitterCommonFriendsLoader().execute(contactsList);
			}
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}
/*
	private class TwitterCommonFriendsLoader extends
			AsyncTask<List<Contact>, Void, Map<String, String>> {

		@Override
		protected Map<String, String> doInBackground(List<Contact>... args) {
			if (adapter == null) {
				return null;
			}

			final List<Contact> contactList = args[0];

			// load my friend list json string at first
			String myFriendListJSONString = mSharedPreferences.getString(
					PREF_FRIEND_LIST_JSON, null);

			if (myFriendListJSONString == null) {
				// get my friend list string by rejoin the arraylist of contacts
				// into string
				StringBuilder mStringBuilder = new StringBuilder();
				mStringBuilder.append("{\"ids\":[");
				Iterator<Contact> i = contactList.iterator();
				while (i.hasNext()) {
					Contact contact = i.next();
					mStringBuilder.append('"');
					mStringBuilder.append(contact.getId());
					mStringBuilder.append('"');
					if (contactList.indexOf(contact) != contactList.size() - 1) {
						mStringBuilder.append(',');
					}
				}
				mStringBuilder.append("]}");
				myFriendListJSONString = mStringBuilder.toString();
				Log.v(TAG, "my friend list string: " + myFriendListJSONString);
			}

			final String myFriendListJSONStringPassedToTime = myFriendListJSONString;

			final Map<String, String> commonFriendListMap = new HashMap<String, String>();

			// generate common friend list for each contact, respecting the rate
			// limit of Twitter REST API
			final String requestMethod = "GET";
			final Timer mTimer = new Timer();
			mTimer.scheduleAtFixedRate(new TimerTask() {

				int contactListIndex = 0;

				@Override
				public void run() {
					if (contactListIndex == contactList.size()) {
						mTimer.cancel();
					}

					Contact contact = contactList.get(contactListIndex);
					String requestURL = "https://api.twitter.com/1.1/friends/ids.json?user_id="
							+ contact.getId();

					// do request via REST API
					try {
						// get response of the request
						Response contactFriendListResponse = adapter.api(
								requestURL, requestMethod, null, null, null);

						// parse response to JSON string
						String contactFriendListJSONString = contactFriendListResponse
								.getResponseBodyAsString("UTF-8");
						Log.v(TAG, "contactFriendListJSONString: "
								+ contactFriendListJSONString);

						// generate common friend list string
						String commonFriendListString = Utils
								.generateCommonFriendListStringByJSONString(
										myFriendListJSONStringPassedToTime,
										contactFriendListJSONString, "ids", ';');
						Log.v(TAG, "common friend list string: "
								+ commonFriendListString);
						commonFriendListMap.put(contact.getId(),
								commonFriendListString);
					} catch (Exception e) {
						e.printStackTrace();
					}

					contactListIndex++;
				}

			}, API_RATE_LIMIT, API_RATE_LIMIT);

			return commonFriendListMap;
		}

		@Override
		protected void onPostExecute(Map<String, String> results) {
			if (results == null || results.size() == 0) {
				return;
			}

			// update database
			if (mDbHelper == null) {
				mDbHelper = new TrustEvaluationDbHelper(getActivity());
			}
			mDbHelper.updateCommonFriendList(results,
					ListContactSplittedActivity.TWITTER);
		}
	}
*/
}

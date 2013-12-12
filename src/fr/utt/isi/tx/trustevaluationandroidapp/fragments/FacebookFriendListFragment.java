package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.GraphUserListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.PseudoFacebookGraphUserListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.PseudoFacebookGraphUser;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
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

public class FacebookFriendListFragment extends Fragment implements
		OnClickListener {

	// tag for debug
	private static final String TAG = "FacebookFriendListFragment";

	// database helper
	private static TrustEvaluationDbHelper mDbHelper = null;

	// shared preferences for storing my user id
	private static SharedPreferences mSharedPreferences;
	private static final String PREF_NAME = "facebook_fragment_preferences";
	private static final String PREF_USER_ID = "user_id";

	// user id
	private String userId = null;

	// ui lifecycle helper
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private static final int REAUTH_ACTIVITY_CODE = 100;

	// login button view
	private com.facebook.widget.LoginButton loginButtonView;

	// friend list view
	private ListView friendListView;

	// update button view
	private Button updateButtonView;

	private HashMap<String, ProfilePictureView> mProfilePictureViews = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get shared preferences
		mSharedPreferences = getActivity().getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		// get user id from shared preferences
		userId = mSharedPreferences.getString(PREF_USER_ID, null);

		// create ui lifecycle helper instance
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_facebook_friend_list,
				container, false);

		// Login button view
		loginButtonView = (com.facebook.widget.LoginButton) view
				.findViewById(R.id.login_button);
		loginButtonView.setVisibility(View.INVISIBLE);

		// Friend list view
		friendListView = (ListView) view
				.findViewById(R.id.facebook_friend_list);
		friendListView.setVisibility(View.INVISIBLE);

		// Update button view
		updateButtonView = (Button) view.findViewById(R.id.update_button);
		updateButtonView.setOnClickListener(this);
		updateButtonView.setVisibility(View.INVISIBLE);

		if (mProfilePictureViews == null) {
			mProfilePictureViews = new HashMap<String, ProfilePictureView>();
		}

		// check for an open session
		proceedBySession(Session.getActiveSession(), false);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		Session session = Session.getActiveSession();
		// only add the menu when the fragment is showing
		if (this.isVisible() && session != null && session.isOpened()) {
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
			// log out flow
			// destroy session
			Session.getActiveSession().closeAndClearTokenInformation();

			// clear table
			if (mDbHelper == null) {
				mDbHelper = new TrustEvaluationDbHelper(getActivity());
			}
			mDbHelper
					.clearTable(TrustEvaluationDataContract.FacebookContact.TABLE_NAME);

			// toggle views
			proceedBySession(Session.getActiveSession(), false);

			return true;
		}
		return false;
	}

	private void onSessionStateChange(final Session session,
			SessionState state, Exception exception) {
		Log.v(TAG, "Facebook session state changed");
		proceedBySession(session, false);
	}

	private void proceedBySession(Session session, boolean isForUpdate) {
		if (session != null && session.isOpened()) {
			// Hide login button
			// Show friend list
			// Request friend list data
			loginButtonView.setVisibility(View.GONE);
			friendListView.setVisibility(View.VISIBLE);
			updateButtonView.setVisibility(View.VISIBLE);

			// show the progress dialog
			ListContactSplittedActivity.mProgressDialog.show();

			// get my user id
			if (userId == null) {
				makeMeRequest(session);
			}

			if (mDbHelper == null) {
				mDbHelper = new TrustEvaluationDbHelper(getActivity());
			}

			// try getting friend list from database
			if (!isForUpdate) {
				List<PseudoFacebookGraphUser> contacts = mDbHelper
						.getFacebookContacts(null);
				if (contacts != null) {
					PseudoFacebookGraphUserListAdapter mPseudoFacebookGraphUserListAdapter = new PseudoFacebookGraphUserListAdapter(
							getActivity(), R.layout.facebook_friend_list,
							contacts);
					friendListView
							.setAdapter(mPseudoFacebookGraphUserListAdapter);
					getActivity().supportInvalidateOptionsMenu();

					// dismiss the progress dialog
					ListContactSplittedActivity.mProgressDialog.dismiss();

					return;
				}
			}

			// update flow
			// clear table and re-request data
			mDbHelper
					.clearTable(TrustEvaluationDataContract.FacebookContact.TABLE_NAME);

			// get friend list from remote request
			makeNewMyFriendRequest(session);
		} else {
			// Show login button
			// Hide friend list
			loginButtonView.setVisibility(View.VISIBLE);
			friendListView.setVisibility(View.INVISIBLE);
			updateButtonView.setVisibility(View.INVISIBLE);
		}

		getActivity().supportInvalidateOptionsMenu();
	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								// store the user id
								userId = user.getId();
								Editor e = mSharedPreferences.edit();
								e.putString(PREF_USER_ID, userId);
								e.commit();
							}
						}
						if (response.getError() != null) {

						}
					}
				});
		request.executeAsync();
	}

	private void makeNewMyFriendRequest(final Session session) {
		Request request = Request.newMyFriendsRequest(session,
				new Request.GraphUserListCallback() {

					@SuppressWarnings("unchecked")
					@Override
					public void onCompleted(List<GraphUser> users,
							Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (users != null) {
								// database
								if (mDbHelper == null) {
									mDbHelper = new TrustEvaluationDbHelper(
											getActivity());
								}
								mDbHelper.insertFacebookContacts(users);

								// set adapter to list view
								GraphUserListAdapter mGraphUserListAdapter = new GraphUserListAdapter(
										getActivity(),
										R.id.facebook_friend_list, users);
								friendListView
										.setAdapter(mGraphUserListAdapter);
							}

							// dismiss the progress dialog
							ListContactSplittedActivity.mProgressDialog
									.dismiss();

							new FacebookCommonFriendsLoader().execute(users);
						}
						if (response.getError() != null) {
							// TODO: handle error
						}
					}
				});
		request.executeAsync();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.update_button:
			proceedBySession(Session.getActiveSession(), true);
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private class FacebookCommonFriendsLoader extends
			AsyncTask<List<GraphUser>, Void, Map<String, String>> {

		private static final String TAG = "FacebookCommonFriendsLoader";

		@Override
		protected Map<String, String> doInBackground(List<GraphUser>... users) {
			Session activeSession = Session.getActiveSession();
			if (activeSession == null || activeSession.isClosed()) {
				return null;
			}

			// prepare for http request
			String accessToken = activeSession.getAccessToken();
			String limit = "5000";
			String offset = "0";
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet();

			Map<String, String> commonFriendLists = new HashMap<String, String>();

			Iterator<GraphUser> i = users[0].iterator();
			while (i.hasNext()) {
				// form the http query
				String id = i.next().getId();
				String query = "https://graph.facebook.com/" + userId
						+ "/mutualfriends?limit=" + limit + "&offset=" + offset
						+ "&user=" + id + "&access_token=" + accessToken;
				httpGet.setURI(URI.create(query));

				try {
					// do get the json response
					HttpResponse response = httpClient.execute(httpGet,
							localContext);
					JSONObject mJSONObject = new JSONObject(
							Utils.getASCIIContentFromEntity(response
									.getEntity()));
					JSONArray dataArray = mJSONObject.getJSONArray("data");

					// parse json
					String result = "";
					for (int j = 0; j < dataArray.length(); j++) {
						JSONObject data = dataArray.getJSONObject(j);
						result += data.getString("id");
						if (j != dataArray.length() - 1) {
							result += ";";
						}
					}
					//Log.v(TAG, "result string: " + result);

					commonFriendLists.put(id, result);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return commonFriendLists;
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
					ListContactSplittedActivity.FACEBOOK);
		}

	}

}

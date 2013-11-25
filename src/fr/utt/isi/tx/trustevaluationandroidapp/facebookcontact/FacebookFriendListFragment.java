package fr.utt.isi.tx.trustevaluationandroidapp.facebookcontact;

import java.util.HashMap;
import java.util.List;

import org.brickred.customadapter.ImageLoader;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FacebookFriendListFragment extends Fragment implements
		OnClickListener {

	// tag for debug
	private static final String TAG = "FacebookFriendListFragment";

	// database helper
	private static TrustEvaluationDbHelper mDbHelper = null;

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

	// adapters
	private GraphUserListAdapter mGraphUserListAdapter = null;
	private PseudoFacebookGraphUserListAdapter mPseudoFacebookGraphUserListAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
			// log out
			Session.getActiveSession().closeAndClearTokenInformation();

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

			if (mDbHelper == null) {
				mDbHelper = new TrustEvaluationDbHelper(getActivity());
			}

			// try getting friend list from database
			if (!isForUpdate) {
				List<PseudoFacebookGraphUser> contacts = mDbHelper
						.getFacebookContacts(null);
				if (contacts != null) {
					mPseudoFacebookGraphUserListAdapter = new PseudoFacebookGraphUserListAdapter(
							getActivity(), R.layout.facebook_friend_list,
							contacts);
					friendListView
							.setAdapter(mPseudoFacebookGraphUserListAdapter);
					getActivity().supportInvalidateOptionsMenu();
					return;
				}
			}

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

	private void makeNewMyFriendRequest(final Session session) {
		Request request = Request.newMyFriendsRequest(session,
				new Request.GraphUserListCallback() {

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
								mGraphUserListAdapter = new GraphUserListAdapter(
										getActivity(),
										R.id.facebook_friend_list, users);
								friendListView
										.setAdapter(mGraphUserListAdapter);
							}
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

	private class GraphUserListAdapter extends ArrayAdapter<GraphUser> {

		private static final String TAG = "GraphUserListAdapter";

		private List<GraphUser> listElements;

		private ImageLoader imageLoader;

		public GraphUserListAdapter(Context context, int resource,
				List<GraphUser> listElements) {
			super(context, resource, listElements);
			this.listElements = listElements;
			this.imageLoader = new ImageLoader(context);
			Log.v(TAG, "list elements ok.");
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.facebook_friend_list, null);
			}

			GraphUser listElement = listElements.get(position);
			if (listElement != null) {
				// get facebook id
				String facebookId = listElement.getId();

				// form facebook profile picture url by facebook id
				String facebookProfilePictureUrl = "http://graph.facebook.com/"
						+ facebookId + "/picture";

				// load profile picture
				ImageView profilePictureView = (ImageView) view
						.findViewById(R.id.profile_picture);
				imageLoader.DisplayImage(facebookProfilePictureUrl,
						profilePictureView);

				// set profile name
				TextView userNameView = (TextView) view
						.findViewById(R.id.friend_list_user_name);
				userNameView.setText(listElement.getName());
			}
			return view;
		}

	}

	private class PseudoFacebookGraphUserListAdapter extends
			ArrayAdapter<PseudoFacebookGraphUser> {
		private static final String TAG = "PseudoFacebookGraphUserListAdapter";

		private List<PseudoFacebookGraphUser> listElements;

		private ImageLoader imageLoader;

		public PseudoFacebookGraphUserListAdapter(Context context,
				int resource, List<PseudoFacebookGraphUser> listElements) {
			super(context, resource, listElements);
			this.listElements = listElements;
			this.imageLoader = new ImageLoader(context);
			Log.v(TAG, "list elements ok.");
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.facebook_friend_list, null);
			}

			PseudoFacebookGraphUser listElement = listElements.get(position);
			if (listElement != null) {
				// get facebook id
				String facebookId = listElement.getId();

				// form facebook profile picture url by facebook id
				String facebookProfilePictureUrl = "http://graph.facebook.com/"
						+ facebookId + "/picture";

				// load profile picture
				ImageView profilePictureView = (ImageView) view
						.findViewById(R.id.profile_picture);
				imageLoader.DisplayImage(facebookProfilePictureUrl,
						profilePictureView);

				// set profile name
				TextView userNameView = (TextView) view
						.findViewById(R.id.friend_list_user_name);
				userNameView.setText(listElement.getName());
			}
			return view;
		}
	}

}

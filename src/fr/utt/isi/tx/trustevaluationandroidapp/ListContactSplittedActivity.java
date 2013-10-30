package fr.utt.isi.tx.trustevaluationandroidapp;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//import android.support.v7.app.ActionBarActivity;

public class ListContactSplittedActivity extends FragmentActivity {

	// tag for log
	public static final String TAG = "ListContactSplittedActivity";

	// flag indicating whether the activity is visible
	private boolean isResumed = false;

	// setting menu
	private MenuItem settings;

	// key of contact type passed in bundles
	public static final String KEY_CONTACT_TYPE = "contact_type";

	// contact types
	public static final int LOCAL_PHONE = 0;
	public static final int LOCAL_EMAIL = 1;
	public static final int FACEBOOK = 2;
	public static final int TWITTER = 3;
	public static final int LINKEDIN = 4;

	// contact type
	private int contactType = FACEBOOK;

	// fragment array index
	private static final int LOCAL_PHONE_LIST_FRAGMENT = 0;
	private static final int LOCAL_EMAIL_LIST_FRAGMENT = 1;
	private static final int FACEBOOK_SPLASH_FRAGMENT = 2;
	private static final int FACEBOOK_FRIEND_LIST_FRAGMENT = 3;
	private static final int FACEBOOK_USER_SETTINGS_FRAGMENT = 4;
	// private static final int TWITTER_FOLLOW_LIST_FRAGMENT = 5;
	// private static final int LINKEDIN_CONTACT_LIST_FRAGMENT = 6;

	// number of fragments
	private static final int FRAGMENT_COUNT = 5;

	// fragment array
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	/* facebook variables and methods */
	// UiLifecycleHelper
	private UiLifecycleHelper uiHelper;

	// session state change listener
	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// only make changes if the activity is visible and the facebook contacts are currently involved
		if (isResumed && contactType == FACEBOOK) {
			Log.v(TAG, "session state changed");
			FragmentManager manager = getSupportFragmentManager();

			// get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();

			// clear back stack
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}

			// show the right fragment depending on session state
			if (state.isOpened()) {
				showFragment(FACEBOOK_FRIEND_LIST_FRAGMENT, false);
			} else if (state.isClosed()) {
				showFragment(FACEBOOK_SPLASH_FRAGMENT, false);
			}
		}
	}

	/**
	 * log in if necessary, otherwise show friend list
	 */
	private void checkFacebookLoginFlow() {
		Session session = Session.getActiveSession();

		if (session != null && session.isOpened()) {
			// if the session is already open,
			// try to show the selection fragment
			Log.v(TAG, "Facebook logged in");
			showFragment(FACEBOOK_FRIEND_LIST_FRAGMENT, false);
		} else {
			// otherwise present the splash screen
			// and ask the person to login.
			Log.v(TAG, "Facebook not logged in");
			showFragment(FACEBOOK_SPLASH_FRAGMENT, false);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "activity state: onCreate");
		super.onCreate(savedInstanceState);

		// get intent extra
		Intent intent = getIntent();
		int intentExtra = intent.getIntExtra(MainActivity.EXTRA_CONTACT_TYPE,
				LOCAL_PHONE);
		contactType = intentExtra;
		Log.v(TAG, "intent extra gotten: " + MainActivity.EXTRA_CONTACT_TYPE
				+ "=" + intentExtra);

		// create facebook ui lifecycle helper and pass the listener
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list_contact_splitted);

		// TODO: add progress bar

		// TODO: add filter bar

		// get fragments objects by fragment manager
		Log.v(TAG, "assign fragments");
		FragmentManager fm = getSupportFragmentManager();
		fragments[LOCAL_PHONE_LIST_FRAGMENT] = fm
				.findFragmentById(R.id.localPhoneListFragment);
		fragments[LOCAL_EMAIL_LIST_FRAGMENT] = fm
				.findFragmentById(R.id.localEmailListFragment);
		fragments[FACEBOOK_SPLASH_FRAGMENT] = fm
				.findFragmentById(R.id.facebookSplashFragment);
		fragments[FACEBOOK_FRIEND_LIST_FRAGMENT] = fm
				.findFragmentById(R.id.facebookFriendListFragment);
		fragments[FACEBOOK_USER_SETTINGS_FRAGMENT] = fm
				.findFragmentById(R.id.facebookUserSettingsFragment);
		// fragments[TWITTER_FOLLOW_LIST_FRAGMENT] =
		// fm.findFragmentById(R.id.facebookFriendListFragment);
		// fragments[LINKEDIN_CONTACT_LIST_FRAGMENT] =
		// fm.findFragmentById(R.id.facebookFriendListFragment);
		Log.v(TAG, "fragments assigned");

		// hide the fragments except local contact list fragment
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();

		// populate contact list
		Log.v(TAG, "populate contacts");
		populateContactList(contactType);
		Log.v(TAG, "population over");
	}

	private void populateContactList(int contactType) {
		Log.v(TAG, "contact type: " + contactType);
		switch (contactType) {

		// contacts from phone number
		case LOCAL_PHONE:
			// show fragment
			showFragment(LOCAL_PHONE_LIST_FRAGMENT, false);
			break;

		// contacts from email
		case LOCAL_EMAIL:
			// show fragment
			showFragment(LOCAL_EMAIL_LIST_FRAGMENT, false);
			break;

		// contacts from facebook
		case FACEBOOK:
			checkFacebookLoginFlow();
			break;

		// contacts from twitter
		case TWITTER:
			// TODO
			// showFragment(TWITTER_FOLLOW_LIST_FRAGMENT, false);
			break;

		// contacts from linkedin
		case LINKEDIN:
			// TODO
			// showFragment(LINKEDIN_CONTACT_LIST_FRAGMENT, false);
			break;

		}
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		Log.v(TAG, "show fragment " + fragmentIndex);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		// show the fragment by index, and hide all others
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}

		transaction.commit();
		Log.v(TAG, "fragment " + fragmentIndex + " shown");
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// only add the menu when the selection fragment is showing
		if (fragments[FACEBOOK_FRIEND_LIST_FRAGMENT].isVisible()) {
			if (menu.size() == 0) {
				settings = menu.add(R.string.settings);
			}
			return true;
		} else {
			menu.clear();
			settings = null;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(settings)) {
			showFragment(FACEBOOK_USER_SETTINGS_FRAGMENT, true);
			return true;
		}
		return false;
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();

		// if facebook concerned
		if (contactType == FACEBOOK) {
			checkFacebookLoginFlow();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
}

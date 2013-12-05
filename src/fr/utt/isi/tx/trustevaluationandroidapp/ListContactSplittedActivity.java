package fr.utt.isi.tx.trustevaluationandroidapp;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import fr.utt.isi.tx.trustevaluationandroidapp.facebookcontact.FacebookFriendListFragment;
import fr.utt.isi.tx.trustevaluationandroidapp.linkedincontact.LinkedinContactListFragment;
import fr.utt.isi.tx.trustevaluationandroidapp.localcontact.LocalEmailListFragment;
import fr.utt.isi.tx.trustevaluationandroidapp.localcontact.LocalPhoneListFragment;
import fr.utt.isi.tx.trustevaluationandroidapp.twittercontact.TwitterFriendListFragment2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;

public class ListContactSplittedActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	// tag for log
	public static final String TAG = "ListContactSplittedActivity";

	// key of contact type passed in bundles
	public static final String KEY_CONTACT_TYPE = "contact_type";

	// contact types
	public static final int LOCAL_PHONE = 0;
	public static final int LOCAL_EMAIL = 1;
	public static final int FACEBOOK = 2;
	public static final int TWITTER = 3;
	public static final int LINKEDIN = 4;

	// contact type
	public static int contactType = LOCAL_PHONE;

	// fragment array index
	private static final int LOCAL_PHONE_LIST_FRAGMENT = 0;
	private static final int LOCAL_EMAIL_LIST_FRAGMENT = 1;
	private static final int FACEBOOK_FRIEND_LIST_FRAGMENT = 2;
	private static final int TWITTER_FRIEND_LIST_FRAGMENT = 3;
	private static final int LINKEDIN_CONTACT_LIST_FRAGMENT = 4;
	private static final int FACEBOOK_USER_SETTINGS_FRAGMENT = 5;

	// number of fragments
	private static final int FRAGMENT_COUNT = FACEBOOK_USER_SETTINGS_FRAGMENT + 1;

	// fragment array
	// private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	// pager adapter
	private ContactFragmentPagerAdapter mPagerAdapter;

	// view pager
	private ViewPager mViewPager;
	
	// progress dialog for all fragments
	public static ProgressDialog mProgressDialog;

	/**
	 * Facebook UiLifecycleHelper
	 */
	private UiLifecycleHelper uiHelper;

	/**
	 * Facebook session state change listener
	 */
	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "activity state: onCreate");
		super.onCreate(savedInstanceState);

		// create facebook ui lifecycle helper and pass the listener
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list_contact_splitted);
		
		// setup the progress dialog
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.setMessage("Loading...");

		// create pager adapter
		mPagerAdapter = new ContactFragmentPagerAdapter(
				getSupportFragmentManager());

		// get action bar by support library
		final ActionBar actionBar = getSupportActionBar();

		// set navigation mode to tab mode
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayShowHomeEnabled(true);

		// get view pager
		mViewPager = (ViewPager) findViewById(R.id.pager);

		// set pager adapter to view pager
		mViewPager.setAdapter(mPagerAdapter);

		// set listener
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// add tabs to action bar
		actionBar.addTab(actionBar.newTab().setText("PhoneBook")
				.setTag(LOCAL_PHONE).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("EmailBook")
				.setTag(LOCAL_EMAIL).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Facebook")
				.setTag(FACEBOOK).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Twitter").setTag(TWITTER)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("LinkedIn")
				.setTag(LINKEDIN).setTabListener(this));
		
		MainActivity.mProgressDialog.dismiss();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		contactType = (Integer) tab.getTag();
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	private class ContactFragmentPagerAdapter extends FragmentStatePagerAdapter {

		public ContactFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int fragmentIndex) {
			Log.v("PagerAdapter", "fragment index: " + fragmentIndex);
			switch (fragmentIndex) {
			default:
				return null;
			case LOCAL_PHONE_LIST_FRAGMENT:
				return new LocalPhoneListFragment();
			case LOCAL_EMAIL_LIST_FRAGMENT:
				return new LocalEmailListFragment();
			case FACEBOOK_FRIEND_LIST_FRAGMENT:
				return new FacebookFriendListFragment();
			case TWITTER_FRIEND_LIST_FRAGMENT:
				return new TwitterFriendListFragment2();
			case LINKEDIN_CONTACT_LIST_FRAGMENT:
				return new LinkedinContactListFragment();
			}
		}

		@Override
		public int getCount() {
			return FRAGMENT_COUNT - 1;
		}

	}

}

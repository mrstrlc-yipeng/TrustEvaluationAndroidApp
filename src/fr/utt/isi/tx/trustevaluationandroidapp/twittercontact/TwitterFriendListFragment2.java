package fr.utt.isi.tx.trustevaluationandroidapp.twittercontact;

import java.util.List;

import org.brickred.customadapter.ImageLoader;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TwitterFriendListFragment2 extends Fragment {

	// Tag for debug
	private static final String TAG = "TwitterFriendListFragment2";

	// adapter by socialAuth lib
	private SocialAuthAdapter adapter;

	// views
	private ImageView profileImageView;
	private TextView profileNameView;
	private ListView friendList;
	
	private static ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "Creating fragment...");

		adapter = new SocialAuthAdapter(new ResponseListener());
		imageLoader = new ImageLoader(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_twitter_friend_list,
				container, false);

		Log.v(TAG, "Creating view...");

		profileImageView = (ImageView) view
				.findViewById(R.id.twitter_profile_pic);
		profileNameView = (TextView) view.findViewById(R.id.twitter_user_name);
		friendList = (ListView) view.findViewById(R.id.twitter_friend_list);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ListContactSplittedActivity.contactType == ListContactSplittedActivity.TWITTER) {
			adapter = adapter == null ? new SocialAuthAdapter(
					new ResponseListener()) : adapter;
			adapter.addCallBack(Provider.TWITTER,
					"http://txtrustevaluation.easyredmine.com");
			adapter.authorize(getActivity(), Provider.TWITTER);
		}
	}

	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// my user profile
			adapter.getUserProfileAsync(new ProfileDataListener());

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

	private final class ProfileDataListener implements
			SocialAuthListener<Profile> {

		@Override
		public void onExecute(String provider, Profile t) {
			// profile image
			imageLoader.DisplayImage(t.getProfileImageURL(), profileImageView);
			Log.v(TAG, "image url: " + t.getProfileImageURL());

			// profile name
			profileNameView.setText(t.getFullName());
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	private final class ContactDataListener implements
			SocialAuthListener<List<Contact>> {

		@Override
		public void onExecute(String provider, List<Contact> t) {

			Log.d(TAG, "Receiving Data");
			List<Contact> contactsList = t;

			if (contactsList != null && contactsList.size() > 0) {
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
		//ImageLoader imageLoader;

		public TwitterContactAdapter(Context context, int textViewResourceId,
				List<Contact> contacts) {
			super(context, textViewResourceId);

			this.contacts = contacts;
			//imageLoader = new ImageLoader(context);
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
				t.setText(listElement.getDisplayName());
				Log.v(TAG, "user name: " + listElement.getDisplayName());
			}
			Log.v(TAG, "element ok");

			return view;
		}

	}
}

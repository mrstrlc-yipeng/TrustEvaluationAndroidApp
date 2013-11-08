package fr.utt.isi.tx.trustevaluationandroidapp.twittercontact;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.api.FriendsFollowersResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import fr.utt.isi.tx.trustevaluationandroidapp.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterFriendListFragment extends Fragment {

	// tag for debug
	private static final String TAG = "TwitterFriendListFragment";

	// view objects
	private ImageView profileImageView;
	private TextView profileNameView;
	private ListView friendListView;

	// twitter concerned objects by twitter4j
	private static Twitter twitter;
	private static RequestToken requestToken;
	private static AccessToken accessToken;
	private static String pinCode;
	private static long userId;
	private static FriendsFollowersResources friendsFollowers;
	private static List<User> friendList = null;

	// process type for login flow
	private static final String REQUEST_TOKEN_PROCESS = "request_token";
	private static final String ACCESS_TOKEN_PROCESS = "access_token";

	// storage by shared preference
	private static SharedPreferences mSharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_twitter_friend_list,
				container, false);

		// affect shared preference object
		Log.v(TAG, "Loading preferences...");
		mSharedPreferences = getActivity().getSharedPreferences(
				Const.PREFERENCE_NAME, Context.MODE_PRIVATE);
		Log.v(TAG, "Preferences loaded");
		// affect views
		Log.v(TAG, "Loading views...");
		profileImageView = (ImageView) view
				.findViewById(R.id.twitter_profile_pic);
		profileNameView = (TextView) view.findViewById(R.id.twitter_user_name);
		friendListView = (ListView) view.findViewById(R.id.twitter_friend_list);
		Log.v(TAG, "Views loaded");

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "on resume");

		if (isConnected()) {
			doLoad();
			if (friendList != null) {
				friendListView.setAdapter(new UserListAdapter(getActivity(),
						R.layout.twitter_friend_list, friendList));
			} else {
				new ProcessFriendList().execute();
			}
		} else {
			if (requestToken != null) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						getActivity());
				alert.setTitle("PIN");
				alert.setMessage("Enter the PIN (if aviailable)");

				final EditText input = new EditText(getActivity());
				alert.setView(input);

				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								pinCode = input.getText().toString();
								new ProcessOAuth()
										.execute(ACCESS_TOKEN_PROCESS);
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								requestToken = null;
							}
						});

				alert.show();
			} else {
				new ProcessOAuth().execute(REQUEST_TOKEN_PROCESS);
			}
		}
	}

	private boolean isConnected() {
		return mSharedPreferences.getString(Const.PREF_KEY_TOKEN, null) != null;
	}

	/* TODO: add menu settings to process logout flow
	private void disconnectTwitter() {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.remove(Const.PREF_USER_ID);
		editor.remove(Const.PREF_KEY_TOKEN);
		editor.remove(Const.PREF_KEY_SECRET);
		editor.commit();

		doSave(true);
	}
	*/

	private void doSave(boolean isEmpty) {
		try {
			FileOutputStream fos = getActivity().openFileOutput(
					Const.SESSION_FILE_NAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			Log.v(TAG, "Saving access token object...");
			if (isEmpty) {
				oos.writeObject(null);
			} else {
				oos.writeObject(accessToken);
			}
			Log.v(TAG, "Access token object saved");
			oos.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(getActivity(), "Failed to open file output stream",
					Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(getActivity(), "Failed to output twitter object",
					Toast.LENGTH_LONG).show();
		}
	}

	private void doLoad() {
		try {
			FileInputStream fis = getActivity().openFileInput(
					Const.SESSION_FILE_NAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Log.v(TAG, "Loading access token object...");
			accessToken = (AccessToken) ois.readObject();
			Log.v(TAG, "Access token loaded");
			if (accessToken != null) {
				Log.v(TAG, "Recreating twitter object");
				TwitterFactory tf = new TwitterFactory();
				twitter = tf.getInstance();
				twitter.setOAuthConsumer(Const.CONSUMER_KEY,
						Const.CONSUMER_SECRET);
				twitter.setOAuthAccessToken(accessToken);
				Log.v(TAG, "Twitter ok");
			}
			ois.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(getActivity(), "Backup file not found",
					Toast.LENGTH_LONG).show();
		} catch (StreamCorruptedException e) {
			Toast.makeText(getActivity(), "Failed to open object input stream",
					Toast.LENGTH_LONG).show();
		} catch (ClassNotFoundException e) {
			Toast.makeText(getActivity(), "Failed to input twitter object",
					Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(getActivity(), "Failed to input twitter object",
					Toast.LENGTH_LONG).show();
		}
	}

	private class ProcessOAuth extends AsyncTask<String, Void, Void> {

		private String processType;

		@Override
		protected Void doInBackground(String... params) {
			processType = params[0];
			if (processType == REQUEST_TOKEN_PROCESS) {
				askRequestToken();
			} else if (processType == ACCESS_TOKEN_PROCESS) {
				askAccessToken();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void results) {
			if (processType == REQUEST_TOKEN_PROCESS) {
				postRequestToken();
			} else if (processType == ACCESS_TOKEN_PROCESS) {
				postAccessToken();
			}
		}

		protected void askRequestToken() {
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
			configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
			Configuration configuration = configurationBuilder.build();
			twitter = new TwitterFactory(configuration).getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken();
				Log.v(TAG, "Token ok: " + requestToken.getToken());
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}

		protected void postRequestToken() {
			Toast.makeText(getActivity(), "Please authorize this app!",
					Toast.LENGTH_LONG).show();
			getActivity().startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken
							.getAuthenticationURL())));
		}

		protected void askAccessToken() {
			try {
				if (pinCode.length() > 0) {
					accessToken = twitter.getOAuthAccessToken(requestToken,
							pinCode);
					Log.v(TAG, "access token ok: " + accessToken.getToken());
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
				userId = twitter.getId();
			} catch (TwitterException e) {
				if (401 == e.getStatusCode()) {
					Toast.makeText(getActivity(),
							"Unable to get the access token.",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		}

		protected void postAccessToken() {
			Editor e = mSharedPreferences.edit();
			e.putLong(Const.PREF_USER_ID, userId);
			e.putString(Const.PREF_KEY_TOKEN, accessToken.getToken());
			e.putString(Const.PREF_KEY_SECRET, accessToken.getTokenSecret());
			e.commit();
			doSave(false);
			onResume();
		}

	}

	private class ProcessFriendList extends AsyncTask<Void, Void, Void> {

		// user profile variables
		private Bitmap profileImageBitmap;
		private String profileName;

		// friend list variables
		private long cursor = -1;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// my user profile
				User myUser = twitter.showUser(twitter.getId());
				profileImageBitmap = downloadImage(myUser
						.getBiggerProfileImageURL());
				profileName = myUser.getName();

				// friends
				Log.v(TAG, "Getting friend followers object...");
				friendsFollowers = twitter.friendsFollowers();
				Log.v(TAG, "Friend followers object ok");

				Log.v(TAG, "Getting friends for user " + twitter.getId());
				while (cursor != 0) {
					PagableResponseList<User> l = friendsFollowers
							.getFriendsList(twitter.getId(), cursor);
					if (cursor == -1) {
						// first page
						friendList = l;
					} else {
						friendList.addAll(l);
					}
					cursor = l.getNextCursor();
				}
				Log.v(TAG, "Friends list ok");
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void results) {
			// set profile views
			Log.v(TAG, "Setting image...");
			profileImageView.setImageBitmap(profileImageBitmap);
			Log.v(TAG, "Image ok");
			profileNameView.setText(profileName);

			// set friend list view adapter
			Log.v(TAG, "Setting adapter...");
			friendListView.setAdapter(new UserListAdapter(getActivity(),
					R.layout.twitter_friend_list, friendList));
			Log.v(TAG, "Adapter ok");
		}

		private Bitmap downloadImage(String url) {
			Bitmap b = null;

			try {
				URL aURL = new URL(url);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				b = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
			} catch (IOException e) {

			}

			return b;
		}

	}

	private class UserListAdapter extends ArrayAdapter<User> {

		private static final String TAG = "UserListAdapter";

		private Context context;
		private List<User> userList;

		public UserListAdapter(Context context, int resourceId,
				List<User> userList) {
			super(context, resourceId, userList);

			Log.v(TAG, "Creating adapter...");
			this.context = context;
			Log.v(TAG, "context ok");
			this.userList = userList;
			Log.v(TAG, "list ok");
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v(TAG, "Creating view...");
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.twitter_friend_list, null);
			}

			User listElement = userList.get(position);
			Log.v(TAG, "Affecting element..." + listElement.getScreenName());
			if (listElement != null) {
				TextView t = (TextView) view
						.findViewById(R.id.friend_list_user_name);
				t.setText(listElement.getName());
			}
			Log.v(TAG, "element ok");

			return view;
		}
	}
}

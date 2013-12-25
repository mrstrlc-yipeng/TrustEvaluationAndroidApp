package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.util.ArrayList;
import java.util.List;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.LocalContactListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.tasks.MatchingTask;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.LocalContact;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public abstract class LocalContactListFragment extends Fragment implements
		OnClickListener {

	private static final String TAG = "LocalContactListFragment";

	// database helper
	private static TrustEvaluationDbHelper mDbHelper = null;

	// context
	private Context context;

	// contact list view
	private ListView contactListView;

	// contact update button view
	private Button updateButtonView;

	public abstract int getContactType();

	@Override
	public void onAttach(Activity context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_local_contact_list,
				container, false);

		// get contact list view object
		contactListView = (ListView) view.findViewById(R.id.local_contact_list);

		// get update button view object
		updateButtonView = (Button) view.findViewById(R.id.update_button);
		updateButtonView.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// show the progress dialog
		ListContactSplittedActivity.mProgressDialog.show();

		// execute the long running data retrieve task
		new LongRunningDataRetrieve().execute(false);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.update_button:
			// show the progress dialog
			ListContactSplittedActivity.mProgressDialog.show();

			// execute the long running data retrieve task
			new LongRunningDataRetrieve().execute(true);

			// contactListView.setAdapter(new LocalContactArrayAdapter(context,
			// R.layout.local_contact_list, getLocalContacts(
			// getContactType(), true)));
			break;
		default:
			break;
		}
	}

	protected List<LocalContact> getLocalContacts(int contactType,
			boolean isForUpdate) {

		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}

		List<LocalContact> contacts = new ArrayList<LocalContact>();

		if (!isForUpdate) {
			// try to get contact list from database directly
			contacts = (ArrayList<LocalContact>) mDbHelper.getLocalContacts(
					contactType, null);
			if (contacts != null) {
				return contacts;
			}
		}

		// update flow
		// clear the table and re-insert all data
		String tableName;
		if (contactType == ListContactSplittedActivity.LOCAL_PHONE) {
			tableName = TrustEvaluationDataContract.LocalPhoneContact.TABLE_NAME;
		} else if (contactType == ListContactSplittedActivity.LOCAL_EMAIL) {
			tableName = TrustEvaluationDataContract.LocalEmailContact.TABLE_NAME;
		} else {
			return null;
		}
		mDbHelper.clearTable(tableName);

		// get contacts from device
		contacts = (ArrayList<LocalContact>) getLocalContactsFromDevice(contactType);
		// update database
		Log.v(TAG, "updating database...");
		updateDatabase(contactType, contacts);

		return contacts;
	}

	protected List<LocalContact> getLocalContactsFromDevice(int contactType) {
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}

		List<LocalContact> contacts = new ArrayList<LocalContact>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				Uri subUri;
				String selection;
				if (contactType == ListContactSplittedActivity.LOCAL_PHONE) {
					subUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
					selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
							+ " = ?";
				} else {
					subUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
					selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID
							+ " = ?";
				}

				Cursor cursor2 = cr.query(subUri, null, selection,
						new String[] { id }, null);
				while (cursor2.moveToNext()) {
					// get the contact id in device
					String contactId = cursor2
							.getString(cursor2
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

					long contactLongId = cursor2
							.getLong(cursor2
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

					// get the contact names
					String name = cursor2
							.getString(cursor2
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

					// clean the display name for the case where the source of
					// contact is email
					if (contactType == ListContactSplittedActivity.LOCAL_EMAIL) {
						name = Utils.cleanDisplayName(name);
					}

					String lookupKey = cursor2
							.getString(cursor2
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));

					Uri contactUri = Contacts.getLookupUri(contactLongId,
							lookupKey);

					// get the contact detail info (phone number or email
					// address)
					String contactDetail;
					if (contactType == ListContactSplittedActivity.LOCAL_PHONE) {
						contactDetail = cursor2
								.getString(cursor2
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					} else {
						contactDetail = cursor2
								.getString(cursor2
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					}

					// set up contact list
					if (contactDetail != null) {
						LocalContact contact = new LocalContact(contactId,
								name, contactDetail, contactUri);
						contacts.add(contact);
					}

				}
				cursor2.close();
			}
		}
		cursor.close();

		return contacts;
	}

	public void updateDatabase() {
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}

		List<LocalContact> phoneContacts = getLocalContactsFromDevice(ListContactSplittedActivity.LOCAL_PHONE);
		updateDatabase(ListContactSplittedActivity.LOCAL_PHONE, phoneContacts);
		List<LocalContact> emailContacts = getLocalContactsFromDevice(ListContactSplittedActivity.LOCAL_EMAIL);
		updateDatabase(ListContactSplittedActivity.LOCAL_EMAIL, emailContacts);
	}

	public void updateDatabase(int contactType) {
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}

		List<LocalContact> contacts = getLocalContactsFromDevice(contactType);
		updateDatabase(contactType, contacts);
	}

	public void updateDatabase(int contactType, List<LocalContact> contacts) {
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}
		mDbHelper.insertLocalContact(contactType, contacts);
	}

	private class LongRunningDataRetrieve extends
			AsyncTask<Boolean, Void, List<LocalContact>> {

		@Override
		protected List<LocalContact> doInBackground(Boolean... args) {
			boolean isForUpdate = args[0];

			return getLocalContacts(getContactType(), isForUpdate);
		}

		@Override
		protected void onPostExecute(List<LocalContact> contactList) {
			// set the list view
			contactListView.setAdapter(new LocalContactListAdapter(context,
					R.layout.local_contact_list, contactList));

			// dismiss the progress dialog
			ListContactSplittedActivity.mProgressDialog.dismiss();
			
			// do matching in background
			new MatchingTask(getActivity()).execute(getContactType());
		}
	}

	public Context getContext() {
		return context;
	}

	public ListView getContactListView() {
		return contactListView;
	}
}

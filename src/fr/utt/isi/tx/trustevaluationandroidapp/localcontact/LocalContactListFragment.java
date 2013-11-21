package fr.utt.isi.tx.trustevaluationandroidapp.localcontact;

import java.util.ArrayList;
import java.util.List;

import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

		// set adapter
		contactListView.setAdapter(new LocalContactArrayAdapter(context,
				R.layout.local_contact_list, getLocalContacts(getContactType(),
						false)));

		// get update button view object
		updateButtonView = (Button) view.findViewById(R.id.update_button);
		updateButtonView.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.update_button:
			contactListView.setAdapter(new LocalContactArrayAdapter(context,
					R.layout.local_contact_list, getLocalContacts(
							getContactType(), true)));
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
					
					String lookupKey = cursor2
							.getString(cursor2
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
					
					Uri contactUri = Contacts.getLookupUri(contactLongId, lookupKey);
					Log.v(TAG, "uri = " + contactUri);

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

						// check at here whether this contact has already been
						// inserted into database
						contact.setInsertedInDatabase(mDbHelper
								.isContactInserted(contactType, contactId));
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

	public Context getContext() {
		return context;
	}

	public ListView getContactListView() {
		return contactListView;
	}
}

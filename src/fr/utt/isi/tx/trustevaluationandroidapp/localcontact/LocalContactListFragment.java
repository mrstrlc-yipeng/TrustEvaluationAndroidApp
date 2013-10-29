package fr.utt.isi.tx.trustevaluationandroidapp.localcontact;

import java.util.ArrayList;
import java.util.List;

import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.R.id;
import fr.utt.isi.tx.trustevaluationandroidapp.R.layout;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public abstract class LocalContactListFragment extends Fragment {

	private static final String TAG = "LocalContactListFragment";

	// context
	private Context context;

	// contact list view
	private ListView contactListView;

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
		Log.v(TAG, "creating view...");
		View view = inflater.inflate(R.layout.fragment_local_contact_list,
				container, false);

		// obtain contact list view object
		contactListView = (ListView) view.findViewById(R.id.local_contact_list);
		Log.v(TAG, "list view obtained");

		// set adapter
		contactListView
				.setAdapter(new ContactArrayAdapter(context,
						R.layout.local_contact_list,
						getLocalContacts(getContactType())));
		Log.v(TAG, "adapter set");

		return view;
	}

	protected List<ContactUser> getLocalContacts(int contactType) {
		ArrayList<ContactUser> names = new ArrayList<ContactUser>();
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
					// to get the contact names
					String name = cursor2
							.getString(cursor2
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

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
					if (contactDetail != null) {
						names.add(new ContactUser(name, contactDetail));
					}
				}
				cursor2.close();
			}
		}
		return names;
	}

	public Context getContext() {
		return context;
	}

	public ListView getContactListView() {
		return contactListView;
	}
}

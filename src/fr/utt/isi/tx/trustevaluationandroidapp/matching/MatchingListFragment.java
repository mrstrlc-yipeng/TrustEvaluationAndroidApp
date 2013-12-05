package fr.utt.isi.tx.trustevaluationandroidapp.matching;

import java.util.Iterator;
import java.util.List;

import org.brickred.socialauth.Contact;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.facebookcontact.PseudoFacebookGraphUser;
import fr.utt.isi.tx.trustevaluationandroidapp.localcontact.LocalContact;

public class MatchingListFragment extends Fragment {

	private static final String TAG = "MatchingListFragment";

	private MergedListAdapter adapter;
	private TrustEvaluationContactNode contactNode;
	private TrustEvaluationDbHelper mDbHelper;

	private ListView mergedList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "creating fragment...");

		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}
		
		if (contactNode == null)
			contactNode = new TrustEvaluationContactNode(getActivity());

		List<LocalContact> localPhoneList = mDbHelper.getLocalContacts(
				ListContactSplittedActivity.LOCAL_PHONE, null);
		List<LocalContact> localEmailList = mDbHelper.getLocalContacts(
				ListContactSplittedActivity.LOCAL_EMAIL, null);
		List<PseudoFacebookGraphUser> facebookList = mDbHelper
				.getFacebookContacts(null);
		List<Contact> twitterList = mDbHelper.getTwitterContacts(null);
		List<Contact> linkedinList = mDbHelper.getLinkedinContacts(null);
		merge(localPhoneList, localEmailList, facebookList, twitterList,
				linkedinList);
		
		List<MergedContact> mergedContacts = contactNode.getMergedContacts(null);
		updateSourceScore(mergedContacts);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (contactNode == null)
			contactNode = new TrustEvaluationContactNode(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_matching_list,
				container, false);

		Log.v(TAG, "Creating view...");

		mergedList = (ListView) view.findViewById(R.id.matching_list);

		List<MergedContact> contacts = contactNode.getMergedContacts(null);
		adapter = new MergedListAdapter(getActivity(), R.id.matching_list,
				contacts);

		mergedList.setAdapter(adapter);

		return view;
	}

	public void merge(List<LocalContact> localPhoneList,
			List<LocalContact> localEmailList,
			List<PseudoFacebookGraphUser> facebookList,
			List<Contact> twitterList, List<Contact> linkedinList) {
		Log.v(TAG, "merged lists...");

		if (contactNode == null)
			contactNode = new TrustEvaluationContactNode(getActivity());

		// Merge local phone contact list
		if (localPhoneList != null && localPhoneList.size() > 0) {
			Iterator<LocalContact> i1 = localPhoneList.iterator();
			while (i1.hasNext()) {
				Log.v(TAG, "contact");
				LocalContact contact = i1.next();

				Log.v(TAG, "contactNode");
				if (!contactNode
						.isInsertedContactNode(contact.getDisplayName())) {
					contactNode.insertContactNode(contactNode
							.generateMergedContact(
									ListContactSplittedActivity.LOCAL_PHONE,
									contact.getDisplayName()));
				}
			}
		}

		// Merge local email contact list
		if (localEmailList != null && localEmailList.size() > 0) {
			Iterator<LocalContact> i2 = localEmailList.iterator();
			while (i2.hasNext()) {
				LocalContact contact = i2.next();
				matching(ListContactSplittedActivity.LOCAL_EMAIL,
						contact.getDisplayName());
			}
		}
		// Merge facebook contact list
		/*if (facebookList != null && facebookList.size() > 0) {
			Iterator<PseudoFacebookGraphUser> i3 = facebookList.iterator();
			while (i3.hasNext()) {
				PseudoFacebookGraphUser contact = i3.next();
				matching(ListContactSplittedActivity.FACEBOOK,
						contact.getName());
			}
		}*/

		// Merge twitter contact list
		if (twitterList != null && twitterList.size() > 0) {
			Iterator<Contact> i4 = twitterList.iterator();
			while (i4.hasNext()) {
				Contact contact = i4.next();
				matching(ListContactSplittedActivity.TWITTER,
						contact.getFirstName());
			}
		}
		// Merge linkedin contact list
		if (linkedinList != null && linkedinList.size() > 0) {
			Iterator<Contact> i5 = linkedinList.iterator();
			while (i5.hasNext()) {
				Contact contact = i5.next();
				matching(ListContactSplittedActivity.LINKEDIN,
						contact.getFirstName() + contact.getLastName());
			}
		}

	}

	// Matching
	// Update if matched; Insert if not matched
	public void matching(int contactType, String contactName) {
		Log.v(TAG, "matching names...");

		boolean isMerged = false;
		List<MergedContact> mergedContactList = contactNode
				.getMergedContacts(null);
		Iterator<MergedContact> j = mergedContactList.iterator();

		while (j.hasNext()) {
			MergedContact mergedContact = j.next();
			if (MatchingAlgo.isOnePerson(contactName,
					mergedContact.getDisplayNameGlobal())) {
				contactNode.updateContactNode(contactType, mergedContact);
				isMerged = true;
				break;
			}
		}

		if (!isMerged) {
			if (!contactNode.isInsertedContactNode(contactName))
				contactNode.insertContactNode(contactNode
						.generateMergedContact(contactType, contactName));
		}
	}

	public void updateSourceScore(List<MergedContact> contacts) {
		Log.v(TAG, "updating source score..");

		Iterator<MergedContact> i = contacts.iterator();
		while (i.hasNext()) {
			MergedContact contact = i.next();
			contactNode.calculateSourceScore(contact);
		}
	}

	private class MergedListAdapter extends ArrayAdapter<MergedContact> {
		List<MergedContact> contacts;

		public MergedListAdapter(Context context, int textViewResourceId,
				List<MergedContact> contacts) {
			super(context, textViewResourceId, contacts);

			this.contacts = contacts;
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
				view = inflater.inflate(R.layout.matching_list, null);
			}

			MergedContact listElement = contacts.get(position);
			if (listElement != null) {
				TextView name = (TextView) view
						.findViewById(R.id.contact_name_global);
				name.setText(listElement.getDisplayNameGlobal());

				TextView score = (TextView) view
						.findViewById(R.id.source_score);
				score.setText("" + listElement.getSourceScore());
			}
			Log.v(TAG, "element ok");

			return view;
		}

	}

}

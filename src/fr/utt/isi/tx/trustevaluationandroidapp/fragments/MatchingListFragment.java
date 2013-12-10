package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.util.Iterator;
import java.util.List;

import org.brickred.socialauth.Contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.ContactNodeListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.computingalgo.MatchingAlgo;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.LocalContact;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.MergedContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.PseudoFacebookGraphUser;

public class MatchingListFragment extends Fragment {

	private static final String TAG = "MatchingListFragment";

	private ContactNodeListAdapter adapter;
	private TrustEvaluationContactNode contactNodeHelper;
	private TrustEvaluationDbHelper mDbHelper;

	private ListView mergedList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "creating fragment...");

		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}

		if (contactNodeHelper == null)
			contactNodeHelper = new TrustEvaluationContactNode(getActivity());

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

		List<MergedContactNode> mergedContacts = contactNodeHelper
				.getMergedContacts(null);
		updateSourceScore(mergedContacts);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (contactNodeHelper == null)
			contactNodeHelper = new TrustEvaluationContactNode(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_matching_list,
				container, false);

		Log.v(TAG, "Creating view...");

		mergedList = (ListView) view.findViewById(R.id.matching_list);

		List<MergedContactNode> contacts = contactNodeHelper
				.getMergedContacts(null);
		adapter = new ContactNodeListAdapter(getActivity(), R.id.matching_list,
				contacts);

		mergedList.setAdapter(adapter);

		return view;
	}
	
	public void merge(List<LocalContact> localPhoneList,
			List<LocalContact> localEmailList,
			List<PseudoFacebookGraphUser> facebookList,
			List<Contact> twitterList, List<Contact> linkedinList) {
		Log.v(TAG, "merged lists...");

		if (contactNodeHelper == null)
			contactNodeHelper = new TrustEvaluationContactNode(getActivity());

		// Merge local phone contact list
		if (localPhoneList != null && localPhoneList.size() > 0) {
			Iterator<LocalContact> i1 = localPhoneList.iterator();
			while (i1.hasNext()) {
				Log.v(TAG, "contact");
				LocalContact contact = i1.next();

				Log.v(TAG, "contactNode");
				if (!contactNodeHelper.isInsertedContactNode(contact
						.getDisplayName())) {
					contactNodeHelper.insertContactNode(contactNodeHelper
							.generateMergedContact(
									ListContactSplittedActivity.LOCAL_PHONE,
									contact));
				}
			}
		}

		// Merge local email contact list
		if (localEmailList != null && localEmailList.size() > 0) {
			Iterator<LocalContact> i2 = localEmailList.iterator();
			while (i2.hasNext()) {
				LocalContact contact = i2.next();
				matching(ListContactSplittedActivity.LOCAL_EMAIL,
						contact);
			}
		}

		// Merge facebook contact list
		if (facebookList != null && facebookList.size() > 0) {
			Iterator<PseudoFacebookGraphUser> i3 = facebookList.iterator();
			while (i3.hasNext()) {
				PseudoFacebookGraphUser contact = i3.next();
				matching(ListContactSplittedActivity.FACEBOOK,
						contact);
			}
		}

		// Merge twitter contact list
		if (twitterList != null && twitterList.size() > 0) {
			Iterator<Contact> i4 = twitterList.iterator();
			while (i4.hasNext()) {
				Contact contact = i4.next();
				matching(ListContactSplittedActivity.TWITTER,
						contact);
			}
		}
		
		// Merge linkedin contact list
		if (linkedinList != null && linkedinList.size() > 0) {
			Iterator<Contact> i5 = linkedinList.iterator();
			while (i5.hasNext()) {
				Contact contact = i5.next();
				matching(ListContactSplittedActivity.LINKEDIN,
						contact);
			}
		}

	}

	// Matching
	// Update if matched; Insert if not matched
	public void matching(int contactType, Contact contact) {
		Log.v(TAG, "matching names...");

		// fetch the name
		String name;
		switch (contactType) {
		case ListContactSplittedActivity.LOCAL_PHONE:
			name = contact.getDisplayName();
			break;
		case ListContactSplittedActivity.LOCAL_EMAIL:
			name = contact.getDisplayName();
			break;
		case ListContactSplittedActivity.FACEBOOK:
			name = contact.getDisplayName();
			break;
		case ListContactSplittedActivity.TWITTER:
			name = contact.getFirstName();
			break;
		case ListContactSplittedActivity.LINKEDIN:
			name = contact.getFirstName() + " " + contact.getLastName();
			break;
		default:
			return;
		}

		// flag, whether to update or to insert
		boolean isMerged = false;
		
		// get the existing contact node list in database
		List<MergedContactNode> mergedContactList = contactNodeHelper
				.getMergedContacts(null);
		
		Iterator<MergedContactNode> j = mergedContactList.iterator();
		while (j.hasNext()) {
			MergedContactNode mergedContact = j.next();
			
			// do matching
			if (MatchingAlgo.isOnePerson(name,
					mergedContact.getDisplayNameGlobal())) {
				
				// set corresponding identification field(s)
				switch (contactType) {
				case ListContactSplittedActivity.LOCAL_PHONE:
					mergedContact.setIsLocalPhone(1);
					break;
				case ListContactSplittedActivity.LOCAL_EMAIL:
					mergedContact.setIsLocalEmail(1);
					break;
				case ListContactSplittedActivity.FACEBOOK:
					mergedContact.setIsFacebook(1);
					mergedContact.setFacebookId(contact.getId());
					break;
				case ListContactSplittedActivity.TWITTER:
					mergedContact.setIsTwitter(1);
					break;
				case ListContactSplittedActivity.LINKEDIN:
					mergedContact.setIsLinkedin(1);
					break;
				default:
					return;
				}
				
				// update contact node
				contactNodeHelper.updateContactNode(contactType, mergedContact);
				
				// set flag to true
				isMerged = true;
				break;
			}
		}

		if (!isMerged) {
			if (!contactNodeHelper.isInsertedContactNode(name))
				contactNodeHelper.insertContactNode(contactNodeHelper
						.generateMergedContact(contactType, contact));
		}
	}

	public void updateSourceScore(List<MergedContactNode> contacts) {
		Log.v(TAG, "updating source score..");

		Iterator<MergedContactNode> i = contacts.iterator();
		while (i.hasNext()) {
			MergedContactNode contact = i.next();
			contactNodeHelper.calculateSourceScore(contact);
		}
	}

}

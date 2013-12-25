package fr.utt.isi.tx.trustevaluationandroidapp.tasks;

import java.util.Iterator;
import java.util.List;

import org.brickred.socialauth.Contact;

import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.computingalgo.MatchingAlgo;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.LocalContact;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.MergedContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.PseudoFacebookGraphUser;
import android.content.Context;
import android.os.AsyncTask;

public class MatchingTask extends AsyncTask<Integer, Void, Void> {

	private Context context;

	private TrustEvaluationDbHelper mDbHelper;
	private TrustEvaluationContactNode contactNodeHelper;

	public MatchingTask(Context context) {
		super();

		this.context = context;
		mDbHelper = new TrustEvaluationDbHelper(this.context);
		contactNodeHelper = new TrustEvaluationContactNode(context);
	}

	@Override
	protected Void doInBackground(Integer... contactTypes) {
		
		// do matching and merge by contact type
		for (int i = 0; i < contactTypes.length; i++) {
			switch (contactTypes[i]) {
			case ListContactSplittedActivity.LOCAL_PHONE:
				mergeLocalPhoneList(mDbHelper.getLocalContacts(
						ListContactSplittedActivity.LOCAL_PHONE, null));
				break;
			case ListContactSplittedActivity.LOCAL_EMAIL:
				mergeLocalEmailList(mDbHelper.getLocalContacts(
						ListContactSplittedActivity.LOCAL_EMAIL, null));
				break;
			case ListContactSplittedActivity.FACEBOOK:
				mergeFacebookList(mDbHelper.getFacebookContacts(null));
				break;
			case ListContactSplittedActivity.TWITTER:
				mergeTwitterList(mDbHelper.getTwitterContacts(null));
				break;
			case ListContactSplittedActivity.LINKEDIN:
				mergeLinkedinList(mDbHelper.getLinkedinContacts(null));
				break;
			default:
				return null;
			}
		}
		
		// update source score
		List<MergedContactNode> mergedContacts = contactNodeHelper.getMergedContacts(null);
		for (int i = 0; i < mergedContacts.size(); i++) {
			contactNodeHelper.calculateSourceScore(mergedContacts.get(i));
		}
		
		return null;
	}

	public void mergeLocalPhoneList(List<LocalContact> localPhoneList) {
		if (localPhoneList != null && localPhoneList.size() > 0) {
			Iterator<LocalContact> i1 = localPhoneList.iterator();
			while (i1.hasNext()) {
				LocalContact contact = i1.next();
				matching(ListContactSplittedActivity.LOCAL_PHONE, contact);
			}
		}
	}

	public void mergeLocalEmailList(List<LocalContact> localEmailList) {
		if (localEmailList != null && localEmailList.size() > 0) {
			Iterator<LocalContact> i2 = localEmailList.iterator();
			while (i2.hasNext()) {
				LocalContact contact = i2.next();
				matching(ListContactSplittedActivity.LOCAL_EMAIL, contact);
			}
		}
	}

	public void mergeFacebookList(List<PseudoFacebookGraphUser> facebookList) {
		if (facebookList != null && facebookList.size() > 0) {
			Iterator<PseudoFacebookGraphUser> i3 = facebookList.iterator();
			while (i3.hasNext()) {
				PseudoFacebookGraphUser contact = i3.next();
				matching(ListContactSplittedActivity.FACEBOOK, contact);
			}
		}
	}

	public void mergeTwitterList(List<Contact> twitterList) {
		if (twitterList != null && twitterList.size() > 0) {
			Iterator<Contact> i4 = twitterList.iterator();
			while (i4.hasNext()) {
				Contact contact = i4.next();
				matching(ListContactSplittedActivity.TWITTER, contact);
			}
		}
	}

	public void mergeLinkedinList(List<Contact> linkedinList) {
		if (linkedinList != null && linkedinList.size() > 0) {
			Iterator<Contact> i5 = linkedinList.iterator();
			while (i5.hasNext()) {
				Contact contact = i5.next();
				matching(ListContactSplittedActivity.LINKEDIN, contact);
			}
		}
	}

	// Matching
	// Update if matched; Insert if not matched
	public void matching(int contactType, Contact contact) {
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

		if (mergedContactList != null) {
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
					contactNodeHelper.updateContactNode(contactType,
							mergedContact);

					// set flag to true
					isMerged = true;
					break;
				}
			}
		}

		if (!isMerged) {
			if (!contactNodeHelper.isInsertedContactNode(name))
				contactNodeHelper.insertContactNode(contactNodeHelper
						.generateMergedContact(contactType, contact));
		}
	}

}

package fr.utt.isi.tx.trustevaluationandroidapp.localcontact;

import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;

public class LocalPhoneListFragment extends LocalContactListFragment {

	@Override
	public int getContactType() {
		return ListContactSplittedActivity.LOCAL_PHONE;
	}
}

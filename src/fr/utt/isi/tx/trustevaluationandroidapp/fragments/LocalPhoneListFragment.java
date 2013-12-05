package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;

public class LocalPhoneListFragment extends LocalContactListFragment {

	@Override
	public int getContactType() {
		return ListContactSplittedActivity.LOCAL_PHONE;
	}
}

package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;

public class LocalEmailListFragment extends LocalContactListFragment {

	@Override
	public int getContactType() {
		return ListContactSplittedActivity.LOCAL_EMAIL;
	}

}

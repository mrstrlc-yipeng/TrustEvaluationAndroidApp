package fr.utt.isi.tx.trustevaluationandroidapp.localcontact;

import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;

public class LocalEmailListFragment extends LocalContactListFragment {

	@Override
	public int getContactType() {
		return ListContactSplittedActivity.LOCAL_EMAIL;
	}

}

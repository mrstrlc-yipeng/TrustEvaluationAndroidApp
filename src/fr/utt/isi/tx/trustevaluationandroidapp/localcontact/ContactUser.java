package fr.utt.isi.tx.trustevaluationandroidapp.localcontact;

import android.widget.BaseAdapter;

public class ContactUser {
	private String displayName;

	private String contactDetail;

	private BaseAdapter adapter;

	public ContactUser(String displayName, String contactDetail) {
		super();
		this.displayName = displayName;
		this.contactDetail = contactDetail;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	public String getContactDetail() {
		return contactDetail;
	}

	public void setContactDetail(String contactDetail) {
		this.contactDetail = contactDetail;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}
}

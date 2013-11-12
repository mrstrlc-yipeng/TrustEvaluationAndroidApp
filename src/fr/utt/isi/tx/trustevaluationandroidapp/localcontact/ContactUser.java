package fr.utt.isi.tx.trustevaluationandroidapp.localcontact;

import android.widget.BaseAdapter;

public class ContactUser {
	private String contactId;
	
	private String displayName;

	private String contactDetail;
	
	private boolean isInsertedInDatabase;

	private BaseAdapter adapter;

	public ContactUser(String contactId, String displayName, String contactDetail) {
		super();
		this.contactId = contactId;
		this.displayName = displayName;
		this.contactDetail = contactDetail;
	}
	
	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
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
	
	public boolean isInsertedInDatabase() {
		return isInsertedInDatabase;
	}

	public void setInsertedInDatabase(boolean isInsertedInDatabase) {
		this.isInsertedInDatabase = isInsertedInDatabase;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

}

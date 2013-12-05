package fr.utt.isi.tx.trustevaluationandroidapp.utils;

import org.brickred.socialauth.Contact;

import android.net.Uri;
import android.widget.BaseAdapter;

public class LocalContact extends Contact {

	private static final long serialVersionUID = -811731420096266690L;

	private String contactId;

	private String displayName;

	private String contactDetail;

	private Uri contactUri = null;

	private BaseAdapter adapter;

	public LocalContact(String contactId, String displayName,
			String contactDetail, Uri contactUri) {
		super();
		this.contactId = contactId;
		this.displayName = displayName;
		this.contactDetail = contactDetail;
		this.contactUri = contactUri;
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

	public Uri getContactUri() {
		return contactUri;
	}

	public void setContactUri(Uri contactUri) {
		this.contactUri = contactUri;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

}

package fr.utt.isi.tx.trustevaluationandroidapp.utils;

import org.brickred.socialauth.Contact;

public class PseudoFacebookGraphUser extends Contact {

	private static final long serialVersionUID = -312163410973483197L;

	private String id;

	private String displayName;
	
	private String profileImageURL;

	public PseudoFacebookGraphUser(String id, String displayName, String profileImageURL) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.profileImageURL = profileImageURL;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getProfileImageURL() {
		return profileImageURL;
	}

	@Override
	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}

}

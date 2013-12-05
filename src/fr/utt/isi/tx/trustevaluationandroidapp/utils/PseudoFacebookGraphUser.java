package fr.utt.isi.tx.trustevaluationandroidapp.utils;

import org.brickred.socialauth.Contact;

public class PseudoFacebookGraphUser extends Contact {

	private static final long serialVersionUID = -312163410973483197L;

	private String id;

	private String name;
	
	private String profileImageUrl;

	public PseudoFacebookGraphUser(String id, String name, String profileImageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.profileImageUrl = profileImageUrl;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

}
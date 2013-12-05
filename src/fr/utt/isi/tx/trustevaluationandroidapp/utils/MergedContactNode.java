package fr.utt.isi.tx.trustevaluationandroidapp.utils;

import org.brickred.socialauth.Contact;

public class MergedContactNode extends Contact {

	private static final long serialVersionUID = -2565996209687173183L;

	private String id;
	private String displayNameGlobal;
	private int sourceScore;
	private int trustScore;
	private int isLocalPhone;
	private int isLocalEmail;
	private int isFacebook;
	private int isTwitter;
	private int isLinkedin;

	public MergedContactNode() {

	}

	public MergedContactNode(String displayNameGlobal, int sourceScore,
			int truseScore, int isLocalPhone, int isLocalEmail, int isFacebook,
			int isTwitter, int isLinkedin) {
		super();
		this.displayNameGlobal = displayNameGlobal;
		this.sourceScore = sourceScore;
		this.trustScore = truseScore;
		this.isLocalPhone = isLocalPhone;
		this.isLocalEmail = isLocalEmail;
		this.isFacebook = isFacebook;
		this.isTwitter = isTwitter;
		this.isLinkedin = isLinkedin;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayNameGlobal() {
		return displayNameGlobal;
	}

	public void setDisplayNameGlobal(String displayNameGlobal) {
		this.displayNameGlobal = displayNameGlobal;
	}

	public int getSourceScore() {
		return sourceScore;
	}

	public void setSourceScore(int sourceScore) {
		this.sourceScore = sourceScore;
	}

	public int getTrustScore() {
		return trustScore;
	}

	public void setTrustScore(int trustScore) {
		this.trustScore = trustScore;
	}

	public int getIsLocalPhone() {
		return isLocalPhone;
	}

	public void setIsLocalPhone(int isLocalPhone) {
		this.isLocalPhone = isLocalPhone;
	}

	public int getIsLocalEmail() {
		return isLocalEmail;
	}

	public void setIsLocalEmail(int isLocalEmail) {
		this.isLocalEmail = isLocalEmail;
	}

	public int getIsFacebook() {
		return isFacebook;
	}

	public void setIsFacebook(int isFacebook) {
		this.isFacebook = isFacebook;
	}

	public int getIsTwitter() {
		return isTwitter;
	}

	public void setIsTwitter(int isTwitter) {
		this.isTwitter = isTwitter;
	}

	public int getIsLinkedin() {
		return isLinkedin;
	}

	public void setIsLinkedin(int isLinkedin) {
		this.isLinkedin = isLinkedin;
	}

}

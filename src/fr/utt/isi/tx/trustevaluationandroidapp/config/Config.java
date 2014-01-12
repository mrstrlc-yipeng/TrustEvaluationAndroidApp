package fr.utt.isi.tx.trustevaluationandroidapp.config;

public class Config {
	
	// database
	public static final String FTS_VERSION = "fts3";
	
	// shared preferences
	public static final String PREF_NAME_FACEBOOK = "facebook_fragment_preferences";
	public static final String PREF_USER_ID_FACEBOOK = "user_id_facebook";
	public static final String PREF_NAME_TWITTER = "twitter_fragment_preferences";
	public static final String PREF_IS_FIRST_VISIT_TWITTER = "is_first_visit_twitter";
	public static final String PREF_NAME_LINKEDIN = "linkedin_fragment_preferences";
	public static final String PREF_IS_FIRST_VISIT_LINKEDIN = "is_first_visit_linkedin";
	
	// export configs
	public static final String EXPORT_FILE_NAME = "trust_evaluation_db_export.csv";
	
	// mailing configs
	public static final String EMAIL_DESTINATION = "charles.perez@utt.fr";
	public static final String EMAIL_SUJECT = "trust evaluation db export";
	public static final String EMAIL_BODY = "";
	
	private Config() {
		
	}

}

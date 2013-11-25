package fr.utt.isi.tx.trustevaluationandroidapp.database;

import android.provider.BaseColumns;

public final class TrustEvaluationDataContract {
	private static final String COMMA_SEP = ",";

	public TrustEvaluationDataContract() {
	}

	public static abstract class ContactNode implements BaseColumns {
		public static final String TABLE_NAME = "contact_node";
		public static final String COLUMN_NAME_DISPLAY_NAME_GLOBAL = "display_name_global";
		public static final String COLUMN_NAME_SOURCE_SCORE = "source_score";
		public static final String COLUMN_NAME_TRUST_SCORE = "trust_score";
		public static final String COLUMN_NAME_IS_LOCAL_PHONE = "is_local_phone";
		public static final String COLUMN_NAME_IS_LOCAL_EMAIL = "is_local_email";
		public static final String COLUMN_NAME_IS_FACEBOOK = "is_facebook";
		public static final String COLUMN_NAME_IS_TWITTER = "is_twitter";
		public static final String COLUMN_NAME_IS_LINKEDIN = "is_linkedin";

		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY"
				+ COMMA_SEP
				+ COLUMN_NAME_DISPLAY_NAME_GLOBAL
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_SOURCE_SCORE
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_TRUST_SCORE
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_LOCAL_PHONE
				+ " TINYINT"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_LOCAL_EMAIL
				+ " TINYINT"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_FACEBOOK
				+ " TINYINT"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_TWITTER
				+ " TINYINT"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_LINKEDIN + " TINYINT" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public static abstract class LocalPhoneContact implements BaseColumns {
		public static final String TABLE_NAME = "local_phone_contact";
		public static final String COLUMN_NAME_LOCAL_ID = "local_id";
		public static final String COLUMN_NAME_LOCAL_NAME = "local_name";
		public static final String COLUMN_NAME_LOCAL_NUMBER = "local_number";
		public static final String COLUMN_NAME_LOCAL_URI = "local_uri";
		public static final String COLUMN_NAME_CONTACT_NODE_ID = "contact_node_id";
		public static final String COLUMN_NAME_IS_MERGED = "is_merged";

		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_ID
				+ " INTERGER"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_NAME
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_NUMBER
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_URI
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_CONTACT_NODE_ID
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_MERGED + " TINYINT" + " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public static abstract class LocalEmailContact implements BaseColumns {
		public static final String TABLE_NAME = "local_email_contact";
		public static final String COLUMN_NAME_LOCAL_ID = "local_id";
		public static final String COLUMN_NAME_LOCAL_NAME = "local_name";
		public static final String COLUMN_NAME_LOCAL_EMAIL = "local_email";
		public static final String COLUMN_NAME_LOCAL_URI = "local_uri";
		public static final String COLUMN_NAME_CONTACT_NODE_ID = "contact_node_id";
		public static final String COLUMN_NAME_IS_MERGED = "is_merged";
		
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_ID
				+ " INTERGER"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_NAME
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_EMAIL
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_LOCAL_URI
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_CONTACT_NODE_ID
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_MERGED + " TINYINT" + " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public static abstract class FacebookContact implements BaseColumns {
		public static final String TABLE_NAME = "facebook_contact";
		public static final String COLUMN_NAME_FACEBOOK_ID = "facebook_id";
		public static final String COLUMN_NAME_FACEBOOK_NAME = "facebook_name";
		public static final String COLUMN_NAME_FACEBOOK_PROFILE_IMAGE_URL = "facebook_profile_image_url";
		public static final String COLUMN_NAME_CONTACT_NODE_ID = "contact_node_id";
		public static final String COLUMN_NAME_IS_MERGED = "is_merged";
		
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY"
				+ COMMA_SEP
				+ COLUMN_NAME_FACEBOOK_ID
				+ " INTERGER"
				+ COMMA_SEP
				+ COLUMN_NAME_FACEBOOK_NAME
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_FACEBOOK_PROFILE_IMAGE_URL
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_CONTACT_NODE_ID
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_MERGED + " TINYINT" + " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public static abstract class TwitterContact implements BaseColumns {
		public static final String TABLE_NAME = "twitter_contact";
		public static final String COLUMN_NAME_TWITTER_ID = "twitter_id";
		public static final String COLUMN_NAME_TWITTER_NAME = "twitter_name";
		public static final String COLUMN_NAME_TWITTER_USERNAME = "twitter_username";
		public static final String COLUMN_NAME_TWITTER_PROFILE_IMAGE_URL = "twitter_profile_image_url";
		public static final String COLUMN_NAME_CONTACT_NODE_ID = "contact_node_id";
		public static final String COLUMN_NAME_IS_MERGED = "is_merged";
		
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY"
				+ COMMA_SEP
				+ COLUMN_NAME_TWITTER_ID
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_TWITTER_NAME
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_TWITTER_USERNAME
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_TWITTER_PROFILE_IMAGE_URL
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_CONTACT_NODE_ID
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_MERGED + " TINYINT" + " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public static abstract class LinkedinContact implements BaseColumns {
		public static final String TABLE_NAME = "linkedin_contact";
		public static final String COLUMN_NAME_LINKEDIN_ID = "linkedin_id";
		public static final String COLUMN_NAME_LINKEDIN_FIRST_NAME = "linkedin_first_name";
		public static final String COLUMN_NAME_LINKEDIN_LAST_NAME = "linkedin_last_name";
		public static final String COLUMN_NAME_LINKEDIN_PROFILE_IMAGE_URL = "linkedin_profile_image_url";
		public static final String COLUMN_NAME_CONTACT_NODE_ID = "contact_node_id";
		public static final String COLUMN_NAME_IS_MERGED = "is_merged";
		
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY"
				+ COMMA_SEP
				+ COLUMN_NAME_LINKEDIN_ID
				+ " INTERGER"
				+ COMMA_SEP
				+ COLUMN_NAME_LINKEDIN_FIRST_NAME
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_LINKEDIN_LAST_NAME
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_LINKEDIN_PROFILE_IMAGE_URL
				+ " TEXT"
				+ COMMA_SEP
				+ COLUMN_NAME_CONTACT_NODE_ID
				+ " INTEGER"
				+ COMMA_SEP
				+ COLUMN_NAME_IS_MERGED + " TINYINT" + " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}
}

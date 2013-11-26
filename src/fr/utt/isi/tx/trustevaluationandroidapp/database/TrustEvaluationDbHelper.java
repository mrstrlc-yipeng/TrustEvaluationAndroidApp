package fr.utt.isi.tx.trustevaluationandroidapp.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brickred.socialauth.Contact;

import com.facebook.model.GraphUser;

import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.facebookcontact.PseudoFacebookGraphUser;
import fr.utt.isi.tx.trustevaluationandroidapp.localcontact.LocalContact;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.util.Log;

public class TrustEvaluationDbHelper extends SQLiteOpenHelper {

	private static final String TAG = "TrustEvaluationDbHelper";

	// increase database version when database schema changes
	public static final int DATABASE_VERSION = 22;
	public static final String DATABASE_NAME = "TrustEvaluation.db";

	private SQLiteDatabase readable = null;
	private SQLiteDatabase writable = null;

	public TrustEvaluationDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TrustEvaluationDataContract.ContactNode.SQL_CREATE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.LocalPhoneContact.SQL_CREATE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.LocalEmailContact.SQL_CREATE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.FacebookContact.SQL_CREATE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.TwitterContact.SQL_CREATE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.LinkedinContact.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropDatabase(db);
		onCreate(db);
	}

	public void dropDatabase(SQLiteDatabase db) {
		db.execSQL(TrustEvaluationDataContract.ContactNode.SQL_DELETE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.LocalPhoneContact.SQL_DELETE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.LocalEmailContact.SQL_DELETE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.FacebookContact.SQL_DELETE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.TwitterContact.SQL_DELETE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.LinkedinContact.SQL_DELETE_ENTRIES);
	}

	public boolean isContactInserted(int contactType, String contactId) {
		if (readable == null) {
			readable = this.getReadableDatabase();
		}

		String tableName;
		String selection;
		switch (contactType) {
		case ListContactSplittedActivity.LOCAL_PHONE:
			tableName = TrustEvaluationDataContract.LocalPhoneContact.TABLE_NAME;
			selection = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_ID
					+ " = ?";
			break;
		case ListContactSplittedActivity.LOCAL_EMAIL:
			tableName = TrustEvaluationDataContract.LocalEmailContact.TABLE_NAME;
			selection = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_ID
					+ " = ?";
			break;
		case ListContactSplittedActivity.FACEBOOK:
			tableName = TrustEvaluationDataContract.FacebookContact.TABLE_NAME;
			selection = TrustEvaluationDataContract.FacebookContact.COLUMN_NAME_FACEBOOK_ID
					+ " = ?";
			break;
		case ListContactSplittedActivity.TWITTER:
			tableName = TrustEvaluationDataContract.TwitterContact.TABLE_NAME;
			selection = TrustEvaluationDataContract.TwitterContact.COLUMN_NAME_TWITTER_ID
					+ " = ?";
			break;
		case ListContactSplittedActivity.LINKEDIN:
			tableName = TrustEvaluationDataContract.LinkedinContact.TABLE_NAME;
			selection = TrustEvaluationDataContract.LinkedinContact.COLUMN_NAME_LINKEDIN_ID
					+ " = ?";
			break;
		default:
			// if contact type is undefined, just return true ( data row already
			// inserted ) to avoid any insert transactions
			return true;
		}

		// selection argument
		String[] selectionArgs = { contactId };

		// query
		Cursor c = readable.query(tableName, null, selection, selectionArgs,
				null, null, null);

		boolean isInserted = false;
		if (c.getCount() == 0) {
			// no rows found
			isInserted = false;
		} else {
			// rows found
			isInserted = true;
		}
		c.close();

		Log.v(TAG, "inserted? " + isInserted);
		return isInserted;
	}

	public void insertLocalContact(int contactType, List<LocalContact> contacts) {
		if (writable == null) {
			writable = this.getWritableDatabase();
		}

		String tableName;
		if (contactType == ListContactSplittedActivity.LOCAL_PHONE) {
			tableName = TrustEvaluationDataContract.LocalPhoneContact.TABLE_NAME;
		} else if (contactType == ListContactSplittedActivity.LOCAL_EMAIL) {
			tableName = TrustEvaluationDataContract.LocalEmailContact.TABLE_NAME;
		} else {
			return;
		}

		String query = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?,?)";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		Iterator<LocalContact> i = contacts.iterator();
		while (i.hasNext()) {
			LocalContact contact = i.next();
			if (!contact.isInsertedInDatabase()) {
				statement.clearBindings();
				statement.bindString(2, contact.getContactId());
				statement.bindString(3, contact.getDisplayName());
				statement.bindString(4, contact.getContactDetail());
				statement.bindString(5, contact.getContactUri().toString());
				statement.bindNull(6);
				statement.bindLong(7, 0);
				statement.execute();
			}
		}
		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public List<LocalContact> getLocalContacts(int contactType, String sortOrder) {
		if (readable == null) {
			readable = this.getReadableDatabase();
		}

		String tableName;
		String[] columnNames = new String[4];
		if (contactType == ListContactSplittedActivity.LOCAL_PHONE) {
			tableName = TrustEvaluationDataContract.LocalPhoneContact.TABLE_NAME;
			columnNames[0] = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_ID;
			columnNames[1] = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_NAME;
			columnNames[2] = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_NUMBER;
			columnNames[3] = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_URI;
		} else if (contactType == ListContactSplittedActivity.LOCAL_EMAIL) {
			tableName = TrustEvaluationDataContract.LocalEmailContact.TABLE_NAME;
			columnNames[0] = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_ID;
			columnNames[1] = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_NAME;
			columnNames[2] = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_EMAIL;
			columnNames[3] = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_URI;
		} else {
			return null;
		}

		List<LocalContact> contacts = new ArrayList<LocalContact>();
		Cursor c = readable.query(tableName, null, null, null, null, null,
				sortOrder);
		if (c.getCount() == 0) {
			contacts = null;
		} else {
			while (c.moveToNext()) {
				contacts.add(new LocalContact(c.getString(c
						.getColumnIndex(columnNames[0])), c.getString(c
						.getColumnIndex(columnNames[1])), c.getString(c
						.getColumnIndex(columnNames[2])), Uri.parse(c
						.getString(c.getColumnIndex(columnNames[3])))));
			}
		}
		c.close();

		return contacts;
	}

	public void insertTwitterContact(List<Contact> contacts) {
		if (writable == null) {
			writable = this.getWritableDatabase();
		}

		String query = "INSERT INTO "
				+ TrustEvaluationDataContract.TwitterContact.TABLE_NAME
				+ " VALUES (?,?,?,?,?,?,?)";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		Iterator<Contact> i = contacts.iterator();
		while (i.hasNext()) {
			Contact contact = i.next();
			if (!isContactInserted(ListContactSplittedActivity.TWITTER,
					contact.getId())) {
				statement.clearBindings();
				statement.bindString(2, contact.getId());
				// real display name
				statement.bindString(3, contact.getFirstName());
				// twitter user name
				statement.bindString(4, contact.getDisplayName());
				// profile image url
				statement.bindString(5, contact.getProfileImageURL());
				statement.bindNull(6);
				statement.bindLong(7, 0);
				statement.execute();
			}
		}

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public List<Contact> getTwitterContacts(String sortOrder) {
		if (readable == null) {
			readable = this.getReadableDatabase();
		}

		List<Contact> contacts = new ArrayList<Contact>();
		Cursor c = readable.query(
				TrustEvaluationDataContract.TwitterContact.TABLE_NAME, null,
				null, null, null, null, sortOrder);
		if (c.getCount() == 0) {
			contacts = null;
		} else {
			while (c.moveToNext()) {
				Contact contact = new Contact();
				contact.setId(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.TwitterContact.COLUMN_NAME_TWITTER_ID)));
				contact.setFirstName(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.TwitterContact.COLUMN_NAME_TWITTER_NAME)));
				contact.setDisplayName(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.TwitterContact.COLUMN_NAME_TWITTER_USERNAME)));
				contact.setProfileImageURL(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.TwitterContact.COLUMN_NAME_TWITTER_PROFILE_IMAGE_URL)));
				contacts.add(contact);
			}
		}
		c.close();

		return contacts;
	}

	public void insertLinkedinContact(List<Contact> contacts) {
		if (writable == null) {
			writable = this.getWritableDatabase();
		}

		String query = "INSERT INTO "
				+ TrustEvaluationDataContract.LinkedinContact.TABLE_NAME
				+ " VALUES (?,?,?,?,?,?,?)";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		Iterator<Contact> i = contacts.iterator();
		while (i.hasNext()) {
			Contact contact = i.next();
			if (!isContactInserted(ListContactSplittedActivity.LINKEDIN,
					contact.getId())) {
				statement.clearBindings();
				statement.bindString(2, contact.getId());
				statement.bindString(3, contact.getFirstName());
				statement.bindString(4, contact.getLastName());
				// profile image url
				String url = contact.getProfileImageURL();
				if (url == null) {
					statement.bindNull(5);
				} else {
					statement.bindString(5, contact.getProfileImageURL());
				}
				statement.bindNull(6);
				statement.bindLong(7, 0);
				statement.execute();
			}
		}

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public List<Contact> getLinkedinContacts(String sortOrder) {
		if (readable == null) {
			readable = this.getReadableDatabase();
		}

		List<Contact> contacts = new ArrayList<Contact>();
		Cursor c = readable.query(
				TrustEvaluationDataContract.LinkedinContact.TABLE_NAME, null,
				null, null, null, null, sortOrder);
		if (c.getCount() == 0) {
			contacts = null;
		} else {
			while (c.moveToNext()) {
				Contact contact = new Contact();
				contact.setId(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.LinkedinContact.COLUMN_NAME_LINKEDIN_ID)));
				contact.setFirstName(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.LinkedinContact.COLUMN_NAME_LINKEDIN_FIRST_NAME)));
				contact.setLastName(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.LinkedinContact.COLUMN_NAME_LINKEDIN_LAST_NAME)));
				contact.setProfileImageURL(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.LinkedinContact.COLUMN_NAME_LINKEDIN_PROFILE_IMAGE_URL)));
				contacts.add(contact);
			}
		}
		c.close();

		return contacts;
	}

	public void insertFacebookContacts(List<GraphUser> contacts) {
		if (writable == null) {
			writable = this.getWritableDatabase();
		}

		String query = "INSERT INTO "
				+ TrustEvaluationDataContract.FacebookContact.TABLE_NAME
				+ " VALUES (?,?,?,?,?,?)";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		Iterator<GraphUser> i = contacts.iterator();
		while (i.hasNext()) {
			GraphUser contact = i.next();
			if (!isContactInserted(ListContactSplittedActivity.FACEBOOK,
					contact.getId())) {
				// get facebook id
				String facebookId = contact.getId();

				// form facebook profile picture url by facebook id
				String facebookProfilePictureUrl = "http://graph.facebook.com/"
						+ facebookId + "/picture";
				
				// do statement
				statement.clearBindings();
				statement.bindString(2, facebookId);
				statement.bindString(3, contact.getName());
				statement.bindString(4, facebookProfilePictureUrl);
				statement.bindNull(5);
				statement.bindLong(6, 0);
				statement.execute();
			}
		}

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public List<PseudoFacebookGraphUser> getFacebookContacts(String sortOrder) {
		if (readable == null) {
			readable = this.getReadableDatabase();
		}

		List<PseudoFacebookGraphUser> contacts = new ArrayList<PseudoFacebookGraphUser>();
		Cursor c = readable.query(
				TrustEvaluationDataContract.FacebookContact.TABLE_NAME, null,
				null, null, null, null, sortOrder);
		if (c.getCount() == 0) {
			contacts = null;
		} else {
			while (c.moveToNext()) {
				// get facebook id from cursor
				String facebookId = c
						.getString(c
								.getColumnIndex(TrustEvaluationDataContract.FacebookContact.COLUMN_NAME_FACEBOOK_ID));

				// get facebook name from cursor
				String facebookName = c
						.getString(c
								.getColumnIndex(TrustEvaluationDataContract.FacebookContact.COLUMN_NAME_FACEBOOK_NAME));

				// form the profile image url by facebook id
				String facebookProfileImageUrl = "http://graph.facebook.com/"
						+ facebookId + "/picture";

				PseudoFacebookGraphUser contact = new PseudoFacebookGraphUser(
						facebookId, facebookName, facebookProfileImageUrl);
				contacts.add(contact);
			}
		}
		c.close();

		return contacts;
	}
}

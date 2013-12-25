package fr.utt.isi.tx.trustevaluationandroidapp.database;

import java.util.ArrayList;
import java.util.List;

import org.brickred.socialauth.Contact;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import fr.utt.isi.tx.trustevaluationandroidapp.activities.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.models.MergedContactNode;

public class TrustEvaluationContactNode {
	private static final String TAG = "TrustEvaluationContactNode";

	private static TrustEvaluationDbHelper mDbHelper = null;
	public String tableName = TrustEvaluationDataContract.ContactNode.TABLE_NAME;

	private SQLiteDatabase readable = null;
	private SQLiteDatabase writable = null;

	public TrustEvaluationContactNode(Context context) {
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(context);
		}
	}

	public boolean isInsertedContactNode(String name) {
		return isInsertedContactNode(tableName, name);
	}
	
	public boolean isInsertedContactNode(String tableName, String name) {
		if (readable == null) {
			readable = mDbHelper.getReadableDatabase();
		}

		String selection = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
				+ " = ?";

		// selection argument
		String[] selectionArgs = { name };

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

		//Log.v(TAG, "insertedContactNode? " + isInserted);
		return isInserted;
	}

	public MergedContactNode generateMergedContact(int contactType,
			Contact contact) {
		Log.v(TAG, "generating merged contact..");

		String name;
		MergedContactNode contactNode;
		switch (contactType) {
		case ListContactSplittedActivity.LOCAL_PHONE:
			name = contact.getDisplayName();
			contactNode = new MergedContactNode(name);
			contactNode.setIsLocalPhone(1);
			break;
		case ListContactSplittedActivity.LOCAL_EMAIL:
			name = contact.getDisplayName();
			contactNode = new MergedContactNode(name);
			contactNode.setIsLocalEmail(1);
			break;
		case ListContactSplittedActivity.FACEBOOK:
			name = contact.getDisplayName();
			contactNode = new MergedContactNode(name);
			contactNode.setIsFacebook(1);
			contactNode.setFacebookId(contact.getId());
			break;
		case ListContactSplittedActivity.TWITTER:
			name = contact.getFirstName();
			contactNode = new MergedContactNode(name);
			contactNode.setIsTwitter(1);
			break;
		case ListContactSplittedActivity.LINKEDIN:
			name = contact.getFirstName() + " " + contact.getLastName();
			contactNode = new MergedContactNode(name);
			contactNode.setIsLinkedin(1);
			break;
		default:
			return null;
		}

		return contactNode;
	}

	public void insertContactNode(MergedContactNode contactNode) {
		insertContactNode(tableName, contactNode);
	}
	
	public void insertContactNode(String tableName, MergedContactNode contactNode) {
		//Log.v(TAG, "inserting contactNode..");

		if (writable == null) {
			writable = mDbHelper.getWritableDatabase();
		}

		String query = "INSERT INTO " + tableName
				+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		if (!isInsertedContactNode(tableName, contactNode.getDisplayNameGlobal())) {
			statement.clearBindings();
			statement.bindString(2, contactNode.getDisplayNameGlobal());
			statement.bindLong(3, contactNode.getSourceScore());
			statement.bindLong(4, contactNode.getTrustScore());
			statement.bindLong(5, contactNode.getIsLocalPhone());
			statement.bindLong(6, contactNode.getIsLocalEmail());
			statement.bindLong(7, contactNode.getIsFacebook());
			statement.bindLong(8, contactNode.getIsTwitter());
			statement.bindLong(9, contactNode.getIsLinkedin());
			if (contactNode.getFacebookId() == null) {
				statement.bindNull(10);
			} else {
				statement.bindString(10, contactNode.getFacebookId());
			}
			statement.execute();
		}

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public void updateContactNode(int contactType,
			MergedContactNode mergedContact) {
		if (writable == null) {
			writable = mDbHelper.getWritableDatabase();
		}

		String query = "UPDATE "
				+ tableName
				+ " SET "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_PHONE
				+ "=? "
				+ ", "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_EMAIL
				+ "=? "
				+ ", "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_FACEBOOK
				+ "=? "
				+ ", "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_TWITTER
				+ "=? "
				+ ", "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LINKEDIN
				+ "=? "
				+ ", "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_FACEBOOK_ID
				+ "=? "
				+ " WHERE "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
				+ "=?";

		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		statement.clearBindings();
		statement.bindLong(1, mergedContact.getIsLocalPhone());
		statement.bindLong(2, mergedContact.getIsLocalEmail());
		statement.bindLong(3, mergedContact.getIsFacebook());
		statement.bindLong(4, mergedContact.getIsTwitter());
		statement.bindLong(5, mergedContact.getIsLinkedin());
		if (mergedContact.getFacebookId() != null)
			statement.bindString(6, mergedContact.getFacebookId());
		else
			statement.bindNull(6);
		statement.bindString(7, mergedContact.getDisplayNameGlobal());
		statement.execute();

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}
	
	public void createVirtualFTSTableForSearch() {
		if (writable == null) {
			writable = mDbHelper.getWritableDatabase();
		}
		
		writable.execSQL(TrustEvaluationDataContract.ContactNode.SQL_CREATE_VIRTUAL_FTS_ENTRIES);
		
		List<MergedContactNode> contactNodeList = getMergedContacts(null);
		for (int i = 0; i < contactNodeList.size(); i++) {
			insertContactNode(TrustEvaluationDataContract.ContactNode.VIRTUAL_TABLE_NAME, contactNodeList.get(i));
		}
	}
	
	public void dropVirtualFTSTableForSearch() {
		if (writable == null) {
			writable = mDbHelper.getWritableDatabase();
		}
		
		writable.execSQL(TrustEvaluationDataContract.ContactNode.SQL_DELETE_VIRTUAL_FTS_ENTRIES);
	}

	public List<MergedContactNode> getMergedContacts(String sortOrder) {
		return getMergedContacts(tableName, null, null, sortOrder);
	}
	
	public List<MergedContactNode> getMergedContacts(String tableName, String selection, String[] selectionArgs, String sortOrder) {
		String displayName;
		int sourceScore;
		int trustScore;
		int isLocalPhone;
		int isLocalEmail;
		int isFacebook;
		int isTwitter;
		int isLinkedin;

		if (readable == null) {
			readable = mDbHelper.getReadableDatabase();
		}

		List<MergedContactNode> contacts = new ArrayList<MergedContactNode>();

		Cursor c = readable.query(tableName, null, selection, selectionArgs, null, null,
				sortOrder);
		if (c.getCount() == 0) {
			contacts = null;
		} else {
			while (c.moveToNext()) {
				displayName = c
						.getString(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL));
				sourceScore = c
						.getInt(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE));
				trustScore = c
						.getInt(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_TRUST_SCORE));
				isLocalPhone = c
						.getInt(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_PHONE));
				isLocalEmail = c
						.getInt(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_EMAIL));
				isFacebook = c
						.getInt(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_FACEBOOK));
				isTwitter = c
						.getInt(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_TWITTER));
				isLinkedin = c
						.getInt(c
								.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LINKEDIN));

				MergedContactNode contact = new MergedContactNode();
				contact.setDisplayNameGlobal(displayName);
				contact.setSourceScore(sourceScore);
				contact.setTrustScore(trustScore);
				contact.setIsLocalPhone(isLocalPhone);
				contact.setIsLocalEmail(isLocalEmail);
				contact.setIsFacebook(isFacebook);
				contact.setIsTwitter(isTwitter);
				contact.setIsLinkedin(isLinkedin);
				contact.setFacebookId(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_FACEBOOK_ID)));
				contacts.add(contact);
			}
		}
		c.close();

		return contacts;

	}

	public void calculateSourceScore(MergedContactNode contact) {
		int score = 0;
		int isLocalPhone;
		int isLocalEmail;
		int isFacebook;
		int isTwitter;
		int isLinkedin;

		if (readable == null) {
			readable = mDbHelper.getReadableDatabase();
		}

		String selection = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
				+ "=?";
		String[] selectionArgs = { contact.getDisplayNameGlobal() };

		Cursor c = readable.query(tableName, null, selection, selectionArgs,
				null, null, null);

		while (c.moveToNext()) {
			isLocalPhone = c
					.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_PHONE));
			isLocalEmail = c
					.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_EMAIL));
			isFacebook = c
					.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_FACEBOOK));
			isTwitter = c
					.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_TWITTER));
			isLinkedin = c
					.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LINKEDIN));

			score = isLocalPhone + isLocalEmail + isFacebook + isTwitter
					+ isLinkedin;
		}

		c.close();
		
		if (writable == null) {
			writable = mDbHelper.getWritableDatabase();
		}

		String query = "UPDATE "
				+ tableName
				+ " SET "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE
				+ "=? WHERE "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
				+ "=?";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		statement.clearBindings();
		statement.bindLong(1, score);
		statement.bindString(2, contact.getDisplayNameGlobal());
		statement.execute();

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

}

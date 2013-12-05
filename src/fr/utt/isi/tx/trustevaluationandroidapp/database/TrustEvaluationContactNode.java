package fr.utt.isi.tx.trustevaluationandroidapp.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.matching.MergedContact;

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

		Log.v(TAG, "insertedContactNode? " + isInserted);
		return isInserted;
	}

	public MergedContact generateMergedContact(int contactType, String name) {
		Log.v(TAG, "generating merged contact..");

		MergedContact contact = new MergedContact();

		contact.setDisplayNameGlobal(name);
		contact.setSourceScore(0);
		contact.setTrustScore(0);

		switch (contactType) {
		case ListContactSplittedActivity.LOCAL_PHONE:
			contact.setIsLocalPhone(1);
			contact.setIsLocalEmail(0);
			contact.setIsFacebook(0);
			contact.setIsTwitter(0);
			contact.setIsLinkedin(0);
			break;
		case ListContactSplittedActivity.LOCAL_EMAIL:
			contact.setIsLocalPhone(0);
			contact.setIsLocalEmail(1);
			contact.setIsFacebook(0);
			contact.setIsTwitter(0);
			contact.setIsLinkedin(0);
			break;
		case ListContactSplittedActivity.FACEBOOK:
			contact.setIsLocalPhone(0);
			contact.setIsLocalEmail(0);
			contact.setIsFacebook(1);
			contact.setIsTwitter(0);
			contact.setIsLinkedin(0);
			break;
		case ListContactSplittedActivity.TWITTER:
			contact.setIsLocalPhone(0);
			contact.setIsLocalEmail(0);
			contact.setIsFacebook(0);
			contact.setIsTwitter(1);
			contact.setIsLinkedin(0);
			break;
		case ListContactSplittedActivity.LINKEDIN:
			contact.setIsLocalPhone(0);
			contact.setIsLocalEmail(0);
			contact.setIsFacebook(0);
			contact.setIsTwitter(0);
			contact.setIsLinkedin(1);
			break;
		default:
			contact.setIsLocalPhone(0);
			contact.setIsLocalEmail(0);
			contact.setIsFacebook(0);
			contact.setIsTwitter(0);
			contact.setIsLinkedin(0);
			break;
		}

		return contact;
	}

	public void insertContactNode(MergedContact contact) {
		Log.v(TAG, "inserting contactNode..");

		if (writable == null) {
			writable = mDbHelper.getWritableDatabase();
		}

		String query = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?,?,?)";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		if (!isInsertedContactNode(contact.getDisplayNameGlobal())) {
			statement.clearBindings();
			statement.bindString(1, contact.getDisplayNameGlobal());
			statement.bindLong(2, contact.getSourceScore());
			statement.bindLong(3, contact.getTrustScore());
			statement.bindLong(4, contact.getIsLocalPhone());
			statement.bindLong(5, contact.getIsLocalEmail());
			statement.bindLong(6, contact.getIsFacebook());
			statement.bindLong(7, contact.getIsTwitter());
			statement.bindLong(8, contact.getIsLinkedin());
			statement.execute();
		}

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public void updateContactNode(int contactType, MergedContact mergedContact) {
		if (writable == null) {
			writable = mDbHelper.getWritableDatabase();
		}

		String list = "";

		switch (contactType) {
		case ListContactSplittedActivity.LOCAL_PHONE:
			list = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_PHONE;
			break;
		case ListContactSplittedActivity.LOCAL_EMAIL:
			list = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_EMAIL;
			break;
		case ListContactSplittedActivity.FACEBOOK:
			list = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_FACEBOOK;
			break;
		case ListContactSplittedActivity.TWITTER:
			list = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_TWITTER;
			break;
		case ListContactSplittedActivity.LINKEDIN:
			list = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LINKEDIN;
			break;
		default:
			break;
		}

		String query = "UPDATE "
				+ tableName
				+ " SET "
				+ list
				+ "=? WHERE "
				+ TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
				+ "=?";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		statement.clearBindings();
		statement.bindLong(1, 1);
		statement.bindString(2, mergedContact.getDisplayNameGlobal());
		statement.execute();

		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public List<MergedContact> getMergedContacts(String sortOrder) {
		if (readable == null) {
			readable = mDbHelper.getReadableDatabase();
		}

		List<MergedContact> contacts = new ArrayList<MergedContact>();

		Cursor c = readable.query(tableName, null, null, null, null, null,
				sortOrder);
		if (c.getCount() == 0) {
			contacts = null;
		} else {
			while (c.moveToNext()) {
				MergedContact contact = new MergedContact();
				contact.setDisplayNameGlobal(c.getString(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL)));
				contact.setSourceScore(c.getInt(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE)));
				contact.setTrustScore(c.getInt(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_TRUST_SCORE)));
				contact.setIsLocalPhone(c.getInt(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_PHONE)));
				contact.setIsLocalEmail(c.getInt(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_EMAIL)));
				contact.setIsFacebook(c.getInt(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_FACEBOOK)));
				contact.setIsTwitter(c.getInt(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_TWITTER)));
				contact.setIsLinkedin(c.getInt(c
						.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LINKEDIN)));
				contacts.add(contact);
			}
		}
		c.close();

		return contacts;

	}

	public void calculateSourceScore(MergedContact contact) {
		Log.v(TAG, "calculating source score...");

		int score = 0;

		if (readable == null) {
			readable = mDbHelper.getReadableDatabase();
		}

		String selection = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
				+ "=?";
		String[] selectionArgs = { contact.getDisplayNameGlobal() };

		Cursor c = readable.query(tableName, null, selection, selectionArgs,
				null, null, null);

		while (c.moveToNext()) {
			score = c
					.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_PHONE))
					+ c.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LOCAL_EMAIL))
					+ c.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_FACEBOOK))
					+ c.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_TWITTER))
					+ c.getInt(c
							.getColumnIndex(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_LINKEDIN));
		}

		c.close();

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

package fr.utt.isi.tx.trustevaluationandroidapp.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.utt.isi.tx.trustevaluationandroidapp.ListContactSplittedActivity;
import fr.utt.isi.tx.trustevaluationandroidapp.localcontact.ContactUser;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class TrustEvaluationDbHelper extends SQLiteOpenHelper {

	private static final String TAG = "TrustEvaluationDbHelper";

	// increase database version when database schema changes
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "TrustEvaluation.db";

	private static SQLiteDatabase readable = null;
	private static SQLiteDatabase writable = null;

	public TrustEvaluationDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TrustEvaluationDataContract.LocalPhoneContact.SQL_CREATE_ENTRIES);
		db.execSQL(TrustEvaluationDataContract.LocalEmailContact.SQL_CREATE_ENTRIES);
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
		// selection clause
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

		// db.close();

		Log.v(TAG, "inserted? " + isInserted);
		return isInserted;
	}

	public void insertLocalContact(int contactType, List<ContactUser> contacts) {
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

		String query = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?)";
		SQLiteStatement statement = writable.compileStatement(query);
		writable.beginTransaction();

		Iterator<ContactUser> i = contacts.iterator();
		while (i.hasNext()) {
			ContactUser contact = i.next();
			if (!contact.isInsertedInDatabase()) {
				statement.clearBindings();
				statement.bindString(2, contact.getContactId());
				statement.bindString(3, contact.getDisplayName());
				statement.bindString(4, contact.getContactDetail());
				statement.bindNull(5);
				statement.bindLong(6, 0);
				statement.execute();
			}
		}
		writable.setTransactionSuccessful();
		writable.endTransaction();
	}

	public List<ContactUser> getLocalContacts(int contactType, String sortOrder) {
		if (readable == null) {
			readable = this.getReadableDatabase();
		}

		String tableName;
		String[] columnNames = new String[3];
		if (contactType == ListContactSplittedActivity.LOCAL_PHONE) {
			tableName = TrustEvaluationDataContract.LocalPhoneContact.TABLE_NAME;
			columnNames[0] = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_ID;
			columnNames[1] = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_NAME;
			columnNames[2] = TrustEvaluationDataContract.LocalPhoneContact.COLUMN_NAME_LOCAL_NUMBER;
		} else if (contactType == ListContactSplittedActivity.LOCAL_EMAIL) {
			tableName = TrustEvaluationDataContract.LocalEmailContact.TABLE_NAME;
			columnNames[0] = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_ID;
			columnNames[1] = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_NAME;
			columnNames[2] = TrustEvaluationDataContract.LocalEmailContact.COLUMN_NAME_LOCAL_EMAIL;
		} else {
			return null;
		}

		ArrayList<ContactUser> contacts = new ArrayList<ContactUser>();
		Cursor c = readable.query(tableName, null, null, null, null, null,
				sortOrder);
		if (c.getCount() == 0) {
			contacts = null;
		} else {
			while (c.moveToNext()) {
				contacts.add(new ContactUser(
						c.getString(c.getColumnIndex(columnNames[0])), 
						c.getString(c.getColumnIndex(columnNames[1])), 
						c.getString(c.getColumnIndex(columnNames[2]))));
			}
		}

		return contacts;
	}

}

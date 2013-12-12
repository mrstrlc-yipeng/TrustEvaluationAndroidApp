package fr.utt.isi.tx.trustevaluationandroidapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Utils {
	
	private static final String TAG = "Utils";
	
	public static String cleanDisplayName(String displayName) {
		// clean display_name if its format is email
		if (displayName.contains("@")) {
			String[] split = displayName.split("@");			
			displayName = split[0];
		} 
		
		// clean all the characters other than letters
		String newDisplayName1 = displayName.replaceAll("[1-9]", "");
		String newDisplayName = newDisplayName1.replaceAll("[^a-zA-Z ]", " ");
		
		return newDisplayName;
	}

	public static String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();

		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);

			if (n > 0)
				out.append(new String(b, 0, n));
		}
		
		//Log.v(TAG, out.toString());

		return out.toString();
	}

	public static ArrayList<String> convertJSONStringToArrayList(
			String mJSONString, String key) throws JSONException {
		if (mJSONString == null || mJSONString == "") {
			return null;
		}

		JSONObject mJSONObject = new JSONObject(mJSONString);
		JSONArray mJSONArray = mJSONObject.getJSONArray(key);

		ArrayList<String> mArrayList = new ArrayList<String>();

		for (int i = 0; i < mJSONArray.length(); i++) {
			mArrayList.add(mJSONArray.getString(i));
		}

		return mArrayList;
	}

	public static String generateCommonFriendListStringByJSONString(
			String myFriendListJSON, String contactFriendListJSON, String key, char seperator)
			throws JSONException {
		// convert the JSON strings into array lists in order to do intersection
		ArrayList<String> myFriendArrayList = convertJSONStringToArrayList(
				myFriendListJSON, key);
		ArrayList<String> contactFriendArrayList = convertJSONStringToArrayList(
				contactFriendListJSON, key);
		
		if (myFriendArrayList == null || contactFriendArrayList == null) {
			return null;
		}
		
		// do intersection of two array lists
		myFriendArrayList.retainAll(contactFriendArrayList);
		
		// convert the intersection array list into string
		if (myFriendArrayList.size() == 0) {
			return "";
		}
		
		StringBuilder commonFriendList = new StringBuilder();
		Iterator<String> i = myFriendArrayList.iterator();
		while (i.hasNext()) {
			String contact = i.next();
			commonFriendList.append(contact);
			if (myFriendArrayList.indexOf(contact) != myFriendArrayList.size() - 1) {
				commonFriendList.append(seperator);
			}
		}
		return commonFriendList.toString();
	}
}

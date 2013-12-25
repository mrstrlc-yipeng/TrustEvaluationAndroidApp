package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.ContactNodeListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.models.MergedContactNode;

public class MatchingListFragment extends Fragment {

	private static final String TAG = "MatchingListFragment";

	private String sortOrder = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE
			+ " DESC";
	private String searchQuery = null;

	private ContactNodeListAdapter adapter;
	private TrustEvaluationContactNode contactNodeHelper;

	private ListView mergedList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the intent, verify the action and get the query
		Intent intent = getActivity().getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			searchQuery = intent.getStringExtra(SearchManager.QUERY);
			Log.v(TAG, searchQuery);
		}

		if (contactNodeHelper == null)
			contactNodeHelper = new TrustEvaluationContactNode(getActivity());
		
		new VirtualContactNodeTableCopier().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_matching_list,
				container, false);

		mergedList = (ListView) view.findViewById(R.id.matching_list);

		List<MergedContactNode> contacts;
		if (searchQuery != null && searchQuery != "") {
			// execute the search by "match" mechanism of FTS
			String tableName = TrustEvaluationDataContract.ContactNode.VIRTUAL_TABLE_NAME;
			String selection = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
					+ " MATCH ?";
			String[] selectionArgs = new String[] { searchQuery };
			contacts = contactNodeHelper.getMergedContacts(tableName,
					selection, selectionArgs, sortOrder);
		} else {
			contacts = contactNodeHelper.getMergedContacts(sortOrder);
		}
		adapter = new ContactNodeListAdapter(getActivity(), R.id.matching_list,
				contacts);

		mergedList.setAdapter(adapter);

		return view;
	}

	private class VirtualContactNodeTableCopier extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.v(TAG, "creating virtual table...");
			if (contactNodeHelper == null)
				contactNodeHelper = new TrustEvaluationContactNode(
						getActivity());
			
			// drop the older virtual table
			//contactNodeHelper.dropVirtualFTSTableForSearch();

			// create the virtual table of contact node table
			contactNodeHelper.createVirtualFTSTableForSearch();
			return null;
		}

	}

}

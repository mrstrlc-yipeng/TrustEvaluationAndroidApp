package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
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

		adapter = new ContactNodeListAdapter(getActivity(),
				R.layout.contact_node_list, getContactNodes());

		mergedList.setAdapter(adapter);

		return view;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// change the list to complete layout with icon of platforms
			adapter = new ContactNodeListAdapter(getActivity(),
					R.layout.contact_node_complete_list, getContactNodes());
			mergedList.setAdapter(adapter);
			mergedList.invalidateViews();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// change back the list to simple layout
			adapter = new ContactNodeListAdapter(getActivity(),
					R.layout.contact_node_list, getContactNodes());
			mergedList.setAdapter(adapter);
			mergedList.invalidateViews();
		} else {
			Log.v(TAG, "others");
		}
	}

	private List<MergedContactNode> getContactNodes() {
		List<MergedContactNode> contactNodes;
		if (searchQuery != null && searchQuery != "") {
			// execute the search by "match" mechanism of FTS
			String tableName = TrustEvaluationDataContract.ContactNode.VIRTUAL_TABLE_NAME;
			String selection = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
					+ " MATCH ?";
			String[] selectionArgs = new String[] { searchQuery };
			contactNodes = contactNodeHelper.getMergedContacts(tableName,
					selection, selectionArgs, sortOrder);
		} else {
			contactNodes = contactNodeHelper.getMergedContacts(sortOrder);
		}

		return contactNodes;
	}

}

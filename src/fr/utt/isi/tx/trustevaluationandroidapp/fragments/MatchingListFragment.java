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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.ContactNodeListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.models.MergedContactNode;

public class MatchingListFragment extends Fragment implements OnClickListener {

	private static final String TAG = "MatchingListFragment";

	private String sortOrder = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE
			+ " DESC";
	private String searchQuery = null;

	private ContactNodeListAdapter adapter;
	private TrustEvaluationDbHelper mDbHelper;

	private ListView mergedList;
	
	private int currentLayoutResourceId = R.layout.contact_node_list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the intent, verify the action and get the query
		Intent intent = getActivity().getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			searchQuery = intent.getStringExtra(SearchManager.QUERY);
			Log.v(TAG, searchQuery);
		}
		
		mDbHelper = new TrustEvaluationDbHelper(getActivity());
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
		
		Button refreshButton = (Button) view.findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(this);

		return view;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// change the list to complete layout with icon of platforms
			updateListView(R.layout.contact_node_complete_list);
			currentLayoutResourceId = R.layout.contact_node_complete_list;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// change back the list to simple layout
			updateListView(R.layout.contact_node_list);
			currentLayoutResourceId = R.layout.contact_node_list;
		} else {
			Log.v(TAG, "others");
		}
	}

	private List<MergedContactNode> getContactNodes() {
		List<MergedContactNode> contactNodes;
		if (searchQuery != null && searchQuery != "") {
			String tableName = TrustEvaluationDataContract.ContactNode.TABLE_NAME;
			String selection = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_DISPLAY_NAME_GLOBAL
					+ " LIKE ?";
			String[] selectionArgs = new String[] { "%" + searchQuery + "%" };
			contactNodes = mDbHelper.getMergedContacts(tableName,
					selection, selectionArgs, sortOrder);
		} else {
			contactNodes = mDbHelper.getMergedContacts(sortOrder);
		}

		return contactNodes;
	}
	
	private void updateListView(int resourceId) {
		adapter = new ContactNodeListAdapter(getActivity(),
				resourceId, getContactNodes());
		mergedList.setAdapter(adapter);
		mergedList.invalidateViews();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.refresh_button:
			updateListView(currentLayoutResourceId);
			break;
		default:
			break;
		}
	}

}

package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.util.List;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.ContactNodeListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.MergedContactNode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TrustIndexListFragment extends Fragment {
	
	private static final String TAG = "TrustIndexListFragment";
	
	private String sortOrder = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_TRUST_SCORE + " DESC";
	
	private TrustEvaluationDbHelper mDbHelper = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_trust_index_list,
				container, false);

		// list view
		ListView trustIndexListView = (ListView) view.findViewById(R.id.trust_index_list);
		
		// data list
		Log.v(TAG, "getting contact node list from db...");
		List<MergedContactNode> contactNodeList = mDbHelper.getMergedContacts(sortOrder);
		
		// set up adapter to display the trust index score
		ContactNodeListAdapter mAdapter = new ContactNodeListAdapter(getActivity(), R.layout.contact_node_list, contactNodeList);
		mAdapter.setScoreColumn(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_TRUST_SCORE);
		
		// assign the adapter to the list view
		trustIndexListView.setAdapter(mAdapter);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}
	}
	
}

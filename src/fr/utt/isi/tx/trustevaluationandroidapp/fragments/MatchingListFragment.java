package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.ContactNodeListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.utils.MergedContactNode;

public class MatchingListFragment extends Fragment {

	//private static final String TAG = "MatchingListFragment";
	
	private String sortOrder = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE + " DESC";

	private ContactNodeListAdapter adapter;
	private TrustEvaluationContactNode contactNodeHelper;

	private ListView mergedList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		List<MergedContactNode> contacts = contactNodeHelper
				.getMergedContacts(sortOrder);
		adapter = new ContactNodeListAdapter(getActivity(), R.id.matching_list,
				contacts);

		mergedList.setAdapter(adapter);

		return view;
	}

}

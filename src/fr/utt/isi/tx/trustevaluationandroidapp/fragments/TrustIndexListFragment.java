package fr.utt.isi.tx.trustevaluationandroidapp.fragments;

import java.io.File;
import java.io.IOException;
import java.util.List;

import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.adapters.ContactNodeListAdapter;
import fr.utt.isi.tx.trustevaluationandroidapp.config.Config;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import fr.utt.isi.tx.trustevaluationandroidapp.models.MergedContactNode;
import fr.utt.isi.tx.trustevaluationandroidapp.tasks.TweetTrustIndexTask;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TrustIndexListFragment extends Fragment implements
		OnItemClickListener, OnClickListener {

	private static final String TAG = "TrustIndexListFragment";

	private static final String[] ITEM_OPTIONS = { "send tweet" };

	private String sortOrder = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_TRUST_SCORE
			+ " DESC";

	private TrustEvaluationDbHelper mDbHelper = null;

	private List<MergedContactNode> contactNodeList;

	private ListView trustIndexListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}
		mDbHelper.calculateTrustIndex();

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_trust_index_list,
				container, false);

		// list view
		trustIndexListView = (ListView) view
				.findViewById(R.id.trust_index_list);

		updateListView();
		
		Button refreshButton = (Button) view.findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(this);

		return view;
	}

	private void updateListView() {
		// data list
		String tableName = TrustEvaluationDataContract.ContactNode.TABLE_NAME;
		String selection = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_IS_FACEBOOK + "= ?";
		String[] selectionArgs = new String[] {String.valueOf(1)};
		contactNodeList = mDbHelper.getMergedContacts(tableName, selection, selectionArgs, sortOrder);

		// set up adapter to display the trust index score
		ContactNodeListAdapter mAdapter = new ContactNodeListAdapter(
				getActivity(), R.layout.contact_node_list, contactNodeList);
		mAdapter.setScoreColumn(TrustEvaluationDataContract.ContactNode.COLUMN_NAME_TRUST_SCORE);

		// assign the adapter to the list view
		trustIndexListView.setAdapter(mAdapter);
		trustIndexListView.setOnItemClickListener(this);
		trustIndexListView.invalidateViews();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String contactName = contactNodeList.get(position)
				.getDisplayNameGlobal();
		int trustIndex = contactNodeList.get(position).getTrustScore();

		// composite status
		final String status = "The trust score between "
				+ contactName
				+ " and I is estimated to be "
				+ trustIndex
				+ " by #TrustEvaluationAndroidApp ! Try evaluate your contacts now! ";

		AlertDialog.Builder optionsBuilder = new AlertDialog.Builder(
				getActivity());
		optionsBuilder.setTitle(R.string.app_name);
		optionsBuilder.setItems(ITEM_OPTIONS,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setTitle("Tweet status:");
						builder.setMessage(status);
						builder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										new TweetTrustIndexTask(getActivity())
												.execute(status);
									}
								});
						builder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

									}
								});
						builder.create().show();
					}
				});
		optionsBuilder.create().show();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mDbHelper == null) {
			mDbHelper = new TrustEvaluationDbHelper(getActivity());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_export:
			Log.v(TAG, "item selected export");
			exportAndSend();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.refresh_button:
			// re-do calculate
			if (mDbHelper == null) {
				mDbHelper = new TrustEvaluationDbHelper(getActivity());
			}
			mDbHelper.calculateTrustIndex();
			
			// update list view
			updateListView();
			break;
		default:
			break;
		}
	}

	public void exportAndSend() {
		try {
			mDbHelper.exportNodeContactTable(getActivity());
		} catch (IOException e) {
			Toast.makeText(getActivity(), "Unable to export database.",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// get the export file
		File file = new File(getActivity().getFilesDir(),
				Config.EXPORT_FILE_NAME);

		// create email
		Intent i = new Intent(android.content.Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL,
				new String[] { Config.EMAIL_DESTINATION });
		i.putExtra(Intent.EXTRA_SUBJECT, Config.EMAIL_SUJECT);
		i.putExtra(Intent.EXTRA_TEXT, Config.EMAIL_BODY);

		// attach the file
		Uri uri = Uri.fromFile(file);
		Log.v(TAG, "file uri: " + uri.toString());
		i.putExtra(android.content.Intent.EXTRA_STREAM, uri);

		try {
			startActivity(Intent.createChooser(i,
					"Send mail with exported database file..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getActivity(),
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

}

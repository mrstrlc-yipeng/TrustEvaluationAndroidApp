package fr.utt.isi.tx.trustevaluationandroidapp.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.models.MergedContactNode;

public class ContactNodeListAdapter extends ArrayAdapter<MergedContactNode> {

	//private static final String TAG = "ContactNodeListAdapter";

	private Context context;

	private List<MergedContactNode> contactNodes;
	
	private String scoreColumn = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE;

	public ContactNodeListAdapter(Context context, int textViewResourceId,
			List<MergedContactNode> contactNodes) {
		super(context, textViewResourceId, contactNodes);

		this.context = context;
		this.contactNodes = contactNodes;
	}

	@Override
	public int getCount() {
		return contactNodes.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.contact_node_list, null);
		}

		MergedContactNode listElement = contactNodes.get(position);
		if (listElement != null) {
			// set display name into view
			TextView nameView = (TextView) view
					.findViewById(R.id.contact_name_global);
			nameView.setText(listElement.getDisplayNameGlobal());

			// set corresponding score into view
			TextView scoreView = (TextView) view.findViewById(R.id.source_score);
			String score;
			if (scoreColumn == TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE) {
				score = String.valueOf(listElement.getSourceScore());
			} else if (scoreColumn == TrustEvaluationDataContract.ContactNode.COLUMN_NAME_TRUST_SCORE) {
				score = String.valueOf(listElement.getTrustScore());
			} else {
				score = "N/A";
			}
			scoreView.setText("" + score);
		}

		return view;
	}
	
	public String getScoreColumn() {
		return this.scoreColumn;
	}
	
	public void setScoreColumn(String scoreColumn) {
		this.scoreColumn = scoreColumn;
	}
}

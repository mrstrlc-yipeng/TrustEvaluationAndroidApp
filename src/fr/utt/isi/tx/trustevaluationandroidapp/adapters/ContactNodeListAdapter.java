package fr.utt.isi.tx.trustevaluationandroidapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDataContract;
import fr.utt.isi.tx.trustevaluationandroidapp.models.MergedContactNode;

public class ContactNodeListAdapter extends ArrayAdapter<MergedContactNode> {

	private static final String TAG = "ContactNodeListAdapter";

	private Context context;
	
	private int textViewResourceId;

	private List<MergedContactNode> contactNodes;
	
	private String scoreColumn = TrustEvaluationDataContract.ContactNode.COLUMN_NAME_SOURCE_SCORE;

	public ContactNodeListAdapter(Context context, int textViewResourceId,
			List<MergedContactNode> contactNodes) {
		super(context, textViewResourceId, contactNodes);

		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.contactNodes = contactNodes == null ? new ArrayList<MergedContactNode>() : contactNodes;
	}

	@Override
	public int getCount() {
		return contactNodes.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null || textViewResourceId == R.layout.contact_node_complete_list) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(textViewResourceId, null);
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
			
			// set source check image into view if the layout is R.layout.contact_node_complet_list
			if (textViewResourceId == R.layout.contact_node_complete_list) {
				Log.v(TAG, listElement.getDisplayNameGlobal() + ": " + listElement.getIsLocalPhone() + " " + listElement.getIsLocalEmail() + " " + listElement.getIsFacebook() + " " + listElement.getIsTwitter() + " " + listElement.getIsLinkedin());

				if (listElement.getIsLocalPhone() == 1) {
					ImageView isLocalPhoneImageView = (ImageView) view.findViewById(R.id.is_local_phone);
					isLocalPhoneImageView.setImageResource(R.drawable.local_phone_logo);
				}
				
				if (listElement.getIsLocalEmail() == 1) {
					ImageView isLocalEmailImageView = (ImageView) view.findViewById(R.id.is_local_email);
					isLocalEmailImageView.setImageResource(R.drawable.email_logo);
				}
				
				if (listElement.getIsFacebook() == 1) {
					ImageView isFacebookImageView = (ImageView) view.findViewById(R.id.is_facebook);
					isFacebookImageView.setImageResource(R.drawable.facebook_logo);
				}
				
				if (listElement.getIsTwitter() == 1) {
					ImageView isTwitterImageView = (ImageView) view.findViewById(R.id.is_twitter);
					isTwitterImageView.setImageResource(R.drawable.twitter_logo);
				}
				
				if (listElement.getIsLinkedin() == 1) {
					ImageView isLinkedinImageView = (ImageView) view.findViewById(R.id.is_linkedin);
					isLinkedinImageView.setImageResource(R.drawable.linkedin_logo);
				}
			}
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

package fr.utt.isi.tx.trustevaluationandroidapp.adapters;

import java.util.List;

import org.brickred.customadapter.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphUser;

import fr.utt.isi.tx.trustevaluationandroidapp.R;

public class GraphUserListAdapter extends ArrayAdapter<GraphUser> {

	// private static final String TAG = "GraphUserListAdapter";

	private Context context;

	private List<GraphUser> listElements;

	private ImageLoader imageLoader;

	public GraphUserListAdapter(Context context, int resource,
			List<GraphUser> listElements) {
		super(context, resource, listElements);
		this.context = context;
		this.listElements = listElements;
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return listElements.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.facebook_friend_list, null);
		}

		GraphUser listElement = listElements.get(position);
		if (listElement != null) {
			// get facebook id
			String facebookId = listElement.getId();

			// form facebook profile picture url by facebook id
			String facebookProfilePictureUrl = "http://graph.facebook.com/"
					+ facebookId + "/picture";

			// load profile picture
			ImageView profilePictureView = (ImageView) view
					.findViewById(R.id.profile_picture);
			imageLoader.DisplayImage(facebookProfilePictureUrl,
					profilePictureView);

			// set profile name
			TextView userNameView = (TextView) view
					.findViewById(R.id.friend_list_user_name);
			userNameView.setText(listElement.getName());
		}
		return view;
	}
}

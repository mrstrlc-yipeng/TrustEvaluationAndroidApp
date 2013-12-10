package fr.utt.isi.tx.trustevaluationandroidapp.adapters;

import java.util.List;

import org.brickred.customadapter.ImageLoader;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import fr.utt.isi.tx.trustevaluationandroidapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SocialAuthContactListAdapter extends ArrayAdapter<Contact> {

	private Context context;
	
	private Provider provider;
	
	private List<Contact> contacts;
	
	private ImageLoader imageLoader;

	public SocialAuthContactListAdapter(Context context, int textViewResourceId,
			List<Contact> contacts) {
		super(context, textViewResourceId, contacts);

		this.context = context;
		this.contacts = contacts;
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.twitter_friend_list, null);
		}

		Contact listElement = contacts.get(position);
		if (listElement != null) {
			// profile image
			ImageView i = (ImageView) view.findViewById(R.id.contact_image);
			imageLoader.DisplayImage(listElement.getProfileImageURL(), i);

			// profile full name
			TextView t = (TextView) view.findViewById(R.id.contact_name);
			String name;
			if (provider == Provider.TWITTER) {
				name = listElement.getFirstName();
			} else if (provider == Provider.LINKEDIN) {
				name = listElement.getFirstName() + " " + listElement.getLastName();
			} else {
				name = listElement.getDisplayName();
			}
			t.setText(name);
		}

		return view;
	}
	
	public Provider getProvider() {
		return provider;
	}
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
}

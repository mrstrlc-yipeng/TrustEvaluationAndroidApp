package fr.utt.isi.tx.trustevaluationandroidapp.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import fr.utt.isi.tx.trustevaluationandroidapp.R;
import fr.utt.isi.tx.trustevaluationandroidapp.models.LocalContact;

public class LocalContactListAdapter extends ArrayAdapter<LocalContact> {

	// context
	private Context context;

	// ContactUser list object
	private List<LocalContact> contacts;

	public LocalContactListAdapter(Context context, int resourceId,
			List<LocalContact> contacts) {
		super(context, resourceId, contacts);
		this.context = context;
		this.contacts = contacts;

		for (int i = 0; i < contacts.size(); i++) {
			contacts.get(i).setAdapter(this);
		}
	}
	
	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			// inflate customized layout
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.local_contact_list, null);
		}

		// get object
		LocalContact contact = contacts.get(position);
		if (contact != null) {
			// quick contact badge possible to modify the contact
			QuickContactBadge quickContactBadge = (QuickContactBadge) view
					.findViewById(R.id.quick_contact_badge);

			if (quickContactBadge != null && contact.getContactUri() != null) {
				// assign the badge by local contact uri
				quickContactBadge.assignContactUri(contact.getContactUri());
			}

			// text view to show display_name
			TextView viewDisplayName = (TextView) view
					.findViewById(R.id.contact_display_name);

			if (viewDisplayName != null && contact.getDisplayName() != null) {
				// show display_name
				String displayName = contact.getDisplayName();
				viewDisplayName.setText(displayName);
			}

		}

		return view;
	}
}

package fr.utt.isi.tx.trustevaluationandroidapp.localcontact;

import java.util.List;

import fr.utt.isi.tx.trustevaluationandroidapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactArrayAdapter extends ArrayAdapter<ContactUser> {

	// context
	private Context context;

	// ContactUser list object
	private List<ContactUser> contacts;

	public ContactArrayAdapter(Context context, int resourceId,
			List<ContactUser> contacts) {
		super(context, resourceId, contacts);
		this.context = context;
		this.contacts = contacts;

		for (int i = 0; i < contacts.size(); i++) {
			contacts.get(i).setAdapter(this);
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			// inflate customized layout
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.local_contact_list, null);
		}

		// get object
		ContactUser contact = contacts.get(position);
		if (contact != null) {
			// text view to show display_name
			TextView viewDisplayName = (TextView) view
					.findViewById(R.id.contact_display_name);

			if (viewDisplayName != null) {
				// show display_name
				viewDisplayName.setText(cleanDisplayName(contact
						.getDisplayName()));
			}

		}

		return view;
	}

	private String cleanDisplayName(String displayName) {
		// clean display_name if its format is email
		if (displayName.contains("@")) {
			String[] split1 = displayName.split("@");

			// reform to "firstname lastname" format if it contains a "."
			if (split1[0].contains(".")) {
				String[] split2 = split1[0].split("\\.");
				return split2[0] + " " + split2[1];
			} else {
				return split1[0];
			}

		} else {
			return displayName;
		}
	}
}

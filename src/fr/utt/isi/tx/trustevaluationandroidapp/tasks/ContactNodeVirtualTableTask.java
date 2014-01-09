package fr.utt.isi.tx.trustevaluationandroidapp.tasks;

import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationContactNode;
import android.content.Context;
import android.os.AsyncTask;

public class ContactNodeVirtualTableTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	
	private TrustEvaluationContactNode contactNodeHelper;

	public ContactNodeVirtualTableTask(Context context) {
		super();
		
		this.context = context;
		contactNodeHelper = new TrustEvaluationContactNode(
				this.context);
	}

	@Override
	protected Void doInBackground(Void... params) {
		// drop the older virtual table
		//contactNodeHelper.dropVirtualFTSTableForSearch();

		// create the virtual table of contact node table
		contactNodeHelper.createVirtualFTSTableForSearch();
		return null;
	}

}

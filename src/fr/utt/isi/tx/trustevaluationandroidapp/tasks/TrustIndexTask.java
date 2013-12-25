package fr.utt.isi.tx.trustevaluationandroidapp.tasks;

import fr.utt.isi.tx.trustevaluationandroidapp.database.TrustEvaluationDbHelper;
import android.content.Context;
import android.os.AsyncTask;

public class TrustIndexTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;

	private TrustEvaluationDbHelper mDbHelper;

	public TrustIndexTask(Context context) {
		super();
		
		this.context = context;
		mDbHelper = new TrustEvaluationDbHelper(this.context);
	}

	@Override
	protected Void doInBackground(Void... params) {
		mDbHelper.calculateTrustIndex();
		return null;
	}

}

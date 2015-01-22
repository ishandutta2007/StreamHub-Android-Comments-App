package com.livefyre.comments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class BaseActivity extends ActionBarActivity {

	public static final String TAG = BaseActivity.class.getSimpleName();

    protected   LFCApplication application = AppSingleton.getInstance().getApplication();

	private AlertDialog alertDialog;
	private ProgressDialog dialog;

    //application object
    protected LFCApplication lfcApplication = AppSingleton.getInstance().getApplication();

	protected void showProgressDialog(String message) {

        dialog = new ProgressDialog(this);
		dialog.setMessage(message);
		dialog.setCancelable(false);
		dialog.show();

	}

	protected void dismissProgressDialog() {
		try {
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	protected void showAlert(String alertMsg, boolean type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("");
		builder.setMessage(alertMsg);
		builder.setCancelable(false);
		if (type) {
			builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
		} else {
			builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					alertDialog.dismiss();
				}
			});
		}

		alertDialog = builder.create();
		alertDialog.show();
	}

	protected void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	protected boolean isNetworkAvailable() {
		ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true) {
			return true;
		} else {
			return false;
		}
	}
}
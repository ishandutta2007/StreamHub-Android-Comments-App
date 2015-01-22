package com.livefyre.comments;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class LFCApplication extends Application {

	private SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	@Override
	public void onCreate() {
		super.onCreate();

		AppSingleton.getInstance().setApplication(this);
		init();
	}

	private void init() {
		sharedPreferences = getApplicationContext().getSharedPreferences(
				LFSAppConstants.SHARED_PREFERENCES, MODE_PRIVATE);
	}

	public void putDataInSharedPref(String key, String value) {
		(sharedPreferences.edit()).putString(key, value).commit();
	}

	public String getDataFromSharedPrefs(String key, String defaultVal) {
		return sharedPreferences.getString(key, defaultVal);
	}

	public void clearDataInSharedPref(String key) {
		(sharedPreferences.edit()).remove(key).commit();
	}

	public String getErrorStringFromResourceCode(int resourceCode) {
		return getResources().getText(resourceCode).toString();
	}

    public void printLog(boolean print,String tag,String value){
        if(print)
        Log.d(tag,value);
    }
}

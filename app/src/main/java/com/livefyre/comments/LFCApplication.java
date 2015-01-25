package com.livefyre.comments;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.livefyre.comments.models.ContentBean;

import java.util.ArrayList;
import java.util.HashMap;

public class LFCApplication extends Application {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ArrayList<ContentBean> contentCollection;

    @Override
    public void onCreate() {
        super.onCreate();

        AppSingleton.getInstance().setApplication(this);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences(
                LFSAppConstants.SHARED_PREFERENCES, MODE_PRIVATE);
        contentCollection = new ArrayList<>();
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

    public void printLog(boolean print, String tag, String value) {
        if (print)
            Log.d(tag, value);
    }

    public void addContent(ContentBean contentBean) {
        contentCollection.add(contentBean);
    }

    public ArrayList<ContentBean> getContentCollection() {
        return contentCollection;
    }
}

package com.livefyre.comments.activities;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.livefyre.comments.BaseActivity;
import com.livefyre.comments.LFSAppConstants;
import com.livefyre.comments.LFSConfig;
import com.livefyre.comments.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import livefyre.streamhub.AdminClient;
import livefyre.streamhub.BootstrapClient;

public class CommentsActivity extends BaseActivity {
    public static final String TAG = CommentsActivity.class.getSimpleName();


    Toolbar toolbar;

    private String adminClintId = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        pullViews();

        buildToolBar();

        adminClintCall();

    }

    private void buildToolBar() {
        //toolbar
        setSupportActionBar(toolbar);
        //disable title on toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //app icon
        toolbar.setNavigationIcon(R.drawable.flame);
    }

    private void pullViews() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
    }

    void adminClintCall() {
        if (!isNetworkAvailable()) {
            showToast("Network Not Available");
            return;
        } else {
            showProgressDialog();
        }
        try {
            AdminClient.authenticateUser(LFSConfig.USER_TOKEN,
                    LFSConfig.COLLECTION_ID, LFSConfig.ARTICLE_ID,
                    LFSConfig.SITE_ID, LFSConfig.NETWORK_ID,
                    new AdminCallback());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public class AdminCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject AdminClintJsonResponseObject) {
            JSONObject data;
            application.printLog(true,TAG+"-AdminCallback-onSuccess",AdminClintJsonResponseObject.toString());
            try {
                data = AdminClintJsonResponseObject.getJSONObject("data");

                if (!data.isNull("permissions")) {
                    JSONObject permissions = data.getJSONObject("permissions");
                    if (!permissions.isNull("moderator_key"))
                        application.putDataInSharedPref(
                                LFSAppConstants.ISMOD, "yes");
                    else {
                        application.putDataInSharedPref(
                                LFSAppConstants.ISMOD, "no");
                    }
                } else {
                    application.putDataInSharedPref(
                            LFSAppConstants.ISMOD, "no");
                }

                if (!data.isNull("profile")) {
                    JSONObject profile = data.getJSONObject("profile");

                    if (!profile.isNull("id")) {
                        application.putDataInSharedPref(
                                LFSAppConstants.ID, profile.getString("id"));
                        adminClintId = profile.getString("id");
                    }
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            bootstrapClientCall();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            // Log.d("adminClintCall", "Fail");
            application.printLog(true,TAG+"-AdminCallback-onFailure",error.toString());

            bootstrapClientCall();
        }

    }

    void bootstrapClientCall() {
        try {
            BootstrapClient.getInit(LFSConfig.NETWORK_ID, LFSConfig.SITE_ID,
                    LFSConfig.ARTICLE_ID, new InitCallback());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private class InitCallback extends JsonHttpResponseHandler {

        public void onSuccess(String data) {
            application.printLog(true,TAG+"-InitCallback-onSuccess",data.toString());
            try {
                buildReviewList(new JSONObject(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            application.printLog(true,TAG+"-InitCallback-onFailure",error.toString());
        }

    }
    void buildReviewList(JSONObject data) {
//        try {
//            content = new ContentParser(data);
//            content.getContentFromResponce(this);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

}

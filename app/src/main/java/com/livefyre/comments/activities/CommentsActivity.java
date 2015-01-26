package com.livefyre.comments.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.livefyre.comments.BaseActivity;
import com.livefyre.comments.LFSAppConstants;
import com.livefyre.comments.LFSConfig;
import com.livefyre.comments.R;
import com.livefyre.comments.adapter.CommentsAdapter;
import com.livefyre.comments.parsers.ContentParser;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import livefyre.streamhub.AdminClient;
import livefyre.streamhub.BootstrapClient;

public class CommentsActivity extends BaseActivity {
    public static final String TAG = CommentsActivity.class.getSimpleName();


    Toolbar toolbar;

    ListView commentsLV;

    ImageButton postNewCommentIv;

    private String adminClintId = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        pullViews();

        setListenersToViews();

        buildToolBar();

        adminClintCall();

    }

    private void setListenersToViews() {
        postNewCommentIv.setOnClickListener(postNewCommentListener);
        commentsLV.setOnItemClickListener(commentsLVListener);
    }

    private void buildToolBar() {


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        //toolbar
        setSupportActionBar(toolbar);
        //disable title on toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);

        TextView activityName = (TextView) findViewById(R.id.activityTitle);
        homeIcon.setBackgroundResource(R.drawable.flame);
        activityName.setText("Comments");

    }

    private void pullViews() {

        commentsLV = (ListView) findViewById(R.id.commentsLV);

        postNewCommentIv = (ImageButton) findViewById(R.id.postNewCommentIv);
    }

    void adminClintCall() {
        if (!isNetworkAvailable()) {
            showAlert("No connection available","TRY AGAIN",tryAgain);
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
            application.printLog(true, TAG + "-AdminCallback-onSuccess", AdminClintJsonResponseObject.toString());
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
            application.printLog(true, TAG + "-AdminCallback-onFailure", error.toString());

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
            application.printLog(false, TAG + "-InitCallback-onSuccess", data.toString());

            buildReviewList(data);

        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            application.printLog(true, TAG + "-InitCallback-onFailure", error.toString());
        }

    }

    void buildReviewList(String data) {

        ContentParser content = new ContentParser(data.toString());
        content.getContentFromResponse(this);
        CommentsAdapter mCommentsAdapter = new CommentsAdapter(this, application);
        commentsLV.setAdapter(mCommentsAdapter);
        dismissProgressDialog();
    }

    DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            adminClintCall();
        }
    };

    View.OnClickListener postNewCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(CommentsActivity.this, NewActivity.class);
            intent.putExtra(LFSAppConstants.PURPOSE,LFSAppConstants.NEW_COMMENT);
            startActivity(intent);
        }
    };
    OnItemClickListener commentsLVListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            Intent detailViewIntent = new Intent(CommentsActivity.this,CommentActivity.class);
            detailViewIntent.putExtra("position", position);
            startActivity(detailViewIntent);
        }
    };
}

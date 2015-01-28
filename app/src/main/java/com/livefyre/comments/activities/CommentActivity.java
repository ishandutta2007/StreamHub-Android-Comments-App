package com.livefyre.comments.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livefyre.comments.BaseActivity;
import com.livefyre.comments.LFSAppConstants;
import com.livefyre.comments.LFSConfig;
import com.livefyre.comments.LFUtils;
import com.livefyre.comments.R;
import com.livefyre.comments.models.ContentBean;
import com.livefyre.comments.parsers.ContentParser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.MalformedURLException;

import livefyre.streamhub.LFSActions;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.LFSFlag;
import livefyre.streamhub.WriteClient;

public class CommentActivity extends BaseActivity {
    Toolbar toolbar;
    TextView authorNameTv, postedDateOrTime, commentBody, moderatorTv, likesTv;

    LinearLayout featureLL, likeLL, newReplyLL;

    ImageView avatarIv, imageAttachedToCommentIv, moreIv;

    private String contentId;

    private ProgressDialog dialog;


    ContentBean comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);

        getDataFromIntent();

        pullViews();

        setData();

        setListenersToViews();

        buildToolBar();
    }

    private void setData() {
        ContentBean comment = ContentParser.ContentCollection.get(contentId);
        //Author Name
        authorNameTv.setText(comment.getAuthor().getDisplayName());
        //Posted Date
        postedDateOrTime.setText(LFUtils.getFormatedDate(
                comment.getCreatedAt(), LFSAppConstants.SHART));
        //Comment Body
        commentBody.setText(LFUtils.trimTrailingWhitespace(Html
                        .fromHtml(comment.getBodyHtml())),
                TextView.BufferType.SPANNABLE);


        Picasso.with(getApplicationContext()).load(comment.getAuthor().getAvatar()).fit()
                .into(avatarIv);
    }

    private void getDataFromIntent() {
        Intent in = getIntent();
        contentId = in.getStringExtra(LFSAppConstants.ID);
    }

    private void pullViews() {
        authorNameTv = (TextView) findViewById(R.id.authorNameTv);
        postedDateOrTime = (TextView) findViewById(R.id.postedDateOrTime);
        commentBody = (TextView) findViewById(R.id.commentBody);
        likesTv = (TextView) findViewById(R.id.likesTv);
        moderatorTv = (TextView) findViewById(R.id.moderatorTv);
        featureLL = (LinearLayout) findViewById(R.id.featureLL);
        newReplyLL = (LinearLayout) findViewById(R.id.newReplyLL);
        likeLL = (LinearLayout) findViewById(R.id.likeLL);
        avatarIv = (ImageView) findViewById(R.id.avatarIv);
        imageAttachedToCommentIv = (ImageView) findViewById(R.id.imageAttachedToCommentIv);
        moreIv = (ImageView) findViewById(R.id.moreIv);

    }

    private void setListenersToViews() {
        newReplyLL.setOnClickListener(newReplyLLListener);
        moreIv.setOnClickListener(moreListener);
    }

    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Activity Icon
        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
        homeIcon.setOnClickListener(homeIconListener);
        homeIcon.setBackgroundResource(R.drawable.back);

        //Activity Name
        TextView activityName = (TextView) findViewById(R.id.activityTitle);


        activityName.setText("Comment");

    }

    View.OnClickListener homeIconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    View.OnClickListener moreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moreDialog(ContentParser.ContentCollection.get(contentId).getId(), ContentParser.ContentCollection.get(contentId).getIsFeatured());
        }
    };
    View.OnClickListener helpfulListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    View.OnClickListener newReplyLLListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(CommentActivity.this, NewActivity.class);
            intent.putExtra(LFSAppConstants.PURPOSE, LFSAppConstants.NEW_REPLY);
            intent.putExtra(LFSAppConstants.ID, ContentParser.ContentCollection.get(contentId).getId());
            startActivity(intent);
        }
    };

    private void moreDialog(final String id, final Boolean isFeatured) {
        ContentBean mBean = ContentParser.ContentCollection.get(contentId);

        final Dialog dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.more);
        dialog.setCancelable(true);
        if (isFeatured) {
            ((TextView) dialog.findViewById(R.id.alreadyFeatured))
                    .setText("Unfeature");
        } else {
            ((TextView) dialog.findViewById(R.id.alreadyFeatured))
                    .setText("Feature");
        }
        LinearLayout emptyDialogSpace = (LinearLayout) dialog
                .findViewById(R.id.emptyDialogSpace);
        emptyDialogSpace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        LinearLayout edit = (LinearLayout) dialog.findViewById(R.id.edit);
        LinearLayout feature = (LinearLayout) dialog.findViewById(R.id.feature);
        LinearLayout flag = (LinearLayout) dialog.findViewById(R.id.flag);
        LinearLayout bozo = (LinearLayout) dialog.findViewById(R.id.bozo);
        LinearLayout banUser = (LinearLayout) dialog.findViewById(R.id.banUser);
        LinearLayout delete = (LinearLayout) dialog.findViewById(R.id.delete);

        View moreLine = dialog.findViewById(R.id.moreLine);

        if ("yes".equals(application.getDataFromSharedPrefs(LFSAppConstants.ISMOD, "")) && mBean.getIsModerator().equals("true")) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            feature.setVisibility(View.VISIBLE);
            moreLine.setVisibility(View.VISIBLE);

        } else if ("yes".equals(application.getDataFromSharedPrefs(LFSAppConstants.ISMOD, ""))) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            moreLine.setVisibility(View.VISIBLE);
            feature.setVisibility(View.VISIBLE);
            flag.setVisibility(View.VISIBLE);
            bozo.setVisibility(View.VISIBLE);
            banUser.setVisibility(View.VISIBLE);
            moreLine.setVisibility(View.VISIBLE);

        } else if (mBean.getAuthorId().equals(
                application.getDataFromSharedPrefs(LFSAppConstants.ID, ""))) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        } else {
            flag.setVisibility(View.VISIBLE);
        }
        if (mBean.getIsFeatured()) {
            flag.setVisibility(View.GONE);
        }
        //Edit
        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent replyView = new Intent(CommentActivity.this, NewActivity.class);
                replyView.putExtra("id", id);
                replyView.putExtra(LFSAppConstants.BODY, ContentParser.ContentCollection.get(contentId).getBodyHtml());
                replyView.putExtra(LFSAppConstants.PURPOSE, LFSAppConstants.EDIT);
                replyView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(replyView);
                dialog.dismiss();
            }
        });

        //Feature
        feature.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                if (isFeatured) {
                    try {
                        WriteClient.featureMessage("unfeature", id, LFSConfig.COLLECTION_ID, LFSConfig.USER_TOKEN,
                                null, new helpfulCallback());// same as helpful
                        // call back
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else {

                    try {
                        WriteClient.featureMessage("feature", id,
                                LFSConfig.COLLECTION_ID, LFSConfig.USER_TOKEN,
                                null, new helpfulCallback());// same as helpful
                        // call back
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                dialog.dismiss();

            }
        });

        flag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flagDialog(id);
                dialog.dismiss();
            }
        });

        //bozo
        bozo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                RequestParams perameters = new RequestParams();
                perameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                perameters.put("message_id", id);

                WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSActions.BOZO, perameters,
                        new actionCallback());
                dialog.dismiss();
            }
        });

//        banUser.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                showProgress();
//                RequestParams perameters = new RequestParams();
//                perameters.put("network", LFSConfig.NETWORK_ID);
//                perameters.put(LFSConstants.LFSPostUserTokenKey,
//                        LFSConfig.USER_TOKEN);
//                perameters.put("retroactive", "0");
//                WriteClient.flagAuthor(ContentParser.ContentCollection.get(id)
//                                .getAuthorId(), LFSConfig.USER_TOKEN, perameters,
//                        new actionCallback());
//
//                dialog.dismiss();
//
//            }
//        });

        //delete
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSActions.DELETE, parameters,
                        new actionCallback());

                dialog.dismiss();

            }
        });
        if (edit.getVisibility() == View.GONE
                && feature.getVisibility() == View.GONE
                && delete.getVisibility() == View.GONE
                && banUser.getVisibility() == View.GONE
                && bozo.getVisibility() == View.GONE
                && flag.getVisibility() == View.GONE) {

        } else {
            dialog.show();
        }
    }

    private void flagDialog(final String id) {
        final Dialog dialog = new Dialog(CommentActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setTitle("");
        dialog.setContentView(R.layout.flag);
        dialog.setCancelable(true);
        LinearLayout emptyDialogSpace = (LinearLayout) dialog
                .findViewById(R.id.emptyDialogSpace);
        emptyDialogSpace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        LinearLayout spam = (LinearLayout) dialog.findViewById(R.id.spam);
        spam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RequestParams perameters = new RequestParams();
                perameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                perameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.SPAM, perameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        LinearLayout offensive = (LinearLayout) dialog.findViewById(R.id.offensive);
        offensive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams perameters = new RequestParams();
                perameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                perameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.OFFENSIVE, perameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        LinearLayout offtopic = (LinearLayout) dialog.findViewById(R.id.offTopic);
        offtopic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams perameters = new RequestParams();
                perameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                perameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.OFF_TOPIC, perameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        LinearLayout disagree = (LinearLayout) dialog.findViewById(R.id.disagree);
        disagree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams perameters = new RequestParams();
                perameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                perameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.DISAGREE, perameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private class actionCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject responce) {
            Log.d("action ClientCall", "success" + responce);
            if (!responce.isNull("data")) {
                dismissProgress();
                showAlert("Comment Deleted Successfully", "OK", null);
            }
            if (dialog.isShowing())
                dismissProgress();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgress();
            Log.d("action ClientCall", error + "");
            showToast("Something went wrong.");
        }

    }

    private class helpfulCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject data) {
            dismissProgress();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgress();
            showToast("Something went wrong.");

        }

    }

    private class flagCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject data) {
            showToast("Content flagged successfully");

        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgress();
            showToast("Something went wrong.");

        }

    }

    protected void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait." + "\n"
                + "Your request is being processed..");

        dialog.setCancelable(false);
        dialog.show();
    }

    protected void dismissProgress() {
        try {
            dialog.dismiss();
        } catch (Exception e) {

        }
    }


}

package com.livefyre.comments.activities;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.livefyre.comments.RoundedTransformation;
import com.livefyre.comments.models.ContentBean;
import com.livefyre.comments.models.Vote;
import com.livefyre.comments.parsers.ContentParser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.List;

import livefyre.streamhub.LFSActions;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.LFSFlag;
import livefyre.streamhub.WriteClient;

public class CommentActivity extends BaseActivity {
    Toolbar toolbar;
    TextView authorNameTv, postedDateOrTime, commentBody, moderatorTv, likesTv,likeCountTv;

    LinearLayout featureLL, likeLL, newReplyLL;

    ImageView avatarIv, imageAttachedToCommentIv, moreIv,likeIv;

    private String contentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);

        getDataFromIntent();

        pullViews();

        populateData();

        setListenersToViews();

        buildToolBar();
    }


    View.OnClickListener likeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showProgressDialog();

           int HFVal=knowHelpfulValue(
                    application
                            .getDataFromSharedPrefs(LFSAppConstants.ID, ""),
                    ContentParser.ContentCollection.get(contentId).getVote());


            if (HFVal == 1) {
                    RequestParams parameters = new RequestParams();
                    parameters.put("value", "0");
                    parameters.put(LFSConstants.LFSPostUserTokenKey,
                            LFSConfig.USER_TOKEN);
                    parameters.put("message_id", ContentParser.ContentCollection.get(contentId).getId());

                    WriteClient.postAction(LFSConfig.COLLECTION_ID, ContentParser.ContentCollection.get(contentId).getId(),
                            LFSConfig.USER_TOKEN, LFSActions.VOTE, parameters,
                            new helpfulCallback());
                } else {
                    RequestParams parameters = new RequestParams();
                    parameters.put("value", "1");
                    parameters.put(LFSConstants.LFSPostUserTokenKey,
                            LFSConfig.USER_TOKEN);
                    parameters.put("message_id", ContentParser.ContentCollection.get(contentId).getId());

                    WriteClient.postAction(LFSConfig.COLLECTION_ID, ContentParser.ContentCollection.get(contentId).getId(),
                            LFSConfig.USER_TOKEN, LFSActions.VOTE, parameters,
                            new helpfulCallback());

        }}
    };

    int knowHelpfulValue(String authorId, List<Vote> v) {

        int helpfulValue = 0;
        if (v != null)
            for (int i = 0; i < v.size(); i++) {
                if (v.get(i).getAuthor().equals(authorId)) { // helpful or not
                    // helpful

                    if (v.get(i).getValue().equals("1"))
                        helpfulValue = 1;
                    else
                        helpfulValue = 2;
                    break;
                }
            }

        return helpfulValue;
    }



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
                showProgressDialog();
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
                dialog.dismiss();
                flagDialog(ContentParser.ContentCollection.get(contentId).getId());

            }
        });

        //bozo
        bozo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgressDialog();
                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSActions.BOZO, parameters,
                        new actionCallback());
                dialog.dismiss();
            }
        });

        banUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgressDialog();
                RequestParams parameters = new RequestParams();
                parameters.put("network", LFSConfig.NETWORK_ID);
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("retroactive", "0");
                WriteClient.flagAuthor(ContentParser.ContentCollection.get(id)
                                .getAuthorId(), LFSConfig.USER_TOKEN, parameters,
                        new actionCallback());

                dialog.dismiss();

            }
        });

        //delete
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgressDialog();
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
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
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
                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.SPAM, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        LinearLayout offensive = (LinearLayout) dialog.findViewById(R.id.offensive);
        offensive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.OFFENSIVE, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        LinearLayout offtopic = (LinearLayout) dialog.findViewById(R.id.offTopic);
        offtopic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.OFF_TOPIC, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        LinearLayout disagree = (LinearLayout) dialog.findViewById(R.id.disagree);
        disagree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.DISAGREE, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        dialog.show();
    }





    View.OnClickListener moreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moreDialog(ContentParser.ContentCollection.get(contentId).getId(), ContentParser.ContentCollection.get(contentId).getIsFeatured());
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

    View.OnClickListener homeIconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    //Call backs

    private class actionCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject responce) {
            Log.d("action ClientCall", "success" + responce);
                dismissProgressDialog();
            if (!responce.isNull("data")) {
                dismissProgressDialog();
                showAlert("Comment Deleted Successfully", "OK", null);
            }

        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgressDialog();
            Log.d("action ClientCall", error + "");
            showToast("Something went wrong.");
        }

    }

    private class helpfulCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject data) {
            dismissProgressDialog();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgressDialog();
            showToast("Something went wrong.");

        }

    }

    private class flagCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject data) {
            customToast();

        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
           dismissProgressDialog();
            if(error!=null)
                showToast(error.toString());
                else
            showToast("Something went wrong.");

        }

    }

    private void populateData() {
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

        Picasso.with(getApplicationContext()).load(comment.getAuthor().getAvatar()).fit().transform(new RoundedTransformation(90, 0)).into(avatarIv);

        if (comment.getOembedUrl() != null) {
            if (comment.getOembedUrl().length() > 0) {
                imageAttachedToCommentIv.setVisibility(View.VISIBLE);
                application.printLog(true,"comment.getOembedUrl()",comment.getOembedUrl()+" URL");
                Picasso.with(getApplication()).load(comment.getOembedUrl()).fit().into(imageAttachedToCommentIv);
            } else {
                imageAttachedToCommentIv.setVisibility(View.GONE);
            }
        } else {
           imageAttachedToCommentIv.setVisibility(View.GONE);
        }


        if (comment.getVote() != null) {// know helpful value and set color

            if (comment.getVote().size() > 0) {
               int  helpfulFlag = 0;

                helpfulFlag = knowHelpfulValue(
                        application
                                .getDataFromSharedPrefs(LFSAppConstants.ID,""),
                        comment.getVote());



                if (helpfulFlag == 1) {
                    likeIv
                            .setImageResource(R.drawable.like);
                    likeCountTv.setTextColor(Color
                            .parseColor("#e85b3f"));
                } else if (helpfulFlag == 2) {
                    likeIv
                            .setImageResource(R.drawable.unlike);
                    likeCountTv.setTextColor(Color
                            .parseColor("#757575"));
                } else {
                    likeIv
                            .setImageResource(R.drawable.unlike);
                    likeCountTv.setTextColor(Color
                            .parseColor("#757575"));
                }

                likeCountTv.setText(comment.getVote().size()+"");

            } else {
                likeIv
                        .setImageResource(R.drawable.unlike);
                likeCountTv.setTextColor(Color
                        .parseColor("#757575"));
                likeCountTv.setText("0");
            }

        } else {
            likeIv
                    .setImageResource(R.drawable.unlike);
            likeCountTv
                    .setTextColor(Color.parseColor("#757575"));
            likeCountTv.setText("0");
        }
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
        likeCountTv = (TextView) findViewById(R.id.likesCountTv);
        featureLL = (LinearLayout) findViewById(R.id.featureLL);
        newReplyLL = (LinearLayout) findViewById(R.id.newReplyLL);
        likeLL = (LinearLayout) findViewById(R.id.likeLL);
        avatarIv = (ImageView) findViewById(R.id.avatarIv);
        likeIv = (ImageView) findViewById(R.id.likeIv);
        imageAttachedToCommentIv = (ImageView) findViewById(R.id.imageAttachedToCommentIv);
        moreIv = (ImageView) findViewById(R.id.moreIv);
        LinearLayout activityIconLL= (LinearLayout) findViewById(R.id.activityIconLL);
        activityIconLL.setOnClickListener(homeIconListener);
    }

    private void setListenersToViews() {
        newReplyLL.setOnClickListener(newReplyLLListener);
        moreIv.setOnClickListener(moreListener);
        likeLL.setOnClickListener(likeListener);
    }

    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Activity Icon
        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
        homeIcon.setBackgroundResource(R.drawable.arrow_left);

        LinearLayout activityIconLL = (LinearLayout) findViewById(R.id.activityIconLL);
        activityIconLL.setOnClickListener(homeIconListener);

        //Activity Name
        TextView activityName = (TextView) findViewById(R.id.activityTitle);
        activityName.setText("Comment");

    }

}

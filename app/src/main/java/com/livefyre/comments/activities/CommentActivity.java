package com.livefyre.comments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livefyre.comments.BaseActivity;
import com.livefyre.comments.LFSAppConstants;
import com.livefyre.comments.LFUtils;
import com.livefyre.comments.R;
import com.livefyre.comments.models.ContentBean;

public class CommentActivity extends BaseActivity {
    Toolbar toolbar;
    TextView authorNameTv, postedDateOrTime, commentBody, moderatorTv, likesTv;

    LinearLayout featureLL;

    ImageView avatarIv, imageAttachedToCommentIv;

    private int position;

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
    private void setData(){
         ContentBean comment = application.getContentCollection().get(position);
        //Author Name
        authorNameTv.setText(comment.getAuthor().getDisplayName());
        //Posted Date
       postedDateOrTime.setText(LFUtils.getFormatedDate(
                comment.getCreatedAt(), LFSAppConstants.SHART));
        //Comment Body
        commentBody.setText(LFUtils.trimTrailingWhitespace(Html
                        .fromHtml(comment.getBodyHtml())),
                TextView.BufferType.SPANNABLE);

    }
    private void getDataFromIntent(){
        Intent in=getIntent();
        position=in.getIntExtra("position",-1);
    }
    private void pullViews() {
        authorNameTv = (TextView) findViewById(R.id.authorNameTv);
        postedDateOrTime = (TextView) findViewById(R.id.postedDateOrTime);
        commentBody = (TextView) findViewById(R.id.commentBody);
        likesTv = (TextView) findViewById(R.id.likesTv);
        moderatorTv = (TextView) findViewById(R.id.moderatorTv);
        featureLL = (LinearLayout) findViewById(R.id.featureLL);

        avatarIv = (ImageView) findViewById(R.id.avatarIv);
        imageAttachedToCommentIv = (ImageView) findViewById(R.id.imageAttachedToCommentIv);

    }

    private void setListenersToViews() {
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


}

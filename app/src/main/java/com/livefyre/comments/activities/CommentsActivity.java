package com.livefyre.comments.activities;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.livefyre.comments.BaseActivity;
import com.livefyre.comments.R;

public class CommentsActivity extends BaseActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);
        //toolbar
        toolbar= (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //disable title on toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //app icon
        toolbar.setNavigationIcon(R.drawable.flame);
    }

}

package com.livefyre.comments.activities;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comments_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_post) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

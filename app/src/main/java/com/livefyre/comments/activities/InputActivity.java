package com.livefyre.comments.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filepicker.sdk.FilePicker;
import com.filepicker.sdk.FilePickerAPI;
import com.livefyre.comments.BaseActivity;
import com.livefyre.comments.LFSConfig;
import com.livefyre.comments.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;

import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.WriteClient;

public class InputActivity extends BaseActivity {
    Toolbar toolbar;
    TextView commentTv;
    LinearLayout attachImageLL;
    FrameLayout attacheImageFL;
    ImageView capturedImage;
    RelativeLayout deleteCapturedImage;
    JSONObject imgObj;
    String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_activity);


        pullViews();

        setListenersToViews();

        buildToolBar();
    }


    private void pullViews() {
        attachImageLL = (LinearLayout) findViewById(R.id.attachImageLL);
        attacheImageFL = (FrameLayout) findViewById(R.id.attacheImageFL);
        capturedImage = (ImageView) findViewById(R.id.capturedImage);
        deleteCapturedImage = (RelativeLayout) findViewById(R.id.deleteCapturedImage);
        commentTv = (TextView) findViewById(R.id.commentTv);
    }

    private void setListenersToViews() {
        attachImageLL.setOnClickListener(attachImageLLListener);
        deleteCapturedImage.setOnClickListener(deleteCapturedImageListener);
    }


    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Activity Icon
        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
        homeIcon.setOnClickListener(homeIconListener);
        homeIcon.setBackgroundResource(R.drawable.close);

        //Activity Name
        TextView activityName = (TextView) findViewById(R.id.activityTitle);
        activityName.setText("New Comment");

        //Action
        TextView actionTv = (TextView) findViewById(R.id.actionTv);
        actionTv.setVisibility(View.VISIBLE);
        actionTv.setText("POST");
        actionTv.setOnClickListener(actionTvListener);

    }

    View.OnClickListener attachImageLLListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(InputActivity.this, FilePicker.class);
            FilePickerAPI.setKey(LFSConfig.FILEPICKER_API_KEY);

            startActivityForResult(intent, FilePickerAPI.REQUEST_CODE_GETFILE);
        }
    };

    View.OnClickListener actionTvListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String description = commentTv.getText().toString();
            if (description.length() == 0) {
                showAlert("Please Enter Description.", tryAgain);
                return;
            }
            String descriptionHTML = Html.toHtml((android.text.Spanned) commentTv.getText());
            postNewComment(descriptionHTML);

        }
    };

    void postNewComment(String body) {
        if (!isNetworkAvailable()) {
            showToast("Network Not Available");
            return;
        }
        showProgressDialog();
        HashMap<String, Object> perameters = new HashMap<>();
        perameters.put(LFSConstants.LFSPostBodyKey, body);
        perameters.put(LFSConstants.LFSPostType,
                LFSConstants.LFSPostTypeComment);
        perameters.put(LFSConstants.LFSPostUserTokenKey, LFSConfig.USER_TOKEN);
        if (imgObj != null)
            perameters.put(LFSConstants.LFSPostAttachment,
                    (new JSONArray().put(imgObj)).toString());
        try {
            WriteClient.postContent(LFSConfig.NETWORK_ID,
                    LFSConfig.COLLECTION_ID, null, LFSConfig.USER_TOKEN,
                    perameters, new writeclientCallback());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public class writeclientCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject data) {
            dismissProgressDialog();
            showAlert("Comment Posted Successfully.", tryAgain);
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgressDialog();
            Log.d("data error", "" + content);

            try {
                JSONObject errorJson = new JSONObject(content);
                if (!errorJson.isNull("msg")) {

                    showAlert(errorJson.getString("msg"), tryAgain);
                } else {
                    showAlert("Something went wrong.", tryAgain);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", tryAgain);

            }

        }
    }

    View.OnClickListener homeIconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    View.OnClickListener deleteCapturedImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            attachImageLL.setVisibility(View.VISIBLE);
            attacheImageFL.setVisibility(View.GONE);
        }
    };

    DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE) {
            if (resultCode != RESULT_OK) {
                // Result was cancelled by the user or there was an error
                showAlert("Something Went Wrong.", tryAgain);
                attachImageLL.setVisibility(View.VISIBLE);
                attacheImageFL.setVisibility(View.GONE);
                return;
            }

            attachImageLL.setVisibility(View.GONE);
            attacheImageFL.setVisibility(View.VISIBLE);

            String imgUrl = data.getExtras().getString("fpurl");
            Log.d("url", imgUrl + "");

            try {

                imgObj = new JSONObject();
                imgObj.put("link", imgUrl);
                imgObj.put("provider_name", "LivefyreFilePicker");
                imgObj.put("thumbnail_url", imgUrl);
                imgObj.put("type", "photo");
                imgObj.put("url", imgUrl);
                try {
                    Picasso.with(getBaseContext()).load(imgUrl).fit()
                            .into(capturedImage);
                } catch (Exception e) {

                }


            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
    }
}

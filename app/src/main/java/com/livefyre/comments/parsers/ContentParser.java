package com.livefyre.comments.parsers;

import com.livefyre.comments.AppSingleton;
import com.livefyre.comments.LFCApplication;
import com.livefyre.comments.activities.CommentsActivity;
import com.livefyre.comments.models.AuthorsBean;
import com.livefyre.comments.models.CommentStatus;
import com.livefyre.comments.models.ContentBean;
import com.livefyre.comments.models.ContentTypeEnum;
import com.livefyre.comments.models.Vote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kvanadev5 on 25/01/15.
 */
public class ContentParser {
    private LFCApplication application = AppSingleton.getInstance().getApplication();
    String contentCollectionString;
    JSONObject contentCollectionJSONObject;
    private static HashMap<String, Object> authorsCollection;

    public ContentParser(String contentCollectionString) {
        this.contentCollectionString = contentCollectionString;
    }

    public void getContentFromResponse(CommentsActivity commentsActivity) {
        try {
            // String to JSON
            contentCollectionJSONObject = new JSONObject(contentCollectionString);

            // headDocument
            JSONObject headDocument = contentCollectionJSONObject.getJSONObject("headDocument");

            // Collecting Authors
            authorCollection(headDocument.getJSONObject("authors"));

            // Collecting Content
            JSONArray mainContent = headDocument.getJSONArray("content");

            contentCollection(mainContent);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void contentCollection(JSONArray mainContent) {


        try {

            for (int mainContentCount = 0; mainContentCount < mainContent.length(); mainContentCount++) {

                ContentBean contentBean = new ContentBean();

                JSONObject commentJSONObj = (JSONObject) mainContent.get(mainContentCount);

                JSONObject commentContentJSONObj = commentJSONObj.getJSONObject("content");

                JSONObject annotations;
                if (!commentContentJSONObj.isNull("annotations")) {
                    annotations = commentContentJSONObj.getJSONObject("annotations");
                    if (!annotations.isNull("featuredmessage")) {
                        contentBean.setIsFeatured(true);
                    }
                    if (!annotations.isNull("moderator")) {
                        contentBean.setIsModerator(annotations
                                .getString("moderator"));

                    }


                    if (!annotations.isNull("vote")) {
                        JSONArray vote = annotations.getJSONArray("vote");
                        List<Vote> votes = new ArrayList<>();

                        for (int j = 0; j < vote.length(); j++) {
                            JSONObject voteJson = vote.getJSONObject(j);
                            Vote v = new Vote();
                            if (!voteJson.isNull("value")
                                    && !voteJson.isNull("author")) {
                                v.setValue(voteJson.getString("value"));

                                v.setAuthor(voteJson.getString("author"));
                                votes.add(v);
                            }
                        }
                        contentBean.setVote(votes);

                        int count = 0;
                        for (int i = 0; i < votes.size(); i++) {
                            if (votes.get(i).getValue().equals("1"))
                                count++;
                        }

                        contentBean.setHelpfulcount(count);

                    }
                }

                if (!commentJSONObj.isNull("vis")) {
                    contentBean.setVisibility(commentJSONObj.getString("vis"));
                    if (contentBean.getVisibility().equals("1"))
                        contentBean.setReviewStatus(CommentStatus.NOT_DELETED);
                    else
                        contentBean.setReviewStatus(CommentStatus.DELETED);
                }

                if (!commentContentJSONObj.isNull("ancestorId"))
                    contentBean.setAncestorId(commentContentJSONObj.getString("ancestorId"));

                if (!commentContentJSONObj.isNull("bodyHtml"))
                    contentBean.setBodyHtml(commentContentJSONObj.getString("bodyHtml"));
                else
                    contentBean.setBodyHtml("");

                if (!commentContentJSONObj.isNull("id"))
                    contentBean.setId(commentContentJSONObj.getString("id"));

                if (!commentContentJSONObj.isNull(""))
                    contentBean.setAuthorId(commentContentJSONObj.getString("authorId"));

                if (!commentContentJSONObj.isNull("parentId")) {
                    contentBean.setParentId(commentContentJSONObj.getString("parentId"));

                    if (commentContentJSONObj.getString("parentId").equals(""))
                        contentBean.setContentType(ContentTypeEnum.PARENT);
                    else
                        contentBean.setContentType(ContentTypeEnum.CHILD);
                }
                if (!commentJSONObj.getString("vis").equals("1"))
                    contentBean.setContentType(ContentTypeEnum.DELETED);


//            if (!mainContent.isNull("event")) {
//                contentBean.setEvent(mainContent.getString("event"));
//                lastEvent = contentBean.getEvent();
//            }

                if (!commentJSONObj.isNull("type"))
                    contentBean.setType(commentJSONObj.getString("type"));

                if (!commentContentJSONObj.isNull("createdAt"))
                    contentBean.setCreatedAt(commentContentJSONObj.getString("createdAt"));

                if (!commentContentJSONObj.isNull("updatedAt"))
                    contentBean.setUpdatedAt(commentContentJSONObj.getString("updatedAt"));

                if (!commentContentJSONObj.isNull("authorId")) {
                    contentBean.setAuthor((AuthorsBean) authorsCollection
                            .get(commentContentJSONObj.getString("authorId")));

                }
//                if (!commentJSONObj.isNull("childContent")) {
//                    contentBean.setChildContent(mainContent
//                            .getJSONArray("childContent"));
//
//                }
//            if (from.equals("stream")) {
//                contentBean.setFrom("stream");
//            }
//            contentBean.setDepth(depthValue);

                application.addContent(contentBean);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void authorCollection(JSONObject authorsJson) {
        if (authorsCollection == null)
            authorsCollection = new HashMap<>();
        @SuppressWarnings("rawtypes")
        Iterator keys = authorsJson.keys();
        while (keys.hasNext()) {
            String authorId = (String) keys.next();

            AuthorsBean author = new AuthorsBean();
            try {
                if (!authorsJson.getJSONObject(authorId).isNull("avatar"))
                    author.setAvatar(authorsJson.getJSONObject(authorId)
                            .getString("avatar"));
                if (!authorsJson.getJSONObject(authorId).isNull("displayName"))
                    author.setDisplayName(authorsJson.getJSONObject(authorId)
                            .getString("displayName"));
                if (!authorsJson.getJSONObject(authorId).isNull("profileUrl"))
                    author.setProfileUrl(authorsJson.getJSONObject(authorId)
                            .getString("profileUrl"));
                if (!authorsJson.getJSONObject(authorId).isNull("type"))
                    author.setType(authorsJson.getJSONObject(authorId)
                            .getString("type"));
                if (!authorsJson.getJSONObject(authorId).isNull("id"))
                    author.setId(authorsJson.getJSONObject(authorId).getString(
                            "id"));

            } catch (Exception e) {
                e.printStackTrace();
            }

            authorsCollection.put(authorId, author);
        }
    }

}

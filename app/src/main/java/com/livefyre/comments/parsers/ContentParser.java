package com.livefyre.comments.parsers;

import android.util.Log;

import com.livefyre.comments.listeners.ContentUpdateListener;
import com.livefyre.comments.models.AuthorsBean;
import com.livefyre.comments.models.CommentStatus;
import com.livefyre.comments.models.ContentBean;
import com.livefyre.comments.models.ContentTypeEnum;
import com.livefyre.comments.models.Vote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContentParser {

    private JSONObject jsonResponseObject;
    private static HashMap<String, Object> authorsCollection;
    public static HashMap<String, ContentBean> ContentCollection;
    private static List<ContentBean> childs;
    private static int depth = 0;
    int i = 0;
    int visibilityCount = 0;
    public static String lastEvent = "0";

    public ContentParser(JSONObject jsonResponseObject) {
        this.jsonResponseObject = jsonResponseObject;
    }

    private ContentUpdateListener l1;
    private static int countFlag = 0;
    public void getContentFromResponse(ContentUpdateListener l1)
            throws JSONException {
        this.l1 = l1;
        ContentCollection = new HashMap();
        // Collecting Authors
        JSONObject headDocument = jsonResponseObject
                .getJSONObject("headDocument");
        lastEvent = headDocument.getString("event");

        authorCollection(headDocument.getJSONObject("authors"));
        // Collecting Childes
        JSONArray contentArray = headDocument.getJSONArray("content");
        addChild(contentArray);

        if (ContentCollection.size() > 0) {
            for (ContentBean t : ContentCollection.values()) {
//                if (!t.getParentId().equals("")) {
//                    if ((ContentCollection.get(t.getParentId())) != null) {
                        ContentBean bean = ContentCollection.get(t
                                .getParentId());
                        if (bean != null) {
                            List<String> list;
                            if (bean.getChildBeanContent() != null) {
                                list = bean.getChildBeanContent();
                                list.add(list.size(), t.getId());
                            } else {
                                list = new ArrayList();
                                list.add(t.getId());
                            }
                            bean.setChildBeanContent(list);
                        }

//                    }
//                }
            }
        }

        for (int i = 0; i < contentArray.length(); i++) {
            JSONObject t = (JSONObject) contentArray.get(i);
            if (t.has("content"))
                if (t.getJSONObject("content").getString("parentId").equals("")
                        && t.getString("vis").equals("1")) {
                    visibilityCount = 0;
                    int visCount = visibilityCountForContent((new JSONArray())
                            .put(t));
                    ContentBean bean = ContentCollection.get(t.getJSONObject(
                            "content").getString("id"));
                    bean.setVisibilityCount(visCount - 1);
                }
        }

    }

    public int visibilityCountForContent(JSONArray ja) {
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo;
            try {
                jo = (JSONObject) ja.get(i);
                if (jo.has("vis") && jo.has("content")) {
                    if (jo.getString("vis").equals("1")
                            && !jo.getJSONObject("content").has("oembed")) {
                        visibilityCount++;
                    }
                }
                if (jo.has("childContent")) {
                    visibilityCountForContent(jo.getJSONArray("childContent"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return visibilityCount;
    }

    public static List<ContentBean> getChildContentForReview(String contentId) {

        childs = new ArrayList();
        depth = 0;
        if (ContentCollection.get(contentId) != null) {
            if (ContentCollection.get(contentId).getChildBeanContent() != null)
                getChildsForReview(ContentCollection.get(contentId)
                        .getChildBeanContent());
        } else {
            Log.d("Child", "No child");
        }

        return childs;

    }

    private static void getChildsForReview(List<String> ja) {

        Collections.sort(ja, new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                ContentBean b1 = ContentCollection.get(p1);
                ContentBean b2 = ContentCollection.get(p2);

                return Integer.parseInt(b2.getCreatedAt())
                        - Integer.parseInt(b1.getCreatedAt());
            }
        });

        for (int i = 0; i < ja.size(); i++) {

            ContentBean bean = ContentCollection.get(ja.get(i));
            if(!bean.getVisibility().equals("1")){
                if (bean.getChildBeanContent() != null) {
                    Log.d("Log childs", "" + bean.getChildBeanContent().toString());
                    if (findChildsVisibleStatus(bean.getChildBeanContent())) {
                        childs.add(bean);

                    }

                }
            }
//            else
                childs.add(bean);

            if (bean.getChildBeanContent() != null) {
                if (bean.getChildBeanContent().size() > 0) {
                    depth++;
                    getChildsForReview(bean.getChildBeanContent());
                }
            }
        }
        if (depth != 0)
            depth--;
    }

    static Boolean findChildsVisibleStatus(List<String> childsList) {
        for (int i = 0; i < childsList.size(); i++) {
            if (ContentCollection.get(childsList.get(i)).getVisibility()
                    .equals("1")) {
                return true;
            }
            if (ContentCollection.get(childsList.get(i)).getChildBeanContent() != null) {
                findChildsVisibleStatus(ContentCollection
                        .get(childsList.get(i)).getChildBeanContent());
            }
        }
        return false;

    }

    public static void addChild(JSONArray ja) throws JSONException {
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = (JSONObject) ja.get(i);
            if (jo.getJSONObject("content").has("oembed")) {
                if (jo.getJSONObject("content").getJSONObject("oembed")
                        .has("url")) {
                    String targetId = jo.getJSONObject("content").getString(
                            "targetId");
                    if (!jo.isNull("event"))
                        lastEvent = jo.getString("event");
                    ContentBean bean = ContentCollection.get(targetId);
                    if (bean != null)
                        bean.setOembedUrl(jo.getJSONObject("content")
                                .getJSONObject("oembed").getString("url"));
                }

            } else {
                ContentBean bean = addContent(jo, depth, null, "bootstrap");
                ContentCollection.put(bean.getId(), bean);
            }

            if (jo.has("childContent")) {
                depth++;
                addChild((JSONArray) jo.get("childContent"));

            }
        }
        if (depth != 0)
            depth--;
    }

    public static ContentBean addContent(JSONObject objectMain, int depthValue,
                                         ContentBean contentBean, String from) {
        if (contentBean == null)
            contentBean = new ContentBean();
        try {
            JSONObject object = objectMain.getJSONObject("content");
            JSONObject annotations;
            if (!object.isNull("annotations")) {
                annotations = object.getJSONObject("annotations");
                if (!annotations.isNull("featuredmessage")) {
                    contentBean.setIsFeatured(true);
                }
                if (!annotations.isNull("moderator")) {
                    contentBean.setIsModerator(annotations
                            .getString("moderator"));
                }

                if (!annotations.isNull("vote")) {
                    JSONArray vote = annotations.getJSONArray("vote");
                    List<Vote> votes = new ArrayList();

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

            if (!objectMain.isNull("vis")) {
                contentBean.setVisibility(objectMain.getString("vis"));
                if (contentBean.getVisibility().equals("1"))
                    contentBean.setReviewStatus(CommentStatus.NOT_DELETED);
                else
                    contentBean.setReviewStatus(CommentStatus.DELETED);
            }

            if (!object.isNull("ancestorId"))
                contentBean.setAncestorId(object.getString("ancestorId"));

            if (!object.isNull("title"))
                contentBean.setTitle(object.getString("title"));

            if (!object.isNull("bodyHtml"))
                contentBean.setBodyHtml(object.getString("bodyHtml"));
            else
                contentBean.setBodyHtml("");
            if (!object.isNull("id"))
                contentBean.setId(object.getString("id"));

            if (!object.isNull("authorId"))
                contentBean.setAuthorId(object.getString("authorId"));

            if (!object.isNull("parentId")) {
                contentBean.setParentId(object.getString("parentId"));

                if (object.getString("parentId").equals(""))
                    contentBean.setContentType(ContentTypeEnum.PARENT);
                else
                    contentBean.setContentType(ContentTypeEnum.CHILD);
            }
            if (!objectMain.getString("vis").equals("1"))
                contentBean.setContentType(ContentTypeEnum.DELETED);

            if (!objectMain.isNull("event")) {
                contentBean.setEvent(objectMain.getString("event"));
                lastEvent = contentBean.getEvent();
            }

            if (!objectMain.isNull("type"))
                contentBean.setType(objectMain.getString("type"));

            if (!object.isNull("createdAt"))
                contentBean.setCreatedAt(object.getString("createdAt"));

            if (!object.isNull("updatedAt"))
                contentBean.setUpdatedAt(object.getString("updatedAt"));

            if (!object.isNull("authorId")) {
                contentBean.setAuthor((AuthorsBean) authorsCollection
                        .get(object.getString("authorId")));

            }
            if (!objectMain.isNull("childContent")) {
                contentBean.setChildContent(objectMain
                        .getJSONArray("childContent"));

            }
            if (from.equals("stream")) {
                contentBean.setFrom("stream");
            }
            contentBean.setDepth(depthValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentBean;

    }

    void authorCollection(JSONObject authorsJson) {
        if (authorsCollection == null)
            authorsCollection = new HashMap();
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

    public void setStreamData(String data) {

        JSONObject jsonObject;
        HashSet<String> updateSet = new HashSet();
        try {

            jsonObject = new JSONObject(data).getJSONObject("data");

            if (!jsonObject.isNull("authors")) {
                JSONObject states = jsonObject.getJSONObject("authors");
                if (states != null) {
                    Map<String, JSONObject> statesMap = new HashMap<String, JSONObject>();

                    Iterator keys = states.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        try {
                            statesMap.put(key, states.getJSONObject(key));
                            updateSet.add(key);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                    authorCollection(states);
                }
            }

            if (!jsonObject.isNull("states")) {
                JSONObject states = jsonObject.getJSONObject("states");
                if (states != null) {
                    Map<String, JSONObject> statesMap = new HashMap<String, JSONObject>();

                    Iterator keys = states.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        try {
                            statesMap.put(key, states.getJSONObject(key));
                            updateSet.add(key);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                    handleStatesDataNew(statesMap);
                }
            }

            if (!jsonObject.isNull("annotations")) {
                JSONObject annotations = jsonObject
                        .getJSONObject("annotations");
                if (annotations != null) {
                    Map<String, JSONObject> annotationsMap = new HashMap<String, JSONObject>();

                    Iterator keys = annotations.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        try {
                            annotationsMap.put(key,
                                    annotations.getJSONObject(key));
                            updateSet.add(key);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                    handleAnnotationsData(annotationsMap);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        updateChilds(updateSet);
        l1.onDataUpdate(updateSet);

    }

    private void updateChilds(HashSet<String> updateSet) {
        // TODO Auto-generated method stub
        for (String beanId : updateSet) {
            countFlag = 0;
            ContentBean childId = ContentCollection.get(beanId);
            if (childId != null) {
                if (childId.getAncestorId() != null) {
                    ContentBean parentId = ContentCollection.get(childId
                            .getAncestorId());
                    // Log.d("Ancestor", "" + childId.getAncestorId());
                    if (parentId != null)
                        if (parentId.getChildBeanContent() != null) {
                            getChildsVisibleCount(parentId
                                    .getChildBeanContent());
                            parentId.setVisibilityCount(countFlag);
                        }
                }
            }
        }

    }

    private static void getChildsVisibleCount(List<String> ja) {
        for (int i = 0; i < ja.size(); i++) {

            ContentBean bean = ContentCollection.get(ja.get(i));
            if (bean.getVisibility().equals("1"))
                countFlag++;
            if (bean.getChildBeanContent() != null) {
                if (bean.getChildBeanContent().size() > 0) {
                    getChildsVisibleCount(bean.getChildBeanContent());
                }
            }

        }
    }

    private void handleAnnotationsData(Map<String, JSONObject> map) {
        for (String contentId : map.keySet()) {
            JSONObject content = map.get(contentId);
            JSONObject added, removed, updated;
            try {
                added = content.getJSONObject("added");
                removed = content.getJSONObject("removed");
                updated = content.getJSONObject("updated");
                handleAddedAnnotations(contentId, added);
                handleAddedAnnotations(contentId, updated);
                handleRemovedAnnotations(contentId, removed);

                // Get Existed Content from collection of contents

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void handleAddedAnnotations(String contentId, JSONObject added) {
        ContentBean contentbean = ContentCollection.get(contentId);
        if (contentbean != null) {
            // Handle Votes in annotation
            if (!added.isNull("vote")) {
                JSONArray vote;
                try {
                    vote = added.getJSONArray("vote");
                    // List<Vote> votes = new ArrayList<>();
                    List<Vote> existedVotes = contentbean.getVote();

                    for (int j = 0; j < vote.length(); j++) {
                        JSONObject voteJson = vote.getJSONObject(j);
                        Vote v = new Vote();
                        if (!voteJson.isNull("value")
                                && !voteJson.isNull("author")) {
                            v.setValue(voteJson.getString("value"));

                            v.setAuthor(voteJson.getString("author"));
                            if (existedVotes != null) {
                                // Log.d("ExistedVotes Votes", "" +
                                // existedVotes);
                                for (int votelocation = 0; votelocation < existedVotes
                                        .size(); votelocation++) {
                                    Vote ev = existedVotes.get(votelocation);
                                    if (ev.getAuthor().equals(
                                            voteJson.getString("author")))
                                        existedVotes.remove(ev);
                                }
                            } else {
                                existedVotes = new ArrayList();
                            }
                            existedVotes.add(v);
                        }
                    }
                    contentbean.setVote(existedVotes);
                    int count = 0;
                    for (int i = 0; i < existedVotes.size(); i++) {
                        if (existedVotes.get(i).getValue().equals("1"))
                            count++;
                    }

                    contentbean.setHelpfulcount(count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (!added.isNull("featuredmessage")) {
                if (contentbean.getIsFeatured() == false) {
                    contentbean.setIsFeatured(true);
                }

            }
        }

    }

    private void handleRemovedAnnotations(String contentId, JSONObject removed) {
        ContentBean contentbean = ContentCollection.get(contentId);
        if (contentbean != null) {
            // Handle Votes in annotation
            if (!removed.isNull("vote")) {
                JSONArray vote;
                try {
                    vote = removed.getJSONArray("vote");
                    List<Vote> existedVotes = contentbean.getVote();

                    for (int j = 0; j < vote.length(); j++) {
                        JSONObject voteJson = vote.getJSONObject(j);
                        Vote v = new Vote();
                        if (!voteJson.isNull("value")
                                && !voteJson.isNull("author")) {
                            v.setValue(voteJson.getString("value"));

                            v.setAuthor(voteJson.getString("author"));
                            if (existedVotes != null) {
                                for (int position = 0; position < existedVotes
                                        .size(); position++) {
                                    Vote ev = existedVotes.get(position);
                                    if (ev.getAuthor().equals(
                                            voteJson.getString("author")))
                                        existedVotes.remove(ev);
                                }
                            } else {
                                existedVotes = new ArrayList();
                            }
                        }
                    }
                    contentbean.setVote(existedVotes);
                    int count = 0;
                    for (int i = 0; i < existedVotes.size(); i++) {
                        if (existedVotes.get(i).getValue().equals("1"))
                            count++;
                    }

                    contentbean.setHelpfulcount(count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!removed.isNull("featuredmessage")) {
                if (contentbean.getIsFeatured() == true) {
                    contentbean.setIsFeatured(false);
                }

            }
        }

    }

    private void handleStatesDataNew(Map<String, JSONObject> map) {
        for (JSONObject mainContent : map.values()) {
            try {
                JSONObject content = mainContent.getJSONObject("content");
                String parentId = null;
                if (!content.isNull("parentId"))
                    parentId = content.getString("parentId");
                ContentBean parentBean = ContentCollection.get(parentId);
                ContentBean newBean = ContentCollection.get(content
                        .getString("id"));
                if (newBean == null)
                    newBean = new ContentBean();
                if (mainContent.getString("type").equals("3")) {
                    if (mainContent.getJSONObject("content").has("oembed")) {
                        if (mainContent.getJSONObject("content")
                                .getJSONObject("oembed").has("url")) {
                            String targetId = mainContent.getJSONObject(
                                    "content").getString("targetId");
                            ContentBean bean = ContentCollection.get(targetId);
                            if (bean != null)
                                bean.setOembedUrl(mainContent
                                        .getJSONObject("content")
                                        .getJSONObject("oembed")
                                        .getString("url"));
                            if (!mainContent.isNull("event")) {
                                lastEvent = mainContent.getString("event");
                            }
                        }

                    }

                } else if (mainContent.getString("vis").equals("1")) {
                    // New or Edited
                    if (parentBean != null) {
                        newBean = addContent(mainContent,
                                parentBean.getDepth() + 1, newBean, "stream");

                        List<String> list;
                        if (parentBean.getChildBeanContent() != null) {
                            list = parentBean.getChildBeanContent();
                            Boolean flag = false;
                            for (String listID : list) {
                                if (listID.equals(newBean.getId()))
                                    flag = true;
                            }
                            if (!flag)
                                list.add(newBean.getId());
                        } else {
                            list = new ArrayList();
                            list.add(newBean.getId());
                            parentBean.setChildBeanContent(list);

                        }
                    } else {
                        newBean = addContent(mainContent, 0, newBean, "stream");
                    }
                    ContentCollection.put(newBean.getId(), newBean);

                } else {
                    // Deleted
                    String deletedParent = newBean.getParentId();
                    parentBean = ContentCollection.get(deletedParent);
                    if (parentBean != null) {
                        Log.d("Depth ", "Parent Depth" + parentBean.getDepth());
                        newBean = addContent(mainContent,
                                parentBean.getDepth() + 1, newBean, "stream");

                    } else {
                        // Parent Deleted
                        newBean = addContent(mainContent, 0, newBean, "stream");

                    }

                    ContentCollection.put(newBean.getId(), newBean);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


           }
    }



}
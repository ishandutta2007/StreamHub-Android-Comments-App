package com.livefyre.comments.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

public class ContentBean implements Serializable {
    private static final long serialVersionUID = 1775265761057887054L;

    private CommentStatus reviewStatus = CommentStatus.NOT_DELETED;
    private int visibilityCount=0;
    private String visibility;
    private Boolean isFeatured=false;
    private String isModerator="false";
    private String title;
    private String bodyHtml = "<p></p>";
    private String id;
    private String authorId;
    private String parentId = "";
    private String updatedAt;
    private String createdAt;
    private String type;
    private String event;
    private List<String>  childBeanContent=null;
    private AuthorsBean author;
    private int depth=0;
    private List<Vote> vote;
    private int helpfulcount=0;
    private String oembedUrl;
    private JSONArray childContent;
    private ContentTypeEnum contentType=ContentTypeEnum.CHILD;	//	Parent or Child
    private int newReplyCount=0;
    private String ancestorId="";
    private String from="bootstrap";


    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public CommentStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(CommentStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public AuthorsBean getAuthor() {
        return author;
    }

    public void setAuthor(AuthorsBean author) {
        this.author = author;
    }



    public List<Vote> getVote() {
        return vote;
    }

    public void setVote(List<Vote> vote) {
        this.vote = vote;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getVisibilityCount() {
        return visibilityCount;
    }

    public void setVisibilityCount(int visibilityCount) {
        this.visibilityCount = visibilityCount;
    }

    public String getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(String isModerator) {
        this.isModerator = isModerator;
    }

    public int getHelpfulcount() {
        return helpfulcount;
    }

    public void setHelpfulcount(int helpfulcount) {
        this.helpfulcount = helpfulcount;
    }

    public String getOembedUrl() {
        return oembedUrl;
    }

    public void setOembedUrl(String oembedUrl) {
        this.oembedUrl = oembedUrl;
    }

    public JSONArray getChildContent() {
        return childContent;
    }

    public void setChildContent(JSONArray childContent) {
        this.childContent = childContent;
    }

    public List<String> getChildBeanContent() {
        return childBeanContent;
    }

    public void setChildBeanContent(List<String> childBeanContent) {
        this.childBeanContent = childBeanContent;
    }

    public ContentTypeEnum getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    public int getNewReplyCount() {
        return newReplyCount;
    }

    public void setNewReplyCount(int newReplyCount) {
        this.newReplyCount = newReplyCount;
    }

    public String getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(String ancestorId) {
        this.ancestorId = ancestorId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }






}
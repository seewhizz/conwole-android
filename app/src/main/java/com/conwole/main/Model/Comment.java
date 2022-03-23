package com.conwole.main.Model;

public class Comment {

    private String uid;
    private String commentText;

    public Comment() {
    }

    public Comment(String uid, String commentText) {
        this.uid = uid;
        this.commentText = commentText;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}

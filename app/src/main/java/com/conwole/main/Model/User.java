package com.conwole.main.Model;

public class User {

    private String user_id;
    private String user_email;
    private String profile_pic_url;
    private String full_name;
    private String user_bio;
    private String user_link;
    private String timestamp;
    private String token;

    public User() {
    }

    public User(String user_id, String user_email, String profile_pic_url, String full_name, String timestamp, String token) {
        this.user_id = user_id;
        this.user_email = user_email;
        this.profile_pic_url = profile_pic_url;
        this.full_name = full_name;
        this.timestamp = timestamp;
        this.token = token;
    }

    public User(String user_id, String user_email, String profile_pic_url, String full_name, String user_bio, String user_link, String timestamp, String token) {
        this.user_id = user_id;
        this.user_email = user_email;
        this.profile_pic_url = profile_pic_url;
        this.full_name = full_name;
        this.user_bio = user_bio;
        this.user_link = user_link;
        this.timestamp = timestamp;
        this.token = token;
    }

    public String getUser_bio() {
        return user_bio;
    }

    public void setUser_bio(String user_bio) {
        this.user_bio = user_bio;
    }

    public String getUser_link() {
        return user_link;
    }

    public void setUser_link(String user_link) {
        this.user_link = user_link;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

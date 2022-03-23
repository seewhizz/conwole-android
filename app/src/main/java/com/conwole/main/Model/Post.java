package com.conwole.main.Model;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String key;
    private String category;
    private String title;
    private String description;
    private String image_url;
    private String user_id;
    private String timestamp;
    public int like_count = 0;
    public Map<String, Boolean> likes = new HashMap<>();

    public Post() {
    }

    public Post(String key, String category, String title, String description, String image_url, String user_id, String timestamp) {
        this.key = key;
        this.category = category;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

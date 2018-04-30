package com.crisanto.kevin.picstant.models;

public class Like {

    int story_id;
    String story_image, story_username, story_title;

    public Like(int story_id, String story_image, String story_username, String story_title) {
        this.story_id = story_id;
        this.story_image = story_image;
        this.story_username = story_username;
        this.story_title = story_title;
    }

    public int getStory_id() {
        return story_id;
    }

    public void setStory_id(int story_id) {
        this.story_id = story_id;
    }

    public String getStory_image() {
        return story_image;
    }

    public void setStory_image(String story_image) {
        this.story_image = story_image;
    }

    public String getStory_username() {
        return story_username;
    }

    public void setStory_username(String story_username) {
        this.story_username = story_username;
    }

    public String getStory_title() {
        return story_title;
    }

    public void setStory_title(String story_title) {
        this.story_title = story_title;
    }
}

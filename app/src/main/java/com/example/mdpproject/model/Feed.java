package com.example.mdpproject.model;

public class Feed {
    private String title;
    private String Content;
    private String ImageSrc;
    private String link;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return Content;
    }

    public String getImageSrc() {
        return ImageSrc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setImageSrc(String imageSrc) {
        ImageSrc = imageSrc;
    }

}

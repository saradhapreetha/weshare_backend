package com.weshare.watch_service.models;

import jakarta.persistence.*;

import java.net.URL;

@Entity
@Table(name="videos")

public class Videos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String author;
    private String url;
    private String objectkey;

    public String getObjectkey() {
        return objectkey;
    }

    public void setObjectkey(String objectkey) {
        this.objectkey = objectkey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }




}

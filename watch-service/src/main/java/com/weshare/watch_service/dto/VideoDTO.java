package com.weshare.watch_service.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.net.URL;

public class VideoDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String author;

    private URL presignedURL;


    public VideoDTO(String title,String author,String description, URL presignedURL){
        this.title=title;
        this.description=description;
        this.author=author;
        this.presignedURL=presignedURL;
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

    public URL getPresignedURL() {
        return presignedURL;
    }

    public void setPresignedURL(URL presignedURL) {
        this.presignedURL = presignedURL;
    }



}

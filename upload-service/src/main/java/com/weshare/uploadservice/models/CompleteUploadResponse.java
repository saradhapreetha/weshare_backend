package com.weshare.uploadservice.models;

public class CompleteUploadResponse {
    private String Location;
    private String Bucket;
    private String Key;
    private String ETag;
    private String ServerSideEncryption;

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getBucket() {
        return Bucket;
    }

    public void setBucket(String bucket) {
        Bucket = bucket;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }

    public String getServerSideEncryption() {
        return ServerSideEncryption;
    }

    public void setServerSideEncryption(String serverSideEncryption) {
        ServerSideEncryption = serverSideEncryption;
    }

    // Constructor
    public CompleteUploadResponse(String Location, String Bucket, String Key, String ETag, String ServerSideEncryption) {
        this.Location = Location;
        this.Bucket = Bucket;
        this.Key = Key;
        this.ETag = ETag;
        this.ServerSideEncryption = ServerSideEncryption;
    }


}

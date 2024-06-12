package com.weshare.uploadservice.exceptions;

public class VideoSaveException extends  RuntimeException{
    public VideoSaveException(String message){
        super(message);
    }

    public VideoSaveException(String message, Throwable cause){
        super(message, cause);
    }
}


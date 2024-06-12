package com.weshare.uploadservice.exceptions;

public class UploadChunksException extends RuntimeException{
    public UploadChunksException(String message){
        super(message);
    }

    public UploadChunksException(String message, Throwable cause){
        super(message, cause);
    }
}

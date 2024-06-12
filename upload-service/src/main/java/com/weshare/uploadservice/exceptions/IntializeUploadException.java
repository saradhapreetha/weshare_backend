package com.weshare.uploadservice.exceptions;

public class IntializeUploadException extends RuntimeException {
    public IntializeUploadException(String message){
        super(message);
    }

    public IntializeUploadException(String message, Throwable cause){
        super(message, cause);
    }
}

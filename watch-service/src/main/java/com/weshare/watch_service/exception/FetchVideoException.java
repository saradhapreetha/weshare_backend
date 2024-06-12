package com.weshare.watch_service.exception;

public class FetchVideoException extends RuntimeException{
    public FetchVideoException() {
    }
    public FetchVideoException(String message, Throwable cause){
        super(message, cause);
    }
}

package com.weshare.uploadservice.exceptions;

import com.weshare.uploadservice.models.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(IntializeUploadException.class)
    public ResponseEntity<ErrorResponse> handleException(IntializeUploadException e){
         logger.error(e.getMessage());
         ErrorResponse errorResponse = new ErrorResponse("Internal server Error",e.getMessage());
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(UploadCompletionException.class)
    public ResponseEntity<ErrorResponse> handleUploadCompletionException(UploadCompletionException e) {
        logger.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Upload Completion Error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(UploadChunksException.class)
    public ResponseEntity<ErrorResponse> handleUploadCompletionException(UploadChunksException e) {
        logger.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Upload Completion Error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(VideoSaveException.class)
    public ResponseEntity<ErrorResponse> handleVideoSaveException(VideoSaveException e){
        logger.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Internal server Error",e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }



}

package com.weshare.watch_service.exception;

import com.weshare.watch_service.services.WatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WatchService.class);
        @ExceptionHandler(FetchVideoException.class)
        public ResponseEntity<String> handleDataAccessException(FetchVideoException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
}

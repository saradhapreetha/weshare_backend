package com.weshare.uploadservice.services;


import com.weshare.uploadservice.exceptions.UploadCompletionException;
import com.weshare.uploadservice.exceptions.VideoSaveException;
import com.weshare.uploadservice.models.Videos;
import com.weshare.uploadservice.repositories.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    private static final Logger logger = LoggerFactory.getLogger(UploadToS3Service.class);
    public Videos saveVideo(Videos video){

        try{return videoRepository.save(video);}
        catch(DataAccessException e){
            logger.error(e.getMessage());
            throw new VideoSaveException("Error occured with database while saving video " , e);
        }
    }
}

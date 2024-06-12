package com.weshare.watch_service.services;


import com.weshare.watch_service.exception.FetchVideoException;
import com.weshare.watch_service.repositories.VideoRepository;
import com.weshare.watch_service.models.Videos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    private static final Logger logger = LoggerFactory.getLogger(WatchService.class);

    public List<Videos> getAllVideos(){

        try{return videoRepository.findAll();}
        catch(DataAccessException e){
            logger.error(e.getMessage());
            throw new FetchVideoException("Error occured with database while fetching video " , e);
        }
    }

}

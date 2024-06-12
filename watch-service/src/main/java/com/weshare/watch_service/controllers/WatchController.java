package com.weshare.watch_service.controllers;


import com.weshare.watch_service.dto.VideoDTO;
import com.weshare.watch_service.services.VideoService;
import com.weshare.watch_service.models.Videos;
import com.weshare.watch_service.services.WatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@RestController

public class WatchController {


    @Autowired
    private S3Client s3Client;

    @Autowired
    private WatchService s3Service;

    @Autowired
    private VideoService videoService;

//    @PostMapping("/watch")
//    public URL getPresignedUrl(@RequestParam String objectKey){
//        try {
//            return s3Service.generatePresignedUrl(objectKey);
//        }
//
//        catch(Exception e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @GetMapping("/home/watch")
    public List<VideoDTO> getAllVideos(){

            List<Videos> videos = videoService.getAllVideos();
            return videos.stream().map(video -> {
                URL presignedUrl = s3Service.generatePresignedUrl(video.getObjectkey());
                return new VideoDTO(video.getTitle(), video.getAuthor(), video.getDescription(), presignedUrl);
            }).collect(Collectors.toList());

    }

}

package com.weshare.uploadservice.controllers;


import com.weshare.uploadservice.models.VideoSearchData;
import com.weshare.uploadservice.services.VideoSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class VideoSearchController {

    @Autowired
    private VideoSearchService videoSearchService;

    @GetMapping("/")
    public List<VideoSearchData> searchVideos(@RequestParam String query)
    {
        return videoSearchService.searchByTitle(query);
    }

    @PostMapping("/add")
            public void pushVideoToSearch(@RequestBody VideoSearchData video)
    {
        videoSearchService.saveVideoToSearch(video);
    }
}

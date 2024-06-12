package com.weshare.uploadservice.services;


import com.weshare.uploadservice.models.VideoSearchData;
import com.weshare.uploadservice.repositories.VideoSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoSearchService {

    @Autowired
    private VideoSearchRepository videoSearchRepository;

    public List<VideoSearchData> searchByTitle(String keyword) {
        return videoSearchRepository.findByTitleContaining(keyword);
    }

    public void saveVideoToSearch(VideoSearchData video) {
        System.out.println("saving to search");
        videoSearchRepository.save(video);
    }
}

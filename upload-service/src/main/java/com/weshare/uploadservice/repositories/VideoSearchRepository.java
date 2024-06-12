package com.weshare.uploadservice.repositories;

import com.weshare.uploadservice.models.VideoSearchData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface VideoSearchRepository extends ElasticsearchRepository<VideoSearchData,String> {

    List<VideoSearchData> findByTitleContaining(String query);

}

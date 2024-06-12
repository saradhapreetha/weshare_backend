package com.weshare.uploadservice.repositories;

import com.weshare.uploadservice.models.Videos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VideoRepository extends JpaRepository<Videos,Integer> {

    List<Videos> findAll();

    Videos save(Videos video);
}

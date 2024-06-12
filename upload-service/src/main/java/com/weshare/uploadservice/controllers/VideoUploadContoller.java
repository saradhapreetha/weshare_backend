package com.weshare.uploadservice.controllers;


import com.weshare.uploadservice.exceptions.UploadCompletionException;
import com.weshare.uploadservice.models.CompleteUploadResponse;
import com.weshare.uploadservice.models.Videos;
import com.weshare.uploadservice.services.UploadToS3Service;
import com.weshare.uploadservice.services.VideoService;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
public class VideoUploadContoller {

    @Autowired
    private UploadToS3Service s3Service;

    @Autowired
    private VideoService videoService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;



    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeUpload(@RequestParam("filename") String filename) {

            System.out.println("SEEE Request");
            System.out.println(filename);
            System.out.println("SEEE Request");
           // String filename = request.get("filename");
            if (filename == null || filename.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Filename is required"));
            }

            return s3Service.initializeUpload(filename);
    }



    @PostMapping("/upload")
    public ResponseEntity<String> uploadChunks(
              @RequestPart("filename") String filename,
            @RequestPart("chunkIndex") String chunkIndex,
          @RequestPart("uploadId") String uploadId,
            @RequestPart("chunk") MultipartFile chunk
            ) {

            System.out.println("INNNNN");
            return s3Service.upload(chunkIndex,chunk,filename,uploadId);
    }

    @PostMapping("/upload/complete")
    public ResponseEntity<CompleteUploadResponse> completeUpload(
            @RequestBody Map<String, Object> request
    ) {

            System.out.println("INNNNN complete");
            String filename = (String) request.get("filename");
            String uploadId = (String) request.get("uploadId");
            CompleteUploadResponse response = s3Service.completeUpload(filename,uploadId);

            System.out.println("filename sent to kafka "+filename);
            CompletableFuture<SendResult<String,String>> future = kafkaTemplate.send("transcode",filename);
            future.whenComplete((result,ex)->{

                if(ex==null)
                {
                    RecordMetadata metadata = result.getRecordMetadata();
                    System.out.println("completed "+metadata);
                }			});

            return ResponseEntity.ok(response);
    }

    @PostMapping("/addVideos")
    public ResponseEntity<Videos> saveVideo(@RequestBody Videos video) {
        System.out.println(video.getUrl());
        System.out.println("add video body "+video+" data "+video.getObjectkey());

            Videos savedVideo = videoService.saveVideo(video);
            return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);

    }


    private ResponseEntity<Map<String, String>> handleException(Exception e, String errorMessage) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", errorMessage, "message", e.getMessage()));
    }


//    @PostMapping("/uploadchunk")
//    public String uploadFile(@RequestParam("file") String file){
//        try{
//            //file = "C:\\Users\\Admin\\Documents\\youtube-backend\\upload-service\\src\\main\\resources\\static\\testvideo.mp4";
//            System.out.println("FILEEEE "+file);
//            return s3Service.uploadFile(file);
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            return "Error uploading file: " + e.getMessage();
//        }
   //  }
}

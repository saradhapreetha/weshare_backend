package com.weshare.watch_service.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URL;
import java.time.Duration;

@Service
public class WatchService {


    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Presigner presigner;

    @Value("${aws.region}")
    private String region;
    private static final Logger logger = LoggerFactory.getLogger(WatchService.class);
    private static final String bucketName = "we-share-uploaded-videos";

    public URL generatePresignedUrl
            (String objectKey) {
        try {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1)) // URL expires in 1 hour
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedObject = presigner.presignGetObject(getObjectPresignRequest);
            presigner.close();
            return presignedObject.url();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}

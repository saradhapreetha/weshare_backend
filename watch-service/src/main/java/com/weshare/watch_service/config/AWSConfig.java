package com.weshare.watch_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AWSConfig {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretKey;
    @Value("${aws.region}")
    private String region;



    @Bean
    public S3Presigner s3Presigner(){
        try {

            System.out.println(region);
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                  accessKeyId,
                    secretKey

            );


            return  S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();
        }
        catch(Exception e)
        {
            System.out.println("Yes error here");
            e.printStackTrace();
            return null;
        }
    }
}

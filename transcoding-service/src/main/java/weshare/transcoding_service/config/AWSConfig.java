package weshare.transcoding_service.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretKey;
    

    @Value("${aws.region}")
    private String region;


     @Bean
    public S3Client s3Client(){
         try {

             System.out.println(region);
             AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                   accessKeyId,
                     secretKey

             );


             return S3Client.builder()
                     .region(Region.of(region))
                     .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                     .build();
         }
         catch(Exception e)
         {
             e.printStackTrace();
             return null;
         }
     }
}

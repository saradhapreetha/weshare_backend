package com.weshare.uploadservice.services;


import com.weshare.uploadservice.exceptions.IntializeUploadException;
import com.weshare.uploadservice.exceptions.UploadChunksException;
import com.weshare.uploadservice.exceptions.UploadCompletionException;
import com.weshare.uploadservice.models.CompleteUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UploadToS3Service {
    @Autowired
    private S3Client s3Client;

    private static final Logger logger = LoggerFactory.getLogger(UploadToS3Service.class);


    private static final String bucketName = "we-share-uploaded-videos";


    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeUpload(String filename) {
        try {
            //String filename = request.get("filename");
            if (filename == null || filename.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Filename is required"));
            }

            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType("video/mp4")
                    .build();

            CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(createMultipartUploadRequest);
            String uploadId = createMultipartUploadResponse.uploadId();

            return ResponseEntity.ok(Map.of("uploadId", uploadId));
        }
        catch(SdkClientException e)
        {
            throw new IntializeUploadException("Failed to initialize video upload "+filename,e);
        }
    }

    public ResponseEntity<String> upload(String chunkIndex, MultipartFile chunk,String filename,String uploadId){

        try {
            int partIndex = Integer.parseInt(chunkIndex);
            System.out.println("PartIndex " + partIndex);
            ByteBuffer byteBuffer = ByteBuffer.wrap(chunk.getBytes());
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .uploadId(uploadId)
                    .partNumber(partIndex)
                    .contentLength((long) chunk.getSize())
                    .build();

            UploadPartResponse response = s3Client.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(byteBuffer));
            System.out.println("PARTTTTTT");
            System.out.println(response);
            //return new ResponseEntity<>(response, HttpStatus.OK);
            return ResponseEntity.ok("Yes");
        }
        catch(IOException | MaxUploadSizeExceededException e){
             throw new UploadChunksException("Failed to upload chunks "+filename+" "+chunkIndex,e);
        }

    }

    public CompleteUploadResponse completeUpload(String filename, String uploadId) {
        try {

            System.out.println("complete upload");
            // List parts from S3
            ListPartsRequest listPartsRequest = ListPartsRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .uploadId(uploadId)
                    .build();

            ListPartsResponse listPartsResponse = s3Client.listParts(listPartsRequest);

            // Collect the uploaded parts
            List<CompletedPart> completedParts = new ArrayList<>();
            listPartsResponse.parts().forEach(part -> completedParts.add(CompletedPart.builder()
                    .eTag(part.eTag())
                    .partNumber(part.partNumber())
                    .build()));

            // Complete the multipart upload
            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                    .parts(completedParts)
                    .build();

            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .uploadId(uploadId)
                    .multipartUpload(completedMultipartUpload)
                    .build();

            CompleteMultipartUploadResponse response=s3Client.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println("completed "+response);
            CompleteUploadResponse completedData = new CompleteUploadResponse(response.location(), response.bucket(), response.key(),response.eTag(),response.serverSideEncryptionAsString());

            return completedData;

        }
        catch(S3Exception e){
            logger.error(e.getMessage());
            throw new UploadCompletionException("Failed to complete for "+filename , e);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
          throw new UploadCompletionException("Failed to complete for "+filename , e);
        }
    }


    public String uploadFile(String filePath){
        Path path = Paths.get(filePath);
        if(!Files.exists(path)){
            throw new RuntimeException("File does not exits: "+filePath);
        }

        File fileToUpload = path.toFile();
        long contentLength = fileToUpload.length();
        long partSize = 5 * 1024 * 1024;
        String keyName = fileToUpload.getName();
        System.out.println("SEE");
        System.out.println(filePath);
        System.out.println(keyName);
        System.out.println("SEE");
        try{

            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest
                    .builder()
                    .bucket(bucketName).key(keyName).build();
            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
            String uploadId = createResponse.uploadId();
            List<CompletedPart> completedParts = new ArrayList<>();
            int partNumber = 1;
            ByteBuffer buffer = ByteBuffer.allocate(5 * 1024 * 1024);

            try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
                long fileSize = file.length();
                long position = 0;

                while (position < fileSize) {
                    file.seek(position);
                    int bytesRead = file.getChannel().read(buffer);

                    buffer.flip();
                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .uploadId(uploadId)
                            .partNumber(partNumber)
                            .contentLength((long) bytesRead)
                            .build();

                    UploadPartResponse response = s3Client.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(buffer));

                    completedParts.add(CompletedPart.builder()
                            .partNumber(partNumber)
                            .eTag(response.eTag())
                            .build());

                    buffer.clear();
                    position += bytesRead;
                    partNumber++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Complete the multipart upload
            CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
                    .parts(completedParts)
                    .build();

            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .uploadId(uploadId)
                    .multipartUpload(completedUpload)
                    .build();

            CompleteMultipartUploadResponse completeResponse = s3Client.completeMultipartUpload(completeRequest);
            String objectUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .build())
                    .toExternalForm();

            System.out.println("Uploaded object URL: " + objectUrl);

        }
        catch(SdkClientException e){
                e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }


      return "";



    }
}

package weshare.transcoding_service.service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class TranscodingService {

    private final String ffmpegPath = "C:\\Program Files\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe"; // Path to your ffmpeg binary
    private static final String bucketName = "we-share-uploaded-videos";
    @Autowired
    private S3Client s3Client;

    private static String outputDirectory = "hls";
    private static String localPath = "local.mp4";
    public void convertToHLS(String inputFilePath) throws IOException {

        downloadFileFromS3(inputFilePath);

        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

        List<Resolution> resolutions = List.of(
                new Resolution("320x180", "500000", "64000"),
                new Resolution("854x480", "1000000", "128000"),
                new Resolution("1280x720", "2500000", "192000")
        );

        List<VariantPlaylist> variantPlaylists = new ArrayList<>();

        for (Resolution resolution : resolutions) {
            String outputFileName = String.format("%s_%s.m3u8", getFileNameWithoutExtension(inputFilePath), resolution.getResolution());
            String segmentFileName = String.format("%s_%s_%%03d.ts", getFileNameWithoutExtension(inputFilePath), resolution.getResolution());
            System.out.println("output "+outputFileName);
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(localPath)
                    .overrideOutputFiles(true)
                    .addOutput(Paths.get(outputDirectory, outputFileName).toString())
                    .setFormat("hls")
                    .addExtraArgs("-hls_time", "10")
                    .addExtraArgs("-hls_list_size", "0")
                    .addExtraArgs("-hls_segment_filename", Paths.get(outputDirectory, segmentFileName).toString())
                    .setVideoCodec("h264")
                    .setVideoBitRate(Long.parseLong(resolution.getVideoBitrate()))
                    .addExtraArgs("-vf", "scale=" + resolution.getResolution())
                    .setAudioCodec("aac")
                    .setAudioBitRate(Long.parseLong(resolution.getAudioBitrate()))
                    .done();

            executor.createJob(builder).run();

            variantPlaylists.add(new VariantPlaylist(resolution.getResolution(), outputFileName));
        }

        generateMasterPlaylist(variantPlaylists, Paths.get(outputDirectory, getFileNameWithoutExtension(localPath) + "_master.m3u8").toString());

        uploadFilesToS3(outputDirectory,variantPlaylists);
        Files.deleteIfExists(Paths.get(localPath));
    }

    private void downloadFileFromS3(String key) throws IOException {
        System.out.println("download "+key);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

        S3Response responseInputStream1 = s3Client.getObject(getObjectRequest).response();
        ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
        System.out.println("*************localPath "+Paths.get(localPath));
        Files.copy(responseInputStream,Paths.get(localPath));
        responseInputStream.close();
    }

    private void uploadFilesToS3(String directory,List<VariantPlaylist> variantPlayLists) throws IOException {
         for(VariantPlaylist variantPlaylist : variantPlayLists)
         {
               String outputFileName = variantPlaylist.getOutputFileName();
               System.out.println("outputFileName "+outputFileName);
               File file = new File(directory,outputFileName);
               uploadFileToS3(file,"hls/"+outputFileName);

               String segmentPattern = outputFileName.replace(".m3u8","_%03d.ts");

               for(int i=0;;i++)
               {
                   File segmentFile = new File(directory,String.format(segmentPattern,i));
                   if(!segmentFile.exists())
                   {
                       break;
                   }
                   uploadFileToS3(segmentFile,"hls/"+segmentFile.getName());
                   Files.deleteIfExists(segmentFile.toPath());

               }

               Files.deleteIfExists(file.toPath());
         }

         String masterPlaylistPath = directory + "/" + getFileNameWithoutExtension(localPath)+ "_master.m3u8";
         System.out.println("Mater playlist path 2 "+masterPlaylistPath);
         uploadFileToS3(new File(masterPlaylistPath),"hls/"+new File(masterPlaylistPath));
         Files.deleteIfExists(Paths.get(masterPlaylistPath));
    }
    private void generateMasterPlaylist(List<VariantPlaylist> variantPlaylists, String masterPlaylistPath) throws IOException {
        System.out.println("MASTER PLAYLIST "+masterPlaylistPath);
        StringBuilder masterPlaylist = new StringBuilder("#EXTM3U\n");
        for (VariantPlaylist variant : variantPlaylists) {
            int bandwidth = switch (variant.getResolution()) {
                case "320x180" -> 676800;
                case "854x480" -> 1353600;
                default -> 3230400;
            };
            masterPlaylist.append(String.format("#EXT-X-STREAM-INF:BANDWIDTH=%d,RESOLUTION=%s\n%s\n", bandwidth, variant.getResolution(), variant.getOutputFileName()));
        }
        Files.write(Paths.get(masterPlaylistPath), masterPlaylist.toString().getBytes());
    }

    private void uploadFileToS3(File file,String key) throws IOException{
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        System.out.println("NAMEEEE "+file.getName());
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }
    private String getFileNameWithoutExtension(String filePath) {
        String newPath = Paths.get(filePath).getFileName().toString().replaceFirst("[.][^.]+$", "");
        System.out.println("newPath "+newPath);
        return newPath.substring(0, filePath.lastIndexOf('.'));
    }

    private static class Resolution {
        private final String resolution;
        private final String videoBitrate;
        private final String audioBitrate;

        public Resolution(String resolution, String videoBitrate, String audioBitrate) {
            this.resolution = resolution;
            this.videoBitrate = videoBitrate;
            this.audioBitrate = audioBitrate;
        }

        public String getResolution() {
            return resolution;
        }

        public String getVideoBitrate() {
            return videoBitrate;
        }

        public String getAudioBitrate() {
            return audioBitrate;
        }
    }

    private static class VariantPlaylist {
        private final String resolution;
        private final String outputFileName;

        public VariantPlaylist(String resolution, String outputFileName) {
            this.resolution = resolution;
            this.outputFileName = outputFileName;
        }

        public String getResolution() {
            return resolution;
        }

        public String getOutputFileName() {
            return outputFileName;
        }
    }
}

package weshare.transcoding_service.service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ProcessingVideoService {

    private final String ffmpegPath = "C:\\Program Files\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe"; // Set the path to your ffmpeg binary
    private final String inputPath = "src\\main\\resources\\testvideo.mp4";
    private final String outputDirectory = "output";

    public void convertToDash() throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

        Path outputDirPath = Paths.get(outputDirectory);
        if(!Files.exists(outputDirPath)){
            Files.createDirectories(outputDirPath);
        }

        String[] scaleOptions = {
                "1280_720",
                "640_320",
                "1920_1080",
                "854_480"
        };

        String videoCodec = "libx264";
        String x264Options = "keyint=24:min-keyint=24:no-scenecut";
        long[] videoBitrates = {500_000L, 1_000_000L, 2_000_000L, 4_000_000L}; // bitrates in bits per second

        for (String scale : scaleOptions) {
            for (long bitrate : videoBitrates) {
                String outputFileName = String.format("%s_%s_%dk.mpd", getFileNameWithoutExtension(inputPath), scale.replace('x','_'), bitrate / 1000);

                FFmpegBuilder builder = new FFmpegBuilder()
                        .setInput(inputPath)
                        .overrideOutputFiles(true)
                        .addOutput(Paths.get(outputDirectory, outputFileName).resolve("tmp").toString())
                        .setFormat("dash")
                        .setVideoCodec(videoCodec)
                        .addExtraArgs("-x264opts", x264Options)
                        .addExtraArgs("-vf", "scale=" + scale)
                        .setVideoBitRate(bitrate)
                        .done();

                executor.createJob(builder).run();
            }
        }

        System.out.println("DASH encoding completed.");
    }

    private String getFileNameWithoutExtension(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf('.'));
    }
}

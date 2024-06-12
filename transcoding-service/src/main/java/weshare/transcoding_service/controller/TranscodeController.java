package weshare.transcoding_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import weshare.transcoding_service.service.TranscodingService;

import java.io.IOException;

@RestController
@RequestMapping("/transcode")
public class TranscodeController {

    @Autowired
    private TranscodingService videoTranscoderService;

    @PostMapping("/convert")
    public String convertToDash() {
        try {

            videoTranscoderService.convertToHLS("testvideo.mp4");
            return "HLS encoding completed.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error during DASH encoding: " + e.getMessage();
        }
    }
}

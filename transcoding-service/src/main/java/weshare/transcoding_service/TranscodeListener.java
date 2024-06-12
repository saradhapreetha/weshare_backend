package weshare.transcoding_service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import weshare.transcoding_service.service.TranscodingService;

import java.io.IOException;

@Service
public class TranscodeListener {

    @Autowired
    private TranscodingService videoTranscoderService;
    @KafkaListener(topics="transcode",groupId = "transcoder-group")
    public void listen(String filename) throws IOException {
        System.out.println("Received message: "+filename);
        videoTranscoderService.convertToHLS(filename);

    }


}

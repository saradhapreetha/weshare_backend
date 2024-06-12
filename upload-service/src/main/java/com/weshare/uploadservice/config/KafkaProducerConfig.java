package com.weshare.uploadservice.config;



import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Value("${spring.kafka.properties.ssl.truststore.location}")
    private String truststoreLocation;


    @Value("${spring.kafka.properties.ssl.keystore.location}")
    private String keystoreLocation;

    @Value("${spring.kafka.properties.ssl.key.location}")
    private String keyLocation;

//    @Value("${spring.kafka.properties.ssl.keystore.password}")
//    private String keystorePassword;

    @Bean
    public Map<String,Object> producerConfig(){
         Map<String, Object> props = new HashMap<>();
         props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
         props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
         props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
            props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,120000);
            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.type", "PEM");
            props.put("ssl.truststore.location", truststoreLocation);
            props.put("ssl.keystore.type", "PEM");
            props.put("ssl.keystore.location", keystoreLocation);
         return props;
    }

    @Bean
    public ProducerFactory<String,String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(
            ProducerFactory<String,String> producerFactory ){
                return new KafkaTemplate<>(producerFactory);
    }

}

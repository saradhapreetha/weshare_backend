package weshare.transcoding_service.config;



import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Value("${spring.kafka.properties.ssl.truststore.location}")
    private String truststoreLocation;



    @Value("${spring.kafka.properties.ssl.keystore.location}")
    private String keystoreLocation;

    @Value("${spring.kafka.properties.ssl.key.location}")
    private String keyLocation;


    @Bean
    public Map<String,Object> consumerConfig(){
         Map<String, Object> props = new HashMap<>();
         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
         props.put(ConsumerConfig.GROUP_ID_CONFIG,"transcoder-group");
         props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
         props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        props.put("security.protocol", "SSL");
        props.put("ssl.truststore.type", "PEM");
        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.keystore.type", "PEM");
        props.put("ssl.keystore.location", keystoreLocation);
         return props;
    }

    @Bean
    public ConsumerFactory<String,String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}

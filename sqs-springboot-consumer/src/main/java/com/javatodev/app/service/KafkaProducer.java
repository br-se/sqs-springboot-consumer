package com.javatodev.app.service;

import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaProducer extends BaseProducer {

    public static void sendToKafka(Map<String, String> headers) throws InterruptedException {

        KafkaProducer producer = new KafkaProducer();
        producer.loadConfiguration(System.getenv());
        Properties props = producer.loadKafkaProducerProperties();
        // props.setProperty(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
        // TracingProducerInterceptor.class.getName());
        producer.createKafkaProducer(props);

        Thread producerThread = null;
        try {

            log.info("DEBUG producer.run()");
            producerThread = new Thread(() -> producer.run(headers));

        } finally {
        }

        producerThread.start();

    }

}
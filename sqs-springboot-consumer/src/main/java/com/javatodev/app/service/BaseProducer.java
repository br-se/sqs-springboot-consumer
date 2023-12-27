package com.javatodev.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;

public abstract class BaseProducer {

    // private static final String BOOTSTRAP_SERVERS_ENV_VAR = "BOOTSTRAP_SERVERS";
    // private static final String TOPIC_ENV_VAR = "TOPIC";
    private static final String NUM_MESSAGES_ENV_VAR = "NUM_MESSAGES";
    private static final String DELAY_ENV_VAR = "DELAY";

    // private static final String DEFAULT_BOOTSTRAP_SERVERS = "172.31.77.69:9092";
    // private static final String DEFAULT_TOPIC = "mytopic";
    private static final String DEFAULT_NUM_MESSAGES = "3";
    private static final String DEFAULT_DELAY = "5000";

    private static final Logger log = LogManager.getLogger(BaseProducer.class);

    protected String bootstrapServers;
    protected String topic;
    protected int numMessages;
    protected long delay;
    protected Producer<String, String> producer;

    public void run(Map<String, String> headers) {
        Span span = OpenTelUtils.addTrace(headers, "producer-send", SpanKind.PRODUCER);
        try (Scope scope = span.makeCurrent()) {
            for (int i = 0; i < this.numMessages; i++) {
                String message = "produced by java (fargate) - message number " + i;

                ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, message);

                record.headers().add("traceparent", OpenTelUtils.returnTraceParent(headers).getBytes());

                this.producer.send(record);

                log.info("Message [{}] sent to topic [{}]", message, this.topic);

                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
            // Do nothing
        } finally {
            this.producer.close();
            if (span != null) {
                span.end();
            }
        }
    }

    public void loadConfiguration(Map<String, String> map) {
        this.bootstrapServers = "172.31.77.69:29092";// map.getOrDefault(BOOTSTRAP_SERVERS_ENV_VAR,
                                                     // DEFAULT_BOOTSTRAP_SERVERS);
        this.topic = "mytopic"; // map.getOrDefault(TOPIC_ENV_VAR, DEFAULT_TOPIC);
        this.numMessages = Integer.parseInt(map.getOrDefault(NUM_MESSAGES_ENV_VAR, DEFAULT_NUM_MESSAGES));
        this.delay = Long.parseLong(map.getOrDefault(DELAY_ENV_VAR, DEFAULT_DELAY));
    }

    public Properties loadKafkaProducerProperties() {
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public void createKafkaProducer(Properties props) {
        this.producer = new KafkaProducer<>(props);
    }
}
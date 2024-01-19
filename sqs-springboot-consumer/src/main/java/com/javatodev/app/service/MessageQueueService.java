package com.javatodev.app.service;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
// import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
// import org.springframework.cloud.aws.messaging.listener.annotation.Headers;
// import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
// import com.amazonaws.services.sqs.model.Message;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageQueueService {
    @Autowired
    AmazonSQSAsync amazonSQSAsync;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accesskey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String awsSecretKeyId;

    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public void SpringCloudSQSService(AmazonSQSAsync amazonSQSAsync) {
        this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSQSAsync);
    }

    // @Valid T paramT, MessageHeaders paramMessageHeaders, Acknowledgment
    // paramAcknowledgment
    // @WithSpan(kind = SpanKind.SERVER)
    @SqsListener(value = "${spring.cloud.aws.sqs.endpoint}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listen(String message, @Headers Map<String, String> headers) {
        Span span = OpenTelUtils.addTrace(headers, "sqs-listener",
                SpanKind.CONSUMER);

        // log.info("Message headers", headers);
        // for (Map.Entry<String, String> entry : headers.entrySet()) {
        // log.info(entry.getKey() + ":" + entry.getValue().toString());
        // }

        log.info("Message received on listen method at {}", OffsetDateTime.now());
        try {
            // try {
            log.info("Sending message to Kafka");

            KafkaProducer.sendToKafka(headers);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (span != null) {
                System.out.println("Ending SPAN");
                span.end();
            }
        }
    }

}

package com.javatodev.app.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.api.trace.SpanKind;

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
    // private final AmazonSQS amazonSQSClient;

    // @Valid T paramT, MessageHeaders paramMessageHeaders, Acknowledgment
    // paramAcknowledgment
    @WithSpan(kind = SpanKind.SERVER)
    @SqsListener("${spring.cloud.aws.sqs.endpoint}")
    public void listen(String message) {
        log.info("Message received on listen method at {}", OffsetDateTime.now());

    }

    // @Scheduled(fixedDelay = 5000) // executes on every 5 second gap.
    // public void receiveMessages() {

    // log.info("DEBUG " + accesskey);
    // log.info("DEBUG " + awsSecretKeyId);

    // try {
    // String queueUrl =
    // amazonSQSClient.getQueueUrl(messageQueueTopic).getQueueUrl();
    // log.info("Reading SQS Queue done: URL {}", queueUrl);

    // ReceiveMessageResult receiveMessageResult =
    // amazonSQSClient.receiveMessage(queueUrl);

    // if (!receiveMessageResult.getMessages().isEmpty()) {
    // Message message = receiveMessageResult.getMessages().get(0);
    // log.info("Incoming Message From SQS {}", message.getMessageId());
    // log.info("Message Body {}", message.getBody());
    // processInvoice(message.getBody());
    // amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
    // }

    // } catch (QueueDoesNotExistException e) {
    // log.error("Queue does not exist {}", e.getMessage());
    // }
    // }
}

package com.javatodev.app.service;

import java.util.Map;
import java.util.Properties;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaProducer extends BaseProducer {

    public static void sendToKafka(Map<String, String> headers) throws InterruptedException {
        // configureOpenTelemetry();

        KafkaProducer producer = new KafkaProducer();
        producer.loadConfiguration(System.getenv());
        Properties props = producer.loadKafkaProducerProperties();
        // props.setProperty(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
        // TracingProducerInterceptor.class.getName());
        producer.createKafkaProducer(props);

        Span span = OpenTelUtils.addTrace(headers, "producer-run", SpanKind.SERVER);
        Thread producerThread = null;
        try (Scope scope = span.makeCurrent()){

            log.info("DEBUG producer.run()");
            producerThread = new Thread(() -> producer.run(headers));

        } finally {
            if (span != null) {
            span.end();
            }
        }

        producerThread.start();

    }

    // private static void configureOpenTelemetry() {
    // Resource resource = Resource.getDefault()
    // .merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME,
    // "my-kafka-service")));

    // SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
    // .addSpanProcessor(BatchSpanProcessor.builder(JaegerGrpcSpanExporter.builder().build()).build())
    // .setSampler(Sampler.alwaysOn())
    // .setResource(resource)
    // .build();

    // OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
    // .setTracerProvider(sdkTracerProvider)
    // .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
    // .buildAndRegisterGlobal();
    // }
}
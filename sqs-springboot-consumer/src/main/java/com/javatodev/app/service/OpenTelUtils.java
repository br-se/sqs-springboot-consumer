package com.javatodev.app.service;

import java.util.Map;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenTelUtils {

    static OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .buildAndRegisterGlobal();
    static Tracer tracer = openTelemetry.getTracer("dynatrace-opentel", "1.0.0");

    static String returnTraceParent(Map<String, String> carrier) {
        String traceparent = null;

        for (Map.Entry<String, String> entry : carrier.entrySet()) {
            if (entry.getKey().equals("traceparent")) {
                traceparent = (String) entry.getValue();
            }
        }

        return traceparent;
    }

    static TextMapGetter<Map<String, String>> getter = new TextMapGetter<>() {
        @Override
        public String get(Map<String, String> carrier, String key) {
            String traceparent = null;

            System.out.println(carrier.entrySet().toString());

            for (Map.Entry<String, String> entry : carrier.entrySet()) {
                if (entry.getKey().equals("traceparent")) {
                    traceparent = (String) entry.getValue();
                    log.info("DEBUG traceparent");
                    System.out.println(traceparent);
                }
            }

            return traceparent;
        }

        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            throw new UnsupportedOperationException("Unimplemented method 'keys'");
        }

    };

    public static Span addTrace(Map<String, String> headers, String spanName, SpanKind kind) {
        var propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
        var parentContext = propagator.extract(Context.current(), headers, getter);

        log.info("DEBUG parentContext");
        System.out.println(parentContext);

        return tracer.spanBuilder(spanName)
                .setParent(parentContext)
                .setSpanKind(kind)
                .startSpan();

    }
}

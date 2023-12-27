package com.javatodev.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.opentelemetry.sdk.OpenTelemetrySdk;

@EnableScheduling
@SpringBootApplication(exclude = {
        org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration.class,
        org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration.class,
        org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration.class
})
public class SqsSpringbootConsumerApplication {

    public static void main(String[] args) {

        SpringApplication.run(SqsSpringbootConsumerApplication.class, args);
    }

    // private static void initOpenTelemetry() {
    // OpenTelemetrySdk sdk = AutoConfiguredOpenTelemetrySdk.builder()
    // .addResourceCustomizer((resource, properties) -> {
    // Resource dtMetadata = Resource.empty();

    // for (String name : new String[] {
    // "dt_metadata_e617c525669e072eebe3d0f08212e8f2.properties",
    // "/var/lib/dynatrace/enrichment/dt_metadata.properties" }) {
    // try {
    // Properties props = new Properties();
    // props.load(name.startsWith("/var") ? new FileInputStream(name)
    // : new FileInputStream(Files.readAllLines(Paths.get(name)).get(0)));
    // dtMetadata = dtMetadata.merge(Resource.create(props.entrySet().stream()
    // .collect(Attributes::builder,
    // (b, e) -> b.put(e.getKey().toString(), e.getValue().toString()),
    // (b1, b2) -> b1.putAll(b2.build()))
    // .build()));
    // } catch (IOException e) {
    // }
    // }

    // return resource.merge(dtMetadata);
    // }).build().getOpenTelemetrySdk();
    // OpenTelemetryAppender.install(sdk);
    // }
}

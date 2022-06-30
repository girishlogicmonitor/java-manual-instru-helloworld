package com.yourcompany.lmoteldemo;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Controller
@Configuration
@SpringBootApplication
@RequestMapping(path = "/yourcompany")
public class Helloworld {

    public static OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

    private static final String SERVICE_NAME = "yourcompany-primary-service";   //

    static Integer myPort = 0;

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        myPort = event.getWebServer().getPort();
    }


    public static void main(String[] args) {
        SpringApplication.run(Helloworld.class, args);
    }

    static {

        // field passed by _JAVA_OPTIONS or JAVA_OPTS or export command to env
        System.setProperty("OTEL_SERVICE_NAME", SERVICE_NAME);
        System.setProperty("OTEL_RESOURCE_ATTRIBUTES", "host.name=cusmersupportservice_dc02,service.name=cusmersupportservice,service.namespace=,ip=192.168.13.02,datacenter=p01-gcp.yourcompany,foo=bar");

        // your working identy token with logic monitor platform having tracing enabled
        //System.setProperty("LOGICMONITOR_BEARER_TOKEN", "<your-company-token>");

        // initialise application start scope sdkTracerProvider
        // Resource serviceResource = LMResourceDetector.detect();


        //Create Resource
        AttributesBuilder attrBuilders = Attributes.builder()
                .put(ResourceAttributes.SERVICE_NAME, SERVICE_NAME)
                .put(ResourceAttributes.SERVICE_NAMESPACE, "yourcompanyproduct")
                .put(ResourceAttributes.HOST_NAME, "dc2.us-west-1.yourcompany.com");

        //Resource serviceResource = LMResourceDetector.detect();
        Resource serviceResource = Resource
                .create(attrBuilders.build());

        String endpoint = "https://test.lmoteldev.logicmonitordev.com";

        //Create Span Exporter either an http or an GRPC
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(endpoint)
                .build();

        System.out.println("Endpoint for tracing => "+endpoint);

        //Create SdkTracerProvider so GlobalOpenTelemetry can understand
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                        .setScheduleDelay(200, TimeUnit.MILLISECONDS).build())
                .setResource(serviceResource)
                .build();

        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder().build()).build())
                .build();

        openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setMeterProvider(meterProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

    @RequestMapping("/random")
    public @ResponseBody ResponseEntity<String> performDummyOperation(HttpServletRequest request) {

        System.out.println(request.getRequestURL());
        Integer limit = 3;  // send as many traces you want to understand further deeper
        for (int i = 0; i <= limit; ++i) {
            try {
                YourCompanyOperation operation = new YourCompanyOperation();
                operation.performCompanyRootOperation();
                //Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok("Done");
    }
}

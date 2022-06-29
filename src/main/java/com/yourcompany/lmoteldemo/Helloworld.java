package com.yourcompany.lmoteldemo;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

import java.util.concurrent.TimeUnit;

public class Helloworld {

    private static final String SERVICE_NAME = "yourcompany-primary-service";

    static {
        /*
        // field passed by _JAVA_OPTIONS or JAVA_OPTS or export command to env
        System.setProperty("OTEL_SERVICE_NAME", SERVICE_NAME);
        System.setProperty("OTEL_RESOURCE_ATTRIBUTES", "host.name=cusmersupportservice_dc02,service.name=cusmersupportservice,service.namespace=,ip=192.168.13.02,datacenter=p01-gcp.yourcompany,foo=bar");

        // your working identy token with logic monitor platform having tracing enabled
        System.setProperty("LOGICMONITOR_BEARER_TOKEN", "<your-company-token>");

        // initialise application start scope sdkTracerProvider
        // Resource serviceResource = LMResourceDetector.detect();
*/
        
        //Create Resource
        AttributesBuilder attrBuilders = Attributes.builder()
                .put(ResourceAttributes.SERVICE_NAME, SERVICE_NAME)
                .put(ResourceAttributes.SERVICE_NAMESPACE, "us-west-1")
                .put(ResourceAttributes.HOST_NAME, "dc2.us-west-1.yourcompany.com");

        //Resource serviceResource = LMResourceDetector.detect();
        Resource serviceResource = Resource
                .create(attrBuilders.build());


        //Create Span Exporter either an http or an GRPC
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                //.setEndpoint("https://qauattraces01.logicmonitor.com/rest")
                .setEndpoint("http://localhost:55680")
                .build();

        //Create SdkTracerProvider so GlobalOpenTelemetry can understand
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                        .setScheduleDelay(200, TimeUnit.MILLISECONDS).build())
                .setResource(serviceResource)
                .build();
    }

    public static void main(String[] args) {
        YourCompanyOperation operation = new YourCompanyOperation();
        operation.performCompanyRootOperation();
    }
}

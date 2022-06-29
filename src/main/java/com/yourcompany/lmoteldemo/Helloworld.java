package com.yourcompany.lmoteldemo;

import com.logicmonitor.resource.LMResourceDetector;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

import java.util.concurrent.TimeUnit;

public class Helloworld {

    private static final String SERVICE_NAME = "yourcompany-primary-service";

    static {
        // initialise application start scope sdkTracerProvider
        Resource serviceResource = LMResourceDetector.detect();

        // field passed by _JAVA_OPTIONS or JAVA_OPTS or export command to env
        System.setProperty("OTEL_SERVICE_NAME", SERVICE_NAME);
        System.setProperty("OTEL_RESOURCE_ATTRIBUTES", "host.name=cusmersupportservice_dc02,service.name=cusmersupportservice,service.namespace=,ip=192.168.13.02,datacenter=p01-gcp.yourcompany,foo=bar"/*do custom works?*/);

        // your working identy token with logic monitor platform having tracing enabled
        System.setProperty("LOGICMONITOR_BEARER_TOKEN", "<your-company-token>");


        //Create Span Exporter either an http or an GRPC
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("https://qauattraces01.logicmonitor.com/rest")
                //.setEndpoint("http://localhost:55680")
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

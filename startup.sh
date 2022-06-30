#! /bin/bash

export OTEL_METRICS_EXPORTER=logging
export OTEL_TRACES_EXPORTER=logging


java -javaagent:opentelemetry-javaagent-all.jar -Dotel.metrics.exporter=none -Dotel.traces.exporter=otlp -Dotel.resource.attributes=host.name=cusmersupportservice_dc_8080,service.name=cusmersupportservice,service.namespace=,ip=192.168.13.02 -Dotel.exporter.otlp.insecure=true -Dotel.exporter.otlp.endpoint=http://10.55.13.130:4317 -jar -Dserver.port=8080 target/*.jar




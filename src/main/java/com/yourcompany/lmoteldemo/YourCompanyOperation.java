package com.yourcompany.lmoteldemo;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class YourCompanyOperation {

    // take handle of Tracer
    Tracer tracer = GlobalOpenTelemetry.getTracer("company-xoperation-instrumentation");

    /*
    *   ROOT OPERATION
    * */
    public void performCompanyRootOperation() {
        Span parentSpan = tracer.spanBuilder("searchAnalytics").startSpan();
        parentSpan.setAttribute("priority", "business.priority");
        parentSpan.setAttribute("severity", "none");
        parentSpan.setAttribute("traffic", "peak");
        parentSpan.setAttribute("serviceHealthOk", true);

        try (Scope scope = parentSpan.makeCurrent()) {
            Thread.sleep(200); // mimic something going on in io
            boolean performInternalOperation = companyInternalOperations("xyz");

        } catch (Throwable t) {
            parentSpan.setStatus(StatusCode.ERROR, "This will be some message we see with alert");  // there is a problem performing primary operation
        } finally {
            parentSpan.end(); // primary operation finishes
        }
    }

    /*
    *  CHILD OPERATION 1
    * */
    public boolean companyInternalOperations(String foo) {
        Span childSpan = tracer.spanBuilder("yourCompanyInternalOperation").startSpan();
        try {
            Thread.sleep(200);  // mimic something going on in io
            childSpan.setStatus(StatusCode.OK);
            Attributes eventAttributes = Attributes.builder().put("input", foo)
                    /*add any new inputs*/
                    .build();
            childSpan.addEvent("secondaryOperationFinishes", eventAttributes);
        } catch (InterruptedException e) {
            childSpan.setStatus(StatusCode.ERROR, "internal operation failure due to " + e.getMessage());
        } finally {
            childSpan.end();    // secondary operation finishes
        }
        return true;
    }
}

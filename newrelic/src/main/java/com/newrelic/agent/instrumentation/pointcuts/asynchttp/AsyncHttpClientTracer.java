// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.asynchttp;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import java.util.List;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.dispatchers.AsyncDispatcher;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.tracers.ExternalComponentNameFormat;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.TransactionActivityInitiator;
import com.newrelic.agent.tracers.AbstractCrossProcessTracer;

public class AsyncHttpClientTracer extends AbstractCrossProcessTracer implements TransactionActivityInitiator
{
    private Object response;
    private final String txName;
    private final long startTime2;
    
    public AsyncHttpClientTracer(final Transaction transaction, final String txName, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final long startTime, final String methodName) {
        super(transaction, sig, object, host, library, uri, methodName);
        this.setMetricNameFormat(ExternalComponentNameFormat.create(host, library, true, uri, new String[0]));
        this.txName = txName;
        this.startTime2 = startTime;
    }
    
    protected String getHeaderValue(final Object returnValue, final String name) {
        if (this.response == null) {
            return null;
        }
        if (this.response instanceof WSResponse) {
            this.response = ((WSResponse)this.response)._nr_response();
        }
        if (this.response instanceof Response) {
            return ((Response)this.response).getHeader(name);
        }
        return null;
    }
    
    public void setResponse(final Object response) {
        this.response = response;
    }
    
    public long getStartTime() {
        return this.startTime2;
    }
    
    public String getUri() {
        return this.txName;
    }
    
    public String getHeader(final String name) {
        return null;
    }
    
    public Dispatcher createDispatcher() {
        return new AsyncDispatcher(this.getTransaction(), new SimpleMetricNameFormat(this.getUri()));
    }
    
    @InterfaceMixin(originalClassName = { "com/ning/http/client/Response" })
    public interface Response
    {
        public static final String CLASS = "com/ning/http/client/Response";
        
        String getHeader(String p0);
        
        List<String> getHeaders(String p0);
    }
    
    @InterfaceMixin(originalClassName = { "play/api/libs/ws/Response" })
    public interface WSResponse
    {
        public static final String CLASS = "play/api/libs/ws/Response";
        
        @FieldAccessor(fieldName = "ahcResponse", fieldDesc = "Lcom/ning/http/client/Response;", existingField = true)
        Object _nr_response();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.StatusLine;
import com.newrelic.agent.deps.org.apache.http.client.HttpResponseException;
import com.newrelic.agent.deps.org.apache.http.util.EntityUtils;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.client.ResponseHandler;

@Immutable
public class BasicResponseHandler implements ResponseHandler<String>
{
    public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return (entity == null) ? null : EntityUtils.toString(entity);
    }
}

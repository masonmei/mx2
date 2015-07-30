// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.utilization;

import com.newrelic.agent.stats.StatsWorks;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.org.apache.http.conn.ssl.X509HostnameVerifier;
import com.newrelic.agent.deps.org.apache.http.conn.ssl.StrictHostnameVerifier;
import com.newrelic.agent.deps.org.apache.http.client.config.RequestConfig;
import com.newrelic.agent.deps.org.apache.http.config.SocketConfig;
import com.newrelic.agent.deps.org.apache.http.impl.client.HttpClientBuilder;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.impl.client.CloseableHttpClient;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.util.EntityUtils;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpUriRequest;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpGet;
import com.newrelic.agent.deps.org.apache.http.conn.ConnectTimeoutException;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;

public class AWS
{
    protected static final String INSTANCE_TYPE_URL = "http://169.254.169.254/2008-02-01/meta-data/instance-type";
    protected static final String INSTANCE_ID_URL = "http://169.254.169.254/2008-02-01/meta-data/instance-id";
    protected static final String INSTANCE_AVAILABLITY_ZONE = "http://169.254.169.254/2008-02-01/meta-data/placement/availability-zone";
    private static int requestTimeoutInMillis;
    private static final int MIN_CHAR_CODEPOINT;
    
    protected AwsData getAwsData() {
        final String type = this.getAwsValue("http://169.254.169.254/2008-02-01/meta-data/instance-type");
        final String id = (type == null) ? null : this.getAwsValue("http://169.254.169.254/2008-02-01/meta-data/instance-id");
        final String zone = (type == null && id == null) ? null : this.getAwsValue("http://169.254.169.254/2008-02-01/meta-data/placement/availability-zone");
        if (type == null || id == null || zone == null) {
            return AwsData.EMPTY_DATA;
        }
        return new AwsData(id, type, zone);
    }
    
    protected String getAwsValue(final String url) {
        try {
            final String value = this.makeHttpRequest(url);
            if (this.isInvalidAwsValue(value)) {
                Agent.LOG.log(Level.WARNING, MessageFormat.format("Failed to validate AWS value {0}", value));
                recordAwsError();
                return null;
            }
            return value.trim();
        }
        catch (ConnectTimeoutException e) {}
        catch (Throwable t) {
            Agent.LOG.log(Level.FINEST, MessageFormat.format("Error occurred trying to get AWS value. {0}", t));
            recordAwsError();
        }
        return null;
    }
    
    protected boolean isInvalidAwsValue(final String value) {
        if (value == null) {
            return true;
        }
        if (value.getBytes().length > 255) {
            return true;
        }
        for (int i = 0; i < value.length(); ++i) {
            final char c = value.charAt(i);
            if (c < '0' || c > '9') {
                if (c < 'a' || c > 'z') {
                    if (c < 'A' || c > 'Z') {
                        if (c != ' ' && c != '_' && c != '.' && c != '/') {
                            if (c != '-') {
                                if (c <= AWS.MIN_CHAR_CODEPOINT) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    protected String makeHttpRequest(final String url) throws IOException {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = configureHttpClient();
            final HttpGet httpGet = new HttpGet(url);
            final CloseableHttpResponse response = httpclient.execute((HttpUriRequest)httpGet);
            if (response.getStatusLine().getStatusCode() <= 207) {
                final String string = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (httpclient != null) {
                    try {
                        httpclient.close();
                    }
                    catch (IOException ex) {}
                }
                return string;
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                }
                catch (IOException ex2) {}
            }
        }
        finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                }
                catch (IOException ex3) {}
            }
        }
        return null;
    }
    
    private static CloseableHttpClient configureHttpClient() {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(AWS.requestTimeoutInMillis).setSoKeepAlive(true).build());
        final RequestConfig.Builder requestBuilder = RequestConfig.custom().setConnectTimeout(AWS.requestTimeoutInMillis).setConnectionRequestTimeout(AWS.requestTimeoutInMillis).setSocketTimeout(AWS.requestTimeoutInMillis);
        builder.setDefaultRequestConfig(requestBuilder.build());
        builder.setHostnameVerifier(new StrictHostnameVerifier());
        return builder.build();
    }
    
    private static void recordAwsError() {
        ServiceFactory.getStatsService().doStatsWork(StatsWorks.getIncrementCounterWork("Supportability/utilization/aws/error", 1));
    }
    
    static {
        AWS.requestTimeoutInMillis = 100;
        MIN_CHAR_CODEPOINT = "\u007f".codePointAt(0);
    }
    
    protected static class AwsData
    {
        private final String instanceId;
        private final String instanceType;
        private final String availablityZone;
        static final AwsData EMPTY_DATA;
        
        private AwsData() {
            this.instanceId = null;
            this.instanceType = null;
            this.availablityZone = null;
        }
        
        protected AwsData(final String id, final String type, final String zone) {
            this.instanceId = id;
            this.instanceType = type;
            this.availablityZone = zone;
        }
        
        public String getInstanceId() {
            return this.instanceId;
        }
        
        public String getInstanceType() {
            return this.instanceType;
        }
        
        public String getAvailabityZone() {
            return this.availablityZone;
        }
        
        static {
            EMPTY_DATA = new AwsData();
        }
    }
}

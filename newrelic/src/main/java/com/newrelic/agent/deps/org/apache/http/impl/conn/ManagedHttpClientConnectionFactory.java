// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import com.newrelic.agent.deps.org.apache.http.HttpConnection;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import com.newrelic.agent.deps.org.apache.http.entity.ContentLengthStrategy;
import java.nio.charset.CodingErrorAction;
import com.newrelic.agent.deps.org.apache.http.config.ConnectionConfig;
import com.newrelic.agent.deps.org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageParserFactory;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageWriterFactory;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import java.util.concurrent.atomic.AtomicLong;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.conn.ManagedHttpClientConnection;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.conn.HttpConnectionFactory;

@Immutable
public class ManagedHttpClientConnectionFactory implements HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>
{
    private static final AtomicLong COUNTER;
    public static final ManagedHttpClientConnectionFactory INSTANCE;
    private final Log log;
    private final Log headerlog;
    private final Log wirelog;
    private final HttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final HttpMessageParserFactory<HttpResponse> responseParserFactory;
    
    public ManagedHttpClientConnectionFactory(final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        this.log = LogFactory.getLog(DefaultManagedHttpClientConnection.class);
        this.headerlog = LogFactory.getLog("com.newrelic.agent.deps.org.apache.http.headers");
        this.wirelog = LogFactory.getLog("com.newrelic.agent.deps.org.apache.http.wire");
        this.requestWriterFactory = ((requestWriterFactory != null) ? requestWriterFactory : DefaultHttpRequestWriterFactory.INSTANCE);
        this.responseParserFactory = ((responseParserFactory != null) ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE);
    }
    
    public ManagedHttpClientConnectionFactory(final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        this(null, responseParserFactory);
    }
    
    public ManagedHttpClientConnectionFactory() {
        this(null, null);
    }
    
    public ManagedHttpClientConnection create(final HttpRoute route, final ConnectionConfig config) {
        final ConnectionConfig cconfig = (config != null) ? config : ConnectionConfig.DEFAULT;
        CharsetDecoder chardecoder = null;
        CharsetEncoder charencoder = null;
        final Charset charset = cconfig.getCharset();
        final CodingErrorAction malformedInputAction = (cconfig.getMalformedInputAction() != null) ? cconfig.getMalformedInputAction() : CodingErrorAction.REPORT;
        final CodingErrorAction unmappableInputAction = (cconfig.getUnmappableInputAction() != null) ? cconfig.getUnmappableInputAction() : CodingErrorAction.REPORT;
        if (charset != null) {
            chardecoder = charset.newDecoder();
            chardecoder.onMalformedInput(malformedInputAction);
            chardecoder.onUnmappableCharacter(unmappableInputAction);
            charencoder = charset.newEncoder();
            charencoder.onMalformedInput(malformedInputAction);
            charencoder.onUnmappableCharacter(unmappableInputAction);
        }
        final String id = "http-outgoing-" + Long.toString(ManagedHttpClientConnectionFactory.COUNTER.getAndIncrement());
        return new LoggingManagedHttpClientConnection(id, this.log, this.headerlog, this.wirelog, cconfig.getBufferSize(), cconfig.getFragmentSizeHint(), chardecoder, charencoder, cconfig.getMessageConstraints(), null, null, this.requestWriterFactory, this.responseParserFactory);
    }
    
    static {
        COUNTER = new AtomicLong();
        INSTANCE = new ManagedHttpClientConnectionFactory();
    }
}

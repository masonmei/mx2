// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.entity;

import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import com.newrelic.agent.deps.org.apache.http.entity.ContentType;
import com.newrelic.agent.deps.org.apache.http.client.utils.URLEncodedUtils;
import com.newrelic.agent.deps.org.apache.http.protocol.HTTP;
import com.newrelic.agent.deps.org.apache.http.NameValuePair;
import java.util.List;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.entity.StringEntity;

@NotThreadSafe
public class UrlEncodedFormEntity extends StringEntity
{
    public UrlEncodedFormEntity(final List<? extends NameValuePair> parameters, final String charset) throws UnsupportedEncodingException {
        super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create("application/x-www-form-urlencoded", charset));
    }
    
    public UrlEncodedFormEntity(final Iterable<? extends NameValuePair> parameters, final Charset charset) {
        super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET), ContentType.create("application/x-www-form-urlencoded", charset));
    }
    
    public UrlEncodedFormEntity(final List<? extends NameValuePair> parameters) throws UnsupportedEncodingException {
        this(parameters, (Charset)null);
    }
    
    public UrlEncodedFormEntity(final Iterable<? extends NameValuePair> parameters) {
        this(parameters, null);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Pattern;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public abstract class AnalyticsEvent implements JSONStreamAware
{
    private static final Pattern TYPE_VALID;
    final String type;
    final long timestamp;
    Map<String, Object> userAttributes;
    
    public AnalyticsEvent(final String type, final long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }
    
    public abstract void writeJSONString(final Writer p0) throws IOException;
    
    public static boolean isValidType(final String type) {
        return AnalyticsEvent.TYPE_VALID.matcher(type).matches();
    }
    
    public boolean isValid() {
        return isValidType(this.type);
    }
    
    static {
        TYPE_VALID = Pattern.compile("^[a-zA-Z0-9:_ ]{1,255}$");
    }
}

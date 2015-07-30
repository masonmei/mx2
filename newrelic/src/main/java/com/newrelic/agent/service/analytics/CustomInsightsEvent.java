// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import com.newrelic.agent.deps.org.json.simple.JSONObject;
import java.io.Writer;
import java.util.Map;

public class CustomInsightsEvent extends AnalyticsEvent
{
    public CustomInsightsEvent(final String type, final long timestamp, final Map<String, Object> attributes) {
        super(type, timestamp);
        this.userAttributes = attributes;
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        final JSONObject intrinsics = new JSONObject();
        intrinsics.put("type", this.type);
        intrinsics.put("timestamp", this.timestamp);
        JSONArray.writeJSONString(Arrays.asList(intrinsics, this.userAttributes), out);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONObject;
import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class JSONException extends Exception implements JSONStreamAware
{
    private static final long serialVersionUID = 3132223563667774992L;
    
    public JSONException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final Throwable cause) {
        super(cause);
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        JSONObject.writeJSONString(new HashMap<String, Map>() {
            {
                final Map<String, Object> vals = new HashMap<String, Object>();
                vals.put("message", JSONException.this.getMessage());
                final Object cause = JSONException.this.getCause();
                if (cause != null) {
                    vals.put("type", cause.getClass().getName());
                }
                vals.put("backtrace", StackTraces.stackTracesToStrings(JSONException.this.getStackTrace()));
                ((HashMap<String, Map<String, Object>>)this).put("exception", vals);
            }
        }, out);
    }
}

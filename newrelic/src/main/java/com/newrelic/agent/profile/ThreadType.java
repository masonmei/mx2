// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONValue;
import java.io.Writer;
import com.newrelic.agent.deps.org.json.simple.JSONAware;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public interface ThreadType extends JSONStreamAware, JSONAware
{
    String getName();
    
    public enum BasicThreadType implements ThreadType
    {
        AGENT("agent"), 
        AGENT_INSTRUMENTATION("agent_instrumentation"), 
        REQUEST("request"), 
        BACKGROUND("background"), 
        OTHER("other");
        
        private String name;
        
        private BasicThreadType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void writeJSONString(Writer out) throws IOException {
            JSONValue.writeJSONString(this.name, out);
        }
        
        public String toJSONString() {
            return this.name;
        }
    }
}

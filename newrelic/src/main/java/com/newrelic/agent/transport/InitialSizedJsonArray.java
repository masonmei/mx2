// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class InitialSizedJsonArray implements JSONStreamAware
{
    private List<Object> toSend;
    
    public InitialSizedJsonArray(final int size) {
        if (size > 0) {
            this.toSend = new ArrayList<Object>(size);
        }
        else {
            this.toSend = Collections.emptyList();
        }
    }
    
    public void add(final Object obj) {
        this.toSend.add(obj);
    }
    
    public void addAll(final Collection<Object> objs) {
        this.toSend.addAll(objs);
    }
    
    public int size() {
        return this.toSend.size();
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        JSONArray.writeJSONString(this.toSend, out);
    }
}

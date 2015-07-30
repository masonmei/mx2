// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.module;

import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableList;
import java.io.Writer;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class Jar implements JSONStreamAware, Cloneable
{
    private final String name;
    private final JarInfo jarInfo;
    
    public Jar(final String name, final JarInfo jarInfo) {
        this.name = name;
        this.jarInfo = jarInfo;
    }
    
    protected String getName() {
        return this.name;
    }
    
    protected String getVersion() {
        return this.jarInfo.version;
    }
    
    public void writeJSONString(final Writer pWriter) throws IOException {
        final List<Object> toSend = ImmutableList.of(this.name, this.jarInfo.version, this.jarInfo.attributes);
        JSONArray.writeJSONString(toSend, pWriter);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.getVersion() == null) ? 0 : this.getVersion().hashCode());
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Jar other = (Jar)obj;
        if (this.getVersion() == null) {
            if (other.getVersion() != null) {
                return false;
            }
        }
        else if (!this.getVersion().equals(other.getVersion())) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
}

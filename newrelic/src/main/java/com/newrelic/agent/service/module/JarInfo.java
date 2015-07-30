// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.module;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import java.util.Map;

class JarInfo
{
    static final JarInfo MISSING;
    public final String version;
    public final Map<String, String> attributes;
    
    public JarInfo(final String version, final Map<String, String> attributes) {
        this.version = ((version == null) ? " " : version);
        this.attributes = (Map<String, String>)((attributes == null) ? ImmutableMap.of() : ImmutableMap.copyOf((Map<?, ?>)attributes));
    }
    
    public String toString() {
        return "JarInfo [version=" + this.version + ", attributes=" + this.attributes + "]";
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
        result = 31 * result + ((this.version == null) ? 0 : this.version.hashCode());
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
        final JarInfo other = (JarInfo)obj;
        if (this.attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        }
        else if (!this.attributes.equals(other.attributes)) {
            return false;
        }
        if (this.version == null) {
            if (other.version != null) {
                return false;
            }
        }
        else if (!this.version.equals(other.version)) {
            return false;
        }
        return true;
    }
    
    static {
        MISSING = new JarInfo(" ", (Map<String, String>)ImmutableMap.of());
    }
}

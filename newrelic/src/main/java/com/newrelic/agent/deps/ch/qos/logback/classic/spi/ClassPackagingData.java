// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import java.io.Serializable;

public class ClassPackagingData implements Serializable
{
    private static final long serialVersionUID = -804643281218337001L;
    final String codeLocation;
    final String version;
    private final boolean exact;
    
    public ClassPackagingData(final String codeLocation, final String version) {
        this.codeLocation = codeLocation;
        this.version = version;
        this.exact = true;
    }
    
    public ClassPackagingData(final String classLocation, final String version, final boolean exact) {
        this.codeLocation = classLocation;
        this.version = version;
        this.exact = exact;
    }
    
    public String getCodeLocation() {
        return this.codeLocation;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public boolean isExact() {
        return this.exact;
    }
    
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + ((this.codeLocation == null) ? 0 : this.codeLocation.hashCode());
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
        final ClassPackagingData other = (ClassPackagingData)obj;
        if (this.codeLocation == null) {
            if (other.codeLocation != null) {
                return false;
            }
        }
        else if (!this.codeLocation.equals(other.codeLocation)) {
            return false;
        }
        if (this.exact != other.exact) {
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
}

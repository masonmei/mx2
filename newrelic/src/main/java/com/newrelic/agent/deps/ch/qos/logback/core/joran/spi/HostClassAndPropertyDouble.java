// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

public class HostClassAndPropertyDouble
{
    final Class hostClass;
    final String propertyName;
    
    public HostClassAndPropertyDouble(final Class hostClass, final String propertyName) {
        this.hostClass = hostClass;
        this.propertyName = propertyName;
    }
    
    public Class getHostClass() {
        return this.hostClass;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.hostClass == null) ? 0 : this.hostClass.hashCode());
        result = 31 * result + ((this.propertyName == null) ? 0 : this.propertyName.hashCode());
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
        final HostClassAndPropertyDouble other = (HostClassAndPropertyDouble)obj;
        if (this.hostClass == null) {
            if (other.hostClass != null) {
                return false;
            }
        }
        else if (!this.hostClass.equals(other.hostClass)) {
            return false;
        }
        if (this.propertyName == null) {
            if (other.propertyName != null) {
                return false;
            }
        }
        else if (!this.propertyName.equals(other.propertyName)) {
            return false;
        }
        return true;
    }
}

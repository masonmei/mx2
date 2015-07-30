// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.util.HashMap;
import java.util.Map;

public class DefaultNestedComponentRegistry
{
    Map<HostClassAndPropertyDouble, Class> defaultComponentMap;
    
    public DefaultNestedComponentRegistry() {
        this.defaultComponentMap = new HashMap<HostClassAndPropertyDouble, Class>();
    }
    
    public void add(final Class hostClass, final String propertyName, final Class componentClass) {
        final HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName.toLowerCase());
        this.defaultComponentMap.put(hpDouble, componentClass);
    }
    
    public Class findDefaultComponentType(Class hostClass, String propertyName) {
        propertyName = propertyName.toLowerCase();
        while (hostClass != null) {
            final Class componentClass = this.oneShotFind(hostClass, propertyName);
            if (componentClass != null) {
                return componentClass;
            }
            hostClass = hostClass.getSuperclass();
        }
        return null;
    }
    
    private Class oneShotFind(final Class hostClass, final String propertyName) {
        final HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName);
        return this.defaultComponentMap.get(hpDouble);
    }
}

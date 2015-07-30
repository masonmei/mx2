// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.introspector;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.beans.PropertyDescriptor;

public class MethodProperty extends Property
{
    private final PropertyDescriptor property;
    
    public MethodProperty(final PropertyDescriptor property) {
        super(property.getName(), property.getPropertyType());
        this.property = property;
    }
    
    public void set(final Object object, final Object value) throws Exception {
        this.property.getWriteMethod().invoke(object, value);
    }
    
    public Object get(final Object object) {
        try {
            return this.property.getReadMethod().invoke(object, new Object[0]);
        }
        catch (Exception e) {
            throw new YAMLException("Unable to find getter for property " + this.property.getName() + " on object " + object + ":" + e);
        }
    }
}

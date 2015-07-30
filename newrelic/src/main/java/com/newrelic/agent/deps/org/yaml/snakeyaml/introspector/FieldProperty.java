// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.introspector;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.lang.reflect.Field;

public class FieldProperty extends Property
{
    private final Field field;
    
    public FieldProperty(final Field field) {
        super(field.getName(), field.getType());
        this.field = field;
    }
    
    public void set(final Object object, final Object value) throws Exception {
        this.field.set(object, value);
    }
    
    public Object get(final Object object) {
        try {
            return this.field.get(object);
        }
        catch (Exception e) {
            throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + object + " : " + e);
        }
    }
}

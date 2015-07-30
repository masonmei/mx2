// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.application;

import java.text.MessageFormat;
import java.util.Collections;
import com.newrelic.agent.config.BaseConfig;
import java.util.List;
import com.newrelic.api.agent.ApplicationNamePriority;

public class PriorityApplicationName
{
    public static final PriorityApplicationName NONE;
    private final ApplicationNamePriority priority;
    private final String name;
    private final List<String> names;
    
    private PriorityApplicationName(final String name, final ApplicationNamePriority priority) {
        this.priority = priority;
        if (name == null) {
            this.name = null;
            this.names = null;
        }
        else {
            this.names = Collections.unmodifiableList((List<? extends String>)BaseConfig.getUniqueStringsFromString(name, ";"));
            this.name = this.names.get(0);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getNames() {
        return this.names;
    }
    
    public ApplicationNamePriority getPriority() {
        return this.priority;
    }
    
    public String toString() {
        return MessageFormat.format("{0}[name={1}, priority={2}]", this.getClass().getName(), this.getName(), this.getPriority());
    }
    
    public static PriorityApplicationName create(final String name, final ApplicationNamePriority priority) {
        return new PriorityApplicationName(name, priority);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = 31 * result + ((this.priority == null) ? 0 : this.priority.hashCode());
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
        final PriorityApplicationName other = (PriorityApplicationName)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return this.priority == other.priority;
    }
    
    static {
        NONE = create(null, ApplicationNamePriority.NONE);
    }
}

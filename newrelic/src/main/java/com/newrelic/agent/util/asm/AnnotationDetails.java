// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import java.util.Iterator;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Multimaps;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.com.google.common.base.Supplier;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.newrelic.agent.deps.com.google.common.collect.ListMultimap;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;

public class AnnotationDetails extends AnnotationVisitor
{
    final String desc;
    private ListMultimap<String, Object> attributes;
    
    public AnnotationDetails(final AnnotationVisitor av, final String desc) {
        super(327680, av);
        this.desc = desc;
    }
    
    public List<Object> getValues(final String name) {
        if (this.attributes == null) {
            return Collections.emptyList();
        }
        return this.attributes.get(name);
    }
    
    public Object getValue(final String name) {
        final Collection<Object> values = this.getValues(name);
        if (values.isEmpty()) {
            return null;
        }
        return values.iterator().next();
    }
    
    public void visit(final String name, final Object value) {
        this.getOrCreateAttributes().put(name, value);
        super.visit(name, value);
    }
    
    Multimap<String, Object> getOrCreateAttributes() {
        if (this.attributes == null) {
            this.attributes = Multimaps.newListMultimap((Map<String, Collection<Object>>)Maps.newHashMap(), new Supplier<List<Object>>() {
                public List<Object> get() {
                    return Lists.newArrayList();
                }
            });
        }
        return this.attributes;
    }
    
    public boolean equals(final Object obj) {
        if (!(obj instanceof AnnotationDetails)) {
            return super.equals(obj);
        }
        final AnnotationDetails other = (AnnotationDetails)obj;
        if (!this.desc.equals(other.desc)) {
            return false;
        }
        if ((this.attributes == null || other.attributes == null) && this.attributes != other.attributes) {
            return false;
        }
        for (final Map.Entry<String, Object> entry : this.attributes.entries()) {
            final List<Object> list = other.attributes.get(entry.getKey());
            if (!list.contains(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        return "AnnotationDetails [desc=" + this.desc + ", attributes=" + this.attributes + "]";
    }
}

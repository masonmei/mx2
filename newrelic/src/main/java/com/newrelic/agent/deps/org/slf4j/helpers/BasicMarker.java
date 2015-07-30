// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.helpers;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import com.newrelic.agent.deps.org.slf4j.Marker;

public class BasicMarker implements Marker
{
    private static final long serialVersionUID = 1803952589649545191L;
    private final String name;
    private List refereceList;
    private static String OPEN;
    private static String CLOSE;
    private static String SEP;
    
    BasicMarker(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("A marker name cannot be null");
        }
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public synchronized void add(final Marker reference) {
        if (reference == null) {
            throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
        }
        if (this.contains(reference)) {
            return;
        }
        if (reference.contains(this)) {
            return;
        }
        if (this.refereceList == null) {
            this.refereceList = new Vector();
        }
        this.refereceList.add(reference);
    }
    
    public synchronized boolean hasReferences() {
        return this.refereceList != null && this.refereceList.size() > 0;
    }
    
    public boolean hasChildren() {
        return this.hasReferences();
    }
    
    public synchronized Iterator iterator() {
        if (this.refereceList != null) {
            return this.refereceList.iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }
    
    public synchronized boolean remove(final Marker referenceToRemove) {
        if (this.refereceList == null) {
            return false;
        }
        for (int size = this.refereceList.size(), i = 0; i < size; ++i) {
            final Marker m = this.refereceList.get(i);
            if (referenceToRemove.equals(m)) {
                this.refereceList.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final Marker other) {
        if (other == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (this.equals(other)) {
            return true;
        }
        if (this.hasReferences()) {
            for (int i = 0; i < this.refereceList.size(); ++i) {
                final Marker ref = this.refereceList.get(i);
                if (ref.contains(other)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean contains(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (this.name.equals(name)) {
            return true;
        }
        if (this.hasReferences()) {
            for (int i = 0; i < this.refereceList.size(); ++i) {
                final Marker ref = this.refereceList.get(i);
                if (ref.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Marker)) {
            return false;
        }
        final Marker other = (Marker)obj;
        return this.name.equals(other.getName());
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public String toString() {
        if (!this.hasReferences()) {
            return this.getName();
        }
        final Iterator it = this.iterator();
        final StringBuffer sb = new StringBuffer(this.getName());
        sb.append(' ').append(BasicMarker.OPEN);
        while (it.hasNext()) {
            final Marker reference = it.next();
            sb.append(reference.getName());
            if (it.hasNext()) {
                sb.append(BasicMarker.SEP);
            }
        }
        sb.append(BasicMarker.CLOSE);
        return sb.toString();
    }
    
    static {
        BasicMarker.OPEN = "[ ";
        BasicMarker.CLOSE = " ]";
        BasicMarker.SEP = ", ";
    }
}

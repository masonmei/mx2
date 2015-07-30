// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

class SerialVersionUIDAdder$Item implements Comparable
{
    final String name;
    final int access;
    final String desc;
    
    SerialVersionUIDAdder$Item(final String name, final int access, final String desc) {
        this.name = name;
        this.access = access;
        this.desc = desc;
    }
    
    public int compareTo(final SerialVersionUIDAdder$Item serialVersionUIDAdder$Item) {
        int n = this.name.compareTo(serialVersionUIDAdder$Item.name);
        if (n == 0) {
            n = this.desc.compareTo(serialVersionUIDAdder$Item.desc);
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        return o instanceof SerialVersionUIDAdder$Item && this.compareTo((SerialVersionUIDAdder$Item)o) == 0;
    }
    
    public int hashCode() {
        return (this.name + this.desc).hashCode();
    }
}

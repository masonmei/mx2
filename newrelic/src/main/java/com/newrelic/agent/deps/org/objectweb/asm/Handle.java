// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

public final class Handle
{
    final int a;
    final String b;
    final String c;
    final String d;
    
    public Handle(final int a, final String b, final String c, final String d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    
    public int getTag() {
        return this.a;
    }
    
    public String getOwner() {
        return this.b;
    }
    
    public String getName() {
        return this.c;
    }
    
    public String getDesc() {
        return this.d;
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Handle)) {
            return false;
        }
        final Handle handle = (Handle)o;
        return this.a == handle.a && this.b.equals(handle.b) && this.c.equals(handle.c) && this.d.equals(handle.d);
    }
    
    public int hashCode() {
        return this.a + this.b.hashCode() * this.c.hashCode() * this.d.hashCode();
    }
    
    public String toString() {
        return this.b + '.' + this.c + this.d + " (" + this.a + ')';
    }
}

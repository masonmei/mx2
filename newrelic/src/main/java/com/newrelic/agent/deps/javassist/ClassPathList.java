// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist;

final class ClassPathList
{
    ClassPathList next;
    ClassPath path;
    
    ClassPathList(final ClassPath p, final ClassPathList n) {
        this.next = n;
        this.path = p;
    }
}

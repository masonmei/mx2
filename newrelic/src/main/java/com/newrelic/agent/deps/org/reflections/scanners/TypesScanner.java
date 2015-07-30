// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.scanners;

import com.newrelic.agent.deps.org.reflections.vfs.Vfs;

@Deprecated
public class TypesScanner extends AbstractScanner
{
    public Object scan(final Vfs.File file, Object classObject) {
        classObject = super.scan(file, classObject);
        final String className = this.getMetadataAdapter().getClassName(classObject);
        this.getStore().put(className, className);
        return classObject;
    }
    
    public void scan(final Object cls) {
        throw new UnsupportedOperationException("should not get here");
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.scanners;

import com.newrelic.agent.deps.org.reflections.vfs.Vfs;

public class ResourcesScanner extends AbstractScanner
{
    public boolean acceptsInput(final String file) {
        return !file.endsWith(".class");
    }
    
    public Object scan(final Vfs.File file, final Object classObject) {
        this.getStore().put(file.getName(), file.getRelativePath());
        return classObject;
    }
    
    public void scan(final Object cls) {
        throw new UnsupportedOperationException();
    }
}

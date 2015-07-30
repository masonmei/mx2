// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import java.io.FileFilter;

public enum ExtensionFileTypes
{
    XML((FileFilter)new ExtensionFileFilter("xml")), 
    YML((FileFilter)new MultipleExtensionFileFilter(new String[] { "yml", "yaml" })), 
    JAR((FileFilter)new ExtensionFileFilter("jar"));
    
    private FileFilter filter;
    
    private ExtensionFileTypes(final FileFilter pFilter) {
        this.filter = pFilter;
    }
    
    public FileFilter getFilter() {
        return this.filter;
    }
}

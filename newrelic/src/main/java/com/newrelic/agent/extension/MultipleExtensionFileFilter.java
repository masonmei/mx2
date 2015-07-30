// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import java.util.Iterator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.FileFilter;

public class MultipleExtensionFileFilter implements FileFilter
{
    private final List<String> extensions;
    
    public MultipleExtensionFileFilter(final String... pFileExtn) {
        this.extensions = new ArrayList<String>();
        for (final String ext : pFileExtn) {
            if (ext != null && ext.length() != 0 && !ext.startsWith(".")) {
                this.extensions.add("." + ext);
            }
            else {
                this.extensions.add(ext);
            }
        }
    }
    
    public boolean accept(final File pFile) {
        if (pFile != null && pFile.isFile() && pFile.canRead()) {
            final String name = pFile.getName();
            for (final String ext : this.extensions) {
                if (name.endsWith(ext)) {
                    return true;
                }
            }
        }
        return false;
    }
}

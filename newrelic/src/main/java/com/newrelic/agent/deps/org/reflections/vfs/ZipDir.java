// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.vfs;

import java.io.IOException;
import com.newrelic.agent.deps.org.reflections.Reflections;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import com.newrelic.agent.deps.com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class ZipDir implements Vfs.Dir
{
    final ZipFile jarFile;
    
    public ZipDir(final JarFile jarFile) {
        this.jarFile = jarFile;
    }
    
    public String getPath() {
        return this.jarFile.getName();
    }
    
    public Iterable<Vfs.File> getFiles() {
        return new Iterable<Vfs.File>() {
            public Iterator<Vfs.File> iterator() {
                return new AbstractIterator<Vfs.File>() {
                    final Enumeration<? extends ZipEntry> entries = ZipDir.this.jarFile.entries();
                    
                    protected Vfs.File computeNext() {
                        while (this.entries.hasMoreElements()) {
                            final ZipEntry entry = (ZipEntry)this.entries.nextElement();
                            if (!entry.isDirectory()) {
                                return new com.newrelic.agent.deps.org.reflections.vfs.ZipFile(ZipDir.this, entry);
                            }
                        }
                        return this.endOfData();
                    }
                };
            }
        };
    }
    
    public void close() {
        try {
            this.jarFile.close();
        }
        catch (IOException e) {
            if (Reflections.log != null) {
                Reflections.log.warn("Could not close JarFile", e);
            }
        }
    }
    
    public String toString() {
        return this.jarFile.getName();
    }
}

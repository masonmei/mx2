// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.security.SecureClassLoader;

public class CleverClassLoader extends SecureClassLoader
{
    public CleverClassLoader(final ClassLoader parent) {
        super(parent);
    }
    
    public Class loadClassSpecial(final String name) throws ClassNotFoundException, IOException {
        String fileName = name.replace('.', '/');
        fileName += ".class";
        final InputStream inStream = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
        if (inStream == null) {
            throw new ClassNotFoundException("Unable to find class " + name);
        }
        try {
            final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            Streams.copy(inStream, oStream);
            final Class loadClass = this.loadClass(name, oStream.toByteArray());
            inStream.close();
            return loadClass;
        }
        finally {
            inStream.close();
        }
    }
    
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (name.startsWith("com.newrelic")) {
            try {
                return ClassLoader.getSystemClassLoader().loadClass(name);
            }
            catch (NoClassDefFoundError e) {
                try {
                    return (Class<?>)this.loadClassSpecial(name);
                }
                catch (IOException e2) {
                    throw e;
                }
            }
        }
        return super.loadClass(name);
    }
    
    protected Class loadClass(final String name, final byte[] bytes) {
        return this.defineClass(name, bytes, 0, bytes.length);
    }
}

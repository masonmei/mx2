// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.verifier;

import java.net.URL;
import java.net.URLClassLoader;

public class VerificationClassLoader extends URLClassLoader
{
    public VerificationClassLoader(final URL[] urls) {
        super(urls);
    }
    
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.")) {
            return super.loadClass(name, resolve);
        }
        final Class<?> c = this.findClass(name);
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }
}

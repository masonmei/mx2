// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.lang.reflect.Method;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class ProxyClassVisitor extends ClassVisitor
{
    private static final String PROXY_METHOD_DESC;
    private boolean hasProxyMethod;
    
    public ProxyClassVisitor() {
        super(327680);
        this.hasProxyMethod = false;
    }
    
    public ProxyClassVisitor(final ClassVisitor cv) {
        super(327680, cv);
        this.hasProxyMethod = false;
    }
    
    public boolean isProxy() {
        return this.hasProxyMethod;
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (!this.hasProxyMethod && desc.equals(ProxyClassVisitor.PROXY_METHOD_DESC)) {
            this.hasProxyMethod = true;
        }
        return super.visitField(access, name, desc, signature, value);
    }
    
    static {
        PROXY_METHOD_DESC = Type.getDescriptor(Method.class);
    }
}

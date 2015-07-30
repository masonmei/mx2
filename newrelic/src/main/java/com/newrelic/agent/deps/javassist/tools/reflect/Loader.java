// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.tools.reflect;

import com.newrelic.agent.deps.javassist.NotFoundException;
import com.newrelic.agent.deps.javassist.CannotCompileException;
import com.newrelic.agent.deps.javassist.Translator;
import com.newrelic.agent.deps.javassist.ClassPool;
import com.newrelic.agent.deps.javassist.Loader;

public class Loader extends com.newrelic.agent.deps.javassist.Loader
{
    protected Reflection reflection;
    
    public static void main(final String[] args) throws Throwable {
        final Loader cl = new Loader();
        cl.run(args);
    }
    
    public Loader() throws CannotCompileException, NotFoundException {
        this.delegateLoadingOf("com.newrelic.agent.deps.javassist.tools.reflect.Loader");
        this.reflection = new Reflection();
        final ClassPool pool = ClassPool.getDefault();
        this.addTranslator(pool, this.reflection);
    }
    
    public boolean makeReflective(final String clazz, final String metaobject, final String metaclass) throws CannotCompileException, NotFoundException {
        return this.reflection.makeReflective(clazz, metaobject, metaclass);
    }
}

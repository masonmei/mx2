// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist;

public interface Translator
{
    void start(ClassPool p0) throws NotFoundException, CannotCompileException;
    
    void onLoad(ClassPool p0, String p1) throws NotFoundException, CannotCompileException;
}

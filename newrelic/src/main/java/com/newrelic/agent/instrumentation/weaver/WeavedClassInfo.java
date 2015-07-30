// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.tree.FieldNode;
import java.util.Collection;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import com.newrelic.api.agent.weaver.MatchType;

public interface WeavedClassInfo
{
    String getSuperName();
    
    MatchType getMatchType();
    
    Set<Method> getWeavedMethods();
    
    Map<Method, TraceDetails> getTracedMethods();
    
    Collection<FieldNode> getReferencedFields();
    
    MethodVisitor getMethodVisitor(String p0, MethodVisitor p1, int p2, Method p3);
    
    MethodVisitor getConstructorMethodVisitor(MethodVisitor p0, String p1, int p2, String p3, String p4);
    
    Map<String, FieldNode> getNewFields();
    
    boolean isSkipIfPresent();
}

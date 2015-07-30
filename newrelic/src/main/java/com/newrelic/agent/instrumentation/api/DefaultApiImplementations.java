// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.api;

import com.newrelic.api.agent.Response;
import java.util.Enumeration;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import java.util.Arrays;
import com.newrelic.agent.util.asm.Utils;
import java.lang.reflect.Modifier;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;

public class DefaultApiImplementations
{
    private final Map<String, Map<Method, MethodNode>> interfaceToMethods;
    
    public DefaultApiImplementations() throws Exception {
        this((Class<?>[])new Class[] { DefaultRequest.class, DefaultResponse.class });
    }
    
    public DefaultApiImplementations(final Class<?>... defaultImplementations) throws Exception {
        final Map<String, Map<Method, MethodNode>> interfaceToMethods = (Map<String, Map<Method, MethodNode>>)Maps.newHashMap();
        for (final Class<?> clazz : defaultImplementations) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                throw new Exception(clazz.getName() + " cannot be abstract");
            }
            final ClassReader reader = Utils.readClass(clazz);
            final String[] interfaces = reader.getInterfaces();
            if (interfaces.length != 1) {
                throw new Exception(clazz.getName() + " implements multiple interfaces: " + Arrays.asList(interfaces));
            }
            final Map<Method, MethodNode> methods = (Map<Method, MethodNode>)Maps.newHashMap();
            interfaceToMethods.put(interfaces[0], methods);
            final ClassVisitor cv = new ClassVisitor(327680) {
                public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                    final Method method = new Method(name, desc);
                    if ((access & 0x10) != 0x0) {
                        Agent.LOG.severe("Default implementation " + reader.getClassName() + " should not declared " + method + " final");
                        return null;
                    }
                    final MethodNode node = new MethodNode(access, name, desc, signature, exceptions);
                    methods.put(method, node);
                    return node;
                }
            };
            reader.accept(cv, 2);
            methods.remove(new Method("<init>", "()V"));
            methods.remove(new Method("<cinit>", "()V"));
        }
        this.interfaceToMethods = (Map<String, Map<Method, MethodNode>>)ImmutableMap.copyOf((Map<?, ?>)interfaceToMethods);
    }
    
    public Map<String, Map<Method, MethodNode>> getApiClassNameToDefaultMethods() {
        return this.interfaceToMethods;
    }
    
    private static final class DefaultRequest implements Request
    {
        public HeaderType getHeaderType() {
            return HeaderType.HTTP;
        }
        
        public String getHeader(final String name) {
            return null;
        }
        
        public String getRequestURI() {
            return null;
        }
        
        public String getRemoteUser() {
            return null;
        }
        
        public Enumeration<?> getParameterNames() {
            return null;
        }
        
        public String[] getParameterValues(final String name) {
            return null;
        }
        
        public Object getAttribute(final String name) {
            return null;
        }
        
        public String getCookieValue(final String name) {
            return null;
        }
    }
    
    private static final class DefaultResponse implements Response
    {
        public int getStatus() throws Exception {
            return 0;
        }
        
        public String getStatusMessage() throws Exception {
            return null;
        }
        
        public void setHeader(final String name, final String value) {
        }
        
        public String getContentType() {
            return null;
        }
        
        public HeaderType getHeaderType() {
            return HeaderType.HTTP;
        }
    }
}

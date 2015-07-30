// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.HashMap;
import java.lang.reflect.Method;
import java.util.Map;

public class JasperClassFactory
{
    static final Map<ClassLoader, JasperClassFactory> classFactories;
    private final Method visitTemplateTextMethod;
    private final Method templateTextGetTextMethod;
    private final Method templateTextSetTextMethod;
    private final Class<?> scriptletClass;
    private final Class<?> nodeClass;
    private final Class<?> markClass;
    private final Method generateVisitorVisitScriptletMethod;
    private Method visitorVisitScriptletMethod;
    private Method nodeGetParentMethod;
    private Method nodeGetQName;
    
    private JasperClassFactory(final ClassLoader classloader) throws Exception {
        final Class<?> generateVisitorClass = classloader.loadClass("org.apache.jasper.compiler.Generator$GenerateVisitor");
        final Class<?> templateTextClass = classloader.loadClass("org.apache.jasper.compiler.Node$TemplateText");
        (this.visitTemplateTextMethod = generateVisitorClass.getMethod("visit", templateTextClass)).setAccessible(true);
        (this.templateTextGetTextMethod = templateTextClass.getMethod("getText", (Class<?>[])new Class[0])).setAccessible(true);
        (this.templateTextSetTextMethod = templateTextClass.getMethod("setText", String.class)).setAccessible(true);
        this.scriptletClass = classloader.loadClass("org.apache.jasper.compiler.Node$Scriptlet");
        (this.generateVisitorVisitScriptletMethod = generateVisitorClass.getMethod("visit", this.scriptletClass)).setAccessible(true);
        final Class<?> visitorClass = classloader.loadClass("org.apache.jasper.compiler.Node$Visitor");
        (this.visitorVisitScriptletMethod = visitorClass.getMethod("visit", this.scriptletClass)).setAccessible(true);
        this.markClass = classloader.loadClass("org.apache.jasper.compiler.Mark");
        this.nodeClass = classloader.loadClass("org.apache.jasper.compiler.Node");
        (this.nodeGetParentMethod = this.nodeClass.getMethod("getParent", (Class<?>[])new Class[0])).setAccessible(true);
        (this.nodeGetQName = this.nodeClass.getMethod("getQName", (Class<?>[])new Class[0])).setAccessible(true);
    }
    
    public Object createScriptlet(final String script) throws Exception {
        return this.scriptletClass.getConstructor(String.class, this.markClass, this.nodeClass).newInstance(script, null, null);
    }
    
    public GenerateVisitor getGenerateVisitor(final Object visitor) {
        return new GenerateVisitorImpl(visitor);
    }
    
    public Node getNode(final Object node) {
        return new NodeImpl(node);
    }
    
    public Visitor getVisitor(final Object visitor) {
        return new VisitorImpl(visitor);
    }
    
    public TemplateText getTemplateText(final Object text) {
        return new TemplateTextImpl(text);
    }
    
    public static synchronized JasperClassFactory getJasperClassFactory(final ClassLoader cl) throws Exception {
        JasperClassFactory factory = JasperClassFactory.classFactories.get(cl);
        if (factory == null) {
            factory = new JasperClassFactory(cl);
            JasperClassFactory.classFactories.put(cl, factory);
        }
        return factory;
    }
    
    static {
        classFactories = new HashMap<ClassLoader, JasperClassFactory>();
    }
    
    private class NodeImpl implements Node
    {
        private final Object node;
        
        public NodeImpl(final Object node) {
            this.node = node;
        }
        
        public Node getParent() throws Exception {
            return new NodeImpl(JasperClassFactory.this.nodeGetParentMethod.invoke(this.node, new Object[0]));
        }
        
        public String getQName() throws Exception {
            return (String)JasperClassFactory.this.nodeGetQName.invoke(this.node, new Object[0]);
        }
    }
    
    private class VisitorImpl implements Visitor
    {
        private Object visitor;
        
        public VisitorImpl(final Object visitor) {
            this.visitor = visitor;
        }
        
        public void writeScriptlet(final String script) throws Exception {
            JasperClassFactory.this.visitorVisitScriptletMethod.invoke(this.visitor, JasperClassFactory.this.createScriptlet(script));
        }
    }
    
    private class GenerateVisitorImpl implements GenerateVisitor
    {
        private final Object visitor;
        
        public GenerateVisitorImpl(final Object visitor) {
            this.visitor = visitor;
        }
        
        public void visit(final TemplateText text) throws Exception {
            JasperClassFactory.this.visitTemplateTextMethod.invoke(this.visitor, ((TemplateTextImpl)text).text);
        }
        
        public void writeScriptlet(final String script) throws Exception {
            JasperClassFactory.this.generateVisitorVisitScriptletMethod.invoke(this.visitor, JasperClassFactory.this.createScriptlet(script));
        }
    }
    
    private class TemplateTextImpl implements TemplateText
    {
        final Object text;
        
        public TemplateTextImpl(final Object text) {
            this.text = text;
        }
        
        public String getText() throws Exception {
            return (String)JasperClassFactory.this.templateTextGetTextMethod.invoke(this.text, new Object[0]);
        }
        
        public void setText(final String text) throws Exception {
            JasperClassFactory.this.templateTextSetTextMethod.invoke(this.text, text);
        }
    }
}

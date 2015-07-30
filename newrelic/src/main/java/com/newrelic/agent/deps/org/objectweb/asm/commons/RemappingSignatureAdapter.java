// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import com.newrelic.agent.deps.org.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter extends SignatureVisitor
{
    private final SignatureVisitor v;
    private final Remapper remapper;
    private String className;
    
    public RemappingSignatureAdapter(final SignatureVisitor signatureVisitor, final Remapper remapper) {
        this(327680, signatureVisitor, remapper);
    }
    
    protected RemappingSignatureAdapter(final int n, final SignatureVisitor v, final Remapper remapper) {
        super(n);
        this.v = v;
        this.remapper = remapper;
    }
    
    public void visitClassType(final String className) {
        this.className = className;
        this.v.visitClassType(this.remapper.mapType(className));
    }
    
    public void visitInnerClassType(final String s) {
        final String string = this.remapper.mapType(this.className) + '$';
        this.className = this.className + '$' + s;
        final String mapType = this.remapper.mapType(this.className);
        this.v.visitInnerClassType(mapType.substring(mapType.startsWith(string) ? string.length() : (mapType.lastIndexOf(36) + 1)));
    }
    
    public void visitFormalTypeParameter(final String s) {
        this.v.visitFormalTypeParameter(s);
    }
    
    public void visitTypeVariable(final String s) {
        this.v.visitTypeVariable(s);
    }
    
    public SignatureVisitor visitArrayType() {
        this.v.visitArrayType();
        return this;
    }
    
    public void visitBaseType(final char c) {
        this.v.visitBaseType(c);
    }
    
    public SignatureVisitor visitClassBound() {
        this.v.visitClassBound();
        return this;
    }
    
    public SignatureVisitor visitExceptionType() {
        this.v.visitExceptionType();
        return this;
    }
    
    public SignatureVisitor visitInterface() {
        this.v.visitInterface();
        return this;
    }
    
    public SignatureVisitor visitInterfaceBound() {
        this.v.visitInterfaceBound();
        return this;
    }
    
    public SignatureVisitor visitParameterType() {
        this.v.visitParameterType();
        return this;
    }
    
    public SignatureVisitor visitReturnType() {
        this.v.visitReturnType();
        return this;
    }
    
    public SignatureVisitor visitSuperclass() {
        this.v.visitSuperclass();
        return this;
    }
    
    public void visitTypeArgument() {
        this.v.visitTypeArgument();
    }
    
    public SignatureVisitor visitTypeArgument(final char c) {
        this.v.visitTypeArgument(c);
        return this;
    }
    
    public void visitEnd() {
        this.v.visitEnd();
    }
}

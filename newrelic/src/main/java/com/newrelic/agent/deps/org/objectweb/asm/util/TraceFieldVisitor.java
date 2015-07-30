// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.util;

import com.newrelic.agent.deps.org.objectweb.asm.Attribute;
import com.newrelic.agent.deps.org.objectweb.asm.TypePath;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;

public final class TraceFieldVisitor extends FieldVisitor
{
    public final Printer p;
    
    public TraceFieldVisitor(final Printer printer) {
        this(null, printer);
    }
    
    public TraceFieldVisitor(final FieldVisitor fieldVisitor, final Printer p2) {
        super(327680, fieldVisitor);
        this.p = p2;
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        return new TraceAnnotationVisitor((this.fv == null) ? null : this.fv.visitAnnotation(s, b), this.p.visitFieldAnnotation(s, b));
    }
    
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        return new TraceAnnotationVisitor((this.fv == null) ? null : this.fv.visitTypeAnnotation(n, typePath, s, b), this.p.visitFieldTypeAnnotation(n, typePath, s, b));
    }
    
    public void visitAttribute(final Attribute attribute) {
        this.p.visitFieldAttribute(attribute);
        super.visitAttribute(attribute);
    }
    
    public void visitEnd() {
        this.p.visitFieldEnd();
        super.visitEnd();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.util;

import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;

public final class TraceAnnotationVisitor extends AnnotationVisitor
{
    private final Printer p;
    
    public TraceAnnotationVisitor(final Printer printer) {
        this(null, printer);
    }
    
    public TraceAnnotationVisitor(final AnnotationVisitor annotationVisitor, final Printer p2) {
        super(327680, annotationVisitor);
        this.p = p2;
    }
    
    public void visit(final String s, final Object o) {
        this.p.visit(s, o);
        super.visit(s, o);
    }
    
    public void visitEnum(final String s, final String s2, final String s3) {
        this.p.visitEnum(s, s2, s3);
        super.visitEnum(s, s2, s3);
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final String s2) {
        return new TraceAnnotationVisitor((this.av == null) ? null : this.av.visitAnnotation(s, s2), this.p.visitAnnotation(s, s2));
    }
    
    public AnnotationVisitor visitArray(final String s) {
        return new TraceAnnotationVisitor((this.av == null) ? null : this.av.visitArray(s), this.p.visitArray(s));
    }
    
    public void visitEnd() {
        this.p.visitAnnotationEnd();
        super.visitEnd();
    }
}

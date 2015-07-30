// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.util;

import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Attribute;
import com.newrelic.agent.deps.org.objectweb.asm.TypePath;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import java.io.PrintWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public final class TraceClassVisitor extends ClassVisitor
{
    private final PrintWriter pw;
    public final Printer p;
    
    public TraceClassVisitor(final PrintWriter printWriter) {
        this(null, printWriter);
    }
    
    public TraceClassVisitor(final ClassVisitor classVisitor, final PrintWriter printWriter) {
        this(classVisitor, new Textifier(), printWriter);
    }
    
    public TraceClassVisitor(final ClassVisitor classVisitor, final Printer p3, final PrintWriter pw) {
        super(327680, classVisitor);
        this.pw = pw;
        this.p = p3;
    }
    
    public void visit(final int n, final int n2, final String s, final String s2, final String s3, final String[] array) {
        this.p.visit(n, n2, s, s2, s3, array);
        super.visit(n, n2, s, s2, s3, array);
    }
    
    public void visitSource(final String s, final String s2) {
        this.p.visitSource(s, s2);
        super.visitSource(s, s2);
    }
    
    public void visitOuterClass(final String s, final String s2, final String s3) {
        this.p.visitOuterClass(s, s2, s3);
        super.visitOuterClass(s, s2, s3);
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        return new TraceAnnotationVisitor((this.cv == null) ? null : this.cv.visitAnnotation(s, b), this.p.visitClassAnnotation(s, b));
    }
    
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        return new TraceAnnotationVisitor((this.cv == null) ? null : this.cv.visitTypeAnnotation(n, typePath, s, b), this.p.visitClassTypeAnnotation(n, typePath, s, b));
    }
    
    public void visitAttribute(final Attribute attribute) {
        this.p.visitClassAttribute(attribute);
        super.visitAttribute(attribute);
    }
    
    public void visitInnerClass(final String s, final String s2, final String s3, final int n) {
        this.p.visitInnerClass(s, s2, s3, n);
        super.visitInnerClass(s, s2, s3, n);
    }
    
    public FieldVisitor visitField(final int n, final String s, final String s2, final String s3, final Object o) {
        return new TraceFieldVisitor((this.cv == null) ? null : this.cv.visitField(n, s, s2, s3, o), this.p.visitField(n, s, s2, s3, o));
    }
    
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        return new TraceMethodVisitor((this.cv == null) ? null : this.cv.visitMethod(n, s, s2, s3, array), this.p.visitMethod(n, s, s2, s3, array));
    }
    
    public void visitEnd() {
        this.p.visitClassEnd();
        if (this.pw != null) {
            this.p.print(this.pw);
            this.pw.flush();
        }
        super.visitEnd();
    }
}

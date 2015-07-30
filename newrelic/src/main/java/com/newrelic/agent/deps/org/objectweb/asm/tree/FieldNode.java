// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Attribute;
import com.newrelic.agent.deps.org.objectweb.asm.TypePath;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;

public class FieldNode extends FieldVisitor
{
    public int access;
    public String name;
    public String desc;
    public String signature;
    public Object value;
    public List visibleAnnotations;
    public List invisibleAnnotations;
    public List visibleTypeAnnotations;
    public List invisibleTypeAnnotations;
    public List attrs;
    static /* synthetic */ Class class$org$objectweb$asm$tree$FieldNode;
    
    public FieldNode(final int n, final String s, final String s2, final String s3, final Object o) {
        this(327680, n, s, s2, s3, o);
        if (this.getClass() != FieldNode.class$org$objectweb$asm$tree$FieldNode) {
            throw new IllegalStateException();
        }
    }
    
    public FieldNode(final int n, final int access, final String name, final String desc, final String signature, final Object value) {
        super(n);
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.value = value;
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final AnnotationNode annotationNode = new AnnotationNode(s);
        if (b) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList(1);
            }
            this.visibleAnnotations.add(annotationNode);
        }
        else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList(1);
            }
            this.invisibleAnnotations.add(annotationNode);
        }
        return annotationNode;
    }
    
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        final TypeAnnotationNode typeAnnotationNode = new TypeAnnotationNode(n, typePath, s);
        if (b) {
            if (this.visibleTypeAnnotations == null) {
                this.visibleTypeAnnotations = new ArrayList(1);
            }
            this.visibleTypeAnnotations.add(typeAnnotationNode);
        }
        else {
            if (this.invisibleTypeAnnotations == null) {
                this.invisibleTypeAnnotations = new ArrayList(1);
            }
            this.invisibleTypeAnnotations.add(typeAnnotationNode);
        }
        return typeAnnotationNode;
    }
    
    public void visitAttribute(final Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = new ArrayList(1);
        }
        this.attrs.add(attribute);
    }
    
    public void visitEnd() {
    }
    
    public void check(final int n) {
        if (n == 262144) {
            if (this.visibleTypeAnnotations != null && this.visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (this.invisibleTypeAnnotations != null && this.invisibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
        }
    }
    
    public void accept(final ClassVisitor classVisitor) {
        final FieldVisitor visitField = classVisitor.visitField(this.access, this.name, this.desc, this.signature, this.value);
        if (visitField == null) {
            return;
        }
        for (int n = (this.visibleAnnotations == null) ? 0 : this.visibleAnnotations.size(), i = 0; i < n; ++i) {
            final AnnotationNode annotationNode = (AnnotationNode) this.visibleAnnotations.get(i);
            annotationNode.accept(visitField.visitAnnotation(annotationNode.desc, true));
        }
        for (int n2 = (this.invisibleAnnotations == null) ? 0 : this.invisibleAnnotations.size(), j = 0; j < n2; ++j) {
            final AnnotationNode annotationNode2 = (AnnotationNode) this.invisibleAnnotations.get(j);
            annotationNode2.accept(visitField.visitAnnotation(annotationNode2.desc, false));
        }
        for (int n3 = (this.visibleTypeAnnotations == null) ? 0 : this.visibleTypeAnnotations.size(), k = 0; k < n3; ++k) {
            final TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode) this.visibleTypeAnnotations.get(k);
            typeAnnotationNode.accept(visitField.visitTypeAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, true));
        }
        for (int n4 = (this.invisibleTypeAnnotations == null) ? 0 : this.invisibleTypeAnnotations.size(), l = 0; l < n4; ++l) {
            final TypeAnnotationNode typeAnnotationNode2 = (TypeAnnotationNode) this.invisibleTypeAnnotations.get(l);
            typeAnnotationNode2.accept(visitField.visitTypeAnnotation(typeAnnotationNode2.typeRef, typeAnnotationNode2.typePath, typeAnnotationNode2.desc, false));
        }
        for (int n5 = (this.attrs == null) ? 0 : this.attrs.size(), n6 = 0; n6 < n5; ++n6) {
            visitField.visitAttribute((Attribute)this.attrs.get(n6));
        }
        visitField.visitEnd();
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        FieldNode.class$org$objectweb$asm$tree$FieldNode = class$("com.newrelic.agent.deps.org.objectweb.asm.tree.FieldNode");
    }
}

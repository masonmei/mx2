// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import com.newrelic.agent.deps.org.objectweb.asm.TypePath;

public class TypeAnnotationNode extends AnnotationNode
{
    public int typeRef;
    public TypePath typePath;
    static /* synthetic */ Class class$org$objectweb$asm$tree$TypeAnnotationNode;
    
    public TypeAnnotationNode(final int n, final TypePath typePath, final String s) {
        this(327680, n, typePath, s);
        if (this.getClass() != TypeAnnotationNode.class$org$objectweb$asm$tree$TypeAnnotationNode) {
            throw new IllegalStateException();
        }
    }
    
    public TypeAnnotationNode(final int n, final int typeRef, final TypePath typePath, final String s) {
        super(n, s);
        this.typeRef = typeRef;
        this.typePath = typePath;
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
        TypeAnnotationNode.class$org$objectweb$asm$tree$TypeAnnotationNode = class$("com.newrelic.agent.deps.org.objectweb.asm.tree.TypeAnnotationNode");
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.objectweb.asm.TypePath;
import java.util.List;

public class LocalVariableAnnotationNode extends TypeAnnotationNode
{
    public List start;
    public List end;
    public List index;
    
    public LocalVariableAnnotationNode(final int n, final TypePath typePath, final LabelNode[] array, final LabelNode[] array2, final int[] array3, final String s) {
        this(327680, n, typePath, array, array2, array3, s);
    }
    
    public LocalVariableAnnotationNode(final int n, final int n2, final TypePath typePath, final LabelNode[] array, final LabelNode[] array2, final int[] array3, final String s) {
        super(n, n2, typePath, s);
        (this.start = new ArrayList(array.length)).addAll(Arrays.asList(array));
        (this.end = new ArrayList(array2.length)).addAll(Arrays.asList(array2));
        this.index = new ArrayList(array3.length);
        for (int length = array3.length, i = 0; i < length; ++i) {
            this.index.add(array3[i]);
        }
    }
    
    public void accept(final MethodVisitor methodVisitor, final boolean b) {
        final Label[] array = new Label[this.start.size()];
        final Label[] array2 = new Label[this.end.size()];
        final int[] array3 = new int[this.index.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ((LabelNode)this.start.get(i)).getLabel();
            array2[i] = ((LabelNode)this.end.get(i)).getLabel();
            array3[i] = (Integer)this.index.get(i);
        }
        this.accept(methodVisitor.visitLocalVariableAnnotation(this.typeRef, this.typePath, array, array2, array3, this.desc, true));
    }
}

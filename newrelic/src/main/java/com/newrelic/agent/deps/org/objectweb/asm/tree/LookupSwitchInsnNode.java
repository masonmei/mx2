// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class LookupSwitchInsnNode extends AbstractInsnNode
{
    public LabelNode dflt;
    public List keys;
    public List labels;
    
    public LookupSwitchInsnNode(final LabelNode dflt, final int[] array, final LabelNode[] array2) {
        super(171);
        this.dflt = dflt;
        this.keys = new ArrayList((array == null) ? 0 : array.length);
        this.labels = new ArrayList((array2 == null) ? 0 : array2.length);
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                this.keys.add(new Integer(array[i]));
            }
        }
        if (array2 != null) {
            this.labels.addAll(Arrays.asList(array2));
        }
    }
    
    public int getType() {
        return 12;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        final int[] array = new int[this.keys.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (int)this.keys.get(i);
        }
        final Label[] array2 = new Label[this.labels.size()];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = ((LabelNode)this.labels.get(j)).getLabel();
        }
        methodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), array, array2);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        final LookupSwitchInsnNode lookupSwitchInsnNode = new LookupSwitchInsnNode(AbstractInsnNode.clone(this.dflt, map), null, AbstractInsnNode.clone(this.labels, map));
        lookupSwitchInsnNode.keys.addAll(this.keys);
        return lookupSwitchInsnNode.cloneAnnotations(this);
    }
}

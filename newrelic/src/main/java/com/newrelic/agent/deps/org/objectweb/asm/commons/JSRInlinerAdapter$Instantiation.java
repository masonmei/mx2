// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.tree.AbstractInsnNode;
import java.util.HashMap;
import com.newrelic.agent.deps.org.objectweb.asm.tree.LabelNode;
import java.util.Map;
import java.util.BitSet;
import java.util.AbstractMap;

class JSRInlinerAdapter$Instantiation extends AbstractMap
{
    final JSRInlinerAdapter$Instantiation previous;
    public final BitSet subroutine;
    public final Map rangeTable;
    public final LabelNode returnLabel;
    final /* synthetic */ JSRInlinerAdapter this$0;
    
    JSRInlinerAdapter$Instantiation(final JSRInlinerAdapter this$0, final JSRInlinerAdapter$Instantiation previous, final BitSet subroutine) {
        this.this$0 = this$0;
        this.rangeTable = new HashMap();
        this.previous = previous;
        this.subroutine = subroutine;
        for (JSRInlinerAdapter$Instantiation previous2 = previous; previous2 != null; previous2 = previous2.previous) {
            if (previous2.subroutine == subroutine) {
                throw new RuntimeException("Recursive invocation of " + subroutine);
            }
        }
        if (previous != null) {
            this.returnLabel = new LabelNode();
        }
        else {
            this.returnLabel = null;
        }
        Object o = null;
        for (int i = 0; i < this$0.instructions.size(); ++i) {
            final AbstractInsnNode value = this$0.instructions.get(i);
            if (value.getType() == 8) {
                final LabelNode labelNode = (LabelNode)value;
                if (o == null) {
                    o = new LabelNode();
                }
                this.rangeTable.put(labelNode, o);
            }
            else if (this.findOwner(i) == this) {
                o = null;
            }
        }
    }
    
    public JSRInlinerAdapter$Instantiation findOwner(final int n) {
        if (!this.subroutine.get(n)) {
            return null;
        }
        if (!this.this$0.dualCitizens.get(n)) {
            return this;
        }
        JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation = this;
        for (JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation2 = this.previous; jsrInlinerAdapter$Instantiation2 != null; jsrInlinerAdapter$Instantiation2 = jsrInlinerAdapter$Instantiation2.previous) {
            if (jsrInlinerAdapter$Instantiation2.subroutine.get(n)) {
                jsrInlinerAdapter$Instantiation = jsrInlinerAdapter$Instantiation2;
            }
        }
        return jsrInlinerAdapter$Instantiation;
    }
    
    public LabelNode gotoLabel(final LabelNode labelNode) {
        return this.findOwner(this.this$0.instructions.indexOf(labelNode)).rangeTable.get(labelNode);
    }
    
    public LabelNode rangeLabel(final LabelNode labelNode) {
        return this.rangeTable.get(labelNode);
    }
    
    public Set entrySet() {
        return null;
    }
    
    public LabelNode get(final Object o) {
        return this.gotoLabel((LabelNode)o);
    }
}

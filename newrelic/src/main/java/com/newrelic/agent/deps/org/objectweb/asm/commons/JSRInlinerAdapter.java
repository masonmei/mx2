// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import com.newrelic.agent.deps.org.objectweb.asm.tree.LocalVariableNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InsnNode;
import java.util.List;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InsnList;
import java.util.LinkedList;
import com.newrelic.agent.deps.org.objectweb.asm.tree.LookupSwitchInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.TableSwitchInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.TryCatchBlockNode;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.tree.AbstractInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.LabelNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.JumpInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import java.util.HashMap;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.BitSet;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.Opcodes;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;

public class JSRInlinerAdapter extends MethodNode implements Opcodes
{
    private final Map subroutineHeads;
    private final BitSet mainSubroutine;
    final BitSet dualCitizens;
    static /* synthetic */ Class class$org$objectweb$asm$commons$JSRInlinerAdapter;
    
    public JSRInlinerAdapter(final MethodVisitor methodVisitor, final int n, final String s, final String s2, final String s3, final String[] array) {
        this(327680, methodVisitor, n, s, s2, s3, array);
        if (this.getClass() != JSRInlinerAdapter.class$org$objectweb$asm$commons$JSRInlinerAdapter) {
            throw new IllegalStateException();
        }
    }
    
    protected JSRInlinerAdapter(final int n, final MethodVisitor mv, final int n2, final String s, final String s2, final String s3, final String[] array) {
        super(n, n2, s, s2, s3, array);
        this.subroutineHeads = new HashMap();
        this.mainSubroutine = new BitSet();
        this.dualCitizens = new BitSet();
        this.mv = mv;
    }
    
    public void visitJumpInsn(final int n, final Label label) {
        super.visitJumpInsn(n, label);
        final LabelNode label2 = ((JumpInsnNode)this.instructions.getLast()).label;
        if (n == 168 && !this.subroutineHeads.containsKey(label2)) {
            this.subroutineHeads.put(label2, new BitSet());
        }
    }
    
    public void visitEnd() {
        if (!this.subroutineHeads.isEmpty()) {
            this.markSubroutines();
            this.emitCode();
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
    
    private void markSubroutines() {
        final BitSet set = new BitSet();
        this.markSubroutineWalk(this.mainSubroutine, 0, set);
        for (final Map.Entry<LabelNode, V> entry : this.subroutineHeads.entrySet()) {
            this.markSubroutineWalk((BitSet)entry.getValue(), this.instructions.indexOf(entry.getKey()), set);
        }
    }
    
    private void markSubroutineWalk(final BitSet set, final int n, final BitSet set2) {
        this.markSubroutineWalkDFS(set, n, set2);
        int i = 1;
        while (i != 0) {
            i = 0;
            for (final TryCatchBlockNode tryCatchBlockNode : this.tryCatchBlocks) {
                final int index = this.instructions.indexOf(tryCatchBlockNode.handler);
                if (set.get(index)) {
                    continue;
                }
                final int index2 = this.instructions.indexOf(tryCatchBlockNode.start);
                final int index3 = this.instructions.indexOf(tryCatchBlockNode.end);
                final int nextSetBit = set.nextSetBit(index2);
                if (nextSetBit == -1 || nextSetBit >= index3) {
                    continue;
                }
                this.markSubroutineWalkDFS(set, index, set2);
                i = 1;
            }
        }
    }
    
    private void markSubroutineWalkDFS(final BitSet set, int n, final BitSet set2) {
        while (true) {
            final AbstractInsnNode value = this.instructions.get(n);
            if (set.get(n)) {
                return;
            }
            set.set(n);
            if (set2.get(n)) {
                this.dualCitizens.set(n);
            }
            set2.set(n);
            if (value.getType() == 7 && value.getOpcode() != 168) {
                this.markSubroutineWalkDFS(set, this.instructions.indexOf(((JumpInsnNode)value).label), set2);
            }
            if (value.getType() == 11) {
                final TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode)value;
                this.markSubroutineWalkDFS(set, this.instructions.indexOf(tableSwitchInsnNode.dflt), set2);
                for (int i = tableSwitchInsnNode.labels.size() - 1; i >= 0; --i) {
                    this.markSubroutineWalkDFS(set, this.instructions.indexOf((AbstractInsnNode)tableSwitchInsnNode.labels.get(i)), set2);
                }
            }
            if (value.getType() == 12) {
                final LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode)value;
                this.markSubroutineWalkDFS(set, this.instructions.indexOf(lookupSwitchInsnNode.dflt), set2);
                for (int j = lookupSwitchInsnNode.labels.size() - 1; j >= 0; --j) {
                    this.markSubroutineWalkDFS(set, this.instructions.indexOf((AbstractInsnNode)lookupSwitchInsnNode.labels.get(j)), set2);
                }
            }
            switch (this.instructions.get(n).getOpcode()) {
                case 167:
                case 169:
                case 170:
                case 171:
                case 172:
                case 173:
                case 174:
                case 175:
                case 176:
                case 177:
                case 191: {}
                default: {
                    if (++n >= this.instructions.size()) {
                        return;
                    }
                    continue;
                }
            }
        }
    }
    
    private void emitCode() {
        final LinkedList list = new LinkedList<JSRInlinerAdapter$Instantiation>();
        list.add(new JSRInlinerAdapter$Instantiation(this, null, this.mainSubroutine));
        final InsnList instructions = new InsnList();
        final ArrayList tryCatchBlocks = new ArrayList();
        final ArrayList localVariables = new ArrayList();
        while (!list.isEmpty()) {
            this.emitSubroutine(list.removeFirst(), list, instructions, tryCatchBlocks, localVariables);
        }
        this.instructions = instructions;
        this.tryCatchBlocks = tryCatchBlocks;
        this.localVariables = localVariables;
    }
    
    private void emitSubroutine(final JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation, final List list, final InsnList list2, final List list3, final List list4) {
        LabelNode labelNode = null;
        for (int i = 0; i < this.instructions.size(); ++i) {
            final AbstractInsnNode value = this.instructions.get(i);
            final JSRInlinerAdapter$Instantiation owner = jsrInlinerAdapter$Instantiation.findOwner(i);
            if (value.getType() == 8) {
                final LabelNode rangeLabel = jsrInlinerAdapter$Instantiation.rangeLabel((LabelNode)value);
                if (rangeLabel != labelNode) {
                    list2.add(rangeLabel);
                    labelNode = rangeLabel;
                }
            }
            else if (owner == jsrInlinerAdapter$Instantiation) {
                if (value.getOpcode() == 169) {
                    LabelNode returnLabel = null;
                    for (JSRInlinerAdapter$Instantiation previous = jsrInlinerAdapter$Instantiation; previous != null; previous = previous.previous) {
                        if (previous.subroutine.get(i)) {
                            returnLabel = previous.returnLabel;
                        }
                    }
                    if (returnLabel == null) {
                        throw new RuntimeException("Instruction #" + i + " is a RET not owned by any subroutine");
                    }
                    list2.add(new JumpInsnNode(167, returnLabel));
                }
                else if (value.getOpcode() == 168) {
                    final LabelNode label = ((JumpInsnNode)value).label;
                    final JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation2 = new JSRInlinerAdapter$Instantiation(this, jsrInlinerAdapter$Instantiation, this.subroutineHeads.get(label));
                    final LabelNode gotoLabel = jsrInlinerAdapter$Instantiation2.gotoLabel(label);
                    list2.add(new InsnNode(1));
                    list2.add(new JumpInsnNode(167, gotoLabel));
                    list2.add(jsrInlinerAdapter$Instantiation2.returnLabel);
                    list.add(jsrInlinerAdapter$Instantiation2);
                }
                else {
                    list2.add(value.clone(jsrInlinerAdapter$Instantiation));
                }
            }
        }
        for (final TryCatchBlockNode tryCatchBlockNode : this.tryCatchBlocks) {
            final LabelNode rangeLabel2 = jsrInlinerAdapter$Instantiation.rangeLabel(tryCatchBlockNode.start);
            final LabelNode rangeLabel3 = jsrInlinerAdapter$Instantiation.rangeLabel(tryCatchBlockNode.end);
            if (rangeLabel2 == rangeLabel3) {
                continue;
            }
            final LabelNode gotoLabel2 = jsrInlinerAdapter$Instantiation.gotoLabel(tryCatchBlockNode.handler);
            if (rangeLabel2 == null || rangeLabel3 == null || gotoLabel2 == null) {
                throw new RuntimeException("Internal error!");
            }
            list3.add(new TryCatchBlockNode(rangeLabel2, rangeLabel3, gotoLabel2, tryCatchBlockNode.type));
        }
        for (final LocalVariableNode localVariableNode : this.localVariables) {
            final LabelNode rangeLabel4 = jsrInlinerAdapter$Instantiation.rangeLabel(localVariableNode.start);
            final LabelNode rangeLabel5 = jsrInlinerAdapter$Instantiation.rangeLabel(localVariableNode.end);
            if (rangeLabel4 == rangeLabel5) {
                continue;
            }
            list4.add(new LocalVariableNode(localVariableNode.name, localVariableNode.desc, localVariableNode.signature, rangeLabel4, rangeLabel5, localVariableNode.index));
        }
    }
    
    private static void log(final String s) {
        System.err.println(s);
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
        JSRInlinerAdapter.class$org$objectweb$asm$commons$JSRInlinerAdapter = class$("com.newrelic.agent.deps.org.objectweb.asm.commons.JSRInlinerAdapter");
    }
}

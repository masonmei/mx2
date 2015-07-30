// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.commons.AnalyzerAdapter;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.Collections;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.tree.LocalVariableNode;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.org.objectweb.asm.tree.LabelNode;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.VarInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.AbstractInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;

class MergeMethodVisitor extends MethodNode
{
    private static final Method NOTICE_INSTRUMENTATION_ERROR_METHOD;
    private final Method method;
    private final String className;
    private int nextLocalIndex;
    private MethodInsnNode invokeOriginalNode;
    protected final int firstLocal;
    private final InstrumentationPackage instrumentationPackage;
    
    public MergeMethodVisitor(final InstrumentationPackage instrumentationPackage, final String className, final int api, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        super(api, access, name, desc, signature, exceptions);
        this.instrumentationPackage = instrumentationPackage;
        this.method = new Method(name, desc);
        this.className = className;
        final Type[] args = Type.getArgumentTypes(desc);
        int nextLocal = ((0x8 & access) == 0x0) ? 1 : 0;
        for (int i = 0; i < args.length; ++i) {
            nextLocal += args[i].getSize();
        }
        this.firstLocal = nextLocal;
    }
    
    public void visitEnd() {
        try {
            final int newIndex = this.determineIfNew();
            if (this.invokeOriginalNode != null) {
                final boolean isVoid = this.method.getReturnType().equals(Type.VOID_TYPE);
                final Label startOfOriginalMethod = new Label();
                final Label endOfOriginalMethod = new Label();
                AbstractInsnNode insertPoint;
                if (newIndex >= 0) {
                    insertPoint = this.instructions.get(newIndex);
                }
                else {
                    insertPoint = this.invokeOriginalNode;
                }
                final boolean callsThrow = this.isThrowCalled();
                int rethrowExceptionIndex = -1;
                int returnLocalIndex = -1;
                if (!isVoid || callsThrow) {
                    final Label startOfMethod = new Label();
                    final Label endOfMethod = new Label();
                    this.instructions.insert(this.getLabelNode(startOfMethod));
                    this.instructions.add(this.getLabelNode(endOfMethod));
                    if (callsThrow) {
                        rethrowExceptionIndex = this.nextLocalIndex;
                        this.visitLocalVariable("rethrowException", Type.getDescriptor(Throwable.class), null, startOfMethod, endOfMethod, rethrowExceptionIndex);
                    }
                    if (!isVoid) {
                        returnLocalIndex = this.nextLocalIndex;
                        this.visitLocalVariable("originalReturnValue", this.method.getReturnType().getDescriptor(), null, startOfMethod, endOfMethod, returnLocalIndex);
                    }
                }
                if (rethrowExceptionIndex >= 0) {
                    this.storeExceptionAtThrowSites(rethrowExceptionIndex);
                }
                final LabelNode startOfOriginalMethodLabelNode = this.getLabelNode(startOfOriginalMethod);
                this.instructions.insertBefore(insertPoint, startOfOriginalMethodLabelNode);
                final boolean isStatic = (this.access & 0x8) != 0x0;
                if (!isStatic) {
                    this.instructions.insertBefore(insertPoint, new VarInsnNode(25, 0));
                }
                int index = isStatic ? 0 : 1;
                for (int i = 0; i < this.method.getArgumentTypes().length; ++i) {
                    final Type argType = this.method.getArgumentTypes()[i];
                    this.instructions.insertBefore(insertPoint, new VarInsnNode(argType.getOpcode(21), index));
                    index += argType.getSize();
                }
                this.instructions.insertBefore(insertPoint, new MethodInsnNode(isStatic ? 184 : 182, this.className, this.method.getName(), this.method.getDescriptor()));
                if (returnLocalIndex >= 0) {
                    this.instructions.insertBefore(insertPoint, new VarInsnNode(this.method.getReturnType().getOpcode(54), returnLocalIndex));
                }
                this.instructions.insertBefore(insertPoint, this.getLabelNode(endOfOriginalMethod));
                if (returnLocalIndex >= 0) {
                    final AbstractInsnNode loadInsertPoint = (newIndex >= 0) ? this.invokeOriginalNode : insertPoint;
                    this.instructions.insertBefore(loadInsertPoint, new VarInsnNode(this.method.getReturnType().getOpcode(21), returnLocalIndex));
                }
                if (returnLocalIndex < 0) {
                    final AbstractInsnNode popInstruction = this.invokeOriginalNode.getNext();
                    if (popInstruction.getOpcode() == 87) {
                        this.instructions.remove(popInstruction);
                    }
                    else {
                        this.instrumentationPackage.getLogger().severe("Unexpected instruction " + popInstruction.getOpcode() + ", Method: " + this.className + '.' + this.method + ", expected " + 87);
                    }
                }
                else if (this.method.getReturnType().getSort() == 10 || this.method.getReturnType().getSort() == 9) {
                    if (!Type.getType(Object.class).equals(this.method.getReturnType())) {
                        final AbstractInsnNode nextInstruction = this.invokeOriginalNode.getNext();
                        if (nextInstruction.getOpcode() != 87) {
                            if (nextInstruction.getOpcode() != 176) {
                                this.expectCastOrInvokeWithObject(nextInstruction);
                            }
                        }
                    }
                }
                else {
                    final AbstractInsnNode nextInstruction = this.invokeOriginalNode.getNext();
                    if (nextInstruction.getOpcode() == 87) {
                        if (this.method.getReturnType().getSize() == 2) {
                            this.instructions.insertBefore(nextInstruction, new InsnNode(88));
                            this.instructions.remove(nextInstruction);
                        }
                    }
                    else {
                        if (nextInstruction.getOpcode() == 192) {
                            this.instructions.remove(nextInstruction);
                        }
                        else {
                            this.expectCastOrInvokeWithObject(nextInstruction);
                        }
                        final AbstractInsnNode invokeVirtualInstruction = this.invokeOriginalNode.getNext();
                        if (invokeVirtualInstruction.getOpcode() == 182) {
                            this.instructions.remove(invokeVirtualInstruction);
                        }
                        else {
                            this.instrumentationPackage.getLogger().severe("Unexpected instruction " + nextInstruction.getOpcode() + ", Method: " + this.className + '.' + this.method + ", expected " + 182);
                        }
                    }
                }
                this.instructions.remove(this.invokeOriginalNode);
                final Label afterOriginalMethodExceptionHandler = new Label();
                this.visitLabel(afterOriginalMethodExceptionHandler);
                GeneratorAdapter generator = new GeneratorAdapter(this.access, this.method, this);
                if (rethrowExceptionIndex >= 0) {
                    final Label afterThrow = new Label();
                    generator.dup();
                    this.visitVarInsn(25, rethrowExceptionIndex);
                    this.visitJumpInsn(166, afterThrow);
                    this.visitInsn(191);
                    this.visitLabel(afterThrow);
                }
                this.noticeInstrumentationErrorInstructions(generator);
                if (returnLocalIndex >= 0) {
                    this.visitVarInsn(this.method.getReturnType().getOpcode(21), returnLocalIndex);
                }
                this.visitInsn(this.method.getReturnType().getOpcode(172));
                this.visitTryCatchBlock(endOfOriginalMethod, afterOriginalMethodExceptionHandler, afterOriginalMethodExceptionHandler, Type.getInternalName(Throwable.class));
                final Label start = new Label();
                this.instructions.insert(this.getLabelNode(start));
                this.initializePreambleLocals(startOfOriginalMethodLabelNode);
                final MethodNode preambleHandler = new MethodNode(327680);
                final Label beforeOriginalMethodExceptionHandler = new Label();
                generator = new GeneratorAdapter(this.access, this.method, preambleHandler);
                generator.goTo(startOfOriginalMethod);
                generator.visitLabel(beforeOriginalMethodExceptionHandler);
                this.noticeInstrumentationErrorInstructions(generator);
                this.instructions.insertBefore(startOfOriginalMethodLabelNode, preambleHandler.instructions);
                this.visitTryCatchBlock(start, startOfOriginalMethod, beforeOriginalMethodExceptionHandler, Type.getInternalName(Throwable.class));
            }
        }
        catch (IllegalInstructionException t) {
            this.instrumentationPackage.getLogger().severe("Unable to process method " + this.method);
            throw t;
        }
        catch (Exception e) {
            this.instrumentationPackage.getLogger().severe("Unable to process method " + this.method);
            throw new RuntimeException(e);
        }
    }
    
    private void storeExceptionAtThrowSites(final int rethrowExceptionIndex) {
        for (final AbstractInsnNode insnNode : this.instructions.toArray()) {
            if (191 == insnNode.getOpcode()) {
                this.instructions.insertBefore(insnNode, new VarInsnNode(58, rethrowExceptionIndex));
                this.instructions.insertBefore(insnNode, new VarInsnNode(25, rethrowExceptionIndex));
            }
        }
    }
    
    private boolean isThrowCalled() {
        for (final AbstractInsnNode insnNode : this.instructions.toArray()) {
            if (191 == insnNode.getOpcode()) {
                return true;
            }
        }
        return false;
    }
    
    private void expectCastOrInvokeWithObject(final AbstractInsnNode nextInstruction) {
        if (nextInstruction.getOpcode() == 192) {
            this.instructions.remove(nextInstruction);
        }
        else if (nextInstruction instanceof MethodInsnNode) {
            final MethodInsnNode methodNode = (MethodInsnNode)nextInstruction;
            final Type type = Type.getType(methodNode.desc);
            if (type.getArgumentTypes().length <= 0 || !Type.getType(Object.class).equals(type.getArgumentTypes()[0])) {
                this.instrumentationPackage.getLogger().severe("Unexpected instruction " + nextInstruction.getOpcode() + ", Method: " + methodNode.owner + '.' + methodNode.name + methodNode.desc);
            }
        }
        else {
            this.instrumentationPackage.getLogger().severe("Unexpected instruction " + nextInstruction.getOpcode());
        }
    }
    
    private void initializePreambleLocals(final LabelNode startOfOriginalMethodLabelNode) {
        final List<LocalVariableNode> localsInPreamble = this.getLocalsInPreamble(startOfOriginalMethodLabelNode);
        if (!localsInPreamble.isEmpty()) {
            final int firstLocalIndex = Utils.getFirstLocal(this.access, this.method);
            final LabelNode localsStart = this.getLabelNode(new Label());
            this.instructions.insert(localsStart);
            for (final LocalVariableNode local : localsInPreamble) {
                if (local.index >= firstLocalIndex) {
                    this.changeLocalVariableScopeStart(local, localsStart);
                }
            }
        }
    }
    
    private void changeLocalVariableScopeStart(final LocalVariableNode local, final LabelNode newStart) {
        final Type type = Type.getType(local.desc);
        local.start = newStart;
        final List<LocalVariableNode> collidingLocals = this.getCollidingVariables(local, this.localVariables);
        if (!collidingLocals.isEmpty()) {
            this.instrumentationPackage.getLogger().log(Level.FINEST, "slot {0} ({1}) collision detected", new Object[] { local.index, local.name });
            final int newIndex = this.nextLocalIndex;
            this.nextLocalIndex += type.getSize();
            for (final LocalVariableNode collidingLocal : collidingLocals) {
                this.instrumentationPackage.getLogger().log(Level.FINEST, "\tchanging {0}", new Object[] { collidingLocal.name });
                collidingLocal.index = newIndex;
                this.changeLocalSlot(local.index, newIndex, collidingLocal.start, collidingLocal.end);
            }
        }
        final AbstractInsnNode initialValue = this.getInitialValueInstruction(type);
        if (initialValue != null) {
            this.instructions.insert(newStart, new VarInsnNode(type.getOpcode(54), local.index));
            this.instructions.insert(newStart, initialValue);
        }
    }
    
    private List<LocalVariableNode> getCollidingVariables(final LocalVariableNode local, final List<LocalVariableNode> otherLocals) {
        final List<LocalVariableNode> collisions = new ArrayList<LocalVariableNode>();
        for (final LocalVariableNode otherLocal : otherLocals) {
            if (local.name.equals(otherLocal.name) && local.desc.equals(otherLocal.desc)) {
                continue;
            }
            if (!this.shareSlot(local, otherLocal) || !this.scopesOverlap(local, otherLocal)) {
                continue;
            }
            collisions.add(otherLocal);
        }
        return collisions;
    }
    
    private boolean shareSlot(final LocalVariableNode local, final LocalVariableNode otherLocal) {
        return local.index == otherLocal.index;
    }
    
    private boolean scopesOverlap(final LocalVariableNode local, final LocalVariableNode otherLocal) {
        return this.scopeContainsAnyPartOf(local, otherLocal) || this.scopeContainsAnyPartOf(otherLocal, local);
    }
    
    private boolean scopeContainsAnyPartOf(final LocalVariableNode local, final LocalVariableNode otherLocal) {
        for (AbstractInsnNode currentNode = local.start; currentNode != null && !currentNode.equals(local.end); currentNode = currentNode.getNext()) {
            if (currentNode.equals(otherLocal.start)) {
                return true;
            }
            if (currentNode.equals(otherLocal.end)) {
                return !currentNode.equals(local.start);
            }
        }
        return false;
    }
    
    private void changeLocalSlot(final int oldSlot, final int newSlot, final LabelNode start, final LabelNode end) {
        for (AbstractInsnNode currentNode = (null == start.getPrevious()) ? start : start.getPrevious(); null != currentNode && !currentNode.equals(end); currentNode = currentNode.getNext()) {
            if (currentNode.getType() == 2) {
                final VarInsnNode currentInsn = (VarInsnNode)currentNode;
                if (currentInsn.var == oldSlot) {
                    currentInsn.var = newSlot;
                }
            }
        }
    }
    
    private AbstractInsnNode getInitialValueInstruction(final Type type) {
        switch (type.getSort()) {
            case 9:
            case 10: {
                return new InsnNode(1);
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: {
                return new InsnNode(3);
            }
            case 7: {
                return new InsnNode(9);
            }
            case 6: {
                return new InsnNode(11);
            }
            case 8: {
                return new InsnNode(14);
            }
            default: {
                return null;
            }
        }
    }
    
    private List<LocalVariableNode> getLocalsInPreamble(final AbstractInsnNode insertPoint) {
        final int endIndex = this.instructions.indexOf(insertPoint);
        if (endIndex < 0) {
            return Collections.emptyList();
        }
        final List<LocalVariableNode> locals = (List<LocalVariableNode>)Lists.newArrayList();
        for (final LocalVariableNode local : this.localVariables) {
            final int startIndex = this.instructions.indexOf(local.start);
            final int end = this.instructions.indexOf(local.end);
            if (startIndex < endIndex && end > endIndex) {
                locals.add(local);
            }
        }
        return locals;
    }
    
    private void noticeInstrumentationErrorInstructions(final GeneratorAdapter generator) {
        generator.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "instrumentation", BridgeUtils.INSTRUMENTATION_TYPE);
        generator.swap();
        generator.push(this.instrumentationPackage.implementationTitle);
        generator.invokeInterface(BridgeUtils.INSTRUMENTATION_TYPE, MergeMethodVisitor.NOTICE_INSTRUMENTATION_ERROR_METHOD);
    }
    
    private int determineIfNew() {
        final AnalyzerAdapter stackAnalyzer = new AnalyzerAdapter(this.className, this.access, this.name, this.desc, new MethodVisitor(327680) {});
        boolean callsThrow = false;
        int lastStackZeroIndex = 0;
        final AbstractInsnNode[] inst = this.instructions.toArray();
        for (int i = 0; i < inst.length; ++i) {
            final int stackSize = (stackAnalyzer.stack == null) ? 0 : stackAnalyzer.stack.size();
            if (stackSize == 0) {
                lastStackZeroIndex = i;
            }
            inst[i].accept(stackAnalyzer);
            if (inst[i].getOpcode() == 191) {
                callsThrow = true;
            }
            if (inst[i] instanceof MethodInsnNode) {
                final MethodInsnNode invoke = (MethodInsnNode)inst[i];
                if (isOriginalMethodInvocation(invoke.owner, invoke.name, invoke.desc)) {
                    if (callsThrow) {
                        throw new IllegalInstructionException(this.className + '.' + this.name + this.desc + " can only throw an exception from the original method invocation");
                    }
                    this.invokeOriginalNode = invoke;
                    if (stackSize > 0) {
                        return lastStackZeroIndex;
                    }
                    return -1;
                }
            }
        }
        return -1;
    }
    
    public static boolean isInitMethod(final String name) {
        return "<init>".equals(name);
    }
    
    public boolean isNewMethod() {
        return this.invokeOriginalNode == null && !isInitMethod(this.name);
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);
        if (index >= this.nextLocalIndex) {
            this.nextLocalIndex = index + Type.getType(desc).getSize();
        }
    }
    
    public static boolean isOriginalMethodInvocation(final String owner, final String name, final String desc) {
        return owner.equals(BridgeUtils.WEAVER_TYPE.getInternalName()) && name.equals(WeaveUtils.CALL_ORIGINAL_METHOD.getName()) && desc.equals(WeaveUtils.CALL_ORIGINAL_METHOD.getDescriptor());
    }
    
    static {
        NOTICE_INSTRUMENTATION_ERROR_METHOD = new Method("noticeInstrumentationError", Type.VOID_TYPE, new Type[] { Type.getType(Throwable.class), Type.getType(String.class) });
    }
}

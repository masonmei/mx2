// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.org.objectweb.asm.commons;

import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.Opcodes;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.commons.RemappingMethodAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Remapper;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;
import java.util.HashMap;
import com.newrelic.agent.deps.org.objectweb.asm.commons.TryCatchBlockSorter;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AnalyzerAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.LocalVariablesSorter;

public abstract class MethodCallInlinerAdapter extends LocalVariablesSorter
{
    private final AnalyzerAdapter analyzerAdapter;
    private Map<String, InlinedMethod> inliners;
    static InlinedMethod DO_NOT_INLINE;
    
    public MethodCallInlinerAdapter(final String owner, final int access, final String name, final String desc, final MethodVisitor next, final boolean inlineFrames) {
        this(327680, owner, access, name, desc, next, inlineFrames);
    }
    
    protected MethodCallInlinerAdapter(final int api, final String owner, final int access, final String name, final String desc, final MethodVisitor next, final boolean inlineFrames) {
        super(api, access, desc, getNext(owner, access, name, desc, next, inlineFrames));
        this.analyzerAdapter = (inlineFrames ? ((AnalyzerAdapter)this.mv) : null);
    }
    
    private static MethodVisitor getNext(final String owner, final int access, final String name, final String desc, final MethodVisitor next, final boolean inlineFrames) {
        MethodVisitor mv = new TryCatchBlockSorter(next, access, name, desc, null, null);
        if (inlineFrames) {
            mv = new AnalyzerAdapter(owner, access, name, desc, mv);
        }
        return mv;
    }
    
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        final InlinedMethod inliner = this.getInliner(owner, name, desc);
        if (inliner == MethodCallInlinerAdapter.DO_NOT_INLINE) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        if (inliner.inliner == null) {
            MethodVisitor mv = this.mv;
            if (this.analyzerAdapter != null) {
                mv = new MergeFrameAdapter(this.api, this.analyzerAdapter, mv);
            }
            final int access = (opcode == 184) ? 8 : 0;
            inliner.inliner = new InliningAdapter(this.api, access, desc, this, mv, inliner.remapper);
        }
        inliner.method.accept(inliner.inliner);
    }
    
    protected abstract InlinedMethod mustInline(final String p0, final String p1, final String p2);
    
    private InlinedMethod getInliner(final String owner, final String name, final String desc) {
        if (this.inliners == null) {
            this.inliners = new HashMap<String, InlinedMethod>();
        }
        final String key = owner + "." + name + desc;
        InlinedMethod method = this.inliners.get(key);
        if (method == null) {
            method = this.mustInline(owner, name, desc);
            if (method == null) {
                method = MethodCallInlinerAdapter.DO_NOT_INLINE;
            }
            else {
                method.method.instructions.resetLabels();
            }
            this.inliners.put(key, method);
        }
        return method;
    }
    
    static {
        MethodCallInlinerAdapter.DO_NOT_INLINE = new InlinedMethod(null, null);
    }
    
    public static class InlinedMethod
    {
        public final MethodNode method;
        public final Remapper remapper;
        InliningAdapter inliner;
        
        public InlinedMethod(final MethodNode method, final Remapper remapper) {
            this.method = method;
            this.remapper = remapper;
        }
    }
    
    static class InliningAdapter extends RemappingMethodAdapter
    {
        private final int access;
        private final String desc;
        private final LocalVariablesSorter caller;
        private Label end;
        
        public InliningAdapter(final int api, final int access, final String desc, final LocalVariablesSorter caller, final MethodVisitor next, final Remapper remapper) {
            super(api, access, desc, next, remapper);
            this.access = access;
            this.desc = desc;
            this.caller = caller;
        }
        
        public void visitCode() {
            super.visitCode();
            final int off = ((this.access & 0x8) == 0x0) ? 1 : 0;
            final Type[] args = Type.getArgumentTypes(this.desc);
            int argRegister = off;
            for (int i = 0; i < args.length; ++i) {
                argRegister += args[i].getSize();
            }
            for (int i = args.length - 1; i >= 0; --i) {
                argRegister -= args[i].getSize();
                this.visitVarInsn(args[i].getOpcode(54), argRegister);
            }
            if (off > 0) {
                this.visitVarInsn(58, 0);
            }
            this.end = new Label();
        }
        
        public void visitInsn(final int opcode) {
            if (opcode >= 172 && opcode <= 177) {
                super.visitJumpInsn(167, this.end);
            }
            else {
                super.visitInsn(opcode);
            }
        }
        
        public void visitVarInsn(final int opcode, final int var) {
            super.visitVarInsn(opcode, var + this.firstLocal);
        }
        
        public void visitIincInsn(final int var, final int increment) {
            super.visitIincInsn(var + this.firstLocal, increment);
        }
        
        public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
            super.visitLocalVariable(name, desc, signature, start, end, index + this.firstLocal);
        }
        
        public void visitMaxs(final int stack, final int locals) {
            super.visitLabel(this.end);
        }
        
        public void visitEnd() {
        }
        
        protected int newLocalMapping(final Type type) {
            return this.caller.newLocal(type);
        }
    }
    
    static class MergeFrameAdapter extends MethodVisitor
    {
        private final AnalyzerAdapter analyzerAdapter;
        
        public MergeFrameAdapter(final int api, final AnalyzerAdapter analyzerAdapter, final MethodVisitor next) {
            super(api, next);
            this.analyzerAdapter = analyzerAdapter;
        }
        
        public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
            final List<Object> callerLocal = (List<Object>)this.analyzerAdapter.locals;
            final int nCallerLocal = (callerLocal == null) ? 0 : callerLocal.size();
            final int nMergedLocal = Math.max(nCallerLocal, nLocal);
            final Object[] mergedLocal = new Object[nMergedLocal];
            for (int i = 0; i < nCallerLocal; ++i) {
                if (callerLocal.get(i) != Opcodes.TOP) {
                    mergedLocal[i] = callerLocal.get(i);
                }
            }
            for (int i = 0; i < nLocal; ++i) {
                if (local[i] != Opcodes.TOP) {
                    mergedLocal[i] = local[i];
                }
            }
            final List<Object> callerStack = (List<Object>)this.analyzerAdapter.stack;
            final int nCallerStack = (callerStack == null) ? 0 : callerStack.size();
            final int nMergedStack = nCallerStack + nStack;
            final Object[] mergedStack = new Object[nMergedStack];
            for (int j = 0; j < nCallerStack; ++j) {
                mergedStack[j] = callerStack.get(j);
            }
            if (nStack > 0) {
                System.arraycopy(stack, 0, mergedStack, nCallerStack, nStack);
            }
            super.visitFrame(type, nMergedLocal, mergedLocal, nMergedStack, mergedStack);
        }
    }
}

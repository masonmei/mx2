// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import com.newrelic.agent.deps.org.objectweb.asm.Handle;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.Opcodes;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class AnalyzerAdapter extends MethodVisitor
{
    public List locals;
    public List stack;
    private List labels;
    public Map uninitializedTypes;
    private int maxStack;
    private int maxLocals;
    private String owner;
    static /* synthetic */ Class class$org$objectweb$asm$commons$AnalyzerAdapter;
    
    public AnalyzerAdapter(final String s, final int n, final String s2, final String s3, final MethodVisitor methodVisitor) {
        this(327680, s, n, s2, s3, methodVisitor);
        if (this.getClass() != AnalyzerAdapter.class$org$objectweb$asm$commons$AnalyzerAdapter) {
            throw new IllegalStateException();
        }
    }
    
    protected AnalyzerAdapter(final int n, final String owner, final int n2, final String s, final String s2, final MethodVisitor methodVisitor) {
        super(n, methodVisitor);
        this.owner = owner;
        this.locals = new ArrayList();
        this.stack = new ArrayList();
        this.uninitializedTypes = new HashMap();
        if ((n2 & 0x8) == 0x0) {
            if ("<init>".equals(s)) {
                this.locals.add(Opcodes.UNINITIALIZED_THIS);
            }
            else {
                this.locals.add(owner);
            }
        }
        final Type[] argumentTypes = Type.getArgumentTypes(s2);
        for (int i = 0; i < argumentTypes.length; ++i) {
            switch (argumentTypes[i].getSort()) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5: {
                    this.locals.add(Opcodes.INTEGER);
                    break;
                }
                case 6: {
                    this.locals.add(Opcodes.FLOAT);
                    break;
                }
                case 7: {
                    this.locals.add(Opcodes.LONG);
                    this.locals.add(Opcodes.TOP);
                    break;
                }
                case 8: {
                    this.locals.add(Opcodes.DOUBLE);
                    this.locals.add(Opcodes.TOP);
                    break;
                }
                case 9: {
                    this.locals.add(argumentTypes[i].getDescriptor());
                    break;
                }
                default: {
                    this.locals.add(argumentTypes[i].getInternalName());
                    break;
                }
            }
        }
        this.maxLocals = this.locals.size();
    }
    
    public void visitFrame(final int n, final int n2, final Object[] array, final int n3, final Object[] array2) {
        if (n != -1) {
            throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
        }
        if (this.mv != null) {
            this.mv.visitFrame(n, n2, array, n3, array2);
        }
        if (this.locals != null) {
            this.locals.clear();
            this.stack.clear();
        }
        else {
            this.locals = new ArrayList();
            this.stack = new ArrayList();
        }
        visitFrameTypes(n2, array, this.locals);
        visitFrameTypes(n3, array2, this.stack);
        this.maxStack = Math.max(this.maxStack, this.stack.size());
    }
    
    private static void visitFrameTypes(final int n, final Object[] array, final List list) {
        for (final Object o : array) {
            list.add(o);
            if (o == Opcodes.LONG || o == Opcodes.DOUBLE) {
                list.add(Opcodes.TOP);
            }
        }
    }
    
    public void visitInsn(final int n) {
        if (this.mv != null) {
            this.mv.visitInsn(n);
        }
        this.execute(n, 0, null);
        if ((n >= 172 && n <= 177) || n == 191) {
            this.locals = null;
            this.stack = null;
        }
    }
    
    public void visitIntInsn(final int n, final int n2) {
        if (this.mv != null) {
            this.mv.visitIntInsn(n, n2);
        }
        this.execute(n, n2, null);
    }
    
    public void visitVarInsn(final int n, final int n2) {
        if (this.mv != null) {
            this.mv.visitVarInsn(n, n2);
        }
        this.execute(n, n2, null);
    }
    
    public void visitTypeInsn(final int n, final String s) {
        if (n == 187) {
            if (this.labels == null) {
                final Label label = new Label();
                (this.labels = new ArrayList(3)).add(label);
                if (this.mv != null) {
                    this.mv.visitLabel(label);
                }
            }
            for (int i = 0; i < this.labels.size(); ++i) {
                this.uninitializedTypes.put(this.labels.get(i), s);
            }
        }
        if (this.mv != null) {
            this.mv.visitTypeInsn(n, s);
        }
        this.execute(n, 0, s);
    }
    
    public void visitFieldInsn(final int n, final String s, final String s2, final String s3) {
        if (this.mv != null) {
            this.mv.visitFieldInsn(n, s, s2, s3);
        }
        this.execute(n, 0, s3);
    }
    
    public void visitMethodInsn(final int n, final String s, final String s2, final String s3) {
        if (this.api >= 327680) {
            super.visitMethodInsn(n, s, s2, s3);
            return;
        }
        this.doVisitMethodInsn(n, s, s2, s3, n == 185);
    }
    
    public void visitMethodInsn(final int n, final String s, final String s2, final String s3, final boolean b) {
        if (this.api < 327680) {
            super.visitMethodInsn(n, s, s2, s3, b);
            return;
        }
        this.doVisitMethodInsn(n, s, s2, s3, b);
    }
    
    private void doVisitMethodInsn(final int n, final String s, final String s2, final String s3, final boolean b) {
        if (this.mv != null) {
            this.mv.visitMethodInsn(n, s, s2, s3, b);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        this.pop(s3);
        if (n != 184) {
            final Object pop = this.pop();
            if (n == 183 && s2.charAt(0) == '<') {
                Object o;
                if (pop == Opcodes.UNINITIALIZED_THIS) {
                    o = this.owner;
                }
                else {
                    o = this.uninitializedTypes.get(pop);
                }
                for (int i = 0; i < this.locals.size(); ++i) {
                    if (this.locals.get(i) == pop) {
                        this.locals.set(i, o);
                    }
                }
                for (int j = 0; j < this.stack.size(); ++j) {
                    if (this.stack.get(j) == pop) {
                        this.stack.set(j, o);
                    }
                }
            }
        }
        this.pushDesc(s3);
        this.labels = null;
    }
    
    public void visitInvokeDynamicInsn(final String s, final String s2, final Handle handle, final Object... array) {
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(s, s2, handle, array);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        this.pop(s2);
        this.pushDesc(s2);
        this.labels = null;
    }
    
    public void visitJumpInsn(final int n, final Label label) {
        if (this.mv != null) {
            this.mv.visitJumpInsn(n, label);
        }
        this.execute(n, 0, null);
        if (n == 167) {
            this.locals = null;
            this.stack = null;
        }
    }
    
    public void visitLabel(final Label label) {
        if (this.mv != null) {
            this.mv.visitLabel(label);
        }
        if (this.labels == null) {
            this.labels = new ArrayList(3);
        }
        this.labels.add(label);
    }
    
    public void visitLdcInsn(final Object o) {
        if (this.mv != null) {
            this.mv.visitLdcInsn(o);
        }
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        if (o instanceof Integer) {
            this.push(Opcodes.INTEGER);
        }
        else if (o instanceof Long) {
            this.push(Opcodes.LONG);
            this.push(Opcodes.TOP);
        }
        else if (o instanceof Float) {
            this.push(Opcodes.FLOAT);
        }
        else if (o instanceof Double) {
            this.push(Opcodes.DOUBLE);
            this.push(Opcodes.TOP);
        }
        else if (o instanceof String) {
            this.push("java/lang/String");
        }
        else if (o instanceof Type) {
            final int sort = ((Type)o).getSort();
            if (sort == 10 || sort == 9) {
                this.push("java/lang/Class");
            }
            else {
                if (sort != 11) {
                    throw new IllegalArgumentException();
                }
                this.push("java/lang/invoke/MethodType");
            }
        }
        else {
            if (!(o instanceof Handle)) {
                throw new IllegalArgumentException();
            }
            this.push("java/lang/invoke/MethodHandle");
        }
        this.labels = null;
    }
    
    public void visitIincInsn(final int n, final int n2) {
        if (this.mv != null) {
            this.mv.visitIincInsn(n, n2);
        }
        this.execute(132, n, null);
    }
    
    public void visitTableSwitchInsn(final int n, final int n2, final Label label, final Label... array) {
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(n, n2, label, array);
        }
        this.execute(170, 0, null);
        this.locals = null;
        this.stack = null;
    }
    
    public void visitLookupSwitchInsn(final Label label, final int[] array, final Label[] array2) {
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(label, array, array2);
        }
        this.execute(171, 0, null);
        this.locals = null;
        this.stack = null;
    }
    
    public void visitMultiANewArrayInsn(final String s, final int n) {
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(s, n);
        }
        this.execute(197, n, s);
    }
    
    public void visitMaxs(final int n, final int n2) {
        if (this.mv != null) {
            this.maxStack = Math.max(this.maxStack, n);
            this.maxLocals = Math.max(this.maxLocals, n2);
            this.mv.visitMaxs(this.maxStack, this.maxLocals);
        }
    }
    
    private Object get(final int n) {
        this.maxLocals = Math.max(this.maxLocals, n + 1);
        return (n < this.locals.size()) ? this.locals.get(n) : Opcodes.TOP;
    }
    
    private void set(final int i, final Object o) {
        this.maxLocals = Math.max(this.maxLocals, i + 1);
        while (i >= this.locals.size()) {
            this.locals.add(Opcodes.TOP);
        }
        this.locals.set(i, o);
    }
    
    private void push(final Object o) {
        this.stack.add(o);
        this.maxStack = Math.max(this.maxStack, this.stack.size());
    }
    
    private void pushDesc(final String s) {
        final int n = (s.charAt(0) == '(') ? (s.indexOf(41) + 1) : 0;
        switch (s.charAt(n)) {
            case 'V': {}
            case 'B':
            case 'C':
            case 'I':
            case 'S':
            case 'Z': {
                this.push(Opcodes.INTEGER);
            }
            case 'F': {
                this.push(Opcodes.FLOAT);
            }
            case 'J': {
                this.push(Opcodes.LONG);
                this.push(Opcodes.TOP);
            }
            case 'D': {
                this.push(Opcodes.DOUBLE);
                this.push(Opcodes.TOP);
            }
            case '[': {
                if (n == 0) {
                    this.push(s);
                    break;
                }
                this.push(s.substring(n, s.length()));
                break;
            }
            default: {
                if (n == 0) {
                    this.push(s.substring(1, s.length() - 1));
                    break;
                }
                this.push(s.substring(n + 1, s.length() - 1));
                break;
            }
        }
    }
    
    private Object pop() {
        return this.stack.remove(this.stack.size() - 1);
    }
    
    private void pop(final int n) {
        final int size = this.stack.size();
        for (int n2 = size - n, i = size - 1; i >= n2; --i) {
            this.stack.remove(i);
        }
    }
    
    private void pop(final String s) {
        final char char1 = s.charAt(0);
        if (char1 == '(') {
            int n = 0;
            final Type[] argumentTypes = Type.getArgumentTypes(s);
            for (int i = 0; i < argumentTypes.length; ++i) {
                n += argumentTypes[i].getSize();
            }
            this.pop(n);
        }
        else if (char1 == 'J' || char1 == 'D') {
            this.pop(2);
        }
        else {
            this.pop(1);
        }
    }
    
    private void execute(final int n, final int n2, final String s) {
        if (this.locals == null) {
            this.labels = null;
            return;
        }
        Label_1940: {
            switch (n) {
                case 0:
                case 116:
                case 117:
                case 118:
                case 119:
                case 145:
                case 146:
                case 147:
                case 167:
                case 177: {
                    break;
                }
                case 1: {
                    this.push(Opcodes.NULL);
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 16:
                case 17: {
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 9:
                case 10: {
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 11:
                case 12:
                case 13: {
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 14:
                case 15: {
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 21:
                case 23:
                case 25: {
                    this.push(this.get(n2));
                    break;
                }
                case 22:
                case 24: {
                    this.push(this.get(n2));
                    this.push(Opcodes.TOP);
                    break;
                }
                case 46:
                case 51:
                case 52:
                case 53: {
                    this.pop(2);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 47:
                case 143: {
                    this.pop(2);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 48: {
                    this.pop(2);
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 49:
                case 138: {
                    this.pop(2);
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 50: {
                    this.pop(1);
                    final Object pop = this.pop();
                    if (pop instanceof String) {
                        this.pushDesc(((String)pop).substring(1));
                        break;
                    }
                    this.push("java/lang/Object");
                    break;
                }
                case 54:
                case 56:
                case 58: {
                    this.set(n2, this.pop());
                    if (n2 <= 0) {
                        break;
                    }
                    final Object value = this.get(n2 - 1);
                    if (value == Opcodes.LONG || value == Opcodes.DOUBLE) {
                        this.set(n2 - 1, Opcodes.TOP);
                        break;
                    }
                    break;
                }
                case 55:
                case 57: {
                    this.pop(1);
                    this.set(n2, this.pop());
                    this.set(n2 + 1, Opcodes.TOP);
                    if (n2 <= 0) {
                        break;
                    }
                    final Object value2 = this.get(n2 - 1);
                    if (value2 == Opcodes.LONG || value2 == Opcodes.DOUBLE) {
                        this.set(n2 - 1, Opcodes.TOP);
                        break;
                    }
                    break;
                }
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86: {
                    this.pop(3);
                    break;
                }
                case 80:
                case 82: {
                    this.pop(4);
                    break;
                }
                case 87:
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 170:
                case 171:
                case 172:
                case 174:
                case 176:
                case 191:
                case 194:
                case 195:
                case 198:
                case 199: {
                    this.pop(1);
                    break;
                }
                case 88:
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 173:
                case 175: {
                    this.pop(2);
                    break;
                }
                case 89: {
                    final Object pop2 = this.pop();
                    this.push(pop2);
                    this.push(pop2);
                    break;
                }
                case 90: {
                    final Object pop3 = this.pop();
                    final Object pop4 = this.pop();
                    this.push(pop3);
                    this.push(pop4);
                    this.push(pop3);
                    break;
                }
                case 91: {
                    final Object pop5 = this.pop();
                    final Object pop6 = this.pop();
                    final Object pop7 = this.pop();
                    this.push(pop5);
                    this.push(pop7);
                    this.push(pop6);
                    this.push(pop5);
                    break;
                }
                case 92: {
                    final Object pop8 = this.pop();
                    final Object pop9 = this.pop();
                    this.push(pop9);
                    this.push(pop8);
                    this.push(pop9);
                    this.push(pop8);
                    break;
                }
                case 93: {
                    final Object pop10 = this.pop();
                    final Object pop11 = this.pop();
                    final Object pop12 = this.pop();
                    this.push(pop11);
                    this.push(pop10);
                    this.push(pop12);
                    this.push(pop11);
                    this.push(pop10);
                    break;
                }
                case 94: {
                    final Object pop13 = this.pop();
                    final Object pop14 = this.pop();
                    final Object pop15 = this.pop();
                    final Object pop16 = this.pop();
                    this.push(pop14);
                    this.push(pop13);
                    this.push(pop16);
                    this.push(pop15);
                    this.push(pop14);
                    this.push(pop13);
                    break;
                }
                case 95: {
                    final Object pop17 = this.pop();
                    final Object pop18 = this.pop();
                    this.push(pop17);
                    this.push(pop18);
                    break;
                }
                case 96:
                case 100:
                case 104:
                case 108:
                case 112:
                case 120:
                case 122:
                case 124:
                case 126:
                case 128:
                case 130:
                case 136:
                case 142:
                case 149:
                case 150: {
                    this.pop(2);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 97:
                case 101:
                case 105:
                case 109:
                case 113:
                case 127:
                case 129:
                case 131: {
                    this.pop(4);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 98:
                case 102:
                case 106:
                case 110:
                case 114:
                case 137:
                case 144: {
                    this.pop(2);
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 99:
                case 103:
                case 107:
                case 111:
                case 115: {
                    this.pop(4);
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 121:
                case 123:
                case 125: {
                    this.pop(3);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 132: {
                    this.set(n2, Opcodes.INTEGER);
                    break;
                }
                case 133:
                case 140: {
                    this.pop(1);
                    this.push(Opcodes.LONG);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 134: {
                    this.pop(1);
                    this.push(Opcodes.FLOAT);
                    break;
                }
                case 135:
                case 141: {
                    this.pop(1);
                    this.push(Opcodes.DOUBLE);
                    this.push(Opcodes.TOP);
                    break;
                }
                case 139:
                case 190:
                case 193: {
                    this.pop(1);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 148:
                case 151:
                case 152: {
                    this.pop(4);
                    this.push(Opcodes.INTEGER);
                    break;
                }
                case 168:
                case 169: {
                    throw new RuntimeException("JSR/RET are not supported");
                }
                case 178: {
                    this.pushDesc(s);
                    break;
                }
                case 179: {
                    this.pop(s);
                    break;
                }
                case 180: {
                    this.pop(1);
                    this.pushDesc(s);
                    break;
                }
                case 181: {
                    this.pop(s);
                    this.pop();
                    break;
                }
                case 187: {
                    this.push(this.labels.get(0));
                    break;
                }
                case 188: {
                    this.pop();
                    switch (n2) {
                        case 4: {
                            this.pushDesc("[Z");
                            break Label_1940;
                        }
                        case 5: {
                            this.pushDesc("[C");
                            break Label_1940;
                        }
                        case 8: {
                            this.pushDesc("[B");
                            break Label_1940;
                        }
                        case 9: {
                            this.pushDesc("[S");
                            break Label_1940;
                        }
                        case 10: {
                            this.pushDesc("[I");
                            break Label_1940;
                        }
                        case 6: {
                            this.pushDesc("[F");
                            break Label_1940;
                        }
                        case 7: {
                            this.pushDesc("[D");
                            break Label_1940;
                        }
                        default: {
                            this.pushDesc("[J");
                            break Label_1940;
                        }
                    }
                    break;
                }
                case 189: {
                    this.pop();
                    this.pushDesc("[" + Type.getObjectType(s));
                    break;
                }
                case 192: {
                    this.pop();
                    this.pushDesc(Type.getObjectType(s).getDescriptor());
                    break;
                }
                default: {
                    this.pop(n2);
                    this.pushDesc(s);
                    break;
                }
            }
        }
        this.labels = null;
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
        AnalyzerAdapter.class$org$objectweb$asm$commons$AnalyzerAdapter = class$("com.newrelic.agent.deps.org.objectweb.asm.commons.AnalyzerAdapter");
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.Handle;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class InstructionAdapter extends MethodVisitor
{
    public static final Type OBJECT_TYPE;
    static /* synthetic */ Class class$org$objectweb$asm$commons$InstructionAdapter;
    
    public InstructionAdapter(final MethodVisitor methodVisitor) {
        this(327680, methodVisitor);
        if (this.getClass() != InstructionAdapter.class$org$objectweb$asm$commons$InstructionAdapter) {
            throw new IllegalStateException();
        }
    }
    
    protected InstructionAdapter(final int n, final MethodVisitor methodVisitor) {
        super(n, methodVisitor);
    }
    
    public void visitInsn(final int n) {
        switch (n) {
            case 0: {
                this.nop();
                break;
            }
            case 1: {
                this.aconst(null);
                break;
            }
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8: {
                this.iconst(n - 3);
                break;
            }
            case 9:
            case 10: {
                this.lconst(n - 9);
                break;
            }
            case 11:
            case 12:
            case 13: {
                this.fconst(n - 11);
                break;
            }
            case 14:
            case 15: {
                this.dconst(n - 14);
                break;
            }
            case 46: {
                this.aload(Type.INT_TYPE);
                break;
            }
            case 47: {
                this.aload(Type.LONG_TYPE);
                break;
            }
            case 48: {
                this.aload(Type.FLOAT_TYPE);
                break;
            }
            case 49: {
                this.aload(Type.DOUBLE_TYPE);
                break;
            }
            case 50: {
                this.aload(InstructionAdapter.OBJECT_TYPE);
                break;
            }
            case 51: {
                this.aload(Type.BYTE_TYPE);
                break;
            }
            case 52: {
                this.aload(Type.CHAR_TYPE);
                break;
            }
            case 53: {
                this.aload(Type.SHORT_TYPE);
                break;
            }
            case 79: {
                this.astore(Type.INT_TYPE);
                break;
            }
            case 80: {
                this.astore(Type.LONG_TYPE);
                break;
            }
            case 81: {
                this.astore(Type.FLOAT_TYPE);
                break;
            }
            case 82: {
                this.astore(Type.DOUBLE_TYPE);
                break;
            }
            case 83: {
                this.astore(InstructionAdapter.OBJECT_TYPE);
                break;
            }
            case 84: {
                this.astore(Type.BYTE_TYPE);
                break;
            }
            case 85: {
                this.astore(Type.CHAR_TYPE);
                break;
            }
            case 86: {
                this.astore(Type.SHORT_TYPE);
                break;
            }
            case 87: {
                this.pop();
                break;
            }
            case 88: {
                this.pop2();
                break;
            }
            case 89: {
                this.dup();
                break;
            }
            case 90: {
                this.dupX1();
                break;
            }
            case 91: {
                this.dupX2();
                break;
            }
            case 92: {
                this.dup2();
                break;
            }
            case 93: {
                this.dup2X1();
                break;
            }
            case 94: {
                this.dup2X2();
                break;
            }
            case 95: {
                this.swap();
                break;
            }
            case 96: {
                this.add(Type.INT_TYPE);
                break;
            }
            case 97: {
                this.add(Type.LONG_TYPE);
                break;
            }
            case 98: {
                this.add(Type.FLOAT_TYPE);
                break;
            }
            case 99: {
                this.add(Type.DOUBLE_TYPE);
                break;
            }
            case 100: {
                this.sub(Type.INT_TYPE);
                break;
            }
            case 101: {
                this.sub(Type.LONG_TYPE);
                break;
            }
            case 102: {
                this.sub(Type.FLOAT_TYPE);
                break;
            }
            case 103: {
                this.sub(Type.DOUBLE_TYPE);
                break;
            }
            case 104: {
                this.mul(Type.INT_TYPE);
                break;
            }
            case 105: {
                this.mul(Type.LONG_TYPE);
                break;
            }
            case 106: {
                this.mul(Type.FLOAT_TYPE);
                break;
            }
            case 107: {
                this.mul(Type.DOUBLE_TYPE);
                break;
            }
            case 108: {
                this.div(Type.INT_TYPE);
                break;
            }
            case 109: {
                this.div(Type.LONG_TYPE);
                break;
            }
            case 110: {
                this.div(Type.FLOAT_TYPE);
                break;
            }
            case 111: {
                this.div(Type.DOUBLE_TYPE);
                break;
            }
            case 112: {
                this.rem(Type.INT_TYPE);
                break;
            }
            case 113: {
                this.rem(Type.LONG_TYPE);
                break;
            }
            case 114: {
                this.rem(Type.FLOAT_TYPE);
                break;
            }
            case 115: {
                this.rem(Type.DOUBLE_TYPE);
                break;
            }
            case 116: {
                this.neg(Type.INT_TYPE);
                break;
            }
            case 117: {
                this.neg(Type.LONG_TYPE);
                break;
            }
            case 118: {
                this.neg(Type.FLOAT_TYPE);
                break;
            }
            case 119: {
                this.neg(Type.DOUBLE_TYPE);
                break;
            }
            case 120: {
                this.shl(Type.INT_TYPE);
                break;
            }
            case 121: {
                this.shl(Type.LONG_TYPE);
                break;
            }
            case 122: {
                this.shr(Type.INT_TYPE);
                break;
            }
            case 123: {
                this.shr(Type.LONG_TYPE);
                break;
            }
            case 124: {
                this.ushr(Type.INT_TYPE);
                break;
            }
            case 125: {
                this.ushr(Type.LONG_TYPE);
                break;
            }
            case 126: {
                this.and(Type.INT_TYPE);
                break;
            }
            case 127: {
                this.and(Type.LONG_TYPE);
                break;
            }
            case 128: {
                this.or(Type.INT_TYPE);
                break;
            }
            case 129: {
                this.or(Type.LONG_TYPE);
                break;
            }
            case 130: {
                this.xor(Type.INT_TYPE);
                break;
            }
            case 131: {
                this.xor(Type.LONG_TYPE);
                break;
            }
            case 133: {
                this.cast(Type.INT_TYPE, Type.LONG_TYPE);
                break;
            }
            case 134: {
                this.cast(Type.INT_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 135: {
                this.cast(Type.INT_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 136: {
                this.cast(Type.LONG_TYPE, Type.INT_TYPE);
                break;
            }
            case 137: {
                this.cast(Type.LONG_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 138: {
                this.cast(Type.LONG_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 139: {
                this.cast(Type.FLOAT_TYPE, Type.INT_TYPE);
                break;
            }
            case 140: {
                this.cast(Type.FLOAT_TYPE, Type.LONG_TYPE);
                break;
            }
            case 141: {
                this.cast(Type.FLOAT_TYPE, Type.DOUBLE_TYPE);
                break;
            }
            case 142: {
                this.cast(Type.DOUBLE_TYPE, Type.INT_TYPE);
                break;
            }
            case 143: {
                this.cast(Type.DOUBLE_TYPE, Type.LONG_TYPE);
                break;
            }
            case 144: {
                this.cast(Type.DOUBLE_TYPE, Type.FLOAT_TYPE);
                break;
            }
            case 145: {
                this.cast(Type.INT_TYPE, Type.BYTE_TYPE);
                break;
            }
            case 146: {
                this.cast(Type.INT_TYPE, Type.CHAR_TYPE);
                break;
            }
            case 147: {
                this.cast(Type.INT_TYPE, Type.SHORT_TYPE);
                break;
            }
            case 148: {
                this.lcmp();
                break;
            }
            case 149: {
                this.cmpl(Type.FLOAT_TYPE);
                break;
            }
            case 150: {
                this.cmpg(Type.FLOAT_TYPE);
                break;
            }
            case 151: {
                this.cmpl(Type.DOUBLE_TYPE);
                break;
            }
            case 152: {
                this.cmpg(Type.DOUBLE_TYPE);
                break;
            }
            case 172: {
                this.areturn(Type.INT_TYPE);
                break;
            }
            case 173: {
                this.areturn(Type.LONG_TYPE);
                break;
            }
            case 174: {
                this.areturn(Type.FLOAT_TYPE);
                break;
            }
            case 175: {
                this.areturn(Type.DOUBLE_TYPE);
                break;
            }
            case 176: {
                this.areturn(InstructionAdapter.OBJECT_TYPE);
                break;
            }
            case 177: {
                this.areturn(Type.VOID_TYPE);
                break;
            }
            case 190: {
                this.arraylength();
                break;
            }
            case 191: {
                this.athrow();
                break;
            }
            case 194: {
                this.monitorenter();
                break;
            }
            case 195: {
                this.monitorexit();
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public void visitIntInsn(final int n, final int n2) {
        Label_0196: {
            switch (n) {
                case 16: {
                    this.iconst(n2);
                    break;
                }
                case 17: {
                    this.iconst(n2);
                    break;
                }
                case 188: {
                    switch (n2) {
                        case 4: {
                            this.newarray(Type.BOOLEAN_TYPE);
                            break Label_0196;
                        }
                        case 5: {
                            this.newarray(Type.CHAR_TYPE);
                            break Label_0196;
                        }
                        case 8: {
                            this.newarray(Type.BYTE_TYPE);
                            break Label_0196;
                        }
                        case 9: {
                            this.newarray(Type.SHORT_TYPE);
                            break Label_0196;
                        }
                        case 10: {
                            this.newarray(Type.INT_TYPE);
                            break Label_0196;
                        }
                        case 6: {
                            this.newarray(Type.FLOAT_TYPE);
                            break Label_0196;
                        }
                        case 11: {
                            this.newarray(Type.LONG_TYPE);
                            break Label_0196;
                        }
                        case 7: {
                            this.newarray(Type.DOUBLE_TYPE);
                            break Label_0196;
                        }
                        default: {
                            throw new IllegalArgumentException();
                        }
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
    }
    
    public void visitVarInsn(final int n, final int n2) {
        switch (n) {
            case 21: {
                this.load(n2, Type.INT_TYPE);
                break;
            }
            case 22: {
                this.load(n2, Type.LONG_TYPE);
                break;
            }
            case 23: {
                this.load(n2, Type.FLOAT_TYPE);
                break;
            }
            case 24: {
                this.load(n2, Type.DOUBLE_TYPE);
                break;
            }
            case 25: {
                this.load(n2, InstructionAdapter.OBJECT_TYPE);
                break;
            }
            case 54: {
                this.store(n2, Type.INT_TYPE);
                break;
            }
            case 55: {
                this.store(n2, Type.LONG_TYPE);
                break;
            }
            case 56: {
                this.store(n2, Type.FLOAT_TYPE);
                break;
            }
            case 57: {
                this.store(n2, Type.DOUBLE_TYPE);
                break;
            }
            case 58: {
                this.store(n2, InstructionAdapter.OBJECT_TYPE);
                break;
            }
            case 169: {
                this.ret(n2);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public void visitTypeInsn(final int n, final String s) {
        final Type objectType = Type.getObjectType(s);
        switch (n) {
            case 187: {
                this.anew(objectType);
                break;
            }
            case 189: {
                this.newarray(objectType);
                break;
            }
            case 192: {
                this.checkcast(objectType);
                break;
            }
            case 193: {
                this.instanceOf(objectType);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public void visitFieldInsn(final int n, final String s, final String s2, final String s3) {
        switch (n) {
            case 178: {
                this.getstatic(s, s2, s3);
                break;
            }
            case 179: {
                this.putstatic(s, s2, s3);
                break;
            }
            case 180: {
                this.getfield(s, s2, s3);
                break;
            }
            case 181: {
                this.putfield(s, s2, s3);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
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
        switch (n) {
            case 183: {
                this.invokespecial(s, s2, s3, b);
                break;
            }
            case 182: {
                this.invokevirtual(s, s2, s3, b);
                break;
            }
            case 184: {
                this.invokestatic(s, s2, s3, b);
                break;
            }
            case 185: {
                this.invokeinterface(s, s2, s3);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public void visitInvokeDynamicInsn(final String s, final String s2, final Handle handle, final Object... array) {
        this.invokedynamic(s, s2, handle, array);
    }
    
    public void visitJumpInsn(final int n, final Label label) {
        switch (n) {
            case 153: {
                this.ifeq(label);
                break;
            }
            case 154: {
                this.ifne(label);
                break;
            }
            case 155: {
                this.iflt(label);
                break;
            }
            case 156: {
                this.ifge(label);
                break;
            }
            case 157: {
                this.ifgt(label);
                break;
            }
            case 158: {
                this.ifle(label);
                break;
            }
            case 159: {
                this.ificmpeq(label);
                break;
            }
            case 160: {
                this.ificmpne(label);
                break;
            }
            case 161: {
                this.ificmplt(label);
                break;
            }
            case 162: {
                this.ificmpge(label);
                break;
            }
            case 163: {
                this.ificmpgt(label);
                break;
            }
            case 164: {
                this.ificmple(label);
                break;
            }
            case 165: {
                this.ifacmpeq(label);
                break;
            }
            case 166: {
                this.ifacmpne(label);
                break;
            }
            case 167: {
                this.goTo(label);
                break;
            }
            case 168: {
                this.jsr(label);
                break;
            }
            case 198: {
                this.ifnull(label);
                break;
            }
            case 199: {
                this.ifnonnull(label);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public void visitLabel(final Label label) {
        this.mark(label);
    }
    
    public void visitLdcInsn(final Object o) {
        if (o instanceof Integer) {
            this.iconst((int)o);
        }
        else if (o instanceof Byte) {
            this.iconst((int)o);
        }
        else if (o instanceof Character) {
            this.iconst((char)o);
        }
        else if (o instanceof Short) {
            this.iconst((int)o);
        }
        else if (o instanceof Boolean) {
            this.iconst(((boolean)o) ? 1 : 0);
        }
        else if (o instanceof Float) {
            this.fconst((float)o);
        }
        else if (o instanceof Long) {
            this.lconst((long)o);
        }
        else if (o instanceof Double) {
            this.dconst((double)o);
        }
        else if (o instanceof String) {
            this.aconst(o);
        }
        else if (o instanceof Type) {
            this.tconst((Type)o);
        }
        else {
            if (!(o instanceof Handle)) {
                throw new IllegalArgumentException();
            }
            this.hconst((Handle)o);
        }
    }
    
    public void visitIincInsn(final int n, final int n2) {
        this.iinc(n, n2);
    }
    
    public void visitTableSwitchInsn(final int n, final int n2, final Label label, final Label... array) {
        this.tableswitch(n, n2, label, array);
    }
    
    public void visitLookupSwitchInsn(final Label label, final int[] array, final Label[] array2) {
        this.lookupswitch(label, array, array2);
    }
    
    public void visitMultiANewArrayInsn(final String s, final int n) {
        this.multianewarray(s, n);
    }
    
    public void nop() {
        this.mv.visitInsn(0);
    }
    
    public void aconst(final Object o) {
        if (o == null) {
            this.mv.visitInsn(1);
        }
        else {
            this.mv.visitLdcInsn(o);
        }
    }
    
    public void iconst(final int n) {
        if (n >= -1 && n <= 5) {
            this.mv.visitInsn(3 + n);
        }
        else if (n >= -128 && n <= 127) {
            this.mv.visitIntInsn(16, n);
        }
        else if (n >= -32768 && n <= 32767) {
            this.mv.visitIntInsn(17, n);
        }
        else {
            this.mv.visitLdcInsn(new Integer(n));
        }
    }
    
    public void lconst(final long n) {
        if (n == 0L || n == 1L) {
            this.mv.visitInsn(9 + (int)n);
        }
        else {
            this.mv.visitLdcInsn(new Long(n));
        }
    }
    
    public void fconst(final float n) {
        final int floatToIntBits = Float.floatToIntBits(n);
        if (floatToIntBits == 0L || floatToIntBits == 1065353216 || floatToIntBits == 1073741824) {
            this.mv.visitInsn(11 + (int)n);
        }
        else {
            this.mv.visitLdcInsn(new Float(n));
        }
    }
    
    public void dconst(final double n) {
        final long doubleToLongBits = Double.doubleToLongBits(n);
        if (doubleToLongBits == 0L || doubleToLongBits == 4607182418800017408L) {
            this.mv.visitInsn(14 + (int)n);
        }
        else {
            this.mv.visitLdcInsn(new Double(n));
        }
    }
    
    public void tconst(final Type type) {
        this.mv.visitLdcInsn(type);
    }
    
    public void hconst(final Handle handle) {
        this.mv.visitLdcInsn(handle);
    }
    
    public void load(final int n, final Type type) {
        this.mv.visitVarInsn(type.getOpcode(21), n);
    }
    
    public void aload(final Type type) {
        this.mv.visitInsn(type.getOpcode(46));
    }
    
    public void store(final int n, final Type type) {
        this.mv.visitVarInsn(type.getOpcode(54), n);
    }
    
    public void astore(final Type type) {
        this.mv.visitInsn(type.getOpcode(79));
    }
    
    public void pop() {
        this.mv.visitInsn(87);
    }
    
    public void pop2() {
        this.mv.visitInsn(88);
    }
    
    public void dup() {
        this.mv.visitInsn(89);
    }
    
    public void dup2() {
        this.mv.visitInsn(92);
    }
    
    public void dupX1() {
        this.mv.visitInsn(90);
    }
    
    public void dupX2() {
        this.mv.visitInsn(91);
    }
    
    public void dup2X1() {
        this.mv.visitInsn(93);
    }
    
    public void dup2X2() {
        this.mv.visitInsn(94);
    }
    
    public void swap() {
        this.mv.visitInsn(95);
    }
    
    public void add(final Type type) {
        this.mv.visitInsn(type.getOpcode(96));
    }
    
    public void sub(final Type type) {
        this.mv.visitInsn(type.getOpcode(100));
    }
    
    public void mul(final Type type) {
        this.mv.visitInsn(type.getOpcode(104));
    }
    
    public void div(final Type type) {
        this.mv.visitInsn(type.getOpcode(108));
    }
    
    public void rem(final Type type) {
        this.mv.visitInsn(type.getOpcode(112));
    }
    
    public void neg(final Type type) {
        this.mv.visitInsn(type.getOpcode(116));
    }
    
    public void shl(final Type type) {
        this.mv.visitInsn(type.getOpcode(120));
    }
    
    public void shr(final Type type) {
        this.mv.visitInsn(type.getOpcode(122));
    }
    
    public void ushr(final Type type) {
        this.mv.visitInsn(type.getOpcode(124));
    }
    
    public void and(final Type type) {
        this.mv.visitInsn(type.getOpcode(126));
    }
    
    public void or(final Type type) {
        this.mv.visitInsn(type.getOpcode(128));
    }
    
    public void xor(final Type type) {
        this.mv.visitInsn(type.getOpcode(130));
    }
    
    public void iinc(final int n, final int n2) {
        this.mv.visitIincInsn(n, n2);
    }
    
    public void cast(final Type type, final Type type2) {
        if (type != type2) {
            if (type == Type.DOUBLE_TYPE) {
                if (type2 == Type.FLOAT_TYPE) {
                    this.mv.visitInsn(144);
                }
                else if (type2 == Type.LONG_TYPE) {
                    this.mv.visitInsn(143);
                }
                else {
                    this.mv.visitInsn(142);
                    this.cast(Type.INT_TYPE, type2);
                }
            }
            else if (type == Type.FLOAT_TYPE) {
                if (type2 == Type.DOUBLE_TYPE) {
                    this.mv.visitInsn(141);
                }
                else if (type2 == Type.LONG_TYPE) {
                    this.mv.visitInsn(140);
                }
                else {
                    this.mv.visitInsn(139);
                    this.cast(Type.INT_TYPE, type2);
                }
            }
            else if (type == Type.LONG_TYPE) {
                if (type2 == Type.DOUBLE_TYPE) {
                    this.mv.visitInsn(138);
                }
                else if (type2 == Type.FLOAT_TYPE) {
                    this.mv.visitInsn(137);
                }
                else {
                    this.mv.visitInsn(136);
                    this.cast(Type.INT_TYPE, type2);
                }
            }
            else if (type2 == Type.BYTE_TYPE) {
                this.mv.visitInsn(145);
            }
            else if (type2 == Type.CHAR_TYPE) {
                this.mv.visitInsn(146);
            }
            else if (type2 == Type.DOUBLE_TYPE) {
                this.mv.visitInsn(135);
            }
            else if (type2 == Type.FLOAT_TYPE) {
                this.mv.visitInsn(134);
            }
            else if (type2 == Type.LONG_TYPE) {
                this.mv.visitInsn(133);
            }
            else if (type2 == Type.SHORT_TYPE) {
                this.mv.visitInsn(147);
            }
        }
    }
    
    public void lcmp() {
        this.mv.visitInsn(148);
    }
    
    public void cmpl(final Type type) {
        this.mv.visitInsn((type == Type.FLOAT_TYPE) ? 149 : 151);
    }
    
    public void cmpg(final Type type) {
        this.mv.visitInsn((type == Type.FLOAT_TYPE) ? 150 : 152);
    }
    
    public void ifeq(final Label label) {
        this.mv.visitJumpInsn(153, label);
    }
    
    public void ifne(final Label label) {
        this.mv.visitJumpInsn(154, label);
    }
    
    public void iflt(final Label label) {
        this.mv.visitJumpInsn(155, label);
    }
    
    public void ifge(final Label label) {
        this.mv.visitJumpInsn(156, label);
    }
    
    public void ifgt(final Label label) {
        this.mv.visitJumpInsn(157, label);
    }
    
    public void ifle(final Label label) {
        this.mv.visitJumpInsn(158, label);
    }
    
    public void ificmpeq(final Label label) {
        this.mv.visitJumpInsn(159, label);
    }
    
    public void ificmpne(final Label label) {
        this.mv.visitJumpInsn(160, label);
    }
    
    public void ificmplt(final Label label) {
        this.mv.visitJumpInsn(161, label);
    }
    
    public void ificmpge(final Label label) {
        this.mv.visitJumpInsn(162, label);
    }
    
    public void ificmpgt(final Label label) {
        this.mv.visitJumpInsn(163, label);
    }
    
    public void ificmple(final Label label) {
        this.mv.visitJumpInsn(164, label);
    }
    
    public void ifacmpeq(final Label label) {
        this.mv.visitJumpInsn(165, label);
    }
    
    public void ifacmpne(final Label label) {
        this.mv.visitJumpInsn(166, label);
    }
    
    public void goTo(final Label label) {
        this.mv.visitJumpInsn(167, label);
    }
    
    public void jsr(final Label label) {
        this.mv.visitJumpInsn(168, label);
    }
    
    public void ret(final int n) {
        this.mv.visitVarInsn(169, n);
    }
    
    public void tableswitch(final int n, final int n2, final Label label, final Label... array) {
        this.mv.visitTableSwitchInsn(n, n2, label, array);
    }
    
    public void lookupswitch(final Label label, final int[] array, final Label[] array2) {
        this.mv.visitLookupSwitchInsn(label, array, array2);
    }
    
    public void areturn(final Type type) {
        this.mv.visitInsn(type.getOpcode(172));
    }
    
    public void getstatic(final String s, final String s2, final String s3) {
        this.mv.visitFieldInsn(178, s, s2, s3);
    }
    
    public void putstatic(final String s, final String s2, final String s3) {
        this.mv.visitFieldInsn(179, s, s2, s3);
    }
    
    public void getfield(final String s, final String s2, final String s3) {
        this.mv.visitFieldInsn(180, s, s2, s3);
    }
    
    public void putfield(final String s, final String s2, final String s3) {
        this.mv.visitFieldInsn(181, s, s2, s3);
    }
    
    public void invokevirtual(final String s, final String s2, final String s3) {
        if (this.api >= 327680) {
            this.invokevirtual(s, s2, s3, false);
            return;
        }
        this.mv.visitMethodInsn(182, s, s2, s3);
    }
    
    public void invokevirtual(final String s, final String s2, final String s3, final boolean b) {
        if (this.api >= 327680) {
            this.mv.visitMethodInsn(182, s, s2, s3, b);
            return;
        }
        if (b) {
            throw new IllegalArgumentException("INVOKEVIRTUAL on interfaces require ASM 5");
        }
        this.invokevirtual(s, s2, s3);
    }
    
    public void invokespecial(final String s, final String s2, final String s3) {
        if (this.api >= 327680) {
            this.invokespecial(s, s2, s3, false);
            return;
        }
        this.mv.visitMethodInsn(183, s, s2, s3, false);
    }
    
    public void invokespecial(final String s, final String s2, final String s3, final boolean b) {
        if (this.api >= 327680) {
            this.mv.visitMethodInsn(183, s, s2, s3, b);
            return;
        }
        if (b) {
            throw new IllegalArgumentException("INVOKESPECIAL on interfaces require ASM 5");
        }
        this.invokespecial(s, s2, s3);
    }
    
    public void invokestatic(final String s, final String s2, final String s3) {
        if (this.api >= 327680) {
            this.invokestatic(s, s2, s3, false);
            return;
        }
        this.mv.visitMethodInsn(184, s, s2, s3, false);
    }
    
    public void invokestatic(final String s, final String s2, final String s3, final boolean b) {
        if (this.api >= 327680) {
            this.mv.visitMethodInsn(184, s, s2, s3, b);
            return;
        }
        if (b) {
            throw new IllegalArgumentException("INVOKESTATIC on interfaces require ASM 5");
        }
        this.invokestatic(s, s2, s3);
    }
    
    public void invokeinterface(final String s, final String s2, final String s3) {
        this.mv.visitMethodInsn(185, s, s2, s3, true);
    }
    
    public void invokedynamic(final String s, final String s2, final Handle handle, final Object[] array) {
        this.mv.visitInvokeDynamicInsn(s, s2, handle, array);
    }
    
    public void anew(final Type type) {
        this.mv.visitTypeInsn(187, type.getInternalName());
    }
    
    public void newarray(final Type type) {
        int n = 0;
        switch (type.getSort()) {
            case 1: {
                n = 4;
                break;
            }
            case 2: {
                n = 5;
                break;
            }
            case 3: {
                n = 8;
                break;
            }
            case 4: {
                n = 9;
                break;
            }
            case 5: {
                n = 10;
                break;
            }
            case 6: {
                n = 6;
                break;
            }
            case 7: {
                n = 11;
                break;
            }
            case 8: {
                n = 7;
                break;
            }
            default: {
                this.mv.visitTypeInsn(189, type.getInternalName());
                return;
            }
        }
        this.mv.visitIntInsn(188, n);
    }
    
    public void arraylength() {
        this.mv.visitInsn(190);
    }
    
    public void athrow() {
        this.mv.visitInsn(191);
    }
    
    public void checkcast(final Type type) {
        this.mv.visitTypeInsn(192, type.getInternalName());
    }
    
    public void instanceOf(final Type type) {
        this.mv.visitTypeInsn(193, type.getInternalName());
    }
    
    public void monitorenter() {
        this.mv.visitInsn(194);
    }
    
    public void monitorexit() {
        this.mv.visitInsn(195);
    }
    
    public void multianewarray(final String s, final int n) {
        this.mv.visitMultiANewArrayInsn(s, n);
    }
    
    public void ifnull(final Label label) {
        this.mv.visitJumpInsn(198, label);
    }
    
    public void ifnonnull(final Label label) {
        this.mv.visitJumpInsn(199, label);
    }
    
    public void mark(final Label label) {
        this.mv.visitLabel(label);
    }
    
    static {
        _clinit_();
        OBJECT_TYPE = Type.getType("Ljava/lang/Object;");
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    private static void _clinit_() {
        InstructionAdapter.class$org$objectweb$asm$commons$InstructionAdapter = class$("com.newrelic.agent.deps.org.objectweb.asm.commons.InstructionAdapter");
    }
}

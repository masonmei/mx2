// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import java.util.Arrays;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.Handle;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

public class GeneratorAdapter extends LocalVariablesSorter
{
    private static final String CLDESC = "Ljava/lang/Class;";
    private static final Type BYTE_TYPE;
    private static final Type BOOLEAN_TYPE;
    private static final Type SHORT_TYPE;
    private static final Type CHARACTER_TYPE;
    private static final Type INTEGER_TYPE;
    private static final Type FLOAT_TYPE;
    private static final Type LONG_TYPE;
    private static final Type DOUBLE_TYPE;
    private static final Type NUMBER_TYPE;
    private static final Type OBJECT_TYPE;
    private static final Method BOOLEAN_VALUE;
    private static final Method CHAR_VALUE;
    private static final Method INT_VALUE;
    private static final Method FLOAT_VALUE;
    private static final Method LONG_VALUE;
    private static final Method DOUBLE_VALUE;
    public static final int ADD = 96;
    public static final int SUB = 100;
    public static final int MUL = 104;
    public static final int DIV = 108;
    public static final int REM = 112;
    public static final int NEG = 116;
    public static final int SHL = 120;
    public static final int SHR = 122;
    public static final int USHR = 124;
    public static final int AND = 126;
    public static final int OR = 128;
    public static final int XOR = 130;
    public static final int EQ = 153;
    public static final int NE = 154;
    public static final int LT = 155;
    public static final int GE = 156;
    public static final int GT = 157;
    public static final int LE = 158;
    private final int access;
    private final Type returnType;
    private final Type[] argumentTypes;
    private final List localTypes;
    static /* synthetic */ Class class$org$objectweb$asm$commons$GeneratorAdapter;
    
    public GeneratorAdapter(final MethodVisitor methodVisitor, final int n, final String s, final String s2) {
        this(327680, methodVisitor, n, s, s2);
        if (this.getClass() != GeneratorAdapter.class$org$objectweb$asm$commons$GeneratorAdapter) {
            throw new IllegalStateException();
        }
    }
    
    protected GeneratorAdapter(final int n, final MethodVisitor methodVisitor, final int access, final String s, final String s2) {
        super(n, access, s2, methodVisitor);
        this.localTypes = new ArrayList();
        this.access = access;
        this.returnType = Type.getReturnType(s2);
        this.argumentTypes = Type.getArgumentTypes(s2);
    }
    
    public GeneratorAdapter(final int n, final Method method, final MethodVisitor methodVisitor) {
        this(methodVisitor, n, null, method.getDescriptor());
    }
    
    public GeneratorAdapter(final int n, final Method method, final String s, final Type[] array, final ClassVisitor classVisitor) {
        this(n, method, classVisitor.visitMethod(n, method.getName(), method.getDescriptor(), s, getInternalNames(array)));
    }
    
    private static String[] getInternalNames(final Type[] array) {
        if (array == null) {
            return null;
        }
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[i].getInternalName();
        }
        return array2;
    }
    
    public void push(final boolean b) {
        this.push(b ? 1 : 0);
    }
    
    public void push(final int n) {
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
    
    public void push(final long n) {
        if (n == 0L || n == 1L) {
            this.mv.visitInsn(9 + (int)n);
        }
        else {
            this.mv.visitLdcInsn(new Long(n));
        }
    }
    
    public void push(final float n) {
        final int floatToIntBits = Float.floatToIntBits(n);
        if (floatToIntBits == 0L || floatToIntBits == 1065353216 || floatToIntBits == 1073741824) {
            this.mv.visitInsn(11 + (int)n);
        }
        else {
            this.mv.visitLdcInsn(new Float(n));
        }
    }
    
    public void push(final double n) {
        final long doubleToLongBits = Double.doubleToLongBits(n);
        if (doubleToLongBits == 0L || doubleToLongBits == 4607182418800017408L) {
            this.mv.visitInsn(14 + (int)n);
        }
        else {
            this.mv.visitLdcInsn(new Double(n));
        }
    }
    
    public void push(final String s) {
        if (s == null) {
            this.mv.visitInsn(1);
        }
        else {
            this.mv.visitLdcInsn(s);
        }
    }
    
    public void push(final Type type) {
        if (type == null) {
            this.mv.visitInsn(1);
        }
        else {
            switch (type.getSort()) {
                case 1: {
                    this.mv.visitFieldInsn(178, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                case 2: {
                    this.mv.visitFieldInsn(178, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                case 3: {
                    this.mv.visitFieldInsn(178, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                case 4: {
                    this.mv.visitFieldInsn(178, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                case 5: {
                    this.mv.visitFieldInsn(178, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                case 6: {
                    this.mv.visitFieldInsn(178, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                case 7: {
                    this.mv.visitFieldInsn(178, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                case 8: {
                    this.mv.visitFieldInsn(178, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
                    break;
                }
                default: {
                    this.mv.visitLdcInsn(type);
                    break;
                }
            }
        }
    }
    
    public void push(final Handle handle) {
        this.mv.visitLdcInsn(handle);
    }
    
    private int getArgIndex(final int n) {
        int n2 = ((this.access & 0x8) == 0x0) ? 1 : 0;
        for (int i = 0; i < n; ++i) {
            n2 += this.argumentTypes[i].getSize();
        }
        return n2;
    }
    
    private void loadInsn(final Type type, final int n) {
        this.mv.visitVarInsn(type.getOpcode(21), n);
    }
    
    private void storeInsn(final Type type, final int n) {
        this.mv.visitVarInsn(type.getOpcode(54), n);
    }
    
    public void loadThis() {
        if ((this.access & 0x8) != 0x0) {
            throw new IllegalStateException("no 'this' pointer within static method");
        }
        this.mv.visitVarInsn(25, 0);
    }
    
    public void loadArg(final int n) {
        this.loadInsn(this.argumentTypes[n], this.getArgIndex(n));
    }
    
    public void loadArgs(final int n, final int n2) {
        int argIndex = this.getArgIndex(n);
        for (int i = 0; i < n2; ++i) {
            final Type type = this.argumentTypes[n + i];
            this.loadInsn(type, argIndex);
            argIndex += type.getSize();
        }
    }
    
    public void loadArgs() {
        this.loadArgs(0, this.argumentTypes.length);
    }
    
    public void loadArgArray() {
        this.push(this.argumentTypes.length);
        this.newArray(GeneratorAdapter.OBJECT_TYPE);
        for (int i = 0; i < this.argumentTypes.length; ++i) {
            this.dup();
            this.push(i);
            this.loadArg(i);
            this.box(this.argumentTypes[i]);
            this.arrayStore(GeneratorAdapter.OBJECT_TYPE);
        }
    }
    
    public void storeArg(final int n) {
        this.storeInsn(this.argumentTypes[n], this.getArgIndex(n));
    }
    
    public Type getLocalType(final int n) {
        return this.localTypes.get(n - this.firstLocal);
    }
    
    protected void setLocalType(final int n, final Type type) {
        final int n2 = n - this.firstLocal;
        while (this.localTypes.size() < n2 + 1) {
            this.localTypes.add(null);
        }
        this.localTypes.set(n2, type);
    }
    
    public void loadLocal(final int n) {
        this.loadInsn(this.getLocalType(n), n);
    }
    
    public void loadLocal(final int n, final Type type) {
        this.setLocalType(n, type);
        this.loadInsn(type, n);
    }
    
    public void storeLocal(final int n) {
        this.storeInsn(this.getLocalType(n), n);
    }
    
    public void storeLocal(final int n, final Type type) {
        this.setLocalType(n, type);
        this.storeInsn(type, n);
    }
    
    public void arrayLoad(final Type type) {
        this.mv.visitInsn(type.getOpcode(46));
    }
    
    public void arrayStore(final Type type) {
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
    
    public void swap(final Type type, final Type type2) {
        if (type2.getSize() == 1) {
            if (type.getSize() == 1) {
                this.swap();
            }
            else {
                this.dupX2();
                this.pop();
            }
        }
        else if (type.getSize() == 1) {
            this.dup2X1();
            this.pop2();
        }
        else {
            this.dup2X2();
            this.pop2();
        }
    }
    
    public void math(final int n, final Type type) {
        this.mv.visitInsn(type.getOpcode(n));
    }
    
    public void not() {
        this.mv.visitInsn(4);
        this.mv.visitInsn(130);
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
    
    private static Type getBoxedType(final Type type) {
        switch (type.getSort()) {
            case 3: {
                return GeneratorAdapter.BYTE_TYPE;
            }
            case 1: {
                return GeneratorAdapter.BOOLEAN_TYPE;
            }
            case 4: {
                return GeneratorAdapter.SHORT_TYPE;
            }
            case 2: {
                return GeneratorAdapter.CHARACTER_TYPE;
            }
            case 5: {
                return GeneratorAdapter.INTEGER_TYPE;
            }
            case 6: {
                return GeneratorAdapter.FLOAT_TYPE;
            }
            case 7: {
                return GeneratorAdapter.LONG_TYPE;
            }
            case 8: {
                return GeneratorAdapter.DOUBLE_TYPE;
            }
            default: {
                return type;
            }
        }
    }
    
    public void box(final Type type) {
        if (type.getSort() == 10 || type.getSort() == 9) {
            return;
        }
        if (type == Type.VOID_TYPE) {
            this.push((String)null);
        }
        else {
            final Type boxedType = getBoxedType(type);
            this.newInstance(boxedType);
            if (type.getSize() == 2) {
                this.dupX2();
                this.dupX2();
                this.pop();
            }
            else {
                this.dupX1();
                this.swap();
            }
            this.invokeConstructor(boxedType, new Method("<init>", Type.VOID_TYPE, new Type[] { type }));
        }
    }
    
    public void valueOf(final Type type) {
        if (type.getSort() == 10 || type.getSort() == 9) {
            return;
        }
        if (type == Type.VOID_TYPE) {
            this.push((String)null);
        }
        else {
            final Type boxedType;
            this.invokeStatic(boxedType, new Method("valueOf", boxedType = getBoxedType(type), new Type[] { type }));
        }
    }
    
    public void unbox(final Type type) {
        Type type2 = GeneratorAdapter.NUMBER_TYPE;
        Method method = null;
        switch (type.getSort()) {
            case 0: {
                return;
            }
            case 2: {
                type2 = GeneratorAdapter.CHARACTER_TYPE;
                method = GeneratorAdapter.CHAR_VALUE;
                break;
            }
            case 1: {
                type2 = GeneratorAdapter.BOOLEAN_TYPE;
                method = GeneratorAdapter.BOOLEAN_VALUE;
                break;
            }
            case 8: {
                method = GeneratorAdapter.DOUBLE_VALUE;
                break;
            }
            case 6: {
                method = GeneratorAdapter.FLOAT_VALUE;
                break;
            }
            case 7: {
                method = GeneratorAdapter.LONG_VALUE;
                break;
            }
            case 3:
            case 4:
            case 5: {
                method = GeneratorAdapter.INT_VALUE;
                break;
            }
        }
        if (method == null) {
            this.checkCast(type);
        }
        else {
            this.checkCast(type2);
            this.invokeVirtual(type2, method);
        }
    }
    
    public Label newLabel() {
        return new Label();
    }
    
    public void mark(final Label label) {
        this.mv.visitLabel(label);
    }
    
    public Label mark() {
        final Label label = new Label();
        this.mv.visitLabel(label);
        return label;
    }
    
    public void ifCmp(final Type type, final int n, final Label label) {
        switch (type.getSort()) {
            case 7: {
                this.mv.visitInsn(148);
                break;
            }
            case 8: {
                this.mv.visitInsn((n == 156 || n == 157) ? 151 : 152);
                break;
            }
            case 6: {
                this.mv.visitInsn((n == 156 || n == 157) ? 149 : 150);
                break;
            }
            case 9:
            case 10: {
                switch (n) {
                    case 153: {
                        this.mv.visitJumpInsn(165, label);
                        return;
                    }
                    case 154: {
                        this.mv.visitJumpInsn(166, label);
                        return;
                    }
                    default: {
                        throw new IllegalArgumentException("Bad comparison for type " + type);
                    }
                }
                break;
            }
            default: {
                int n2 = -1;
                switch (n) {
                    case 153: {
                        n2 = 159;
                        break;
                    }
                    case 154: {
                        n2 = 160;
                        break;
                    }
                    case 156: {
                        n2 = 162;
                        break;
                    }
                    case 155: {
                        n2 = 161;
                        break;
                    }
                    case 158: {
                        n2 = 164;
                        break;
                    }
                    case 157: {
                        n2 = 163;
                        break;
                    }
                }
                this.mv.visitJumpInsn(n2, label);
                return;
            }
        }
        this.mv.visitJumpInsn(n, label);
    }
    
    public void ifICmp(final int n, final Label label) {
        this.ifCmp(Type.INT_TYPE, n, label);
    }
    
    public void ifZCmp(final int n, final Label label) {
        this.mv.visitJumpInsn(n, label);
    }
    
    public void ifNull(final Label label) {
        this.mv.visitJumpInsn(198, label);
    }
    
    public void ifNonNull(final Label label) {
        this.mv.visitJumpInsn(199, label);
    }
    
    public void goTo(final Label label) {
        this.mv.visitJumpInsn(167, label);
    }
    
    public void ret(final int n) {
        this.mv.visitVarInsn(169, n);
    }
    
    public void tableSwitch(final int[] array, final TableSwitchGenerator tableSwitchGenerator) {
        float n;
        if (array.length == 0) {
            n = 0.0f;
        }
        else {
            n = array.length / (array[array.length - 1] - array[0] + 1);
        }
        this.tableSwitch(array, tableSwitchGenerator, n >= 0.5f);
    }
    
    public void tableSwitch(final int[] array, final TableSwitchGenerator tableSwitchGenerator, final boolean b) {
        for (int i = 1; i < array.length; ++i) {
            if (array[i] < array[i - 1]) {
                throw new IllegalArgumentException("keys must be sorted ascending");
            }
        }
        final Label label = this.newLabel();
        final Label label2 = this.newLabel();
        if (array.length > 0) {
            final int length = array.length;
            final int n = array[0];
            final int n2 = array[length - 1];
            final int n3 = n2 - n + 1;
            if (b) {
                final Label[] array2 = new Label[n3];
                Arrays.fill(array2, label);
                for (int j = 0; j < length; ++j) {
                    array2[array[j] - n] = this.newLabel();
                }
                this.mv.visitTableSwitchInsn(n, n2, label, array2);
                for (int k = 0; k < n3; ++k) {
                    final Label label3 = array2[k];
                    if (label3 != label) {
                        this.mark(label3);
                        tableSwitchGenerator.generateCase(k + n, label2);
                    }
                }
            }
            else {
                final Label[] array3 = new Label[length];
                for (int l = 0; l < length; ++l) {
                    array3[l] = this.newLabel();
                }
                this.mv.visitLookupSwitchInsn(label, array, array3);
                for (int n4 = 0; n4 < length; ++n4) {
                    this.mark(array3[n4]);
                    tableSwitchGenerator.generateCase(array[n4], label2);
                }
            }
        }
        this.mark(label);
        tableSwitchGenerator.generateDefault();
        this.mark(label2);
    }
    
    public void returnValue() {
        this.mv.visitInsn(this.returnType.getOpcode(172));
    }
    
    private void fieldInsn(final int n, final Type type, final String s, final Type type2) {
        this.mv.visitFieldInsn(n, type.getInternalName(), s, type2.getDescriptor());
    }
    
    public void getStatic(final Type type, final String s, final Type type2) {
        this.fieldInsn(178, type, s, type2);
    }
    
    public void putStatic(final Type type, final String s, final Type type2) {
        this.fieldInsn(179, type, s, type2);
    }
    
    public void getField(final Type type, final String s, final Type type2) {
        this.fieldInsn(180, type, s, type2);
    }
    
    public void putField(final Type type, final String s, final Type type2) {
        this.fieldInsn(181, type, s, type2);
    }
    
    private void invokeInsn(final int n, final Type type, final Method method, final boolean b) {
        this.mv.visitMethodInsn(n, (type.getSort() == 9) ? type.getDescriptor() : type.getInternalName(), method.getName(), method.getDescriptor(), b);
    }
    
    public void invokeVirtual(final Type type, final Method method) {
        this.invokeInsn(182, type, method, false);
    }
    
    public void invokeConstructor(final Type type, final Method method) {
        this.invokeInsn(183, type, method, false);
    }
    
    public void invokeStatic(final Type type, final Method method) {
        this.invokeInsn(184, type, method, false);
    }
    
    public void invokeInterface(final Type type, final Method method) {
        this.invokeInsn(185, type, method, true);
    }
    
    public void invokeDynamic(final String s, final String s2, final Handle handle, final Object... array) {
        this.mv.visitInvokeDynamicInsn(s, s2, handle, array);
    }
    
    private void typeInsn(final int n, final Type type) {
        this.mv.visitTypeInsn(n, type.getInternalName());
    }
    
    public void newInstance(final Type type) {
        this.typeInsn(187, type);
    }
    
    public void newArray(final Type type) {
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
                this.typeInsn(189, type);
                return;
            }
        }
        this.mv.visitIntInsn(188, n);
    }
    
    public void arrayLength() {
        this.mv.visitInsn(190);
    }
    
    public void throwException() {
        this.mv.visitInsn(191);
    }
    
    public void throwException(final Type type, final String s) {
        this.newInstance(type);
        this.dup();
        this.push(s);
        this.invokeConstructor(type, Method.getMethod("void <init> (String)"));
        this.throwException();
    }
    
    public void checkCast(final Type type) {
        if (!type.equals(GeneratorAdapter.OBJECT_TYPE)) {
            this.typeInsn(192, type);
        }
    }
    
    public void instanceOf(final Type type) {
        this.typeInsn(193, type);
    }
    
    public void monitorEnter() {
        this.mv.visitInsn(194);
    }
    
    public void monitorExit() {
        this.mv.visitInsn(195);
    }
    
    public void endMethod() {
        if ((this.access & 0x400) == 0x0) {
            this.mv.visitMaxs(0, 0);
        }
        this.mv.visitEnd();
    }
    
    public void catchException(final Label label, final Label label2, final Type type) {
        if (type == null) {
            this.mv.visitTryCatchBlock(label, label2, this.mark(), null);
        }
        else {
            this.mv.visitTryCatchBlock(label, label2, this.mark(), type.getInternalName());
        }
    }
    
    static {
        _clinit_();
        BYTE_TYPE = Type.getObjectType("java/lang/Byte");
        BOOLEAN_TYPE = Type.getObjectType("java/lang/Boolean");
        SHORT_TYPE = Type.getObjectType("java/lang/Short");
        CHARACTER_TYPE = Type.getObjectType("java/lang/Character");
        INTEGER_TYPE = Type.getObjectType("java/lang/Integer");
        FLOAT_TYPE = Type.getObjectType("java/lang/Float");
        LONG_TYPE = Type.getObjectType("java/lang/Long");
        DOUBLE_TYPE = Type.getObjectType("java/lang/Double");
        NUMBER_TYPE = Type.getObjectType("java/lang/Number");
        OBJECT_TYPE = Type.getObjectType("java/lang/Object");
        BOOLEAN_VALUE = Method.getMethod("boolean booleanValue()");
        CHAR_VALUE = Method.getMethod("char charValue()");
        INT_VALUE = Method.getMethod("int intValue()");
        FLOAT_VALUE = Method.getMethod("float floatValue()");
        LONG_VALUE = Method.getMethod("long longValue()");
        DOUBLE_VALUE = Method.getMethod("double doubleValue()");
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
        GeneratorAdapter.class$org$objectweb$asm$commons$GeneratorAdapter = class$("com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter");
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.InvocationHandler;
import com.newrelic.agent.bridge.AgentBridge;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

public class MethodBuilder
{
    static final String INVOCATION_HANDLER_FIELD_NAME = "__nr__InvocationHandlers";
    static final Type INVOCATION_HANDLER_ARRAY_TYPE;
    static final Type INVOCATION_HANDLER_TYPE;
    static final Method INVOCATION_HANDLER_INVOKE_METHOD;
    private final GeneratorAdapter mv;
    private final int access;
    public static final Object LOAD_THIS;
    public static final Object LOAD_ARG_ARRAY;
    private static final Map<Type, Type> primitiveToObjectType;
    
    public MethodBuilder(final GeneratorAdapter mv, final int access) {
        this.mv = mv;
        this.access = access;
    }
    
    public GeneratorAdapter getGeneratorAdapter() {
        return this.mv;
    }
    
    public MethodBuilder loadInvocationHandlerFromProxy() {
        this.mv.getStatic(Type.getType(AgentBridge.class), "agentHandler", MethodBuilder.INVOCATION_HANDLER_TYPE);
        return this;
    }
    
    public MethodBuilder invokeInvocationHandlerInterface(final boolean popTheReturnValue) {
        this.mv.invokeInterface(MethodBuilder.INVOCATION_HANDLER_TYPE, MethodBuilder.INVOCATION_HANDLER_INVOKE_METHOD);
        if (popTheReturnValue) {
            this.mv.pop();
        }
        return this;
    }
    
    public MethodBuilder loadInvocationHandlerProxyAndMethod(final Object value) {
        this.pushAndBox(value);
        this.mv.visitInsn(1);
        return this;
    }
    
    public MethodBuilder loadArray(final Class<?> arrayClass, final Object... objects) {
        if (objects == null || objects.length == 0) {
            this.mv.visitInsn(1);
            return this;
        }
        this.mv.push(objects.length);
        final Type objectType = Type.getType(arrayClass);
        this.mv.newArray(objectType);
        for (int i = 0; i < objects.length; ++i) {
            this.mv.dup();
            this.mv.push(i);
            if (MethodBuilder.LOAD_THIS == objects[i]) {
                if (this.isStatic()) {
                    this.mv.visitInsn(1);
                }
                else {
                    this.mv.loadThis();
                }
            }
            else if (MethodBuilder.LOAD_ARG_ARRAY == objects[i]) {
                this.mv.loadArgArray();
            }
            else if (objects[i] instanceof Runnable) {
                ((Runnable)objects[i]).run();
            }
            else {
                this.pushAndBox(objects[i]);
            }
            this.mv.arrayStore(objectType);
        }
        return this;
    }
    
    private boolean isStatic() {
        return (this.access & 0x8) != 0x0;
    }
    
    public MethodBuilder pushAndBox(final Object value) {
        if (value == null) {
            this.mv.visitInsn(1);
        }
        else if (value instanceof Boolean) {
            this.mv.push((boolean)value);
            this.mv.box(Type.BOOLEAN_TYPE);
        }
        else if (value instanceof Integer) {
            this.mv.visitIntInsn(17, (int)value);
            this.mv.box(Type.INT_TYPE);
        }
        else {
            this.mv.visitLdcInsn(value);
        }
        return this;
    }
    
    public MethodBuilder loadSuccessful() {
        this.loadInvocationHandlerProxyAndMethod("s");
        return this;
    }
    
    public MethodBuilder loadUnsuccessful() {
        this.loadInvocationHandlerProxyAndMethod("u");
        return this;
    }
    
    public Type box(final Type type) {
        if (type.getSort() == 10 || type.getSort() == 9) {
            return type;
        }
        final Type boxed = getBoxedType(type);
        this.mv.invokeStatic(boxed, new Method("valueOf", boxed, new Type[] { type }));
        return boxed;
    }
    
    public static Type getBoxedType(final Type type) {
        return MethodBuilder.primitiveToObjectType.get(type);
    }
    
    static {
        INVOCATION_HANDLER_ARRAY_TYPE = Type.getType(InvocationHandler[].class);
        INVOCATION_HANDLER_TYPE = Type.getType(InvocationHandler.class);
        INVOCATION_HANDLER_INVOKE_METHOD = new Method("invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
        LOAD_THIS = new Object();
        LOAD_ARG_ARRAY = new Object();
        primitiveToObjectType = Collections.unmodifiableMap((Map<? extends Type, ? extends Type>)new HashMap<Type, Type>() {
            private static final long serialVersionUID = 1L;
            
            {
                this.put(Type.BOOLEAN_TYPE, Type.getType(Boolean.class));
                this.put(Type.BYTE_TYPE, Type.getType(Byte.class));
                this.put(Type.CHAR_TYPE, Type.getType(Character.class));
                this.put(Type.DOUBLE_TYPE, Type.getType(Double.class));
                this.put(Type.FLOAT_TYPE, Type.getType(Float.class));
                this.put(Type.INT_TYPE, Type.getType(Integer.class));
                this.put(Type.LONG_TYPE, Type.getType(Long.class));
                this.put(Type.SHORT_TYPE, Type.getType(Short.class));
            }
        });
    }
}

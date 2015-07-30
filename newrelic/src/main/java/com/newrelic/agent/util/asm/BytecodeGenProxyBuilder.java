// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.bridge.Transaction;
import java.lang.reflect.Proxy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.util.Collections;
import com.newrelic.agent.util.AgentError;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;

public class BytecodeGenProxyBuilder<T>
{
    private final Class<T> target;
    private final GeneratorAdapter methodAdapter;
    private final boolean loadArguments;
    private Map<Object, Runnable> arguments;
    private final Variables variables;
    private Map<Type, VariableLoader> loaders;
    
    private BytecodeGenProxyBuilder(final Class<T> target, final GeneratorAdapter methodAdapter, final boolean loadArguments) {
        this.target = target;
        this.methodAdapter = methodAdapter;
        this.variables = new VariableLoaderImpl();
        this.loadArguments = loadArguments;
    }
    
    public static <T> BytecodeGenProxyBuilder<T> newBuilder(final Class<T> target, final GeneratorAdapter methodAdapter, final boolean loadArguments) {
        return new BytecodeGenProxyBuilder<T>(target, methodAdapter, loadArguments);
    }
    
    public Variables getVariables() {
        return this.variables;
    }
    
    private BytecodeGenProxyBuilder<T> addArgument(final Object instance, final Runnable runnable) {
        if (this.arguments == null) {
            this.arguments = (Map<Object, Runnable>)Maps.newHashMap();
        }
        if (runnable == null) {
            throw new AgentError("Runnable was null");
        }
        this.arguments.put(instance, runnable);
        return this;
    }
    
    public BytecodeGenProxyBuilder<T> addLoader(final Type t, final VariableLoader loader) {
        if (this.loaders == null) {
            this.loaders = (Map<Type, VariableLoader>)Maps.newHashMap();
        }
        this.loaders.put(t, loader);
        return this;
    }
    
    private Map<Type, VariableLoader> getLoaders() {
        return (this.loaders == null) ? Collections.emptyMap() : this.loaders;
    }
    
    private Map<Object, Runnable> getArguments() {
        return (this.arguments == null) ? Collections.emptyMap() : this.arguments;
    }
    
    public T build() {
        final InvocationHandler handler = new InvocationHandler() {
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                final com.newrelic.agent.deps.org.objectweb.asm.commons.Method m = com.newrelic.agent.deps.org.objectweb.asm.commons.Method.getMethod(method);
                if (BytecodeGenProxyBuilder.this.loadArguments) {
                    for (int i = 0; i < m.getArgumentTypes().length; ++i) {
                        final Object value = args[i];
                        final Type type = m.getArgumentTypes()[i];
                        this.load(type, value);
                    }
                }
                try {
                    this.getMethodVisitor().visitMethodInsn(185, Type.getInternalName(BytecodeGenProxyBuilder.this.target), m.getName(), m.getDescriptor(), true);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    Agent.LOG.log(Level.FINER, "Error invoking {0}.{1}", new Object[] { BytecodeGenProxyBuilder.this.target.getName(), m });
                    throw e;
                }
                return this.dummyReturnValue(m.getReturnType());
            }
            
            private Object dummyReturnValue(final Type returnType) {
                switch (returnType.getSort()) {
                    case 2:
                    case 3:
                    case 4:
                    case 5: {
                        return 0;
                    }
                    case 7: {
                        return 0L;
                    }
                    case 6: {
                        return 0.0f;
                    }
                    case 8: {
                        return 0.0;
                    }
                    case 1: {
                        return false;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            private MethodVisitor getMethodVisitor() {
                if (BytecodeGenProxyBuilder.this.methodAdapter instanceof AdviceAdapter) {
                    try {
                        Field field = AdviceAdapter.class.getDeclaredField("constructor");
                        field.setAccessible(true);
                        if (field.getBoolean(BytecodeGenProxyBuilder.this.methodAdapter)) {
                            field = MethodVisitor.class.getDeclaredField("mv");
                            field.setAccessible(true);
                            return (MethodVisitor)field.get(BytecodeGenProxyBuilder.this.methodAdapter);
                        }
                    }
                    catch (Exception e) {
                        Agent.LOG.log(Level.FINE, (Throwable)e, e.toString(), new Object[0]);
                    }
                }
                return BytecodeGenProxyBuilder.this.methodAdapter;
            }
            
            private void load(final Type type, final Object value) {
                if (value == null) {
                    BytecodeGenProxyBuilder.this.methodAdapter.visitInsn(1);
                    return;
                }
                final VariableLoader loader = BytecodeGenProxyBuilder.this.getLoaders().get(type);
                final Runnable handler = BytecodeGenProxyBuilder.this.getArguments().get(value);
                if (handler != null) {
                    handler.run();
                }
                else if (loader != null) {
                    loader.load(value, BytecodeGenProxyBuilder.this.methodAdapter);
                }
                else if (value instanceof LoaderMarker) {
                    ((LoaderMarker)value).run();
                }
                else {
                    switch (type.getSort()) {
                        case 10: {
                            if (value instanceof String) {
                                BytecodeGenProxyBuilder.this.methodAdapter.push((String)value);
                            }
                            else if (value.getClass().isEnum()) {
                                final Enum theEnum = (Enum)value;
                                BytecodeGenProxyBuilder.this.methodAdapter.getStatic(type, theEnum.name(), type);
                            }
                            else {
                                if (!(value instanceof Runnable)) {
                                    throw new AgentError("Unsupported type " + type);
                                }
                                ((Runnable)value).run();
                            }
                        }
                        case 1: {
                            BytecodeGenProxyBuilder.this.methodAdapter.push((boolean)value);
                        }
                        case 5: {
                            BytecodeGenProxyBuilder.this.methodAdapter.push(((Number)value).intValue());
                        }
                        case 7: {
                            BytecodeGenProxyBuilder.this.methodAdapter.push(((Number)value).longValue());
                        }
                        case 6: {
                            BytecodeGenProxyBuilder.this.methodAdapter.push(((Number)value).floatValue());
                        }
                        case 8: {
                            BytecodeGenProxyBuilder.this.methodAdapter.push(((Number)value).doubleValue());
                        }
                        case 3: {
                            BytecodeGenProxyBuilder.this.methodAdapter.push(((Number)value).intValue());
                        }
                        case 9: {
                            final int count = Array.getLength(value);
                            BytecodeGenProxyBuilder.this.methodAdapter.push(count);
                            BytecodeGenProxyBuilder.this.methodAdapter.newArray(type.getElementType());
                            for (int i = 0; i < count; ++i) {
                                BytecodeGenProxyBuilder.this.methodAdapter.dup();
                                BytecodeGenProxyBuilder.this.methodAdapter.push(i);
                                this.load(type.getElementType(), Array.get(value, i));
                                BytecodeGenProxyBuilder.this.methodAdapter.arrayStore(type.getElementType());
                            }
                        }
                        default: {
                            throw new AgentError("Unsupported type " + type);
                        }
                    }
                }
            }
        };
        final ClassLoader classLoader = BytecodeGenProxyBuilder.class.getClassLoader();
        return (T)Proxy.newProxyInstance(classLoader, new Class[] { this.target }, handler);
    }
    
    public final class VariableLoaderImpl implements Variables
    {
        private Runnable loadThis() {
            return new LoaderMarker() {
                public void run() {
                    BytecodeGenProxyBuilder.this.methodAdapter.visitVarInsn(25, 0);
                }
                
                public String toString() {
                    return "this";
                }
            };
        }
        
        public Object loadThis(final int access) {
            final boolean isStatic = (access & 0x8) == 0x8;
            return isStatic ? null : this.loadThis();
        }
        
        public <N extends Number> N loadLocal(final int local, final Type type, final N value) {
            final Runnable r = new Runnable() {
                public void run() {
                    BytecodeGenProxyBuilder.this.methodAdapter.loadLocal(local, type);
                }
            };
            return this.load(value, r);
        }
        
        public <N extends Number> N load(final N value, final Runnable runnable) {
            BytecodeGenProxyBuilder.this.addArgument(value, runnable);
            return value;
        }
        
        public Transaction loadCurrentTransaction() {
            return this.load(Transaction.class, (Runnable)new Runnable() {
                public void run() {
                    BridgeUtils.getCurrentTransaction(BytecodeGenProxyBuilder.this.methodAdapter);
                }
                
                public String toString() {
                    return Transaction.class.getName() + '.' + "CURRENT";
                }
            });
        }
        
        public <O> O load(final Class<O> clazz, final Runnable runnable) {
            if (clazz.isInterface()) {
                final InvocationHandler handler = new Handler() {
                    public Object doInvoke(final Object proxy, final Method method, final Object[] args) {
                        runnable.run();
                        return null;
                    }
                    
                    public String toString() {
                        return runnable.toString();
                    }
                };
                return (O)Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { clazz, LoaderMarker.class }, handler);
            }
            if (clazz.isArray()) {
                final O key = (O)Array.newInstance(clazz.getComponentType(), 0);
                BytecodeGenProxyBuilder.this.addArgument(key, runnable);
                return key;
            }
            if (String.class.equals(clazz)) {
                final O key = (O)Long.toString(System.identityHashCode(runnable));
                BytecodeGenProxyBuilder.this.addArgument(key, runnable);
                return key;
            }
            throw new AgentError("Unsupported type " + clazz.getName());
        }
        
        public <O> O loadLocal(final int localId, final Class<O> clazz) {
            return this.load(clazz, (Runnable)new Runnable() {
                public void run() {
                    BytecodeGenProxyBuilder.this.methodAdapter.loadLocal(localId, Type.getType(clazz));
                }
            });
        }
    }
    
    private abstract static class Handler implements InvocationHandler
    {
        public final Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (method.getName().equals("hashCode")) {
                return System.identityHashCode(proxy);
            }
            if (method.getName().equals("toString")) {
                return this.toString();
            }
            return this.doInvoke(proxy, method, args);
        }
        
        protected abstract Object doInvoke(final Object p0, final Method p1, final Object[] p2);
    }
    
    public interface LoaderMarker extends Runnable
    {
    }
}

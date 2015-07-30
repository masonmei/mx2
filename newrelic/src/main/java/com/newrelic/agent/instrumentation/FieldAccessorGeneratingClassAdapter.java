// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class FieldAccessorGeneratingClassAdapter extends ClassVisitor
{
    private final String className;
    private final Map<String, String> allFields;
    private final Method[] methods;
    private final boolean hasFieldAccessors;
    
    public FieldAccessorGeneratingClassAdapter(final ClassVisitor cv, final String className, final Class<?> extensionClass) {
        super(327680, cv);
        this.allFields = new HashMap<String, String>();
        this.className = className;
        this.methods = extensionClass.getMethods();
        this.hasFieldAccessors = this.hasFieldAccessors();
    }
    
    private boolean hasFieldAccessors() {
        for (final Method method : this.methods) {
            if (method.getAnnotation(FieldAccessor.class) != null) {
                return true;
            }
        }
        return false;
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (this.hasFieldAccessors) {
            this.allFields.put(name, desc);
        }
        return super.visitField(access, name, desc, signature, value);
    }
    
    public void visitEnd() {
        if (this.hasFieldAccessors) {
            this.addFieldAccessors();
        }
        super.visitEnd();
    }
    
    private void addFieldAccessors() {
        final Map<String, Object[]> fields = new HashMap<String, Object[]>();
        for (final Method method : this.methods) {
            final FieldAccessor fieldAccessor = method.getAnnotation(FieldAccessor.class);
            if (fieldAccessor != null) {
                Class<?> returnType = method.getReturnType();
                if (Void.TYPE.equals(returnType)) {
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1) {
                        returnType = parameterTypes[0];
                    }
                }
                final String fieldName = (fieldAccessor.existingField() ? "" : "__nr__") + fieldAccessor.fieldName();
                Object[] fieldDesc = fields.get(fieldName);
                final Class type = (fieldDesc == null) ? null : ((Class)fieldDesc[0]);
                if (type != null && !returnType.equals(type)) {
                    throw new StopProcessingException("Method " + method.getName() + " uses type " + type.getName() + ", but " + returnType.getName() + " was expected.");
                }
                if (!fieldAccessor.existingField()) {
                    fieldDesc = new Object[] { returnType, fieldAccessor.volatileAccess() ? 194 : 130 };
                    fields.put(fieldName, fieldDesc);
                }
                else if (!this.allFields.containsKey(fieldName)) {
                    throw new StopProcessingException(this.className + " does not contain a field named " + fieldName);
                }
                Type fieldType;
                final Type returnType2 = fieldType = Type.getType(returnType);
                if (fieldAccessor.fieldDesc().length() > 0) {
                    fieldType = Type.getType(fieldAccessor.fieldDesc());
                }
                this.writeMethod(fieldName, returnType2, fieldType, method);
            }
        }
        for (final Map.Entry<String, Object[]> field : fields.entrySet()) {
            final String fieldName2 = field.getKey();
            final Object[] fieldDesc2 = field.getValue();
            final Type fieldType2 = Type.getType((Class)fieldDesc2[0]);
            this.cv.visitField((int)fieldDesc2[1], fieldName2, fieldType2.getDescriptor(), null, null);
        }
    }
    
    private void writeMethod(final String fieldName, final Type returnType, final Type fieldType, final Method method) {
        final boolean setter = Void.TYPE.equals(method.getReturnType());
        final com.newrelic.agent.deps.org.objectweb.asm.commons.Method newMethod = InstrumentationUtils.getMethod(method);
        final GeneratorAdapter mv = new GeneratorAdapter(1, newMethod, null, null, this);
        mv.visitCode();
        mv.loadThis();
        if (setter) {
            mv.loadArgs();
        }
        int op = setter ? 181 : 180;
        mv.visitFieldInsn(op, this.className, fieldName, fieldType.getDescriptor());
        op = (setter ? 177 : returnType.getOpcode(172));
        mv.visitInsn(op);
        mv.visitMaxs(0, 0);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;

public class WeaveUtils
{
    public static final String NEW_FIELD_ANNOTATION_DESCRIPTOR;
    static final Method CALL_ORIGINAL_METHOD;
    
    public static boolean isWeavedClass(final ClassReader reader) {
        final boolean[] weaved = { false };
        reader.accept(new ClassVisitor(327680) {
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (Type.getDescriptor(Weave.class).equals(desc)) {
                    weaved[0] = true;
                }
                return null;
            }
        }, 1);
        return weaved[0];
    }
    
    static {
        NEW_FIELD_ANNOTATION_DESCRIPTOR = Type.getDescriptor(NewField.class);
        CALL_ORIGINAL_METHOD = new Method("callOriginal", Type.getType(Object.class), new Type[0]);
    }
}

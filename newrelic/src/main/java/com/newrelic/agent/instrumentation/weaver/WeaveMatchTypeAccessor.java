// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.api.agent.weaver.MatchType;

public class WeaveMatchTypeAccessor
{
    private MatchType matchType;
    
    public MatchType getMatchType() {
        return this.matchType;
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible, final AnnotationVisitor va) {
        if (desc.equals(Type.getType(Weave.class).getDescriptor())) {
            this.matchType = MatchType.ExactClass;
            return new AnnotationVisitor(327680, va) {
                public void visitEnum(final String name, final String desc, final String value) {
                    WeaveMatchTypeAccessor.this.matchType = MatchType.valueOf(value);
                    super.visitEnum(name, desc, value);
                }
            };
        }
        return va;
    }
}

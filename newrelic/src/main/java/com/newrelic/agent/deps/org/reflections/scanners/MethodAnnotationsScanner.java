// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.scanners;

import java.util.Iterator;

public class MethodAnnotationsScanner extends AbstractScanner
{
    public void scan(final Object cls) {
        for (final Object method : this.getMetadataAdapter().getMethods(cls)) {
            for (final String methodAnnotation : this.getMetadataAdapter().getMethodAnnotationNames(method)) {
                if (this.acceptResult(methodAnnotation)) {
                    this.getStore().put(methodAnnotation, this.getMetadataAdapter().getMethodFullKey(cls, method));
                }
            }
        }
    }
}

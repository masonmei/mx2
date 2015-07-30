// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.scanners;

import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.base.Joiner;

public class TypeElementsScanner extends AbstractScanner
{
    private boolean includeFields;
    private boolean includeMethods;
    private boolean includeAnnotations;
    private boolean publicOnly;
    
    public TypeElementsScanner() {
        this.includeFields = true;
        this.includeMethods = true;
        this.includeAnnotations = true;
        this.publicOnly = true;
    }
    
    public void scan(final Object cls) {
        final String className = this.getMetadataAdapter().getClassName(cls);
        if (!this.acceptResult(className)) {
            return;
        }
        this.getStore().put(className, "");
        if (this.includeFields) {
            for (final Object field : this.getMetadataAdapter().getFields(cls)) {
                final String fieldName = this.getMetadataAdapter().getFieldName(field);
                this.getStore().put(className, fieldName);
            }
        }
        if (this.includeMethods) {
            for (final Object method : this.getMetadataAdapter().getMethods(cls)) {
                if (!this.publicOnly || this.getMetadataAdapter().isPublic(method)) {
                    final String methodKey = this.getMetadataAdapter().getMethodName(method) + "(" + Joiner.on(", ").join(this.getMetadataAdapter().getParameterNames(method)) + ")";
                    this.getStore().put(className, methodKey);
                }
            }
        }
        if (this.includeAnnotations) {
            for (final Object annotation : this.getMetadataAdapter().getClassAnnotationNames(cls)) {
                this.getStore().put(className, "@" + annotation);
            }
        }
    }
    
    public TypeElementsScanner includeFields() {
        return this.includeFields(true);
    }
    
    public TypeElementsScanner includeFields(final boolean include) {
        this.includeFields = include;
        return this;
    }
    
    public TypeElementsScanner includeMethods() {
        return this.includeMethods(true);
    }
    
    public TypeElementsScanner includeMethods(final boolean include) {
        this.includeMethods = include;
        return this;
    }
    
    public TypeElementsScanner includeAnnotations() {
        return this.includeAnnotations(true);
    }
    
    public TypeElementsScanner includeAnnotations(final boolean include) {
        this.includeAnnotations = include;
        return this;
    }
    
    public TypeElementsScanner publicOnly(final boolean only) {
        this.publicOnly = only;
        return this;
    }
    
    public TypeElementsScanner publicOnly() {
        return this.publicOnly(true);
    }
}

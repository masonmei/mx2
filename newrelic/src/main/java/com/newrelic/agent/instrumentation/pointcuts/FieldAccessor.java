// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@LoadOnBootstrap
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAccessor {
    String fieldName();
    
    String fieldDesc() default "";
    
    boolean existingField() default false;
    
    boolean volatileAccess() default false;
}

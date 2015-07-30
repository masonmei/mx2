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
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfaceMapper {
    String originalInterfaceName();
    
    String[] className() default {};
    
    String[] skip() default {};
    
    Class<?> classVisitor() default Object.class;
}
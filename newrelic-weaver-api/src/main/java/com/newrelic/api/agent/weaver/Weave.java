// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.api.agent.weaver;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Weave {
    MatchType type() default MatchType.ExactClass;
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

public class IncompatibleClassException extends Exception
{
    private static final long serialVersionUID = -5823372159561159549L;
    Class requestedClass;
    Class obtainedClass;
    
    IncompatibleClassException(final Class requestedClass, final Class obtainedClass) {
        this.requestedClass = requestedClass;
        this.obtainedClass = obtainedClass;
    }
}

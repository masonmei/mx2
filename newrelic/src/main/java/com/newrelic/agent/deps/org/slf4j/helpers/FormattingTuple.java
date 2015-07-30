// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.helpers;

public class FormattingTuple
{
    public static FormattingTuple NULL;
    private String message;
    private Throwable throwable;
    private Object[] argArray;
    
    public FormattingTuple(final String message) {
        this(message, null, null);
    }
    
    public FormattingTuple(final String message, final Object[] argArray, final Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        if (throwable == null) {
            this.argArray = argArray;
        }
        else {
            this.argArray = trimmedCopy(argArray);
        }
    }
    
    static Object[] trimmedCopy(final Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
        final int trimemdLen = argArray.length - 1;
        final Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Object[] getArgArray() {
        return this.argArray;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    static {
        FormattingTuple.NULL = new FormattingTuple(null);
    }
}

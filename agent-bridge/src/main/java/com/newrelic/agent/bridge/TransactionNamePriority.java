// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

public enum TransactionNamePriority
{
    NONE, 
    REQUEST_URI, 
    STATUS_CODE, 
    FILTER_NAME, 
    FILTER_INIT_PARAM, 
    SERVLET_NAME, 
    SERVLET_INIT_PARAM, 
    JSP, 
    FRAMEWORK_LOW, 
    FRAMEWORK, 
    FRAMEWORK_HIGH, 
    CUSTOM_LOW, 
    CUSTOM_HIGH, 
    FROZEN;
    
    public static TransactionNamePriority convert(final com.newrelic.api.agent.TransactionNamePriority priority) {
        switch (priority) {
            case CUSTOM_HIGH: {
                return TransactionNamePriority.CUSTOM_HIGH;
            }
            case CUSTOM_LOW: {
                return TransactionNamePriority.CUSTOM_LOW;
            }
            case FRAMEWORK_HIGH: {
                return TransactionNamePriority.FRAMEWORK_HIGH;
            }
            case FRAMEWORK_LOW: {
                return TransactionNamePriority.FRAMEWORK_LOW;
            }
            case REQUEST_URI: {
                return TransactionNamePriority.REQUEST_URI;
            }
            default: {
                throw new IllegalArgumentException("Unmapped TransactionNamePriority " + priority);
            }
        }
    }
    
    public boolean isGreaterThan(final TransactionNamePriority other) {
        return this.compareTo(other) > 0;
    }
    
    public boolean isLessThan(final TransactionNamePriority other) {
        return this.compareTo(other) < 0;
    }
}

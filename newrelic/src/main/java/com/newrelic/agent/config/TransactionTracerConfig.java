// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

public interface TransactionTracerConfig
{
    boolean isEnabled();
    
    String getRecordSql();
    
    boolean isLogSql();
    
    int getInsertSqlMaxLength();
    
    long getTransactionThresholdInMillis();
    
    long getTransactionThresholdInNanos();
    
    double getStackTraceThresholdInMillis();
    
    double getStackTraceThresholdInNanos();
    
    double getExplainThresholdInMillis();
    
    double getExplainThresholdInNanos();
    
    boolean isExplainEnabled();
    
    int getMaxExplainPlans();
    
    boolean isGCTimeEnabled();
    
    int getMaxStackTraces();
    
    int getMaxSegments();
    
    int getTopN();
    
    boolean isStackBasedNamingEnabled();
}

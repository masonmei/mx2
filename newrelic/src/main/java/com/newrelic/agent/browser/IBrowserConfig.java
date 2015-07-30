// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.browser;

public interface IBrowserConfig
{
    String getBrowserTimingHeader();
    
    String getBrowserTimingFooter(BrowserTransactionState p0);
}

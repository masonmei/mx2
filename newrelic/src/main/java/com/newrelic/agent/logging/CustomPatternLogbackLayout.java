// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.deps.ch.qos.logback.classic.PatternLayout;

class CustomPatternLogbackLayout extends PatternLayout
{
    private static final String THREAD_ID_CHAR = "i";
    private static final String MARKER_LEVEL_ID = "ml";
    private static final String PROCESS_ID = "pid";
    
    public CustomPatternLogbackLayout(final String pPattern) {
        CustomPatternLogbackLayout.defaultConverterMap.put("i", ThreadIdLogbackConverter.class.getName());
        CustomPatternLogbackLayout.defaultConverterMap.put("ml", MarkerLevelConverter.class.getName());
        CustomPatternLogbackLayout.defaultConverterMap.put("pid", ProcessIdLogbackConverter.class.getName());
        this.setPattern(pPattern);
    }
}

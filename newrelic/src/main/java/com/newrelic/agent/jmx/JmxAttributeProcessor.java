// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import java.util.Map;
import javax.management.Attribute;
import javax.management.ObjectInstance;
import com.newrelic.agent.stats.StatsEngine;

public interface JmxAttributeProcessor
{
    boolean process(StatsEngine p0, ObjectInstance p1, Attribute p2, String p3, Map<String, Float> p4);
}

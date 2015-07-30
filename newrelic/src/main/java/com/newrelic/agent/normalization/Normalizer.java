// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import java.util.List;

public interface Normalizer
{
    String normalize(String p0);
    
    List<NormalizationRule> getRules();
}

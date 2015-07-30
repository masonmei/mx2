// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import com.newrelic.agent.service.Service;

public interface NormalizationService extends Service
{
    Normalizer getMetricNormalizer(String p0);
    
    Normalizer getTransactionNormalizer(String p0);
    
    Normalizer getUrlNormalizer(String p0);
    
    String getUrlBeforeParameters(String p0);
}

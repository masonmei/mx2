// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class KeyTransactionConfigImpl implements KeyTransactionConfig
{
    private final Map<String, Long> apdexTs;
    private final long apdexTInMillis;
    
    private KeyTransactionConfigImpl(final Map<String, Object> props, final long apdexTInMillis) {
        this.apdexTInMillis = apdexTInMillis;
        final Map<String, Long> apdexTs = new HashMap<String, Long>();
        for (final Map.Entry<String, Object> entry : props.entrySet()) {
            final Object apdexT = entry.getValue();
            if (apdexT instanceof Number) {
                final Long apdexTinMillis = (long)(((Number)apdexT).doubleValue() * 1000.0);
                final String txName = entry.getKey();
                apdexTs.put(txName, apdexTinMillis);
            }
        }
        this.apdexTs = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)apdexTs);
    }
    
    public boolean isApdexTSet(final String transactionName) {
        return this.apdexTs.containsKey(transactionName);
    }
    
    public long getApdexTInMillis(final String transactionName) {
        final Long apdexT = this.apdexTs.get(transactionName);
        if (apdexT == null) {
            return this.apdexTInMillis;
        }
        return apdexT;
    }
    
    static KeyTransactionConfig createKeyTransactionConfig(Map<String, Object> settings, final long apdexTInMillis) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new KeyTransactionConfigImpl(settings, apdexTInMillis);
    }
}

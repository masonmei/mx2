// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import com.newrelic.agent.jmx.JmxType;

public enum EjbTransactionJmxMetricGenerator
{
    COUNT {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName, "Count", JmxAction.SUM_ALL, JmxType.MONOTONICALLY_INCREASING);
        }
    }, 
    COMMIT {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Committed", JmxType.MONOTONICALLY_INCREASING);
        }
    }, 
    ROLLBACK {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Rolled Back", JmxType.MONOTONICALLY_INCREASING);
        }
    }, 
    TIMEOUT {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Timed Out", JmxType.MONOTONICALLY_INCREASING);
        }
    };
    
    public abstract JmxMetric createMetric(final String... p0);
}

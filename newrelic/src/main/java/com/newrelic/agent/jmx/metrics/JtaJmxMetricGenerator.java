// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import com.newrelic.agent.jmx.JmxType;

public enum JtaJmxMetricGenerator
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
    }, 
    ABANDONDED {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Abandoned", JmxType.MONOTONICALLY_INCREASING);
        }
    };
    
    public abstract JmxMetric createMetric(final String... p0);
}

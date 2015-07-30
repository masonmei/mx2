// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import com.newrelic.agent.jmx.JmxType;

public enum KafkaMetricGenerator
{
    COUNT_MONOTONIC {
        public JmxMetric createMetric(final String pAttributeName) {
            return JmxMetric.create(pAttributeName, "", JmxType.MONOTONICALLY_INCREASING);
        }
    }, 
    VALUE_SIMPLE {
        public JmxMetric createMetric(final String pAttributeName) {
            return JmxMetric.create(pAttributeName, "", JmxType.SIMPLE);
        }
    }, 
    QUEUE_SIZE {
        public JmxMetric createMetric(final String pAttributeName) {
            return JmxMetric.create(pAttributeName, "", JmxType.SIMPLE);
        }
    }, 
    REQ_MEAN {
        public JmxMetric createMetric(final String pAttributeName) {
            return JmxMetric.create(pAttributeName, "", JmxType.SIMPLE);
        }
    };
    
    public abstract JmxMetric createMetric(final String p0);
}

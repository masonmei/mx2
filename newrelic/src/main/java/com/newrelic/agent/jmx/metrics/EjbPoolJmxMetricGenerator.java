// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import com.newrelic.agent.jmx.JmxType;

public enum EjbPoolJmxMetricGenerator
{
    SUCCESS {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName, "Attempts/Successful", JmxAction.SUBTRACT_ALL_FROM_FIRST, JmxType.MONOTONICALLY_INCREASING);
        }
    }, 
    THREADS_WAITING {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Threads/Waiting", JmxType.SIMPLE);
        }
    }, 
    DESTROY {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Beans/Destroyed", JmxType.MONOTONICALLY_INCREASING);
        }
    }, 
    FAILURE {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Attempts/Failed", JmxType.MONOTONICALLY_INCREASING);
        }
    }, 
    AVAILABLE {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Beans/Available", JmxType.SIMPLE);
        }
    }, 
    ACTIVE {
        public JmxMetric createMetric(final String... pAttributeName) {
            return JmxMetric.create(pAttributeName[0], "Beans/Active", JmxType.SIMPLE);
        }
    };
    
    public abstract JmxMetric createMetric(final String... p0);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import java.text.MessageFormat;
import java.util.Map;

public enum JmxAction
{
    USE_FIRST_ATT {
        public float performAction(final String[] pAttributes, final Map<String, Float> pValues) throws IllegalArgumentException {
            if (pAttributes == null || pAttributes.length == 0) {
                return 0.0f;
            }
            return getValue(pValues, pAttributes[0]);
        }
    }, 
    USE_FIRST_RECORDED_ATT {
        public float performAction(final String[] pAttributes, final Map<String, Float> pValues) throws IllegalArgumentException {
            if (pAttributes == null || pAttributes.length == 0) {
                return 0.0f;
            }
            Float value = null;
            for (final String current : pAttributes) {
                value = getValueNullOkay(pValues, current);
                if (value != null) {
                    return value;
                }
            }
            return 0.0f;
        }
    }, 
    SUBTRACT_ALL_FROM_FIRST {
        public float performAction(final String[] pAttributes, final Map<String, Float> values) throws IllegalArgumentException {
            float output;
            if (pAttributes == null) {
                output = 0.0f;
            }
            else {
                final int length = pAttributes.length;
                if (length == 0) {
                    output = 0.0f;
                }
                else {
                    output = getValue(values, pAttributes[0]);
                    if (length > 1) {
                        for (int i = 1; i < length; ++i) {
                            output -= getValue(values, pAttributes[i]);
                        }
                    }
                    if (output < 0.0f) {
                        throw new IllegalArgumentException(MessageFormat.format("The output value can not be negative: {0} ", output));
                    }
                }
            }
            return output;
        }
    }, 
    SUM_ALL {
        public float performAction(final String[] pAttributes, final Map<String, Float> values) throws IllegalArgumentException {
            float output;
            if (pAttributes == null) {
                output = 0.0f;
            }
            else {
                final int length = pAttributes.length;
                if (length == 0) {
                    output = 0.0f;
                }
                else {
                    output = getValue(values, pAttributes[0]);
                    if (length > 1) {
                        for (int i = 1; i < length; ++i) {
                            output += getValue(values, pAttributes[i]);
                        }
                    }
                    if (output < 0.0f) {
                        throw new IllegalArgumentException(MessageFormat.format("The output value can not be negative: {0} ", output));
                    }
                }
            }
            return output;
        }
    };
    
    public abstract float performAction(final String[] p0, final Map<String, Float> p1) throws IllegalArgumentException;
    
    private static float getValue(final Map<String, Float> values, final String att) {
        final Float value = values.get(att);
        if (value == null) {
            throw new IllegalArgumentException(MessageFormat.format("There is no value for attribute {0}", att));
        }
        return value;
    }
    
    private static Float getValueNullOkay(final Map<String, Float> values, final String att) {
        final Float value = values.get(att);
        if (value == null) {
            return null;
        }
        return value;
    }
}

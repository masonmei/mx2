// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import com.newrelic.agent.deps.org.json.simple.parser.ParseException;
import com.newrelic.agent.deps.org.json.simple.parser.JSONParser;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

public class BaseConfig implements Config
{
    public static final String COMMA_SEPARATOR = ",";
    public static final String SEMI_COLON_SEPARATOR = ";";
    private final Map<String, Object> props;
    protected final String systemPropertyPrefix;
    
    public BaseConfig(final Map<String, Object> props) {
        this(props, null);
    }
    
    public BaseConfig(final Map<String, Object> props, final String systemPropertyPrefix) {
        if (systemPropertyPrefix != null && systemPropertyPrefix.length() == 0) {
            throw new IllegalArgumentException("prefix must be null or non-empty");
        }
        this.props = ((props == null) ? Collections.<String, Object>emptyMap() : Collections.unmodifiableMap(props));
        this.systemPropertyPrefix = systemPropertyPrefix;
    }
    
    public Map<String, Object> getProperties() {
        return this.props;
    }
    
    protected Map<String, Object> createMap() {
        return new HashMap<String, Object>();
    }
    
    protected Map<String, Object> nestedProps(final String key) {
        Object value = this.getProperties().get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof ServerProp) {
            value = ((ServerProp)value).getValue();
        }
        if (Map.class.isInstance(value)) {
            return (Map<String, Object>)value;
        }
        final String msg = MessageFormat.format("Agent configuration expected nested configuration values for \"{0}\", got \"{1}\"", key, value);
        Agent.LOG.warning(msg);
        return null;
    }
    
    protected Object getPropertyFromSystemProperties(final String name, final Object defaultVal) {
        if (this.systemPropertyPrefix == null) {
            return null;
        }
        final String key = this.getSystemPropertyKey(name);
        final String result = SystemPropertyFactory.getSystemPropertyProvider().getSystemProperty(key);
        return parseValue(result);
    }
    
    protected String getSystemPropertyKey(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }
        return this.systemPropertyPrefix + key;
    }
    
    protected Object getPropertyFromSystemEnvironment(final String name, final Object defaultVal) {
        if (this.systemPropertyPrefix == null) {
            return null;
        }
        final String key = this.getSystemPropertyKey(name);
        final String result = SystemPropertyFactory.getSystemPropertyProvider().getEnvironmentVariable(key);
        return parseValue(result);
    }
    
    public static Object parseValue(final String val) {
        if (val == null) {
            return val;
        }
        try {
            return new JSONParser().parse(val);
        }
        catch (ParseException e) {
            return val.toString();
        }
    }
    
    public <T> T getProperty(final String key, final T defaultVal) {
        Object propVal = this.getProperties().get(key);
        if (propVal instanceof ServerProp) {
            propVal = ((ServerProp)propVal).getValue();
            return this.castValue(key, propVal, defaultVal);
        }
        Object override = this.getPropertyFromSystemEnvironment(key, defaultVal);
        if (override != null) {
            return (T)override;
        }
        override = this.getPropertyFromSystemProperties(key, defaultVal);
        if (override != null) {
            return (T)override;
        }
        return this.castValue(key, propVal, defaultVal);
    }
    
    protected <T> T castValue(final String key, final Object value, final T defaultVal) {
        try {
            if (value == null) {
                return defaultVal;
            }
            if (value instanceof String) {
                return (T)((String)value).trim();
            }
            return (T)value;
        }
        catch (ClassCastException e) {
            return defaultVal;
        }
    }
    
    public <T> T getProperty(final String key) {
        return this.getProperty(key, (T)null);
    }
    
    protected Set<Integer> getIntegerSet(final String key, final Set<Integer> defaultVal) {
        final Object val = this.getProperty(key);
        if (val instanceof String) {
            return Collections.unmodifiableSet((Set<? extends Integer>)this.getIntegerSetFromString((String)val));
        }
        if (val instanceof Collection) {
            return Collections.unmodifiableSet((Set<? extends Integer>)this.getIntegerSetFromCollection((Collection<?>)val));
        }
        if (val instanceof Integer) {
            return Collections.unmodifiableSet((Set<? extends Integer>)this.getIntegerSetFromCollection(Arrays.asList((Integer)val)));
        }
        return defaultVal;
    }
    
    protected Set<Map<String, Object>> getMapSet(final String key) {
        final Object val = this.getProperty(key);
        if (val instanceof Collection) {
            return Collections.unmodifiableSet((Set<? extends Map<String, Object>>)this.getMapSetFromCollection((Collection<?>)val));
        }
        return Collections.emptySet();
    }
    
    protected Set<Map<String, Object>> getMapSetFromCollection(final Collection<?> values) {
        final Set<Map<String, Object>> result = new HashSet<Map<String, Object>>(values.size());
        for (final Object value : values) {
            result.add((Map<String, Object>)value);
        }
        return result;
    }
    
    protected String getFirstString(final String key, final String separator) {
        final Object val = this.getProperty(key);
        if (!(val instanceof String)) {
            if (val instanceof Collection) {
                final Collection<?> values = (Collection<?>)val;
                final Iterator i$ = values.iterator();
                if (i$.hasNext()) {
                    final Object value = i$.next();
                    String res = (String)value;
                    res = res.trim();
                    if (res.length() != 0) {
                        return res;
                    }
                    return null;
                }
            }
            return null;
        }
        final String[] values2 = ((String)val).split(separator);
        if (values2.length == 0) {
            return null;
        }
        final String res2 = values2[0].trim();
        if (res2.length() == 0) {
            return null;
        }
        return res2;
    }
    
    protected Collection<String> getUniqueStrings(final String key) {
        return this.getUniqueStrings(key, ",");
    }
    
    protected Collection<String> getUniqueStrings(final String key, final String separator) {
        final Object val = this.getProperty(key);
        if (val instanceof String) {
            return getUniqueStringsFromString((String)val, separator);
        }
        if (val instanceof Collection) {
            return getUniqueStringsFromCollection((Collection<?>)val);
        }
        return Collections.emptySet();
    }
    
    public static List<String> getUniqueStringsFromCollection(final Collection<?> values, final String prefix) {
        final List<String> result = new ArrayList<String>(values.size());
        final boolean noPrefix = prefix == null || prefix.isEmpty();
        for (final Object value : values) {
            String val = null;
            if (value instanceof Integer) {
                val = String.valueOf(value);
            }
            else if (value instanceof Long) {
                val = String.valueOf(value);
            }
            else {
                val = (String)value;
            }
            val = val.trim();
            if (val.length() != 0 && !result.contains(val)) {
                if (noPrefix) {
                    result.add(val);
                }
                else {
                    result.add(prefix + val);
                }
            }
        }
        return result;
    }
    
    public static List<String> getUniqueStringsFromCollection(final Collection<?> values) {
        return getUniqueStringsFromCollection(values, null);
    }
    
    public static List<String> getUniqueStringsFromString(final String valuesString, final String separator, final String prefix) {
        final String[] valuesArray = valuesString.split(separator);
        final List<String> result = new ArrayList<String>(valuesArray.length);
        final boolean noPrefix = prefix == null || prefix.isEmpty();
        for (String value : valuesArray) {
            value = value.trim();
            if (value.length() != 0 && !result.contains(value)) {
                if (noPrefix) {
                    result.add(value);
                }
                else {
                    result.add(prefix + value);
                }
            }
        }
        return result;
    }
    
    public static List<String> getUniqueStringsFromString(final String valuesString, final String separator) {
        return getUniqueStringsFromString(valuesString, separator, null);
    }
    
    protected int getIntProperty(final String key, final int defaultVal) {
        final Number val = this.getProperty(key);
        if (val == null) {
            return defaultVal;
        }
        return val.intValue();
    }
    
    protected double getDoubleProperty(final String key, final double defaultVal) {
        final Number val = this.getProperty(key);
        if (val == null) {
            return defaultVal;
        }
        return val.doubleValue();
    }
    
    private Set<Integer> getIntegerSetFromCollection(final Collection<?> values) {
        final Set<Integer> result = new HashSet<Integer>(values.size());
        for (final Object value : values) {
            final int val = ((Number)value).intValue();
            result.add(val);
        }
        return result;
    }
    
    private Set<Integer> getIntegerSetFromString(final String valuesString) {
        final String[] valuesArray = valuesString.split(",");
        final Set<Integer> result = new HashSet<Integer>(valuesArray.length);
        for (String value : valuesArray) {
            value = value.trim();
            if (value.length() != 0) {
                result.add(Integer.parseInt(value));
            }
        }
        return result;
    }
}

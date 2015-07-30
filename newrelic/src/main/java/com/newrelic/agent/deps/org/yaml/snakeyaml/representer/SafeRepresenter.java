// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.representer;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.math.BigInteger;
import com.newrelic.agent.deps.org.yaml.snakeyaml.util.Base64Coder;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import java.util.Date;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;

class SafeRepresenter extends BaseRepresenter
{
    public static Pattern BINARY_PATTERN;
    
    public SafeRepresenter(final Character default_style, final Boolean default_flow_style) {
        super(default_style, default_flow_style);
        this.nullRepresenter = new RepresentNull();
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.representers.put(Character.class, new RepresentString());
        this.representers.put(byte[].class, new RepresentByteArray());
        this.multiRepresenters.put(Number.class, new RepresentNumber());
        this.multiRepresenters.put(List.class, new RepresentList());
        this.multiRepresenters.put(Map.class, new RepresentMap());
        this.multiRepresenters.put(Set.class, new RepresentSet());
        this.multiRepresenters.put(new Object[0].getClass(), new RepresentArray());
        this.multiRepresenters.put(Date.class, new RepresentDate());
        this.multiRepresenters.put(Enum.class, new RepresentEnum());
    }
    
    protected boolean ignoreAliases(final Object data) {
        if (data == null) {
            return true;
        }
        if (data instanceof Object[]) {
            final Object[] array = (Object[])data;
            if (array.length == 0) {
                return true;
            }
        }
        return data instanceof String || data instanceof Boolean || data instanceof Integer || data instanceof Long || data instanceof Float || data instanceof Double || data instanceof Enum;
    }
    
    static {
        SafeRepresenter.BINARY_PATTERN = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]");
    }
    
    private class RepresentNull implements Represent
    {
        public Node representData(final Object data) {
            return SafeRepresenter.this.representScalar("tag:yaml.org,2002:null", "null");
        }
    }
    
    private class RepresentString implements Represent
    {
        public Node representData(final Object data) {
            String tag = "tag:yaml.org,2002:str";
            Character style = null;
            String value = data.toString();
            if (SafeRepresenter.BINARY_PATTERN.matcher(value).find()) {
                tag = "tag:yaml.org,2002:binary";
                final char[] binary = Base64Coder.encode(value.getBytes());
                value = String.valueOf(binary);
                style = '|';
            }
            return SafeRepresenter.this.representScalar(tag, value, style);
        }
    }
    
    private class RepresentBoolean implements Represent
    {
        public Node representData(final Object data) {
            String value;
            if (Boolean.TRUE.equals(data)) {
                value = "true";
            }
            else {
                value = "false";
            }
            return SafeRepresenter.this.representScalar("tag:yaml.org,2002:bool", value);
        }
    }
    
    private class RepresentNumber implements Represent
    {
        public Node representData(final Object data) {
            String tag;
            String value;
            if (data instanceof Byte || data instanceof Short || data instanceof Integer || data instanceof Long || data instanceof BigInteger) {
                tag = "tag:yaml.org,2002:int";
                value = data.toString();
            }
            else {
                final Number number = (Number)data;
                tag = "tag:yaml.org,2002:float";
                if (number.equals(Double.NaN)) {
                    value = ".NaN";
                }
                else if (number.equals(Double.POSITIVE_INFINITY)) {
                    value = ".inf";
                }
                else if (number.equals(Double.NEGATIVE_INFINITY)) {
                    value = "-.inf";
                }
                else {
                    value = number.toString();
                }
            }
            return SafeRepresenter.this.representScalar(tag, value);
        }
    }
    
    private class RepresentList implements Represent
    {
        public Node representData(final Object data) {
            return SafeRepresenter.this.representSequence("tag:yaml.org,2002:seq", (List<?>)data, null);
        }
    }
    
    private class RepresentArray implements Represent
    {
        public Node representData(final Object data) {
            final Object[] array = (Object[])data;
            final List<Object> list = Arrays.asList(array);
            return SafeRepresenter.this.representSequence("tag:yaml.org,2002:seq", list, null);
        }
    }
    
    private class RepresentMap implements Represent
    {
        public Node representData(final Object data) {
            return SafeRepresenter.this.representMapping("tag:yaml.org,2002:map", (Map<?, Object>)data, null);
        }
    }
    
    private class RepresentSet implements Represent
    {
        public Node representData(final Object data) {
            final Map<Object, Object> value = new LinkedHashMap<Object, Object>();
            final Set<Object> set = (Set<Object>)data;
            for (final Object key : set) {
                value.put(key, null);
            }
            return SafeRepresenter.this.representMapping("tag:yaml.org,2002:set", value, null);
        }
    }
    
    private class RepresentDate implements Represent
    {
        public Node representData(final Object data) {
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTime((Date)data);
            final int years = calendar.get(1);
            final int months = calendar.get(2) + 1;
            final int days = calendar.get(5);
            final int hour24 = calendar.get(11);
            final int minutes = calendar.get(12);
            final int seconds = calendar.get(13);
            final int millis = calendar.get(14);
            final StringBuffer buffer = new StringBuffer(String.valueOf(years));
            buffer.append("-");
            if (months < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(months));
            buffer.append("-");
            if (days < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(days));
            buffer.append("T");
            if (hour24 < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(hour24));
            buffer.append(":");
            if (minutes < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(minutes));
            buffer.append(":");
            if (seconds < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(seconds));
            if (millis > 0) {
                if (millis < 10) {
                    buffer.append(".00");
                }
                else if (millis < 100) {
                    buffer.append(".0");
                }
                else {
                    buffer.append(".");
                }
                buffer.append(String.valueOf(millis));
            }
            buffer.append("Z");
            return SafeRepresenter.this.representScalar("tag:yaml.org,2002:timestamp", buffer.toString(), null);
        }
    }
    
    private class RepresentEnum implements Represent
    {
        public Node representData(final Object data) {
            final String tag = "tag:yaml.org,2002:" + data.getClass().getName();
            return SafeRepresenter.this.representScalar(tag, data.toString());
        }
    }
    
    private class RepresentByteArray implements Represent
    {
        public Node representData(final Object data) {
            final String tag = "tag:yaml.org,2002:binary";
            final char[] binary = Base64Coder.encode((byte[])data);
            return SafeRepresenter.this.representScalar(tag, String.valueOf(binary), '|');
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.constructor;

import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.NodeId;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.Calendar;
import java.util.TimeZone;
import com.newrelic.agent.deps.org.yaml.snakeyaml.util.Base64Coder;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import java.util.HashMap;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Collections;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.SequenceNode;
import java.util.List;
import java.util.Collection;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import java.util.LinkedList;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.MappingNode;
import java.util.regex.Pattern;
import java.util.Map;

public class SafeConstructor extends BaseConstructor
{
    private static final Map<String, Boolean> BOOL_VALUES;
    private static final Pattern TIMESTAMP_REGEXP;
    private static final Pattern YMD_REGEXP;
    
    public SafeConstructor() {
        this.yamlConstructors.put("tag:yaml.org,2002:null", new ConstuctYamlNull());
        this.yamlConstructors.put("tag:yaml.org,2002:bool", new ConstuctYamlBool());
        this.yamlConstructors.put("tag:yaml.org,2002:int", new ConstuctYamlInt());
        this.yamlConstructors.put("tag:yaml.org,2002:float", new ConstuctYamlFloat());
        this.yamlConstructors.put("tag:yaml.org,2002:binary", new ConstuctYamlBinary());
        this.yamlConstructors.put("tag:yaml.org,2002:timestamp", new ConstuctYamlTimestamp());
        this.yamlConstructors.put("tag:yaml.org,2002:omap", new ConstuctYamlOmap());
        this.yamlConstructors.put("tag:yaml.org,2002:pairs", new ConstuctYamlPairs());
        this.yamlConstructors.put("tag:yaml.org,2002:set", new ConstuctYamlSet());
        this.yamlConstructors.put("tag:yaml.org,2002:str", new ConstuctYamlStr());
        this.yamlConstructors.put("tag:yaml.org,2002:seq", new ConstuctYamlSeq());
        this.yamlConstructors.put("tag:yaml.org,2002:map", new ConstuctYamlMap());
        this.yamlConstructors.put(null, new ConstuctUndefined());
    }
    
    private void flattenMapping(final MappingNode node) {
        final List<Node[]> merge = new LinkedList<Node[]>();
        int index = 0;
        final List<Node[]> nodeValue = node.getValue();
        while (index < nodeValue.size()) {
            final Node keyNode = nodeValue.get(index)[0];
            final Node valueNode = nodeValue.get(index)[1];
            if (keyNode.getTag().equals("tag:yaml.org,2002:merge")) {
                nodeValue.remove(index);
                switch (valueNode.getNodeId()) {
                    case mapping: {
                        final MappingNode mn = (MappingNode)valueNode;
                        this.flattenMapping(mn);
                        merge.addAll(mn.getValue());
                        continue;
                    }
                    case sequence: {
                        final List<List<Node[]>> submerge = new LinkedList<List<Node[]>>();
                        final SequenceNode sn = (SequenceNode)valueNode;
                        final List<Node> vals = sn.getValue();
                        for (final Node subnode : vals) {
                            if (!(subnode instanceof MappingNode)) {
                                throw new ConstructorException("while constructing a mapping", node.getStartMark(), "expected a mapping for merging, but found " + subnode.getNodeId(), subnode.getStartMark());
                            }
                            final MappingNode mnode = (MappingNode)subnode;
                            this.flattenMapping(mnode);
                            submerge.add(mnode.getValue());
                        }
                        Collections.reverse(submerge);
                        for (final List<Node[]> value : submerge) {
                            merge.addAll(value);
                        }
                        continue;
                    }
                    default: {
                        throw new ConstructorException("while constructing a mapping", node.getStartMark(), "expected a mapping or list of mappings for merging, but found " + valueNode.getNodeId(), valueNode.getStartMark());
                    }
                }
            }
            else if (keyNode.getTag().equals("tag:yaml.org,2002:value")) {
                keyNode.setTag("tag:yaml.org,2002:str");
                ++index;
            }
            else {
                ++index;
            }
        }
        if (!merge.isEmpty()) {
            merge.addAll(nodeValue);
            node.setValue(merge);
        }
    }
    
    protected Map<Object, Object> constructMapping(final MappingNode node) {
        this.flattenMapping(node);
        return super.constructMapping(node);
    }
    
    private Number createNumber(final int sign, String number, final int radix) {
        if (sign < 0) {
            number = "-" + number;
        }
        Number result;
        try {
            final int integer = Integer.parseInt(number, radix);
            result = new Integer(integer);
        }
        catch (NumberFormatException e) {
            try {
                final long longValue = Long.parseLong(number, radix);
                result = new Long(longValue);
            }
            catch (NumberFormatException e2) {
                result = new BigInteger(number, radix);
            }
        }
        return result;
    }
    
    static {
        (BOOL_VALUES = new HashMap<String, Boolean>()).put("yes", Boolean.TRUE);
        SafeConstructor.BOOL_VALUES.put("no", Boolean.FALSE);
        SafeConstructor.BOOL_VALUES.put("true", Boolean.TRUE);
        SafeConstructor.BOOL_VALUES.put("false", Boolean.FALSE);
        SafeConstructor.BOOL_VALUES.put("on", Boolean.TRUE);
        SafeConstructor.BOOL_VALUES.put("off", Boolean.FALSE);
        TIMESTAMP_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)(?:(?:[Tt]|[ \t]+)([0-9][0-9]?):([0-9][0-9]):([0-9][0-9])(?:\\.([0-9]*))?(?:[ \t]*(?:Z|([-+][0-9][0-9]?)(?::([0-9][0-9])?)?))?)?$");
        YMD_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)$");
    }
    
    private class ConstuctYamlNull implements Construct
    {
        public Object construct(final Node node) {
            SafeConstructor.this.constructScalar((ScalarNode)node);
            return null;
        }
    }
    
    private class ConstuctYamlBool implements Construct
    {
        public Object construct(final Node node) {
            final String val = (String)SafeConstructor.this.constructScalar((ScalarNode)node);
            return SafeConstructor.BOOL_VALUES.get(val.toLowerCase());
        }
    }
    
    private class ConstuctYamlInt implements Construct
    {
        public Object construct(final Node node) {
            String value = SafeConstructor.this.constructScalar((ScalarNode)node).toString().replaceAll("_", "");
            int sign = 1;
            final char first = value.charAt(0);
            if (first == '-') {
                sign = -1;
                value = value.substring(1);
            }
            else if (first == '+') {
                value = value.substring(1);
            }
            int base = 10;
            if (value.equals("0")) {
                return new Integer(0);
            }
            if (value.startsWith("0b")) {
                value = value.substring(2);
                base = 2;
            }
            else if (value.startsWith("0x")) {
                value = value.substring(2);
                base = 16;
            }
            else if (value.startsWith("0")) {
                value = value.substring(1);
                base = 8;
            }
            else {
                if (value.indexOf(58) != -1) {
                    final String[] digits = value.split(":");
                    int bes = 1;
                    int val = 0;
                    for (int i = 0, j = digits.length; i < j; ++i) {
                        val += (int)(Long.parseLong(digits[j - i - 1]) * bes);
                        bes *= 60;
                    }
                    return SafeConstructor.this.createNumber(sign, String.valueOf(val), 10);
                }
                return SafeConstructor.this.createNumber(sign, value, 10);
            }
            return SafeConstructor.this.createNumber(sign, value, base);
        }
    }
    
    private class ConstuctYamlFloat implements Construct
    {
        public Object construct(final Node node) {
            String value = SafeConstructor.this.constructScalar((ScalarNode)node).toString().replaceAll("_", "");
            int sign = 1;
            final char first = value.charAt(0);
            if (first == '-') {
                sign = -1;
                value = value.substring(1);
            }
            else if (first == '+') {
                value = value.substring(1);
            }
            final String valLower = value.toLowerCase();
            if (valLower.equals(".inf")) {
                return new Double((sign == -1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            }
            if (valLower.equals(".nan")) {
                return new Double(Double.NaN);
            }
            if (value.indexOf(58) != -1) {
                final String[] digits = value.split(":");
                int bes = 1;
                double val = 0.0;
                for (int i = 0, j = digits.length; i < j; ++i) {
                    val += Double.parseDouble(digits[j - i - 1]) * bes;
                    bes *= 60;
                }
                return new Double(sign * val);
            }
            try {
                final Double d = Double.valueOf(value);
                return new Double(d * sign);
            }
            catch (NumberFormatException e) {
                throw new YAMLException("Invalid number: '" + value + "'; in node " + node);
            }
        }
    }
    
    private class ConstuctYamlBinary implements Construct
    {
        public Object construct(final Node node) {
            final byte[] decoded = Base64Coder.decode(SafeConstructor.this.constructScalar((ScalarNode)node).toString().toCharArray());
            return decoded;
        }
    }
    
    private class ConstuctYamlTimestamp implements Construct
    {
        public Object construct(final Node node) {
            Matcher match = SafeConstructor.YMD_REGEXP.matcher((CharSequence)node.getValue());
            if (match.matches()) {
                final String year_s = match.group(1);
                final String month_s = match.group(2);
                final String day_s = match.group(3);
                final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.clear();
                cal.set(1, Integer.parseInt(year_s));
                cal.set(2, Integer.parseInt(month_s) - 1);
                cal.set(5, Integer.parseInt(day_s));
                return cal.getTime();
            }
            match = SafeConstructor.TIMESTAMP_REGEXP.matcher((CharSequence)node.getValue());
            if (!match.matches()) {
                throw new YAMLException("Expected timestamp: " + node);
            }
            final String year_s = match.group(1);
            final String month_s = match.group(2);
            final String day_s = match.group(3);
            final String hour_s = match.group(4);
            final String min_s = match.group(5);
            final String sec_s = match.group(6);
            final String fract_s = match.group(7);
            final String timezoneh_s = match.group(8);
            final String timezonem_s = match.group(9);
            int usec = 0;
            if (fract_s != null) {
                usec = Integer.parseInt(fract_s);
                if (usec != 0) {
                    while (10 * usec < 1000) {
                        usec *= 10;
                    }
                }
            }
            final Calendar cal2 = Calendar.getInstance();
            cal2.set(1, Integer.parseInt(year_s));
            cal2.set(2, Integer.parseInt(month_s) - 1);
            cal2.set(5, Integer.parseInt(day_s));
            cal2.set(11, Integer.parseInt(hour_s));
            cal2.set(12, Integer.parseInt(min_s));
            cal2.set(13, Integer.parseInt(sec_s));
            cal2.set(14, usec);
            if (timezoneh_s != null || timezonem_s != null) {
                int zone = 0;
                int sign = 1;
                if (timezoneh_s != null) {
                    if (timezoneh_s.startsWith("-")) {
                        sign = -1;
                    }
                    zone += Integer.parseInt(timezoneh_s.substring(1)) * 3600000;
                }
                if (timezonem_s != null) {
                    zone += Integer.parseInt(timezonem_s) * 60000;
                }
                cal2.set(15, sign * zone);
            }
            else {
                cal2.setTimeZone(TimeZone.getTimeZone("UTC"));
            }
            return cal2.getTime();
        }
    }
    
    private class ConstuctYamlOmap implements Construct
    {
        public Object construct(final Node node) {
            final Map<Object, Object> omap = new LinkedHashMap<Object, Object>();
            if (!(node instanceof SequenceNode)) {
                throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a sequence, but found " + node.getNodeId(), node.getStartMark());
            }
            final SequenceNode snode = (SequenceNode)node;
            for (final Node subnode : snode.getValue()) {
                if (!(subnode instanceof MappingNode)) {
                    throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a mapping of length 1, but found " + subnode.getNodeId(), subnode.getStartMark());
                }
                final MappingNode mnode = (MappingNode)subnode;
                if (mnode.getValue().size() != 1) {
                    throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a single mapping item, but found " + mnode.getValue().size() + " items", mnode.getStartMark());
                }
                final Node keyNode = mnode.getValue().get(0)[0];
                final Node valueNode = mnode.getValue().get(0)[1];
                final Object key = SafeConstructor.this.constructObject(keyNode);
                final Object value = SafeConstructor.this.constructObject(valueNode);
                omap.put(key, value);
            }
            return omap;
        }
    }
    
    private class ConstuctYamlPairs implements Construct
    {
        public Object construct(final Node node) {
            final List<Object[]> pairs = new LinkedList<Object[]>();
            if (!(node instanceof SequenceNode)) {
                throw new ConstructorException("while constructing pairs", node.getStartMark(), "expected a sequence, but found " + node.getNodeId(), node.getStartMark());
            }
            final SequenceNode snode = (SequenceNode)node;
            for (final Node subnode : snode.getValue()) {
                if (!(subnode instanceof MappingNode)) {
                    throw new ConstructorException("while constructingpairs", node.getStartMark(), "expected a mapping of length 1, but found " + subnode.getNodeId(), subnode.getStartMark());
                }
                final MappingNode mnode = (MappingNode)subnode;
                if (mnode.getValue().size() != 1) {
                    throw new ConstructorException("while constructing pairs", node.getStartMark(), "expected a single mapping item, but found " + mnode.getValue().size() + " items", mnode.getStartMark());
                }
                final Node keyNode = mnode.getValue().get(0)[0];
                final Node valueNode = mnode.getValue().get(0)[1];
                final Object key = SafeConstructor.this.constructObject(keyNode);
                final Object value = SafeConstructor.this.constructObject(valueNode);
                pairs.add(new Object[] { key, value });
            }
            return pairs;
        }
    }
    
    private class ConstuctYamlSet implements Construct
    {
        public Object construct(final Node node) {
            final Map<Object, Object> value = SafeConstructor.this.constructMapping((MappingNode)node);
            return value.keySet();
        }
    }
    
    private class ConstuctYamlStr implements Construct
    {
        public Object construct(final Node node) {
            final String value = (String)SafeConstructor.this.constructScalar((ScalarNode)node);
            return value;
        }
    }
    
    private class ConstuctYamlSeq implements Construct
    {
        public Object construct(final Node node) {
            return SafeConstructor.this.constructSequence((SequenceNode)node);
        }
    }
    
    private class ConstuctYamlMap implements Construct
    {
        public Object construct(final Node node) {
            return SafeConstructor.this.constructMapping((MappingNode)node);
        }
    }
    
    private class ConstuctUndefined implements Construct
    {
        public Object construct(final Node node) {
            throw new ConstructorException(null, null, "could not determine a constructor for the tag " + node.getTag(), node.getStartMark());
        }
    }
}

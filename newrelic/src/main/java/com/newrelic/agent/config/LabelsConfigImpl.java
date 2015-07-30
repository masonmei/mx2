// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONObject;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.io.Writer;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.base.CharMatcher;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class LabelsConfigImpl implements LabelsConfig, JSONStreamAware
{
    private final Map<String, String> labels;
    
    LabelsConfigImpl(final Object labelsObj) {
        this.labels = (Map<String, String>)Maps.newHashMap();
        this.parseLabels(labelsObj);
    }
    
    public Map<String, String> getLabels() {
        return this.labels;
    }
    
    private void parseLabels(final Object labelsObj) {
        if (labelsObj == null) {
            return;
        }
        try {
            if (labelsObj instanceof Map) {
                this.parseLabelsMap((Map<String, Object>)labelsObj);
            }
            else if (labelsObj instanceof String) {
                this.parseLabelsString((String)labelsObj);
            }
        }
        catch (LabelParseException lpe) {
            Agent.LOG.log(Level.WARNING, "Error parsing labels - {0}", new Object[] { lpe.getMessage() });
            Agent.LOG.log(Level.WARNING, "Labels will not be sent to New Relic");
            this.labels.clear();
        }
    }
    
    private void parseLabelsString(String labelsString) throws LabelParseException {
        labelsString = CharMatcher.is(';').trimFrom(labelsString);
        final String[] arr$;
        final String[] labelsArray = arr$ = labelsString.split(";");
        for (final String labelArray : arr$) {
            final String[] labelKeyAndValue = labelArray.split(":");
            if (labelKeyAndValue.length != 2) {
                throw new LabelParseException("invalid syntax");
            }
            this.addLabelPart(labelKeyAndValue[0], labelKeyAndValue[1]);
        }
    }
    
    private void parseLabelsMap(final Map<String, Object> labelsMap) throws LabelParseException {
        for (final Map.Entry<String, Object> entry : labelsMap.entrySet()) {
            if (entry.getValue() == null) {
                throw new LabelParseException("empty value");
            }
            this.addLabelPart(entry.getKey(), entry.getValue().toString());
        }
    }
    
    private void addLabelPart(String key, String value) throws LabelParseException {
        key = validateLabelPart(key);
        value = validateLabelPart(value);
        if (this.labels.size() == 64) {
            Agent.LOG.log(Level.WARNING, "Exceeded 64 label limit - only the first 64 labels will be sent to New Relic");
            return;
        }
        this.labels.put(key, value);
    }
    
    private static String validateLabelPart(String keyOrValue) throws LabelParseException {
        if (keyOrValue == null || keyOrValue.equals("")) {
            throw new LabelParseException("empty name or value");
        }
        if (keyOrValue.contains(":") || keyOrValue.contains(";")) {
            throw new LabelParseException("illegal character ':' or ';' in name or value '" + keyOrValue + "'");
        }
        if (keyOrValue.length() > 255) {
            keyOrValue = keyOrValue.substring(0, 255);
            Agent.LOG.log(Level.WARNING, "Label name or value over 255 characters.  Truncated to ''{0}''.", new Object[] { keyOrValue });
        }
        return keyOrValue.trim();
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        final List<JSONStreamAware> jsonLabels = new ArrayList<JSONStreamAware>(this.labels.size());
        for (final Map.Entry<String, String> entry : this.labels.entrySet()) {
            final String name = entry.getKey();
            final String value = entry.getValue();
            jsonLabels.add(new JSONStreamAware() {
                public void writeJSONString(final Writer out) throws IOException {
                    JSONObject.writeJSONString(ImmutableMap.of("label_type", name, "label_value", value), out);
                }
            });
        }
        JSONArray.writeJSONString(jsonLabels, out);
    }
    
    private static class LabelParseException extends Exception
    {
        public LabelParseException(final String message) {
            super(message);
        }
    }
}

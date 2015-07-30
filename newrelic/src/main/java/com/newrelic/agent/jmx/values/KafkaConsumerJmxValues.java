// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.KafkaMetricGenerator;
import java.util.regex.Matcher;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import com.newrelic.agent.jmx.create.JmxMetricModifier;
import java.util.regex.Pattern;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class KafkaConsumerJmxValues extends JmxFrameworkValues
{
    public static String PREFIX;
    protected static final Pattern BYTES_RECEIVED;
    protected static final Pattern MESSAGES_RECEIVED;
    private static final JmxMetricModifier TOPIC_MODIFIER;
    private static final JmxMetric COUNT;
    private static List<BaseJmxValue> METRICS;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return KafkaConsumerJmxValues.METRICS;
    }
    
    public String getPrefix() {
        return KafkaConsumerJmxValues.PREFIX;
    }
    
    static {
        KafkaConsumerJmxValues.PREFIX = "kafka.consumer";
        BYTES_RECEIVED = Pattern.compile("^JMX/\"(.+)-(.+?)-BytesPerSec\"/");
        MESSAGES_RECEIVED = Pattern.compile("^JMX/\"(.+)-(.+?)-MessagesPerSec\"/");
        TOPIC_MODIFIER = new JmxMetricModifier() {
            public String getMetricName(final String fullMetricName) {
                Matcher m = KafkaConsumerJmxValues.BYTES_RECEIVED.matcher(fullMetricName);
                if (m.matches() && m.groupCount() == 2) {
                    return "MessageBroker/Kafka/Topic/Consume/Named/" + m.group(2) + "/Received/Bytes";
                }
                m = KafkaConsumerJmxValues.MESSAGES_RECEIVED.matcher(fullMetricName);
                if (m.matches() && m.groupCount() == 2) {
                    return "MessageBroker/Kafka/Topic/Consume/Named/" + m.group(2) + "/Received/Messages";
                }
                return "";
            }
        };
        COUNT = KafkaMetricGenerator.COUNT_MONOTONIC.createMetric("Count");
        (KafkaConsumerJmxValues.METRICS = new ArrayList<BaseJmxValue>(1)).add(new BaseJmxValue("\"kafka.consumer\":type=\"ConsumerTopicMetrics\",name=*", "JMX/{name}/", KafkaConsumerJmxValues.TOPIC_MODIFIER, new JmxMetric[] { KafkaConsumerJmxValues.COUNT }));
    }
}

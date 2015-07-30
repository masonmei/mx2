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

public class KafkaProducerJmxValues extends JmxFrameworkValues
{
    public static String PREFIX;
    protected static final Pattern BYTES_SENT;
    protected static final Pattern MESSAGES_SENT;
    protected static final Pattern MESSAGES_DROPPED;
    private static final JmxMetricModifier TOPIC_MODIFIER;
    private static final JmxMetric COUNT;
    private static List<BaseJmxValue> METRICS;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return KafkaProducerJmxValues.METRICS;
    }
    
    public String getPrefix() {
        return KafkaProducerJmxValues.PREFIX;
    }
    
    static {
        KafkaProducerJmxValues.PREFIX = "kafka.producer";
        BYTES_SENT = Pattern.compile("^JMX/\"(.+)-(.+?)-BytesPerSec\"/");
        MESSAGES_SENT = Pattern.compile("^JMX/\"(.+)-(.+?)-MessagesPerSec\"/");
        MESSAGES_DROPPED = Pattern.compile("^JMX/\"(.+)-(.+?)-DroppedMessagesPerSec\"/");
        TOPIC_MODIFIER = new JmxMetricModifier() {
            public String getMetricName(final String fullMetricName) {
                Matcher m = KafkaProducerJmxValues.BYTES_SENT.matcher(fullMetricName);
                if (m.matches() && m.groupCount() == 2) {
                    return "MessageBroker/Kafka/Topic/Produce/Named/" + m.group(2) + "/Sent/Bytes";
                }
                m = KafkaProducerJmxValues.MESSAGES_SENT.matcher(fullMetricName);
                if (m.matches() && m.groupCount() == 2) {
                    return "MessageBroker/Kafka/Topic/Produce/Named/" + m.group(2) + "/Sent/Messages";
                }
                m = KafkaProducerJmxValues.MESSAGES_DROPPED.matcher(fullMetricName);
                if (m.matches() && m.groupCount() == 2) {
                    return "MessageBroker/Kafka/Topic/Produce/Named/" + m.group(2) + "/Dropped Messages";
                }
                return "";
            }
        };
        COUNT = KafkaMetricGenerator.COUNT_MONOTONIC.createMetric("Count");
        (KafkaProducerJmxValues.METRICS = new ArrayList<BaseJmxValue>(1)).add(new BaseJmxValue("\"kafka.producer\":type=\"ProducerTopicMetrics\",name=*", "JMX/{name}/", KafkaProducerJmxValues.TOPIC_MODIFIER, new JmxMetric[] { KafkaProducerJmxValues.COUNT }));
    }
}

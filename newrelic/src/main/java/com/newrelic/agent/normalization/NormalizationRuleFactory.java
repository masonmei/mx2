// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import com.newrelic.agent.transport.DataSenderWriter;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.util.Map;
import java.util.List;

public class NormalizationRuleFactory
{
    private static final String TRANSACTION_SEGMENT_TERMS_KEY = "transaction_segment_terms";
    public static final String URL_RULES_KEY = "url_rules";
    public static final String METRIC_NAME_RULES_KEY = "metric_name_rules";
    public static final String TRANSACTION_NAME_RULES_KEY = "transaction_name_rules";
    private static final List<Map<String, Object>> EMPTY_RULES_DATA;
    private static final List<NormalizationRule> EMPTY_RULES;
    
    public static List<NormalizationRule> getUrlRules(final String appName, final Map<String, Object> data) {
        try {
            final List<Map<String, Object>> rulesData = getUrlRulesData(appName, data);
            final List<NormalizationRule> rules = createRules(appName, rulesData);
            if (rules.isEmpty()) {
                Agent.LOG.warning("The agent did not receive any url rules from the New Relic server.");
            }
            else {
                final String msg = MessageFormat.format("Received {0} url rule(s) for {1}", rules.size(), appName);
                Agent.LOG.fine(msg);
            }
            return rules;
        }
        catch (Exception e) {
            final String msg2 = MessageFormat.format("An error occurred getting url rules for {0} from the New Relic server. This can indicate a problem with the agent rules supplied by the New Relic server.: {1}", appName, e);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg2, e);
            }
            else {
                Agent.LOG.log(Level.INFO, msg2);
            }
            return NormalizationRuleFactory.EMPTY_RULES;
        }
    }
    
    public static List<NormalizationRule> getMetricNameRules(final String appName, final Map<String, Object> data) {
        try {
            final List<Map<String, Object>> rulesData = getMetricNameRulesData(appName, data);
            final List<NormalizationRule> rules = createRules(appName, rulesData);
            final String msg = MessageFormat.format("Received {0} metric name rule(s) for {1}", rules.size(), appName);
            Agent.LOG.fine(msg);
            return rules;
        }
        catch (Exception e) {
            final String msg2 = MessageFormat.format("An error occurred getting metric name rules for {0} from the New Relic server. This can indicate a problem with the agent rules supplied by the New Relic server.: {1}", appName, e);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg2, e);
            }
            else {
                Agent.LOG.log(Level.INFO, msg2);
            }
            return NormalizationRuleFactory.EMPTY_RULES;
        }
    }
    
    public static List<NormalizationRule> getTransactionNameRules(final String appName, final Map<String, Object> data) {
        try {
            final List<Map<String, Object>> rulesData = getTransactionNameRulesData(appName, data);
            final List<NormalizationRule> rules = createRules(appName, rulesData);
            final String msg = MessageFormat.format("Received {0} transaction name rule(s) for {1}", rules.size(), appName);
            Agent.LOG.fine(msg);
            return rules;
        }
        catch (Exception e) {
            final String msg2 = MessageFormat.format("An error occurred getting transaction name rules for {0} from the New Relic server. This can indicate a problem with the agent rules supplied by the New Relic server.: {1}", appName, e);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg2, e);
            }
            else {
                Agent.LOG.log(Level.INFO, msg2);
            }
            return NormalizationRuleFactory.EMPTY_RULES;
        }
    }
    
    private static List<Map<String, Object>> getUrlRulesData(final String appName, final Map<String, Object> data) {
        try {
            final Object response = data.get("url_rules");
            if (response == null || DataSenderWriter.nullValue().equals(response)) {
                return NormalizationRuleFactory.EMPTY_RULES_DATA;
            }
            if (!(response instanceof List)) {
                final String msg = MessageFormat.format("Unexpected url rules data for {1}: {2}", appName, response);
                Agent.LOG.finer(msg);
                return NormalizationRuleFactory.EMPTY_RULES_DATA;
            }
            return (List<Map<String, Object>>)response;
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("An error occurred getting url rules data for {1} from the New Relic server. This can indicate a problem with the agent rules supplied by the New Relic server.: {2}", appName, e);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg, e);
            }
            else {
                Agent.LOG.log(Level.INFO, msg);
            }
            return NormalizationRuleFactory.EMPTY_RULES_DATA;
        }
    }
    
    private static List<Map<String, Object>> getMetricNameRulesData(final String appName, final Map<String, Object> data) {
        try {
            final Object response = data.get("metric_name_rules");
            if (response == null || DataSenderWriter.nullValue().equals(response)) {
                return NormalizationRuleFactory.EMPTY_RULES_DATA;
            }
            if (!(response instanceof List)) {
                final String msg = MessageFormat.format("Unexpected metric name rules data for {1}: {2}", appName, response);
                Agent.LOG.finer(msg);
                return NormalizationRuleFactory.EMPTY_RULES_DATA;
            }
            return (List<Map<String, Object>>)response;
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("An error occurred getting metric name rules data for {1} from the New Relic server. This can indicate a problem with the agent rules supplied by the New Relic server.: {2}", appName, e);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg, e);
            }
            else {
                Agent.LOG.log(Level.INFO, msg);
            }
            return NormalizationRuleFactory.EMPTY_RULES_DATA;
        }
    }
    
    private static List<Map<String, Object>> getTransactionNameRulesData(final String appName, final Map<String, Object> data) {
        try {
            final Object response = data.get("transaction_name_rules");
            if (response == null || DataSenderWriter.nullValue().equals(response)) {
                return NormalizationRuleFactory.EMPTY_RULES_DATA;
            }
            if (!(response instanceof List)) {
                final String msg = MessageFormat.format("Unexpected transaction name rules data for {1}: {2}", appName, response);
                Agent.LOG.finer(msg);
                return NormalizationRuleFactory.EMPTY_RULES_DATA;
            }
            return (List<Map<String, Object>>)response;
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("An error occurred getting transaction name rules data for {1} from the New Relic server. This can indicate a problem with the agent rules supplied by the New Relic server.: {2}", appName, e);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg, e);
            }
            else {
                Agent.LOG.log(Level.INFO, msg);
            }
            return NormalizationRuleFactory.EMPTY_RULES_DATA;
        }
    }
    
    private static List<NormalizationRule> createRules(final String appName, final List<Map<String, Object>> rulesData) throws Exception {
        final List<NormalizationRule> rules = new ArrayList<NormalizationRule>();
        for (int i = 0; i < rulesData.size(); ++i) {
            final Map<String, Object> ruleData = rulesData.get(i);
            final NormalizationRule rule = createRule(ruleData);
            if (rule != null) {
                if (Agent.LOG.isLoggable(Level.FINER)) {
                    final String msg = MessageFormat.format("Adding rule for \"{0}\": \"{1}\"", appName, rule);
                    Agent.LOG.finer(msg);
                }
                rules.add(rule);
            }
        }
        sortRules(rules);
        return rules;
    }
    
    private static void sortRules(final List<NormalizationRule> rules) {
        Collections.sort(rules, new Comparator<NormalizationRule>() {
            public int compare(final NormalizationRule lhs, final NormalizationRule rhs) {
                final Integer lhsOrder = lhs.getOrder();
                final Integer rhsOrder = rhs.getOrder();
                return lhsOrder.compareTo(rhsOrder);
            }
        });
    }
    
    private static NormalizationRule createRule(final Map<String, Object> ruleData) {
        Boolean eachSegment = ruleData.get("each_segment");
        if (eachSegment == null) {
            eachSegment = Boolean.FALSE;
        }
        Boolean replaceAll = ruleData.get("replace_all");
        if (replaceAll == null) {
            replaceAll = Boolean.FALSE;
        }
        Boolean ignore = ruleData.get("ignore");
        if (ignore == null) {
            ignore = Boolean.FALSE;
        }
        Boolean terminateChain = ruleData.get("terminate_chain");
        if (terminateChain == null) {
            terminateChain = Boolean.TRUE;
        }
        final int order = ruleData.get("eval_order").intValue();
        final String matchExpression = ruleData.get("match_expression");
        final String replacement = ruleData.get("replacement");
        return new NormalizationRule(matchExpression, replacement, ignore, order, terminateChain, eachSegment, replaceAll);
    }
    
    public static List<TransactionSegmentTerms> getTransactionSegmentTermRules(final String appName, final Map<String, Object> data) {
        final List<Map> segmentTerms = (List<Map>)data.get("transaction_segment_terms");
        List<TransactionSegmentTerms> list;
        if (segmentTerms == null) {
            list = Collections.emptyList();
        }
        else {
            list = (List<TransactionSegmentTerms>)Lists.newArrayList();
            for (final Map map : segmentTerms) {
                final List<String> terms = map.get("terms");
                final String prefix = map.get("prefix");
                final TransactionSegmentTerms tst = new TransactionSegmentTerms(prefix, (Set<String>)ImmutableSet.copyOf((Collection<?>)terms));
                list.add(tst);
            }
        }
        Agent.LOG.log(Level.FINE, "Received {0} transaction segment rule(s) for {1}", new Object[] { list.size(), appName });
        return list;
    }
    
    static {
        EMPTY_RULES_DATA = Collections.emptyList();
        EMPTY_RULES = Collections.emptyList();
    }
}

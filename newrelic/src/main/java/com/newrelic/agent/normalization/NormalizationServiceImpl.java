// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import java.util.Map;
import com.newrelic.agent.IRPMService;
import java.util.regex.Matcher;
import com.newrelic.agent.config.AgentConfig;
import java.util.Collections;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.regex.Pattern;
import com.newrelic.agent.ConnectionListener;
import com.newrelic.agent.service.AbstractService;

public class NormalizationServiceImpl extends AbstractService implements NormalizationService, ConnectionListener
{
    private static final Pattern PARAMETER_DELIMITER_PATTERN;
    private static final List<NormalizationRule> EMPTY_RULES;
    private final ConcurrentMap<String, Normalizer> urlNormalizers;
    private final ConcurrentMap<String, Normalizer> transactionNormalizers;
    private final ConcurrentMap<String, Normalizer> metricNormalizers;
    private volatile Normalizer defaultUrlNormalizer;
    private volatile Normalizer defaultTransactionNormalizer;
    private volatile Normalizer defaultMetricNormalizer;
    private final String defaultAppName;
    private final boolean autoAppNamingEnabled;
    
    public NormalizationServiceImpl() {
        super(NormalizationService.class.getSimpleName());
        this.urlNormalizers = new ConcurrentHashMap<String, Normalizer>();
        this.transactionNormalizers = new ConcurrentHashMap<String, Normalizer>();
        this.metricNormalizers = new ConcurrentHashMap<String, Normalizer>();
        final AgentConfig defaultAgentConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.defaultAppName = defaultAgentConfig.getApplicationName();
        this.autoAppNamingEnabled = defaultAgentConfig.isAutoAppNamingEnabled();
        this.defaultUrlNormalizer = this.createUrlNormalizer(this.defaultAppName, NormalizationServiceImpl.EMPTY_RULES);
        this.defaultTransactionNormalizer = this.createTransactionNormalizer(this.defaultAppName, NormalizationServiceImpl.EMPTY_RULES, Collections.emptyList());
        this.defaultMetricNormalizer = this.createMetricNormalizer(this.defaultAppName, NormalizationServiceImpl.EMPTY_RULES);
        ServiceFactory.getRPMServiceManager().addConnectionListener(this);
    }
    
    protected void doStart() throws Exception {
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getRPMServiceManager().removeConnectionListener(this);
    }
    
    public String getUrlBeforeParameters(final String url) {
        final Matcher paramDelimiterMatcher = NormalizationServiceImpl.PARAMETER_DELIMITER_PATTERN.matcher(url);
        if (paramDelimiterMatcher.matches()) {
            return paramDelimiterMatcher.group(1);
        }
        return url;
    }
    
    public Normalizer getUrlNormalizer(final String appName) {
        return this.getOrCreateUrlNormalizer(appName);
    }
    
    public Normalizer getTransactionNormalizer(final String appName) {
        return this.getOrCreateTransactionNormalizer(appName);
    }
    
    public Normalizer getMetricNormalizer(final String appName) {
        return this.getOrCreateMetricNormalizer(appName);
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public void connected(final IRPMService rpmService, final Map<String, Object> data) {
        final String appName = rpmService.getApplicationName();
        final List<NormalizationRule> urlRules = NormalizationRuleFactory.getUrlRules(appName, data);
        final List<NormalizationRule> metricNameRules = NormalizationRuleFactory.getMetricNameRules(appName, data);
        final List<NormalizationRule> transactionNameRules = NormalizationRuleFactory.getTransactionNameRules(appName, data);
        final List<TransactionSegmentTerms> transactionSegmentTermRules = NormalizationRuleFactory.getTransactionSegmentTermRules(appName, data);
        Normalizer normalizer = this.createUrlNormalizer(appName, urlRules);
        this.replaceUrlNormalizer(appName, normalizer);
        normalizer = this.createTransactionNormalizer(appName, transactionNameRules, transactionSegmentTermRules);
        this.replaceTransactionNormalizer(appName, normalizer);
        normalizer = this.createMetricNormalizer(appName, metricNameRules);
        this.replaceMetricNormalizer(appName, normalizer);
    }
    
    public void disconnected(final IRPMService rpmService) {
    }
    
    private Normalizer getOrCreateUrlNormalizer(final String appName) {
        Normalizer normalizer = this.findUrlNormalizer(appName);
        if (normalizer != null) {
            return normalizer;
        }
        normalizer = this.createUrlNormalizer(appName, NormalizationServiceImpl.EMPTY_RULES);
        final Normalizer oldNormalizer = this.urlNormalizers.putIfAbsent(appName, normalizer);
        return (oldNormalizer == null) ? normalizer : oldNormalizer;
    }
    
    private Normalizer findUrlNormalizer(final String appName) {
        if (!this.autoAppNamingEnabled || appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultUrlNormalizer;
        }
        return this.urlNormalizers.get(appName);
    }
    
    private void replaceUrlNormalizer(final String appName, final Normalizer normalizer) {
        final Normalizer oldNormalizer = this.getUrlNormalizer(appName);
        if (oldNormalizer == this.defaultUrlNormalizer) {
            this.defaultUrlNormalizer = normalizer;
        }
        else {
            this.urlNormalizers.put(appName, normalizer);
        }
    }
    
    private Normalizer getOrCreateTransactionNormalizer(final String appName) {
        Normalizer normalizer = this.findTransactionNormalizer(appName);
        if (normalizer != null) {
            return normalizer;
        }
        normalizer = this.createTransactionNormalizer(appName, NormalizationServiceImpl.EMPTY_RULES, Collections.emptyList());
        final Normalizer oldNormalizer = this.transactionNormalizers.putIfAbsent(appName, normalizer);
        return (oldNormalizer == null) ? normalizer : oldNormalizer;
    }
    
    private Normalizer findTransactionNormalizer(final String appName) {
        if (!this.autoAppNamingEnabled || appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultTransactionNormalizer;
        }
        return this.transactionNormalizers.get(appName);
    }
    
    private void replaceTransactionNormalizer(final String appName, final Normalizer normalizer) {
        final Normalizer oldNormalizer = this.getTransactionNormalizer(appName);
        if (oldNormalizer == this.defaultTransactionNormalizer) {
            this.defaultTransactionNormalizer = normalizer;
        }
        else {
            this.transactionNormalizers.put(appName, normalizer);
        }
    }
    
    private Normalizer getOrCreateMetricNormalizer(final String appName) {
        Normalizer normalizer = this.findMetricNormalizer(appName);
        if (normalizer != null) {
            return normalizer;
        }
        normalizer = this.createMetricNormalizer(appName, NormalizationServiceImpl.EMPTY_RULES);
        final Normalizer oldNormalizer = this.metricNormalizers.putIfAbsent(appName, normalizer);
        return (oldNormalizer == null) ? normalizer : oldNormalizer;
    }
    
    private Normalizer findMetricNormalizer(final String appName) {
        if (!this.autoAppNamingEnabled || appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultMetricNormalizer;
        }
        return this.metricNormalizers.get(appName);
    }
    
    private void replaceMetricNormalizer(final String appName, final Normalizer normalizer) {
        final Normalizer oldNormalizer = this.getMetricNormalizer(appName);
        if (oldNormalizer == this.defaultMetricNormalizer) {
            this.defaultMetricNormalizer = normalizer;
        }
        else {
            this.metricNormalizers.put(appName, normalizer);
        }
    }
    
    private Normalizer createUrlNormalizer(final String appName, final List<NormalizationRule> urlRules) {
        return NormalizerFactory.createUrlNormalizer(appName, urlRules);
    }
    
    private Normalizer createTransactionNormalizer(final String appName, final List<NormalizationRule> metricNameRules, final List<TransactionSegmentTerms> transactionSegmentTermRules) {
        return NormalizerFactory.createTransactionNormalizer(appName, metricNameRules, transactionSegmentTermRules);
    }
    
    private Normalizer createMetricNormalizer(final String appName, final List<NormalizationRule> metricNameRules) {
        return NormalizerFactory.createMetricNormalizer(appName, metricNameRules);
    }
    
    static {
        PARAMETER_DELIMITER_PATTERN = Pattern.compile("(.*?)(\\?|#|;).*", 32);
        EMPTY_RULES = Collections.emptyList();
    }
}

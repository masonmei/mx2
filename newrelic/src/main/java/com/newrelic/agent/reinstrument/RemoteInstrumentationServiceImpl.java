// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.reinstrument;

import java.text.MessageFormat;
import java.util.Set;
import com.newrelic.agent.instrumentation.custom.ClassRetransformer;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.util.Collection;
import com.newrelic.agent.extension.util.ExtensionConversionUtility;
import com.newrelic.agent.instrumentation.InstrumentationType;
import java.util.Arrays;
import java.util.Collections;
import com.newrelic.agent.extension.dom.ExtensionDomParser;
import java.util.ArrayList;
import java.util.logging.Level;
import com.newrelic.agent.extension.beans.Extension;
import java.util.Iterator;
import com.newrelic.agent.Agent;
import java.util.List;
import java.util.Map;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.commands.InstrumentUpdateCommand;
import com.newrelic.agent.commands.Command;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.config.ReinstrumentConfig;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.ConnectionListener;
import com.newrelic.agent.service.AbstractService;

public class RemoteInstrumentationServiceImpl extends AbstractService implements RemoteInstrumentationService, ConnectionListener, AgentConfigListener
{
    private static final String INSTRUMENTATION_CONFIG = "instrumentation";
    private static final String CONFIG_KEY = "config";
    private final ReinstrumentConfig reinstrumentConfig;
    private final boolean isEnabled;
    private volatile boolean isLiveAttributesEnabled;
    private volatile String mostRecentXml;
    
    public RemoteInstrumentationServiceImpl() {
        super(RemoteInstrumentationService.class.getSimpleName());
        this.mostRecentXml = null;
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.reinstrumentConfig = config.getReinstrumentConfig();
        this.isEnabled = this.reinstrumentConfig.isEnabled();
        this.isLiveAttributesEnabled = this.reinstrumentConfig.isAttributesEnabled();
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    protected void doStart() throws Exception {
        if (this.isEnabled) {
            ServiceFactory.getCommandParser().addCommands(new InstrumentUpdateCommand(this));
            ServiceFactory.getRPMServiceManager().addConnectionListener(this);
            ServiceFactory.getConfigService().addIAgentConfigListener(this);
        }
    }
    
    protected void doStop() throws Exception {
        if (this.isEnabled) {
            ServiceFactory.getRPMServiceManager().removeConnectionListener(this);
            ServiceFactory.getConfigService().removeIAgentConfigListener(this);
        }
    }
    
    public void connected(final IRPMService pRpmService, final Map<String, Object> pConnectionInfo) {
        if (pConnectionInfo != null) {
            final Object value = pConnectionInfo.get("instrumentation");
            if (value != null && value instanceof List) {
                final List<Map> daMaps = (List<Map>)value;
                for (final Map current : daMaps) {
                    final Object config = current.get("config");
                    if (config != null && config instanceof String) {
                        this.processXml((String)config);
                    }
                    else {
                        Agent.LOG.info("The instrumentation configuration passed down does not contain a config key.");
                    }
                }
            }
        }
    }
    
    public void disconnected(final IRPMService pRpmService) {
    }
    
    public ReinstrumentResult processXml(final String pXml) {
        final ReinstrumentResult result = new ReinstrumentResult();
        try {
            if (this.isEnabled) {
                if (ServiceFactory.getAgent().getInstrumentation().isRetransformClassesSupported()) {
                    if (!ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity()) {
                        this.mostRecentXml = pXml;
                        if (this.isAllXmlRemoved(pXml)) {
                            Agent.LOG.info("The XML file is empty. All custom instrumentation will be removed.");
                            this.updateJvmWithExtension(null, result);
                        }
                        else {
                            Agent.LOG.log(Level.FINE, "Instrumentation modifications received from the server with attributes {0}.", new Object[] { this.isLiveAttributesEnabled ? "enabled" : "disabled" });
                            final Extension currentExt = this.getExtensionAndAddErrors(result, pXml);
                            if (currentExt != null) {
                                this.updateJvmWithExtension(currentExt, result);
                            }
                        }
                    }
                    else {
                        this.handleErrorNoInstrumentation(result, "Remote instrumentation is not supported in high security mode.", pXml);
                    }
                }
                else {
                    this.handleErrorNoInstrumentation(result, "Retransform classes is not supported on the current instrumentation.", pXml);
                }
            }
            else {
                this.handleErrorNoInstrumentation(result, "The Reinstrument Service is currently disabled.", pXml);
            }
        }
        catch (Exception e) {
            this.handleErrorPartialInstrumentation(result, "An unexpected exception occured: " + e.getMessage(), pXml);
        }
        return result;
    }
    
    private boolean isAllXmlRemoved(final String pXml) {
        return pXml == null || pXml.trim().length() == 0;
    }
    
    private Extension getExtensionAndAddErrors(final ReinstrumentResult result, final String pXml) {
        final List<Exception> exceptions = new ArrayList<Exception>();
        final Extension currentExt = ExtensionDomParser.readStringGatherExceptions(pXml, exceptions);
        ReinstrumentUtils.handleErrorPartialInstrumentation(result, exceptions, pXml);
        return currentExt;
    }
    
    private void updateJvmWithExtension(final Extension ext, final ReinstrumentResult result) {
        List<ExtensionClassAndMethodMatcher> pointCuts = null;
        if (ext == null || !ext.isEnabled()) {
            pointCuts = Collections.emptyList();
        }
        else {
            pointCuts = ExtensionConversionUtility.convertToEnabledPointCuts(Arrays.asList(ext), true, InstrumentationType.RemoteCustomXml, this.isLiveAttributesEnabled);
        }
        result.setPointCutsSpecified(pointCuts.size());
        final ClassRetransformer remoteRetransformer = ServiceFactory.getClassTransformerService().getRemoteRetransformer();
        remoteRetransformer.setClassMethodMatchers(pointCuts);
        final Class<?>[] allLoadedClasses = (Class<?>[])ServiceFactory.getAgent().getInstrumentation().getAllLoadedClasses();
        final Set<Class<?>> classesToRetransform = InstrumentationContext.getMatchingClasses(remoteRetransformer.getMatchers(), allLoadedClasses);
        ReinstrumentUtils.checkClassExistsAndRetransformClasses(result, pointCuts, ext, classesToRetransform);
    }
    
    private void handleErrorPartialInstrumentation(final ReinstrumentResult result, final String msg, final String pXml) {
        result.addErrorMessage(msg);
        if (Agent.LOG.isFineEnabled()) {
            Agent.LOG.fine(MessageFormat.format(msg + " This xml being processed was: {0}", pXml));
        }
    }
    
    private void handleErrorNoInstrumentation(final ReinstrumentResult result, final String msg, final String pXml) {
        result.addErrorMessage(msg);
        if (Agent.LOG.isFineEnabled()) {
            Agent.LOG.fine(MessageFormat.format(msg + " This xml will not be instrumented: {0}", pXml));
        }
    }
    
    public void configChanged(final String appName, final AgentConfig agentConfig) {
        final boolean attsEnabled = agentConfig.getReinstrumentConfig().isAttributesEnabled();
        if (this.isLiveAttributesEnabled != attsEnabled) {
            this.isLiveAttributesEnabled = attsEnabled;
            Agent.LOG.log(Level.FINE, "RemoteInstrumentationService: Remote attributes are {0}", new Object[] { this.isLiveAttributesEnabled ? "enabled" : "disabled" });
            if (this.mostRecentXml != null) {
                this.processXml(this.mostRecentXml);
            }
        }
    }
}

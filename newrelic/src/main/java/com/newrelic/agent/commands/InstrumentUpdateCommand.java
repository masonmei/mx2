// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

import com.newrelic.agent.reinstrument.ReinstrumentResult;
import java.util.Collections;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.reinstrument.RemoteInstrumentationService;

public class InstrumentUpdateCommand extends AbstractCommand
{
    private static final String COMMAND_NAME = "instrumentation_update";
    protected static final String ARG_NAME = "instrumentation";
    protected static final String ARG_VALUE_MAP_NAME = "config";
    private final RemoteInstrumentationService service;
    
    public InstrumentUpdateCommand(final RemoteInstrumentationService pService) {
        super("instrumentation_update");
        this.service = pService;
    }
    
    public Map process(final IRPMService pRpmService, final Map pArguments) throws CommandException {
        Agent.LOG.log(Level.FINE, "Processing an instrumentation update command.");
        if (pArguments == null || pArguments.size() == 0) {
            Agent.LOG.warning(MessageFormat.format("The instrumentation_update command must have atleast one argument called {0}", "instrumentation"));
            throw new CommandException("The instrumentation_update command expected 1 argument.");
        }
        final String xml = getXmlFromMaps(pArguments);
        if (xml != null) {
            final ReinstrumentResult result = this.service.processXml(xml);
            if (result != null) {
                return result.getStatusMap();
            }
        }
        return Collections.EMPTY_MAP;
    }
    
    private static void warnIfArgsLeft(final Map pArguments) {
        if (pArguments.size() > 0) {
            Agent.LOG.warning(MessageFormat.format("The instrumentation_update command did not recognize the following arguments: {0}", pArguments.keySet().toString()));
        }
    }
    
    protected static String getXmlFromMaps(final Map pArguments) {
        final Object instrumentationWorkObject = pArguments.remove("instrumentation");
        warnIfArgsLeft(pArguments);
        if (instrumentationWorkObject != null) {
            if (instrumentationWorkObject instanceof Map) {
                final Map instrumentWorkMap = (Map)instrumentationWorkObject;
                final Object config = instrumentWorkMap.get("config");
                return getXml(config);
            }
            Agent.LOG.log(Level.INFO, "The agent instrumentation object is not a Map. The XML will not be processed.");
        }
        else {
            Agent.LOG.log(Level.INFO, "The agent instrumentation object is either null. The instrumentation XML will not be processed.");
        }
        return null;
    }
    
    private static String getXml(final Object xml) {
        if (xml != null) {
            if (xml instanceof String) {
                return (String)xml;
            }
            Agent.LOG.info(MessageFormat.format("The property {0} was empty meaning no instrumentation update will occur.", "config"));
        }
        else {
            Agent.LOG.log(Level.INFO, "The agent instrumentation XML is null. The instrumentation XML will not be processed.");
        }
        return null;
    }
}

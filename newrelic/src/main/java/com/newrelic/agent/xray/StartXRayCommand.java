// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.xray;

import java.util.List;
import java.util.Collections;
import com.newrelic.agent.Agent;
import com.newrelic.agent.commands.CommandException;
import java.util.Map;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.commands.AbstractCommand;

public class StartXRayCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "active_xray_sessions";
    private static final String DISABLED_MESSAGE = "The X-Ray service is disabled";
    private IXRaySessionService xRaySessionService;
    
    public StartXRayCommand(final XRaySessionService xRaySessionService) {
        super("active_xray_sessions");
        this.xRaySessionService = xRaySessionService;
    }
    
    public Map<?, ?> process(final IRPMService rpmService, final Map arguments) throws CommandException {
        if (this.xRaySessionService.isEnabled()) {
            return (Map<?, ?>)this.processEnabled(rpmService, arguments);
        }
        return (Map<?, ?>)this.processDisabled(rpmService, arguments);
    }
    
    private Map processDisabled(final IRPMService rpmService, final Map arguments) {
        Agent.LOG.debug("The X-Ray service is disabled");
        try {
            this.xRaySessionService.stop();
        }
        catch (Exception e) {
            Agent.LOG.warning("Error disabling X-Ray Session service: " + e.getMessage());
        }
        return Collections.EMPTY_MAP;
    }
    
    private Map processEnabled(final IRPMService rpmService, final Map arguments) {
        final Object xray_ids = arguments.remove("xray_ids");
        List<Long> xrayIds;
        if (xray_ids instanceof List) {
            xrayIds = (List<Long>)xray_ids;
        }
        else {
            xrayIds = Collections.emptyList();
        }
        return this.xRaySessionService.processSessionsList(xrayIds, rpmService);
    }
}

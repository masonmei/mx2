// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.Collections;
import com.newrelic.agent.commands.CommandException;
import java.util.Map;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.commands.AbstractCommand;
import com.sun.org.apache.xpath.internal.operations.Bool;

public class StopProfilerCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "stop_profiler";
    private final ProfilerControl profilerControl;
    
    public StopProfilerCommand(final ProfilerControl profilerControl) {
        super("stop_profiler");
        this.profilerControl = profilerControl;
    }
    
    public Map process(final IRPMService rpmService, final Map arguments) throws CommandException {
        if (arguments.size() != 2) {
            throw new CommandException("The stop_profiler command expected 2 arguments");
        }
        final Object report = arguments.get("report_data");
        final Object profileId = arguments.get("profile_id");
        if (!(profileId instanceof Number)) {
            throw new CommandException("The start_profiler command encountered an invalid profile id: " + profileId);
        }
        if (!(report instanceof Boolean)) {
            throw new CommandException("The start_profiler command encountered an invalid report_data parameter: " + report);
        }
        this.profilerControl.stopProfiler(((Number)profileId).longValue(), (Boolean)report);
        return Collections.EMPTY_MAP;
    }
}

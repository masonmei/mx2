// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import com.newrelic.agent.util.TimeConversion;
import java.text.MessageFormat;
import java.util.HashMap;
import com.newrelic.agent.Agent;
import java.util.Collections;
import com.newrelic.agent.commands.CommandException;
import java.util.Map;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.commands.AbstractCommand;

public class StartProfilerCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "start_profiler";
    private static final String DISABLED_MESSAGE = "The profiler service is disabled";
    private static final String DURATION = "duration";
    private static final String SAMPLE_PERIOD = "sample_period";
    private static final String PROFILE_ID = "profile_id";
    private static final String ONLY_RUNNABLE_THREADS = "only_runnable_threads";
    private static final String ONLY_REQUEST_THREADS = "only_request_threads";
    private static final String PROFILE_AGENT_CODE = "profile_agent_code";
    private static final boolean DEFAULT_ONLY_RUNNABLE_THREADS = false;
    private static final boolean DEFAULT_ONLY_REQUEST_THREADS = false;
    private final ProfilerControl profilerControl;
    
    public StartProfilerCommand(final ProfilerControl profilerControl) {
        super("start_profiler");
        this.profilerControl = profilerControl;
    }
    
    public Map<?, ?> process(final IRPMService rpmService, final Map arguments) throws CommandException {
        if (this.profilerControl.isEnabled()) {
            return this.processEnabled(rpmService, arguments);
        }
        return this.processDisabled(rpmService, arguments);
    }
    
    public Map<?, ?> processEnabled(final IRPMService rpmService, final Map<?, ?> arguments) throws CommandException {
        final ProfilerParameters parameters = this.createProfilerParameters(arguments);
        this.profilerControl.startProfiler(parameters);
        return (Map<?, ?>)Collections.EMPTY_MAP;
    }
    
    public Map<?, ?> processDisabled(final IRPMService rpmService, final Map<?, ?> arguments) throws CommandException {
        Agent.LOG.info("The profiler service is disabled");
        final Map<String, String> result = new HashMap<String, String>();
        result.put("error", "The profiler service is disabled");
        return result;
    }
    
    private ProfilerParameters createProfilerParameters(final Map<?, ?> arguments) throws CommandException {
        final long profileId = this.getProfileId(arguments);
        final double samplePeriod = this.getSamplePeriod(arguments);
        final double duration = this.getDuration(arguments);
        if (samplePeriod > duration) {
            final String msg = MessageFormat.format("{0} > {1} in start_profiler command: {2} > {3}", "sample_period", "duration", samplePeriod, duration);
            throw new CommandException(msg);
        }
        final long samplePeriodInMillis = TimeConversion.convertSecondsToMillis(samplePeriod);
        final long durationInMillis = TimeConversion.convertSecondsToMillis(duration);
        final boolean onlyRunnableThreads = this.getOnlyRunnableThreads(arguments);
        final boolean onlyRequestThreads = this.getOnlyRequestThreads(arguments);
        final boolean profileAgentCode = this.getProfileAgentCode(arguments);
        if (arguments.size() > 0) {
            final String msg2 = MessageFormat.format("Unexpected arguments in start_profiler command: {0}", arguments.keySet().toString());
            Agent.LOG.warning(msg2);
        }
        return new ProfilerParameters(profileId, samplePeriodInMillis, durationInMillis, onlyRunnableThreads, onlyRequestThreads, profileAgentCode, null, null, null);
    }
    
    private long getProfileId(final Map<?, ?> arguments) throws CommandException {
        final Object profileId = arguments.remove("profile_id");
        if (profileId instanceof Number) {
            return ((Number)profileId).longValue();
        }
        if (profileId == null) {
            final String msg = MessageFormat.format("Missing {0} in start_profiler command", "profile_id");
            throw new CommandException(msg);
        }
        final String msg = MessageFormat.format("Invalid {0} in start_profiler command: {1}", "profile_id", profileId);
        throw new CommandException(msg);
    }
    
    private double getSamplePeriod(final Map<?, ?> arguments) throws CommandException {
        final Object samplePeriod = arguments.remove("sample_period");
        if (samplePeriod instanceof Number) {
            return ((Number)samplePeriod).doubleValue();
        }
        if (samplePeriod == null) {
            final String msg = MessageFormat.format("Missing {0} in start_profiler command", "sample_period");
            throw new CommandException(msg);
        }
        final String msg = MessageFormat.format("Invalid {0} in start_profiler command: {1}", "sample_period", samplePeriod);
        throw new CommandException(msg);
    }
    
    private double getDuration(final Map<?, ?> arguments) throws CommandException {
        final Object duration = arguments.remove("duration");
        if (duration instanceof Number) {
            return ((Number)duration).doubleValue();
        }
        if (duration == null) {
            final String msg = MessageFormat.format("Missing {0} in start_profiler command", "duration");
            throw new CommandException(msg);
        }
        final String msg = MessageFormat.format("Invalid {0} in start_profiler command: {1}", "duration", duration);
        throw new CommandException(msg);
    }
    
    private boolean getOnlyRunnableThreads(final Map<?, ?> arguments) throws CommandException {
        final Object onlyRunnableThreads = arguments.remove("only_runnable_threads");
        if (onlyRunnableThreads instanceof Boolean) {
            return (boolean)onlyRunnableThreads;
        }
        if (onlyRunnableThreads == null) {
            return false;
        }
        final String msg = MessageFormat.format("Invalid {0} in start_profiler command: {1}", "only_runnable_threads", onlyRunnableThreads);
        throw new CommandException(msg);
    }
    
    private boolean getOnlyRequestThreads(final Map<?, ?> arguments) throws CommandException {
        final Object onlyRequestThreads = arguments.remove("only_request_threads");
        if (onlyRequestThreads instanceof Boolean) {
            return (boolean)onlyRequestThreads;
        }
        if (onlyRequestThreads == null) {
            return false;
        }
        final String msg = MessageFormat.format("Invalid {0} in start_profiler command: {1}", "only_request_threads", onlyRequestThreads);
        throw new CommandException(msg);
    }
    
    private boolean getProfileAgentCode(final Map<?, ?> arguments) throws CommandException {
        final Object profileAgentCode = arguments.remove("profile_agent_code");
        if (profileAgentCode instanceof Boolean) {
            return (boolean)profileAgentCode;
        }
        if (profileAgentCode == null) {
            return Agent.isDebugEnabled();
        }
        final String msg = MessageFormat.format("Invalid {0} in start_profiler command: {1}", "profile_agent_code", profileAgentCode);
        throw new CommandException(msg);
    }
}

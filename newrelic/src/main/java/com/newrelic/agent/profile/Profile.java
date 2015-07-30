// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.Collection;
import java.util.ArrayList;
import com.newrelic.agent.util.StackTraces;
import com.newrelic.agent.transport.DataSenderWriter;
import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Set;
import com.newrelic.agent.ThreadService;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Agent;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class Profile implements IProfile
{
    public static final int MAX_STACK_DEPTH = 300;
    public static final int MAX_STACK_SIZE = 60000;
    public static final int MAX_ENCODED_BYTES = 1000000;
    public static final int STACK_TRIM = 10000;
    private long startTimeMillis;
    private long endTimeMillis;
    private int sampleCount;
    private int totalThreadCount;
    private int runnableThreadCount;
    private Map<Long, Long> startThreadCpuTimes;
    private final ProfilerParameters profilerParameters;
    private final Map<ThreadType, ProfileTree> profileTrees;
    
    public Profile(final ProfilerParameters parameters) {
        this.startTimeMillis = 0L;
        this.endTimeMillis = 0L;
        this.sampleCount = 0;
        this.totalThreadCount = 0;
        this.runnableThreadCount = 0;
        this.profileTrees = new HashMap<ThreadType, ProfileTree>();
        this.profilerParameters = parameters;
    }
    
    private Map<Long, Long> getThreadCpuTimes() {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        if (!threadMXBean.isThreadCpuTimeSupported() || !threadMXBean.isThreadCpuTimeEnabled()) {
            return Collections.emptyMap();
        }
        final HashMap<Long, Long> cpuTimes = new HashMap<Long, Long>();
        for (final long id : threadMXBean.getAllThreadIds()) {
            cpuTimes.put(id, threadMXBean.getThreadCpuTime(id));
        }
        return cpuTimes;
    }
    
    public ProfileTree getProfileTree(final ThreadType threadType) {
        ProfileTree profileTree = this.profileTrees.get(threadType);
        if (profileTree == null) {
            profileTree = new ProfileTree();
            this.profileTrees.put(threadType, profileTree);
        }
        return profileTree;
    }
    
    public void start() {
        this.startTimeMillis = System.currentTimeMillis();
        this.startThreadCpuTimes = this.getThreadCpuTimes();
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        if (!threadMXBean.isThreadCpuTimeSupported()) {
            Agent.LOG.info("Profile unable to record CPU time: Thread CPU time measurement is not supported");
        }
        else if (!threadMXBean.isThreadCpuTimeEnabled()) {
            Agent.LOG.info("Profile unable to record CPU time: Thread CPU time measurement is not enabled");
        }
    }
    
    public void end() {
        this.endTimeMillis = System.currentTimeMillis();
        final Map<Long, Long> endThreadCpuTimes = this.getThreadCpuTimes();
        final ThreadService threadService = ServiceFactory.getThreadService();
        final Set<Long> requestThreadIds = threadService.getRequestThreadIds();
        final Set<Long> backgroundThreadIds = threadService.getBackgroundThreadIds();
        final Set<Long> agentThreadIds = threadService.getAgentThreadIds();
        for (final Map.Entry<Long, Long> entry : endThreadCpuTimes.entrySet()) {
            Long startTime = this.startThreadCpuTimes.get(entry.getKey());
            if (startTime == null) {
                startTime = 0L;
            }
            final long cpuTime = TimeUnit.MILLISECONDS.convert(entry.getValue() - startTime, TimeUnit.NANOSECONDS);
            ProfileTree tree;
            if (requestThreadIds.contains(entry.getKey())) {
                tree = this.getProfileTree(ThreadType.BasicThreadType.REQUEST);
            }
            else if (backgroundThreadIds.contains(entry.getKey())) {
                tree = this.getProfileTree(ThreadType.BasicThreadType.BACKGROUND);
            }
            else if (agentThreadIds.contains(entry.getKey())) {
                tree = this.getProfileTree(ThreadType.BasicThreadType.AGENT);
            }
            else {
                tree = this.getProfileTree(ThreadType.BasicThreadType.OTHER);
            }
            tree.incrementCpuTime(cpuTime);
        }
        final int stackCount = this.getCallSiteCount();
        final String msg = MessageFormat.format("Profile size is {0} stack elements", stackCount);
        Agent.LOG.info(msg);
        if (stackCount > 60000) {
            Agent.LOG.info(MessageFormat.format("Trimmed profile size by {0} stack elements", this.trim(stackCount - 60000, stackCount)));
        }
    }
    
    public void markInstrumentedMethods() {
        try {
            this.doMarkInstrumentedMethods();
        }
        catch (Throwable ex) {
            final String msg = MessageFormat.format("Error marking instrumented methods {0}", ex);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg, ex);
            }
            else {
                Agent.LOG.finer(msg);
            }
        }
    }
    
    private void doMarkInstrumentedMethods() {
        final Class<?>[] allLoadedClasses = (Class<?>[])ServiceFactory.getAgent().getInstrumentation().getAllLoadedClasses();
        final Map<String, Class<?>> classMap = (Map<String, Class<?>>)Maps.newHashMap();
        for (final Class<?> clazz : allLoadedClasses) {
            classMap.put(clazz.getName(), clazz);
        }
        for (final ProfileTree tree : this.profileTrees.values()) {
            tree.setMethodDetails(classMap);
        }
    }
    
    public int trimBy(final int limit) {
        return this.trim(limit, this.getCallSiteCount());
    }
    
    private int trim(final int limit, final int stackCount) {
        final ProfileSegmentSort[] segments = this.getSortedSegments(stackCount);
        int count = 0;
        for (final ProfileSegmentSort segment : segments) {
            if (count >= limit) {
                break;
            }
            segment.remove();
            ++count;
        }
        return count;
    }
    
    private ProfileSegmentSort[] getSortedSegments(final int stackCount) {
        final ProfileSegmentSort[] segments = new ProfileSegmentSort[stackCount];
        int index = 0;
        for (final ProfileTree profileTree : this.profileTrees.values()) {
            for (final ProfileSegment rootSegment : profileTree.getRootSegments()) {
                index = this.addSegment(rootSegment, null, 1, segments, index);
            }
        }
        Arrays.sort(segments);
        return segments;
    }
    
    private int addSegment(final ProfileSegment segment, final ProfileSegment parent, int depth, final ProfileSegmentSort[] segments, int index) {
        final ProfileSegmentSort segSort = new ProfileSegmentSort(segment, parent, depth);
        segments[index++] = segSort;
        for (final ProfileSegment child : segment.getChildren()) {
            index = this.addSegment(child, segment, ++depth, segments, index);
        }
        return index;
    }
    
    private int getCallSiteCount() {
        int count = 0;
        for (final ProfileTree profileTree : this.profileTrees.values()) {
            count += profileTree.getCallSiteCount();
        }
        return count;
    }
    
    public Long getProfileId() {
        return this.profilerParameters.getProfileId();
    }
    
    public ProfilerParameters getProfilerParameters() {
        return this.profilerParameters;
    }
    
    public void beforeSampling() {
        ++this.sampleCount;
    }
    
    public int getSampleCount() {
        return this.sampleCount;
    }
    
    public final long getStartTimeMillis() {
        return this.startTimeMillis;
    }
    
    public final long getEndTimeMillis() {
        return this.endTimeMillis;
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        if (this.profilerParameters.getXraySessionId() == null) {
            JSONArray.writeJSONString(Arrays.asList(this.profilerParameters.getProfileId(), this.startTimeMillis, this.endTimeMillis, this.sampleCount, this.compressedData(out), this.totalThreadCount, this.runnableThreadCount), out);
        }
        else {
            JSONArray.writeJSONString(Arrays.asList(this.profilerParameters.getProfileId(), this.startTimeMillis, this.endTimeMillis, this.sampleCount, this.compressedData(out), this.totalThreadCount, this.runnableThreadCount, this.profilerParameters.getXraySessionId()), out);
        }
    }
    
    private String compressedData(final Writer out) {
        String result = DataSenderWriter.getJsonifiedCompressedEncodedString(this.profileTrees, out, 1);
        for (int maxStack = 60000; result.length() > 1000000 && maxStack > 0; result = DataSenderWriter.getJsonifiedCompressedEncodedString(this.profileTrees, out, 1)) {
            maxStack -= 10000;
            final int stackCount = this.getCallSiteCount();
            this.trim(stackCount - maxStack, stackCount);
        }
        if (DataSenderWriter.isCompressingWriter(out)) {
            final String msg = MessageFormat.format("Profile serialized size = {0} bytes", result.length());
            Agent.LOG.info(msg);
        }
        return result;
    }
    
    private void incrementThreadCounts(final boolean runnable) {
        ++this.totalThreadCount;
        if (runnable) {
            ++this.runnableThreadCount;
        }
    }
    
    private boolean shouldScrubStack(final ThreadType type) {
        return !ThreadType.BasicThreadType.AGENT.equals(type) && !this.profilerParameters.isProfileAgentThreads();
    }
    
    public void addStackTrace(final long threadId, final boolean runnable, final ThreadType type, final StackTraceElement... stackTrace) {
        if (stackTrace.length < 2) {
            return;
        }
        this.incrementThreadCounts(runnable);
        List<StackTraceElement> stackTraceList;
        if (this.shouldScrubStack(type)) {
            stackTraceList = StackTraces.scrubAndTruncate(Arrays.asList(stackTrace), 0);
        }
        else {
            stackTraceList = Arrays.asList(stackTrace);
        }
        final List<StackTraceElement> result = new ArrayList<StackTraceElement>(stackTraceList);
        Collections.reverse(result);
        this.getProfileTree(type).addStackTrace(result, runnable);
    }
    
    private static class ProfileSegmentSort implements Comparable<ProfileSegmentSort>
    {
        private final ProfileSegment segment;
        private final ProfileSegment parent;
        private final int depth;
        
        private ProfileSegmentSort(final ProfileSegment segment, final ProfileSegment parent, final int depth) {
            this.segment = segment;
            this.parent = parent;
            this.depth = depth;
        }
        
        void remove() {
            if (this.parent != null) {
                this.parent.removeChild(this.segment.getMethod());
            }
        }
        
        public String toString() {
            return this.segment.toString();
        }
        
        public int compareTo(final ProfileSegmentSort other) {
            final int thisCount = this.segment.getRunnableCallCount();
            final int otherCount = other.segment.getRunnableCallCount();
            if (thisCount == otherCount) {
                return (this.depth > other.depth) ? -1 : ((this.depth == other.depth) ? 0 : 1);
            }
            return (thisCount > otherCount) ? 1 : -1;
        }
    }
}

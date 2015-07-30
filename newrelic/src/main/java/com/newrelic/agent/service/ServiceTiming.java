// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service;

import java.util.TreeSet;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.logging.IAgentLogger;
import java.util.Set;
import java.util.Map;
import java.util.Comparator;

public class ServiceTiming
{
    private static final Comparator<ServiceNameAndTime> serviceNameComparator;
    private static final Map<ServiceNameAndType, Long> serviceTimings;
    private static final Set<ServiceNameAndTime> serviceInitializationTimings;
    private static final Set<ServiceNameAndTime> serviceStartTimings;
    private static volatile long endTimeInNanos;
    
    public static void addServiceInitialization(final String serviceName) {
        if (serviceName == null) {
            return;
        }
        ServiceTiming.serviceTimings.put(new ServiceNameAndType(serviceName, Type.initialization), System.nanoTime());
    }
    
    public static void addServiceStart(final String serviceName) {
        if (serviceName == null) {
            return;
        }
        ServiceTiming.serviceTimings.put(new ServiceNameAndType(serviceName, Type.start), System.nanoTime());
    }
    
    public static void setEndTime() {
        ServiceTiming.endTimeInNanos = System.nanoTime();
    }
    
    public static void logServiceTimings(final IAgentLogger logger) {
        final boolean startupTimingEnabled = ServiceFactory.getConfigService().getDefaultAgentConfig().isStartupTimingEnabled();
        if (!startupTimingEnabled || logger == null || ServiceTiming.endTimeInNanos == 0L) {
            ServiceTiming.serviceTimings.clear();
            return;
        }
        ServiceNameAndType previousServiceNameAndType = null;
        Long previousServiceTime = null;
        for (final Map.Entry<ServiceNameAndType, Long> entry : ServiceTiming.serviceTimings.entrySet()) {
            if (previousServiceNameAndType == null) {
                previousServiceNameAndType = entry.getKey();
                previousServiceTime = entry.getValue();
            }
            else {
                final long serviceTime = entry.getValue() - previousServiceTime;
                if (previousServiceNameAndType.type == Type.initialization) {
                    ServiceTiming.serviceInitializationTimings.add(new ServiceNameAndTime(previousServiceNameAndType.serviceName, serviceTime));
                }
                else {
                    ServiceTiming.serviceStartTimings.add(new ServiceNameAndTime(previousServiceNameAndType.serviceName, serviceTime));
                }
                previousServiceNameAndType = entry.getKey();
                previousServiceTime = entry.getValue();
            }
        }
        if (previousServiceNameAndType != null && previousServiceTime != null) {
            final long serviceTime2 = ServiceTiming.endTimeInNanos - previousServiceTime;
            if (previousServiceNameAndType.type == Type.initialization) {
                ServiceTiming.serviceInitializationTimings.add(new ServiceNameAndTime(previousServiceNameAndType.serviceName, serviceTime2));
            }
            else {
                ServiceTiming.serviceStartTimings.add(new ServiceNameAndTime(previousServiceNameAndType.serviceName, serviceTime2));
            }
        }
        for (final ServiceNameAndTime entry2 : ServiceTiming.serviceInitializationTimings) {
            logger.log(Level.FINEST, "Service Initialization Timing: {0}:{1}ns", new Object[] { entry2.serviceName, entry2.time });
        }
        for (final ServiceNameAndTime entry2 : ServiceTiming.serviceStartTimings) {
            logger.log(Level.FINEST, "Service Start Timing: {0}:{1}ns", new Object[] { entry2.serviceName, entry2.time });
        }
        ServiceTiming.serviceTimings.clear();
    }
    
    public static Set<ServiceNameAndTime> getServiceInitializationTimings() {
        return ServiceTiming.serviceInitializationTimings;
    }
    
    public static Set<ServiceNameAndTime> getServiceStartTimings() {
        return ServiceTiming.serviceStartTimings;
    }
    
    static {
        serviceNameComparator = new Comparator<ServiceNameAndTime>() {
            public int compare(final ServiceNameAndTime service1, final ServiceNameAndTime service2) {
                return service1.serviceName.compareTo(service2.serviceName);
            }
        };
        serviceTimings = new LinkedHashMap<ServiceNameAndType, Long>();
        serviceInitializationTimings = new TreeSet<ServiceNameAndTime>(ServiceTiming.serviceNameComparator);
        serviceStartTimings = new TreeSet<ServiceNameAndTime>(ServiceTiming.serviceNameComparator);
        ServiceTiming.endTimeInNanos = 0L;
    }
    
    public static class ServiceNameAndTime
    {
        private final String serviceName;
        private final Long time;
        
        public ServiceNameAndTime(final String serviceName, final Long time) {
            this.serviceName = serviceName;
            this.time = time;
        }
        
        public String getServiceName() {
            return this.serviceName;
        }
        
        public Long getTime() {
            return this.time;
        }
        
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ServiceNameAndTime that = (ServiceNameAndTime)o;
            return this.serviceName.equals(that.serviceName) && this.time.equals(that.time);
        }
        
        public int hashCode() {
            int result = this.serviceName.hashCode();
            result = 31 * result + this.time.hashCode();
            return result;
        }
    }
    
    private static class ServiceNameAndType
    {
        private final String serviceName;
        private final Type type;
        
        public ServiceNameAndType(final String serviceName, final Type type) {
            this.serviceName = serviceName;
            this.type = type;
        }
        
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ServiceNameAndType that = (ServiceNameAndType)o;
            return this.serviceName.equals(that.serviceName) && this.type == that.type;
        }
        
        public int hashCode() {
            int result = this.serviceName.hashCode();
            result = 31 * result + this.type.hashCode();
            return result;
        }
    }
    
    private enum Type
    {
        initialization, 
        start;
    }
}

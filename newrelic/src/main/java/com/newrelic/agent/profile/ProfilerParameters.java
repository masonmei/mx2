// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

public class ProfilerParameters
{
    private final Long profileId;
    private final Long samplePeriodInMillis;
    private final Long durationInMillis;
    private final boolean onlyRunnableThreads;
    private final boolean onlyRequestThreads;
    private final boolean profileAgentCode;
    private final String keyTransaction;
    private final Long xraySessionId;
    private final String appName;
    
    public ProfilerParameters(final Long profileId, final long samplePeriodInMillis, final long durationInMillis, final boolean onlyRunnableThreads, final boolean onlyRequestThreads, final boolean profileAgentCode, final String keyTransaction, final Long xraySessionId, final String appName) {
        this.profileId = profileId;
        this.samplePeriodInMillis = samplePeriodInMillis;
        this.durationInMillis = durationInMillis;
        this.onlyRunnableThreads = onlyRunnableThreads;
        this.onlyRequestThreads = onlyRequestThreads;
        this.profileAgentCode = profileAgentCode;
        this.keyTransaction = keyTransaction;
        this.xraySessionId = xraySessionId;
        this.appName = appName;
    }
    
    public Long getProfileId() {
        return this.profileId;
    }
    
    public Long getSamplePeriodInMillis() {
        return this.samplePeriodInMillis;
    }
    
    public Long getDurationInMillis() {
        return this.durationInMillis;
    }
    
    public boolean isRunnablesOnly() {
        return this.onlyRunnableThreads;
    }
    
    public boolean isOnlyRequestThreads() {
        return this.onlyRequestThreads;
    }
    
    public boolean isProfileAgentThreads() {
        return this.profileAgentCode;
    }
    
    public String getKeyTransaction() {
        return this.keyTransaction;
    }
    
    public Long getXraySessionId() {
        return this.xraySessionId;
    }
    
    public String getAppName() {
        return this.appName;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((this.xraySessionId == null) ? result : (31 * result + this.xraySessionId.hashCode()));
        result = ((this.profileId == null) ? result : (31 * result + this.profileId.hashCode()));
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final ProfilerParameters other = (ProfilerParameters)obj;
        if (this.xraySessionId == null) {
            return other.xraySessionId == null && this.profileId == (long)other.profileId;
        }
        return other.xraySessionId != null && this.xraySessionId == (long)other.xraySessionId;
    }
}

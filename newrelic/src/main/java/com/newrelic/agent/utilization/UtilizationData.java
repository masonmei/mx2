// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.utilization;

import java.util.HashMap;
import java.util.Map;

public class UtilizationData
{
    private static final String METADATA_VERSION_KEY = "metadata_version";
    private static final String LOGICAL_CORES_KEY = "logical_processors";
    private static final String RAM_KEY = "total_ram_mib";
    private static final String HOSTNAME_KEY = "hostname";
    private static final String VENDORS_KEY = "vendors";
    private static final String AWS = "aws";
    private static final String AWS_INSTANCE_ID_KEY = "id";
    private static final String AWS_INSTANCE_TYPE_KEY = "type";
    private static final String AWS_ZONE_KEY = "zone";
    private static final String DOCKER = "docker";
    private static final String DOCKER_ID_KEY = "id";
    public static final UtilizationData EMPTY;
    private final String hostname;
    private final String dockerContainerId;
    private final String awsInstanceType;
    private final String awsInstanceId;
    private final String awsZone;
    private final Integer logicalProcessorCount;
    private final long total_ram_mib;
    
    public UtilizationData(final String host, final int logicalProcessorCt, final String dockerId, final AWS.AwsData awsData, final long ram_in_mib) {
        this.hostname = host;
        this.logicalProcessorCount = logicalProcessorCt;
        this.dockerContainerId = dockerId;
        this.awsInstanceId = awsData.getInstanceId();
        this.awsInstanceType = awsData.getInstanceType();
        this.awsZone = awsData.getAvailabityZone();
        this.total_ram_mib = ram_in_mib;
    }
    
    public Map<String, Object> map() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("metadata_version", 1);
        data.put("logical_processors", this.logicalProcessorCount);
        data.put("total_ram_mib", this.total_ram_mib);
        data.put("hostname", this.hostname);
        final Map<String, Object> vendors = new HashMap<String, Object>();
        if (this.awsInstanceId != null) {
            final Map<String, String> aws = new HashMap<String, String>();
            aws.put("id", this.awsInstanceId);
            aws.put("type", this.awsInstanceType);
            aws.put("zone", this.awsZone);
            vendors.put("aws", aws);
        }
        if (this.dockerContainerId != null) {
            final Map<String, String> docker = new HashMap<String, String>();
            docker.put("id", this.dockerContainerId);
            vendors.put("docker", docker);
        }
        if (!vendors.isEmpty()) {
            data.put("vendors", vendors);
        }
        return data;
    }
    
    static {
        EMPTY = new UtilizationData(null, 0, null, com.newrelic.agent.utilization.AWS.AwsData.EMPTY_DATA, 0L);
    }
}

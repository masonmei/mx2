// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

public final class TracerFlags
{
    public static final int GENERATE_SCOPED_METRIC = 2;
    public static final int TRANSACTION_TRACER_SEGMENT = 4;
    public static final int DISPATCHER = 8;
    public static final int CUSTOM = 16;
    public static final int LEAF = 32;
    
    public static boolean isDispatcher(final int flags) {
        return (flags & 0x8) == 0x8;
    }
    
    public static boolean isCustom(final int flags) {
        return (flags & 0x10) == 0x10;
    }
    
    public static int clearSegment(final int flags) {
        return flags & 0xFFFFFFFB;
    }
}

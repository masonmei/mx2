// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import com.newrelic.agent.service.Service;

public interface SqlTraceService extends Service
{
    SqlTracerListener getSqlTracerListener(String p0);
}

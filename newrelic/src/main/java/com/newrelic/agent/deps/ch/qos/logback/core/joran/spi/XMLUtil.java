// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import java.net.URL;

public class XMLUtil
{
    public static final int ILL_FORMED = 1;
    public static final int UNRECOVERABLE_ERROR = 2;
    
    public static int checkIfWellFormed(final URL url, final StatusManager sm) {
        return 0;
    }
}

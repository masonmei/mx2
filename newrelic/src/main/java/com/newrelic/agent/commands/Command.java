// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

import java.util.Map;
import com.newrelic.agent.IRPMService;

public interface Command
{
    String getName();
    
    Map process(IRPMService p0, Map p1) throws CommandException;
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

public interface CommandLineParser
{
    CommandLine parse(Options p0, String[] p1) throws ParseException;
    
    CommandLine parse(Options p0, String[] p1, boolean p2) throws ParseException;
}

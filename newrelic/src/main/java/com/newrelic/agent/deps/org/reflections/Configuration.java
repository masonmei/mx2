// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections;

import com.newrelic.agent.deps.org.reflections.serializers.Serializer;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.base.Predicate;
import com.newrelic.agent.deps.org.reflections.adapters.MetadataAdapter;
import java.net.URL;
import com.newrelic.agent.deps.org.reflections.scanners.Scanner;
import java.util.Set;

public interface Configuration
{
    Set<Scanner> getScanners();
    
    Set<URL> getUrls();
    
    MetadataAdapter getMetadataAdapter();
    
    @Nullable
    Predicate<String> getInputsFilter();
    
    ExecutorService getExecutorService();
    
    Serializer getSerializer();
    
    @Nullable
    ClassLoader[] getClassLoaders();
}

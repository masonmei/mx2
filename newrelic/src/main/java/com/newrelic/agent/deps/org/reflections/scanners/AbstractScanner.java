// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.scanners;

import com.newrelic.agent.deps.org.reflections.adapters.MetadataAdapter;
import com.newrelic.agent.deps.org.reflections.ReflectionsException;
import com.newrelic.agent.deps.org.reflections.vfs.Vfs;
import com.newrelic.agent.deps.com.google.common.base.Predicates;
import com.newrelic.agent.deps.com.google.common.base.Predicate;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import com.newrelic.agent.deps.org.reflections.Configuration;

public abstract class AbstractScanner implements Scanner
{
    private Configuration configuration;
    private Multimap<String, String> store;
    private Predicate<String> resultFilter;
    
    public AbstractScanner() {
        this.resultFilter = Predicates.alwaysTrue();
    }
    
    public boolean acceptsInput(final String file) {
        return this.getMetadataAdapter().acceptsInput(file);
    }
    
    public Object scan(final Vfs.File file, Object classObject) {
        if (classObject == null) {
            try {
                classObject = this.configuration.getMetadataAdapter().getOfCreateClassObject(file);
            }
            catch (Exception e) {
                throw new ReflectionsException("could not create class object from file " + file.getRelativePath());
            }
        }
        this.scan(classObject);
        return classObject;
    }
    
    public abstract void scan(final Object p0);
    
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    public Multimap<String, String> getStore() {
        return this.store;
    }
    
    public void setStore(final Multimap<String, String> store) {
        this.store = store;
    }
    
    public Predicate<String> getResultFilter() {
        return this.resultFilter;
    }
    
    public void setResultFilter(final Predicate<String> resultFilter) {
        this.resultFilter = resultFilter;
    }
    
    public Scanner filterResultsBy(final Predicate<String> filter) {
        this.setResultFilter(filter);
        return this;
    }
    
    public boolean acceptResult(final String fqn) {
        return fqn != null && this.resultFilter.apply(fqn);
    }
    
    protected MetadataAdapter getMetadataAdapter() {
        return this.configuration.getMetadataAdapter();
    }
    
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass());
    }
    
    public int hashCode() {
        return this.getClass().hashCode();
    }
}

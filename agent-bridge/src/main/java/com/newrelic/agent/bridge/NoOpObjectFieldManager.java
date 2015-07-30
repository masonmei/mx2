// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

class NoOpObjectFieldManager implements ObjectFieldManager
{
    public void initializeFields(final String className, final Object target, final Object fieldContainer) {
    }
    
    public Object getFieldContainer(final String className, final Object target) {
        return null;
    }
    
    public void createClassObjectFields(final String className) {
    }
}

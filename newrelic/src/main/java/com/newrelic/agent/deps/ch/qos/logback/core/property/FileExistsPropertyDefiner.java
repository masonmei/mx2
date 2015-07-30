// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.property;

import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.PropertyDefinerBase;

public class FileExistsPropertyDefiner extends PropertyDefinerBase
{
    String path;
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
    
    public String getPropertyValue() {
        if (this.path == null) {
            return "false";
        }
        final File file = new File(this.path);
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {
            return "true";
        }
        return "false";
    }
}

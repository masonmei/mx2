// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.net.URLDecoder;
import java.util.Collection;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import java.net.URL;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class ConfigurationWatchList extends ContextAwareBase
{
    URL mainURL;
    List<File> fileWatchList;
    List<Long> lastModifiedList;
    
    public ConfigurationWatchList() {
        this.fileWatchList = new ArrayList<File>();
        this.lastModifiedList = new ArrayList<Long>();
    }
    
    public void clear() {
        this.mainURL = null;
        this.lastModifiedList.clear();
        this.fileWatchList.clear();
    }
    
    public void setMainURL(final URL mainURL) {
        this.mainURL = mainURL;
        if (mainURL != null) {
            this.addAsFileToWatch(mainURL);
        }
    }
    
    private void addAsFileToWatch(final URL url) {
        final File file = this.convertToFile(url);
        if (file != null) {
            this.fileWatchList.add(file);
            this.lastModifiedList.add(file.lastModified());
        }
    }
    
    public void addToWatchList(final URL url) {
        this.addAsFileToWatch(url);
    }
    
    public URL getMainURL() {
        return this.mainURL;
    }
    
    public List<File> getCopyOfFileWatchList() {
        return new ArrayList<File>(this.fileWatchList);
    }
    
    public boolean changeDetected() {
        for (int len = this.fileWatchList.size(), i = 0; i < len; ++i) {
            final long lastModified = this.lastModifiedList.get(i);
            final File file = this.fileWatchList.get(i);
            if (lastModified != file.lastModified()) {
                return true;
            }
        }
        return false;
    }
    
    File convertToFile(final URL url) {
        final String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            return new File(URLDecoder.decode(url.getFile()));
        }
        this.addInfo("URL [" + url + "] is not of type file");
        return null;
    }
}

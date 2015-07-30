// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import java.net.URI;
import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.util.Loader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.FileInputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import java.io.InputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEventRecorder;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;

public class IncludeAction extends Action
{
    private static final String INCLUDED_TAG = "included";
    private static final String FILE_ATTR = "file";
    private static final String URL_ATTR = "url";
    private static final String RESOURCE_ATTR = "resource";
    private String attributeInUse;
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) throws ActionException {
        final SaxEventRecorder recorder = new SaxEventRecorder();
        this.attributeInUse = null;
        if (!this.checkAttributes(attributes)) {
            return;
        }
        final InputStream in = this.getInputStream(ec, attributes);
        try {
            if (in != null) {
                this.parseAndRecord(in, recorder);
                this.trimHeadAndTail(recorder);
                ec.getJoranInterpreter().getEventPlayer().addEventsDynamically(recorder.saxEventList, 2);
            }
        }
        catch (JoranException e) {
            this.addError("Error while parsing  " + this.attributeInUse, e);
        }
        finally {
            this.close(in);
        }
    }
    
    void close(final InputStream in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException ex) {}
        }
    }
    
    private boolean checkAttributes(final Attributes attributes) {
        final String fileAttribute = attributes.getValue("file");
        final String urlAttribute = attributes.getValue("url");
        final String resourceAttribute = attributes.getValue("resource");
        int count = 0;
        if (!OptionHelper.isEmpty(fileAttribute)) {
            ++count;
        }
        if (!OptionHelper.isEmpty(urlAttribute)) {
            ++count;
        }
        if (!OptionHelper.isEmpty(resourceAttribute)) {
            ++count;
        }
        if (count == 0) {
            this.addError("One of \"path\", \"resource\" or \"url\" attributes must be set.");
            return false;
        }
        if (count > 1) {
            this.addError("Only one of \"file\", \"url\" or \"resource\" attributes should be set.");
            return false;
        }
        if (count == 1) {
            return true;
        }
        throw new IllegalStateException("Count value [" + count + "] is not expected");
    }
    
    private InputStream getInputStreamByFilePath(final String pathToFile) {
        try {
            return new FileInputStream(pathToFile);
        }
        catch (IOException ioe) {
            final String errMsg = "File [" + pathToFile + "] does not exist.";
            this.addError(errMsg, ioe);
            return null;
        }
    }
    
    URL attributeToURL(final String urlAttribute) {
        try {
            return new URL(urlAttribute);
        }
        catch (MalformedURLException mue) {
            final String errMsg = "URL [" + urlAttribute + "] is not well formed.";
            this.addError(errMsg, mue);
            return null;
        }
    }
    
    private InputStream getInputStreamByUrl(final URL url) {
        return this.openURL(url);
    }
    
    InputStream openURL(final URL url) {
        try {
            return url.openStream();
        }
        catch (IOException e) {
            final String errMsg = "Failed to open [" + url.toString() + "]";
            this.addError(errMsg, e);
            return null;
        }
    }
    
    URL resourceAsURL(final String resourceAttribute) {
        final URL url = Loader.getResourceBySelfClassLoader(resourceAttribute);
        if (url == null) {
            final String errMsg = "Could not find resource corresponding to [" + resourceAttribute + "]";
            this.addError(errMsg);
            return null;
        }
        return url;
    }
    
    URL filePathAsURL(final String path) {
        final URI uri = new File(path).toURI();
        try {
            return uri.toURL();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private InputStream getInputStreamByResource(final URL url) {
        return this.openURL(url);
    }
    
    URL getInputURL(final InterpretationContext ec, final Attributes attributes) {
        final String fileAttribute = attributes.getValue("file");
        final String urlAttribute = attributes.getValue("url");
        final String resourceAttribute = attributes.getValue("resource");
        if (!OptionHelper.isEmpty(fileAttribute)) {
            this.attributeInUse = ec.subst(fileAttribute);
            return this.filePathAsURL(this.attributeInUse);
        }
        if (!OptionHelper.isEmpty(urlAttribute)) {
            this.attributeInUse = ec.subst(urlAttribute);
            return this.attributeToURL(this.attributeInUse);
        }
        if (!OptionHelper.isEmpty(resourceAttribute)) {
            this.attributeInUse = ec.subst(resourceAttribute);
            return this.resourceAsURL(this.attributeInUse);
        }
        throw new IllegalStateException("A URL stream should have been returned");
    }
    
    InputStream getInputStream(final InterpretationContext ec, final Attributes attributes) {
        final URL inputURL = this.getInputURL(ec, attributes);
        if (inputURL == null) {
            return null;
        }
        ConfigurationWatchListUtil.addToWatchList(this.context, inputURL);
        return this.openURL(inputURL);
    }
    
    private void trimHeadAndTail(final SaxEventRecorder recorder) {
        final List<SaxEvent> saxEventList = recorder.saxEventList;
        if (saxEventList.size() == 0) {
            return;
        }
        final SaxEvent first = saxEventList.get(0);
        if (first != null && first.qName.equalsIgnoreCase("included")) {
            saxEventList.remove(0);
        }
        final SaxEvent last = saxEventList.get(recorder.saxEventList.size() - 1);
        if (last != null && last.qName.equalsIgnoreCase("included")) {
            saxEventList.remove(recorder.saxEventList.size() - 1);
        }
    }
    
    private void parseAndRecord(final InputStream inputSource, final SaxEventRecorder recorder) throws JoranException {
        recorder.setContext(this.context);
        recorder.recordEvents(inputSource);
    }
    
    public void end(final InterpretationContext ec, final String name) throws ActionException {
    }
}

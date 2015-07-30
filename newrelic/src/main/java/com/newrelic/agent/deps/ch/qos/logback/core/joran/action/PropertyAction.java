// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import java.util.Properties;
import java.net.URL;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.util.Loader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;

public class PropertyAction extends Action
{
    static final String RESOURCE_ATTRIBUTE = "resource";
    static String INVALID_ATTRIBUTES;
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) {
        if ("substitutionProperty".equals(localName)) {
            this.addWarn("[substitutionProperty] element has been deprecated. Please use the [property] element instead.");
        }
        final String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        final String scopeStr = attributes.getValue("scope");
        final ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        if (this.checkFileAttributeSanity(attributes)) {
            String file = attributes.getValue("file");
            file = ec.subst(file);
            try {
                final FileInputStream istream = new FileInputStream(file);
                this.loadAndSetProperties(ec, istream, scope);
            }
            catch (FileNotFoundException e3) {
                this.addError("Could not find properties file [" + file + "].");
            }
            catch (IOException e1) {
                this.addError("Could not read properties file [" + file + "].", e1);
            }
        }
        else if (this.checkResourceAttributeSanity(attributes)) {
            String resource = attributes.getValue("resource");
            resource = ec.subst(resource);
            final URL resourceURL = Loader.getResourceBySelfClassLoader(resource);
            if (resourceURL == null) {
                this.addError("Could not find resource [" + resource + "].");
            }
            else {
                try {
                    final InputStream istream2 = resourceURL.openStream();
                    this.loadAndSetProperties(ec, istream2, scope);
                }
                catch (IOException e2) {
                    this.addError("Could not read resource file [" + resource + "].", e2);
                }
            }
        }
        else if (this.checkValueNameAttributesSanity(attributes)) {
            value = RegularEscapeUtil.basicEscape(value);
            value = value.trim();
            value = ec.subst(value);
            ActionUtil.setProperty(ec, name, value, scope);
        }
        else {
            this.addError(PropertyAction.INVALID_ATTRIBUTES);
        }
    }
    
    void loadAndSetProperties(final InterpretationContext ec, final InputStream istream, final ActionUtil.Scope scope) throws IOException {
        final Properties props = new Properties();
        props.load(istream);
        istream.close();
        ActionUtil.setProperties(ec, props, scope);
    }
    
    boolean checkFileAttributeSanity(final Attributes attributes) {
        final String file = attributes.getValue("file");
        final String name = attributes.getValue("name");
        final String value = attributes.getValue("value");
        final String resource = attributes.getValue("resource");
        return !OptionHelper.isEmpty(file) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(resource);
    }
    
    boolean checkResourceAttributeSanity(final Attributes attributes) {
        final String file = attributes.getValue("file");
        final String name = attributes.getValue("name");
        final String value = attributes.getValue("value");
        final String resource = attributes.getValue("resource");
        return !OptionHelper.isEmpty(resource) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file);
    }
    
    boolean checkValueNameAttributesSanity(final Attributes attributes) {
        final String file = attributes.getValue("file");
        final String name = attributes.getValue("name");
        final String value = attributes.getValue("value");
        final String resource = attributes.getValue("resource");
        return !OptionHelper.isEmpty(name) && !OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file) && OptionHelper.isEmpty(resource);
    }
    
    public void end(final InterpretationContext ec, final String name) {
    }
    
    public void finish(final InterpretationContext ec) {
    }
    
    static {
        PropertyAction.INVALID_ATTRIBUTES = "In <property> element, either the \"file\" attribute alone, or the \"resource\" element alone, or both the \"name\" and \"value\" attributes must be set.";
    }
}

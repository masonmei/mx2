// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import com.newrelic.agent.extension.dom.ExtensionDomParser;
import java.util.Map;
import java.io.InputStream;
import com.newrelic.agent.deps.org.yaml.snakeyaml.Yaml;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.BaseConstructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.Loader;
import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Constructor;
import java.util.List;

public class ExtensionParsers
{
    private final ExtensionParser yamlParser;
    private final ExtensionParser xmlParser;
    
    public ExtensionParsers(final List<ConfigurationConstruct> constructs) {
        final Constructor constructor = new Constructor() {
            {
                for (final ConfigurationConstruct construct : constructs) {
                    this.yamlConstructors.put(construct.getName(), construct);
                }
            }
        };
        final Loader loader = new Loader(constructor);
        final Yaml yaml = new Yaml(loader);
        this.yamlParser = new ExtensionParser() {
            public Extension parse(final ClassLoader classloader, final InputStream inputStream, final boolean custom) throws Exception {
                final Object config = yaml.load(inputStream);
                if (config instanceof Map) {
                    return new YamlExtension(classloader, (Map<String, Object>)config, custom);
                }
                throw new Exception("Invalid yaml extension");
            }
        };
        this.xmlParser = new ExtensionParser() {
            public Extension parse(final ClassLoader classloader, final InputStream inputStream, final boolean custom) throws Exception {
                final com.newrelic.agent.extension.beans.Extension ext = ExtensionDomParser.readFile(inputStream);
                return new XmlExtension(this.getClass().getClassLoader(), ext.getName(), ext, custom);
            }
        };
    }
    
    public ExtensionParser getParser(final String fileName) {
        if (fileName.endsWith(".yml")) {
            return this.yamlParser;
        }
        return this.xmlParser;
    }
    
    public ExtensionParser getXmlParser() {
        return this.xmlParser;
    }
    
    public ExtensionParser getYamlParser() {
        return this.yamlParser;
    }
    
    public interface ExtensionParser
    {
        Extension parse(ClassLoader p0, InputStream p1, boolean p2) throws Exception;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import java.util.Collection;
import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.newrelic.agent.config.AgentConfig;
import java.util.Iterator;
import java.util.HashSet;
import java.util.LinkedList;
import com.newrelic.agent.logging.IAgentLogger;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.List;

public class ClassNameFilter
{
    private static final String EXCLUDES_FILE = "/META-INF/excludes";
    private final List<Pattern> excludePatterns;
    private final List<Pattern> includePatterns;
    private volatile Set<String> includeClasses;
    private final IAgentLogger logger;
    
    public ClassNameFilter(final IAgentLogger logger) {
        this.excludePatterns = new LinkedList<Pattern>();
        this.includePatterns = new LinkedList<Pattern>();
        this.includeClasses = new HashSet<String>();
        this.logger = logger;
    }
    
    public boolean isExcluded(final String className) {
        for (final Pattern pattern : this.excludePatterns) {
            if (pattern.matcher(className).matches()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isIncluded(final String className) {
        if (this.includeClasses.contains(className)) {
            return true;
        }
        for (final Pattern pattern : this.includePatterns) {
            if (pattern.matcher(className).matches()) {
                return true;
            }
        }
        return false;
    }
    
    public void addConfigClassFilters(final AgentConfig config) {
        final Set<String> excludes = config.getClassTransformerConfig().getExcludes();
        if (excludes.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Exclude class name filters:");
            for (final String exclude : excludes) {
                sb.append("\n").append(exclude);
                this.addExclude(exclude);
            }
            this.logger.finer(sb.toString());
        }
        final Set<String> includes = config.getClassTransformerConfig().getIncludes();
        for (final String include : includes) {
            this.addInclude(include);
        }
    }
    
    public void addExcludeFileClassFilters() {
        final InputStream iStream = this.getClass().getResourceAsStream("/META-INF/excludes");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        final List<String> excludeList = new LinkedList<String>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    excludeList.add(line);
                }
            }
        }
        catch (IOException ex) {
            this.logger.severe(MessageFormat.format("Unable to read the class excludes file at {0} found within the New Relic jar.", "/META-INF/excludes"));
        }
        finally {
            try {
                iStream.close();
            }
            catch (IOException ex2) {}
        }
        for (final String exclude : excludeList) {
            this.addExclude(exclude);
        }
        this.logger.finer("Excludes initialized: " + excludeList);
    }
    
    public void addInclude(final String include) {
        if (this.isRegex(include)) {
            this.addIncludeRegex(include);
        }
        else {
            this.addIncludeClass(include);
        }
    }
    
    public void addIncludeClass(final String className) {
        final String regex = this.classNameToRegex(className);
        this.addIncludeRegex(regex);
    }
    
    public void addIncludeRegex(final String regex) {
        final Pattern pattern = this.regexToPattern(regex);
        if (pattern != null) {
            this.includePatterns.add(pattern);
        }
    }
    
    public void addExclude(final String exclude) {
        if (this.isRegex(exclude)) {
            this.addExcludeRegex(exclude);
        }
        else {
            this.addExcludeClass(exclude);
        }
    }
    
    public void addExcludeClass(final String className) {
        final String regex = this.classNameToRegex(className);
        this.addExcludeRegex(regex);
    }
    
    public void addExcludeRegex(final String regex) {
        final Pattern pattern = this.regexToPattern(regex);
        if (pattern != null) {
            this.excludePatterns.add(pattern);
        }
    }
    
    private String classNameToRegex(final String className) {
        return "^" + className.replace("$", "\\$") + "$";
    }
    
    private Pattern regexToPattern(final String regex) {
        try {
            return Pattern.compile(regex);
        }
        catch (Exception e) {
            this.logger.severe(MessageFormat.format("Unable to compile pattern: {0}", regex));
            return null;
        }
    }
    
    private boolean isRegex(final String value) {
        return value.indexOf(42) >= 0 || value.indexOf(124) >= 0 || value.indexOf(94) >= 0;
    }
    
    public void addClassMatcherIncludes(final Collection<ClassMatcher> classMatchers) {
        final Set<String> classNames = new HashSet<String>();
        classNames.addAll(this.includeClasses);
        for (final ClassMatcher classMatcher : classMatchers) {
            for (final String className : classMatcher.getClassNames()) {
                classNames.add(className);
            }
        }
        this.logger.finer("Class name inclusions: " + classNames);
        this.includeClasses = classNames;
    }
}

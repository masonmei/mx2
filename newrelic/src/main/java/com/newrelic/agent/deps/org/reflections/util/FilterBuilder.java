// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.util;

import java.util.regex.Pattern;
import com.newrelic.agent.deps.org.reflections.ReflectionsException;
import java.util.ArrayList;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.base.Joiner;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;
import com.newrelic.agent.deps.com.google.common.base.Predicate;

public class FilterBuilder implements Predicate<String>
{
    private final List<Predicate<String>> chain;
    
    public FilterBuilder() {
        this.chain = (List<Predicate<String>>)Lists.newArrayList();
    }
    
    private FilterBuilder(final Iterable<Predicate<String>> filters) {
        this.chain = (List<Predicate<String>>)Lists.newArrayList((Iterable<?>)filters);
    }
    
    public FilterBuilder include(final String regex) {
        return this.add(new Include(regex));
    }
    
    public FilterBuilder exclude(final String regex) {
        this.add(new Exclude(regex));
        return this;
    }
    
    public FilterBuilder add(final Predicate<String> filter) {
        this.chain.add(filter);
        return this;
    }
    
    public FilterBuilder includePackage(final Class<?> aClass) {
        return this.add(new Include(packageNameRegex(aClass)));
    }
    
    public FilterBuilder excludePackage(final Class<?> aClass) {
        return this.add(new Exclude(packageNameRegex(aClass)));
    }
    
    public FilterBuilder includePackage(final String... prefixes) {
        final int len$ = prefixes.length;
        final int i$ = 0;
        if (i$ < len$) {
            final String prefix = prefixes[i$];
            return this.add(new Include(prefix(prefix)));
        }
        return this;
    }
    
    public FilterBuilder excludePackage(final String prefix) {
        return this.add(new Exclude(prefix(prefix)));
    }
    
    private static String packageNameRegex(final Class<?> aClass) {
        return prefix(aClass.getPackage().getName() + ".");
    }
    
    public static String prefix(final String qualifiedName) {
        return qualifiedName.replace(".", "\\.") + ".*";
    }
    
    public String toString() {
        return Joiner.on(", ").join(this.chain);
    }
    
    public boolean apply(final String regex) {
        boolean accept = this.chain == null || this.chain.isEmpty() || this.chain.get(0) instanceof Exclude;
        if (this.chain != null) {
            for (final Predicate<String> filter : this.chain) {
                if (accept && filter instanceof Include) {
                    continue;
                }
                if (!accept && filter instanceof Exclude) {
                    continue;
                }
                accept = filter.apply(regex);
                if (!accept && filter instanceof Exclude) {
                    break;
                }
            }
        }
        return accept;
    }
    
    public static FilterBuilder parse(final String includeExcludeString) {
        final List<Predicate<String>> filters = new ArrayList<Predicate<String>>();
        if (!Utils.isEmpty(includeExcludeString)) {
            for (final String string : includeExcludeString.split(",")) {
                final String trimmed = string.trim();
                final char prefix = trimmed.charAt(0);
                final String pattern = trimmed.substring(1);
                Predicate<String> filter = null;
                switch (prefix) {
                    case '+': {
                        filter = new Include(pattern);
                        break;
                    }
                    case '-': {
                        filter = new Exclude(pattern);
                        break;
                    }
                    default: {
                        throw new ReflectionsException("includeExclude should start with either + or -");
                    }
                }
                filters.add(filter);
            }
            return new FilterBuilder(filters);
        }
        return new FilterBuilder();
    }
    
    public static FilterBuilder parsePackages(final String includeExcludeString) {
        final List<Predicate<String>> filters = new ArrayList<Predicate<String>>();
        if (!Utils.isEmpty(includeExcludeString)) {
            for (final String string : includeExcludeString.split(",")) {
                final String trimmed = string.trim();
                final char prefix = trimmed.charAt(0);
                String pattern = trimmed.substring(1);
                if (!pattern.endsWith(".")) {
                    pattern += ".";
                }
                pattern = prefix(pattern);
                Predicate<String> filter = null;
                switch (prefix) {
                    case '+': {
                        filter = new Include(pattern);
                        break;
                    }
                    case '-': {
                        filter = new Exclude(pattern);
                        break;
                    }
                    default: {
                        throw new ReflectionsException("includeExclude should start with either + or -");
                    }
                }
                filters.add(filter);
            }
            return new FilterBuilder(filters);
        }
        return new FilterBuilder();
    }
    
    public abstract static class Matcher implements Predicate<String>
    {
        final Pattern pattern;
        
        public Matcher(final String regex) {
            this.pattern = Pattern.compile(regex);
        }
        
        public abstract boolean apply(final String p0);
        
        public String toString() {
            return this.pattern.pattern();
        }
    }
    
    public static class Include extends Matcher
    {
        public Include(final String patternString) {
            super(patternString);
        }
        
        public boolean apply(final String regex) {
            return this.pattern.matcher(regex).matches();
        }
        
        public String toString() {
            return "+" + super.toString();
        }
    }
    
    public static class Exclude extends Matcher
    {
        public Exclude(final String patternString) {
            super(patternString);
        }
        
        public boolean apply(final String regex) {
            return !this.pattern.matcher(regex).matches();
        }
        
        public String toString() {
            return "-" + super.toString();
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import com.newrelic.agent.deps.com.google.common.base.Joiner;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.regex.Pattern;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

public class Strings
{
    public static final String NEWRELIC_DEPENDENCY_INTERNAL_PACKAGE_PREFIX = "com/newrelic/agent/deps/";
    private static final String NEWRELIC_DEPENDENCY_PACKAGE_PREFIX = "com.newrelic.agent.deps.";
    
    public static boolean isBlank(final String str) {
        return str == null || str.length() == 0;
    }
    
    public static Collection<String> trim(final Collection<String> strings) {
        final Collection<String> trimmedList = new ArrayList<String>(strings.size());
        for (final String string : strings) {
            trimmedList.add(string.trim());
        }
        return trimmedList;
    }
    
    public static String unquoteDatabaseName(final String s) {
        final int index = s.indexOf(46);
        if (index > 0) {
            return new StringBuilder(s.length()).append(unquote(s.substring(0, index))).append('.').append(unquote(s.substring(index + 1))).toString();
        }
        return unquote(s);
    }
    
    public static String join(final char delimiter, final String... strings) {
        if (strings.length == 0) {
            return null;
        }
        if (strings.length == 1) {
            return strings[0];
        }
        int length = strings.length - 1;
        for (final String s : strings) {
            length += s.length();
        }
        final StringBuilder sb = new StringBuilder(length);
        sb.append(strings[0]);
        for (int i = 1; i < strings.length; ++i) {
            if (!strings[i].isEmpty()) {
                sb.append(delimiter).append(strings[i]);
            }
        }
        return sb.toString();
    }
    
    public static String join(final String... strings) {
        if (strings.length == 0) {
            return null;
        }
        if (strings.length == 1) {
            return strings[0];
        }
        int length = 0;
        for (final String s : strings) {
            length += s.length();
        }
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < strings.length; ++i) {
            if (!strings[i].isEmpty()) {
                sb.append(strings[i]);
            }
        }
        return sb.toString();
    }
    
    public static String[] split(final String string, final String delimiter) {
        final StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
        final List<String> segments = new ArrayList<String>(4);
        while (tokenizer.hasMoreTokens()) {
            segments.add(tokenizer.nextToken());
        }
        return segments.toArray(new String[segments.size()]);
    }
    
    public static String unquote(final String string) {
        if (string == null || string.length() < 2) {
            return string;
        }
        final char first = string.charAt(0);
        final char last = string.charAt(string.length() - 1);
        if (first != last || (first != '\"' && first != '\'' && first != '`')) {
            return string;
        }
        return string.substring(1, string.length() - 1);
    }
    
    public static boolean isEmpty(final String string) {
        return string == null || string.length() == 0;
    }
    
    public static String fixClassName(final String className) {
        return trimName(className, "com.newrelic.agent.deps.");
    }
    
    private static String trimName(final String fullName, final String prefix) {
        if (fullName.startsWith(prefix)) {
            return fullName.substring(prefix.length());
        }
        return fullName;
    }
    
    public static String fixInternalClassName(String className) {
        className = className.replace('.', '/');
        return trimName(className, "com/newrelic/agent/deps/");
    }
    
    public static String getGlobPattern(final String glob) {
        final StringBuilder b = new StringBuilder().append('^');
        for (final char c : glob.toCharArray()) {
            switch (c) {
                case '.': {
                    b.append("\\.");
                    break;
                }
                case '/': {
                    b.append("\\/");
                    break;
                }
                case '*': {
                    b.append(".*");
                    break;
                }
                default: {
                    b.append(c);
                    break;
                }
            }
        }
        return b.toString();
    }
    
    public static Pattern getPatternFromGlobs(final List<String> globs) {
        final List<String> patterns = (List<String>)Lists.newArrayListWithCapacity(globs.size());
        for (final String glob : globs) {
            patterns.add('(' + getGlobPattern(glob) + ')');
        }
        final String pattern = Joiner.on('|').join(patterns);
        return Pattern.compile(pattern);
    }
}

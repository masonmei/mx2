// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class TypeHandler
{
    public static Object createValue(final String str, final Object obj) throws ParseException {
        return createValue(str, (Class)obj);
    }
    
    public static Object createValue(final String str, final Class clazz) throws ParseException {
        if (PatternOptionBuilder.STRING_VALUE == clazz) {
            return str;
        }
        if (PatternOptionBuilder.OBJECT_VALUE == clazz) {
            return createObject(str);
        }
        if (PatternOptionBuilder.NUMBER_VALUE == clazz) {
            return createNumber(str);
        }
        if (PatternOptionBuilder.DATE_VALUE == clazz) {
            return createDate(str);
        }
        if (PatternOptionBuilder.CLASS_VALUE == clazz) {
            return createClass(str);
        }
        if (PatternOptionBuilder.FILE_VALUE == clazz) {
            return createFile(str);
        }
        if (PatternOptionBuilder.EXISTING_FILE_VALUE == clazz) {
            return createFile(str);
        }
        if (PatternOptionBuilder.FILES_VALUE == clazz) {
            return createFiles(str);
        }
        if (PatternOptionBuilder.URL_VALUE == clazz) {
            return createURL(str);
        }
        return null;
    }
    
    public static Object createObject(final String classname) throws ParseException {
        Class cl = null;
        try {
            cl = Class.forName(classname);
        }
        catch (ClassNotFoundException cnfe) {
            throw new ParseException("Unable to find the class: " + classname);
        }
        Object instance = null;
        try {
            instance = cl.newInstance();
        }
        catch (Exception e) {
            throw new ParseException(e.getClass().getName() + "; Unable to create an instance of: " + classname);
        }
        return instance;
    }
    
    public static Number createNumber(final String str) throws ParseException {
        try {
            if (str.indexOf(46) != -1) {
                return Double.valueOf(str);
            }
            return Long.valueOf(str);
        }
        catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }
    
    public static Class createClass(final String classname) throws ParseException {
        try {
            return Class.forName(classname);
        }
        catch (ClassNotFoundException e) {
            throw new ParseException("Unable to find the class: " + classname);
        }
    }
    
    public static Date createDate(final String str) throws ParseException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public static URL createURL(final String str) throws ParseException {
        try {
            return new URL(str);
        }
        catch (MalformedURLException e) {
            throw new ParseException("Unable to parse the URL: " + str);
        }
    }
    
    public static File createFile(final String str) throws ParseException {
        return new File(str);
    }
    
    public static File[] createFiles(final String str) throws ParseException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

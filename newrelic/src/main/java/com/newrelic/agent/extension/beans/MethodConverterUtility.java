// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.beans;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.regex.Pattern;

class MethodConverterUtility
{
    private static final String BEGIN_PARENTH_FOR_DESCRIPTOR = "(";
    private static final String END_PARENTH_FOR_DESCRIPTOR = ")";
    private static final String ARRAY_NOTATIOM = "[";
    private static final Pattern ARRAY_PATTERN;
    private static final Pattern BRACKETS;
    private static final String COLLECTION_TYPE_REGEX = "<.+?>";
    
    private static String convertParamToDescriptorFormat(final String inputParam) {
        if (inputParam == null) {
            throw new RuntimeException("The input parameter can not be null.");
        }
        Type paramType;
        if (Type.BOOLEAN_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.BOOLEAN_TYPE;
        }
        else if (Type.BYTE_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.BYTE_TYPE;
        }
        else if (Type.CHAR_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.CHAR_TYPE;
        }
        else if (Type.DOUBLE_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.DOUBLE_TYPE;
        }
        else if (Type.FLOAT_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.FLOAT_TYPE;
        }
        else if (Type.INT_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.INT_TYPE;
        }
        else if (Type.LONG_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.LONG_TYPE;
        }
        else if (Type.SHORT_TYPE.getClassName().equals(inputParam)) {
            paramType = Type.SHORT_TYPE;
        }
        else {
            final Matcher arrayMatcher = MethodConverterUtility.ARRAY_PATTERN.matcher(inputParam);
            if (arrayMatcher.matches()) {
                final String typeName = arrayMatcher.group(1);
                final String brackets = arrayMatcher.group(2);
                return makeArrayType(typeName, brackets);
            }
            if (inputParam.contains("[")) {
                throw new RuntimeException("Brackets should only be in the parameter name if it is an array. Name: " + inputParam);
            }
            final String output = inputParam.replace(".", "/").replaceAll("<.+?>", "");
            paramType = Type.getObjectType(output);
        }
        return paramType.getDescriptor();
    }
    
    private static String makeArrayType(final String paramType, final String brackets) {
        final Matcher mms = MethodConverterUtility.BRACKETS.matcher(brackets);
        int count = 0;
        while (mms.find()) {
            ++count;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            sb.append("[");
        }
        sb.append(convertParamToDescriptorFormat(paramType));
        return sb.toString();
    }
    
    protected static String paramNamesToParamDescriptor(final List<String> inputParameters) {
        if (inputParameters == null) {
            return "()";
        }
        final List<String> descriptors = new ArrayList<String>();
        for (final String param : inputParameters) {
            descriptors.add(convertParamToDescriptorFormat(param.trim()));
        }
        return convertToParmDescriptor(descriptors);
    }
    
    private static String convertToParmDescriptor(final List<String> paramDescriptors) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (paramDescriptors != null && !paramDescriptors.isEmpty()) {
            for (final String param : paramDescriptors) {
                if (Type.getType(param) == null) {
                    throw new RuntimeException("The generated parameter descriptor is invalid. Name: " + param);
                }
                sb.append(param);
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    static {
        ARRAY_PATTERN = Pattern.compile("(.+?)((\\[\\])+)\\z");
        BRACKETS = Pattern.compile("(\\[\\])");
    }
}

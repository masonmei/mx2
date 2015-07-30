// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

public class CoreConstants
{
    public static final String LINE_SEPARATOR;
    public static final int LINE_SEPARATOR_LEN;
    public static final String CODES_URL = "http://logback.qos.ch/codes.html";
    public static final String DEFAULT_CONTEXT_NAME = "default";
    public static final String PATTERN_RULE_REGISTRY = "PATTERN_RULE_REGISTRY";
    public static final String ISO8601_STR = "ISO8601";
    public static final String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
    public static final String DAILY_DATE_PATTERN = "yyyy-MM-dd";
    public static final String CLF_DATE_PATTERN = "dd/MM/yyyy:HH:mm:ss Z";
    public static final String EVALUATOR_MAP = "EVALUATOR_MAP";
    public static final String VALUE_OF = "valueOf";
    public static final String EMPTY_STRING = "";
    public static final String[] EMPTY_STRING_ARRAY;
    public static final Class<?>[] EMPTY_CLASS_ARRAY;
    public static final String CAUSED_BY = "Caused by: ";
    public static final String SUPPRESSED = "\tSuppressed: ";
    public static final String WRAPPED_BY = "Wrapped by: ";
    public static final char PERCENT_CHAR = '%';
    public static final char LEFT_PARENTHESIS_CHAR = '(';
    public static final char RIGHT_PARENTHESIS_CHAR = ')';
    public static final char ESCAPE_CHAR = '\\';
    public static final char CURLY_LEFT = '{';
    public static final char CURLY_RIGHT = '}';
    public static final char COMMA_CHAR = ',';
    public static final char DOUBLE_QUOTE_CHAR = '\"';
    public static final char SINGLE_QUOTE_CHAR = '\'';
    public static final char COLON_CHAR = ':';
    public static final char DASH_CHAR = '-';
    public static final int TABLE_ROW_LIMIT = 10000;
    public static final int OOS_RESET_FREQUENCY = 70;
    public static long REFERENCE_BIPS;
    public static final int MAX_ERROR_COUNT = 4;
    public static final char DOT = '.';
    public static final char TAB = '\t';
    public static final char DOLLAR = '$';
    public static final String SEE_FNP_NOT_SET = "See also http://logback.qos.ch/codes.html#tbr_fnp_not_set";
    public static final String CONFIGURATION_WATCH_LIST = "CONFIGURATION_WATCH_LIST";
    public static final String CONFIGURATION_WATCH_LIST_RESET = "CONFIGURATION_WATCH_LIST_RESET";
    public static final String SAFE_JORAN_CONFIGURATION = "SAFE_JORAN_CONFIGURATION";
    public static final String XML_PARSING = "XML_PARSING";
    public static final String HOSTNAME_KEY = "HOSTNAME";
    public static final String CONTEXT_NAME_KEY = "CONTEXT_NAME";
    public static final int BYTES_PER_INT = 4;
    public static final int MILLIS_IN_ONE_SECOND = 1000;
    public static final int MILLIS_IN_ONE_MINUTE = 60000;
    public static final int MILLIS_IN_ONE_HOUR = 3600000;
    public static final int MILLIS_IN_ONE_DAY = 86400000;
    public static final int MILLIS_IN_ONE_WEEK = 604800000;
    public static final String CONTEXT_SCOPE_VALUE = "context";
    public static final String RESET_MSG_PREFIX = "Will reset and reconfigure context ";
    public static final String JNDI_COMP_PREFIX = "java:comp/env";
    public static final String UNDEFINED_PROPERTY_SUFFIX = "_IS_UNDEFINED";
    public static final String LEFT_ACCOLADE;
    public static final String RIGHT_ACCOLADE;
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
        LINE_SEPARATOR_LEN = CoreConstants.LINE_SEPARATOR.length();
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_CLASS_ARRAY = new Class[0];
        CoreConstants.REFERENCE_BIPS = 9000L;
        LEFT_ACCOLADE = new String(new char[] { '{' });
        RIGHT_ACCOLADE = new String(new char[] { '}' });
    }
}

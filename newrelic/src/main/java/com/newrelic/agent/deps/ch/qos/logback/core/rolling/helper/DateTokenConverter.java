// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import com.newrelic.agent.deps.ch.qos.logback.core.util.DatePatternToRegexUtil;
import java.util.Date;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.util.CachingDateFormatter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.DynamicConverter;

public class DateTokenConverter<E> extends DynamicConverter<E> implements MonoTypedConverter
{
    public static final String CONVERTER_KEY = "d";
    public static final String AUXILIARY_TOKEN = "AUX";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private String datePattern;
    private CachingDateFormatter cdf;
    private boolean primary;
    
    public DateTokenConverter() {
        this.primary = true;
    }
    
    public void start() {
        this.datePattern = this.getFirstOption();
        if (this.datePattern == null) {
            this.datePattern = "yyyy-MM-dd";
        }
        final List<String> optionList = this.getOptionList();
        if (optionList != null && optionList.size() > 1) {
            final String secondOption = optionList.get(1);
            if ("AUX".equalsIgnoreCase(secondOption)) {
                this.primary = false;
            }
        }
        this.cdf = new CachingDateFormatter(this.datePattern);
    }
    
    public String convert(final Date date) {
        return this.cdf.format(date.getTime());
    }
    
    public String convert(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Date) {
            return this.convert((Date)o);
        }
        throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
    }
    
    public String getDatePattern() {
        return this.datePattern;
    }
    
    public boolean isApplicable(final Object o) {
        return o instanceof Date;
    }
    
    public String toRegex() {
        final DatePatternToRegexUtil datePatternToRegexUtil = new DatePatternToRegexUtil(this.datePattern);
        return datePatternToRegexUtil.toRegex();
    }
    
    public boolean isPrimary() {
        return this.primary;
    }
}

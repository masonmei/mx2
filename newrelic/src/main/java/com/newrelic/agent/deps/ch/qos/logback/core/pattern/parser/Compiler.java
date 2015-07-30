// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser;

import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.DynamicConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.CompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.LiteralConverter;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.Converter;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

class Compiler<E> extends ContextAwareBase
{
    Converter<E> head;
    Converter<E> tail;
    final Node top;
    final Map converterMap;
    
    Compiler(final Node top, final Map converterMap) {
        this.top = top;
        this.converterMap = converterMap;
    }
    
    Converter<E> compile() {
        final Converter<E> converter = null;
        this.tail = converter;
        this.head = converter;
        for (Node n = this.top; n != null; n = n.next) {
            switch (n.type) {
                case 0: {
                    this.addToList(new LiteralConverter<E>((String)n.getValue()));
                    break;
                }
                case 2: {
                    final CompositeNode cn = (CompositeNode)n;
                    final CompositeConverter<E> compositeConverter = this.createCompositeConverter(cn);
                    if (compositeConverter == null) {
                        this.addError("Failed to create converter for [%" + cn.getValue() + "] keyword");
                        this.addToList(new LiteralConverter<E>("%PARSER_ERROR[" + cn.getValue() + "]"));
                        break;
                    }
                    compositeConverter.setFormattingInfo(cn.getFormatInfo());
                    compositeConverter.setOptionList(cn.getOptions());
                    final Compiler<E> childCompiler = new Compiler<E>(cn.getChildNode(), this.converterMap);
                    childCompiler.setContext(this.context);
                    final Converter<E> childConverter = childCompiler.compile();
                    compositeConverter.setChildConverter(childConverter);
                    this.addToList(compositeConverter);
                    break;
                }
                case 1: {
                    final SimpleKeywordNode kn = (SimpleKeywordNode)n;
                    final DynamicConverter<E> dynaConverter = this.createConverter(kn);
                    if (dynaConverter != null) {
                        dynaConverter.setFormattingInfo(kn.getFormatInfo());
                        dynaConverter.setOptionList(kn.getOptions());
                        this.addToList(dynaConverter);
                        break;
                    }
                    final Converter<E> errConveter = new LiteralConverter<E>("%PARSER_ERROR[" + kn.getValue() + "]");
                    this.addStatus(new ErrorStatus("[" + kn.getValue() + "] is not a valid conversion word", this));
                    this.addToList(errConveter);
                    break;
                }
            }
        }
        return this.head;
    }
    
    private void addToList(final Converter<E> c) {
        if (this.head == null) {
            this.tail = c;
            this.head = c;
        }
        else {
            this.tail.setNext(c);
            this.tail = c;
        }
    }
    
    DynamicConverter<E> createConverter(final SimpleKeywordNode kn) {
        final String keyword = (String)kn.getValue();
        final String converterClassStr = this.converterMap.get(keyword);
        if (converterClassStr != null) {
            try {
                return (DynamicConverter<E>)OptionHelper.instantiateByClassName(converterClassStr, DynamicConverter.class, this.context);
            }
            catch (Exception e) {
                this.addError("Failed to instantiate converter class [" + converterClassStr + "] for keyword [" + keyword + "]", e);
                return null;
            }
        }
        this.addError("There is no conversion class registered for conversion word [" + keyword + "]");
        return null;
    }
    
    CompositeConverter<E> createCompositeConverter(final CompositeNode cn) {
        final String keyword = (String)cn.getValue();
        final String converterClassStr = this.converterMap.get(keyword);
        if (converterClassStr != null) {
            try {
                return (CompositeConverter<E>)OptionHelper.instantiateByClassName(converterClassStr, CompositeConverter.class, this.context);
            }
            catch (Exception e) {
                this.addError("Failed to instantiate converter class [" + converterClassStr + "] as a composite converter for keyword [" + keyword + "]", e);
                return null;
            }
        }
        this.addError("There is no conversion class registered for composite conversion word [" + keyword + "]");
        return null;
    }
}

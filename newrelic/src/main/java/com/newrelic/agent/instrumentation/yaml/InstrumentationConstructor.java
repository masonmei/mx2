// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.yaml;

import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.AllMethodsMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.AndClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.NotMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.SequenceNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import java.util.Iterator;
import java.util.Arrays;
import com.newrelic.agent.extension.ConfigurationConstruct;
import java.util.Collection;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Constructor;

public class InstrumentationConstructor extends Constructor
{
    public final Collection<ConfigurationConstruct> constructs;
    
    public InstrumentationConstructor() {
        this.constructs = Arrays.asList(new ConstructClassMethodNameFormatDescriptor(), new ConstructChildClassMatcher(), new ConstructNotClassMatcher(), new ConstructAndClassMatcher(), new ConstructOrClassMatcher(), new ConstructExactClassMatcher(), new ConstructInterfaceMatcher(), new ConstructAllMethodsMatcher(), new ConstructOrMethodMatcher(), new ConstructExactMethodMatcher(), new ConstructInstanceMethodMatcher(), new ConstructStaticMethodMatcher());
        for (final ConfigurationConstruct c : this.constructs) {
            this.yamlConstructors.put(c.getName(), c);
        }
    }
    
    private class ConstructClassMethodNameFormatDescriptor extends ConfigurationConstruct
    {
        public ConstructClassMethodNameFormatDescriptor() {
            super("!class_method_metric_name_format");
        }
        
        public Object construct(final Node node) {
            final String prefix = (String)InstrumentationConstructor.this.constructScalar((ScalarNode)node);
            return new PointCutFactory.ClassMethodNameFormatDescriptor(prefix, false);
        }
    }
    
    private class ConstructChildClassMatcher extends ConfigurationConstruct
    {
        public ConstructChildClassMatcher() {
            super("!child_class_matcher");
        }
        
        public Object construct(final Node node) {
            final String val = (String)InstrumentationConstructor.this.constructScalar((ScalarNode)node);
            return new ChildClassMatcher(val);
        }
    }
    
    private class ConstructNotClassMatcher extends ConfigurationConstruct
    {
        public ConstructNotClassMatcher() {
            super("!not_class_matcher");
        }
        
        public Object construct(final Node node) {
            final List args = InstrumentationConstructor.this.constructSequence((SequenceNode)node);
            return new NotMatcher(PointCutFactory.getClassMatcher(args.get(0)));
        }
    }
    
    private class ConstructAndClassMatcher extends ConfigurationConstruct
    {
        public ConstructAndClassMatcher() {
            super("!and_class_matcher");
        }
        
        public Object construct(final Node node) {
            final List args = InstrumentationConstructor.this.constructSequence((SequenceNode)node);
            return new AndClassMatcher(PointCutFactory.getClassMatchers(args));
        }
    }
    
    private class ConstructOrClassMatcher extends ConfigurationConstruct
    {
        public ConstructOrClassMatcher() {
            super("!or_class_matcher");
        }
        
        public Object construct(final Node node) {
            final List args = InstrumentationConstructor.this.constructSequence((SequenceNode)node);
            return OrClassMatcher.getClassMatcher(PointCutFactory.getClassMatchers(args));
        }
    }
    
    private class ConstructExactClassMatcher extends ConfigurationConstruct
    {
        public ConstructExactClassMatcher() {
            super("!exact_class_matcher");
        }
        
        public Object construct(final Node node) {
            final String val = (String)InstrumentationConstructor.this.constructScalar((ScalarNode)node);
            return new ExactClassMatcher(val);
        }
    }
    
    private class ConstructInterfaceMatcher extends ConfigurationConstruct
    {
        public ConstructInterfaceMatcher() {
            super("!interface_matcher");
        }
        
        public Object construct(final Node node) {
            final String val = (String)InstrumentationConstructor.this.constructScalar((ScalarNode)node);
            return new InterfaceMatcher(val);
        }
    }
    
    private class ConstructAllMethodsMatcher extends ConfigurationConstruct
    {
        public ConstructAllMethodsMatcher() {
            super("!all_methods_matcher");
        }
        
        public Object construct(final Node node) {
            return new AllMethodsMatcher();
        }
    }
    
    private class ConstructOrMethodMatcher extends ConfigurationConstruct
    {
        public ConstructOrMethodMatcher() {
            super("!or_method_matcher");
        }
        
        public Object construct(final Node node) {
            final List args = InstrumentationConstructor.this.constructSequence((SequenceNode)node);
            return OrMethodMatcher.getMethodMatcher(PointCutFactory.getMethodMatchers(args));
        }
    }
    
    private class ConstructExactMethodMatcher extends ConfigurationConstruct
    {
        public ConstructExactMethodMatcher() {
            super("!exact_method_matcher");
        }
        
        public Object construct(final Node node) {
            final List args = InstrumentationConstructor.this.constructSequence((SequenceNode)node);
            final List methodDescriptors = args.subList(1, args.size());
            return PointCutFactory.createExactMethodMatcher(args.get(0), methodDescriptors);
        }
    }
    
    private class ConstructInstanceMethodMatcher extends ConfigurationConstruct
    {
        public ConstructInstanceMethodMatcher() {
            super("!instance_method_matcher");
        }
        
        public Object construct(final Node node) {
            final List args = InstrumentationConstructor.this.constructSequence((SequenceNode)node);
            return PointCutFactory.getMethodMatcher(args.get(0));
        }
    }
    
    private class ConstructStaticMethodMatcher extends ConfigurationConstruct
    {
        public ConstructStaticMethodMatcher() {
            super("!static_method_matcher");
        }
        
        public Object construct(final Node node) {
            final List args = InstrumentationConstructor.this.constructSequence((SequenceNode)node);
            return PointCutFactory.getMethodMatcher(args.get(0));
        }
    }
}

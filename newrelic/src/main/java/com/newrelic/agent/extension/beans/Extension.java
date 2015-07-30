// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.beans;

import javax.xml.bind.annotation.XmlValue;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "instrumentation" })
@XmlRootElement(name = "extension")
public class Extension
{
    protected Instrumentation instrumentation;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "enabled")
    protected Boolean enabled;
    @XmlAttribute(name = "version")
    protected Double version;
    
    public Instrumentation getInstrumentation() {
        return this.instrumentation;
    }
    
    public void setInstrumentation(final Instrumentation value) {
        this.instrumentation = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public boolean isEnabled() {
        return this.enabled == null || this.enabled;
    }
    
    public void setEnabled(final Boolean value) {
        this.enabled = value;
    }
    
    public double getVersion() {
        if (this.version == null) {
            return 1.0;
        }
        return this.version;
    }
    
    public void setVersion(final Double value) {
        this.version = value;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "pointcut" })
    public static class Instrumentation
    {
        protected List<Pointcut> pointcut;
        @XmlAttribute(name = "metricPrefix")
        protected String metricPrefix;
        
        public List<Pointcut> getPointcut() {
            if (this.pointcut == null) {
                this.pointcut = new ArrayList<Pointcut>();
            }
            return this.pointcut;
        }
        
        public String getMetricPrefix() {
            return this.metricPrefix;
        }
        
        public void setMetricPrefix(final String value) {
            this.metricPrefix = value;
        }
        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "nameTransaction", "methodAnnotation", "className", "interfaceName", "method" })
        public static class Pointcut
        {
            protected NameTransaction nameTransaction;
            protected String methodAnnotation;
            protected ClassName className;
            protected String interfaceName;
            protected List<Method> method;
            @XmlAttribute(name = "transactionStartPoint")
            protected Boolean transactionStartPoint;
            @XmlAttribute(name = "metricNameFormat")
            protected String metricNameFormat;
            @XmlAttribute(name = "excludeFromTransactionTrace")
            protected Boolean excludeFromTransactionTrace;
            @XmlAttribute(name = "ignoreTransaction")
            protected Boolean ignoreTransaction;
            @XmlAttribute(name = "transactionType")
            protected String transactionType;
            
            public NameTransaction getNameTransaction() {
                return this.nameTransaction;
            }
            
            public void setNameTransaction(final NameTransaction value) {
                this.nameTransaction = value;
            }
            
            public String getMethodAnnotation() {
                return this.methodAnnotation;
            }
            
            public void setMethodAnnotation(final String value) {
                this.methodAnnotation = value;
            }
            
            public ClassName getClassName() {
                return this.className;
            }
            
            public void setClassName(final ClassName value) {
                this.className = value;
            }
            
            public String getInterfaceName() {
                return this.interfaceName;
            }
            
            public void setInterfaceName(final String value) {
                this.interfaceName = value;
            }
            
            public List<Method> getMethod() {
                if (this.method == null) {
                    this.method = new ArrayList<Method>();
                }
                return this.method;
            }
            
            public boolean isTransactionStartPoint() {
                return this.transactionStartPoint != null && this.transactionStartPoint;
            }
            
            public void setTransactionStartPoint(final Boolean value) {
                this.transactionStartPoint = value;
            }
            
            public String getMetricNameFormat() {
                return this.metricNameFormat;
            }
            
            public void setMetricNameFormat(final String value) {
                this.metricNameFormat = value;
            }
            
            public boolean isExcludeFromTransactionTrace() {
                return this.excludeFromTransactionTrace != null && this.excludeFromTransactionTrace;
            }
            
            public void setExcludeFromTransactionTrace(final Boolean value) {
                this.excludeFromTransactionTrace = value;
            }
            
            public boolean isIgnoreTransaction() {
                return this.ignoreTransaction != null && this.ignoreTransaction;
            }
            
            public void setIgnoreTransaction(final Boolean value) {
                this.ignoreTransaction = value;
            }
            
            public String getTransactionType() {
                if (this.transactionType == null) {
                    return "background";
                }
                return this.transactionType;
            }
            
            public void setTransactionType(final String value) {
                this.transactionType = value;
            }
            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = { "value" })
            public static class ClassName
            {
                @XmlValue
                protected String value;
                @XmlAttribute(name = "includeSubclasses")
                protected Boolean includeSubclasses;
                
                public String getValue() {
                    return this.value;
                }
                
                public void setValue(final String value) {
                    this.value = value;
                }
                
                public boolean isIncludeSubclasses() {
                    return this.includeSubclasses != null && this.includeSubclasses;
                }
                
                public void setIncludeSubclasses(final Boolean value) {
                    this.includeSubclasses = value;
                }
            }
            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = { "returnType", "name", "parameters" })
            public static class Method
            {
                protected String returnType;
                protected String name;
                protected Parameters parameters;
                
                public String getReturnType() {
                    return this.returnType;
                }
                
                public void setReturnType(final String value) {
                    this.returnType = value;
                }
                
                public String getName() {
                    return this.name;
                }
                
                public void setName(final String value) {
                    this.name = value;
                }
                
                public Parameters getParameters() {
                    return this.parameters;
                }
                
                public void setParameters(final Parameters value) {
                    this.parameters = value;
                }
                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = { "type" })
                public static class Parameters
                {
                    protected List<Type> type;
                    
                    public List<Type> getType() {
                        if (this.type == null) {
                            this.type = new ArrayList<Type>();
                        }
                        return this.type;
                    }
                    
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = { "value" })
                    public static class Type
                    {
                        @XmlValue
                        protected String value;
                        @XmlAttribute(name = "attributeName")
                        protected String attributeName;
                        
                        public String getValue() {
                            return this.value;
                        }
                        
                        public void setValue(final String value) {
                            this.value = value;
                        }
                        
                        public String getAttributeName() {
                            return this.attributeName;
                        }
                        
                        public void setAttributeName(final String value) {
                            this.attributeName = value;
                        }
                    }
                }
            }
            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class NameTransaction
            {
            }
        }
    }
}

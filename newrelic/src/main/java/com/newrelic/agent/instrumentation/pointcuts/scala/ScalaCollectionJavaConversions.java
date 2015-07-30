// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.service.ServiceFactory;
import java.util.List;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;
import com.newrelic.agent.util.SingleClassLoader;
import com.newrelic.agent.util.MethodCache;

public class ScalaCollectionJavaConversions
{
    private static final String JAVA_CONVERSIONS_CLASS = "scala.collection.JavaConversions";
    private static final String SCALA_MAP_CLASS = "scala.collection.Map";
    private static final String MAP_AS_JAVA_MAP_METHOD_NAME = "mapAsJavaMap";
    private static final String MAP_AS_JAVA_MAP_METHOD_DESC = "(Lscala/collection/Map;)Ljava/util/Map;";
    private static final String SCALA_SEQ_CLASS = "scala.collection.Seq";
    private static final String SEQ_AS_JAVA_LIST_METHOD_NAME = "seqAsJavaList";
    private static final String SEQ_AS_JAVA_LIST_METHOD_DESC = "(Lscala/collection/Seq;)Ljava/util/List;";
    private static final MethodCache mapAsJavaMapCache;
    private static final MethodCache seqAsJavaListCache;
    private static final SingleClassLoader javaConversions;
    private static final SingleClassLoader scalaMap;
    private static final SingleClassLoader scalaSeq;
    
    public static Map asJavaMap(final Object map) {
        try {
            final ClassLoader cl = map.getClass().getClassLoader();
            final Class javaConversionsClass = ScalaCollectionJavaConversions.javaConversions.loadClass(cl);
            final Class scalaMapClass = ScalaCollectionJavaConversions.scalaMap.loadClass(cl);
            final Method m = ScalaCollectionJavaConversions.mapAsJavaMapCache.getDeclaredMethod(javaConversionsClass, scalaMapClass);
            return (Map)m.invoke(null, map);
        }
        catch (Exception e) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Exception converting Scala Map to Java Map: {0}", e);
                Agent.LOG.finer(msg);
            }
            return null;
        }
    }
    
    public static List asJavaList(final Object list) {
        try {
            final ClassLoader cl = list.getClass().getClassLoader();
            final Class javaConversionsClass = ScalaCollectionJavaConversions.javaConversions.loadClass(cl);
            final Class scalaSeqClass = ScalaCollectionJavaConversions.scalaSeq.loadClass(cl);
            final Method m = ScalaCollectionJavaConversions.seqAsJavaListCache.getDeclaredMethod(javaConversionsClass, scalaSeqClass);
            return (List)m.invoke(null, list);
        }
        catch (Exception e) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Exception converting Scala Seq to Java List: {0}", e);
                Agent.LOG.finer(msg);
            }
            return null;
        }
    }
    
    static {
        mapAsJavaMapCache = ServiceFactory.getCacheService().getMethodCache("scala.collection.JavaConversions", "mapAsJavaMap", "(Lscala/collection/Map;)Ljava/util/Map;");
        seqAsJavaListCache = ServiceFactory.getCacheService().getMethodCache("scala.collection.JavaConversions", "seqAsJavaList", "(Lscala/collection/Seq;)Ljava/util/List;");
        javaConversions = ServiceFactory.getCacheService().getSingleClassLoader("scala.collection.JavaConversions");
        scalaMap = ServiceFactory.getCacheService().getSingleClassLoader("scala.collection.Map");
        scalaSeq = ServiceFactory.getCacheService().getSingleClassLoader("scala.collection.Seq");
    }
}

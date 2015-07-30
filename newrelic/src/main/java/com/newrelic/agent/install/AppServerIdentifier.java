// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.install;

import java.io.File;

public class AppServerIdentifier
{
    private static String tomcat5Marker;
    private static String tomcat67Marker;
    private static String jettyMarker;
    private static String jBoss4Marker;
    private static String jBoss56Marker;
    private static String jBoss7Marker;
    private static String glassfish3Marker;
    
    public static AppServerType getAppServerType(final String path) throws Exception {
        return getAppServerType(new File(path));
    }
    
    public static AppServerType getAppServerType(final File rootDir) throws Exception {
        if (!rootDir.exists()) {
            throw new Exception("App server root " + rootDir.toString() + "does not exist on filesystem.");
        }
        if (!rootDir.isDirectory()) {
            throw new Exception("App server root " + rootDir.toString() + "is not a directory.");
        }
        if (isTomcat(rootDir.toString())) {
            return AppServerType.TOMCAT;
        }
        if (isJetty(rootDir.toString())) {
            return AppServerType.JETTY;
        }
        if (isJBoss(rootDir.toString())) {
            return AppServerType.JBOSS;
        }
        if (isJBoss7(rootDir.toString())) {
            return AppServerType.JBOSS7;
        }
        if (isGlassfish(rootDir.toString())) {
            return AppServerType.GLASSFISH;
        }
        return AppServerType.UNKNOWN;
    }
    
    private static boolean isTomcat(final String rootDir) {
        return markerFileExists(rootDir + AppServerIdentifier.tomcat5Marker) || markerFileExists(rootDir + AppServerIdentifier.tomcat67Marker);
    }
    
    private static boolean isJetty(final String rootDir) {
        return markerFileExists(rootDir + AppServerIdentifier.jettyMarker);
    }
    
    private static boolean isJBoss(final String rootDir) {
        return markerFileExists(rootDir + AppServerIdentifier.jBoss4Marker) || markerFileExists(rootDir + AppServerIdentifier.jBoss56Marker);
    }
    
    private static boolean isJBoss7(final String rootDir) {
        return markerFileExists(rootDir + AppServerIdentifier.jBoss7Marker);
    }
    
    private static boolean isGlassfish(final String rootDir) {
        return markerFileExists(rootDir + AppServerIdentifier.glassfish3Marker);
    }
    
    private static boolean markerFileExists(final String path) {
        final File markerFile = new File(path);
        return markerFile.exists() && markerFile.isFile();
    }
    
    static {
        AppServerIdentifier.tomcat5Marker = "/server/lib/catalina.jar";
        AppServerIdentifier.tomcat67Marker = "/lib/catalina.jar";
        AppServerIdentifier.jettyMarker = "/bin/jetty.sh";
        AppServerIdentifier.jBoss4Marker = "/lib/jboss-common.jar";
        AppServerIdentifier.jBoss56Marker = "/lib/jboss-common-core.jar";
        AppServerIdentifier.jBoss7Marker = "/jboss-modules.jar";
        AppServerIdentifier.glassfish3Marker = "/config/domain.xml";
    }
    
    public enum AppServerType
    {
        TOMCAT("Tomcat"), 
        JETTY("Jetty"), 
        JBOSS("JBoss"), 
        JBOSS7("JBoss7"), 
        GLASSFISH("Glassfish"), 
        UNKNOWN("Unknown");
        
        private final String name;
        
        private AppServerType(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}

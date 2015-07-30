// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.install;

import com.newrelic.agent.util.EditableFile;
import java.io.File;

public class JettySelfInstaller extends SelfInstaller
{
    private final String scriptPath = "/bin/jetty";
    private final String altScriptPath = "/bin/jetty-cygwin";
    private final String agentAlreadySet = "^(export )?JAVA_OPTIONS=(.*)\\-javaagent:(.*)newrelic.jar(.*)$";
    private final String winlinLocator = "[# \r\n]+ This is how the Jetty server will be started([ \t]+)?";
    private String rootDir;
    
    public boolean backupAndEditStartScript(final String appServerRootDir) {
        this.rootDir = appServerRootDir;
        boolean result = this.backupAndEdit(appServerRootDir + this.getStartScript());
        final File jettyCygwin = new File(appServerRootDir + this.getAlternateStartScript());
        if (jettyCygwin.exists()) {
            result &= this.backupAndEdit(jettyCygwin.toString());
        }
        return result;
    }
    
    private boolean backupAndEdit(final String fullPathToScript) {
        try {
            final EditableFile file = new EditableFile(fullPathToScript);
            final String fullSwitch = JettySelfInstaller.lineSep + JettySelfInstaller.lineSep + this.getCommentForAgentSwitch(file.comment) + JettySelfInstaller.lineSep + this.getAgentSettings();
            if (!file.contains(this.getAgentAlreadySetExpr())) {
                if (file.contains(this.getLocator())) {
                    this.backup(file);
                    file.insertBeforeLocator(this.getLocator(), fullSwitch, false);
                    System.out.println("Added agent switch to start script " + file.getLocation());
                }
                else {
                    System.out.println("Did not locate Jetty start script. No edit performed");
                }
            }
            else {
                System.out.println("Did not edit start script " + file.getLocation() + " because:");
                System.out.println(" .:. The agent switch is already set");
            }
            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public String getStartScript() {
        String path = "/bin/jetty";
        if (this.osIsWindows) {
            path = path.replaceAll("/", "\\\\");
        }
        return path + ".sh";
    }
    
    public String getAlternateStartScript() {
        String path = "/bin/jetty-cygwin";
        if (this.osIsWindows) {
            path = path.replaceAll("/", "\\\\");
        }
        return path + ".sh";
    }
    
    public String getLocator() {
        return "[# \r\n]+ This is how the Jetty server will be started([ \t]+)?";
    }
    
    public String getAlternateLocator() {
        return this.getLocator();
    }
    
    public String getAgentSettings() {
        String switchPath = this.rootDir;
        if (this.osIsWindows) {
            switchPath = switchPath.replaceAll("\\\\", "/");
        }
        return "NR_JAR=" + switchPath + "/newrelic/newrelic.jar; export NR_JAR" + JettySelfInstaller.lineSep + "JAVA_OPTIONS=\"\\$\\{JAVA_OPTIONS\\} -javaagent:\\$NR_JAR\"; export JAVA_OPTIONS";
    }
    
    public String getAgentAlreadySetExpr() {
        return "^(export )?JAVA_OPTIONS=(.*)\\-javaagent:(.*)newrelic.jar(.*)$";
    }
}

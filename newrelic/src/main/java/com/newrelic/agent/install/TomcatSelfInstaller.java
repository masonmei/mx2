// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.install;

import com.newrelic.agent.util.EditableFile;
import java.io.File;

public class TomcatSelfInstaller extends SelfInstaller
{
    private final String scriptPath = "/bin/catalina";
    private final String altScriptPath = "/bin/catalina.50";
    private final String unixAgentSet = "^(export )?CATALINA_OPTS=(.*)\\-javaagent:(.*)newrelic.jar(.*)$";
    private final String unixLocator = "^(export )?CATALINA_OPTS=(.*)$";
    private final String altUnixLocator = "^# OS specific support(.*)$";
    private final String windowsAgentSet = "^(SET|set) CATALINA_OPTS=%CATALINA_OPTS% \\-javaagent:(.*)$";
    private final String winLocator = "^rem Guess CATALINA_HOME if not defined(.*)$";
    private final String altWinLocator = "^rem Suppress Terminate batch job(.*)$";
    private String rootDir;
    
    public boolean backupAndEditStartScript(final String appServerRootDir) {
        this.rootDir = appServerRootDir;
        boolean result = this.backupAndEdit(appServerRootDir + this.getStartScript());
        final File catalina50 = new File(appServerRootDir + this.getAlternateStartScript());
        if (catalina50.exists()) {
            result &= this.backupAndEdit(catalina50.toString());
        }
        return result;
    }
    
    private boolean backupAndEdit(final String fullPathToScript) {
        try {
            final EditableFile file = new EditableFile(fullPathToScript);
            final String fullSwitch = TomcatSelfInstaller.lineSep + this.getCommentForAgentSwitch(file.comment) + TomcatSelfInstaller.lineSep + this.getAgentSettings();
            if (!file.contains(this.getAgentAlreadySetExpr())) {
                if (file.contains(this.getLocator())) {
                    this.backup(file);
                    if (this.osIsWindows) {
                        file.insertBeforeLocator(this.getLocator(), fullSwitch, true);
                    }
                    else {
                        file.insertAfterLocator(this.getLocator(), fullSwitch, true);
                    }
                    System.out.println("Added agent switch to start script " + file.getLocation());
                }
                else {
                    if (!file.contains(this.getAlternateLocator())) {
                        System.out.println("Did not locate Tomcat start script. No edit performed");
                        return false;
                    }
                    this.backup(file);
                    file.insertBeforeLocator(this.getAlternateLocator(), fullSwitch, true);
                    System.out.println("Added agent switch to start script " + file.getLocation());
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
        String path = "/bin/catalina";
        if (this.osIsWindows) {
            path = path.replaceAll("/", "\\\\");
        }
        return path + (this.osIsWindows ? ".bat" : ".sh");
    }
    
    public String getAlternateStartScript() {
        return "/bin/catalina.50" + (this.osIsWindows ? ".bat" : ".sh");
    }
    
    public String getLocator() {
        return this.osIsWindows ? "^rem Guess CATALINA_HOME if not defined(.*)$" : "^(export )?CATALINA_OPTS=(.*)$";
    }
    
    public String getAlternateLocator() {
        return this.osIsWindows ? "^rem Suppress Terminate batch job(.*)$" : "^# OS specific support(.*)$";
    }
    
    public String getAgentSettings() {
        final String unixSwitch = "NR_JAR=" + this.rootDir + "/newrelic/newrelic.jar; export NR_JAR" + TomcatSelfInstaller.lineSep + "CATALINA_OPTS=\"\\$CATALINA_OPTS -javaagent:\\$NR_JAR\"; export CATALINA_OPTS" + TomcatSelfInstaller.lineSep;
        final String winSwitch = "set CATALINA_OPTS=%CATALINA_OPTS% -javaagent:\"" + this.rootDir.replaceAll("\\\\", "/") + "/newrelic/newrelic.jar\"" + TomcatSelfInstaller.lineSep;
        return this.osIsWindows ? winSwitch : unixSwitch;
    }
    
    public String getAgentAlreadySetExpr() {
        return this.osIsWindows ? "^(SET|set) CATALINA_OPTS=%CATALINA_OPTS% \\-javaagent:(.*)$" : "^(export )?CATALINA_OPTS=(.*)\\-javaagent:(.*)newrelic.jar(.*)$";
    }
}

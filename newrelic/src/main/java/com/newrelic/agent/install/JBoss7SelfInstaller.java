// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.install;

import com.newrelic.agent.util.EditableFile;

public class JBoss7SelfInstaller extends SelfInstaller
{
    private final String scriptPath = "/bin/standalone";
    private final String agentAlreadySet = "(.*)JAVA_OPTS=(.*)\\-javaagent:(.*)newrelic.jar";
    private final String windowsLocator = "rem Setup JBoss specific properties";
    private String rootDir;
    
    public boolean backupAndEditStartScript(final String appServerRootDir) {
        this.rootDir = appServerRootDir;
        boolean result = this.backupAndEdit(appServerRootDir + this.getStartScript());
        result &= this.backupAndEdit(appServerRootDir + this.getAlternateStartScript());
        return result;
    }
    
    private boolean backupAndEdit(final String fullPathToScript) {
        try {
            final EditableFile file = new EditableFile(fullPathToScript);
            final String fullSwitch = this.getCommentForAgentSwitch(file.comment) + JBoss7SelfInstaller.lineSep + this.getAgentSettings();
            if (!file.contains(this.getAgentAlreadySetExpr())) {
                this.backup(file);
                if (this.osIsWindows) {
                    file.insertBeforeLocator(this.getLocator(), fullSwitch + JBoss7SelfInstaller.lineSep, true);
                }
                else {
                    file.append(fullSwitch + JBoss7SelfInstaller.lineSep);
                }
                System.out.println("Added agent switch to start script " + file.getLocation());
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
        String path = "/bin/standalone";
        if (this.osIsWindows) {
            path = path.replaceAll("/", "\\\\");
        }
        return path + (this.osIsWindows ? ".bat" : ".conf");
    }
    
    public String getAlternateStartScript() {
        return this.getStartScript();
    }
    
    public String getLocator() {
        return "rem Setup JBoss specific properties";
    }
    
    public String getAlternateLocator() {
        return this.getLocator();
    }
    
    public String getAgentSettings() {
        final String unixSwitch = "JAVA_OPTS=\"$JAVA_OPTS -javaagent:" + this.rootDir + "/newrelic/newrelic.jar\"" + JBoss7SelfInstaller.lineSep;
        final String windowsSwitch = "set JAVA_OPTS=-javaagent:\"" + this.rootDir.replaceAll("\\\\", "/") + "/newrelic/newrelic.jar\" %JAVA_OPTS%";
        return this.osIsWindows ? windowsSwitch : unixSwitch;
    }
    
    public String getAgentAlreadySetExpr() {
        return "(.*)JAVA_OPTS=(.*)\\-javaagent:(.*)newrelic.jar";
    }
}

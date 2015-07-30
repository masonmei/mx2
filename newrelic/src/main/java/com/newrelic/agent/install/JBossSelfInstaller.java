// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.install;

import com.newrelic.agent.util.EditableFile;

public class JBossSelfInstaller extends SelfInstaller
{
    private final String scriptPath = "/bin/run";
    private final String agentAlreadySet = "(.*)JAVA_OPTS=(.*)\\-javaagent:(.*)newrelic.jar";
    private final String windowsLocator = "set JBOSS_CLASSPATH=%RUN_CLASSPATH%";
    private String rootDir;
    
    public boolean backupAndEditStartScript(final String appServerRootDir) {
        this.rootDir = appServerRootDir;
        return this.backupAndEdit(appServerRootDir + this.getStartScript());
    }
    
    private boolean backupAndEdit(final String fullPathToScript) {
        try {
            final EditableFile file = new EditableFile(fullPathToScript);
            final String fullSwitch = this.getCommentForAgentSwitch(file.comment) + JBossSelfInstaller.lineSep + this.getAgentSettings();
            if (!file.contains(this.getAgentAlreadySetExpr())) {
                this.backup(file);
                if (this.osIsWindows) {
                    file.insertAfterLocator(this.getLocator(), JBossSelfInstaller.lineSep + fullSwitch, true);
                }
                else {
                    file.append(fullSwitch + JBossSelfInstaller.lineSep);
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
        String path = "/bin/run";
        if (this.osIsWindows) {
            path = path.replaceAll("/", "\\\\");
        }
        return path + (this.osIsWindows ? ".bat" : ".conf");
    }
    
    public String getAlternateStartScript() {
        return this.getStartScript();
    }
    
    public String getLocator() {
        return "set JBOSS_CLASSPATH=%RUN_CLASSPATH%";
    }
    
    public String getAlternateLocator() {
        return this.getLocator();
    }
    
    public String getAgentSettings() {
        final String unixSwitch = "JAVA_OPTS=\"$JAVA_OPTS -javaagent:" + this.rootDir + "/newrelic/newrelic.jar\"" + JBossSelfInstaller.lineSep;
        final String windowsSwitch = "set JAVA_OPTS=-javaagent:\"" + this.rootDir.replaceAll("\\\\", "/") + "/newrelic/newrelic.jar\" %JAVA_OPTS%";
        return this.osIsWindows ? windowsSwitch : unixSwitch;
    }
    
    public String getAgentAlreadySetExpr() {
        return "(.*)JAVA_OPTS=(.*)\\-javaagent:(.*)newrelic.jar";
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.HashMap;
import com.newrelic.agent.command.XmlInstrumentOptions;
import com.newrelic.agent.deps.org.apache.commons.cli.Option;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.instrumentation.verifier.InstrumentationVerifier;
import com.newrelic.agent.instrumentation.verifier.VerificationLogger;
import com.newrelic.agent.service.ServiceManager;
import com.newrelic.agent.service.ServiceFactory;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.apache.commons.cli.HelpFormatter;
import com.newrelic.agent.install.SelfInstaller;
import java.io.IOException;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.install.ConfigInstaller;
import com.newrelic.agent.install.SelfInstallerFactory;
import com.newrelic.agent.install.AppServerIdentifier;
import java.io.File;
import com.newrelic.agent.command.XmlInstrumentValidator;
import java.util.List;
import com.newrelic.agent.deps.org.apache.commons.cli.CommandLine;
import com.newrelic.agent.deps.org.apache.commons.cli.CommandLineParser;
import com.newrelic.agent.deps.org.apache.commons.cli.ParseException;
import com.newrelic.agent.deps.org.apache.commons.cli.PosixParser;
import com.newrelic.agent.deps.org.apache.commons.cli.Options;
import java.util.Map;

class AgentCommandLineParser
{
    private static final String INSTALL_COMMAND = "install";
    private static final String DEPLOYMENT_COMMAND = "deployment";
    private static final String VERIFY_INSTRUMENTATION_COMMAND = "verifyInstrumentation";
    private static final String INSTRUMENT_COMMAND = "instrument";
    private static final Map<String, Options> commandOptionsMap;
    private static final Map<String, String> commandDescriptions;
    
    public void parseCommand(final String[] args) {
        final CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmd = parser.parse(getCommandLineOptions(), args);
            final List<String> argList = (List<String>)cmd.getArgList();
            final String command = (argList.size() > 0) ? argList.get(0) : null;
            if (cmd.hasOption('h')) {
                this.printHelp(command);
                return;
            }
            if (command != null) {
                final Options commandOptions = AgentCommandLineParser.commandOptionsMap.get(command);
                if (commandOptions == null) {
                    this.printHelp();
                    System.err.println("\nInvalid command - " + command);
                    System.exit(1);
                }
                cmd = parser.parse(commandOptions, args);
            }
            if ("deployment".equals(command)) {
                this.deploymentCommand(cmd);
            }
            else if ("install".equals(command)) {
                this.installCommand(cmd);
            }
            else if ("instrument".equals(command)) {
                this.instrumentCommand(cmd);
            }
            else if ("verifyInstrumentation".equals(command)) {
                this.verifyInstrumentation(cmd);
            }
            else if (cmd.hasOption('v') || cmd.hasOption("version")) {
                System.out.println(Agent.getVersion());
            }
            else {
                this.printHelp();
                System.exit(1);
            }
        }
        catch (ParseException e2) {
            System.err.println("Error parsing arguments");
            this.printHelp();
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println("Error executing command");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void instrumentCommand(final CommandLine cmd) throws Exception {
        XmlInstrumentValidator.validateInstrumentation(cmd);
    }
    
    private void deploymentCommand(final CommandLine cmd) throws Exception {
        Deployments.recordDeployment(cmd);
    }
    
    private void installCommand(final CommandLine cmd) throws Exception {
        System.out.println("***** ( ( o))  New Relic Java Agent Installer");
        System.out.println("***** Installing version " + Agent.getVersion() + " ...");
        final File newRelicDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        File appServerDir = null;
        if (cmd.getOptionValue('s') != null) {
            appServerDir = new File(cmd.getOptionValue('s'));
        }
        if (appServerDir == null || !appServerDir.exists() || !appServerDir.isDirectory()) {
            appServerDir = newRelicDir.getParentFile();
        }
        appServerDir = appServerDir.getCanonicalFile();
        boolean startup_patched = false;
        boolean config_installed = false;
        final AppServerIdentifier.AppServerType type = AppServerIdentifier.getAppServerType(appServerDir);
        if (type == null || type == AppServerIdentifier.AppServerType.UNKNOWN) {
            this.printUnknownAppServer(appServerDir);
        }
        else {
            final SelfInstaller installer = SelfInstallerFactory.getSelfInstaller(type);
            if (installer != null) {
                startup_patched = installer.backupAndEditStartScript(appServerDir.toString());
            }
        }
        if (newRelicDir.exists() && newRelicDir.isDirectory()) {
            if (ConfigInstaller.isConfigInstalled(newRelicDir)) {
                System.out.println("No need to create New Relic configuration file because:");
                System.out.println(MessageFormat.format(" .:. A config file already exists: {0}", ConfigInstaller.configPath(newRelicDir)));
                config_installed = true;
            }
            else {
                try {
                    ConfigInstaller.install(cmd.getOptionValue('l'), newRelicDir);
                    config_installed = true;
                    System.out.println("Generated New Relic configuration file " + ConfigInstaller.configPath(newRelicDir));
                }
                catch (IOException e) {
                    System.err.println(MessageFormat.format("An error occurred generating the configuration file {0} : {1}", ConfigInstaller.configPath(newRelicDir), e.toString()));
                    Agent.LOG.log(Level.FINE, "Config file generation error", e);
                }
            }
        }
        else {
            System.err.println("Could not create New Relic configuration file because:");
            System.err.println(MessageFormat.format(" .:. {0} does not exist or is not a directory", newRelicDir.getAbsolutePath()));
        }
        if (startup_patched && config_installed) {
            this.printInstallSuccess();
            System.exit(0);
        }
        else {
            this.printInstallIncomplete();
            System.exit(1);
        }
    }
    
    private void printInstallSuccess() {
        System.out.println("***** Install successful");
        System.out.println("***** Next steps:");
        System.out.println("You're almost done! To see performance data for your app:" + SelfInstaller.lineSep + " .:. Restart your app server" + SelfInstaller.lineSep + " .:. Exercise your app" + SelfInstaller.lineSep + " .:. Log into http://rpm.newrelic.com" + SelfInstaller.lineSep + "Within two minutes, your app should show up, ready to monitor and troubleshoot." + SelfInstaller.lineSep + "If app data doesn't appear, check newrelic/logs/newrelic_agent.log for errors.");
    }
    
    private void printInstallIncomplete() {
        System.out.println("***** Install incomplete");
        System.out.println("***** Next steps:");
        System.out.println("For help completing the install, see https://newrelic.com/docs/java/new-relic-for-java");
    }
    
    private void printUnknownAppServer(final File appServerLoc) {
        final StringBuilder knownAppServers = new StringBuilder();
        for (int i = 0; i < AppServerIdentifier.AppServerType.values().length - 1; ++i) {
            final AppServerIdentifier.AppServerType type = AppServerIdentifier.AppServerType.values()[i];
            knownAppServers.append(type.getName());
            if (i < AppServerIdentifier.AppServerType.values().length - 3) {
                knownAppServers.append(", ");
            }
            else if (i == AppServerIdentifier.AppServerType.values().length - 3) {
                knownAppServers.append(" or ");
            }
        }
        System.out.println("Could not edit start script because:");
        System.out.println(" .:. Could not locate a " + knownAppServers.toString() + " instance in " + appServerLoc.toString());
        System.out.println("Try re-running the install command with the -s <AppServerRootDirectory> option or from <AppServerRootDirectory>" + SelfInstaller.fileSep + "newrelic.");
        System.out.println("If that doesn't work, locate and edit the start script manually.");
    }
    
    private void printHelp() {
        final HelpFormatter formatter = new HelpFormatter();
        System.out.println(MessageFormat.format("New Relic Agent Version {0}", Agent.getVersion()));
        formatter.printHelp("java -jar newrelic.jar", "", getBasicOptions(), this.getCommandLineFooter());
    }
    
    private void printHelp(final String command) {
        if (command == null) {
            this.printHelp();
            return;
        }
        final HelpFormatter formatter = new HelpFormatter();
        System.out.println(MessageFormat.format("New Relic Agent Version {0}", Agent.getVersion()));
        final String footer = "\n  " + command + ' ' + AgentCommandLineParser.commandDescriptions.get(command);
        formatter.printHelp("java -jar newrelic.jar " + command, "", AgentCommandLineParser.commandOptionsMap.get(command), footer);
    }
    
    private void verifyInstrumentation(final CommandLine cmd) {
        final List<String> args = cmd.getArgList().subList(1, cmd.getArgList().size());
        final String instrumentationJar = args.get(0);
        final boolean expectedVerificationResult = Boolean.valueOf(args.get(1));
        List<String> userJars = new ArrayList<String>();
        if (args.size() > 2) {
            userJars = args.subList(2, args.size());
        }
        try {
            ServiceFactory.setServiceManager(null);
            final VerificationLogger logger = new VerificationLogger();
            final InstrumentationVerifier instrumentationVerifier = new InstrumentationVerifier(logger);
            final boolean passed = instrumentationVerifier.verify(instrumentationJar, userJars);
            final List<String> output = logger.getOutput();
            logger.flush();
            if (passed == expectedVerificationResult) {
                instrumentationVerifier.printVerificationResults(System.out, output);
            }
            else {
                instrumentationVerifier.printVerificationResults(System.err, output);
                System.exit(1);
            }
        }
        catch (Exception e) {
            System.err.println("Unexpected error while verifying");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private String getCommandLineFooter() {
        final int maxCommandLength = this.getMaxCommandLength();
        final String minSpaces = "    ";
        final StringBuilder builder = new StringBuilder("\nCommands:");
        for (final Map.Entry<String, String> entry : AgentCommandLineParser.commandDescriptions.entrySet()) {
            final String extraSpaces = new String(new char[maxCommandLength - entry.getKey().length()]).replace('\0', ' ');
            builder.append("\n  ").append(entry.getKey()).append(extraSpaces).append(minSpaces).append(entry.getValue());
        }
        return builder.toString();
    }
    
    private int getMaxCommandLength() {
        int max = 0;
        for (final String command : AgentCommandLineParser.commandDescriptions.keySet()) {
            max = Math.max(max, command.length());
        }
        return max;
    }
    
    static Options getCommandLineOptions() {
        final Collection<Options> values = new ArrayList<Options>(Collections.singletonList(getBasicOptions()));
        values.addAll(AgentCommandLineParser.commandOptionsMap.values());
        return combineOptions(values);
    }
    
    private static Options combineOptions(final Collection<Options> optionsList) {
        final Options newOptions = new Options();
        for (final Options options : optionsList) {
            for (final Option option : options.getOptions()) {
                newOptions.addOption(option);
            }
        }
        return newOptions;
    }
    
    private static Options getBasicOptions() {
        final Options options = new Options();
        options.addOption("v", false, "Prints the agent version");
        options.addOption("version", false, "Prints the agent version");
        options.addOption("h", false, "Prints help");
        return options;
    }
    
    private static Options getInstallOptions() {
        final Options options = new Options();
        options.addOption("l", true, "Use the given license key");
        options.addOption("s", true, "Path to application server");
        return options;
    }
    
    private static Options getDeploymentOptions() {
        final Options options = new Options();
        options.addOption("appname", true, "Set the application name.  Default is app_name setting in newrelic.yml");
        options.addOption("environment", true, "Set the environment (staging, production, test, development)");
        options.addOption("user", true, "Specify the user deploying");
        options.addOption("revision", true, "Specify the revision being deployed");
        options.addOption("changes", false, "Reads the change log for a deployment from standard input");
        return options;
    }
    
    private static Options getInstrumentOptions() {
        final Options options = new Options();
        final XmlInstrumentOptions[] arr$;
        final XmlInstrumentOptions[] instrumentOps = arr$ = XmlInstrumentOptions.values();
        for (final XmlInstrumentOptions op : arr$) {
            options.addOption(op.getFlagName(), op.isArgRequired(), op.getDescription());
        }
        return options;
    }
    
    private static Options getVerifyInstrumentationOptions() {
        final Options options = new Options();
        return options;
    }
    
    static {
        (commandOptionsMap = new HashMap<String, Options>()).put("deployment", getDeploymentOptions());
        AgentCommandLineParser.commandOptionsMap.put("install", getInstallOptions());
        AgentCommandLineParser.commandOptionsMap.put("instrument", getInstrumentOptions());
        AgentCommandLineParser.commandOptionsMap.put("verifyInstrumentation", getVerifyInstrumentationOptions());
        (commandDescriptions = new HashMap<String, String>()).put("deployment", "[OPTIONS] [description]  Records a deployment");
        AgentCommandLineParser.commandDescriptions.put("install", "[OPTIONS]                Generates a newrelic.yml configuration with the given license key and attempts to integrate with app server");
        AgentCommandLineParser.commandDescriptions.put("instrument", "[OPTIONS]                Validates a custom instrumentation xml configuration file.");
    }
}

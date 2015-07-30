// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.install;

import com.newrelic.agent.util.Strings;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.newrelic.agent.util.Streams;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class ConfigInstaller
{
    private static final String REPLACE_WITH_YOUR_LICENSE_KEY = "replace_with_your_license_key";
    private static final String LICENSE_KEY_CONFIG_PARAM = "<%= license_key %>";
    private static final String GENERATE_FOR_USER_CONFIG_PARAM = "<%= generated_for_user %>";
    
    public static boolean isConfigInstalled(final File configDir) {
        return configDir != null && configDir.exists() && configDir.isDirectory() && new File(configDir, "newrelic.yml").exists();
    }
    
    public static String configPath(final File configDir) {
        return new File(configDir, "newrelic.yml").getAbsolutePath();
    }
    
    public static void install(final String licenseKey, final File configDir) throws Exception {
        generateConfig((licenseKey == null) ? "replace_with_your_license_key" : licenseKey, configDir);
    }
    
    private static void generateConfig(final String licenseKey, final File configDir) throws Exception {
        final InputStream inStream = ConfigInstaller.class.getClassLoader().getResourceAsStream("newrelic.yml");
        if (inStream != null) {
            final String generatedFrom = getGeneratedFromString();
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                Streams.copy(inStream, output);
                final String yaml = output.toString().replace("<%= generated_for_user %>", generatedFrom).replace("<%= license_key %>", licenseKey);
                final FileOutputStream fileOut = new FileOutputStream(configPath(configDir));
                try {
                    Streams.copy(new ByteArrayInputStream(yaml.getBytes()), fileOut, yaml.getBytes().length);
                    fileOut.close();
                }
                finally {
                    fileOut.close();
                }
                inStream.close();
            }
            finally {
                inStream.close();
            }
            return;
        }
        throw new IOException("Unable to open newrelic.yml template");
    }
    
    private static String getGeneratedFromString() {
        return MessageFormat.format("Generated on {0}, from version {1}", new Date(), Agent.getVersion());
    }
    
    public static boolean isLicenseKeyEmpty(final String license) {
        return license == null || license.equals("replace_with_your_license_key") || license.equals("<%= license_key %>") || Strings.isEmpty(license) || Strings.isEmpty(license.trim());
    }
}

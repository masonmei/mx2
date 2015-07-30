// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.browser;

import java.util.Collections;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;
import com.newrelic.agent.config.BaseConfig;

public class BrowserConfig extends BaseConfig implements IBrowserConfig
{
    public static final String BROWSER_KEY = "browser_key";
    public static final String BROWSER_LOADER_VERSION = "browser_monitoring.loader_version";
    public static final String JS_AGENT_LOADER = "js_agent_loader";
    public static final String JS_AGENT_FILE = "js_agent_file";
    public static final String BEACON = "beacon";
    public static final String ERROR_BEACON = "error_beacon";
    public static final String APPLICATION_ID = "application_id";
    private static final String HEADER_BEGIN = "\n<script type=\"text/javascript\">";
    private static final String HEADER_END = "</script>";
    private final BrowserFooter footer;
    private final String header;
    
    private BrowserConfig(final String appName, final Map<String, Object> props) throws Exception {
        super(props);
        this.footer = this.initBrowserFooter(appName);
        this.header = this.initBrowserHeader();
        this.logVersion(appName);
    }
    
    private void logVersion(final String appName) {
        final String version = this.getProperty("browser_monitoring.loader_version");
        if (version != null) {
            Agent.LOG.log(Level.INFO, MessageFormat.format("Using RUM version {0} for application \"{1}\"", version, appName));
        }
    }
    
    private String initBrowserHeader() throws Exception {
        return "\n<script type=\"text/javascript\">" + this.getRequiredProperty("js_agent_loader") + "</script>";
    }
    
    private BrowserFooter initBrowserFooter(final String appName) throws Exception {
        final String beacon = this.getRequiredProperty("beacon");
        final String browserKey = this.getRequiredProperty("browser_key");
        final String errorBeacon = this.getRequiredProperty("error_beacon");
        final String payloadScript = this.getRequiredProperty("js_agent_file");
        final String appId = this.getRequiredProperty("application_id");
        return new BrowserFooter(appName, beacon, browserKey, errorBeacon, payloadScript, appId);
    }
    
    public String getRequiredProperty(final String key) throws Exception {
        final Object val = this.getProperty(key, (Object)null);
        if (val == null) {
            final String msg = MessageFormat.format("Real User Monitoring value for {0} is missing", key);
            throw new Exception(msg);
        }
        return val.toString();
    }
    
    public String getBrowserTimingHeader() {
        return this.header;
    }
    
    public String getBrowserTimingFooter(final BrowserTransactionState state) {
        return this.footer.getFooter(state);
    }
    
    public static IBrowserConfig createBrowserConfig(final String appName, Map<String, Object> settings) throws Exception {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new BrowserConfig(appName, settings);
    }
}

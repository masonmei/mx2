// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.browser;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import com.newrelic.agent.util.Obfuscator;
import java.util.Collections;
import java.util.HashMap;
import java.io.Writer;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONObject;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import com.newrelic.agent.config.BrowserMonitoringConfig;
import com.newrelic.agent.service.ServiceFactory;

public class BrowserFooter
{
    private static final String BEACON_KEY = "beacon";
    private static final String ERROR_BEACON_KEY = "errorBeacon";
    private static final String LICENSE_KEY = "licenseKey";
    private static final String APPLCATION_ID_KEY = "applicationID";
    private static final String TRANSACTION_NAME_KEY = "transactionName";
    private static final String QUEUE_TIME_KEY = "queueTime";
    private static final String APP_TIME_KEY = "applicationTime";
    private static final String TRAN_TRACE_GUID_KEY = "ttGuid";
    private static final String AGENT_TOKEN_KEY = "agentToken";
    private static final String ATTS_KEY = "atts";
    private static final String SSL_FOR_HTTP_KEY = "sslForHttp";
    private static final String AGENT_PAYLOAD_SCRIPT_KEY = "agent";
    public static final String FOOTER_START_SCRIPT = "\n<script type=\"text/javascript\">window.NREUM||(NREUM={});NREUM.info=";
    public static final String FOOTER_END = "</script>";
    private final String beacon;
    private final String browserKey;
    private final String errorBeacon;
    private final String payloadScript;
    private final String appId;
    private final Boolean isSslForHttp;
    
    public BrowserFooter(final String appName, final String pBeacon, final String pBrowserKey, final String pErrorBeacon, final String pPayloadScript, final String pAppId) {
        this.beacon = pBeacon;
        this.browserKey = pBrowserKey;
        this.errorBeacon = pErrorBeacon;
        this.payloadScript = pPayloadScript;
        this.appId = pAppId;
        final BrowserMonitoringConfig config = ServiceFactory.getConfigService().getAgentConfig(appName).getBrowserMonitoringConfig();
        if (config.isSslForHttpSet()) {
            this.isSslForHttp = config.isSslForHttp();
        }
        else {
            this.isSslForHttp = null;
        }
    }
    
    public String getFooter(final BrowserTransactionState state) {
        final String jsonString = this.jsonToString(this.createMapWithData(state));
        if (jsonString != null) {
            return "\n<script type=\"text/javascript\">window.NREUM||(NREUM={});NREUM.info=" + jsonString + "</script>";
        }
        return "";
    }
    
    private String jsonToString(final Map<String, ?> map) {
        ByteArrayOutputStream baos = null;
        Writer out = null;
        try {
            baos = new ByteArrayOutputStream();
            out = new OutputStreamWriter(baos, "UTF-8");
            JSONObject.writeJSONString(map, out);
            out.flush();
            final String s = new String(baos.toByteArray());
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ex) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (IOException ex2) {}
            }
            return s;
        }
        catch (Exception e) {
            Agent.LOG.log(Level.INFO, "An error occured when creating the rum footer. Issue:" + e.getMessage());
            if (Agent.LOG.isFinestEnabled()) {
                Agent.LOG.log(Level.FINEST, "Exception when creating rum footer. ", e);
            }
            final String s2 = null;
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ex3) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (IOException ex4) {}
            }
            return s2;
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ex5) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (IOException ex6) {}
            }
        }
    }
    
    private Map<String, Object> createMapWithData(final BrowserTransactionState state) {
        final Map<String, Object> output = new HashMap<String, Object>();
        output.put("beacon", this.beacon);
        output.put("errorBeacon", this.errorBeacon);
        output.put("licenseKey", this.browserKey);
        output.put("applicationID", this.appId);
        output.put("agent", this.payloadScript);
        output.put("queueTime", state.getExternalTimeInMilliseconds());
        output.put("applicationTime", state.getDurationInMilliseconds());
        output.put("transactionName", this.obfuscate(state.getTransactionName()));
        this.addToMapIfNotNullOrEmpty(output, "sslForHttp", this.isSslForHttp);
        this.addToMapIfNotNullAndObfuscate(output, "atts", getAttributes(state));
        return output;
    }
    
    protected static Map<String, Object> getAttributes(final BrowserTransactionState state) {
        Map<String, Object> atts;
        if (ServiceFactory.getAttributesService().isAttributesEnabledForBrowser(state.getAppName())) {
            final Map<String, ?> userAtts = ServiceFactory.getAttributesService().filterBrowserAttributes(state.getAppName(), state.getUserAttributes());
            final Map<String, ?> agentAtts = ServiceFactory.getAttributesService().filterBrowserAttributes(state.getAppName(), state.getAgentAttributes());
            atts = new HashMap<String, Object>(3);
            if (!ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity() && !userAtts.isEmpty()) {
                atts.put("u", userAtts);
            }
            if (!agentAtts.isEmpty()) {
                atts.put("a", agentAtts);
            }
        }
        else {
            atts = Collections.emptyMap();
        }
        return atts;
    }
    
    private void addToMapIfNotNullOrEmpty(final Map<String, Object> map, final String key, final String value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }
    
    private void addToMapIfNotNullOrEmpty(final Map<String, Object> map, final String key, final Boolean value) {
        if (value != null) {
            map.put(key, value);
        }
    }
    
    private void addToMapIfNotNullAndObfuscate(final Map<String, Object> map, final String key, final Map<String, ?> value) {
        if (value != null && !value.isEmpty()) {
            final String output = this.jsonToString(value);
            if (output != null && !output.isEmpty()) {
                map.put(key, this.obfuscate(output));
            }
        }
    }
    
    private String obfuscate(final String name) {
        if (name == null || name.length() == 0) {
            return "";
        }
        final String licenseKey = ServiceFactory.getConfigService().getDefaultAgentConfig().getLicenseKey();
        try {
            return Obfuscator.obfuscateNameUsingKey(name, licenseKey.substring(0, 13));
        }
        catch (UnsupportedEncodingException e) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Error obfuscating {0}: {1}", name, e);
                Agent.LOG.finer(msg);
            }
            return "";
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

import java.lang.reflect.Constructor;
import com.newrelic.agent.util.RubyConversion;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import com.newrelic.agent.deps.org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.Writer;
import com.newrelic.agent.deps.org.json.simple.JSONValue;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.newrelic.agent.deps.org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import com.newrelic.agent.deps.org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import com.newrelic.agent.deps.org.apache.http.conn.ssl.X509HostnameVerifier;
import com.newrelic.agent.deps.org.apache.http.conn.ssl.StrictHostnameVerifier;
import com.newrelic.agent.deps.org.apache.http.config.SocketConfig;
import java.util.Arrays;
import com.newrelic.agent.deps.org.apache.http.message.BasicHeader;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.impl.client.HttpClientBuilder;
import java.net.URISyntaxException;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.entity.ByteArrayEntity;
import com.newrelic.agent.deps.org.apache.http.client.methods.RequestBuilder;
import com.newrelic.agent.deps.org.apache.http.client.config.RequestConfig;
import java.net.URL;
import java.net.SocketException;
import java.net.MalformedURLException;
import com.newrelic.agent.ForceDisconnectException;
import com.newrelic.agent.deps.org.apache.http.client.CredentialsProvider;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScope;
import com.newrelic.agent.deps.org.apache.http.impl.client.BasicCredentialsProvider;
import com.newrelic.agent.deps.org.apache.http.client.protocol.HttpClientContext;
import com.newrelic.agent.deps.org.apache.http.StatusLine;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpUriRequest;
import com.newrelic.agent.deps.org.apache.http.impl.client.CloseableHttpClient;
import java.util.logging.Level;
import com.newrelic.agent.trace.TransactionTrace;
import com.newrelic.agent.sql.SqlTrace;
import com.newrelic.agent.service.module.Jar;
import com.newrelic.agent.profile.IProfile;
import com.newrelic.agent.MetricDataException;
import com.newrelic.agent.MetricData;
import com.newrelic.agent.service.analytics.CustomInsightsEvent;
import com.newrelic.agent.service.analytics.TransactionEvent;
import com.newrelic.agent.errors.TracedError;
import java.util.Iterator;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.rmi.UnexpectedException;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;
import java.util.Map;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.org.apache.http.auth.UsernamePasswordCredentials;
import com.newrelic.agent.deps.org.apache.http.conn.ssl.SSLContextBuilder;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.io.InputStream;
import java.security.KeyStore;
import com.newrelic.agent.logging.ApacheCommonsAdaptingLogFactory;
import com.newrelic.agent.config.AgentConfig;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import javax.net.ssl.SSLContext;
import com.newrelic.agent.deps.org.apache.http.auth.Credentials;
import com.newrelic.agent.deps.org.apache.http.HttpHost;

public class DataSenderImpl implements DataSender
{
    private static final String MODULE_TYPE = "Jars";
    private static final int PROTOCOL_VERSION = 14;
    private static final int DEFAULT_REQUEST_TIMEOUT_IN_SECONDS = 120;
    private static final String BEFORE_LICENSE_KEY_URI_PATTERN = "/agent_listener/invoke_raw_method?method={0}";
    private static final String AFTER_LICENSE_KEY_URI_PATTERN = "&marshal_format=json&protocol_version=14";
    private static final String LICENSE_KEY_URI_PATTERN = "&license_key={0}";
    private static final String RUN_ID_PATTERN = "&run_id={1}";
    private static final String CONNECT_METHOD = "connect";
    private static final String METRIC_DATA_METHOD = "metric_data";
    private static final String GET_AGENT_COMMANDS_METHOD = "get_agent_commands";
    private static final String AGENT_COMMAND_RESULTS_METHOD = "agent_command_results";
    private static final String GET_REDIRECT_HOST_METHOD = "get_redirect_host";
    private static final String ERROR_DATA_METHOD = "error_data";
    private static final String PROFILE_DATA_METHOD = "profile_data";
    private static final String QUEUE_PING_COMMAND_METHOD = "queue_ping_command";
    private static final String ANALYTIC_DATA_METHOD = "analytic_event_data";
    private static final String CUSTOM_ANALYTIC_DATA_METHOD = "custom_event_data";
    private static final String UPDATE_LOADED_MODULES_METHOD = "update_loaded_modules";
    private static final String SHUTDOWN_METHOD = "shutdown";
    private static final String SQL_TRACE_DATA_METHOD = "sql_trace_data";
    private static final String TRANSACTION_SAMPLE_DATA_METHOD = "transaction_sample_data";
    private static final String USER_AGENT_HEADER_VALUE;
    private static final String GZIP = "gzip";
    private static final String DEFLATE_ENCODING = "deflate";
    private static final String IDENTITY_ENCODING = "identity";
    private static final String RESPONSE_MAP_EXCEPTION_KEY = "exception";
    private static final String EXCEPTION_MAP_MESSAGE_KEY = "message";
    private static final String EXCEPTION_MAP_ERROR_TYPE_KEY = "error_type";
    private static final String EXCEPTION_MAP_RETURN_VALUE_KEY = "return_value";
    private static final String AGENT_RUN_ID_KEY = "agent_run_id";
    private static final String SSL_KEY = "ssl";
    private static final Object NO_AGENT_RUN_ID;
    private static final String NULL_RESPONSE = "null";
    private static final String TIMEOUT_PROPERTY = "timeout";
    private static final int COMPRESSION_LEVEL = -1;
    private static final String GET_XRAY_PARMS_METHOD = "get_xray_metadata";
    private volatile String host;
    private final int port;
    private volatile String protocol;
    private final HttpHost proxy;
    private final Credentials proxyCredentials;
    private final int defaultTimeoutInMillis;
    private volatile boolean auditMode;
    private volatile Object agentRunId;
    private final String agentRunIdUriPattern;
    private final String noAgentRunIdUriPattern;
    private final boolean usePrivateSSL;
    private final boolean useSSL;
    private final SSLContext sslContext;
    
    private static String initUserHeaderValue() {
        String arch = "unknown";
        String javaVersion = "unknown";
        try {
            arch = System.getProperty("os.arch");
            javaVersion = System.getProperty("java.version");
        }
        catch (Exception ex) {}
        return MessageFormat.format("NewRelic-JavaAgent/{0} (java {1} {2})", Agent.getVersion(), javaVersion, arch);
    }
    
    public DataSenderImpl(final AgentConfig config) {
        this.agentRunId = DataSenderImpl.NO_AGENT_RUN_ID;
        System.setProperty("com.newrelic.agent.deps.org.apache.commons.logging.LogFactory", ApacheCommonsAdaptingLogFactory.class.getName());
        this.auditMode = config.isAuditMode();
        Agent.LOG.info(MessageFormat.format("Setting audit_mode to {0}", this.auditMode));
        this.host = config.getHost();
        this.port = config.getPort();
        this.useSSL = config.isSSL();
        this.protocol = (this.useSSL ? "https" : "http");
        String msg = MessageFormat.format("Setting protocol to \"{0}\"", this.protocol);
        Agent.LOG.info(msg);
        final String proxyHost = config.getProxyHost();
        final Integer proxyPort = config.getProxyPort();
        this.usePrivateSSL = config.isUsePrivateSSL();
        this.sslContext = this.createSSLContext();
        if (proxyHost != null && proxyPort != null) {
            msg = MessageFormat.format("Using proxy host {0}:{1}", proxyHost, Integer.toString(proxyPort));
            Agent.LOG.fine(msg);
            this.proxy = new HttpHost(proxyHost, proxyPort);
            this.proxyCredentials = this.getProxyCredentials(proxyHost, proxyPort, config.getProxyUser(), config.getProxyPassword());
        }
        else {
            this.proxy = null;
            this.proxyCredentials = null;
        }
        this.defaultTimeoutInMillis = config.getProperty("timeout", 120) * 1000;
        final String licenseKeyUri = MessageFormat.format("&license_key={0}", config.getLicenseKey());
        this.noAgentRunIdUriPattern = "/agent_listener/invoke_raw_method?method={0}" + licenseKeyUri + "&marshal_format=json&protocol_version=14";
        this.agentRunIdUriPattern = this.noAgentRunIdUriPattern + "&run_id={1}";
    }
    
    public static KeyStore getKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final InputStream in = DataSenderImpl.class.getResourceAsStream("/nrcerts");
        if (null == in) {
            Agent.LOG.fine("Unable to find NR trust store");
        }
        else {
            try {
                keystore.load(in, null);
                in.close();
            }
            finally {
                in.close();
            }
        }
        Agent.LOG.finer("SSL Keystore Provider: " + keystore.getProvider().getName());
        return keystore;
    }
    
    private SSLContext createSSLContext() {
        final SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        try {
            if (this.usePrivateSSL && this.useSSL) {
                sslContextBuilder.loadTrustMaterial(getKeyStore());
            }
            return sslContextBuilder.build();
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private Credentials getProxyCredentials(final String proxyHost, final Integer proxyPort, final String proxyUser, final String proxyPass) {
        if (proxyUser != null && proxyPass != null) {
            Agent.LOG.info(MessageFormat.format("Setting Proxy Authenticator for user '{0}'", proxyUser));
            return new UsernamePasswordCredentials(proxyUser, proxyPass);
        }
        return null;
    }
    
    private void checkAuditMode() {
        final boolean auditMode2 = ServiceFactory.getConfigService().getLocalAgentConfig().isAuditMode();
        if (this.auditMode != auditMode2) {
            this.auditMode = auditMode2;
            Agent.LOG.info(MessageFormat.format("Setting audit_mode to {0}", this.auditMode));
        }
    }
    
    private void setAgentRunId(final Object runId) {
        this.agentRunId = runId;
        if (runId != DataSenderImpl.NO_AGENT_RUN_ID) {
            Agent.LOG.info("Agent run id: " + runId);
        }
    }
    
    public Map<String, Object> connect(final Map<String, Object> startupOptions) throws Exception {
        final String redirectHost = this.getRedirectHost();
        if (redirectHost != null) {
            this.host = redirectHost;
            final String msg = MessageFormat.format("Collector redirection to {0}:{1}", this.host, Integer.toString(this.port));
            Agent.LOG.info(msg);
        }
        return this.doConnect(startupOptions);
    }
    
    private String getRedirectHost() throws Exception {
        final Object response = this.invokeNoRunId("get_redirect_host", "deflate", new InitialSizedJsonArray(0));
        return (response == null) ? null : response.toString();
    }
    
    private Map<String, Object> doConnect(final Map<String, Object> startupOptions) throws Exception {
        final InitialSizedJsonArray params = new InitialSizedJsonArray(1);
        params.add(startupOptions);
        final Object response = this.invokeNoRunId("connect", "deflate", params);
        if (!(response instanceof Map)) {
            final String msg = MessageFormat.format("Expected a map of connection data, got {0}", response);
            throw new UnexpectedException(msg);
        }
        final Map<String, Object> data = (Map<String, Object>)response;
        if (data.containsKey("agent_run_id")) {
            final Object runId = data.get("agent_run_id");
            this.setAgentRunId(runId);
            final Object ssl = data.get("ssl");
            if (Boolean.TRUE.equals(ssl)) {
                Agent.LOG.info("Setting protocol to \"https\"");
                this.protocol = "https";
            }
            return data;
        }
        final String msg2 = MessageFormat.format("Missing {0} connection parameter", "agent_run_id");
        throw new UnexpectedException(msg2);
    }
    
    public List<List<?>> getAgentCommands() throws Exception {
        this.checkAuditMode();
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID) {
            return Collections.emptyList();
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(1);
        params.add(runId);
        final Object response = this.invokeRunId("get_agent_commands", "deflate", runId, params);
        if (response == null || "null".equals(response)) {
            return Collections.emptyList();
        }
        try {
            return (List<List<?>>)response;
        }
        catch (ClassCastException e) {
            final String msg = MessageFormat.format("Invalid response from New Relic when getting agent commands: {0}", e);
            Agent.LOG.warning(msg);
            throw e;
        }
    }
    
    public List<?> getXRayParameters(final Collection<Long> ids) throws Exception {
        if (ids.size() > 0) {
            this.checkAuditMode();
            final Object runId = this.agentRunId;
            if (runId == DataSenderImpl.NO_AGENT_RUN_ID) {
                return Collections.emptyList();
            }
            final JSONArray params = new JSONArray();
            params.add(runId);
            for (final Long s : ids) {
                params.add(s);
            }
            final Object response = this.invokeRunId("get_xray_metadata", "deflate", runId, params);
            if (response == null || "null".equals(response)) {
                return Collections.emptyList();
            }
            try {
                return (List<?>)response;
            }
            catch (ClassCastException e) {
                final String msg = MessageFormat.format("Invalid response from New Relic when getting agent X Ray parameters: {0}", e);
                Agent.LOG.warning(msg);
                throw e;
            }
        }
        Agent.LOG.info("Attempted to fetch X-Ray Session metadata with no session IDs");
        return Collections.emptyList();
    }
    
    public void queuePingCommand() throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(1);
        params.add(runId);
        this.invokeRunId("queue_ping_command", "deflate", runId, params);
    }
    
    public void sendCommandResults(final Map<Long, Object> commandResults) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || commandResults.isEmpty()) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add(runId);
        params.add(commandResults);
        this.invokeRunId("agent_command_results", "deflate", runId, params);
    }
    
    public void sendErrorData(final List<TracedError> errors) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || errors.isEmpty()) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add(runId);
        params.add(errors);
        this.invokeRunId("error_data", "identity", runId, params);
    }
    
    public void sendAnalyticsEvents(final Collection<TransactionEvent> events) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || events.isEmpty()) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add(runId);
        params.add(events);
        this.invokeRunId("analytic_event_data", "deflate", runId, params);
    }
    
    public void sendCustomAnalyticsEvents(final Collection<CustomInsightsEvent> events) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || events.isEmpty()) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add(runId);
        params.add(events);
        this.invokeRunId("custom_event_data", "deflate", runId, params);
    }
    
    public List<List<?>> sendMetricData(final long beginTimeMillis, final long endTimeMillis, final List<MetricData> metricData) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || metricData.isEmpty()) {
            return Collections.emptyList();
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(4);
        params.add(runId);
        params.add(beginTimeMillis / 1000L);
        params.add(endTimeMillis / 1000L);
        params.add(metricData);
        final Object response = this.invokeRunId("metric_data", "deflate", runId, params);
        if (response == null || "null".equals(response)) {
            throw new MetricDataException("Invalid null response sending metric data");
        }
        try {
            return (List<List<?>>)response;
        }
        catch (ClassCastException e) {
            final String msg = MessageFormat.format("Invalid response from New Relic when sending metric data: {0}", e);
            Agent.LOG.warning(msg);
            throw e;
        }
    }
    
    public List<Long> sendProfileData(final List<IProfile> profiles) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || profiles.isEmpty()) {
            return Collections.emptyList();
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add(runId);
        params.add(profiles);
        final Object response = this.invokeRunId("profile_data", "identity", runId, params);
        if (response == null || "null".equals(response)) {
            return Collections.emptyList();
        }
        try {
            return (List<Long>)response;
        }
        catch (ClassCastException e) {
            final String msg = MessageFormat.format("Invalid response from New Relic sending profiles: {0}", e);
            Agent.LOG.warning(msg);
            throw e;
        }
    }
    
    public void sendModules(final List<Jar> pJars) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || pJars == null || pJars.isEmpty()) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add("Jars");
        params.add(pJars);
        final Object response = this.invokeRunId("update_loaded_modules", "identity", runId, params);
        if (response == null || "null".equals(response)) {
            return;
        }
        final String msg = MessageFormat.format("Invalid response from New Relic when sending modules. Response: {0}", response);
        Agent.LOG.warning(msg);
    }
    
    public void sendSqlTraceData(final List<SqlTrace> sqlTraces) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || sqlTraces.isEmpty()) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(1);
        params.add(sqlTraces);
        final Object response = this.invokeRunId("sql_trace_data", "identity", runId, params);
        if (response == null || "null".equals(response)) {
            return;
        }
        final String msg = MessageFormat.format("Invalid response from New Relic when sending sql traces. Response: {0}", response);
        Agent.LOG.warning(msg);
    }
    
    public void sendTransactionTraceData(final List<TransactionTrace> traces) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID || traces.isEmpty()) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add(runId);
        params.add(traces);
        final Object response = this.invokeRunId("transaction_sample_data", "identity", runId, params);
        if (response == null || "null".equals(response)) {
            return;
        }
        final String msg = MessageFormat.format("Invalid response from New Relic when sending transaction traces. Response: {0}", response);
        Agent.LOG.warning(msg);
    }
    
    public void shutdown(final long timeMillis) throws Exception {
        final Object runId = this.agentRunId;
        if (runId == DataSenderImpl.NO_AGENT_RUN_ID) {
            return;
        }
        final InitialSizedJsonArray params = new InitialSizedJsonArray(2);
        params.add(runId);
        params.add(timeMillis);
        final int requestTimeoutInMillis = 10000;
        try {
            this.invokeRunId("shutdown", "deflate", runId, requestTimeoutInMillis, params);
            this.setAgentRunId(DataSenderImpl.NO_AGENT_RUN_ID);
        }
        finally {
            this.setAgentRunId(DataSenderImpl.NO_AGENT_RUN_ID);
        }
    }
    
    private Object invokeRunId(final String method, final String encoding, final Object runId, final JSONStreamAware params) throws Exception {
        return this.invokeRunId(method, encoding, runId, this.defaultTimeoutInMillis, params);
    }
    
    private Object invokeRunId(final String method, final String encoding, final Object runId, final int timeoutInMillis, final JSONStreamAware params) throws Exception {
        final String uri = MessageFormat.format(this.agentRunIdUriPattern, method, runId.toString());
        return this.invoke(method, encoding, uri, params, timeoutInMillis);
    }
    
    private Object invokeNoRunId(final String method, final String encoding, final JSONStreamAware params) throws Exception {
        final String uri = MessageFormat.format(this.noAgentRunIdUriPattern, method);
        return this.invoke(method, encoding, uri, params, this.defaultTimeoutInMillis);
    }
    
    private Object invoke(final String method, final String encoding, final String uri, final JSONStreamAware params, final int timeoutInMillis) throws Exception {
        final ReadResult readResult = this.send(method, encoding, uri, params, timeoutInMillis);
        Map<?, ?> responseMap = null;
        final String responseBody = readResult.getResponseBody();
        if (responseBody != null) {
            Exception ex = null;
            try {
                responseMap = this.getResponseMap(responseBody);
                ex = this.parseException(responseMap);
            }
            catch (Exception e) {
                Agent.LOG.log(Level.WARNING, "Error parsing response JSON({0}) from NewRelic: {1}", new Object[] { method, e.toString() });
                Agent.LOG.log(Level.FINEST, "Invalid response JSON({0}): {1}", new Object[] { method, responseBody });
                throw e;
            }
            if (ex != null) {
                throw ex;
            }
        }
        else {
            Agent.LOG.log(Level.FINER, "Response was null ({0})", new Object[] { method });
        }
        if (responseMap != null) {
            return responseMap.get("return_value");
        }
        return null;
    }
    
    private ReadResult connectAndSend(final String method, final String encoding, final String uri, final JSONStreamAware params, final int timeoutInMillis) throws Exception {
        CloseableHttpClient conn = null;
        try {
            conn = this.createHttpClient(encoding, uri, timeoutInMillis);
            final byte[] data = this.writeData(encoding, params);
            final HttpUriRequest request = this.createRequest(encoding, uri, timeoutInMillis, data);
            final HttpContext context = this.createHttpContext();
            final CloseableHttpResponse response = conn.execute(request, context);
            try {
                final StatusLine statusLine = response.getStatusLine();
                if (statusLine == null) {
                    throw new Exception("The http response has no status line");
                }
                if (this.auditMode) {
                    final String msg = MessageFormat.format("Sent JSON({0}) to: {1}\n{2}", method, request.getURI(), DataSenderWriter.toJSONString(params));
                    Agent.LOG.info(msg);
                }
                final int statusCode = statusLine.getStatusCode();
                if (statusCode == 407) {
                    final String authField = response.getFirstHeader("Proxy-Authenticate").getValue();
                    throw new HttpError("Proxy Authentication Mechanism Failed: " + authField, statusCode);
                }
                if (statusCode != 200) {
                    Agent.LOG.log(Level.FINER, "Connection http status code: {0}", new Object[] { statusCode });
                    throw HttpError.create(statusCode, this.host);
                }
                final String responseBody = this.readResponseBody(response);
                if (this.auditMode) {
                    final String msg2 = MessageFormat.format("Received JSON({0}): {1}", method, responseBody);
                    Agent.LOG.info(msg2);
                }
                final ReadResult create = ReadResult.create(statusCode, responseBody);
                response.close();
                if (conn != null) {
                    conn.close();
                }
                return create;
            }
            finally {
                response.close();
            }
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    private HttpContext createHttpContext() {
        final HttpClientContext context = new HttpClientContext();
        if (this.proxy != null && this.proxyCredentials != null) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(this.proxy), this.proxyCredentials);
            context.setCredentialsProvider(credentialsProvider);
        }
        return context;
    }
    
    private ReadResult send(final String method, final String encoding, final String uri, final JSONStreamAware params, final int timeoutInMillis) throws Exception {
        try {
            return this.connectAndSend(method, encoding, uri, params, timeoutInMillis);
        }
        catch (MalformedURLException e) {
            Agent.LOG.log(Level.SEVERE, "You have requested a connection to New Relic via a protocol which is unavailable in your runtime: {0}", new Object[] { e.toString() });
            throw new ForceDisconnectException(e.toString());
        }
        catch (SocketException e2) {
            if (e2.getCause() instanceof NoSuchAlgorithmException) {
                final String msg = MessageFormat.format("You have requested a connection to New Relic via an algorithm which is unavailable in your runtime: {0}  This may also be indicative of a corrupted keystore or trust store on your server.", e2.getCause().toString());
                Agent.LOG.error(msg);
            }
            else {
                Agent.LOG.log(Level.INFO, "A socket exception was encountered while sending data to New Relic ({0}).  Please check your network / proxy settings.", new Object[] { e2.toString() });
                if (Agent.LOG.isLoggable(Level.FINE)) {
                    Agent.LOG.log(Level.FINE, "Error sending JSON({0}): {1}", new Object[] { method, DataSenderWriter.toJSONString(params) });
                }
                Agent.LOG.log(Level.FINEST, (Throwable)e2, e2.toString(), new Object[0]);
            }
            throw e2;
        }
        catch (HttpError e3) {
            throw e3;
        }
        catch (Exception e4) {
            Agent.LOG.log(Level.INFO, "Remote {0} call failed : {1}.", new Object[] { method, e4.toString() });
            if (Agent.LOG.isLoggable(Level.FINE)) {
                Agent.LOG.log(Level.FINE, "Error sending JSON({0}): {1}", new Object[] { method, DataSenderWriter.toJSONString(params) });
            }
            Agent.LOG.log(Level.FINEST, (Throwable)e4, e4.toString(), new Object[0]);
            throw e4;
        }
    }
    
    private HttpUriRequest createRequest(final String encoding, final String uri, final int requestTimeoutInMillis, final byte[] data) throws MalformedURLException, URISyntaxException {
        final URL url = new URL(this.protocol, this.host, this.port, uri);
        final RequestConfig config = RequestConfig.custom().setConnectTimeout(requestTimeoutInMillis).setConnectionRequestTimeout(requestTimeoutInMillis).setSocketTimeout(requestTimeoutInMillis).build();
        final RequestBuilder requestBuilder = RequestBuilder.post().setUri(url.toURI()).setEntity(new ByteArrayEntity(data));
        requestBuilder.setConfig(config);
        return requestBuilder.build();
    }
    
    private CloseableHttpClient createHttpClient(final String encoding, final String uri, final int requestTimeoutInMillis) throws Exception {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setUserAgent(DataSenderImpl.USER_AGENT_HEADER_VALUE).setDefaultHeaders(Arrays.asList(new BasicHeader("Connection", "Keep-Alive"), new BasicHeader("CONTENT-TYPE", "application/octet-stream"), new BasicHeader("ACCEPT-ENCODING", "gzip"), new BasicHeader("CONTENT-ENCODING", encoding)));
        builder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(requestTimeoutInMillis).setSoKeepAlive(true).build());
        final RequestConfig.Builder requestBuilder = RequestConfig.custom().setConnectTimeout(requestTimeoutInMillis).setConnectionRequestTimeout(requestTimeoutInMillis).setSocketTimeout(requestTimeoutInMillis);
        builder.setDefaultRequestConfig(requestBuilder.build());
        builder.setHostnameVerifier(new StrictHostnameVerifier());
        if (this.proxy != null) {
            builder.setProxy(this.proxy);
        }
        if (this.sslContext != null) {
            builder.setSSLSocketFactory(new SSLConnectionSocketFactory(this.sslContext));
        }
        final CloseableHttpClient httpClient = builder.build();
        return httpClient;
    }
    
    private byte[] writeData(final String encoding, final JSONStreamAware params) throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Writer out = null;
        try {
            final OutputStream os = this.getOutputStream(outStream, encoding);
            out = new OutputStreamWriter(os, "UTF-8");
            JSONValue.writeJSONString(params, out);
            out.flush();
            if (out != null) {
                out.close();
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
        return outStream.toByteArray();
    }
    
    private OutputStream getOutputStream(final OutputStream out, final String encoding) throws IOException {
        if ("deflate".equals(encoding)) {
            return new DeflaterOutputStream(out, new Deflater(-1));
        }
        return out;
    }
    
    private String readResponseBody(final CloseableHttpResponse response) throws Exception {
        final HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new Exception("The http response entity was null");
        }
        final InputStream is = entity.getContent();
        final BufferedReader in = this.getBufferedReader(response, is);
        try {
            final String line = in.readLine();
            in.close();
            is.close();
            return line;
        }
        finally {
            in.close();
            is.close();
        }
    }
    
    private Map<?, ?> getResponseMap(final String responseBody) throws Exception {
        final JSONParser parser = new JSONParser();
        final Object response = parser.parse(responseBody);
        return Map.class.cast(response);
    }
    
    private BufferedReader getBufferedReader(final CloseableHttpResponse response, InputStream is) throws IOException {
        final Header encodingHeader = response.getFirstHeader("content-encoding");
        if (encodingHeader != null) {
            final String encoding = encodingHeader.getValue();
            if ("gzip".equals(encoding)) {
                is = new GZIPInputStream(is);
            }
        }
        return new BufferedReader(new InputStreamReader(is, "UTF-8"));
    }
    
    private Exception parseException(final Map<?, ?> responseMap) throws Exception {
        final Object exception = responseMap.get("exception");
        if (exception == null) {
            return null;
        }
        final Map<?, ?> exceptionMap = Map.class.cast(exception);
        String message;
        try {
            message = (String)exceptionMap.get("message");
        }
        catch (Exception e) {
            message = exceptionMap.toString();
        }
        final String type = (String)exceptionMap.get("error_type");
        final Class<Exception> clazz = RubyConversion.rubyClassToJavaClass(type);
        final Constructor<Exception> constructor = clazz.getConstructor(String.class);
        return constructor.newInstance(message);
    }
    
    static {
        USER_AGENT_HEADER_VALUE = initUserHeaderValue();
        NO_AGENT_RUN_ID = null;
    }
}

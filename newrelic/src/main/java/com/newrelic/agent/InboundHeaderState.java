// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.deps.org.json.simple.parser.JSONParser;
import com.newrelic.agent.service.ServiceUtils;
import java.text.MessageFormat;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.logging.Level;
import com.newrelic.api.agent.InboundHeaders;

public class InboundHeaderState
{
    private static final String CONTENT_LENGTH_REQUEST_HEADER = "Content-Length";
    private static final String NEWRELIC_ID_HEADER_SEPARATOR = "#";
    private static final int CURRENT_SYNTHETICS_VERSION = 1;
    private final Transaction tx;
    private final InboundHeaders inboundHeaders;
    private final CatState catState;
    private final SyntheticsState synState;

    public InboundHeaderState(final Transaction tx, final InboundHeaders inboundHeaders) {
        this.tx = tx;
        this.inboundHeaders = inboundHeaders;
        if (inboundHeaders == null) {
            this.synState = SyntheticsState.NONE;
            this.catState = CatState.NONE;
        }
        else {
            this.synState = this.parseSyntheticsHeader();
            this.catState = this.parseCatHeaders();
        }
    }

    public String getUnparsedSyntheticsHeader() {
        String result = null;
        if (this.inboundHeaders != null) {
            result = HeadersUtil.getSyntheticsHeader(this.inboundHeaders);
        }
        return result;
    }

    private SyntheticsState parseSyntheticsHeader() {
        final String synHeader = this.getUnparsedSyntheticsHeader();
        if (synHeader == null || synHeader.length() == 0) {
            return SyntheticsState.NONE;
        }
        final JSONArray arr = this.getJSONArray(synHeader);
        if (arr == null || arr.size() == 0) {
            Agent.LOG.fine("Synthetic transaction tracing failed: unable to decode header.");
            return SyntheticsState.NONE;
        }
        Agent.LOG.log(Level.FINEST, "Decoded synthetics header => {0}", new Object[] { arr });
        Integer version = null;
        try {
            version = Integer.parseInt(arr.get(0).toString());
        }
        catch (NumberFormatException nfe) {
            Agent.LOG.log(Level.FINEST, "Could not determine Synetics version. Value => {0}. Class => {1}.", new Object[] { arr.get(0), arr.get(0).getClass() });
            return SyntheticsState.NONE;
        }
        if (version > 1) {
            Agent.LOG.log(Level.FINE, "Synthetic transaction tracing failed: invalid version {0}", new Object[] { version });
            return SyntheticsState.NONE;
        }
        SyntheticsState result;
        try {
            result = new SyntheticsState(version, (Number)arr.get(1), (String)arr.get(2), (String)arr.get(3), (String)arr.get(4));
        }
        catch (RuntimeException rex) {
            Agent.LOG.log(Level.FINE, "Synthetic transaction tracing failed: while parsing header: {0}: {1}", new Object[] { rex.getClass().getSimpleName(), rex.getLocalizedMessage() });
            result = SyntheticsState.NONE;
        }
        return result;
    }

    private CatState parseCatHeaders() {
        final String clientCrossProcessID = HeadersUtil.getIdHeader(this.inboundHeaders);
        if (clientCrossProcessID == null || this.tx.isIgnore()) {
            return CatState.NONE;
        }
        if (!this.tx.getCrossProcessConfig().isCrossApplicationTracing()) {
            return CatState.NONE;
        }
        if (!this.isClientCrossProcessIdTrusted(clientCrossProcessID)) {
            return CatState.NONE;
        }
        if (Agent.LOG.isFinestEnabled()) {
            final String msg = MessageFormat.format("Client cross process id is {0}", clientCrossProcessID);
            Agent.LOG.finest(msg);
        }
        final String transactionHeader = HeadersUtil.getTransactionHeader(this.inboundHeaders);
        final JSONArray arr = this.getJSONArray(transactionHeader);
        if (arr == null) {
            return new CatState(clientCrossProcessID, null, Boolean.FALSE, null, null);
        }
        return new CatState(clientCrossProcessID,
                (arr.size() >= 1) ? (String)arr.get(0) : null,
                (arr.size() >= 2) ? (Boolean)arr.get(1) : null,
                (arr.size() >= 3) ? (String)arr.get(2) : null,
                (arr.size() >= 4) ? ServiceUtils.hexStringToInt((String)arr.get(3)) : null);
    }

    public int getSyntheticsVersion() {
        final Integer obj = this.synState.getVersion();
        if (obj == null) {
            return -1;
        }
        final int version = obj;
        if (version < 0) {
            return -1;
        }
        return version;
    }

    private boolean isSupportedSyntheticsVersion() {
        final int version = this.getSyntheticsVersion();
        return version >= 1 && version <= 1;
    }

    public boolean isTrustedSyntheticsRequest() {
        return this.isSupportedSyntheticsVersion() && this.synState.getAccountId() != null;
    }

    public String getSyntheticsResourceId() {
        return this.synState.getSyntheticsResourceId();
    }

    public String getSyntheticsJobId() {
        return this.synState.getSyntheticsJobId();
    }

    public String getSyntheticsMonitorId() {
        return this.synState.getSyntheticsMonitorId();
    }

    public boolean isTrustedCatRequest() {
        return this.catState.getClientCrossProcessId() != null;
    }

    public String getClientCrossProcessId() {
        return this.catState.getClientCrossProcessId();
    }

    public String getReferrerGuid() {
        return this.catState.getReferrerGuid();
    }

    public boolean forceTrace() {
        return this.catState.forceTrace();
    }

    public Integer getReferringPathHash() {
        return this.catState.getReferringPathHash();
    }

    public String getInboundTripId() {
        return this.catState.getInboundTripId();
    }

    public long getRequestContentLength() {
        long contentLength = -1L;
        final String contentLengthString = (this.inboundHeaders == null) ? null : this.inboundHeaders.getHeader("Content-Length");
        if (contentLengthString != null) {
            try {
                contentLength = Long.parseLong(contentLengthString);
            }
            catch (NumberFormatException e) {
                final String msg = MessageFormat.format("Error parsing {0} response header: {1}: {2}", "Content-Length", contentLengthString, e);
                Agent.LOG.finer(msg);
            }
        }
        return contentLength;
    }

    private boolean isClientCrossProcessIdTrusted(final String clientCrossProcessId) {
        final String accountId = this.getAccountId(clientCrossProcessId);
        if (accountId != null) {
            if (this.tx.getCrossProcessConfig().isTrustedAccountId(accountId)) {
                return true;
            }
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                final String msg = MessageFormat.format("Account id {0} in client cross process id {1} is not trusted", accountId, clientCrossProcessId);
                Agent.LOG.log(Level.FINEST, msg);
            }
        }
        else if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Account id not found in client cross process id {0}", clientCrossProcessId);
            Agent.LOG.log(Level.FINER, msg);
        }
        return false;
    }

    private String getAccountId(final String clientCrossProcessId) {
        String accountId = null;
        final int index = clientCrossProcessId.indexOf("#");
        if (index > 0) {
            accountId = clientCrossProcessId.substring(0, index);
        }
        return accountId;
    }

    private JSONArray getJSONArray(final String json) {
        JSONArray result = null;
        if (json != null) {
            try {
                final JSONParser parser = new JSONParser();
                result = (JSONArray)parser.parse(json);
            }
            catch (Exception ex) {
                if (Agent.LOG.isFinerEnabled()) {
                    Agent.LOG.log(Level.FINER, "Unable to parse TRANSACTION header {0}: {1}", new Object[] { "FIXME", ex });
                }
            }
        }
        return result;
    }

    static final class CatState
    {
        private final String clientCrossProcessId;
        private final String referrerGuid;
        private final Boolean forceTrace;
        private final String inboundTripId;
        private final Integer referringPathHash;
        static final CatState NONE;

        CatState(final String clientCrossProcessId, final String referrerGuid, final Boolean forceTrace, final String inboundTripId, final Integer referringPathHash) {
            this.clientCrossProcessId = clientCrossProcessId;
            this.referrerGuid = referrerGuid;
            this.forceTrace = forceTrace;
            this.inboundTripId = inboundTripId;
            this.referringPathHash = referringPathHash;
        }

        String getClientCrossProcessId() {
            return this.clientCrossProcessId;
        }

        String getReferrerGuid() {
            return this.referrerGuid;
        }

        boolean forceTrace() {
            return this.forceTrace;
        }

        String getInboundTripId() {
            return this.inboundTripId;
        }

        Integer getReferringPathHash() {
            return this.referringPathHash;
        }

        static {
            NONE = new CatState(null, null, Boolean.FALSE, null, null);
        }
    }

    static final class SyntheticsState
    {
        private final Integer version;
        private final Number accountId;
        private final String syntheticsResourceId;
        private final String syntheticsJobId;
        private final String syntheticsMonitorId;
        static final SyntheticsState NONE;

        SyntheticsState(final Integer version, final Number accountId, final String syntheticsResourceId, final String syntheticsJobId, final String syntheticsMonitorId) {
            this.version = version;
            this.accountId = accountId;
            this.syntheticsResourceId = syntheticsResourceId;
            this.syntheticsJobId = syntheticsJobId;
            this.syntheticsMonitorId = syntheticsMonitorId;
        }

        Integer getVersion() {
            return this.version;
        }

        Number getAccountId() {
            return this.accountId;
        }

        String getSyntheticsResourceId() {
            return this.syntheticsResourceId;
        }

        String getSyntheticsJobId() {
            return this.syntheticsJobId;
        }

        String getSyntheticsMonitorId() {
            return this.syntheticsMonitorId;
        }

        static {
            NONE = new SyntheticsState(null, null, null, null, null);
        }
    }
}

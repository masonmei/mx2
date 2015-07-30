// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import com.newrelic.agent.deps.ch.qos.logback.core.helpers.Transform;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import javax.servlet.ServletException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.newrelic.agent.deps.ch.qos.logback.core.util.CachingDateFormatter;
import javax.servlet.http.HttpServlet;

public abstract class ViewStatusMessagesServletBase extends HttpServlet
{
    private static final long serialVersionUID = -3551928133801157219L;
    private static CachingDateFormatter SDF;
    static String SUBMIT;
    static String CLEAR;
    int count;
    
    protected abstract StatusManager getStatusManager(final HttpServletRequest p0, final HttpServletResponse p1);
    
    protected abstract String getPageTitle(final HttpServletRequest p0, final HttpServletResponse p1);
    
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.count = 0;
        final StatusManager sm = this.getStatusManager(req, resp);
        resp.setContentType("text/html");
        final PrintWriter output = resp.getWriter();
        output.append("<html>\r\n");
        output.append("<head>\r\n");
        this.printCSS(req.getContextPath(), output);
        output.append("</head>\r\n");
        output.append("<body>\r\n");
        output.append(this.getPageTitle(req, resp));
        output.append("<form method=\"POST\">\r\n");
        output.append("<input type=\"submit\" name=\"" + ViewStatusMessagesServletBase.SUBMIT + "\" value=\"" + ViewStatusMessagesServletBase.CLEAR + "\">");
        output.append("</form>\r\n");
        if (ViewStatusMessagesServletBase.CLEAR.equalsIgnoreCase(req.getParameter(ViewStatusMessagesServletBase.SUBMIT))) {
            sm.clear();
            sm.add(new InfoStatus("Cleared all status messages", this));
        }
        output.append("<table>");
        final StringBuilder buf = new StringBuilder();
        if (sm != null) {
            this.printList(buf, sm);
        }
        else {
            output.append("Could not find status manager");
        }
        output.append(buf);
        output.append("</table>");
        output.append("</body>\r\n");
        output.append("</html>\r\n");
        output.flush();
        output.close();
    }
    
    public void printCSS(final String localRef, final PrintWriter output) {
        output.append("  <STYLE TYPE=\"text/css\">\r\n");
        output.append("    .warn  { font-weight: bold; color: #FF6600;} \r\n");
        output.append("    .error { font-weight: bold; color: #CC0000;} \r\n");
        output.append("    table { margin-left: 2em; margin-right: 2em; border-left: 2px solid #AAA; }\r\n");
        output.append("    tr.even { background: #FFFFFF; }\r\n");
        output.append("    tr.odd  { background: #EAEAEA; }\r\n");
        output.append("    td { padding-right: 1ex; padding-left: 1ex; border-right: 2px solid #AAA; }\r\n");
        output.append("    td.date { text-align: right; font-family: courier, monospace; font-size: smaller; }");
        output.append(CoreConstants.LINE_SEPARATOR);
        output.append("  td.level { text-align: right; }");
        output.append(CoreConstants.LINE_SEPARATOR);
        output.append("    tr.header { background: #596ED5; color: #FFF; font-weight: bold; font-size: larger; }");
        output.append(CoreConstants.LINE_SEPARATOR);
        output.append("  td.exception { background: #A2AEE8; white-space: pre; font-family: courier, monospace;}");
        output.append(CoreConstants.LINE_SEPARATOR);
        output.append("  </STYLE>\r\n");
    }
    
    public void printList(final StringBuilder buf, final StatusManager sm) {
        buf.append("<table>\r\n");
        this.printHeader(buf);
        final List<Status> statusList = sm.getCopyOfStatusList();
        for (final Status s : statusList) {
            ++this.count;
            this.printStatus(buf, s);
        }
        buf.append("</table>\r\n");
    }
    
    public void printHeader(final StringBuilder buf) {
        buf.append("  <tr class=\"header\">\r\n");
        buf.append("    <th>Date </th>\r\n");
        buf.append("    <th>Level</th>\r\n");
        buf.append("    <th>Origin</th>\r\n");
        buf.append("    <th>Message</th>\r\n");
        buf.append("  </tr>\r\n");
    }
    
    String statusLevelAsString(final Status s) {
        switch (s.getEffectiveLevel()) {
            case 0: {
                return "INFO";
            }
            case 1: {
                return "<span class=\"warn\">WARN</span>";
            }
            case 2: {
                return "<span class=\"error\">ERROR</span>";
            }
            default: {
                return null;
            }
        }
    }
    
    String abbreviatedOrigin(final Status s) {
        final Object o = s.getOrigin();
        if (o == null) {
            return null;
        }
        final String fqClassName = o.getClass().getName();
        final int lastIndex = fqClassName.lastIndexOf(46);
        if (lastIndex != -1) {
            return fqClassName.substring(lastIndex + 1, fqClassName.length());
        }
        return fqClassName;
    }
    
    private void printStatus(final StringBuilder buf, final Status s) {
        String trClass;
        if (this.count % 2 == 0) {
            trClass = "even";
        }
        else {
            trClass = "odd";
        }
        buf.append("  <tr class=\"").append(trClass).append("\">\r\n");
        final String dateStr = ViewStatusMessagesServletBase.SDF.format(s.getDate());
        buf.append("    <td class=\"date\">").append(dateStr).append("</td>\r\n");
        buf.append("    <td class=\"level\">").append(this.statusLevelAsString(s)).append("</td>\r\n");
        buf.append("    <td>").append(this.abbreviatedOrigin(s)).append("</td>\r\n");
        buf.append("    <td>").append(s.getMessage()).append("</td>\r\n");
        buf.append("  </tr>\r\n");
        if (s.getThrowable() != null) {
            this.printThrowable(buf, s.getThrowable());
        }
    }
    
    private void printThrowable(final StringBuilder buf, final Throwable t) {
        buf.append("  <tr>\r\n");
        buf.append("    <td colspan=\"4\" class=\"exception\"><pre>");
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        buf.append(Transform.escapeTags(sw.getBuffer()));
        buf.append("    </pre></td>\r\n");
        buf.append("  </tr>\r\n");
    }
    
    static {
        ViewStatusMessagesServletBase.SDF = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss");
        ViewStatusMessagesServletBase.SUBMIT = "submit";
        ViewStatusMessagesServletBase.CLEAR = "Clear";
    }
}

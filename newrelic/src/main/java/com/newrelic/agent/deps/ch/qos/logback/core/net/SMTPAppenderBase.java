// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.net;

import java.util.Hashtable;
import java.util.Iterator;
import javax.mail.Multipart;
import javax.mail.Transport;
import java.util.Date;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import com.newrelic.agent.deps.ch.qos.logback.core.util.ContentTypeUtil;
import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import java.util.Collection;
import java.util.Arrays;
import javax.mail.internet.AddressException;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EvaluationException;
import com.newrelic.agent.deps.ch.qos.logback.core.helpers.CyclicBuffer;
import javax.mail.Authenticator;
import java.util.Properties;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.Address;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.CyclicBufferTrackerImpl;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.DefaultDiscriminator;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.CyclicBufferTracker;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.Discriminator;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluator;
import javax.mail.internet.MimeMessage;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.PatternLayoutBase;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.Layout;
import javax.mail.internet.InternetAddress;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;

public abstract class SMTPAppenderBase<E> extends AppenderBase<E>
{
    static InternetAddress[] EMPTY_IA_ARRAY;
    static final int MAX_DELAY_BETWEEN_STATUS_MESSAGES = 1228800000;
    long lastTrackerStatusPrint;
    int delayBetweenStatusMessages;
    protected Layout<E> subjectLayout;
    protected Layout<E> layout;
    private List<PatternLayoutBase<E>> toPatternLayoutList;
    private String from;
    private String subjectStr;
    private String smtpHost;
    private int smtpPort;
    private boolean starttls;
    private boolean ssl;
    private boolean sessionViaJNDI;
    private String jndiLocation;
    String username;
    String password;
    String localhost;
    boolean asynchronousSending;
    private String charsetEncoding;
    protected MimeMessage mimeMsg;
    protected EventEvaluator<E> eventEvaluator;
    protected Discriminator<E> discriminator;
    protected CyclicBufferTracker<E> cbTracker;
    private int errorCount;
    
    public SMTPAppenderBase() {
        this.lastTrackerStatusPrint = 0L;
        this.delayBetweenStatusMessages = 300000;
        this.toPatternLayoutList = new ArrayList<PatternLayoutBase<E>>();
        this.subjectStr = null;
        this.smtpPort = 25;
        this.starttls = false;
        this.ssl = false;
        this.sessionViaJNDI = false;
        this.jndiLocation = "java:comp/env/mail/Session";
        this.asynchronousSending = true;
        this.charsetEncoding = "UTF-8";
        this.discriminator = new DefaultDiscriminator<E>();
        this.errorCount = 0;
    }
    
    protected abstract Layout<E> makeSubjectLayout(final String p0);
    
    public void start() {
        if (this.cbTracker == null) {
            this.cbTracker = new CyclicBufferTrackerImpl<E>();
        }
        Session session = null;
        if (this.sessionViaJNDI) {
            session = this.lookupSessionInJNDI();
        }
        else {
            session = this.buildSessionFromProperties();
        }
        if (session == null) {
            this.addError("Failed to obtain javax.mail.Session. Cannot start.");
            return;
        }
        this.mimeMsg = new MimeMessage(session);
        try {
            if (this.from != null) {
                this.mimeMsg.setFrom((Address)this.getAddress(this.from));
            }
            else {
                this.mimeMsg.setFrom();
            }
            this.subjectLayout = this.makeSubjectLayout(this.subjectStr);
            this.started = true;
        }
        catch (MessagingException e) {
            this.addError("Could not activate SMTPAppender options.", (Throwable)e);
        }
    }
    
    private Session lookupSessionInJNDI() {
        this.addInfo("Looking up javax.mail.Session at JNDI location [" + this.jndiLocation + "]");
        try {
            final Context initialContext = new InitialContext();
            final Object obj = initialContext.lookup(this.jndiLocation);
            return (Session)obj;
        }
        catch (Exception e) {
            this.addError("Failed to obtain javax.mail.Session from JNDI location [" + this.jndiLocation + "]");
            return null;
        }
    }
    
    private Session buildSessionFromProperties() {
        final Properties props = new Properties(OptionHelper.getSystemProperties());
        if (this.smtpHost != null) {
            ((Hashtable<String, String>)props).put("mail.smtp.host", this.smtpHost);
        }
        ((Hashtable<String, String>)props).put("mail.smtp.port", Integer.toString(this.smtpPort));
        if (this.localhost != null) {
            ((Hashtable<String, String>)props).put("mail.smtp.localhost", this.localhost);
        }
        LoginAuthenticator loginAuthenticator = null;
        if (this.username != null) {
            loginAuthenticator = new LoginAuthenticator(this.username, this.password);
            ((Hashtable<String, String>)props).put("mail.smtp.auth", "true");
        }
        if (this.isSTARTTLS() && this.isSSL()) {
            this.addError("Both SSL and StartTLS cannot be enabled simultaneously");
        }
        else {
            if (this.isSTARTTLS()) {
                ((Hashtable<String, String>)props).put("mail.smtp.starttls.enable", "true");
            }
            if (this.isSSL()) {
                final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
                ((Hashtable<String, String>)props).put("mail.smtp.socketFactory.port", Integer.toString(this.smtpPort));
                ((Hashtable<String, String>)props).put("mail.smtp.socketFactory.class", SSL_FACTORY);
                ((Hashtable<String, String>)props).put("mail.smtp.socketFactory.fallback", "true");
            }
        }
        return Session.getInstance(props, (Authenticator)loginAuthenticator);
    }
    
    protected void append(final E eventObject) {
        if (!this.checkEntryConditions()) {
            return;
        }
        final String key = this.discriminator.getDiscriminatingValue(eventObject);
        final long now = System.currentTimeMillis();
        final CyclicBuffer<E> cb = this.cbTracker.getOrCreate(key, now);
        this.subAppend(cb, eventObject);
        try {
            if (this.eventEvaluator.evaluate(eventObject)) {
                final CyclicBuffer<E> cbClone = new CyclicBuffer<E>(cb);
                cb.clear();
                if (this.asynchronousSending) {
                    final SenderRunnable senderRunnable = new SenderRunnable(cbClone, eventObject);
                    this.context.getExecutorService().execute(senderRunnable);
                }
                else {
                    this.sendBuffer(cbClone, eventObject);
                }
            }
        }
        catch (EvaluationException ex) {
            ++this.errorCount;
            if (this.errorCount < 4) {
                this.addError("SMTPAppender's EventEvaluator threw an Exception-", ex);
            }
        }
        if (this.isEventMarkedForBufferRemoval(eventObject)) {
            this.cbTracker.removeBuffer(key);
        }
        this.cbTracker.clearStaleBuffers(now);
        if (this.lastTrackerStatusPrint + this.delayBetweenStatusMessages < now) {
            this.addInfo("SMTPAppender [" + this.name + "] is tracking [" + this.cbTracker.size() + "] buffers");
            this.lastTrackerStatusPrint = now;
            if (this.delayBetweenStatusMessages < 1228800000) {
                this.delayBetweenStatusMessages *= 4;
            }
        }
    }
    
    protected abstract boolean isEventMarkedForBufferRemoval(final E p0);
    
    protected abstract void subAppend(final CyclicBuffer<E> p0, final E p1);
    
    public boolean checkEntryConditions() {
        if (!this.started) {
            this.addError("Attempting to append to a non-started appender: " + this.getName());
            return false;
        }
        if (this.mimeMsg == null) {
            this.addError("Message object not configured.");
            return false;
        }
        if (this.eventEvaluator == null) {
            this.addError("No EventEvaluator is set for appender [" + this.name + "].");
            return false;
        }
        if (this.layout == null) {
            this.addError("No layout set for appender named [" + this.name + "]. For more information, please visit http://logback.qos.ch/codes.html#smtp_no_layout");
            return false;
        }
        return true;
    }
    
    public synchronized void stop() {
        this.started = false;
    }
    
    InternetAddress getAddress(final String addressStr) {
        try {
            return new InternetAddress(addressStr);
        }
        catch (AddressException e) {
            this.addError("Could not parse address [" + addressStr + "].", (Throwable)e);
            return null;
        }
    }
    
    private List<InternetAddress> parseAddress(final E event) {
        final int len = this.toPatternLayoutList.size();
        final List<InternetAddress> iaList = new ArrayList<InternetAddress>();
        for (int i = 0; i < len; ++i) {
            try {
                final PatternLayoutBase<E> emailPL = this.toPatternLayoutList.get(i);
                final String email = emailPL.doLayout(event);
                if (email != null && email.length() != 0) {
                    final InternetAddress[] tmp = InternetAddress.parse(email, true);
                    iaList.addAll(Arrays.asList(tmp));
                }
            }
            catch (AddressException e) {
                this.addError("Could not parse email address for [" + this.toPatternLayoutList.get(i) + "] for event [" + event + "]", (Throwable)e);
                return iaList;
            }
        }
        return iaList;
    }
    
    public List<PatternLayoutBase<E>> getToList() {
        return this.toPatternLayoutList;
    }
    
    protected void sendBuffer(final CyclicBuffer<E> cb, final E lastEventObject) {
        try {
            final MimeBodyPart part = new MimeBodyPart();
            final StringBuffer sbuf = new StringBuffer();
            final String header = this.layout.getFileHeader();
            if (header != null) {
                sbuf.append(header);
            }
            final String presentationHeader = this.layout.getPresentationHeader();
            if (presentationHeader != null) {
                sbuf.append(presentationHeader);
            }
            this.fillBuffer(cb, sbuf);
            final String presentationFooter = this.layout.getPresentationFooter();
            if (presentationFooter != null) {
                sbuf.append(presentationFooter);
            }
            final String footer = this.layout.getFileFooter();
            if (footer != null) {
                sbuf.append(footer);
            }
            String subjectStr = "Undefined subject";
            if (this.subjectLayout != null) {
                subjectStr = this.subjectLayout.doLayout(lastEventObject);
            }
            this.mimeMsg.setSubject(subjectStr, this.charsetEncoding);
            final List<InternetAddress> destinationAddresses = this.parseAddress(lastEventObject);
            if (destinationAddresses.isEmpty()) {
                this.addInfo("Empty destination address. Aborting email transmission");
                return;
            }
            final InternetAddress[] toAddressArray = destinationAddresses.toArray(SMTPAppenderBase.EMPTY_IA_ARRAY);
            this.mimeMsg.setRecipients(Message.RecipientType.TO, (Address[])toAddressArray);
            final String contentType = this.layout.getContentType();
            if (ContentTypeUtil.isTextual(contentType)) {
                part.setText(sbuf.toString(), this.charsetEncoding, ContentTypeUtil.getSubType(contentType));
            }
            else {
                part.setContent((Object)sbuf.toString(), this.layout.getContentType());
            }
            final Multipart mp = (Multipart)new MimeMultipart();
            mp.addBodyPart((BodyPart)part);
            this.mimeMsg.setContent(mp);
            this.mimeMsg.setSentDate(new Date());
            Transport.send((Message)this.mimeMsg);
            this.addInfo("Sent out SMTP message \"" + subjectStr + "\" to " + Arrays.toString(toAddressArray));
        }
        catch (Exception e) {
            this.addError("Error occurred while sending e-mail notification.", e);
        }
    }
    
    protected abstract void fillBuffer(final CyclicBuffer<E> p0, final StringBuffer p1);
    
    public String getFrom() {
        return this.from;
    }
    
    public String getSubject() {
        return this.subjectStr;
    }
    
    public void setFrom(final String from) {
        this.from = from;
    }
    
    public void setSubject(final String subject) {
        this.subjectStr = subject;
    }
    
    public void setSMTPHost(final String smtpHost) {
        this.setSmtpHost(smtpHost);
    }
    
    public void setSmtpHost(final String smtpHost) {
        this.smtpHost = smtpHost;
    }
    
    public String getSMTPHost() {
        return this.getSmtpHost();
    }
    
    public String getSmtpHost() {
        return this.smtpHost;
    }
    
    public void setSMTPPort(final int port) {
        this.setSmtpPort(port);
    }
    
    public void setSmtpPort(final int port) {
        this.smtpPort = port;
    }
    
    public int getSMTPPort() {
        return this.getSmtpPort();
    }
    
    public int getSmtpPort() {
        return this.smtpPort;
    }
    
    public String getLocalhost() {
        return this.localhost;
    }
    
    public void setLocalhost(final String localhost) {
        this.localhost = localhost;
    }
    
    public CyclicBufferTracker<E> getCyclicBufferTracker() {
        return this.cbTracker;
    }
    
    public void setCyclicBufferTracker(final CyclicBufferTracker<E> cbTracker) {
        this.cbTracker = cbTracker;
    }
    
    public Discriminator<E> getDiscriminator() {
        return this.discriminator;
    }
    
    public void setDiscriminator(final Discriminator<E> discriminator) {
        this.discriminator = discriminator;
    }
    
    public boolean isAsynchronousSending() {
        return this.asynchronousSending;
    }
    
    public void setAsynchronousSending(final boolean asynchronousSending) {
        this.asynchronousSending = asynchronousSending;
    }
    
    public void addTo(final String to) {
        if (to == null || to.length() == 0) {
            throw new IllegalArgumentException("Null or empty <to> property");
        }
        final PatternLayoutBase plb = this.makeNewToPatternLayout(to.trim());
        plb.setContext(this.context);
        plb.start();
        this.toPatternLayoutList.add(plb);
    }
    
    protected abstract PatternLayoutBase<E> makeNewToPatternLayout(final String p0);
    
    public List<String> getToAsListOfString() {
        final List<String> toList = new ArrayList<String>();
        for (final PatternLayoutBase plb : this.toPatternLayoutList) {
            toList.add(plb.getPattern());
        }
        return toList;
    }
    
    public Message getMessage() {
        return (Message)this.mimeMsg;
    }
    
    public void setMessage(final MimeMessage msg) {
        this.mimeMsg = msg;
    }
    
    public boolean isSTARTTLS() {
        return this.starttls;
    }
    
    public void setSTARTTLS(final boolean startTLS) {
        this.starttls = startTLS;
    }
    
    public boolean isSSL() {
        return this.ssl;
    }
    
    public void setSSL(final boolean ssl) {
        this.ssl = ssl;
    }
    
    public void setEvaluator(final EventEvaluator<E> eventEvaluator) {
        this.eventEvaluator = eventEvaluator;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getCharsetEncoding() {
        return this.charsetEncoding;
    }
    
    public String getJndiLocation() {
        return this.jndiLocation;
    }
    
    public void setJndiLocation(final String jndiLocation) {
        this.jndiLocation = jndiLocation;
    }
    
    public boolean isSessionViaJNDI() {
        return this.sessionViaJNDI;
    }
    
    public void setSessionViaJNDI(final boolean sessionViaJNDI) {
        this.sessionViaJNDI = sessionViaJNDI;
    }
    
    public void setCharsetEncoding(final String charsetEncoding) {
        this.charsetEncoding = charsetEncoding;
    }
    
    public Layout<E> getLayout() {
        return this.layout;
    }
    
    public void setLayout(final Layout<E> layout) {
        this.layout = layout;
    }
    
    static {
        SMTPAppenderBase.EMPTY_IA_ARRAY = new InternetAddress[0];
    }
    
    class SenderRunnable implements Runnable
    {
        final CyclicBuffer<E> cyclicBuffer;
        final E e;
        
        SenderRunnable(final CyclicBuffer<E> cyclicBuffer, final E e) {
            this.cyclicBuffer = cyclicBuffer;
            this.e = e;
        }
        
        public void run() {
            SMTPAppenderBase.this.sendBuffer(this.cyclicBuffer, this.e);
        }
    }
}

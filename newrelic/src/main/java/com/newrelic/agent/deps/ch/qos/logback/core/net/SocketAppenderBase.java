// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.net;

import java.net.ConnectException;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PreSerializationTransformer;
import java.io.Serializable;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;

public abstract class SocketAppenderBase<E> extends AppenderBase<E>
{
    static final int DEFAULT_PORT = 4560;
    static final int DEFAULT_RECONNECTION_DELAY = 30000;
    protected String remoteHost;
    protected InetAddress address;
    protected int port;
    protected ObjectOutputStream oos;
    protected int reconnectionDelay;
    private Connector connector;
    protected int counter;
    
    public SocketAppenderBase() {
        this.port = 4560;
        this.reconnectionDelay = 30000;
        this.counter = 0;
    }
    
    public void start() {
        int errorCount = 0;
        if (this.port == 0) {
            ++errorCount;
            this.addError("No port was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_port");
        }
        if (this.address == null) {
            ++errorCount;
            this.addError("No remote address was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
        }
        this.connect(this.address, this.port);
        if (errorCount == 0) {
            this.started = true;
        }
    }
    
    public void stop() {
        if (!this.isStarted()) {
            return;
        }
        this.started = false;
        this.cleanUp();
    }
    
    public void cleanUp() {
        if (this.oos != null) {
            try {
                this.oos.close();
            }
            catch (IOException e) {
                this.addError("Could not close oos.", e);
            }
            this.oos = null;
        }
        if (this.connector != null) {
            this.addInfo("Interrupting the connector.");
            this.connector.interrupted = true;
            this.connector = null;
        }
    }
    
    void connect(final InetAddress address, final int port) {
        if (this.address == null) {
            return;
        }
        try {
            this.cleanUp();
            this.oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
        }
        catch (IOException e) {
            String msg = "Could not connect to remote logback server at [" + address.getHostName() + "].";
            if (this.reconnectionDelay > 0) {
                msg += " We will try again later.";
                this.fireConnector();
            }
            this.addInfo(msg, e);
        }
    }
    
    protected void append(final E event) {
        if (event == null) {
            return;
        }
        if (this.address == null) {
            this.addError("No remote host is set for SocketAppender named \"" + this.name + "\". For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
            return;
        }
        if (this.oos != null) {
            try {
                this.postProcessEvent(event);
                final Serializable serEvent = this.getPST().transform(event);
                this.oos.writeObject(serEvent);
                this.oos.flush();
                if (++this.counter >= 70) {
                    this.counter = 0;
                    this.oos.reset();
                }
            }
            catch (IOException e) {
                if (this.oos != null) {
                    try {
                        this.oos.close();
                    }
                    catch (IOException ex) {}
                }
                this.oos = null;
                this.addWarn("Detected problem with connection: " + e);
                if (this.reconnectionDelay > 0) {
                    this.fireConnector();
                }
            }
        }
    }
    
    protected abstract void postProcessEvent(final E p0);
    
    protected abstract PreSerializationTransformer<E> getPST();
    
    void fireConnector() {
        if (this.connector == null) {
            this.addInfo("Starting a new connector thread.");
            (this.connector = new Connector()).setDaemon(true);
            this.connector.setPriority(1);
            this.connector.start();
        }
    }
    
    protected static InetAddress getAddressByName(final String host) {
        try {
            return InetAddress.getByName(host);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public void setRemoteHost(final String host) {
        this.address = getAddressByName(host);
        this.remoteHost = host;
    }
    
    public String getRemoteHost() {
        return this.remoteHost;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setReconnectionDelay(final int delay) {
        this.reconnectionDelay = delay;
    }
    
    public int getReconnectionDelay() {
        return this.reconnectionDelay;
    }
    
    class Connector extends Thread
    {
        boolean interrupted;
        
        Connector() {
            this.interrupted = false;
        }
        
        public void run() {
            while (!this.interrupted) {
                try {
                    Thread.sleep(SocketAppenderBase.this.reconnectionDelay);
                    SocketAppenderBase.this.addInfo("Attempting connection to " + SocketAppenderBase.this.address.getHostName());
                    final Socket socket = new Socket(SocketAppenderBase.this.address, SocketAppenderBase.this.port);
                    synchronized (this) {
                        SocketAppenderBase.this.oos = new ObjectOutputStream(socket.getOutputStream());
                        SocketAppenderBase.this.connector = null;
                        SocketAppenderBase.this.addInfo("Connection established. Exiting connector thread.");
                    }
                }
                catch (InterruptedException e2) {
                    SocketAppenderBase.this.addInfo("Connector interrupted. Leaving loop.");
                    return;
                }
                catch (ConnectException e3) {
                    SocketAppenderBase.this.addInfo("Remote host " + SocketAppenderBase.this.address.getHostName() + " refused connection.");
                    continue;
                }
                catch (IOException e) {
                    SocketAppenderBase.this.addInfo("Could not connect to " + SocketAppenderBase.this.address.getHostName() + ". Exception is " + e);
                    continue;
                }
                break;
            }
        }
    }
}

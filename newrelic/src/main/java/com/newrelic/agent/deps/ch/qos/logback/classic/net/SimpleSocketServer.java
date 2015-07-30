// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.net;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.JoranConfigurator;
import java.util.Iterator;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.slf4j.LoggerFactory;
import java.util.concurrent.CountDownLatch;
import java.util.List;
import java.net.ServerSocket;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.org.slf4j.Logger;

public class SimpleSocketServer extends Thread
{
    Logger logger;
    private final int port;
    private final LoggerContext lc;
    private boolean closed;
    private ServerSocket serverSocket;
    private List<SocketNode> socketNodeList;
    private CountDownLatch latch;
    
    public static void main(final String[] argv) throws Exception {
        int port = -1;
        if (argv.length == 2) {
            port = parsePortNumber(argv[0]);
        }
        else {
            usage("Wrong number of arguments.");
        }
        final String configFile = argv[1];
        final LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        configureLC(lc, configFile);
        final SimpleSocketServer sss = new SimpleSocketServer(lc, port);
        sss.start();
    }
    
    public SimpleSocketServer(final LoggerContext lc, final int port) {
        this.logger = LoggerFactory.getLogger(SimpleSocketServer.class);
        this.closed = false;
        this.socketNodeList = new ArrayList<SocketNode>();
        this.lc = lc;
        this.port = port;
    }
    
    public void run() {
        try {
            this.logger.info("Listening on port " + this.port);
            this.serverSocket = new ServerSocket(this.port);
            while (!this.closed) {
                this.logger.info("Waiting to accept a new client.");
                this.signalAlmostReadiness();
                final Socket socket = this.serverSocket.accept();
                this.logger.info("Connected to client at " + socket.getInetAddress());
                this.logger.info("Starting new socket node.");
                final SocketNode newSocketNode = new SocketNode(this, socket, this.lc);
                synchronized (this.socketNodeList) {
                    this.socketNodeList.add(newSocketNode);
                }
                new Thread(newSocketNode).start();
            }
        }
        catch (Exception e) {
            if (this.closed) {
                this.logger.info("Exception in run method for a closed server. This is normal.");
            }
            else {
                this.logger.error("Unexpected failure in run method", e);
            }
        }
    }
    
    void signalAlmostReadiness() {
        if (this.latch != null && this.latch.getCount() != 0L) {
            this.latch.countDown();
        }
    }
    
    void setLatch(final CountDownLatch latch) {
        this.latch = latch;
    }
    
    public CountDownLatch getLatch() {
        return this.latch;
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public void close() {
        this.closed = true;
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            }
            catch (IOException e) {
                this.logger.error("Failed to close serverSocket", e);
            }
            finally {
                this.serverSocket = null;
            }
        }
        this.logger.info("closing this server");
        synchronized (this.socketNodeList) {
            for (final SocketNode sn : this.socketNodeList) {
                sn.close();
            }
        }
        if (this.socketNodeList.size() != 0) {
            this.logger.warn("Was expecting a 0-sized socketNodeList after server shutdown");
        }
    }
    
    public void socketNodeClosing(final SocketNode sn) {
        this.logger.debug("Removing {}", sn);
        synchronized (this.socketNodeList) {
            this.socketNodeList.remove(sn);
        }
    }
    
    static void usage(final String msg) {
        System.err.println(msg);
        System.err.println("Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
        System.exit(1);
    }
    
    static int parsePortNumber(final String portStr) {
        try {
            return Integer.parseInt(portStr);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            usage("Could not interpret port number [" + portStr + "].");
            return -1;
        }
    }
    
    public static void configureLC(final LoggerContext lc, final String configFile) throws JoranException {
        final JoranConfigurator configurator = new JoranConfigurator();
        lc.reset();
        configurator.setContext(lc);
        configurator.doConfigure(configFile);
    }
}

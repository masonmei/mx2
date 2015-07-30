// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.net;

import java.util.Properties;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import javax.naming.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;

public abstract class JMSAppenderBase<E> extends AppenderBase<E>
{
    protected String securityPrincipalName;
    protected String securityCredentials;
    protected String initialContextFactoryName;
    protected String urlPkgPrefixes;
    protected String providerURL;
    protected String userName;
    protected String password;
    
    protected Object lookup(final Context ctx, final String name) throws NamingException {
        try {
            return ctx.lookup(name);
        }
        catch (NameNotFoundException e) {
            this.addError("Could not find name [" + name + "].");
            throw e;
        }
    }
    
    public Context buildJNDIContext() throws NamingException {
        Context jndi = null;
        if (this.initialContextFactoryName != null) {
            final Properties env = this.buildEnvProperties();
            jndi = new InitialContext(env);
        }
        else {
            jndi = new InitialContext();
        }
        return jndi;
    }
    
    public Properties buildEnvProperties() {
        final Properties env = new Properties();
        ((Hashtable<String, String>)env).put("java.naming.factory.initial", this.initialContextFactoryName);
        if (this.providerURL != null) {
            ((Hashtable<String, String>)env).put("java.naming.provider.url", this.providerURL);
        }
        else {
            this.addWarn("You have set InitialContextFactoryName option but not the ProviderURL. This is likely to cause problems.");
        }
        if (this.urlPkgPrefixes != null) {
            ((Hashtable<String, String>)env).put("java.naming.factory.url.pkgs", this.urlPkgPrefixes);
        }
        if (this.securityPrincipalName != null) {
            ((Hashtable<String, String>)env).put("java.naming.security.principal", this.securityPrincipalName);
            if (this.securityCredentials != null) {
                ((Hashtable<String, String>)env).put("java.naming.security.credentials", this.securityCredentials);
            }
            else {
                this.addWarn("You have set SecurityPrincipalName option but not the SecurityCredentials. This is likely to cause problems.");
            }
        }
        return env;
    }
    
    public String getInitialContextFactoryName() {
        return this.initialContextFactoryName;
    }
    
    public void setInitialContextFactoryName(final String initialContextFactoryName) {
        this.initialContextFactoryName = initialContextFactoryName;
    }
    
    public String getProviderURL() {
        return this.providerURL;
    }
    
    public void setProviderURL(final String providerURL) {
        this.providerURL = providerURL;
    }
    
    public String getURLPkgPrefixes() {
        return this.urlPkgPrefixes;
    }
    
    public void setURLPkgPrefixes(final String urlPkgPrefixes) {
        this.urlPkgPrefixes = urlPkgPrefixes;
    }
    
    public String getSecurityCredentials() {
        return this.securityCredentials;
    }
    
    public void setSecurityCredentials(final String securityCredentials) {
        this.securityCredentials = securityCredentials;
    }
    
    public String getSecurityPrincipalName() {
        return this.securityPrincipalName;
    }
    
    public void setSecurityPrincipalName(final String securityPrincipalName) {
        this.securityPrincipalName = securityPrincipalName;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
}

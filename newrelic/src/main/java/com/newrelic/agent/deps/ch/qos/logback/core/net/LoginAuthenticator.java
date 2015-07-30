// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.net;

import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;

public class LoginAuthenticator extends Authenticator
{
    String username;
    String password;
    
    LoginAuthenticator(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
    
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password);
    }
}

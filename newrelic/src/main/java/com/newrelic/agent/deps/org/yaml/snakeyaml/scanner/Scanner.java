// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.scanner;

import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.Token;
import java.util.List;

public interface Scanner
{
    boolean checkToken(List<Class<? extends Token>> p0);
    
    boolean checkToken(Class<? extends Token> p0);
    
    Token peekToken();
    
    Token getToken();
}

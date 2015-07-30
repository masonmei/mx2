// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface Service
{
    Service startAsync();
    
    boolean isRunning();
    
    State state();
    
    Service stopAsync();
    
    void awaitRunning();
    
    void awaitRunning(long p0, TimeUnit p1) throws TimeoutException;
    
    void awaitTerminated();
    
    void awaitTerminated(long p0, TimeUnit p1) throws TimeoutException;
    
    Throwable failureCause();
    
    void addListener(Listener p0, Executor p1);
    
    @Beta
    public enum State
    {
        NEW {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        STARTING {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        RUNNING {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        STOPPING {
            @Override
            boolean isTerminal() {
                return false;
            }
        }, 
        TERMINATED {
            @Override
            boolean isTerminal() {
                return true;
            }
        }, 
        FAILED {
            @Override
            boolean isTerminal() {
                return true;
            }
        };
        
        abstract boolean isTerminal();
    }
    
    @Beta
    public abstract static class Listener
    {
        public void starting() {
        }
        
        public void running() {
        }
        
        public void stopping(State from) {
        }
        
        public void terminated(State from) {
        }
        
        public void failed(State from, Throwable failure) {
        }
    }
}

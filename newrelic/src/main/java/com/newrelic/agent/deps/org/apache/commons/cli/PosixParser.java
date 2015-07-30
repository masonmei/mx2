// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class PosixParser extends Parser
{
    private List tokens;
    private boolean eatTheRest;
    private Option currentOption;
    private Options options;
    
    public PosixParser() {
        this.tokens = new ArrayList();
    }
    
    private void init() {
        this.eatTheRest = false;
        this.tokens.clear();
    }
    
    protected String[] flatten(final Options options, final String[] arguments, final boolean stopAtNonOption) {
        this.init();
        this.options = options;
        final Iterator iter = Arrays.asList(arguments).iterator();
        while (iter.hasNext()) {
            final String token = iter.next();
            if (token.startsWith("--")) {
                final int pos = token.indexOf(61);
                final String opt = (pos == -1) ? token : token.substring(0, pos);
                if (!options.hasOption(opt)) {
                    this.processNonOptionToken(token, stopAtNonOption);
                }
                else {
                    this.currentOption = options.getOption(opt);
                    this.tokens.add(opt);
                    if (pos != -1) {
                        this.tokens.add(token.substring(pos + 1));
                    }
                }
            }
            else if ("-".equals(token)) {
                this.tokens.add(token);
            }
            else if (token.startsWith("-")) {
                if (token.length() == 2 || options.hasOption(token)) {
                    this.processOptionToken(token, stopAtNonOption);
                }
                else {
                    this.burstToken(token, stopAtNonOption);
                }
            }
            else {
                this.processNonOptionToken(token, stopAtNonOption);
            }
            this.gobble(iter);
        }
        return this.tokens.toArray(new String[this.tokens.size()]);
    }
    
    private void gobble(final Iterator iter) {
        if (this.eatTheRest) {
            while (iter.hasNext()) {
                this.tokens.add(iter.next());
            }
        }
    }
    
    private void processNonOptionToken(final String value, final boolean stopAtNonOption) {
        if (stopAtNonOption && (this.currentOption == null || !this.currentOption.hasArg())) {
            this.eatTheRest = true;
            this.tokens.add("--");
        }
        this.tokens.add(value);
    }
    
    private void processOptionToken(final String token, final boolean stopAtNonOption) {
        if (stopAtNonOption && !this.options.hasOption(token)) {
            this.eatTheRest = true;
        }
        if (this.options.hasOption(token)) {
            this.currentOption = this.options.getOption(token);
        }
        this.tokens.add(token);
    }
    
    protected void burstToken(final String token, final boolean stopAtNonOption) {
        int i = 1;
        while (i < token.length()) {
            final String ch = String.valueOf(token.charAt(i));
            if (this.options.hasOption(ch)) {
                this.tokens.add("-" + ch);
                this.currentOption = this.options.getOption(ch);
                if (this.currentOption.hasArg() && token.length() != i + 1) {
                    this.tokens.add(token.substring(i + 1));
                    break;
                }
                ++i;
            }
            else {
                if (stopAtNonOption) {
                    this.processNonOptionToken(token.substring(i), true);
                    break;
                }
                this.tokens.add(token);
                break;
            }
        }
    }
}

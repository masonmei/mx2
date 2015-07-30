// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.List;
import java.util.ArrayList;

public class GnuParser extends Parser
{
    protected String[] flatten(final Options options, final String[] arguments, final boolean stopAtNonOption) {
        final List tokens = new ArrayList();
        boolean eatTheRest = false;
        for (int i = 0; i < arguments.length; ++i) {
            final String arg = arguments[i];
            if ("--".equals(arg)) {
                eatTheRest = true;
                tokens.add("--");
            }
            else if ("-".equals(arg)) {
                tokens.add("-");
            }
            else if (arg.startsWith("-")) {
                final String opt = Util.stripLeadingHyphens(arg);
                if (options.hasOption(opt)) {
                    tokens.add(arg);
                }
                else if (opt.indexOf(61) != -1 && options.hasOption(opt.substring(0, opt.indexOf(61)))) {
                    tokens.add(arg.substring(0, arg.indexOf(61)));
                    tokens.add(arg.substring(arg.indexOf(61) + 1));
                }
                else if (options.hasOption(arg.substring(0, 2))) {
                    tokens.add(arg.substring(0, 2));
                    tokens.add(arg.substring(2));
                }
                else {
                    eatTheRest = stopAtNonOption;
                    tokens.add(arg);
                }
            }
            else {
                tokens.add(arg);
            }
            if (eatTheRest) {
                ++i;
                while (i < arguments.length) {
                    tokens.add(arguments[i]);
                    ++i;
                }
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }
}

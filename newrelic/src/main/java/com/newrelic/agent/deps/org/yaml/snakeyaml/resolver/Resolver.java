// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.resolver;

import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.NodeId;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Resolver
{
    private static final String DEFAULT_SCALAR_TAG = "tag:yaml.org,2002:str";
    private static final String DEFAULT_SEQUENCE_TAG = "tag:yaml.org,2002:seq";
    private static final String DEFAULT_MAPPING_TAG = "tag:yaml.org,2002:map";
    private static final Pattern BOOL;
    private static final Pattern FLOAT;
    private static final Pattern INT;
    private static final Pattern MERGE;
    private static final Pattern NULL;
    private static final Pattern EMPTY;
    private static final Pattern TIMESTAMP;
    private static final Pattern VALUE;
    private static final Pattern YAML;
    private Map<Character, List<ResolverTuple>> yamlImplicitResolvers;
    
    public Resolver() {
        this.yamlImplicitResolvers = new HashMap<Character, List<ResolverTuple>>();
        this.addImplicitResolver("tag:yaml.org,2002:bool", Resolver.BOOL, "yYnNtTfFoO");
        this.addImplicitResolver("tag:yaml.org,2002:float", Resolver.FLOAT, "-+0123456789.");
        this.addImplicitResolver("tag:yaml.org,2002:int", Resolver.INT, "-+0123456789");
        this.addImplicitResolver("tag:yaml.org,2002:merge", Resolver.MERGE, "<");
        this.addImplicitResolver("tag:yaml.org,2002:null", Resolver.NULL, "~nN\u0000");
        this.addImplicitResolver("tag:yaml.org,2002:null", Resolver.EMPTY, null);
        this.addImplicitResolver("tag:yaml.org,2002:timestamp", Resolver.TIMESTAMP, "0123456789");
        this.addImplicitResolver("tag:yaml.org,2002:value", Resolver.VALUE, "=");
        this.addImplicitResolver("tag:yaml.org,2002:yaml", Resolver.YAML, "!&*");
    }
    
    public void addImplicitResolver(final String tag, final Pattern regexp, final String first) {
        if (first == null) {
            List<ResolverTuple> curr = this.yamlImplicitResolvers.get(null);
            if (curr == null) {
                curr = new LinkedList<ResolverTuple>();
                this.yamlImplicitResolvers.put(null, curr);
            }
            curr.add(new ResolverTuple(tag, regexp));
        }
        else {
            final char[] chrs = first.toCharArray();
            for (int i = 0, j = chrs.length; i < j; ++i) {
                Character theC = new Character(chrs[i]);
                if (theC == '\0') {
                    theC = null;
                }
                List<ResolverTuple> curr2 = this.yamlImplicitResolvers.get(theC);
                if (curr2 == null) {
                    curr2 = new LinkedList<ResolverTuple>();
                    this.yamlImplicitResolvers.put(theC, curr2);
                }
                curr2.add(new ResolverTuple(tag, regexp));
            }
        }
    }
    
    public String resolve(final NodeId kind, final String value, final boolean implicit) {
        if (kind == NodeId.scalar && implicit) {
            List<ResolverTuple> resolvers = null;
            if ("".equals(value)) {
                resolvers = this.yamlImplicitResolvers.get('\0');
            }
            else {
                resolvers = this.yamlImplicitResolvers.get(value.charAt(0));
            }
            if (resolvers != null) {
                for (final ResolverTuple v : resolvers) {
                    final String tag = v.getTag();
                    final Pattern regexp = v.getRegexp();
                    if (regexp.matcher(value).matches()) {
                        return tag;
                    }
                }
            }
            if (this.yamlImplicitResolvers.containsKey(null)) {
                for (final ResolverTuple v : this.yamlImplicitResolvers.get(null)) {
                    final String tag = v.getTag();
                    final Pattern regexp = v.getRegexp();
                    if (regexp.matcher(value).matches()) {
                        return tag;
                    }
                }
            }
        }
        switch (kind) {
            case scalar: {
                return "tag:yaml.org,2002:str";
            }
            case sequence: {
                return "tag:yaml.org,2002:seq";
            }
            default: {
                return "tag:yaml.org,2002:map";
            }
        }
    }
    
    static {
        BOOL = Pattern.compile("^(?:yes|Yes|YES|no|No|NO|true|True|TRUE|false|False|FALSE|on|On|ON|off|Off|OFF)$");
        FLOAT = Pattern.compile("^(?:[-+]?(?:[0-9][0-9_]*)\\.[0-9_]*(?:[eE][-+][0-9]+)?|[-+]?(?:[0-9][0-9_]*)?\\.[0-9_]+(?:[eE][-+][0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
        INT = Pattern.compile("^(?:[-+]?0b[0-1_]+|[-+]?0[0-7_]+|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x[0-9a-fA-F_]+|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");
        MERGE = Pattern.compile("^(?:<<)$");
        NULL = Pattern.compile("^(?:~|null|Null|NULL| )$");
        EMPTY = Pattern.compile("^$");
        TIMESTAMP = Pattern.compile("^(?:[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?(?:[Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](?:\\.[0-9]*)?(?:[ \t]*(?:Z|[-+][0-9][0-9]?(?::[0-9][0-9])?))?)$");
        VALUE = Pattern.compile("^(?:=)$");
        YAML = Pattern.compile("^(?:!|&|\\*)$");
    }
}

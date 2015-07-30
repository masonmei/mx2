// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.util.Map;

public class DumperOptions
{
    private DefaultScalarStyle defaultStyle;
    private DefaultFlowStyle defaultFlowStyle;
    private boolean canonical;
    private boolean allowUnicode;
    private int indent;
    private int bestWidth;
    private LineBreak lineBreak;
    private boolean explicitStart;
    private boolean explicitEnd;
    private String explicitRoot;
    private Version version;
    private Map<String, String> tags;
    
    public DumperOptions() {
        this.defaultStyle = DefaultScalarStyle.PLAIN;
        this.defaultFlowStyle = DefaultFlowStyle.AUTO;
        this.canonical = false;
        this.allowUnicode = true;
        this.indent = 2;
        this.bestWidth = 80;
        this.lineBreak = LineBreak.LINUX;
        this.explicitStart = false;
        this.explicitEnd = false;
        this.explicitRoot = null;
        this.version = null;
        this.tags = null;
    }
    
    public boolean isAllowUnicode() {
        return this.allowUnicode;
    }
    
    public void setAllowUnicode(final boolean allowUnicode) {
        this.allowUnicode = allowUnicode;
    }
    
    public DefaultScalarStyle getDefaultStyle() {
        return this.defaultStyle;
    }
    
    public void setDefaultStyle(final DefaultScalarStyle defaultStyle) {
        if (defaultStyle == null) {
            throw new NullPointerException("Use DefaultScalarStyle enum.");
        }
        this.defaultStyle = defaultStyle;
    }
    
    public void setIndent(final int indent) {
        if (indent < 1) {
            throw new YAMLException("Indent must be at least 1");
        }
        if (indent > 10) {
            throw new YAMLException("Indent must be at most 10");
        }
        this.indent = indent;
    }
    
    public int getIndent() {
        return this.indent;
    }
    
    public void setVersion(final Version version) {
        this.version = version;
    }
    
    public Version getVersion() {
        return this.version;
    }
    
    public DumperOptions setCanonical(final boolean canonical) {
        this.canonical = canonical;
        return this;
    }
    
    public boolean isCanonical() {
        return this.canonical;
    }
    
    public void setWidth(final int bestWidth) {
        this.bestWidth = bestWidth;
    }
    
    public int getWidth() {
        return this.bestWidth;
    }
    
    public LineBreak getLineBreak() {
        return this.lineBreak;
    }
    
    public void setDefaultFlowStyle(final DefaultFlowStyle defaultFlowStyle) {
        if (defaultFlowStyle == null) {
            throw new NullPointerException("Use DefaultFlowStyle enum.");
        }
        this.defaultFlowStyle = defaultFlowStyle;
    }
    
    public DefaultFlowStyle getDefaultFlowStyle() {
        return this.defaultFlowStyle;
    }
    
    public String getExplicitRoot() {
        return this.explicitRoot;
    }
    
    public void setExplicitRoot(final String expRoot) {
        if (expRoot == null) {
            throw new NullPointerException("Root tag must be specified.");
        }
        this.explicitRoot = expRoot;
    }
    
    public void setLineBreak(final LineBreak lineBreak) {
        if (lineBreak == null) {
            throw new NullPointerException("Specify line break.");
        }
        this.lineBreak = lineBreak;
    }
    
    public boolean isExplicitStart() {
        return this.explicitStart;
    }
    
    public void setExplicitStart(final boolean explicitStart) {
        this.explicitStart = explicitStart;
    }
    
    public boolean isExplicitEnd() {
        return this.explicitEnd;
    }
    
    public void setExplicitEnd(final boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
    }
    
    public Map<String, String> getTags() {
        return this.tags;
    }
    
    public void setTags(final Map<String, String> tags) {
        this.tags = tags;
    }
    
    public enum DefaultScalarStyle
    {
        DOUBLE_QUOTED(new Character('\"')), 
        SINGLE_QUOTED(new Character('\'')), 
        LITERAL(new Character('|')), 
        FOLDED(new Character('>')), 
        PLAIN((Character)null);
        
        private Character styleChar;
        
        private DefaultScalarStyle(final Character defaultStyle) {
            this.styleChar = defaultStyle;
        }
        
        public Character getChar() {
            return this.styleChar;
        }
        
        public String toString() {
            return "Scalar style: '" + this.styleChar + "'";
        }
    }
    
    public enum DefaultFlowStyle
    {
        FLOW(Boolean.TRUE), 
        BLOCK(Boolean.FALSE), 
        AUTO((Boolean)null);
        
        private Boolean styleBoolean;
        
        private DefaultFlowStyle(final Boolean defaultFlowStyle) {
            this.styleBoolean = defaultFlowStyle;
        }
        
        public Boolean getStyleBoolean() {
            return this.styleBoolean;
        }
        
        public String toString() {
            return "Flow style: '" + this.styleBoolean + "'";
        }
    }
    
    public enum LineBreak
    {
        WIN("\r\n"), 
        MAC("\r"), 
        LINUX("\n");
        
        private String lineBreak;
        
        private LineBreak(final String lineBreak) {
            this.lineBreak = lineBreak;
        }
        
        public String getString() {
            return this.lineBreak;
        }
        
        public String toString() {
            return "Line break: " + this.name();
        }
    }
    
    public enum Version
    {
        V1_0(new Integer[] { 1, 0 }), 
        V1_1(new Integer[] { 1, 1 });
        
        private Integer[] version;
        
        private Version(final Integer[] version) {
            this.version = version;
        }
        
        public Integer[] getArray() {
            return this.version;
        }
        
        public String toString() {
            return "Version: " + this.version[0] + "." + this.version[1];
        }
    }
}

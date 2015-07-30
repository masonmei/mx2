// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.parser;

import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowEntryToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.MappingEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.ValueToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.KeyToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.SequenceEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.DocumentEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.StreamEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.DocumentEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.DocumentStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.StreamEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.DocumentStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.StreamStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.StreamStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.MappingStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.ScalarEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.ScalarToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.SequenceStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockEntryToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.TagToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.AnchorToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.AliasEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.AliasToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.Token;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.DirectiveToken;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import com.newrelic.agent.deps.org.yaml.snakeyaml.scanner.ScannerImpl;
import com.newrelic.agent.deps.org.yaml.snakeyaml.reader.Reader;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.LinkedList;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.Event;
import com.newrelic.agent.deps.org.yaml.snakeyaml.scanner.Scanner;
import java.util.Map;

public final class ParserImpl implements Parser
{
    private static final Map<String, String> DEFAULT_TAGS;
    private final Scanner scanner;
    private Event currentEvent;
    private List<Integer> yamlVersion;
    private Map<String, String> tagHandles;
    private final LinkedList<Production> states;
    private final LinkedList<Mark> marks;
    private Production state;
    
    public ParserImpl(final Reader reader) {
        this.scanner = new ScannerImpl(reader);
        this.currentEvent = null;
        this.yamlVersion = null;
        this.tagHandles = new HashMap<String, String>();
        this.states = new LinkedList<Production>();
        this.marks = new LinkedList<Mark>();
        this.state = new ParseStreamStart();
    }
    
    public boolean checkEvent(final List<Class<? extends Event>> choices) {
        this.peekEvent();
        if (this.currentEvent != null) {
            if (choices.size() == 0) {
                return true;
            }
            for (final Class<? extends Event> class1 : choices) {
                if (class1.isInstance(this.currentEvent)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean checkEvent(final Class<? extends Event> cls) {
        final List<Class<? extends Event>> list = new ArrayList<Class<? extends Event>>(1);
        list.add(cls);
        return this.checkEvent(list);
    }
    
    public Event peekEvent() {
        if (this.currentEvent == null && this.state != null) {
            this.currentEvent = this.state.produce();
        }
        return this.currentEvent;
    }
    
    public Event getEvent() {
        this.peekEvent();
        final Event value = this.currentEvent;
        this.currentEvent = null;
        return value;
    }
    
    private List<Object> processDirectives() {
        this.yamlVersion = null;
        this.tagHandles = new HashMap<String, String>();
        while (this.scanner.checkToken(DirectiveToken.class)) {
            final DirectiveToken token = (DirectiveToken)this.scanner.getToken();
            if (token.getName().equals("YAML")) {
                if (this.yamlVersion != null) {
                    throw new ParserException(null, null, "found duplicate YAML directive", token.getStartMark());
                }
                final List<Integer> value = (List<Integer>)token.getValue();
                final Integer major = value.get(0);
                if (major != 1) {
                    throw new ParserException(null, null, "found incompatible YAML document (version 1.* is required)", token.getStartMark());
                }
                this.yamlVersion = (List<Integer>)token.getValue();
            }
            else {
                if (!token.getName().equals("TAG")) {
                    continue;
                }
                final List<String> value2 = (List<String>)token.getValue();
                final String handle = value2.get(0);
                final String prefix = value2.get(1);
                if (this.tagHandles.containsKey(handle)) {
                    throw new ParserException(null, null, "duplicate tag handle " + handle, token.getStartMark());
                }
                this.tagHandles.put(handle, prefix);
            }
        }
        final List<Object> value3 = new ArrayList<Object>(2);
        value3.add(this.yamlVersion);
        if (!this.tagHandles.isEmpty()) {
            value3.add(new HashMap(this.tagHandles));
        }
        else {
            value3.add(new HashMap());
        }
        for (final String key : ParserImpl.DEFAULT_TAGS.keySet()) {
            if (!this.tagHandles.containsKey(key)) {
                this.tagHandles.put(key, ParserImpl.DEFAULT_TAGS.get(key));
            }
        }
        return value3;
    }
    
    private Event parseFlowNode() {
        return this.parseNode(false, false);
    }
    
    private Event parseBlockNodeOrIndentlessSequence() {
        return this.parseNode(true, true);
    }
    
    private Event parseNode(final boolean block, final boolean indentlessSequence) {
        Mark startMark = null;
        Mark endMark = null;
        Mark tagMark = null;
        Event event;
        if (this.scanner.checkToken(AliasToken.class)) {
            final AliasToken token = (AliasToken)this.scanner.getToken();
            event = new AliasEvent(token.getValue(), token.getStartMark(), token.getEndMark());
            this.state = this.states.removeLast();
        }
        else {
            String anchor = null;
            String[] tagTokenTag = null;
            if (this.scanner.checkToken(AnchorToken.class)) {
                final AnchorToken token2 = (AnchorToken)this.scanner.getToken();
                startMark = token2.getStartMark();
                endMark = token2.getEndMark();
                anchor = token2.getValue();
                if (this.scanner.checkToken(TagToken.class)) {
                    final TagToken tagToken = (TagToken)this.scanner.getToken();
                    tagMark = tagToken.getStartMark();
                    endMark = tagToken.getEndMark();
                    tagTokenTag = tagToken.getValue();
                }
            }
            else if (this.scanner.checkToken(TagToken.class)) {
                final TagToken tagToken2 = (TagToken)this.scanner.getToken();
                startMark = (tagMark = tagToken2.getStartMark());
                endMark = tagToken2.getEndMark();
                tagTokenTag = tagToken2.getValue();
                if (this.scanner.checkToken(AnchorToken.class)) {
                    final AnchorToken token3 = (AnchorToken)this.scanner.getToken();
                    endMark = token3.getEndMark();
                    anchor = token3.getValue();
                }
            }
            String tag = null;
            if (tagTokenTag != null) {
                final String handle = tagTokenTag[0];
                final String suffix = tagTokenTag[1];
                if (handle != null) {
                    if (!this.tagHandles.containsKey(handle)) {
                        throw new ParserException("while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
                    }
                    tag = this.tagHandles.get(handle) + suffix;
                }
                else {
                    tag = suffix;
                }
            }
            if (startMark == null) {
                startMark = (endMark = this.scanner.peekToken().getStartMark());
            }
            event = null;
            final boolean implicit = tag == null || tag.equals("!");
            if (indentlessSequence && this.scanner.checkToken(BlockEntryToken.class)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.FALSE);
                this.state = new ParseIndentlessSequenceEntry();
            }
            else if (this.scanner.checkToken(ScalarToken.class)) {
                final ScalarToken token4 = (ScalarToken)this.scanner.getToken();
                endMark = token4.getEndMark();
                final boolean[] implicitValues = new boolean[2];
                if ((token4.getPlain() && tag == null) || "!".equals(tag)) {
                    implicitValues[0] = true;
                    implicitValues[1] = false;
                }
                else if (tag == null) {
                    implicitValues[0] = false;
                    implicitValues[1] = true;
                }
                else {
                    implicitValues[1] = (implicitValues[0] = false);
                }
                event = new ScalarEvent(anchor, tag, implicitValues, token4.getValue(), startMark, endMark, token4.getStyle());
                this.state = this.states.removeLast();
            }
            else if (this.scanner.checkToken(FlowSequenceStartToken.class)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.TRUE);
                this.state = new ParseFlowSequenceFirstEntry();
            }
            else if (this.scanner.checkToken(FlowMappingStartToken.class)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.TRUE);
                this.state = new ParseFlowMappingFirstKey();
            }
            else if (block && this.scanner.checkToken(BlockSequenceStartToken.class)) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.FALSE);
                this.state = new ParseBlockSequenceFirstEntry();
            }
            else if (block && this.scanner.checkToken(BlockMappingStartToken.class)) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.FALSE);
                this.state = new ParseBlockMappingFirstKey();
            }
            else {
                if (anchor == null && tag == null) {
                    String node;
                    if (block) {
                        node = "block";
                    }
                    else {
                        node = "flow";
                    }
                    final Token token5 = this.scanner.peekToken();
                    throw new ParserException("while parsing a " + node + " node", startMark, "expected the node content, but found " + token5.getTokenId(), token5.getStartMark());
                }
                final boolean[] implicitValues2 = { implicit, false };
                event = new ScalarEvent(anchor, tag, implicitValues2, "", startMark, endMark, '\0');
                this.state = this.states.removeLast();
            }
        }
        return event;
    }
    
    private Event processEmptyScalar(final Mark mark) {
        final boolean[] value = { true, false };
        return new ScalarEvent(null, null, value, "", mark, mark, '\0');
    }
    
    static {
        (DEFAULT_TAGS = new HashMap<String, String>()).put("!", "!");
        ParserImpl.DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
    }
    
    private class ParseStreamStart implements Production
    {
        public Event produce() {
            final StreamStartToken token = (StreamStartToken)ParserImpl.this.scanner.getToken();
            final Event event = new StreamStartEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = new ParseImplicitDocumentStart();
            return event;
        }
    }
    
    private class ParseImplicitDocumentStart implements Production
    {
        public Event produce() {
            final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
            choices.add(DirectiveToken.class);
            choices.add(DocumentStartToken.class);
            choices.add(StreamEndToken.class);
            if (!ParserImpl.this.scanner.checkToken(choices)) {
                ParserImpl.this.tagHandles = ParserImpl.DEFAULT_TAGS;
                final Token token = ParserImpl.this.scanner.peekToken();
                final Mark endMark;
                final Mark startMark = endMark = token.getStartMark();
                final Event event = new DocumentStartEvent(startMark, endMark, false, null, null);
                ParserImpl.this.states.add(new ParseDocumentEnd());
                ParserImpl.this.state = new ParseBlockNode();
                return event;
            }
            final Production p = new ParseDocumentStart();
            return p.produce();
        }
    }
    
    private class ParseDocumentStart implements Production
    {
        public Event produce() {
            while (ParserImpl.this.scanner.checkToken(DocumentEndToken.class)) {
                ParserImpl.this.scanner.getToken();
            }
            Event event;
            if (!ParserImpl.this.scanner.checkToken(StreamEndToken.class)) {
                Token token = ParserImpl.this.scanner.peekToken();
                final Mark startMark = token.getStartMark();
                final List<Object> version_tags = ParserImpl.this.processDirectives();
                final List<Object> version = version_tags.get(0);
                final Map<String, String> tags = version_tags.get(1);
                if (!ParserImpl.this.scanner.checkToken(DocumentStartToken.class)) {
                    throw new ParserException(null, null, "expected '<document start>', but found " + ParserImpl.this.scanner.peekToken().getTokenId(), ParserImpl.this.scanner.peekToken().getStartMark());
                }
                token = ParserImpl.this.scanner.getToken();
                final Mark endMark = token.getEndMark();
                Integer[] versionInteger;
                if (version != null) {
                    versionInteger = new Integer[2];
                    versionInteger = version.toArray(versionInteger);
                }
                else {
                    versionInteger = null;
                }
                event = new DocumentStartEvent(startMark, endMark, true, versionInteger, tags);
                ParserImpl.this.states.add(new ParseDocumentEnd());
                ParserImpl.this.state = new ParseDocumentContent();
            }
            else {
                final StreamEndToken token2 = (StreamEndToken)ParserImpl.this.scanner.getToken();
                event = new StreamEndEvent(token2.getStartMark(), token2.getEndMark());
                if (!ParserImpl.this.states.isEmpty()) {
                    throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
                }
                if (!ParserImpl.this.marks.isEmpty()) {
                    throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
                }
                ParserImpl.this.state = null;
            }
            return event;
        }
    }
    
    private class ParseDocumentEnd implements Production
    {
        public Event produce() {
            Token token = ParserImpl.this.scanner.peekToken();
            Mark endMark;
            final Mark startMark = endMark = token.getStartMark();
            boolean explicit = false;
            if (ParserImpl.this.scanner.checkToken(DocumentEndToken.class)) {
                token = ParserImpl.this.scanner.getToken();
                endMark = token.getEndMark();
                explicit = true;
            }
            final Event event = new DocumentEndEvent(startMark, endMark, explicit);
            ParserImpl.this.state = new ParseDocumentStart();
            return event;
        }
    }
    
    private class ParseDocumentContent implements Production
    {
        public Event produce() {
            final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
            choices.add(DirectiveToken.class);
            choices.add(DocumentStartToken.class);
            choices.add(DocumentEndToken.class);
            choices.add(StreamEndToken.class);
            if (ParserImpl.this.scanner.checkToken(choices)) {
                final Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
                ParserImpl.this.state = ParserImpl.this.states.removeLast();
                return event;
            }
            final Production p = new ParseBlockNode();
            return p.produce();
        }
    }
    
    private class ParseBlockNode implements Production
    {
        public Event produce() {
            return ParserImpl.this.parseNode(true, false);
        }
    }
    
    private class ParseBlockSequenceFirstEntry implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.add(token.getStartMark());
            return new ParseBlockSequenceEntry().produce();
        }
    }
    
    private class ParseBlockSequenceEntry implements Production
    {
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(BlockEntryToken.class)) {
                final BlockEntryToken token = (BlockEntryToken)ParserImpl.this.scanner.getToken();
                final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
                choices.add(BlockEntryToken.class);
                choices.add(BlockEndToken.class);
                if (!ParserImpl.this.scanner.checkToken(choices)) {
                    ParserImpl.this.states.add(new ParseBlockSequenceEntry());
                    return new ParseBlockNode().produce();
                }
                ParserImpl.this.state = new ParseBlockSequenceEntry();
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            else {
                if (!ParserImpl.this.scanner.checkToken(BlockEndToken.class)) {
                    final Token token2 = ParserImpl.this.scanner.peekToken();
                    throw new ParserException("while parsing a block collection", ParserImpl.this.marks.getLast(), "expected <block end>, but found " + token2.getTokenId(), token2.getStartMark());
                }
                final Token token2 = ParserImpl.this.scanner.getToken();
                final Event event = new SequenceEndEvent(token2.getStartMark(), token2.getEndMark());
                ParserImpl.this.state = ParserImpl.this.states.removeLast();
                ParserImpl.this.marks.removeLast();
                return event;
            }
        }
    }
    
    private class ParseIndentlessSequenceEntry implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(BlockEntryToken.class)) {
                final Token token = ParserImpl.this.scanner.peekToken();
                final Event event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
                ParserImpl.this.state = ParserImpl.this.states.removeLast();
                return event;
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
            choices.add(BlockEntryToken.class);
            choices.add(KeyToken.class);
            choices.add(ValueToken.class);
            choices.add(BlockEndToken.class);
            if (!ParserImpl.this.scanner.checkToken(choices)) {
                ParserImpl.this.states.add(new ParseIndentlessSequenceEntry());
                return new ParseBlockNode().produce();
            }
            ParserImpl.this.state = new ParseIndentlessSequenceEntry();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseBlockMappingFirstKey implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.add(token.getStartMark());
            return new ParseBlockMappingKey().produce();
        }
    }
    
    private class ParseBlockMappingKey implements Production
    {
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(KeyToken.class)) {
                final Token token = ParserImpl.this.scanner.getToken();
                final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
                choices.add(KeyToken.class);
                choices.add(ValueToken.class);
                choices.add(BlockEndToken.class);
                if (!ParserImpl.this.scanner.checkToken(choices)) {
                    ParserImpl.this.states.add(new ParseBlockMappingValue());
                    return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
                }
                ParserImpl.this.state = new ParseBlockMappingValue();
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            else {
                if (!ParserImpl.this.scanner.checkToken(BlockEndToken.class)) {
                    final Token token = ParserImpl.this.scanner.peekToken();
                    throw new ParserException("while parsing a block mapping", ParserImpl.this.marks.getLast(), "expected <block end>, but found " + token.getTokenId(), token.getStartMark());
                }
                final Token token = ParserImpl.this.scanner.getToken();
                final Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
                ParserImpl.this.state = ParserImpl.this.states.removeLast();
                ParserImpl.this.marks.removeLast();
                return event;
            }
        }
    }
    
    private class ParseBlockMappingValue implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(ValueToken.class)) {
                ParserImpl.this.state = new ParseBlockMappingKey();
                final Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
            choices.add(KeyToken.class);
            choices.add(ValueToken.class);
            choices.add(BlockEndToken.class);
            if (!ParserImpl.this.scanner.checkToken(choices)) {
                ParserImpl.this.states.add(new ParseBlockMappingKey());
                return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            }
            ParserImpl.this.state = new ParseBlockMappingKey();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowSequenceFirstEntry implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.add(token.getStartMark());
            return new ParseFlowSequenceEntry(true).produce();
        }
    }
    
    private class ParseFlowSequenceEntry implements Production
    {
        private boolean first;
        
        public ParseFlowSequenceEntry(final boolean first) {
            this.first = false;
            this.first = first;
        }
        
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(FlowSequenceEndToken.class)) {
                if (!this.first) {
                    if (!ParserImpl.this.scanner.checkToken(FlowEntryToken.class)) {
                        final Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow sequence", ParserImpl.this.marks.getLast(), "expected ',' or ']', but got " + token.getTokenId(), token.getStartMark());
                    }
                    ParserImpl.this.scanner.getToken();
                }
                if (ParserImpl.this.scanner.checkToken(KeyToken.class)) {
                    final Token token = ParserImpl.this.scanner.peekToken();
                    final Event event = new MappingStartEvent(null, null, true, token.getStartMark(), token.getEndMark(), Boolean.TRUE);
                    ParserImpl.this.state = new ParseFlowSequenceEntryMappingKey();
                    return event;
                }
                if (!ParserImpl.this.scanner.checkToken(FlowSequenceEndToken.class)) {
                    ParserImpl.this.states.add(new ParseFlowSequenceEntry(false));
                    return ParserImpl.this.parseFlowNode();
                }
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final Event event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = ParserImpl.this.states.removeLast();
            ParserImpl.this.marks.removeLast();
            return event;
        }
    }
    
    private class ParseFlowSequenceEntryMappingKey implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
            choices.add(ValueToken.class);
            choices.add(FlowEntryToken.class);
            choices.add(FlowSequenceEndToken.class);
            if (!ParserImpl.this.scanner.checkToken(choices)) {
                ParserImpl.this.states.add(new ParseFlowSequenceEntryMappingValue());
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowSequenceEntryMappingValue();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowSequenceEntryMappingValue implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(ValueToken.class)) {
                ParserImpl.this.state = new ParseFlowSequenceEntryMappingEnd();
                final Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
            choices.add(FlowEntryToken.class);
            choices.add(FlowSequenceEndToken.class);
            if (!ParserImpl.this.scanner.checkToken(choices)) {
                ParserImpl.this.states.add(new ParseFlowSequenceEntryMappingEnd());
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowSequenceEntryMappingEnd();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowSequenceEntryMappingEnd implements Production
    {
        public Event produce() {
            ParserImpl.this.state = new ParseFlowSequenceEntry(false);
            final Token token = ParserImpl.this.scanner.peekToken();
            return new MappingEndEvent(token.getStartMark(), token.getEndMark());
        }
    }
    
    private class ParseFlowMappingFirstKey implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.add(token.getStartMark());
            return new ParseFlowMappingKey(true).produce();
        }
    }
    
    private class ParseFlowMappingKey implements Production
    {
        private boolean first;
        
        public ParseFlowMappingKey(final boolean first) {
            this.first = false;
            this.first = first;
        }
        
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(FlowMappingEndToken.class)) {
                if (!this.first) {
                    if (!ParserImpl.this.scanner.checkToken(FlowEntryToken.class)) {
                        final Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow mapping", ParserImpl.this.marks.getLast(), "expected ',' or '}', but got " + token.getTokenId(), token.getStartMark());
                    }
                    ParserImpl.this.scanner.getToken();
                }
                if (ParserImpl.this.scanner.checkToken(KeyToken.class)) {
                    final Token token = ParserImpl.this.scanner.getToken();
                    final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
                    choices.add(ValueToken.class);
                    choices.add(FlowEntryToken.class);
                    choices.add(FlowMappingEndToken.class);
                    if (!ParserImpl.this.scanner.checkToken(choices)) {
                        ParserImpl.this.states.add(new ParseFlowMappingValue());
                        return ParserImpl.this.parseFlowNode();
                    }
                    ParserImpl.this.state = new ParseFlowMappingValue();
                    return ParserImpl.this.processEmptyScalar(token.getEndMark());
                }
                else if (!ParserImpl.this.scanner.checkToken(FlowMappingEndToken.class)) {
                    ParserImpl.this.states.add(new ParseFlowMappingEmptyValue());
                    return ParserImpl.this.parseFlowNode();
                }
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = ParserImpl.this.states.removeLast();
            ParserImpl.this.marks.removeLast();
            return event;
        }
    }
    
    private class ParseFlowMappingValue implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(ValueToken.class)) {
                ParserImpl.this.state = new ParseFlowMappingKey(false);
                final Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final List<Class<? extends Token>> choices = new ArrayList<Class<? extends Token>>();
            choices.add(FlowEntryToken.class);
            choices.add(FlowMappingEndToken.class);
            if (!ParserImpl.this.scanner.checkToken(choices)) {
                ParserImpl.this.states.add(new ParseFlowMappingKey(false));
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowMappingKey(false);
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowMappingEmptyValue implements Production
    {
        public Event produce() {
            ParserImpl.this.state = new ParseFlowMappingKey(false);
            return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
        }
    }
}

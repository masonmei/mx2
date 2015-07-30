// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.composer;

import com.newrelic.agent.deps.org.yaml.snakeyaml.events.MappingEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.MappingNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.MappingStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.CollectionNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.SequenceEndEvent;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.SequenceNode;
import java.util.LinkedList;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.NodeId;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.SequenceStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.ScalarEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.NodeEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.AliasEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.StreamEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.Event;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.StreamStartEvent;
import java.util.HashMap;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import java.util.Map;
import com.newrelic.agent.deps.org.yaml.snakeyaml.resolver.Resolver;
import com.newrelic.agent.deps.org.yaml.snakeyaml.parser.Parser;

public class Composer
{
    private final Parser parser;
    private final Resolver resolver;
    private final Map<String, Node> anchors;
    
    public Composer(final Parser parser, final Resolver resolver) {
        this.parser = parser;
        this.resolver = resolver;
        this.anchors = new HashMap<String, Node>();
    }
    
    public boolean checkNode() {
        if (this.parser.checkEvent(StreamStartEvent.class)) {
            this.parser.getEvent();
        }
        return !this.parser.checkEvent(StreamEndEvent.class);
    }
    
    public Node getNode() {
        if (!this.parser.checkEvent(StreamEndEvent.class)) {
            return this.composeDocument();
        }
        return null;
    }
    
    public Node getSingleNode() {
        this.parser.getEvent();
        Node document = null;
        if (!this.parser.checkEvent(StreamEndEvent.class)) {
            document = this.composeDocument();
        }
        if (!this.parser.checkEvent(StreamEndEvent.class)) {
            final Event event = this.parser.getEvent();
            throw new ComposerException("expected a single document in the stream", document.getStartMark(), "but found another document", event.getStartMark());
        }
        this.parser.getEvent();
        return document;
    }
    
    private Node composeDocument() {
        this.parser.getEvent();
        final Node node = this.composeNode(null, null);
        this.parser.getEvent();
        this.anchors.clear();
        return node;
    }
    
    private Node composeNode(final Node parent, final Object index) {
        if (this.parser.checkEvent(AliasEvent.class)) {
            final AliasEvent event = (AliasEvent)this.parser.getEvent();
            final String anchor = event.getAnchor();
            if (!this.anchors.containsKey(anchor)) {
                throw new ComposerException(null, null, "found undefined alias " + anchor, event.getStartMark());
            }
            return this.anchors.get(anchor);
        }
        else {
            final NodeEvent event2 = (NodeEvent)this.parser.peekEvent();
            String anchor = null;
            anchor = event2.getAnchor();
            if (anchor != null && this.anchors.containsKey(anchor)) {
                throw new ComposerException("found duplicate anchor " + anchor + "; first occurence", this.anchors.get(anchor).getStartMark(), "second occurence", event2.getStartMark());
            }
            Node node = null;
            if (this.parser.checkEvent(ScalarEvent.class)) {
                node = this.composeScalarNode(anchor);
            }
            else if (this.parser.checkEvent(SequenceStartEvent.class)) {
                node = this.composeSequenceNode(anchor);
            }
            else {
                node = this.composeMappingNode(anchor);
            }
            return node;
        }
    }
    
    private Node composeScalarNode(final String anchor) {
        final ScalarEvent ev = (ScalarEvent)this.parser.getEvent();
        String tag = ev.getTag();
        if (tag == null || tag.equals("!")) {
            tag = this.resolver.resolve(NodeId.scalar, ev.getValue(), ev.getImplicit()[0]);
        }
        final Node node = new ScalarNode(tag, ev.getValue(), ev.getStartMark(), ev.getEndMark(), ev.getStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        return node;
    }
    
    private Node composeSequenceNode(final String anchor) {
        final SequenceStartEvent startEvent = (SequenceStartEvent)this.parser.getEvent();
        String tag = startEvent.getTag();
        if (tag == null || tag.equals("!")) {
            tag = this.resolver.resolve(NodeId.sequence, null, startEvent.getImplicit());
        }
        final CollectionNode node = new SequenceNode(tag, new LinkedList<Node>(), startEvent.getStartMark(), null, startEvent.getFlowStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        int index = 0;
        while (!this.parser.checkEvent(SequenceEndEvent.class)) {
            ((List)node.getValue()).add(this.composeNode(node, new Integer(index)));
            ++index;
        }
        final Event endEvent = this.parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }
    
    private Node composeMappingNode(final String anchor) {
        final MappingStartEvent startEvent = (MappingStartEvent)this.parser.getEvent();
        String tag = startEvent.getTag();
        if (tag == null || tag.equals("!")) {
            tag = this.resolver.resolve(NodeId.mapping, null, startEvent.getImplicit());
        }
        final MappingNode node = new MappingNode(tag, new LinkedList<Node[]>(), startEvent.getStartMark(), null, startEvent.getFlowStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        while (!this.parser.checkEvent(MappingEndEvent.class)) {
            final Node itemKey = this.composeNode(node, null);
            final Node itemValue = this.composeNode(node, itemKey);
            node.getValue().add(new Node[] { itemKey, itemValue });
        }
        final Event endEvent = this.parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }
}

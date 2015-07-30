// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.serializer;

import com.newrelic.agent.deps.org.yaml.snakeyaml.events.MappingEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.MappingStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.SequenceEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.SequenceStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.CollectionNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.ScalarEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.NodeId;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.AliasEvent;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.DocumentEndEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.DocumentStartEvent;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.StreamEndEvent;
import java.io.IOException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.Event;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import com.newrelic.agent.deps.org.yaml.snakeyaml.events.StreamStartEvent;
import java.util.HashMap;
import java.util.HashSet;
import com.newrelic.agent.deps.org.yaml.snakeyaml.DumperOptions;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import java.util.Set;
import java.util.Map;
import com.newrelic.agent.deps.org.yaml.snakeyaml.resolver.Resolver;
import com.newrelic.agent.deps.org.yaml.snakeyaml.emitter.Emitter;

public final class Serializer
{
    private final Emitter emitter;
    private final Resolver resolver;
    private boolean explicitStart;
    private boolean explicitEnd;
    private Integer[] useVersion;
    private Map<String, String> useTags;
    private Set<Node> serializedNodes;
    private Map<Node, String> anchors;
    private int lastAnchorId;
    private Boolean closed;
    private String explicitRoot;
    
    public Serializer(final Emitter emitter, final Resolver resolver, final DumperOptions opts) {
        this.emitter = emitter;
        this.resolver = resolver;
        this.explicitStart = opts.isExplicitStart();
        this.explicitEnd = opts.isExplicitEnd();
        if (opts.getVersion() != null) {
            this.useVersion = opts.getVersion().getArray();
        }
        this.useTags = opts.getTags();
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, String>();
        this.lastAnchorId = 0;
        this.closed = null;
        this.explicitRoot = opts.getExplicitRoot();
    }
    
    public void open() throws IOException {
        if (this.closed == null) {
            this.emitter.emit(new StreamStartEvent(null, null));
            this.closed = Boolean.FALSE;
            return;
        }
        if (Boolean.TRUE.equals(this.closed)) {
            throw new SerializerException("serializer is closed");
        }
        throw new SerializerException("serializer is already opened");
    }
    
    public void close() throws IOException {
        if (this.closed == null) {
            throw new SerializerException("serializer is not opened");
        }
        if (!Boolean.TRUE.equals(this.closed)) {
            this.emitter.emit(new StreamEndEvent(null, null));
            this.closed = Boolean.TRUE;
        }
    }
    
    public void serialize(final Node node) throws IOException {
        if (this.closed == null) {
            throw new SerializerException("serializer is not opened");
        }
        if (this.closed) {
            throw new SerializerException("serializer is closed");
        }
        this.emitter.emit(new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
        this.anchorNode(node);
        if (this.explicitRoot != null) {
            node.setTag(this.explicitRoot);
        }
        this.serializeNode(node, null, null);
        this.emitter.emit(new DocumentEndEvent(null, null, this.explicitEnd));
        this.serializedNodes.clear();
        this.anchors.clear();
        this.lastAnchorId = 0;
    }
    
    private void anchorNode(final Node node) {
        if (this.anchors.containsKey(node)) {
            String anchor = this.anchors.get(node);
            if (null == anchor) {
                anchor = this.generateAnchor();
                this.anchors.put(node, anchor);
            }
        }
        else {
            this.anchors.put(node, null);
            switch (node.getNodeId()) {
                case sequence: {
                    final List<Node> list = (List<Node>)node.getValue();
                    for (final Node item : list) {
                        this.anchorNode(item);
                    }
                    break;
                }
                case mapping: {
                    final List<Object[]> map = (List<Object[]>)node.getValue();
                    for (final Object[] object : map) {
                        final Node key = (Node)object[0];
                        final Node value = (Node)object[1];
                        this.anchorNode(key);
                        this.anchorNode(value);
                    }
                    break;
                }
            }
        }
    }
    
    private String generateAnchor() {
        ++this.lastAnchorId;
        final NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(3);
        final String anchorId = format.format(this.lastAnchorId);
        return "id" + anchorId;
    }
    
    private void serializeNode(final Node node, final Node parent, final Object index) throws IOException {
        final String tAlias = this.anchors.get(node);
        if (this.serializedNodes.contains(node)) {
            this.emitter.emit(new AliasEvent(tAlias, null, null));
        }
        else {
            this.serializedNodes.add(node);
            switch (node.getNodeId()) {
                case scalar: {
                    final String detectedTag = this.resolver.resolve(NodeId.scalar, (String)node.getValue(), true);
                    final String defaultTag = this.resolver.resolve(NodeId.scalar, (String)node.getValue(), false);
                    final boolean[] implicit = { false, false };
                    implicit[0] = node.getTag().equals(detectedTag);
                    implicit[1] = node.getTag().equals(defaultTag);
                    final ScalarEvent event = new ScalarEvent(tAlias, node.getTag(), implicit, (String)node.getValue(), null, null, ((ScalarNode)node).getStyle());
                    this.emitter.emit(event);
                    break;
                }
                case sequence: {
                    final boolean implicitS = node.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
                    this.emitter.emit(new SequenceStartEvent(tAlias, node.getTag(), implicitS, null, null, ((CollectionNode)node).getFlowStyle()));
                    int indexCounter = 0;
                    final List<Node> list = (List<Node>)node.getValue();
                    for (final Node item : list) {
                        this.serializeNode(item, node, new Integer(indexCounter));
                        ++indexCounter;
                    }
                    this.emitter.emit(new SequenceEndEvent(null, null));
                    break;
                }
                default: {
                    final String implicitTag = this.resolver.resolve(NodeId.mapping, null, true);
                    final boolean implicitM = node.getTag().equals(implicitTag);
                    this.emitter.emit(new MappingStartEvent(tAlias, node.getTag(), implicitM, null, null, ((CollectionNode)node).getFlowStyle()));
                    final List<Object[]> map = (List<Object[]>)node.getValue();
                    for (final Object[] row : map) {
                        final Node key = (Node)row[0];
                        final Node value = (Node)row[1];
                        this.serializeNode(key, node, null);
                        this.serializeNode(value, node, key);
                    }
                    this.emitter.emit(new MappingEndEvent(null, null));
                    break;
                }
            }
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.subst;

public class Node
{
    Type type;
    Object payload;
    Object defaultPart;
    Node next;
    
    public Node(final Type type, final Object payload) {
        this.type = type;
        this.payload = payload;
    }
    
    public Node(final Type type, final Object payload, final Object defaultPart) {
        this.type = type;
        this.payload = payload;
        this.defaultPart = defaultPart;
    }
    
    public String toString() {
        switch (this.type) {
            case LITERAL: {
                return "Node{type=" + this.type + ", payload='" + this.payload + "'}";
            }
            case VARIABLE: {
                final StringBuilder payloadBuf = new StringBuilder();
                final StringBuilder defaultPartBuf2 = new StringBuilder();
                if (this.defaultPart != null) {
                    this.recursive((Node)this.defaultPart, defaultPartBuf2);
                }
                this.recursive((Node)this.payload, payloadBuf);
                String r = "Node{type=" + this.type + ", payload='" + payloadBuf.toString() + "'";
                if (this.defaultPart != null) {
                    r = r + ", defaultPart=" + defaultPartBuf2.toString();
                }
                r += '}';
                return r;
            }
            default: {
                return null;
            }
        }
    }
    
    public void dump() {
        System.out.print(this.toString());
        System.out.print(" -> ");
        if (this.next != null) {
            this.next.dump();
        }
        else {
            System.out.print(" null");
        }
    }
    
    void recursive(final Node n, final StringBuilder sb) {
        for (Node c = n; c != null; c = c.next) {
            sb.append(c.toString()).append(" --> ");
        }
        sb.append("null ");
    }
    
    public void setNext(final Node n) {
        this.next = n;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Node node = (Node)o;
        if (this.type != node.type) {
            return false;
        }
        Label_0075: {
            if (this.payload != null) {
                if (this.payload.equals(node.payload)) {
                    break Label_0075;
                }
            }
            else if (node.payload == null) {
                break Label_0075;
            }
            return false;
        }
        Label_0108: {
            if (this.defaultPart != null) {
                if (this.defaultPart.equals(node.defaultPart)) {
                    break Label_0108;
                }
            }
            else if (node.defaultPart == null) {
                break Label_0108;
            }
            return false;
        }
        if (this.next != null) {
            if (this.next.equals(node.next)) {
                return true;
            }
        }
        else if (node.next == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = (this.type != null) ? this.type.hashCode() : 0;
        result = 31 * result + ((this.payload != null) ? this.payload.hashCode() : 0);
        result = 31 * result + ((this.defaultPart != null) ? this.defaultPart.hashCode() : 0);
        result = 31 * result + ((this.next != null) ? this.next.hashCode() : 0);
        return result;
    }
    
    enum Type
    {
        LITERAL, 
        VARIABLE;
    }
}

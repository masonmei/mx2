// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.ListIterator;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class InsnList
{
    private int size;
    private AbstractInsnNode first;
    private AbstractInsnNode last;
    AbstractInsnNode[] cache;
    
    public int size() {
        return this.size;
    }
    
    public AbstractInsnNode getFirst() {
        return this.first;
    }
    
    public AbstractInsnNode getLast() {
        return this.last;
    }
    
    public AbstractInsnNode get(final int n) {
        if (n < 0 || n >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        if (this.cache == null) {
            this.cache = this.toArray();
        }
        return this.cache[n];
    }
    
    public boolean contains(final AbstractInsnNode abstractInsnNode) {
        AbstractInsnNode abstractInsnNode2;
        for (abstractInsnNode2 = this.first; abstractInsnNode2 != null && abstractInsnNode2 != abstractInsnNode; abstractInsnNode2 = abstractInsnNode2.next) {}
        return abstractInsnNode2 != null;
    }
    
    public int indexOf(final AbstractInsnNode abstractInsnNode) {
        if (this.cache == null) {
            this.cache = this.toArray();
        }
        return abstractInsnNode.index;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        for (AbstractInsnNode abstractInsnNode = this.first; abstractInsnNode != null; abstractInsnNode = abstractInsnNode.next) {
            abstractInsnNode.accept(methodVisitor);
        }
    }
    
    public ListIterator iterator() {
        return this.iterator(0);
    }
    
    public ListIterator iterator(final int n) {
        return new InsnList$InsnListIterator(this, n);
    }
    
    public AbstractInsnNode[] toArray() {
        int n = 0;
        AbstractInsnNode abstractInsnNode = this.first;
        final AbstractInsnNode[] array = new AbstractInsnNode[this.size];
        while (abstractInsnNode != null) {
            array[n] = abstractInsnNode;
            abstractInsnNode.index = n++;
            abstractInsnNode = abstractInsnNode.next;
        }
        return array;
    }
    
    public void set(final AbstractInsnNode abstractInsnNode, final AbstractInsnNode abstractInsnNode2) {
        final AbstractInsnNode next = abstractInsnNode.next;
        abstractInsnNode2.next = next;
        if (next != null) {
            next.prev = abstractInsnNode2;
        }
        else {
            this.last = abstractInsnNode2;
        }
        final AbstractInsnNode prev = abstractInsnNode.prev;
        abstractInsnNode2.prev = prev;
        if (prev != null) {
            prev.next = abstractInsnNode2;
        }
        else {
            this.first = abstractInsnNode2;
        }
        if (this.cache != null) {
            final int index = abstractInsnNode.index;
            this.cache[index] = abstractInsnNode2;
            abstractInsnNode2.index = index;
        }
        else {
            abstractInsnNode2.index = 0;
        }
        abstractInsnNode.index = -1;
        abstractInsnNode.prev = null;
        abstractInsnNode.next = null;
    }
    
    public void add(final AbstractInsnNode abstractInsnNode) {
        ++this.size;
        if (this.last == null) {
            this.first = abstractInsnNode;
            this.last = abstractInsnNode;
        }
        else {
            this.last.next = abstractInsnNode;
            abstractInsnNode.prev = this.last;
        }
        this.last = abstractInsnNode;
        this.cache = null;
        abstractInsnNode.index = 0;
    }
    
    public void add(final InsnList list) {
        if (list.size == 0) {
            return;
        }
        this.size += list.size;
        if (this.last == null) {
            this.first = list.first;
            this.last = list.last;
        }
        else {
            final AbstractInsnNode first = list.first;
            this.last.next = first;
            first.prev = this.last;
            this.last = list.last;
        }
        this.cache = null;
        list.removeAll(false);
    }
    
    public void insert(final AbstractInsnNode abstractInsnNode) {
        ++this.size;
        if (this.first == null) {
            this.first = abstractInsnNode;
            this.last = abstractInsnNode;
        }
        else {
            this.first.prev = abstractInsnNode;
            abstractInsnNode.next = this.first;
        }
        this.first = abstractInsnNode;
        this.cache = null;
        abstractInsnNode.index = 0;
    }
    
    public void insert(final InsnList list) {
        if (list.size == 0) {
            return;
        }
        this.size += list.size;
        if (this.first == null) {
            this.first = list.first;
            this.last = list.last;
        }
        else {
            final AbstractInsnNode last = list.last;
            this.first.prev = last;
            last.next = this.first;
            this.first = list.first;
        }
        this.cache = null;
        list.removeAll(false);
    }
    
    public void insert(final AbstractInsnNode prev, final AbstractInsnNode next) {
        ++this.size;
        final AbstractInsnNode next2 = prev.next;
        if (next2 == null) {
            this.last = next;
        }
        else {
            next2.prev = next;
        }
        prev.next = next;
        next.next = next2;
        next.prev = prev;
        this.cache = null;
        next.index = 0;
    }
    
    public void insert(final AbstractInsnNode prev, final InsnList list) {
        if (list.size == 0) {
            return;
        }
        this.size += list.size;
        final AbstractInsnNode first = list.first;
        final AbstractInsnNode last = list.last;
        final AbstractInsnNode next = prev.next;
        if (next == null) {
            this.last = last;
        }
        else {
            next.prev = last;
        }
        prev.next = first;
        last.next = next;
        first.prev = prev;
        this.cache = null;
        list.removeAll(false);
    }
    
    public void insertBefore(final AbstractInsnNode next, final AbstractInsnNode prev) {
        ++this.size;
        final AbstractInsnNode prev2 = next.prev;
        if (prev2 == null) {
            this.first = prev;
        }
        else {
            prev2.next = prev;
        }
        next.prev = prev;
        prev.next = next;
        prev.prev = prev2;
        this.cache = null;
        prev.index = 0;
    }
    
    public void insertBefore(final AbstractInsnNode next, final InsnList list) {
        if (list.size == 0) {
            return;
        }
        this.size += list.size;
        final AbstractInsnNode first = list.first;
        final AbstractInsnNode last = list.last;
        final AbstractInsnNode prev = next.prev;
        if (prev == null) {
            this.first = first;
        }
        else {
            prev.next = first;
        }
        next.prev = last;
        last.next = next;
        first.prev = prev;
        this.cache = null;
        list.removeAll(false);
    }
    
    public void remove(final AbstractInsnNode abstractInsnNode) {
        --this.size;
        final AbstractInsnNode next = abstractInsnNode.next;
        final AbstractInsnNode prev = abstractInsnNode.prev;
        if (next == null) {
            if (prev == null) {
                this.first = null;
                this.last = null;
            }
            else {
                prev.next = null;
                this.last = prev;
            }
        }
        else if (prev == null) {
            this.first = next;
            next.prev = null;
        }
        else {
            prev.next = next;
            next.prev = prev;
        }
        this.cache = null;
        abstractInsnNode.index = -1;
        abstractInsnNode.prev = null;
        abstractInsnNode.next = null;
    }
    
    void removeAll(final boolean b) {
        if (b) {
            AbstractInsnNode next;
            for (AbstractInsnNode first = this.first; first != null; first = next) {
                next = first.next;
                first.index = -1;
                first.prev = null;
                first.next = null;
            }
        }
        this.size = 0;
        this.first = null;
        this.last = null;
        this.cache = null;
    }
    
    public void clear() {
        this.removeAll(false);
    }
    
    public void resetLabels() {
        for (AbstractInsnNode abstractInsnNode = this.first; abstractInsnNode != null; abstractInsnNode = abstractInsnNode.next) {
            if (abstractInsnNode instanceof LabelNode) {
                ((LabelNode)abstractInsnNode).resetLabel();
            }
        }
    }
}

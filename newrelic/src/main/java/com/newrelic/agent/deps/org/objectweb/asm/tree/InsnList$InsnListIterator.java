// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.NoSuchElementException;
import java.util.ListIterator;

final class InsnList$InsnListIterator implements ListIterator
{
    AbstractInsnNode next;
    AbstractInsnNode prev;
    AbstractInsnNode remove;
    final /* synthetic */ InsnList this$0;
    
    InsnList$InsnListIterator(final InsnList this$0, final int n) {
        this.this$0 = this$0;
        if (n == this$0.size()) {
            this.next = null;
            this.prev = this$0.getLast();
        }
        else {
            this.next = this$0.get(n);
            this.prev = this.next.prev;
        }
    }
    
    public boolean hasNext() {
        return this.next != null;
    }
    
    public Object next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        final AbstractInsnNode next = this.next;
        this.prev = next;
        this.next = next.next;
        return this.remove = next;
    }
    
    public void remove() {
        if (this.remove != null) {
            if (this.remove == this.next) {
                this.next = this.next.next;
            }
            else {
                this.prev = this.prev.prev;
            }
            this.this$0.remove(this.remove);
            this.remove = null;
            return;
        }
        throw new IllegalStateException();
    }
    
    public boolean hasPrevious() {
        return this.prev != null;
    }
    
    public Object previous() {
        final AbstractInsnNode prev = this.prev;
        this.next = prev;
        this.prev = prev.prev;
        return this.remove = prev;
    }
    
    public int nextIndex() {
        if (this.next == null) {
            return this.this$0.size();
        }
        if (this.this$0.cache == null) {
            this.this$0.cache = this.this$0.toArray();
        }
        return this.next.index;
    }
    
    public int previousIndex() {
        if (this.prev == null) {
            return -1;
        }
        if (this.this$0.cache == null) {
            this.this$0.cache = this.this$0.toArray();
        }
        return this.prev.index;
    }
    
    public void add(final Object o) {
        this.this$0.insertBefore(this.next, (AbstractInsnNode)o);
        this.prev = (AbstractInsnNode)o;
        this.remove = null;
    }
    
    public void set(final Object o) {
        this.this$0.set(this.next.prev, (AbstractInsnNode)o);
        this.prev = (AbstractInsnNode)o;
    }
}

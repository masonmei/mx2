// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import java.util.Random;

public class ReserviorSampledArrayList<E> extends FixedSizeArrayList<E>
{
    private final ThreadLocal<Random> random;
    
    public ReserviorSampledArrayList(final int reservoirSize) {
        super(reservoirSize);
        this.random = new ThreadLocal<Random>() {
            protected Random initialValue() {
                return new Random();
            }
        };
    }
    
    public Integer getSlot() {
        final int currentCount = this.numberOfTries.incrementAndGet() - 1;
        int insertIndex;
        if (currentCount < this.size) {
            insertIndex = currentCount;
        }
        else {
            insertIndex = this.random.get().nextInt(currentCount);
        }
        if (insertIndex >= this.size) {
            return null;
        }
        return insertIndex;
    }
    
    void setRandomFixedSeed(final long seed) {
        this.random.set(new Random(seed));
    }
}

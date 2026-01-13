/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.DoubleBlockProperties;

public static final class DoubleBlockProperties.PropertySource.Pair<S>
implements DoubleBlockProperties.PropertySource<S> {
    private final S first;
    private final S second;

    public DoubleBlockProperties.PropertySource.Pair(S first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
        return propertyRetriever.getFromBoth(this.first, this.second);
    }
}

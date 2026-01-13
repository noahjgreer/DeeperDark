/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.DoubleBlockProperties;

public static final class DoubleBlockProperties.PropertySource.Single<S>
implements DoubleBlockProperties.PropertySource<S> {
    private final S single;

    public DoubleBlockProperties.PropertySource.Single(S single) {
        this.single = single;
    }

    @Override
    public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
        return propertyRetriever.getFrom(this.single);
    }
}

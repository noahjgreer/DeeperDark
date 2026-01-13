/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.DoubleBlockProperties;

public static interface DoubleBlockProperties.PropertySource<S> {
    public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> var1);

    public static final class Single<S>
    implements DoubleBlockProperties.PropertySource<S> {
        private final S single;

        public Single(S single) {
            this.single = single;
        }

        @Override
        public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
            return propertyRetriever.getFrom(this.single);
        }
    }

    public static final class Pair<S>
    implements DoubleBlockProperties.PropertySource<S> {
        private final S first;
        private final S second;

        public Pair(S first, S second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
            return propertyRetriever.getFromBoth(this.first, this.second);
        }
    }
}

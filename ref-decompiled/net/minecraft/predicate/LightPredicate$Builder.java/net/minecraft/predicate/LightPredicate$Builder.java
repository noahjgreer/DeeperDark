/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate;

import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;

public static class LightPredicate.Builder {
    private NumberRange.IntRange light = NumberRange.IntRange.ANY;

    public static LightPredicate.Builder create() {
        return new LightPredicate.Builder();
    }

    public LightPredicate.Builder light(NumberRange.IntRange light) {
        this.light = light;
        return this;
    }

    public LightPredicate build() {
        return new LightPredicate(this.light);
    }
}

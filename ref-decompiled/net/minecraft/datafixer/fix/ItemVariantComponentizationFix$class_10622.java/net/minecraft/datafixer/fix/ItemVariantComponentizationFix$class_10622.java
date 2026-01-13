/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;

@FunctionalInterface
static interface ItemVariantComponentizationFix.class_10622
extends Function<Typed<?>, Typed<?>> {
    @Override
    default public Typed<?> apply(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixRemainder);
    }

    default public <T> Dynamic<T> fixRemainder(Dynamic<T> dynamic) {
        return dynamic.get("minecraft:bucket_entity_data").result().map(dynamic2 -> this.fixRemainder(dynamic, (Dynamic)dynamic2)).orElse(dynamic);
    }

    public <T> Dynamic<T> fixRemainder(Dynamic<T> var1, Dynamic<T> var2);
}

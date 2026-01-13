/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancement.criterion;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.predicate.NumberRange;

public record InventoryChangedCriterion.Conditions.Slots(NumberRange.IntRange occupied, NumberRange.IntRange full, NumberRange.IntRange empty) {
    public static final Codec<InventoryChangedCriterion.Conditions.Slots> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NumberRange.IntRange.CODEC.optionalFieldOf("occupied", (Object)NumberRange.IntRange.ANY).forGetter(InventoryChangedCriterion.Conditions.Slots::occupied), (App)NumberRange.IntRange.CODEC.optionalFieldOf("full", (Object)NumberRange.IntRange.ANY).forGetter(InventoryChangedCriterion.Conditions.Slots::full), (App)NumberRange.IntRange.CODEC.optionalFieldOf("empty", (Object)NumberRange.IntRange.ANY).forGetter(InventoryChangedCriterion.Conditions.Slots::empty)).apply((Applicative)instance, InventoryChangedCriterion.Conditions.Slots::new));
    public static final InventoryChangedCriterion.Conditions.Slots ANY = new InventoryChangedCriterion.Conditions.Slots(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY);

    public boolean test(int full, int empty, int occupied) {
        if (!this.full.test(full)) {
            return false;
        }
        if (!this.empty.test(empty)) {
            return false;
        }
        return this.occupied.test(occupied);
    }
}

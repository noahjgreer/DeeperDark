/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public record ImpossibleCriterion.Conditions() implements CriterionConditions
{
    public static final Codec<ImpossibleCriterion.Conditions> CODEC = MapCodec.unitCodec((Object)new ImpossibleCriterion.Conditions());

    @Override
    public void validate(LootContextPredicateValidator validator) {
    }
}

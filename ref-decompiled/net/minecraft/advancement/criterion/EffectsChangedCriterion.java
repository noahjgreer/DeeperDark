/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.EffectsChangedCriterion
 *  net.minecraft.advancement.criterion.EffectsChangedCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jspecify.annotations.Nullable;

public class EffectsChangedCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, @Nullable Entity source) {
        LootContext lootContext = source != null ? EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)source) : null;
        this.trigger(player, conditions -> conditions.matches(player, lootContext));
    }
}


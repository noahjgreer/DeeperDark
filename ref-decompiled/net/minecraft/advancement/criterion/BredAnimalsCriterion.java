/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.BredAnimalsCriterion
 *  net.minecraft.advancement.criterion.BredAnimalsCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.passive.PassiveEntity
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jspecify.annotations.Nullable;

public class BredAnimalsCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, AnimalEntity parent, AnimalEntity partner, @Nullable PassiveEntity child) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)parent);
        LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)partner);
        LootContext lootContext3 = child != null ? EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)child) : null;
        this.trigger(player, conditions -> conditions.matches(lootContext, lootContext2, lootContext3));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.PlayerHurtEntityCriterion
 *  net.minecraft.advancement.criterion.PlayerHurtEntityCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerHurtEntityCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Entity entity, DamageSource damage, float dealt, float taken, boolean blocked) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)entity);
        this.trigger(player, conditions -> conditions.matches(player, lootContext, damage, dealt, taken, blocked));
    }
}


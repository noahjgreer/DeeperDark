/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.TargetHitCriterion
 *  net.minecraft.advancement.criterion.TargetHitCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TargetHitCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Entity projectile, Vec3d hitPos, int signalStrength) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)projectile);
        this.trigger(player, conditions -> conditions.test(lootContext, hitPos, signalStrength));
    }
}


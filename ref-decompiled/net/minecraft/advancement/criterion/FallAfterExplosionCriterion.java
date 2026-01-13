/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.FallAfterExplosionCriterion
 *  net.minecraft.advancement.criterion.FallAfterExplosionCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.FallAfterExplosionCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

public class FallAfterExplosionCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Vec3d startPosition, @Nullable Entity cause) {
        Vec3d vec3d = player.getEntityPos();
        LootContext lootContext = cause != null ? EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)cause) : null;
        this.trigger(player, conditions -> conditions.matches(player.getEntityWorld(), startPosition, vec3d, lootContext));
    }
}


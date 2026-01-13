/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.EntityHurtPlayerCriterion
 *  net.minecraft.advancement.criterion.EntityHurtPlayerCriterion$Conditions
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class EntityHurtPlayerCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
        this.trigger(player, conditions -> conditions.matches(player, source, dealt, taken, blocked));
    }
}


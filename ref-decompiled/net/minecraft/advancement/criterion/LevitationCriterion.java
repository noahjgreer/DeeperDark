/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.LevitationCriterion
 *  net.minecraft.advancement.criterion.LevitationCriterion$Conditions
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
        this.trigger(player, conditions -> conditions.matches(player, startPos, duration));
    }
}


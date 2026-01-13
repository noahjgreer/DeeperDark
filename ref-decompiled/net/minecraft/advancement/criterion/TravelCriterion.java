/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.TravelCriterion
 *  net.minecraft.advancement.criterion.TravelCriterion$Conditions
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TravelCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Vec3d startPos) {
        Vec3d vec3d = player.getEntityPos();
        this.trigger(player, conditions -> conditions.matches(player.getEntityWorld(), startPos, vec3d));
    }
}


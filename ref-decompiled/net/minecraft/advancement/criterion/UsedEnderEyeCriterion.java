/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.UsedEnderEyeCriterion
 *  net.minecraft.advancement.criterion.UsedEnderEyeCriterion$Conditions
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.UsedEnderEyeCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, BlockPos strongholdPos) {
        double d = player.getX() - (double)strongholdPos.getX();
        double e = player.getZ() - (double)strongholdPos.getZ();
        double f = d * d + e * e;
        this.trigger(player, conditions -> conditions.matches(f));
    }
}


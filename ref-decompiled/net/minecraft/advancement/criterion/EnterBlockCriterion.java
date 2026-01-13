/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.EnterBlockCriterion
 *  net.minecraft.advancement.criterion.EnterBlockCriterion$Conditions
 *  net.minecraft.block.BlockState
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public class EnterBlockCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        this.trigger(player, conditions -> conditions.matches(state));
    }
}


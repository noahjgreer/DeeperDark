/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.DefaultBlockUseCriterion
 *  net.minecraft.advancement.criterion.DefaultBlockUseCriterion$Conditions
 *  net.minecraft.block.BlockState
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.loot.context.LootContext$Builder
 *  net.minecraft.loot.context.LootContextParameters
 *  net.minecraft.loot.context.LootContextTypes
 *  net.minecraft.loot.context.LootWorldContext
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.DefaultBlockUseCriterion;
import net.minecraft.block.BlockState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class DefaultBlockUseCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos) {
        ServerWorld serverWorld = player.getEntityWorld();
        BlockState blockState = serverWorld.getBlockState(pos);
        LootWorldContext lootWorldContext = new LootWorldContext.Builder(serverWorld).add(LootContextParameters.ORIGIN, (Object)pos.toCenterPos()).add(LootContextParameters.THIS_ENTITY, (Object)player).add(LootContextParameters.BLOCK_STATE, (Object)blockState).build(LootContextTypes.BLOCK_USE);
        LootContext lootContext = new LootContext.Builder(lootWorldContext).build(Optional.empty());
        this.trigger(player, conditions -> conditions.test(lootContext));
    }
}


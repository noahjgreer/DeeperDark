/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.InfestedBlock
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.mob.SilverfishEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.tag.EnchantmentTags
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.rule.GameRules
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;

/*
 * Exception performing whole class analysis ignored.
 */
public class InfestedBlock
extends Block {
    public static final MapCodec<InfestedBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.BLOCK.getCodec().fieldOf("host").forGetter(InfestedBlock::getRegularBlock), (App)InfestedBlock.createSettingsCodec()).apply((Applicative)instance, InfestedBlock::new));
    private final Block regularBlock;
    private static final Map<Block, Block> REGULAR_TO_INFESTED_BLOCK = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> REGULAR_TO_INFESTED_STATE = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> INFESTED_TO_REGULAR_STATE = Maps.newIdentityHashMap();

    public MapCodec<? extends InfestedBlock> getCodec() {
        return CODEC;
    }

    public InfestedBlock(Block regularBlock, AbstractBlock.Settings settings) {
        super(settings.hardness(regularBlock.getHardness() / 2.0f).resistance(0.75f));
        this.regularBlock = regularBlock;
        REGULAR_TO_INFESTED_BLOCK.put(regularBlock, this);
    }

    public Block getRegularBlock() {
        return this.regularBlock;
    }

    public static boolean isInfestable(BlockState block) {
        return REGULAR_TO_INFESTED_BLOCK.containsKey(block.getBlock());
    }

    private void spawnSilverfish(ServerWorld world, BlockPos pos) {
        SilverfishEntity silverfishEntity = (SilverfishEntity)EntityType.SILVERFISH.create((World)world, SpawnReason.TRIGGERED);
        if (silverfishEntity != null) {
            silverfishEntity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, 0.0f, 0.0f);
            world.spawnEntity((Entity)silverfishEntity);
            silverfishEntity.playSpawnEffects();
        }
    }

    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        if (((Boolean)world.getGameRules().getValue(GameRules.DO_TILE_DROPS)).booleanValue() && !EnchantmentHelper.hasAnyEnchantmentsIn((ItemStack)tool, (TagKey)EnchantmentTags.PREVENTS_INFESTED_SPAWNS)) {
            this.spawnSilverfish(world, pos);
        }
    }

    public static BlockState fromRegularState(BlockState regularState) {
        return InfestedBlock.copyProperties((Map)REGULAR_TO_INFESTED_STATE, (BlockState)regularState, () -> ((Block)REGULAR_TO_INFESTED_BLOCK.get(regularState.getBlock())).getDefaultState());
    }

    public BlockState toRegularState(BlockState infestedState) {
        return InfestedBlock.copyProperties((Map)INFESTED_TO_REGULAR_STATE, (BlockState)infestedState, () -> this.getRegularBlock().getDefaultState());
    }

    private static BlockState copyProperties(Map<BlockState, BlockState> stateMap, BlockState fromState, Supplier<BlockState> toStateSupplier) {
        return stateMap.computeIfAbsent(fromState, infestedState -> {
            BlockState blockState = (BlockState)toStateSupplier.get();
            for (Property property : infestedState.getProperties()) {
                blockState = blockState.contains(property) ? (BlockState)blockState.with(property, infestedState.get(property)) : blockState;
            }
            return blockState;
        });
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.IceBlock
 *  net.minecraft.block.TranslucentBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.EnchantmentTags
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.LightType
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class IceBlock
extends TranslucentBlock {
    public static final MapCodec<IceBlock> CODEC = IceBlock.createCodec(IceBlock::new);

    public MapCodec<? extends IceBlock> getCodec() {
        return CODEC;
    }

    public IceBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public static BlockState getMeltedState() {
        return Blocks.WATER.getDefaultState();
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!EnchantmentHelper.hasAnyEnchantmentsIn((ItemStack)tool, (TagKey)EnchantmentTags.PREVENTS_ICE_MELTING)) {
            if (((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.WATER_EVAPORATES_GAMEPLAY, pos)).booleanValue()) {
                world.removeBlock(pos, false);
                return;
            }
            BlockState blockState = world.getBlockState(pos.down());
            if (blockState.blocksMovement() || blockState.isLiquid()) {
                world.setBlockState(pos, IceBlock.getMeltedState());
            }
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity()) {
            this.melt(state, (World)world, pos);
        }
    }

    protected void melt(BlockState state, World world, BlockPos pos) {
        if (((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.WATER_EVAPORATES_GAMEPLAY, pos)).booleanValue()) {
            world.removeBlock(pos, false);
            return;
        }
        world.setBlockState(pos, IceBlock.getMeltedState());
        world.updateNeighbor(pos, IceBlock.getMeltedState().getBlock(), null);
    }
}


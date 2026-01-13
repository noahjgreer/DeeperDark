/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPlantStemBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CaveVines
 *  net.minecraft.block.CaveVinesHeadBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CaveVinesHeadBlock
extends AbstractPlantStemBlock
implements CaveVines {
    public static final MapCodec<CaveVinesHeadBlock> CODEC = CaveVinesHeadBlock.createCodec(CaveVinesHeadBlock::new);
    private static final float GROW_CHANCE = 0.11f;

    public MapCodec<CaveVinesHeadBlock> getCodec() {
        return CODEC;
    }

    public CaveVinesHeadBlock(AbstractBlock.Settings settings) {
        super(settings, Direction.DOWN, SHAPE, false, 0.1);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0))).with((Property)BERRIES, (Comparable)Boolean.valueOf(false)));
    }

    protected int getGrowthLength(Random random) {
        return 1;
    }

    protected boolean chooseStemState(BlockState state) {
        return state.isAir();
    }

    protected Block getPlant() {
        return Blocks.CAVE_VINES_PLANT;
    }

    protected BlockState copyState(BlockState from, BlockState to) {
        return (BlockState)to.with((Property)BERRIES, (Comparable)((Boolean)from.get((Property)BERRIES)));
    }

    protected BlockState age(BlockState state, Random random) {
        return (BlockState)super.age(state, random).with((Property)BERRIES, (Comparable)Boolean.valueOf(random.nextFloat() < 0.11f));
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)Items.GLOW_BERRIES);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return CaveVines.pickBerries((Entity)player, (BlockState)state, (World)world, (BlockPos)pos);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(new Property[]{BERRIES});
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return (Boolean)state.get((Property)BERRIES) == false;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, (BlockState)state.with((Property)BERRIES, (Comparable)Boolean.valueOf(true)), 2);
    }
}


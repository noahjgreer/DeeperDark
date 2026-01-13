/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CropBlock
 *  net.minecraft.block.FarmlandBlock
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.mob.RavagerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.rule.GameRules
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.rule.GameRules;

/*
 * Exception performing whole class analysis ignored.
 */
public class CropBlock
extends PlantBlock
implements Fertilizable {
    public static final MapCodec<CropBlock> CODEC = CropBlock.createCodec(CropBlock::new);
    public static final int MAX_AGE = 7;
    public static final IntProperty AGE = Properties.AGE_7;
    private static final VoxelShape[] SHAPES_BY_AGE = Block.createShapeArray((int)7, age -> Block.createColumnShape((double)16.0, (double)0.0, (double)(2 + age * 2)));

    public MapCodec<? extends CropBlock> getCodec() {
        return CODEC;
    }

    public CropBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)this.getAgeProperty(), (Comparable)Integer.valueOf(0)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_AGE[this.getAge(state)];
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    protected IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    public int getAge(BlockState state) {
        return (Integer)state.get((Property)this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return (BlockState)this.getDefaultState().with((Property)this.getAgeProperty(), (Comparable)Integer.valueOf(age));
    }

    public final boolean isMature(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    }

    protected boolean hasRandomTicks(BlockState state) {
        return !this.isMature(state);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        float f;
        int i;
        if (world.getBaseLightLevel(pos, 0) >= 9 && (i = this.getAge(state)) < this.getMaxAge() && random.nextInt((int)(25.0f / (f = CropBlock.getAvailableMoisture((Block)this, (BlockView)world, (BlockPos)pos))) + 1) == 0) {
            world.setBlockState(pos, this.withAge(i + 1), 2);
        }
    }

    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int i = Math.min(this.getMaxAge(), this.getAge(state) + this.getGrowthAmount(world));
        world.setBlockState(pos, this.withAge(i), 2);
    }

    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt((Random)world.random, (int)2, (int)5);
    }

    protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        boolean bl2;
        float f = 1.0f;
        BlockPos blockPos = pos.down();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float g = 0.0f;
                BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
                if (blockState.isOf(Blocks.FARMLAND)) {
                    g = 1.0f;
                    if ((Integer)blockState.get((Property)FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0f;
                    }
                }
                if (i != 0 || j != 0) {
                    g /= 4.0f;
                }
                f += g;
            }
        }
        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = world.getBlockState(blockPos4).isOf(block) || world.getBlockState(blockPos5).isOf(block);
        boolean bl3 = bl2 = world.getBlockState(blockPos2).isOf(block) || world.getBlockState(blockPos3).isOf(block);
        if (bl && bl2) {
            f /= 2.0f;
        } else {
            boolean bl32;
            boolean bl4 = bl32 = world.getBlockState(blockPos4.north()).isOf(block) || world.getBlockState(blockPos5.north()).isOf(block) || world.getBlockState(blockPos5.south()).isOf(block) || world.getBlockState(blockPos4.south()).isOf(block);
            if (bl32) {
                f /= 2.0f;
            }
        }
        return f;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return CropBlock.hasEnoughLightAt((WorldView)world, (BlockPos)pos) && super.canPlaceAt(state, world, pos);
    }

    protected static boolean hasEnoughLightAt(WorldView world, BlockPos pos) {
        return world.getBaseLightLevel(pos, 0) >= 8;
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (entity instanceof RavagerEntity && ((Boolean)serverWorld.getGameRules().getValue(GameRules.DO_MOB_GRIEFING)).booleanValue()) {
                serverWorld.breakBlock(pos, true, entity);
            }
        }
        super.onEntityCollision(state, world, pos, entity, handler, bl);
    }

    protected ItemConvertible getSeedsItem() {
        return Items.WHEAT_SEEDS;
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(this.getSeedsItem());
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !this.isMature(state);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.applyGrowth((World)world, pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }
}


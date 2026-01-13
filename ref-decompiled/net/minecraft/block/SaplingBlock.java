/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.SaplingBlock
 *  net.minecraft.block.SaplingGenerator
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/*
 * Exception performing whole class analysis ignored.
 */
public class SaplingBlock
extends PlantBlock
implements Fertilizable {
    public static final MapCodec<SaplingBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SaplingGenerator.CODEC.fieldOf("tree").forGetter(block -> block.generator), (App)SaplingBlock.createSettingsCodec()).apply((Applicative)instance, SaplingBlock::new));
    public static final IntProperty STAGE = Properties.STAGE;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)12.0);
    protected final SaplingGenerator generator;

    public MapCodec<? extends SaplingBlock> getCodec() {
        return CODEC;
    }

    public SaplingBlock(SaplingGenerator generator, AbstractBlock.Settings settings) {
        super(settings);
        this.generator = generator;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)STAGE, (Comparable)Integer.valueOf(0)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
            this.generate(world, pos, state, random);
        }
    }

    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        if ((Integer)state.get((Property)STAGE) == 0) {
            world.setBlockState(pos, (BlockState)state.cycle((Property)STAGE), 260);
        } else {
            this.generator.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random);
        }
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)world.random.nextFloat() < 0.45;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.generate(world, pos, state, random);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STAGE});
    }
}


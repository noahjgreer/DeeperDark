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
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TorchflowerBlock
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.Items
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
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
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

public class TorchflowerBlock
extends CropBlock {
    public static final MapCodec<TorchflowerBlock> CODEC = TorchflowerBlock.createCodec(TorchflowerBlock::new);
    public static final int field_42775 = 1;
    public static final IntProperty AGE = Properties.AGE_1;
    private static final VoxelShape[] SHAPES_BY_AGE = Block.createShapeArray((int)1, age -> Block.createColumnShape((double)6.0, (double)0.0, (double)(6 + age * 4)));
    private static final int field_44479 = 1;

    public MapCodec<TorchflowerBlock> getCodec() {
        return CODEC;
    }

    public TorchflowerBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_AGE[this.getAge(state)];
    }

    protected IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 2;
    }

    protected ItemConvertible getSeedsItem() {
        return Items.TORCHFLOWER_SEEDS;
    }

    public BlockState withAge(int age) {
        if (age == 2) {
            return Blocks.TORCHFLOWER.getDefaultState();
        }
        return super.withAge(age);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(3) != 0) {
            super.randomTick(state, world, pos, random);
        }
    }

    protected int getGrowthAmount(World world) {
        return 1;
    }
}


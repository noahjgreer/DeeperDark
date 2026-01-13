/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CropBlock
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.StemBlock
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/*
 * Exception performing whole class analysis ignored.
 */
public class StemBlock
extends PlantBlock
implements Fertilizable {
    public static final MapCodec<StemBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec((RegistryKey)RegistryKeys.BLOCK).fieldOf("fruit").forGetter(block -> block.gourdBlock), (App)RegistryKey.createCodec((RegistryKey)RegistryKeys.BLOCK).fieldOf("attached_stem").forGetter(block -> block.attachedStemBlock), (App)RegistryKey.createCodec((RegistryKey)RegistryKeys.ITEM).fieldOf("seed").forGetter(block -> block.pickBlockItem), (App)StemBlock.createSettingsCodec()).apply((Applicative)instance, StemBlock::new));
    public static final int MAX_AGE = 7;
    public static final IntProperty AGE = Properties.AGE_7;
    private static final VoxelShape[] SHAPES_BY_AGE = Block.createShapeArray((int)7, age -> Block.createColumnShape((double)2.0, (double)0.0, (double)(2 + age * 2)));
    private final RegistryKey<Block> gourdBlock;
    private final RegistryKey<Block> attachedStemBlock;
    private final RegistryKey<Item> pickBlockItem;

    public MapCodec<StemBlock> getCodec() {
        return CODEC;
    }

    public StemBlock(RegistryKey<Block> gourdBlock, RegistryKey<Block> attachedStemBlock, RegistryKey<Item> pickBlockItem, AbstractBlock.Settings settings) {
        super(settings);
        this.gourdBlock = gourdBlock;
        this.attachedStemBlock = attachedStemBlock;
        this.pickBlockItem = pickBlockItem;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_AGE[(Integer)state.get((Property)AGE)];
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) < 9) {
            return;
        }
        float f = CropBlock.getAvailableMoisture((Block)this, (BlockView)world, (BlockPos)pos);
        if (random.nextInt((int)(25.0f / f) + 1) == 0) {
            int i = (Integer)state.get((Property)AGE);
            if (i < 7) {
                state = (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i + 1));
                world.setBlockState(pos, state, 2);
            } else {
                Direction direction = Direction.Type.HORIZONTAL.random(random);
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos.down());
                if (world.getBlockState(blockPos).isAir() && (blockState.isOf(Blocks.FARMLAND) || blockState.isIn(BlockTags.DIRT))) {
                    Registry registry = world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
                    Optional optional = registry.getOptionalValue(this.gourdBlock);
                    Optional optional2 = registry.getOptionalValue(this.attachedStemBlock);
                    if (optional.isPresent() && optional2.isPresent()) {
                        world.setBlockState(blockPos, ((Block)optional.get()).getDefaultState());
                        world.setBlockState(pos, (BlockState)((Block)optional2.get()).getDefaultState().with((Property)HorizontalFacingBlock.FACING, (Comparable)direction));
                    }
                }
            }
        }
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)DataFixUtils.orElse((Optional)world.getRegistryManager().getOrThrow(RegistryKeys.ITEM).getOptionalValue(this.pickBlockItem), (Object)this));
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return (Integer)state.get((Property)AGE) != 7;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(7, (Integer)state.get((Property)AGE) + MathHelper.nextInt((Random)world.random, (int)2, (int)5));
        BlockState blockState = (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i));
        world.setBlockState(pos, blockState, 2);
        if (i == 7) {
            blockState.randomTick(world, pos, world.random);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }
}


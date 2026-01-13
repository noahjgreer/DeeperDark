/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.MultifaceBlock
 *  net.minecraft.block.MultifaceGrower
 *  net.minecraft.block.MultifaceGrower$GrowChecker
 *  net.minecraft.block.MultifaceGrower$GrowType
 *  net.minecraft.block.MultifaceGrowthBlock
 *  net.minecraft.block.SculkSpreadable
 *  net.minecraft.block.SculkVeinBlock
 *  net.minecraft.block.SculkVeinBlock$SculkVeinGrowChecker
 *  net.minecraft.block.entity.SculkSpreadManager
 *  net.minecraft.block.entity.SculkSpreadManager$Cursor
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Collection;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.MultifaceGrower;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/*
 * Exception performing whole class analysis ignored.
 */
public class SculkVeinBlock
extends MultifaceGrowthBlock
implements SculkSpreadable {
    public static final MapCodec<SculkVeinBlock> CODEC = SculkVeinBlock.createCodec(SculkVeinBlock::new);
    private final MultifaceGrower allGrowTypeGrower = new MultifaceGrower((MultifaceGrower.GrowChecker)new SculkVeinGrowChecker(this, MultifaceGrower.GROW_TYPES));
    private final MultifaceGrower samePositionOnlyGrower = new MultifaceGrower((MultifaceGrower.GrowChecker)new SculkVeinGrowChecker(this, new MultifaceGrower.GrowType[]{MultifaceGrower.GrowType.SAME_POSITION}));

    public MapCodec<SculkVeinBlock> getCodec() {
        return CODEC;
    }

    public SculkVeinBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public MultifaceGrower getGrower() {
        return this.allGrowTypeGrower;
    }

    public MultifaceGrower getSamePositionOnlyGrower() {
        return this.samePositionOnlyGrower;
    }

    public static boolean place(WorldAccess world, BlockPos pos, BlockState state, Collection<Direction> directions) {
        boolean bl = false;
        BlockState blockState = Blocks.SCULK_VEIN.getDefaultState();
        for (Direction direction : directions) {
            if (!SculkVeinBlock.canGrowOn((BlockView)world, (BlockPos)pos, (Direction)direction)) continue;
            blockState = (BlockState)blockState.with((Property)SculkVeinBlock.getProperty((Direction)direction), (Comparable)Boolean.valueOf(true));
            bl = true;
        }
        if (!bl) {
            return false;
        }
        if (!state.getFluidState().isEmpty()) {
            blockState = (BlockState)blockState.with((Property)MultifaceBlock.WATERLOGGED, (Comparable)Boolean.valueOf(true));
        }
        world.setBlockState(pos, blockState, 3);
        return true;
    }

    public void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, Random random) {
        if (!state.isOf((Block)this)) {
            return;
        }
        for (Direction direction : DIRECTIONS) {
            BooleanProperty booleanProperty = SculkVeinBlock.getProperty((Direction)direction);
            if (!((Boolean)state.get((Property)booleanProperty)).booleanValue() || !world.getBlockState(pos.offset(direction)).isOf(Blocks.SCULK)) continue;
            state = (BlockState)state.with((Property)booleanProperty, (Comparable)Boolean.valueOf(false));
        }
        if (!SculkVeinBlock.hasAnyDirection((BlockState)state)) {
            FluidState fluidState = world.getFluidState(pos);
            state = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).getDefaultState();
        }
        world.setBlockState(pos, state, 3);
        super.spreadAtSamePosition(world, state, pos, random);
    }

    public int spread(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
        if (shouldConvertToBlock && this.convertToBlock(spreadManager, world, cursor.getPos(), random)) {
            return cursor.getCharge() - 1;
        }
        return random.nextInt(spreadManager.getSpreadChance()) == 0 ? MathHelper.floor((float)((float)cursor.getCharge() * 0.5f)) : cursor.getCharge();
    }

    private boolean convertToBlock(SculkSpreadManager spreadManager, WorldAccess world, BlockPos pos, Random random) {
        BlockState blockState = world.getBlockState(pos);
        TagKey tagKey = spreadManager.getReplaceableTag();
        for (Direction direction : Direction.shuffle((Random)random)) {
            BlockPos blockPos;
            BlockState blockState2;
            if (!SculkVeinBlock.hasDirection((BlockState)blockState, (Direction)direction) || !(blockState2 = world.getBlockState(blockPos = pos.offset(direction))).isIn(tagKey)) continue;
            BlockState blockState3 = Blocks.SCULK.getDefaultState();
            world.setBlockState(blockPos, blockState3, 3);
            Block.pushEntitiesUpBeforeBlockChange((BlockState)blockState2, (BlockState)blockState3, (WorldAccess)world, (BlockPos)blockPos);
            world.playSound(null, blockPos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.allGrowTypeGrower.grow(blockState3, world, blockPos, spreadManager.isWorldGen());
            Direction direction2 = direction.getOpposite();
            for (Direction direction3 : DIRECTIONS) {
                BlockPos blockPos2;
                BlockState blockState4;
                if (direction3 == direction2 || !(blockState4 = world.getBlockState(blockPos2 = blockPos.offset(direction3))).isOf((Block)this)) continue;
                this.spreadAtSamePosition(world, blockState4, blockPos2, random);
            }
            return true;
        }
        return false;
    }

    public static boolean veinCoversSculkReplaceable(WorldAccess world, BlockState state, BlockPos pos) {
        if (!state.isOf(Blocks.SCULK_VEIN)) {
            return false;
        }
        for (Direction direction : DIRECTIONS) {
            if (!SculkVeinBlock.hasDirection((BlockState)state, (Direction)direction) || !world.getBlockState(pos.offset(direction)).isIn(BlockTags.SCULK_REPLACEABLE)) continue;
            return true;
        }
        return false;
    }
}


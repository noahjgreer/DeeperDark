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
 *  net.minecraft.block.ConcretePowderBlock
 *  net.minecraft.block.FallingBlock
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class ConcretePowderBlock
extends FallingBlock {
    public static final MapCodec<ConcretePowderBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.BLOCK.getCodec().fieldOf("concrete").forGetter(block -> block.hardenedState), (App)ConcretePowderBlock.createSettingsCodec()).apply((Applicative)instance, ConcretePowderBlock::new));
    private final Block hardenedState;

    public MapCodec<ConcretePowderBlock> getCodec() {
        return CODEC;
    }

    public ConcretePowderBlock(Block hardened, AbstractBlock.Settings settings) {
        super(settings);
        this.hardenedState = hardened;
    }

    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (ConcretePowderBlock.shouldHarden((BlockView)world, (BlockPos)pos, (BlockState)currentStateInPos)) {
            world.setBlockState(pos, this.hardenedState.getDefaultState(), 3);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState;
        BlockPos blockPos;
        World blockView = ctx.getWorld();
        if (ConcretePowderBlock.shouldHarden((BlockView)blockView, (BlockPos)(blockPos = ctx.getBlockPos()), (BlockState)(blockState = blockView.getBlockState(blockPos)))) {
            return this.hardenedState.getDefaultState();
        }
        return super.getPlacementState(ctx);
    }

    private static boolean shouldHarden(BlockView world, BlockPos pos, BlockState state) {
        return ConcretePowderBlock.hardensIn((BlockState)state) || ConcretePowderBlock.hardensOnAnySide((BlockView)world, (BlockPos)pos);
    }

    private static boolean hardensOnAnySide(BlockView world, BlockPos pos) {
        boolean bl = false;
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : Direction.values()) {
            BlockState blockState = world.getBlockState((BlockPos)mutable);
            if (direction == Direction.DOWN && !ConcretePowderBlock.hardensIn((BlockState)blockState)) continue;
            mutable.set((Vec3i)pos, direction);
            blockState = world.getBlockState((BlockPos)mutable);
            if (!ConcretePowderBlock.hardensIn((BlockState)blockState) || blockState.isSideSolidFullSquare(world, pos, direction.getOpposite())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    private static boolean hardensIn(BlockState state) {
        return state.getFluidState().isIn(FluidTags.WATER);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (ConcretePowderBlock.hardensOnAnySide((BlockView)world, (BlockPos)pos)) {
            return this.hardenedState.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return state.getMapColor((BlockView)world, (BlockPos)pos).color;
    }
}


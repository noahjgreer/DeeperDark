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
 *  net.minecraft.block.AbstractCandleBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CakeBlock
 *  net.minecraft.block.CandleBlock
 *  net.minecraft.block.CandleCakeBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class CandleCakeBlock
extends AbstractCandleBlock {
    public static final MapCodec<CandleCakeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.BLOCK.getCodec().fieldOf("candle").forGetter(block -> block.candle), (App)CandleCakeBlock.createSettingsCodec()).apply((Applicative)instance, CandleCakeBlock::new));
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    private static final VoxelShape SHAPE = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)2.0, (double)8.0, (double)14.0), (VoxelShape)Block.createColumnShape((double)14.0, (double)0.0, (double)8.0));
    private static final Map<CandleBlock, CandleCakeBlock> CANDLES_TO_CANDLE_CAKES = Maps.newHashMap();
    private static final Iterable<Vec3d> PARTICLE_OFFSETS = List.of(new Vec3d(8.0, 16.0, 8.0).multiply(0.0625));
    private final CandleBlock candle;

    public MapCodec<CandleCakeBlock> getCodec() {
        return CODEC;
    }

    public CandleCakeBlock(Block candle, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)LIT, (Comparable)Boolean.valueOf(false)));
        if (!(candle instanceof CandleBlock)) {
            throw new IllegalArgumentException("Expected block to be of " + String.valueOf(CandleBlock.class) + " was " + String.valueOf(candle.getClass()));
        }
        CandleBlock candleBlock = (CandleBlock)candle;
        CANDLES_TO_CANDLE_CAKES.put(candleBlock, this);
        this.candle = candleBlock;
    }

    protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
        return PARTICLE_OFFSETS;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE)) {
            return ActionResult.PASS;
        }
        if (CandleCakeBlock.isHittingCandle((BlockHitResult)hit) && stack.isEmpty() && ((Boolean)state.get((Property)LIT)).booleanValue()) {
            CandleCakeBlock.extinguish((PlayerEntity)player, (BlockState)state, (WorldAccess)world, (BlockPos)pos);
            return ActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ActionResult actionResult = CakeBlock.tryEat((WorldAccess)world, (BlockPos)pos, (BlockState)Blocks.CAKE.getDefaultState(), (PlayerEntity)player);
        if (actionResult.isAccepted()) {
            CandleCakeBlock.dropStacks((BlockState)state, (World)world, (BlockPos)pos);
        }
        return actionResult;
    }

    private static boolean isHittingCandle(BlockHitResult hitResult) {
        return hitResult.getPos().y - (double)hitResult.getBlockPos().getY() > 0.5;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT});
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)Blocks.CAKE);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolid();
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return CakeBlock.DEFAULT_COMPARATOR_OUTPUT;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public static BlockState getCandleCakeFromCandle(CandleBlock candle) {
        return ((CandleCakeBlock)CANDLES_TO_CANDLE_CAKES.get(candle)).getDefaultState();
    }

    public static boolean canBeLit(BlockState state) {
        return state.isIn(BlockTags.CANDLE_CAKES, statex -> statex.contains((Property)LIT) && (Boolean)state.get((Property)LIT) == false);
    }
}


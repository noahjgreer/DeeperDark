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
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.PitcherCropBlock
 *  net.minecraft.block.PitcherCropBlock$1
 *  net.minecraft.block.PitcherCropBlock$LowerHalfContext
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.RavagerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.rule.GameRules
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class PitcherCropBlock
extends TallPlantBlock
implements Fertilizable {
    public static final MapCodec<PitcherCropBlock> CODEC = PitcherCropBlock.createCodec(PitcherCropBlock::new);
    public static final int field_43240 = 4;
    public static final IntProperty AGE = Properties.AGE_4;
    public static final EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;
    private static final int field_43241 = 3;
    private static final int field_43391 = 1;
    private static final VoxelShape AGE_0_SHAPE = Block.createColumnShape((double)6.0, (double)-1.0, (double)3.0);
    private static final VoxelShape LOWER_COLLISION_SHAPE = Block.createColumnShape((double)10.0, (double)-1.0, (double)5.0);
    private final Function<BlockState, VoxelShape> shapeFunction = this.createShapeFunction();

    public MapCodec<PitcherCropBlock> getCodec() {
        return CODEC;
    }

    public PitcherCropBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        int[] is = new int[]{0, 9, 11, 22, 26};
        return this.createShapeFunction(state -> {
            int i = ((Integer)state.get((Property)AGE) == 0 ? 4 : 6) + is[(Integer)state.get((Property)AGE)];
            int j = (Integer)state.get((Property)AGE) == 0 ? 6 : 10;
            return switch (1.field_55785[((DoubleBlockHalf)state.get((Property)HALF)).ordinal()]) {
                default -> throw new MatchException(null, null);
                case 1 -> Block.createColumnShape((double)j, (double)-1.0, (double)Math.min(16, -1 + i));
                case 2 -> Block.createColumnShape((double)j, (double)0.0, (double)Math.max(0, -1 + i - 16));
            };
        });
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get((Property)HALF) == DoubleBlockHalf.LOWER) {
            return (Integer)state.get((Property)AGE) == 0 ? AGE_0_SHAPE : LOWER_COLLISION_SHAPE;
        }
        return VoxelShapes.empty();
    }

    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (PitcherCropBlock.isDoubleTallAtAge((int)((Integer)state.get((Property)AGE)))) {
            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        }
        return state.canPlaceAt(world, pos) ? state : Blocks.AIR.getDefaultState();
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (PitcherCropBlock.isLowerHalf((BlockState)state) && !PitcherCropBlock.canPlaceAt((WorldView)world, (BlockPos)pos)) {
            return false;
        }
        return super.canPlaceAt(state, world, pos);
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
        super.appendProperties(builder);
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (entity instanceof RavagerEntity && ((Boolean)serverWorld.getGameRules().getValue(GameRules.DO_MOB_GRIEFING)).booleanValue()) {
                serverWorld.breakBlock(pos, true, entity);
            }
        }
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return false;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    }

    public boolean hasRandomTicks(BlockState state) {
        return state.get((Property)HALF) == DoubleBlockHalf.LOWER && !this.isFullyGrown(state);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl;
        float f = CropBlock.getAvailableMoisture((Block)this, (BlockView)world, (BlockPos)pos);
        boolean bl2 = bl = random.nextInt((int)(25.0f / f) + 1) == 0;
        if (bl) {
            this.tryGrow(world, state, pos, 1);
        }
    }

    private void tryGrow(ServerWorld world, BlockState state, BlockPos pos, int amount) {
        int i = Math.min((Integer)state.get((Property)AGE) + amount, 4);
        if (!this.canGrow((WorldView)world, pos, state, i)) {
            return;
        }
        BlockState blockState = (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i));
        world.setBlockState(pos, blockState, 2);
        if (PitcherCropBlock.isDoubleTallAtAge((int)i)) {
            world.setBlockState(pos.up(), (BlockState)blockState.with((Property)HALF, (Comparable)DoubleBlockHalf.UPPER), 3);
        }
    }

    private static boolean canGrowAt(WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || blockState.isOf(Blocks.PITCHER_CROP);
    }

    private static boolean canPlaceAt(WorldView world, BlockPos pos) {
        return CropBlock.hasEnoughLightAt((WorldView)world, (BlockPos)pos);
    }

    private static boolean isLowerHalf(BlockState state) {
        return state.isOf(Blocks.PITCHER_CROP) && state.get((Property)HALF) == DoubleBlockHalf.LOWER;
    }

    private static boolean isDoubleTallAtAge(int age) {
        return age >= 3;
    }

    private boolean canGrow(WorldView world, BlockPos pos, BlockState state, int age) {
        return !this.isFullyGrown(state) && PitcherCropBlock.canPlaceAt((WorldView)world, (BlockPos)pos) && (!PitcherCropBlock.isDoubleTallAtAge((int)age) || PitcherCropBlock.canGrowAt((WorldView)world, (BlockPos)pos.up()));
    }

    private boolean isFullyGrown(BlockState state) {
        return (Integer)state.get((Property)AGE) >= 4;
    }

    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable PitcherCropBlock.LowerHalfContext getLowerHalfContext(WorldView world, BlockPos pos, BlockState state) {
        if (PitcherCropBlock.isLowerHalf((BlockState)state)) {
            return new LowerHalfContext(pos, state);
        }
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (PitcherCropBlock.isLowerHalf((BlockState)blockState)) {
            return new LowerHalfContext(blockPos, blockState);
        }
        return null;
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        LowerHalfContext lowerHalfContext = this.getLowerHalfContext(world, pos, state);
        if (lowerHalfContext == null) {
            return false;
        }
        return this.canGrow(world, lowerHalfContext.pos, lowerHalfContext.state, (Integer)lowerHalfContext.state.get((Property)AGE) + 1);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        LowerHalfContext lowerHalfContext = this.getLowerHalfContext((WorldView)world, pos, state);
        if (lowerHalfContext == null) {
            return;
        }
        this.tryGrow(world, lowerHalfContext.state, lowerHalfContext.pos, 1);
    }
}


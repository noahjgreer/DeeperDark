/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.LeverBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WallMountedBlock
 *  net.minecraft.block.enums.BlockFace
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.explosion.Explosion
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class LeverBlock
extends WallMountedBlock {
    public static final MapCodec<LeverBlock> CODEC = LeverBlock.createCodec(LeverBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final Function<BlockState, VoxelShape> shapeFunction;

    public MapCodec<LeverBlock> getCodec() {
        return CODEC;
    }

    public LeverBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)FACE, (Comparable)BlockFace.WALL));
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        Map map = VoxelShapes.createBlockFaceHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)6.0, (double)8.0, (double)10.0, (double)16.0));
        return this.createShapeFunction(state -> (VoxelShape)((Map)map.get(state.get((Property)FACE))).get(state.get((Property)FACING)), new Property[]{POWERED});
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            BlockState blockState = (BlockState)state.cycle((Property)POWERED);
            if (((Boolean)blockState.get((Property)POWERED)).booleanValue()) {
                LeverBlock.spawnParticles((BlockState)blockState, (WorldAccess)world, (BlockPos)pos, (float)1.0f);
            }
        } else {
            this.togglePower(state, world, pos, null);
        }
        return ActionResult.SUCCESS;
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks()) {
            this.togglePower(state, (World)world, pos, null);
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    public void togglePower(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        state = (BlockState)state.cycle((Property)POWERED);
        world.setBlockState(pos, state, 3);
        this.updateNeighbors(state, world, pos);
        LeverBlock.playClickSound((PlayerEntity)player, (WorldAccess)world, (BlockPos)pos, (BlockState)state);
        world.emitGameEvent((Entity)player, (RegistryEntry)((Boolean)state.get((Property)POWERED) != false ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE), pos);
    }

    protected static void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state) {
        float f = (Boolean)state.get((Property)POWERED) != false ? 0.6f : 0.5f;
        world.playSound((Entity)player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, f);
    }

    private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
        Direction direction = ((Direction)state.get((Property)FACING)).getOpposite();
        Direction direction2 = LeverBlock.getDirection((BlockState)state).getOpposite();
        double d = (double)pos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
        double e = (double)pos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
        double f = (double)pos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
        world.addParticleClient((ParticleEffect)new DustParticleEffect(0xFF0000, alpha), d, e, f, 0.0, 0.0, 0.0);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue() && random.nextFloat() < 0.25f) {
            LeverBlock.spawnParticles((BlockState)state, (WorldAccess)world, (BlockPos)pos, (float)0.5f);
        }
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved && ((Boolean)state.get((Property)POWERED)).booleanValue()) {
            this.updateNeighbors(state, (World)world, pos);
        }
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Boolean)state.get((Property)POWERED) != false ? 15 : 0;
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue() && LeverBlock.getDirection((BlockState)state) == direction) {
            return 15;
        }
        return 0;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        Direction direction;
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)direction, (Direction)((direction = LeverBlock.getDirection((BlockState)state).getOpposite()).getAxis().isHorizontal() ? Direction.UP : (Direction)state.get((Property)FACING)));
        world.updateNeighborsAlways(pos, (Block)this, wireOrientation);
        world.updateNeighborsAlways(pos.offset(direction), (Block)this, wireOrientation);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACE, FACING, POWERED});
    }
}


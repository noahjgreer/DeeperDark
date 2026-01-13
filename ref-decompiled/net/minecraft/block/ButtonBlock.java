/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ButtonBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WallMountedBlock
 *  net.minecraft.block.enums.BlockFace
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3i
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

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
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
public class ButtonBlock
extends WallMountedBlock {
    public static final MapCodec<ButtonBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType), (App)Codec.intRange((int)1, (int)1024).fieldOf("ticks_to_stay_pressed").forGetter(block -> block.pressTicks), (App)ButtonBlock.createSettingsCodec()).apply((Applicative)instance, ButtonBlock::new));
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final BlockSetType blockSetType;
    private final int pressTicks;
    private final Function<BlockState, VoxelShape> shapeFunction;

    public MapCodec<ButtonBlock> getCodec() {
        return CODEC;
    }

    public ButtonBlock(BlockSetType blockSetType, int pressTicks, AbstractBlock.Settings settings) {
        super(settings.sounds(blockSetType.soundType()));
        this.blockSetType = blockSetType;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)FACE, (Comparable)BlockFace.WALL));
        this.pressTicks = pressTicks;
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        VoxelShape voxelShape = Block.createCubeShape((double)14.0);
        VoxelShape voxelShape2 = Block.createCubeShape((double)12.0);
        Map map = VoxelShapes.createBlockFaceHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)6.0, (double)4.0, (double)8.0, (double)16.0));
        return this.createShapeFunction(state -> VoxelShapes.combineAndSimplify((VoxelShape)((VoxelShape)((Map)map.get(state.get((Property)FACE))).get(state.get((Property)FACING))), (VoxelShape)((Boolean)state.get((Property)POWERED) != false ? voxelShape : voxelShape2), (BooleanBiFunction)BooleanBiFunction.ONLY_FIRST));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return ActionResult.CONSUME;
        }
        this.powerOn(state, world, pos, player);
        return ActionResult.SUCCESS;
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks() && !((Boolean)state.get((Property)POWERED)).booleanValue()) {
            this.powerOn(state, (World)world, pos, null);
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    public void powerOn(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(true)), 3);
        this.updateNeighbors(state, world, pos);
        world.scheduleBlockTick(pos, (Block)this, this.pressTicks);
        this.playClickSound(player, (WorldAccess)world, pos, true);
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_ACTIVATE, pos);
    }

    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        world.playSound((Entity)(powered ? player : null), pos, this.getClickSound(powered), SoundCategory.BLOCKS);
    }

    protected SoundEvent getClickSound(boolean powered) {
        return powered ? this.blockSetType.buttonClickOn() : this.blockSetType.buttonClickOff();
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
        if (((Boolean)state.get((Property)POWERED)).booleanValue() && ButtonBlock.getDirection((BlockState)state) == direction) {
            return 15;
        }
        return 0;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return;
        }
        this.tryPowerWithProjectiles(state, (World)world, pos);
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world.isClient() || !this.blockSetType.canButtonBeActivatedByArrows() || ((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return;
        }
        this.tryPowerWithProjectiles(state, world, pos);
    }

    protected void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
        boolean bl2;
        PersistentProjectileEntity persistentProjectileEntity = this.blockSetType.canButtonBeActivatedByArrows() ? (PersistentProjectileEntity)world.getNonSpectatingEntities(PersistentProjectileEntity.class, state.getOutlineShape((BlockView)world, pos).getBoundingBox().offset(pos)).stream().findFirst().orElse(null) : null;
        boolean bl = persistentProjectileEntity != null;
        if (bl != (bl2 = ((Boolean)state.get((Property)POWERED)).booleanValue())) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl)), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, (WorldAccess)world, pos, bl);
            world.emitGameEvent((Entity)persistentProjectileEntity, (RegistryEntry)(bl ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE), pos);
        }
        if (bl) {
            world.scheduleBlockTick(new BlockPos((Vec3i)pos), (Block)this, this.pressTicks);
        }
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        Direction direction;
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)direction, (Direction)((direction = ButtonBlock.getDirection((BlockState)state).getOpposite()).getAxis().isHorizontal() ? Direction.UP : (Direction)state.get((Property)FACING)));
        world.updateNeighborsAlways(pos, (Block)this, wireOrientation);
        world.updateNeighborsAlways(pos.offset(direction), (Block)this, wireOrientation);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, POWERED, FACE});
    }
}


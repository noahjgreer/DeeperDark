/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.dispenser.DispenserBehavior
 *  net.minecraft.block.dispenser.EquippableDispenserBehavior
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.block.dispenser.ProjectileDispenserBehavior
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.DispenserBlockEntity
 *  net.minecraft.block.entity.DropperBlockEntity
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public class DispenserBlock
extends BlockWithEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<DispenserBlock> CODEC = DispenserBlock.createCodec(DispenserBlock::new);
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    private static final ItemDispenserBehavior DEFAULT_BEHAVIOR = new ItemDispenserBehavior();
    public static final Map<Item, DispenserBehavior> BEHAVIORS = new IdentityHashMap();
    private static final int SCHEDULED_TICK_DELAY = 4;

    public MapCodec<? extends DispenserBlock> getCodec() {
        return CODEC;
    }

    public static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior) {
        BEHAVIORS.put(provider.asItem(), behavior);
    }

    public static void registerProjectileBehavior(ItemConvertible projectile) {
        BEHAVIORS.put(projectile.asItem(), new ProjectileDispenserBehavior(projectile.asItem()));
    }

    public DispenserBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)TRIGGERED, (Comparable)Boolean.valueOf(false)));
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (!world.isClient() && (blockEntity = world.getBlockEntity(pos)) instanceof DispenserBlockEntity) {
            DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity)blockEntity;
            player.openHandledScreen((NamedScreenHandlerFactory)dispenserBlockEntity);
            player.incrementStat(dispenserBlockEntity instanceof DropperBlockEntity ? Stats.INSPECT_DROPPER : Stats.INSPECT_DISPENSER);
        }
        return ActionResult.SUCCESS;
    }

    protected void dispense(ServerWorld world, BlockState state, BlockPos pos) {
        DispenserBlockEntity dispenserBlockEntity = world.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse(null);
        if (dispenserBlockEntity == null) {
            LOGGER.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", (Object)pos);
            return;
        }
        BlockPointer blockPointer = new BlockPointer(world, pos, state, dispenserBlockEntity);
        int i = dispenserBlockEntity.chooseNonEmptySlot(world.random);
        if (i < 0) {
            world.syncWorldEvent(1001, pos, 0);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of((BlockState)dispenserBlockEntity.getCachedState()));
            return;
        }
        ItemStack itemStack = dispenserBlockEntity.getStack(i);
        DispenserBehavior dispenserBehavior = this.getBehaviorForItem((World)world, itemStack);
        if (dispenserBehavior != DispenserBehavior.NOOP) {
            dispenserBlockEntity.setStack(i, dispenserBehavior.dispense(blockPointer, itemStack));
        }
    }

    protected DispenserBehavior getBehaviorForItem(World world, ItemStack stack) {
        if (!stack.isItemEnabled(world.getEnabledFeatures())) {
            return DEFAULT_BEHAVIOR;
        }
        DispenserBehavior dispenserBehavior = (DispenserBehavior)BEHAVIORS.get(stack.getItem());
        if (dispenserBehavior != null) {
            return dispenserBehavior;
        }
        return DispenserBlock.getBehaviorForItem((ItemStack)stack);
    }

    private static DispenserBehavior getBehaviorForItem(ItemStack stack) {
        if (stack.contains(DataComponentTypes.EQUIPPABLE)) {
            return EquippableDispenserBehavior.INSTANCE;
        }
        return DEFAULT_BEHAVIOR;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean bl2 = (Boolean)state.get((Property)TRIGGERED);
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, (Block)this, 4);
            world.setBlockState(pos, (BlockState)state.with((Property)TRIGGERED, (Comparable)Boolean.valueOf(true)), 2);
        } else if (!bl && bl2) {
            world.setBlockState(pos, (BlockState)state.with((Property)TRIGGERED, (Comparable)Boolean.valueOf(false)), 2);
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.dispense(world, state, pos);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DispenserBlockEntity(pos, state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getPlayerLookDirection().getOpposite());
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    public static Position getOutputLocation(BlockPointer pointer) {
        return DispenserBlock.getOutputLocation((BlockPointer)pointer, (double)0.7, (Vec3d)Vec3d.ZERO);
    }

    public static Position getOutputLocation(BlockPointer pointer, double facingOffset, Vec3d constantOffset) {
        Direction direction = (Direction)pointer.state().get((Property)FACING);
        return pointer.centerPos().add(facingOffset * (double)direction.getOffsetX() + constantOffset.getX(), facingOffset * (double)direction.getOffsetY() + constantOffset.getY(), facingOffset * (double)direction.getOffsetZ() + constantOffset.getZ());
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return ScreenHandler.calculateComparatorOutput((BlockEntity)world.getBlockEntity(pos));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, TRIGGERED});
    }
}


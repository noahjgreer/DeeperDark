/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.CreakingHeartBlock
 *  net.minecraft.block.PillarBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CreakingHeartBlockEntity
 *  net.minecraft.block.enums.CreakingHeartState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.explosion.ExplosionImpl
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.block.enums.CreakingHeartState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class CreakingHeartBlock
extends BlockWithEntity {
    public static final MapCodec<CreakingHeartBlock> CODEC = CreakingHeartBlock.createCodec(CreakingHeartBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    public static final EnumProperty<CreakingHeartState> ACTIVE = Properties.CREAKING_HEART_STATE;
    public static final BooleanProperty NATURAL = Properties.NATURAL;

    public MapCodec<CreakingHeartBlock> getCodec() {
        return CODEC;
    }

    public CreakingHeartBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)AXIS, (Comparable)Direction.Axis.Y)).with((Property)ACTIVE, (Comparable)CreakingHeartState.UPROOTED)).with((Property)NATURAL, (Comparable)Boolean.valueOf(false)));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CreakingHeartBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        }
        if (state.get((Property)ACTIVE) != CreakingHeartState.UPROOTED) {
            return CreakingHeartBlock.validateTicker(type, (BlockEntityType)BlockEntityType.CREAKING_HEART, CreakingHeartBlockEntity::tick);
        }
        return null;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.CREAKING_ACTIVE_GAMEPLAY, pos)).booleanValue()) {
            return;
        }
        if (state.get((Property)ACTIVE) == CreakingHeartState.UPROOTED) {
            return;
        }
        if (random.nextInt(16) == 0 && CreakingHeartBlock.isSurroundedByPaleOakLogs((WorldAccess)world, (BlockPos)pos)) {
            world.playSoundClient((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_CREAKING_HEART_IDLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        tickView.scheduleBlockTick(pos, (Block)this, 1);
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState blockState = CreakingHeartBlock.enableIfValid((BlockState)state, (World)world, (BlockPos)pos);
        if (blockState != state) {
            world.setBlockState(pos, blockState, 3);
        }
    }

    private static BlockState enableIfValid(BlockState state, World world, BlockPos pos) {
        boolean bl2;
        boolean bl = CreakingHeartBlock.shouldBeEnabled((BlockState)state, (WorldView)world, (BlockPos)pos);
        boolean bl3 = bl2 = state.get((Property)ACTIVE) == CreakingHeartState.UPROOTED;
        if (bl && bl2) {
            return (BlockState)state.with((Property)ACTIVE, (Comparable)((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.CREAKING_ACTIVE_GAMEPLAY, pos) != false ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT));
        }
        return state;
    }

    public static boolean shouldBeEnabled(BlockState state, WorldView world, BlockPos pos) {
        Direction.Axis axis = (Direction.Axis)state.get((Property)AXIS);
        for (Direction direction : axis.getDirections()) {
            BlockState blockState = world.getBlockState(pos.offset(direction));
            if (blockState.isIn(BlockTags.PALE_OAK_LOGS) && blockState.get((Property)AXIS) == axis) continue;
            return false;
        }
        return true;
    }

    private static boolean isSurroundedByPaleOakLogs(WorldAccess world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isIn(BlockTags.PALE_OAK_LOGS)) continue;
            return false;
        }
        return true;
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return CreakingHeartBlock.enableIfValid((BlockState)((BlockState)this.getDefaultState().with((Property)AXIS, (Comparable)ctx.getSide().getAxis())), (World)ctx.getWorld(), (BlockPos)ctx.getBlockPos());
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return PillarBlock.changeRotation((BlockState)state, (BlockRotation)rotation);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS, ACTIVE, NATURAL});
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity creakingHeartBlockEntity = (CreakingHeartBlockEntity)blockEntity;
            if (explosion instanceof ExplosionImpl) {
                ExplosionImpl explosionImpl = (ExplosionImpl)explosion;
                if (explosion.getDestructionType().destroysBlocks()) {
                    creakingHeartBlockEntity.killPuppet(explosionImpl.getDamageSource());
                    LivingEntity livingEntity = explosion.getCausingEntity();
                    if (livingEntity instanceof PlayerEntity) {
                        PlayerEntity playerEntity = (PlayerEntity)livingEntity;
                        if (explosion.getDestructionType().destroysBlocks()) {
                            this.dropExperienceOnBreak(playerEntity, state, (World)world, pos);
                        }
                    }
                }
            }
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity creakingHeartBlockEntity = (CreakingHeartBlockEntity)blockEntity;
            creakingHeartBlockEntity.killPuppet(player.getDamageSources().playerAttack(player));
            this.dropExperienceOnBreak(player, state, world, pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    private void dropExperienceOnBreak(PlayerEntity player, BlockState state, World world, BlockPos pos) {
        if (!player.shouldSkipBlockDrops() && !player.isSpectator() && ((Boolean)state.get((Property)NATURAL)).booleanValue() && world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.dropExperience(serverWorld, pos, world.random.nextBetween(20, 24));
        }
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        if (state.get((Property)ACTIVE) == CreakingHeartState.UPROOTED) {
            return 0;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CreakingHeartBlockEntity)) {
            return 0;
        }
        CreakingHeartBlockEntity creakingHeartBlockEntity = (CreakingHeartBlockEntity)blockEntity;
        return creakingHeartBlockEntity.getComparatorOutput();
    }
}


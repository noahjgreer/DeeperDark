/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.NetherPortalBlock
 *  net.minecraft.block.NetherPortalBlock$1
 *  net.minecraft.block.Portal
 *  net.minecraft.block.Portal$Effect
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.EntityDimensions
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.s2c.play.PositionFlag
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockLocating
 *  net.minecraft.world.BlockLocating$Rectangle
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.TeleportTarget
 *  net.minecraft.world.TeleportTarget$PostDimensionTransition
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.border.WorldBorder
 *  net.minecraft.world.dimension.DimensionType
 *  net.minecraft.world.dimension.NetherPortal
 *  net.minecraft.world.rule.GameRules
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public class NetherPortalBlock
extends Block
implements Portal {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<NetherPortalBlock> CODEC = NetherPortalBlock.createCodec(NetherPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    private static final Map<Direction.Axis, VoxelShape> SHAPES_BY_AXIS = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)Block.createColumnShape((double)4.0, (double)16.0, (double)0.0, (double)16.0));

    public MapCodec<NetherPortalBlock> getCodec() {
        return CODEC;
    }

    public NetherPortalBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AXIS, (Comparable)Direction.Axis.X));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES_BY_AXIS.get(state.get((Property)AXIS));
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.shouldSpawnMonsters() && ((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLIN_GAMEPLAY, pos)).booleanValue() && random.nextInt(2000) < world.getDifficulty().getId() && world.shouldTickBlockAt(pos)) {
            Entity entity;
            while (world.getBlockState(pos).isOf((Block)this)) {
                pos = pos.down();
            }
            if (world.getBlockState(pos).allowsSpawning((BlockView)world, pos, EntityType.ZOMBIFIED_PIGLIN) && (entity = EntityType.ZOMBIFIED_PIGLIN.spawn(world, pos.up(), SpawnReason.STRUCTURE)) != null) {
                entity.resetPortalCooldown();
                Entity entity2 = entity.getVehicle();
                if (entity2 != null) {
                    entity2.resetPortalCooldown();
                }
            }
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        boolean bl;
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = (Direction.Axis)state.get((Property)AXIS);
        boolean bl2 = bl = axis2 != axis && axis.isHorizontal();
        if (bl || neighborState.isOf((Block)this) || NetherPortal.getOnAxis((BlockView)world, (BlockPos)pos, (Direction.Axis)axis2).wasAlreadyValid()) {
            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        }
        return Blocks.AIR.getDefaultState();
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (entity.canUsePortals(false)) {
            entity.tryUsePortal((Portal)this, pos);
        }
    }

    public int getPortalDelay(ServerWorld world, Entity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            return Math.max(0, (Integer)world.getGameRules().getValue(playerEntity.getAbilities().invulnerable ? GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY));
        }
        return 0;
    }

    public @Nullable TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        RegistryKey registryKey = world.getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER;
        ServerWorld serverWorld = world.getServer().getWorld(registryKey);
        if (serverWorld == null) {
            return null;
        }
        boolean bl = serverWorld.getRegistryKey() == World.NETHER;
        WorldBorder worldBorder = serverWorld.getWorldBorder();
        double d = DimensionType.getCoordinateScaleFactor((DimensionType)world.getDimension(), (DimensionType)serverWorld.getDimension());
        BlockPos blockPos = worldBorder.clampFloored(entity.getX() * d, entity.getY(), entity.getZ() * d);
        return this.getOrCreateExitPortalTarget(serverWorld, entity, pos, blockPos, bl, worldBorder);
    }

    private @Nullable TeleportTarget getOrCreateExitPortalTarget(ServerWorld world, Entity entity2, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder) {
        TeleportTarget.PostDimensionTransition postDimensionTransition;
        BlockLocating.Rectangle rectangle;
        Optional optional = world.getPortalForcer().getPortalPos(scaledPos, inNether, worldBorder);
        if (optional.isPresent()) {
            BlockPos blockPos = (BlockPos)optional.get();
            BlockState blockState = world.getBlockState(blockPos);
            rectangle = BlockLocating.getLargestRectangle((BlockPos)blockPos, (Direction.Axis)((Direction.Axis)blockState.get((Property)Properties.HORIZONTAL_AXIS)), (int)21, (Direction.Axis)Direction.Axis.Y, (int)21, posx -> world.getBlockState(posx) == blockState);
            postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(entity -> entity.addPortalChunkTicketAt(blockPos));
        } else {
            Direction.Axis axis = entity2.getEntityWorld().getBlockState(pos).getOrEmpty((Property)AXIS).orElse(Direction.Axis.X);
            Optional optional2 = world.getPortalForcer().createPortal(scaledPos, axis);
            if (optional2.isEmpty()) {
                LOGGER.error("Unable to create a portal, likely target out of worldborder");
                return null;
            }
            rectangle = (BlockLocating.Rectangle)optional2.get();
            postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
        }
        return NetherPortalBlock.getExitPortalTarget((Entity)entity2, (BlockPos)pos, (BlockLocating.Rectangle)rectangle, (ServerWorld)world, (TeleportTarget.PostDimensionTransition)postDimensionTransition);
    }

    private static TeleportTarget getExitPortalTarget(Entity entity, BlockPos pos, BlockLocating.Rectangle exitPortalRectangle, ServerWorld world, TeleportTarget.PostDimensionTransition postDimensionTransition) {
        Vec3d vec3d;
        Direction.Axis axis;
        BlockState blockState = entity.getEntityWorld().getBlockState(pos);
        if (blockState.contains((Property)Properties.HORIZONTAL_AXIS)) {
            axis = (Direction.Axis)blockState.get((Property)Properties.HORIZONTAL_AXIS);
            BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle((BlockPos)pos, (Direction.Axis)axis, (int)21, (Direction.Axis)Direction.Axis.Y, (int)21, posx -> entity.getEntityWorld().getBlockState(posx) == blockState);
            vec3d = entity.positionInPortal(axis, rectangle);
        } else {
            axis = Direction.Axis.X;
            vec3d = new Vec3d(0.5, 0.0, 0.0);
        }
        return NetherPortalBlock.getExitPortalTarget((ServerWorld)world, (BlockLocating.Rectangle)exitPortalRectangle, (Direction.Axis)axis, (Vec3d)vec3d, (Entity)entity, (TeleportTarget.PostDimensionTransition)postDimensionTransition);
    }

    private static TeleportTarget getExitPortalTarget(ServerWorld world, BlockLocating.Rectangle exitPortalRectangle, Direction.Axis axis, Vec3d positionInPortal, Entity entity, TeleportTarget.PostDimensionTransition postDimensionTransition) {
        BlockPos blockPos = exitPortalRectangle.lowerLeft;
        BlockState blockState = world.getBlockState(blockPos);
        Direction.Axis axis2 = blockState.getOrEmpty((Property)Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
        double d = exitPortalRectangle.width;
        double e = exitPortalRectangle.height;
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        int i = axis == axis2 ? 0 : 90;
        double f = (double)entityDimensions.width() / 2.0 + (d - (double)entityDimensions.width()) * positionInPortal.getX();
        double g = (e - (double)entityDimensions.height()) * positionInPortal.getY();
        double h = 0.5 + positionInPortal.getZ();
        boolean bl = axis2 == Direction.Axis.X;
        Vec3d vec3d = new Vec3d((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
        Vec3d vec3d2 = NetherPortal.findOpenPosition((Vec3d)vec3d, (ServerWorld)world, (Entity)entity, (EntityDimensions)entityDimensions);
        return new TeleportTarget(world, vec3d2, Vec3d.ZERO, (float)i, 0.0f, PositionFlag.combine((Set[])new Set[]{PositionFlag.DELTA, PositionFlag.ROT}), postDimensionTransition);
    }

    public Portal.Effect getPortalEffect() {
        return Portal.Effect.CONFUSION;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(100) == 0) {
            world.playSoundClient((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int i = 0; i < 4; ++i) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            double g = ((double)random.nextFloat() - 0.5) * 0.5;
            double h = ((double)random.nextFloat() - 0.5) * 0.5;
            double j = ((double)random.nextFloat() - 0.5) * 0.5;
            int k = random.nextInt(2) * 2 - 1;
            if (world.getBlockState(pos.west()).isOf((Block)this) || world.getBlockState(pos.east()).isOf((Block)this)) {
                f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
                j = random.nextFloat() * 2.0f * (float)k;
            } else {
                d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
                g = random.nextFloat() * 2.0f * (float)k;
            }
            world.addParticleClient((ParticleEffect)ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return ItemStack.EMPTY;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (1.field_11319[rotation.ordinal()]) {
            case 1: 
            case 2: {
                switch (1.field_11320[((Direction.Axis)state.get((Property)AXIS)).ordinal()]) {
                    case 1: {
                        return (BlockState)state.with((Property)AXIS, (Comparable)Direction.Axis.Z);
                    }
                    case 2: {
                        return (BlockState)state.with((Property)AXIS, (Comparable)Direction.Axis.X);
                    }
                }
                return state;
            }
        }
        return state;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS});
    }
}


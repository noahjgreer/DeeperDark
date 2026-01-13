/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BedBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.DoubleBlockProperties$Type
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BedBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.enums.BedPart
 *  net.minecraft.entity.Dismounting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.DirectionTransformation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.CollisionView
 *  net.minecraft.world.World
 *  net.minecraft.world.World$ExplosionSourceType
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.attribute.BedRule
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.apache.commons.lang3.ArrayUtils
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.tick.ScheduledTickView;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BedBlock
extends HorizontalFacingBlock
implements BlockEntityProvider {
    public static final MapCodec<BedBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DyeColor.CODEC.fieldOf("color").forGetter(BedBlock::getColor), (App)BedBlock.createSettingsCodec()).apply((Applicative)instance, BedBlock::new));
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;
    public static final BooleanProperty OCCUPIED = Properties.OCCUPIED;
    private static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION = (Map)Util.make(() -> {
        VoxelShape voxelShape = Block.createCuboidShape((double)0.0, (double)0.0, (double)0.0, (double)3.0, (double)3.0, (double)3.0);
        VoxelShape voxelShape2 = VoxelShapes.transform((VoxelShape)voxelShape, (DirectionTransformation)DirectionTransformation.field_64511);
        return VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)Block.createColumnShape((double)16.0, (double)3.0, (double)9.0), (VoxelShape[])new VoxelShape[]{voxelShape, voxelShape2}));
    });
    private final DyeColor color;

    public MapCodec<BedBlock> getCodec() {
        return CODEC;
    }

    public BedBlock(DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)PART, (Comparable)BedPart.FOOT)).with((Property)OCCUPIED, (Comparable)Boolean.valueOf(false)));
    }

    public static @Nullable Direction getDirection(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock() instanceof BedBlock ? (Direction)blockState.get((Property)FACING) : null;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS_SERVER;
        }
        if (state.get((Property)PART) != BedPart.HEAD && !(state = world.getBlockState(pos = pos.offset((Direction)state.get((Property)FACING)))).isOf((Block)this)) {
            return ActionResult.CONSUME;
        }
        BedRule bedRule = (BedRule)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.BED_RULE_GAMEPLAY, pos);
        if (bedRule.explodes()) {
            bedRule.errorMessage().ifPresent(message -> player.sendMessage(message, true));
            world.removeBlock(pos, false);
            BlockPos blockPos = pos.offset(((Direction)state.get((Property)FACING)).getOpposite());
            if (world.getBlockState(blockPos).isOf((Block)this)) {
                world.removeBlock(blockPos, false);
            }
            Vec3d vec3d = pos.toCenterPos();
            world.createExplosion(null, world.getDamageSources().badRespawnPoint(vec3d), null, vec3d, 5.0f, true, World.ExplosionSourceType.BLOCK);
            return ActionResult.SUCCESS_SERVER;
        }
        if (((Boolean)state.get((Property)OCCUPIED)).booleanValue()) {
            if (!this.wakeVillager(world, pos)) {
                player.sendMessage((Text)Text.translatable((String)"block.minecraft.bed.occupied"), true);
            }
            return ActionResult.SUCCESS_SERVER;
        }
        player.trySleep(pos).ifLeft(reason -> {
            if (reason.message() != null) {
                player.sendMessage(reason.message(), true);
            }
        });
        return ActionResult.SUCCESS_SERVER;
    }

    private boolean wakeVillager(World world, BlockPos pos) {
        List list = world.getEntitiesByClass(VillagerEntity.class, new Box(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        }
        ((VillagerEntity)list.get(0)).wakeUp();
        return true;
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        super.onLandedUpon(world, state, pos, entity, fallDistance * 0.5);
    }

    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            this.bounceEntity(entity);
        }
    }

    private void bounceEntity(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(vec3d.x, -vec3d.y * (double)0.66f * d, vec3d.z);
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == BedBlock.getDirectionTowardsOtherPart((BedPart)((BedPart)state.get((Property)PART)), (Direction)((Direction)state.get((Property)FACING)))) {
            if (neighborState.isOf((Block)this) && neighborState.get((Property)PART) != state.get((Property)PART)) {
                return (BlockState)state.with((Property)OCCUPIED, (Comparable)((Boolean)neighborState.get((Property)OCCUPIED)));
            }
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos blockPos;
        BlockState blockState;
        BedPart bedPart;
        if (!world.isClient() && player.shouldSkipBlockDrops() && (bedPart = (BedPart)state.get((Property)PART)) == BedPart.FOOT && (blockState = world.getBlockState(blockPos = pos.offset(BedBlock.getDirectionTowardsOtherPart((BedPart)bedPart, (Direction)((Direction)state.get((Property)FACING)))))).isOf((Block)this) && blockState.get((Property)PART) == BedPart.HEAD) {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
            world.syncWorldEvent((Entity)player, 2001, blockPos, Block.getRawIdFromState((BlockState)blockState));
        }
        return super.onBreak(world, pos, state, player);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        World world = ctx.getWorld();
        if (world.getBlockState(blockPos2).canReplace(ctx) && world.getWorldBorder().contains(blockPos2)) {
            return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)direction);
        }
        return null;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES_BY_DIRECTION.get(BedBlock.getOppositePartDirection((BlockState)state).getOpposite());
    }

    public static Direction getOppositePartDirection(BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        return state.get((Property)PART) == BedPart.HEAD ? direction.getOpposite() : direction;
    }

    public static DoubleBlockProperties.Type getBedPart(BlockState state) {
        BedPart bedPart = (BedPart)state.get((Property)PART);
        if (bedPart == BedPart.HEAD) {
            return DoubleBlockProperties.Type.FIRST;
        }
        return DoubleBlockProperties.Type.SECOND;
    }

    private static boolean isBedBelow(BlockView world, BlockPos pos) {
        return world.getBlockState(pos.down()).getBlock() instanceof BedBlock;
    }

    public static Optional<Vec3d> findWakeUpPosition(EntityType<?> type, CollisionView world, BlockPos pos, Direction bedDirection, float spawnAngle) {
        Direction direction2;
        Direction direction = bedDirection.rotateYClockwise();
        Direction direction3 = direction2 = direction.pointsTo(spawnAngle) ? direction.getOpposite() : direction;
        if (BedBlock.isBedBelow((BlockView)world, (BlockPos)pos)) {
            return BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)pos, (Direction)bedDirection, (Direction)direction2);
        }
        int[][] is = BedBlock.getAroundAndOnBedOffsets((Direction)bedDirection, (Direction)direction2);
        Optional optional = BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)pos, (int[][])is, (boolean)true);
        if (optional.isPresent()) {
            return optional;
        }
        return BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)pos, (int[][])is, (boolean)false);
    }

    private static Optional<Vec3d> findWakeUpPosition(EntityType<?> type, CollisionView world, BlockPos pos, Direction bedDirection, Direction respawnDirection) {
        int[][] is = BedBlock.getAroundBedOffsets((Direction)bedDirection, (Direction)respawnDirection);
        Optional optional = BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)pos, (int[][])is, (boolean)true);
        if (optional.isPresent()) {
            return optional;
        }
        BlockPos blockPos = pos.down();
        Optional optional2 = BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)blockPos, (int[][])is, (boolean)true);
        if (optional2.isPresent()) {
            return optional2;
        }
        int[][] js = BedBlock.getOnBedOffsets((Direction)bedDirection);
        Optional optional3 = BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)pos, (int[][])js, (boolean)true);
        if (optional3.isPresent()) {
            return optional3;
        }
        Optional optional4 = BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)pos, (int[][])is, (boolean)false);
        if (optional4.isPresent()) {
            return optional4;
        }
        Optional optional5 = BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)blockPos, (int[][])is, (boolean)false);
        if (optional5.isPresent()) {
            return optional5;
        }
        return BedBlock.findWakeUpPosition(type, (CollisionView)world, (BlockPos)pos, (int[][])js, (boolean)false);
    }

    private static Optional<Vec3d> findWakeUpPosition(EntityType<?> type, CollisionView world, BlockPos pos, int[][] possibleOffsets, boolean ignoreInvalidPos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int[] is : possibleOffsets) {
            mutable.set(pos.getX() + is[0], pos.getY(), pos.getZ() + is[1]);
            Vec3d vec3d = Dismounting.findRespawnPos(type, (CollisionView)world, (BlockPos)mutable, (boolean)ignoreInvalidPos);
            if (vec3d == null) continue;
            return Optional.of(vec3d);
        }
        return Optional.empty();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, PART, OCCUPIED});
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BedBlockEntity(pos, state, this.color);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            BlockPos blockPos = pos.offset((Direction)state.get((Property)FACING));
            world.setBlockState(blockPos, (BlockState)state.with((Property)PART, (Comparable)BedPart.HEAD), 3);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors((WorldAccess)world, pos, 3);
        }
    }

    public DyeColor getColor() {
        return this.color;
    }

    protected long getRenderingSeed(BlockState state, BlockPos pos) {
        BlockPos blockPos = pos.offset((Direction)state.get((Property)FACING), state.get((Property)PART) == BedPart.HEAD ? 0 : 1);
        return MathHelper.hashCode((int)blockPos.getX(), (int)pos.getY(), (int)blockPos.getZ());
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    private static int[][] getAroundAndOnBedOffsets(Direction bedDirection, Direction respawnDirection) {
        return (int[][])ArrayUtils.addAll((Object[])BedBlock.getAroundBedOffsets((Direction)bedDirection, (Direction)respawnDirection), (Object[])BedBlock.getOnBedOffsets((Direction)bedDirection));
    }

    private static int[][] getAroundBedOffsets(Direction bedDirection, Direction respawnDirection) {
        return new int[][]{{respawnDirection.getOffsetX(), respawnDirection.getOffsetZ()}, {respawnDirection.getOffsetX() - bedDirection.getOffsetX(), respawnDirection.getOffsetZ() - bedDirection.getOffsetZ()}, {respawnDirection.getOffsetX() - bedDirection.getOffsetX() * 2, respawnDirection.getOffsetZ() - bedDirection.getOffsetZ() * 2}, {-bedDirection.getOffsetX() * 2, -bedDirection.getOffsetZ() * 2}, {-respawnDirection.getOffsetX() - bedDirection.getOffsetX() * 2, -respawnDirection.getOffsetZ() - bedDirection.getOffsetZ() * 2}, {-respawnDirection.getOffsetX() - bedDirection.getOffsetX(), -respawnDirection.getOffsetZ() - bedDirection.getOffsetZ()}, {-respawnDirection.getOffsetX(), -respawnDirection.getOffsetZ()}, {-respawnDirection.getOffsetX() + bedDirection.getOffsetX(), -respawnDirection.getOffsetZ() + bedDirection.getOffsetZ()}, {bedDirection.getOffsetX(), bedDirection.getOffsetZ()}, {respawnDirection.getOffsetX() + bedDirection.getOffsetX(), respawnDirection.getOffsetZ() + bedDirection.getOffsetZ()}};
    }

    private static int[][] getOnBedOffsets(Direction bedDirection) {
        return new int[][]{{0, 0}, {-bedDirection.getOffsetX(), -bedDirection.getOffsetZ()}};
    }
}


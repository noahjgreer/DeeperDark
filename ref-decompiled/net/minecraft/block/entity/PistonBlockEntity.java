/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.PistonBlock
 *  net.minecraft.block.PistonHeadBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.PistonBlockEntity
 *  net.minecraft.block.entity.PistonBlockEntity$1
 *  net.minecraft.block.enums.PistonType
 *  net.minecraft.block.piston.PistonBehavior
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.MovementType
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Boxes
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.block.OrientationHelper
 */
package net.minecraft.block.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.OrientationHelper;

/*
 * Exception performing whole class analysis ignored.
 */
public class PistonBlockEntity
extends BlockEntity {
    private static final int field_31382 = 2;
    private static final double field_31383 = 0.01;
    public static final double field_31381 = 0.51;
    private static final BlockState DEFAULT_PUSHED_BLOCK_STATE = Blocks.AIR.getDefaultState();
    private static final float DEFAULT_PROGRESS = 0.0f;
    private static final boolean DEFAULT_EXTENDING = false;
    private static final boolean DEFAULT_SOURCE = false;
    private BlockState pushedBlockState = DEFAULT_PUSHED_BLOCK_STATE;
    private Direction facing;
    private boolean extending = false;
    private boolean source = false;
    private static final ThreadLocal<Direction> ENTITY_MOVEMENT_DIRECTION = ThreadLocal.withInitial(() -> null);
    private float progress = 0.0f;
    private float lastProgress = 0.0f;
    private long savedWorldTime;
    private int field_26705;

    public PistonBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.PISTON, pos, state);
    }

    public PistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
        this(pos, state);
        this.pushedBlockState = pushedBlock;
        this.facing = facing;
        this.extending = extending;
        this.source = source;
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    public boolean isExtending() {
        return this.extending;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public boolean isSource() {
        return this.source;
    }

    public float getProgress(float tickProgress) {
        if (tickProgress > 1.0f) {
            tickProgress = 1.0f;
        }
        return MathHelper.lerp((float)tickProgress, (float)this.lastProgress, (float)this.progress);
    }

    public float getRenderOffsetX(float tickProgress) {
        return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(tickProgress));
    }

    public float getRenderOffsetY(float tickProgress) {
        return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(tickProgress));
    }

    public float getRenderOffsetZ(float tickProgress) {
        return (float)this.facing.getOffsetZ() * this.getAmountExtended(this.getProgress(tickProgress));
    }

    private float getAmountExtended(float progress) {
        return this.extending ? progress - 1.0f : 1.0f - progress;
    }

    private BlockState getHeadBlockState() {
        if (!this.isExtending() && this.isSource() && this.pushedBlockState.getBlock() instanceof PistonBlock) {
            return (BlockState)((BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with((Property)PistonHeadBlock.SHORT, (Comparable)Boolean.valueOf(this.progress > 0.25f))).with((Property)PistonHeadBlock.TYPE, (Comparable)(this.pushedBlockState.isOf(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT))).with((Property)PistonHeadBlock.FACING, (Comparable)((Direction)this.pushedBlockState.get((Property)PistonBlock.FACING)));
        }
        return this.pushedBlockState;
    }

    private static void pushEntities(World world, BlockPos pos, float f, PistonBlockEntity blockEntity) {
        Direction direction = blockEntity.getMovementDirection();
        double d = f - blockEntity.progress;
        VoxelShape voxelShape = blockEntity.getHeadBlockState().getCollisionShape((BlockView)world, pos);
        if (voxelShape.isEmpty()) {
            return;
        }
        Box box = PistonBlockEntity.offsetHeadBox((BlockPos)pos, (Box)voxelShape.getBoundingBox(), (PistonBlockEntity)blockEntity);
        List list = world.getOtherEntities(null, Boxes.stretch((Box)box, (Direction)direction, (double)d).union(box));
        if (list.isEmpty()) {
            return;
        }
        List list2 = voxelShape.getBoundingBoxes();
        boolean bl = blockEntity.pushedBlockState.isOf(Blocks.SLIME_BLOCK);
        for (Entity entity : list) {
            Box box4;
            Box box2;
            Box box3;
            if (entity.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            if (bl) {
                if (entity instanceof ServerPlayerEntity) continue;
                Vec3d vec3d = entity.getVelocity();
                double e = vec3d.x;
                double g = vec3d.y;
                double h = vec3d.z;
                switch (1.field_12210[direction.getAxis().ordinal()]) {
                    case 1: {
                        e = direction.getOffsetX();
                        break;
                    }
                    case 2: {
                        g = direction.getOffsetY();
                        break;
                    }
                    case 3: {
                        h = direction.getOffsetZ();
                    }
                }
                entity.setVelocity(e, g, h);
            }
            double i = 0.0;
            Iterator iterator = list2.iterator();
            while (!(!iterator.hasNext() || (box3 = Boxes.stretch((Box)PistonBlockEntity.offsetHeadBox((BlockPos)pos, (Box)(box2 = (Box)iterator.next()), (PistonBlockEntity)blockEntity), (Direction)direction, (double)d)).intersects(box4 = entity.getBoundingBox()) && (i = Math.max(i, PistonBlockEntity.getIntersectionSize((Box)box3, (Direction)direction, (Box)box4))) >= d)) {
            }
            if (i <= 0.0) continue;
            i = Math.min(i, d) + 0.01;
            PistonBlockEntity.moveEntity((Direction)direction, (Entity)entity, (double)i, (Direction)direction);
            if (blockEntity.extending || !blockEntity.source) continue;
            PistonBlockEntity.push((BlockPos)pos, (Entity)entity, (Direction)direction, (double)d);
        }
    }

    private static void moveEntity(Direction direction, Entity entity, double distance, Direction movementDirection) {
        ENTITY_MOVEMENT_DIRECTION.set(direction);
        Vec3d vec3d = entity.getEntityPos();
        entity.move(MovementType.PISTON, new Vec3d(distance * (double)movementDirection.getOffsetX(), distance * (double)movementDirection.getOffsetY(), distance * (double)movementDirection.getOffsetZ()));
        entity.tickBlockCollision(vec3d, entity.getEntityPos());
        entity.popQueuedCollisionCheck();
        ENTITY_MOVEMENT_DIRECTION.set(null);
    }

    private static void moveEntitiesInHoneyBlock(World world, BlockPos pos, float f, PistonBlockEntity blockEntity) {
        if (!blockEntity.isPushingHoneyBlock()) {
            return;
        }
        Direction direction = blockEntity.getMovementDirection();
        if (!direction.getAxis().isHorizontal()) {
            return;
        }
        double d = blockEntity.pushedBlockState.getCollisionShape((BlockView)world, pos).getMax(Direction.Axis.Y);
        Box box = PistonBlockEntity.offsetHeadBox((BlockPos)pos, (Box)new Box(0.0, d, 0.0, 1.0, 1.5000010000000001, 1.0), (PistonBlockEntity)blockEntity);
        double e = f - blockEntity.progress;
        List list = world.getOtherEntities((Entity)null, box, entity -> PistonBlockEntity.canMoveEntity((Box)box, (Entity)entity, (BlockPos)pos));
        for (Entity entity2 : list) {
            PistonBlockEntity.moveEntity((Direction)direction, (Entity)entity2, (double)e, (Direction)direction);
        }
    }

    private static boolean canMoveEntity(Box box, Entity entity, BlockPos pos) {
        return entity.getPistonBehavior() == PistonBehavior.NORMAL && entity.isOnGround() && (entity.isSupportedBy(pos) || entity.getX() >= box.minX && entity.getX() <= box.maxX && entity.getZ() >= box.minZ && entity.getZ() <= box.maxZ);
    }

    private boolean isPushingHoneyBlock() {
        return this.pushedBlockState.isOf(Blocks.HONEY_BLOCK);
    }

    public Direction getMovementDirection() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }

    private static double getIntersectionSize(Box box, Direction direction, Box box2) {
        switch (1.field_21467[direction.ordinal()]) {
            case 1: {
                return box.maxX - box2.minX;
            }
            case 2: {
                return box2.maxX - box.minX;
            }
            default: {
                return box.maxY - box2.minY;
            }
            case 4: {
                return box2.maxY - box.minY;
            }
            case 5: {
                return box.maxZ - box2.minZ;
            }
            case 6: 
        }
        return box2.maxZ - box.minZ;
    }

    private static Box offsetHeadBox(BlockPos pos, Box box, PistonBlockEntity blockEntity) {
        double d = blockEntity.getAmountExtended(blockEntity.progress);
        return box.offset((double)pos.getX() + d * (double)blockEntity.facing.getOffsetX(), (double)pos.getY() + d * (double)blockEntity.facing.getOffsetY(), (double)pos.getZ() + d * (double)blockEntity.facing.getOffsetZ());
    }

    private static void push(BlockPos pos, Entity entity, Direction direction, double amount) {
        double e;
        Direction direction2;
        double d;
        Box box2;
        Box box = entity.getBoundingBox();
        if (box.intersects(box2 = VoxelShapes.fullCube().getBoundingBox().offset(pos)) && Math.abs((d = PistonBlockEntity.getIntersectionSize((Box)box2, (Direction)(direction2 = direction.getOpposite()), (Box)box) + 0.01) - (e = PistonBlockEntity.getIntersectionSize((Box)box2, (Direction)direction2, (Box)box.intersection(box2)) + 0.01)) < 0.01) {
            d = Math.min(d, amount) + 0.01;
            PistonBlockEntity.moveEntity((Direction)direction, (Entity)entity, (double)d, (Direction)direction2);
        }
    }

    public BlockState getPushedBlock() {
        return this.pushedBlockState;
    }

    public void finish() {
        if (this.world != null && (this.lastProgress < 1.0f || this.world.isClient())) {
            this.lastProgress = this.progress = 1.0f;
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.world.getBlockState(this.pos).isOf(Blocks.MOVING_PISTON)) {
                BlockState blockState = this.source ? Blocks.AIR.getDefaultState() : Block.postProcessState((BlockState)this.pushedBlockState, (WorldAccess)this.world, (BlockPos)this.pos);
                this.world.setBlockState(this.pos, blockState, 3);
                this.world.updateNeighbor(this.pos, blockState.getBlock(), OrientationHelper.getEmissionOrientation((World)this.world, (Direction)this.getDirection(), null));
            }
        }
    }

    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        this.finish();
    }

    public Direction getDirection() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }

    public static void tick(World world, BlockPos pos, BlockState state, PistonBlockEntity blockEntity) {
        blockEntity.savedWorldTime = world.getTime();
        blockEntity.lastProgress = blockEntity.progress;
        if (blockEntity.lastProgress >= 1.0f) {
            if (world.isClient() && blockEntity.field_26705 < 5) {
                ++blockEntity.field_26705;
                return;
            }
            world.removeBlockEntity(pos);
            blockEntity.markRemoved();
            if (world.getBlockState(pos).isOf(Blocks.MOVING_PISTON)) {
                BlockState blockState = Block.postProcessState((BlockState)blockEntity.pushedBlockState, (WorldAccess)world, (BlockPos)pos);
                if (blockState.isAir()) {
                    world.setBlockState(pos, blockEntity.pushedBlockState, 340);
                    Block.replace((BlockState)blockEntity.pushedBlockState, (BlockState)blockState, (WorldAccess)world, (BlockPos)pos, (int)3);
                } else {
                    if (blockState.contains((Property)Properties.WATERLOGGED) && ((Boolean)blockState.get((Property)Properties.WATERLOGGED)).booleanValue()) {
                        blockState = (BlockState)blockState.with((Property)Properties.WATERLOGGED, (Comparable)Boolean.valueOf(false));
                    }
                    world.setBlockState(pos, blockState, 67);
                    world.updateNeighbor(pos, blockState.getBlock(), OrientationHelper.getEmissionOrientation((World)world, (Direction)blockEntity.getDirection(), null));
                }
            }
            return;
        }
        float f = blockEntity.progress + 0.5f;
        PistonBlockEntity.pushEntities((World)world, (BlockPos)pos, (float)f, (PistonBlockEntity)blockEntity);
        PistonBlockEntity.moveEntitiesInHoneyBlock((World)world, (BlockPos)pos, (float)f, (PistonBlockEntity)blockEntity);
        blockEntity.progress = f;
        if (blockEntity.progress >= 1.0f) {
            blockEntity.progress = 1.0f;
        }
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.pushedBlockState = view.read("blockState", BlockState.CODEC).orElse(DEFAULT_PUSHED_BLOCK_STATE);
        this.facing = view.read("facing", Direction.INDEX_CODEC).orElse(Direction.DOWN);
        this.lastProgress = this.progress = view.getFloat("progress", 0.0f);
        this.extending = view.getBoolean("extending", false);
        this.source = view.getBoolean("source", false);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("blockState", BlockState.CODEC, (Object)this.pushedBlockState);
        view.put("facing", Direction.INDEX_CODEC, (Object)this.facing);
        view.putFloat("progress", this.lastProgress);
        view.putBoolean("extending", this.extending);
        view.putBoolean("source", this.source);
    }

    public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
        VoxelShape voxelShape = !this.extending && this.source && this.pushedBlockState.getBlock() instanceof PistonBlock ? ((BlockState)this.pushedBlockState.with((Property)PistonBlock.EXTENDED, (Comparable)Boolean.valueOf(true))).getCollisionShape(world, pos) : VoxelShapes.empty();
        Direction direction = (Direction)ENTITY_MOVEMENT_DIRECTION.get();
        if ((double)this.progress < 1.0 && direction == this.getMovementDirection()) {
            return voxelShape;
        }
        BlockState blockState = this.isSource() ? (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with((Property)PistonHeadBlock.FACING, (Comparable)this.facing)).with((Property)PistonHeadBlock.SHORT, (Comparable)Boolean.valueOf(this.extending != 1.0f - this.progress < 0.25f)) : this.pushedBlockState;
        float f = this.getAmountExtended(this.progress);
        double d = (float)this.facing.getOffsetX() * f;
        double e = (float)this.facing.getOffsetY() * f;
        double g = (float)this.facing.getOffsetZ() * f;
        return VoxelShapes.union((VoxelShape)voxelShape, (VoxelShape)blockState.getCollisionShape(world, pos).offset(d, e, g));
    }

    public long getSavedWorldTime() {
        return this.savedWorldTime;
    }

    public void setWorld(World world) {
        super.setWorld(world);
        if (world.createCommandRegistryWrapper(RegistryKeys.BLOCK).getOptional(this.pushedBlockState.getBlock().getRegistryEntry().registryKey()).isEmpty()) {
            this.pushedBlockState = Blocks.AIR.getDefaultState();
        }
    }
}


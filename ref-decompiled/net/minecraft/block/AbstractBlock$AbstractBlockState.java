/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public static abstract class AbstractBlock.AbstractBlockState
extends State<Block, BlockState> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final VoxelShape[] EMPTY_CULLING_FACES = Util.make(new VoxelShape[DIRECTIONS.length], direction -> Arrays.fill(direction, VoxelShapes.empty()));
    private static final VoxelShape[] FULL_CULLING_FACES = Util.make(new VoxelShape[DIRECTIONS.length], direction -> Arrays.fill(direction, VoxelShapes.fullCube()));
    private final int luminance;
    private final boolean hasSidedTransparency;
    private final boolean isAir;
    private final boolean burnable;
    @Deprecated
    private final boolean liquid;
    @Deprecated
    private boolean solid;
    private final PistonBehavior pistonBehavior;
    private final MapColor mapColor;
    private final float hardness;
    private final boolean toolRequired;
    private final boolean opaque;
    private final AbstractBlock.ContextPredicate solidBlockPredicate;
    private final AbstractBlock.ContextPredicate suffocationPredicate;
    private final AbstractBlock.ContextPredicate blockVisionPredicate;
    private final AbstractBlock.ContextPredicate postProcessPredicate;
    private final AbstractBlock.ContextPredicate emissiveLightingPredicate;
    private final  @Nullable AbstractBlock.Offsetter offsetter;
    private final boolean blockBreakParticles;
    private final NoteBlockInstrument instrument;
    private final boolean replaceable;
    private @Nullable ShapeCache shapeCache;
    private FluidState fluidState = Fluids.EMPTY.getDefaultState();
    private boolean ticksRandomly;
    private boolean opaqueFullCube;
    private VoxelShape cullingShape;
    private VoxelShape[] cullingFaces;
    private boolean transparent;
    private int opacity;

    protected AbstractBlock.AbstractBlockState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
        super(block, propertyMap, codec);
        AbstractBlock.Settings settings = block.settings;
        this.luminance = settings.luminance.applyAsInt(this.asBlockState());
        this.hasSidedTransparency = block.hasSidedTransparency(this.asBlockState());
        this.isAir = settings.isAir;
        this.burnable = settings.burnable;
        this.liquid = settings.liquid;
        this.pistonBehavior = settings.pistonBehavior;
        this.mapColor = settings.mapColorProvider.apply(this.asBlockState());
        this.hardness = settings.hardness;
        this.toolRequired = settings.toolRequired;
        this.opaque = settings.opaque;
        this.solidBlockPredicate = settings.solidBlockPredicate;
        this.suffocationPredicate = settings.suffocationPredicate;
        this.blockVisionPredicate = settings.blockVisionPredicate;
        this.postProcessPredicate = settings.postProcessPredicate;
        this.emissiveLightingPredicate = settings.emissiveLightingPredicate;
        this.offsetter = settings.offsetter;
        this.blockBreakParticles = settings.blockBreakParticles;
        this.instrument = settings.instrument;
        this.replaceable = settings.replaceable;
    }

    private boolean shouldBeSolid() {
        if (((Block)this.owner).settings.forceSolid) {
            return true;
        }
        if (((Block)this.owner).settings.forceNotSolid) {
            return false;
        }
        if (this.shapeCache == null) {
            return false;
        }
        VoxelShape voxelShape = this.shapeCache.collisionShape;
        if (voxelShape.isEmpty()) {
            return false;
        }
        Box box = voxelShape.getBoundingBox();
        if (box.getAverageSideLength() >= 0.7291666666666666) {
            return true;
        }
        return box.getLengthY() >= 1.0;
    }

    public void initShapeCache() {
        this.fluidState = ((Block)this.owner).getFluidState(this.asBlockState());
        this.ticksRandomly = ((Block)this.owner).hasRandomTicks(this.asBlockState());
        if (!this.getBlock().hasDynamicBounds()) {
            this.shapeCache = new ShapeCache(this.asBlockState());
        }
        this.solid = this.shouldBeSolid();
        this.cullingShape = this.opaque ? ((Block)this.owner).getCullingShape(this.asBlockState()) : VoxelShapes.empty();
        this.opaqueFullCube = Block.isShapeFullCube(this.cullingShape);
        if (this.cullingShape.isEmpty()) {
            this.cullingFaces = EMPTY_CULLING_FACES;
        } else if (this.opaqueFullCube) {
            this.cullingFaces = FULL_CULLING_FACES;
        } else {
            this.cullingFaces = new VoxelShape[DIRECTIONS.length];
            for (Direction direction : DIRECTIONS) {
                this.cullingFaces[direction.ordinal()] = this.cullingShape.getFace(direction);
            }
        }
        this.transparent = ((Block)this.owner).isTransparent(this.asBlockState());
        this.opacity = ((Block)this.owner).getOpacity(this.asBlockState());
    }

    public Block getBlock() {
        return (Block)this.owner;
    }

    public RegistryEntry<Block> getRegistryEntry() {
        return ((Block)this.owner).getRegistryEntry();
    }

    @Deprecated
    public boolean blocksMovement() {
        Block block = this.getBlock();
        return block != Blocks.COBWEB && block != Blocks.BAMBOO_SAPLING && this.isSolid();
    }

    @Deprecated
    public boolean isSolid() {
        return this.solid;
    }

    public boolean allowsSpawning(BlockView world, BlockPos pos, EntityType<?> type) {
        return this.getBlock().settings.allowsSpawningPredicate.test(this.asBlockState(), world, pos, type);
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public int getOpacity() {
        return this.opacity;
    }

    public VoxelShape getCullingFace(Direction direction) {
        return this.cullingFaces[direction.ordinal()];
    }

    public VoxelShape getCullingShape() {
        return this.cullingShape;
    }

    public boolean exceedsCube() {
        return this.shapeCache == null || this.shapeCache.exceedsCube;
    }

    public boolean hasSidedTransparency() {
        return this.hasSidedTransparency;
    }

    public int getLuminance() {
        return this.luminance;
    }

    public boolean isAir() {
        return this.isAir;
    }

    public boolean isBurnable() {
        return this.burnable;
    }

    @Deprecated
    public boolean isLiquid() {
        return this.liquid;
    }

    public MapColor getMapColor(BlockView world, BlockPos pos) {
        return this.mapColor;
    }

    public BlockState rotate(BlockRotation rotation) {
        return this.getBlock().rotate(this.asBlockState(), rotation);
    }

    public BlockState mirror(BlockMirror mirror) {
        return this.getBlock().mirror(this.asBlockState(), mirror);
    }

    public BlockRenderType getRenderType() {
        return this.getBlock().getRenderType(this.asBlockState());
    }

    public boolean hasEmissiveLighting(BlockView world, BlockPos pos) {
        return this.emissiveLightingPredicate.test(this.asBlockState(), world, pos);
    }

    public float getAmbientOcclusionLightLevel(BlockView world, BlockPos pos) {
        return this.getBlock().getAmbientOcclusionLightLevel(this.asBlockState(), world, pos);
    }

    public boolean isSolidBlock(BlockView world, BlockPos pos) {
        return this.solidBlockPredicate.test(this.asBlockState(), world, pos);
    }

    public boolean emitsRedstonePower() {
        return this.getBlock().emitsRedstonePower(this.asBlockState());
    }

    public int getWeakRedstonePower(BlockView world, BlockPos pos, Direction direction) {
        return this.getBlock().getWeakRedstonePower(this.asBlockState(), world, pos, direction);
    }

    public boolean hasComparatorOutput() {
        return this.getBlock().hasComparatorOutput(this.asBlockState());
    }

    public int getComparatorOutput(World world, BlockPos pos, Direction direction) {
        return this.getBlock().getComparatorOutput(this.asBlockState(), world, pos, direction);
    }

    public float getHardness(BlockView world, BlockPos pos) {
        return this.hardness;
    }

    public float calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos) {
        return this.getBlock().calcBlockBreakingDelta(this.asBlockState(), player, world, pos);
    }

    public int getStrongRedstonePower(BlockView world, BlockPos pos, Direction direction) {
        return this.getBlock().getStrongRedstonePower(this.asBlockState(), world, pos, direction);
    }

    public PistonBehavior getPistonBehavior() {
        return this.pistonBehavior;
    }

    public boolean isOpaqueFullCube() {
        return this.opaqueFullCube;
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public boolean isSideInvisible(BlockState state, Direction direction) {
        return this.getBlock().isSideInvisible(this.asBlockState(), state, direction);
    }

    public VoxelShape getOutlineShape(BlockView world, BlockPos pos) {
        return this.getOutlineShape(world, pos, ShapeContext.absent());
    }

    public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
        return this.getBlock().getOutlineShape(this.asBlockState(), world, pos, context);
    }

    public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.collisionShape;
        }
        return this.getCollisionShape(world, pos, ShapeContext.absent());
    }

    public VoxelShape getCollisionShape(BlockView world, BlockPos pos, ShapeContext context) {
        return this.getBlock().getCollisionShape(this.asBlockState(), world, pos, context);
    }

    public VoxelShape getInsideCollisionShape(BlockView blockView, BlockPos pos, Entity entity) {
        return this.getBlock().getInsideCollisionShape(this.asBlockState(), blockView, pos, entity);
    }

    public VoxelShape getSidesShape(BlockView world, BlockPos pos) {
        return this.getBlock().getSidesShape(this.asBlockState(), world, pos);
    }

    public VoxelShape getCameraCollisionShape(BlockView world, BlockPos pos, ShapeContext context) {
        return this.getBlock().getCameraCollisionShape(this.asBlockState(), world, pos, context);
    }

    public VoxelShape getRaycastShape(BlockView world, BlockPos pos) {
        return this.getBlock().getRaycastShape(this.asBlockState(), world, pos);
    }

    public final boolean hasSolidTopSurface(BlockView world, BlockPos pos, Entity entity) {
        return this.isSolidSurface(world, pos, entity, Direction.UP);
    }

    public final boolean isSolidSurface(BlockView world, BlockPos pos, Entity entity, Direction direction) {
        return Block.isFaceFullSquare(this.getCollisionShape(world, pos, ShapeContext.of(entity)), direction);
    }

    public Vec3d getModelOffset(BlockPos pos) {
        AbstractBlock.Offsetter offsetter = this.offsetter;
        if (offsetter != null) {
            return offsetter.evaluate(this.asBlockState(), pos);
        }
        return Vec3d.ZERO;
    }

    public boolean hasModelOffset() {
        return this.offsetter != null;
    }

    public boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
        return this.getBlock().onSyncedBlockEvent(this.asBlockState(), world, pos, type, data);
    }

    public void neighborUpdate(World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        this.getBlock().neighborUpdate(this.asBlockState(), world, pos, sourceBlock, wireOrientation, notify);
    }

    public final void updateNeighbors(WorldAccess world, BlockPos pos, @Block.SetBlockStateFlag int flags) {
        this.updateNeighbors(world, pos, flags, 512);
    }

    public final void updateNeighbors(WorldAccess world, BlockPos pos, @Block.SetBlockStateFlag int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : DIRECTIONS) {
            mutable.set((Vec3i)pos, direction);
            world.replaceWithStateForNeighborUpdate(direction.getOpposite(), mutable, pos, this.asBlockState(), flags, maxUpdateDepth);
        }
    }

    public final void prepare(WorldAccess world, BlockPos pos, @Block.SetBlockStateFlag int flags) {
        this.prepare(world, pos, flags, 512);
    }

    public void prepare(WorldAccess world, BlockPos pos, @Block.SetBlockStateFlag int flags, int maxUpdateDepth) {
        this.getBlock().prepare(this.asBlockState(), world, pos, flags, maxUpdateDepth);
    }

    public void onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify) {
        this.getBlock().onBlockAdded(this.asBlockState(), world, pos, state, notify);
    }

    public void onStateReplaced(ServerWorld world, BlockPos pos, boolean moved) {
        this.getBlock().onStateReplaced(this.asBlockState(), world, pos, moved);
    }

    public void onExploded(ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        this.getBlock().onExploded(this.asBlockState(), world, pos, explosion, stackMerger);
    }

    public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
        this.getBlock().scheduledTick(this.asBlockState(), world, pos, random);
    }

    public void randomTick(ServerWorld world, BlockPos pos, Random random) {
        this.getBlock().randomTick(this.asBlockState(), world, pos, random);
    }

    public void onEntityCollision(World world, BlockPos pos, Entity entity, EntityCollisionHandler entityCollisionHandler, boolean bl) {
        this.getBlock().onEntityCollision(this.asBlockState(), world, pos, entity, entityCollisionHandler, bl);
    }

    public void onStacksDropped(ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        this.getBlock().onStacksDropped(this.asBlockState(), world, pos, tool, dropExperience);
    }

    public List<ItemStack> getDroppedStacks(LootWorldContext.Builder builder) {
        return this.getBlock().getDroppedStacks(this.asBlockState(), builder);
    }

    public ActionResult onUseWithItem(ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return this.getBlock().onUseWithItem(stack, this.asBlockState(), world, hit.getBlockPos(), player, hand, hit);
    }

    public ActionResult onUse(World world, PlayerEntity player, BlockHitResult hit) {
        return this.getBlock().onUse(this.asBlockState(), world, hit.getBlockPos(), player, hit);
    }

    public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
        this.getBlock().onBlockBreakStart(this.asBlockState(), world, pos, player);
    }

    public boolean shouldSuffocate(BlockView world, BlockPos pos) {
        return this.suffocationPredicate.test(this.asBlockState(), world, pos);
    }

    public boolean shouldBlockVision(BlockView world, BlockPos pos) {
        return this.blockVisionPredicate.test(this.asBlockState(), world, pos);
    }

    public BlockState getStateForNeighborUpdate(WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return this.getBlock().getStateForNeighborUpdate(this.asBlockState(), world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public boolean canPathfindThrough(NavigationType type) {
        return this.getBlock().canPathfindThrough(this.asBlockState(), type);
    }

    public boolean canReplace(ItemPlacementContext context) {
        return this.getBlock().canReplace(this.asBlockState(), context);
    }

    public boolean canBucketPlace(Fluid fluid) {
        return this.getBlock().canBucketPlace(this.asBlockState(), fluid);
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }

    public boolean canPlaceAt(WorldView world, BlockPos pos) {
        return this.getBlock().canPlaceAt(this.asBlockState(), world, pos);
    }

    public boolean shouldPostProcess(BlockView world, BlockPos pos) {
        return this.postProcessPredicate.test(this.asBlockState(), world, pos);
    }

    public @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
        return this.getBlock().createScreenHandlerFactory(this.asBlockState(), world, pos);
    }

    public boolean isIn(TagKey<Block> tag) {
        return this.getBlock().getRegistryEntry().isIn(tag);
    }

    public boolean isIn(TagKey<Block> tag, Predicate<AbstractBlock.AbstractBlockState> predicate) {
        return this.isIn(tag) && predicate.test(this);
    }

    public boolean isIn(RegistryEntryList<Block> blocks) {
        return blocks.contains(this.getBlock().getRegistryEntry());
    }

    public boolean isOf(RegistryEntry<Block> blockEntry) {
        return this.isOf(blockEntry.value());
    }

    public Stream<TagKey<Block>> streamTags() {
        return this.getBlock().getRegistryEntry().streamTags();
    }

    public boolean hasBlockEntity() {
        return this.getBlock() instanceof BlockEntityProvider;
    }

    public boolean keepBlockEntityWhenReplacedWith(BlockState state) {
        return this.getBlock().keepBlockEntityWhenReplacedWith(state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getBlockEntityTicker(World world, BlockEntityType<T> blockEntityType) {
        if (this.getBlock() instanceof BlockEntityProvider) {
            return ((BlockEntityProvider)((Object)this.getBlock())).getTicker(world, this.asBlockState(), blockEntityType);
        }
        return null;
    }

    public boolean isOf(Block block) {
        return this.getBlock() == block;
    }

    public boolean matchesKey(RegistryKey<Block> key) {
        return this.getBlock().getRegistryEntry().matchesKey(key);
    }

    public FluidState getFluidState() {
        return this.fluidState;
    }

    public boolean hasRandomTicks() {
        return this.ticksRandomly;
    }

    public long getRenderingSeed(BlockPos pos) {
        return this.getBlock().getRenderingSeed(this.asBlockState(), pos);
    }

    public BlockSoundGroup getSoundGroup() {
        return this.getBlock().getSoundGroup(this.asBlockState());
    }

    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        this.getBlock().onProjectileHit(world, state, hit, projectile);
    }

    public boolean isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction) {
        return this.isSideSolid(world, pos, direction, SideShapeType.FULL);
    }

    public boolean isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType) {
        if (this.shapeCache != null) {
            return this.shapeCache.isSideSolid(direction, shapeType);
        }
        return shapeType.matches(this.asBlockState(), world, pos, direction);
    }

    public boolean isFullCube(BlockView world, BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.isFullCube;
        }
        return this.getBlock().isShapeFullCube(this.asBlockState(), world, pos);
    }

    public ItemStack getPickStack(WorldView world, BlockPos pos, boolean includeData) {
        return this.getBlock().getPickStack(world, pos, this.asBlockState(), includeData);
    }

    protected abstract BlockState asBlockState();

    public boolean isToolRequired() {
        return this.toolRequired;
    }

    public boolean hasBlockBreakParticles() {
        return this.blockBreakParticles;
    }

    public NoteBlockInstrument getInstrument() {
        return this.instrument;
    }

    static final class ShapeCache {
        private static final Direction[] DIRECTIONS = Direction.values();
        private static final int SHAPE_TYPE_LENGTH = SideShapeType.values().length;
        protected final VoxelShape collisionShape;
        protected final boolean exceedsCube;
        private final boolean[] solidSides;
        protected final boolean isFullCube;

        ShapeCache(BlockState state) {
            Block block = state.getBlock();
            this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent());
            if (!this.collisionShape.isEmpty() && state.hasModelOffset()) {
                throw new IllegalStateException(String.format(Locale.ROOT, "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registries.BLOCK.getId(block)));
            }
            this.exceedsCube = Arrays.stream(Direction.Axis.values()).anyMatch(axis -> this.collisionShape.getMin((Direction.Axis)axis) < 0.0 || this.collisionShape.getMax((Direction.Axis)axis) > 1.0);
            this.solidSides = new boolean[DIRECTIONS.length * SHAPE_TYPE_LENGTH];
            for (Direction direction : DIRECTIONS) {
                for (SideShapeType sideShapeType : SideShapeType.values()) {
                    this.solidSides[ShapeCache.indexSolidSide((Direction)direction, (SideShapeType)sideShapeType)] = sideShapeType.matches(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction);
                }
            }
            this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
        }

        public boolean isSideSolid(Direction direction, SideShapeType shapeType) {
            return this.solidSides[ShapeCache.indexSolidSide(direction, shapeType)];
        }

        private static int indexSolidSide(Direction direction, SideShapeType shapeType) {
            return direction.ordinal() * SHAPE_TYPE_LENGTH + shapeType.ordinal();
        }
    }
}

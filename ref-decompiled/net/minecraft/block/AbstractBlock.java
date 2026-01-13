/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$1
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.Block$SetBlockStateFlag
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.MapColor
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.LootTable
 *  net.minecraft.loot.context.LootContextParameters
 *  net.minecraft.loot.context.LootContextTypes
 *  net.minecraft.loot.context.LootWorldContext
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.resource.featuretoggle.ToggleableFeature
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.EmptyBlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.explosion.Explosion$DestructionType
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
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

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class AbstractBlock
implements ToggleableFeature {
    protected static final Direction[] DIRECTIONS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    protected final boolean collidable;
    protected final float resistance;
    protected final boolean randomTicks;
    protected final BlockSoundGroup soundGroup;
    protected final float slipperiness;
    protected final float velocityMultiplier;
    protected final float jumpVelocityMultiplier;
    protected final boolean dynamicBounds;
    protected final FeatureSet requiredFeatures;
    protected final Settings settings;
    protected final Optional<RegistryKey<LootTable>> lootTableKey;
    protected final String translationKey;

    public AbstractBlock(Settings settings) {
        this.collidable = settings.collidable;
        this.lootTableKey = settings.getLootTableKey();
        this.translationKey = settings.getTranslationKey();
        this.resistance = settings.resistance;
        this.randomTicks = settings.randomTicks;
        this.soundGroup = settings.soundGroup;
        this.slipperiness = settings.slipperiness;
        this.velocityMultiplier = settings.velocityMultiplier;
        this.jumpVelocityMultiplier = settings.jumpVelocityMultiplier;
        this.dynamicBounds = settings.dynamicBounds;
        this.requiredFeatures = settings.requiredFeatures;
        this.settings = settings;
    }

    public Settings getSettings() {
        return this.settings;
    }

    protected abstract MapCodec<? extends Block> getCodec();

    protected static <B extends Block> RecordCodecBuilder<B, Settings> createSettingsCodec() {
        return Settings.CODEC.fieldOf("properties").forGetter(AbstractBlock::getSettings);
    }

    public static <B extends Block> MapCodec<B> createCodec(Function<Settings, B> blockFromSettings) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)AbstractBlock.createSettingsCodec()).apply((Applicative)instance, blockFromSettings));
    }

    protected void prepare(BlockState state, WorldAccess world, BlockPos pos, @Block.SetBlockStateFlag int flags, int maxUpdateDepth) {
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        switch (1.field_10659[type.ordinal()]) {
            case 1: {
                return !state.isFullCube((BlockView)EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
            }
            case 2: {
                return state.getFluidState().isIn(FluidTags.WATER);
            }
            case 3: {
                return !state.isFullCube((BlockView)EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
            }
        }
        return false;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return state;
    }

    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (state.isAir() || explosion.getDestructionType() == Explosion.DestructionType.TRIGGER_BLOCK) {
            return;
        }
        Block block = state.getBlock();
        boolean bl = explosion.getCausingEntity() instanceof PlayerEntity;
        if (block.shouldDropItemsOnExplosion(explosion)) {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            LootWorldContext.Builder builder = new LootWorldContext.Builder(world).add(LootContextParameters.ORIGIN, (Object)Vec3d.ofCenter((Vec3i)pos)).add(LootContextParameters.TOOL, (Object)ItemStack.EMPTY).addOptional(LootContextParameters.BLOCK_ENTITY, (Object)blockEntity).addOptional(LootContextParameters.THIS_ENTITY, (Object)explosion.getEntity());
            if (explosion.getDestructionType() == Explosion.DestructionType.DESTROY_WITH_DECAY) {
                builder.add(LootContextParameters.EXPLOSION_RADIUS, (Object)Float.valueOf(explosion.getPower()));
            }
            state.onStacksDropped(world, pos, ItemStack.EMPTY, bl);
            state.getDroppedStacks(builder).forEach(stack -> stackMerger.accept((ItemStack)stack, pos));
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        block.onDestroyedByExplosion(world, pos, explosion);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
    }

    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        return false;
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return false;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return false;
    }

    protected FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.getDefaultState();
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return false;
    }

    protected float getMaxHorizontalModelOffset() {
        return 0.25f;
    }

    protected float getVerticalModelOffsetMultiplier() {
        return 0.2f;
    }

    public FeatureSet getRequiredFeatures() {
        return this.requiredFeatures;
    }

    protected boolean keepBlockEntityWhenReplacedWith(BlockState state) {
        return false;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        return state.isReplaceable() && (context.getStack().isEmpty() || !context.getStack().isOf(this.asItem()));
    }

    protected boolean canBucketPlace(BlockState state, Fluid fluid) {
        return state.isReplaceable() || !state.isSolid();
    }

    protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
        if (this.lootTableKey.isEmpty()) {
            return Collections.emptyList();
        }
        LootWorldContext lootWorldContext = builder.add(LootContextParameters.BLOCK_STATE, (Object)state).build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootWorldContext.getWorld();
        LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable((RegistryKey)this.lootTableKey.get());
        return lootTable.generateLoot(lootWorldContext);
    }

    protected long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode((Vec3i)pos);
    }

    protected VoxelShape getCullingShape(BlockState state) {
        return state.getOutlineShape((BlockView)EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, ShapeContext.absent());
    }

    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    protected int getOpacity(BlockState state) {
        if (state.isOpaqueFullCube()) {
            return 15;
        }
        return state.isTransparent() ? 0 : 1;
    }

    protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return state.isFullCube(world, pos) ? 0.2f : 1.0f;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return 0;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.collidable ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
    }

    protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
        return VoxelShapes.fullCube();
    }

    protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return Block.isShapeFullCube((VoxelShape)state.getCollisionShape(world, pos));
    }

    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getCollisionShape(state, world, pos, context);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    protected float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = player.canHarvest(state) ? 30 : 100;
        return player.getBlockBreakingSpeed(state) / f / (float)i;
    }

    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
    }

    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    public final Optional<RegistryKey<LootTable>> getLootTableKey() {
        return this.lootTableKey;
    }

    public final String getTranslationKey() {
        return this.translationKey;
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
    }

    protected boolean isTransparent(BlockState state) {
        return !Block.isShapeFullCube((VoxelShape)state.getOutlineShape((BlockView)EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) && state.getFluidState().isEmpty();
    }

    protected boolean hasRandomTicks(BlockState state) {
        return this.randomTicks;
    }

    protected BlockSoundGroup getSoundGroup(BlockState state) {
        return this.soundGroup;
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)this.asItem());
    }

    public abstract Item asItem();

    protected abstract Block asBlock();

    public MapColor getDefaultMapColor() {
        return (MapColor)this.settings.mapColorProvider.apply(this.asBlock().getDefaultState());
    }

    public float getHardness() {
        return this.settings.hardness;
    }
}


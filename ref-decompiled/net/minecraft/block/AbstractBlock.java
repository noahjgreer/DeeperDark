package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.State;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBlock implements ToggleableFeature {
   protected static final Direction[] DIRECTIONS;
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
   protected final Optional lootTableKey;
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

   protected abstract MapCodec getCodec();

   protected static RecordCodecBuilder createSettingsCodec() {
      return AbstractBlock.Settings.CODEC.fieldOf("properties").forGetter(AbstractBlock::getSettings);
   }

   public static MapCodec createCodec(Function blockFromSettings) {
      return RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(createSettingsCodec()).apply(instance, blockFromSettings);
      });
   }

   protected void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      switch (type) {
         case LAND:
            return !state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
         case WATER:
            return state.getFluidState().isIn(FluidTags.WATER);
         case AIR:
            return !state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
         default:
            return false;
      }
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

   protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer stackMerger) {
      if (!state.isAir() && explosion.getDestructionType() != Explosion.DestructionType.TRIGGER_BLOCK) {
         Block block = state.getBlock();
         boolean bl = explosion.getCausingEntity() instanceof PlayerEntity;
         if (block.shouldDropItemsOnExplosion(explosion)) {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            LootWorldContext.Builder builder = (new LootWorldContext.Builder(world)).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).add(LootContextParameters.TOOL, ItemStack.EMPTY).addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity).addOptional(LootContextParameters.THIS_ENTITY, explosion.getEntity());
            if (explosion.getDestructionType() == Explosion.DestructionType.DESTROY_WITH_DECAY) {
               builder.add(LootContextParameters.EXPLOSION_RADIUS, explosion.getPower());
            }

            state.onStacksDropped(world, pos, ItemStack.EMPTY, bl);
            state.getDroppedStacks(builder).forEach((stack) -> {
               stackMerger.accept(stack, pos);
            });
         }

         world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
         block.onDestroyedByExplosion(world, pos, explosion);
      }
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
      return 0.25F;
   }

   protected float getVerticalModelOffsetMultiplier() {
      return 0.2F;
   }

   public FeatureSet getRequiredFeatures() {
      return this.requiredFeatures;
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

   protected List getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
      if (this.lootTableKey.isEmpty()) {
         return Collections.emptyList();
      } else {
         LootWorldContext lootWorldContext = builder.add(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
         ServerWorld serverWorld = lootWorldContext.getWorld();
         LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable((RegistryKey)this.lootTableKey.get());
         return lootTable.generateLoot(lootWorldContext);
      }
   }

   protected long getRenderingSeed(BlockState state, BlockPos pos) {
      return MathHelper.hashCode(pos);
   }

   protected VoxelShape getCullingShape(BlockState state) {
      return state.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
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
      } else {
         return state.isTransparent() ? 0 : 1;
      }
   }

   @Nullable
   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return null;
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return true;
   }

   protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
      return state.isFullCube(world, pos) ? 0.2F : 1.0F;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
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
      return Block.isShapeFullCube(state.getCollisionShape(world, pos));
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
      if (f == -1.0F) {
         return 0.0F;
      } else {
         int i = player.canHarvest(state) ? 30 : 100;
         return player.getBlockBreakingSpeed(state) / f / (float)i;
      }
   }

   protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
   }

   protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return 0;
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return 0;
   }

   public final Optional getLootTableKey() {
      return this.lootTableKey;
   }

   public final String getTranslationKey() {
      return this.translationKey;
   }

   protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
   }

   protected boolean isTransparent(BlockState state) {
      return !Block.isShapeFullCube(state.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) && state.getFluidState().isEmpty();
   }

   protected boolean hasRandomTicks(BlockState state) {
      return this.randomTicks;
   }

   protected BlockSoundGroup getSoundGroup(BlockState state) {
      return this.soundGroup;
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return new ItemStack(this.asItem());
   }

   public abstract Item asItem();

   protected abstract Block asBlock();

   public MapColor getDefaultMapColor() {
      return (MapColor)this.settings.mapColorProvider.apply(this.asBlock().getDefaultState());
   }

   public float getHardness() {
      return this.settings.hardness;
   }

   static {
      DIRECTIONS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
   }

   public static class Settings {
      public static final Codec CODEC = Codec.unit(() -> {
         return create();
      });
      Function mapColorProvider = (state) -> {
         return MapColor.CLEAR;
      };
      boolean collidable = true;
      BlockSoundGroup soundGroup;
      ToIntFunction luminance;
      float resistance;
      float hardness;
      boolean toolRequired;
      boolean randomTicks;
      float slipperiness;
      float velocityMultiplier;
      float jumpVelocityMultiplier;
      @Nullable
      private RegistryKey registryKey;
      private RegistryKeyedValue lootTable;
      private RegistryKeyedValue translationKey;
      boolean opaque;
      boolean isAir;
      boolean burnable;
      /** @deprecated */
      @Deprecated
      boolean liquid;
      /** @deprecated */
      @Deprecated
      boolean forceNotSolid;
      boolean forceSolid;
      PistonBehavior pistonBehavior;
      boolean blockBreakParticles;
      NoteBlockInstrument instrument;
      boolean replaceable;
      TypedContextPredicate allowsSpawningPredicate;
      ContextPredicate solidBlockPredicate;
      ContextPredicate suffocationPredicate;
      ContextPredicate blockVisionPredicate;
      ContextPredicate postProcessPredicate;
      ContextPredicate emissiveLightingPredicate;
      boolean dynamicBounds;
      FeatureSet requiredFeatures;
      @Nullable
      Offsetter offsetter;

      private Settings() {
         this.soundGroup = BlockSoundGroup.STONE;
         this.luminance = (state) -> {
            return 0;
         };
         this.slipperiness = 0.6F;
         this.velocityMultiplier = 1.0F;
         this.jumpVelocityMultiplier = 1.0F;
         this.lootTable = (registryKey) -> {
            return Optional.of(RegistryKey.of(RegistryKeys.LOOT_TABLE, registryKey.getValue().withPrefixedPath("blocks/")));
         };
         this.translationKey = (registryKey) -> {
            return Util.createTranslationKey("block", registryKey.getValue());
         };
         this.opaque = true;
         this.pistonBehavior = PistonBehavior.NORMAL;
         this.blockBreakParticles = true;
         this.instrument = NoteBlockInstrument.HARP;
         this.allowsSpawningPredicate = (state, world, pos, type) -> {
            return state.isSideSolidFullSquare(world, pos, Direction.UP) && state.getLuminance() < 14;
         };
         this.solidBlockPredicate = (state, world, pos) -> {
            return state.isFullCube(world, pos);
         };
         this.suffocationPredicate = (state, world, pos) -> {
            return state.blocksMovement() && state.isFullCube(world, pos);
         };
         this.blockVisionPredicate = this.suffocationPredicate;
         this.postProcessPredicate = (state, world, pos) -> {
            return false;
         };
         this.emissiveLightingPredicate = (state, world, pos) -> {
            return false;
         };
         this.requiredFeatures = FeatureFlags.VANILLA_FEATURES;
      }

      public static Settings create() {
         return new Settings();
      }

      public static Settings copy(AbstractBlock block) {
         Settings settings = copyShallow(block);
         Settings settings2 = block.settings;
         settings.jumpVelocityMultiplier = settings2.jumpVelocityMultiplier;
         settings.solidBlockPredicate = settings2.solidBlockPredicate;
         settings.allowsSpawningPredicate = settings2.allowsSpawningPredicate;
         settings.postProcessPredicate = settings2.postProcessPredicate;
         settings.suffocationPredicate = settings2.suffocationPredicate;
         settings.blockVisionPredicate = settings2.blockVisionPredicate;
         settings.lootTable = settings2.lootTable;
         settings.translationKey = settings2.translationKey;
         return settings;
      }

      /** @deprecated */
      @Deprecated
      public static Settings copyShallow(AbstractBlock block) {
         Settings settings = new Settings();
         Settings settings2 = block.settings;
         settings.hardness = settings2.hardness;
         settings.resistance = settings2.resistance;
         settings.collidable = settings2.collidable;
         settings.randomTicks = settings2.randomTicks;
         settings.luminance = settings2.luminance;
         settings.mapColorProvider = settings2.mapColorProvider;
         settings.soundGroup = settings2.soundGroup;
         settings.slipperiness = settings2.slipperiness;
         settings.velocityMultiplier = settings2.velocityMultiplier;
         settings.dynamicBounds = settings2.dynamicBounds;
         settings.opaque = settings2.opaque;
         settings.isAir = settings2.isAir;
         settings.burnable = settings2.burnable;
         settings.liquid = settings2.liquid;
         settings.forceNotSolid = settings2.forceNotSolid;
         settings.forceSolid = settings2.forceSolid;
         settings.pistonBehavior = settings2.pistonBehavior;
         settings.toolRequired = settings2.toolRequired;
         settings.offsetter = settings2.offsetter;
         settings.blockBreakParticles = settings2.blockBreakParticles;
         settings.requiredFeatures = settings2.requiredFeatures;
         settings.emissiveLightingPredicate = settings2.emissiveLightingPredicate;
         settings.instrument = settings2.instrument;
         settings.replaceable = settings2.replaceable;
         return settings;
      }

      public Settings mapColor(DyeColor color) {
         this.mapColorProvider = (state) -> {
            return color.getMapColor();
         };
         return this;
      }

      public Settings mapColor(MapColor color) {
         this.mapColorProvider = (state) -> {
            return color;
         };
         return this;
      }

      public Settings mapColor(Function mapColorProvider) {
         this.mapColorProvider = mapColorProvider;
         return this;
      }

      public Settings noCollision() {
         this.collidable = false;
         this.opaque = false;
         return this;
      }

      public Settings nonOpaque() {
         this.opaque = false;
         return this;
      }

      public Settings slipperiness(float slipperiness) {
         this.slipperiness = slipperiness;
         return this;
      }

      public Settings velocityMultiplier(float velocityMultiplier) {
         this.velocityMultiplier = velocityMultiplier;
         return this;
      }

      public Settings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
         this.jumpVelocityMultiplier = jumpVelocityMultiplier;
         return this;
      }

      public Settings sounds(BlockSoundGroup soundGroup) {
         this.soundGroup = soundGroup;
         return this;
      }

      public Settings luminance(ToIntFunction luminance) {
         this.luminance = luminance;
         return this;
      }

      public Settings strength(float hardness, float resistance) {
         return this.hardness(hardness).resistance(resistance);
      }

      public Settings breakInstantly() {
         return this.strength(0.0F);
      }

      public Settings strength(float strength) {
         this.strength(strength, strength);
         return this;
      }

      public Settings ticksRandomly() {
         this.randomTicks = true;
         return this;
      }

      public Settings dynamicBounds() {
         this.dynamicBounds = true;
         return this;
      }

      public Settings dropsNothing() {
         this.lootTable = RegistryKeyedValue.fixed(Optional.empty());
         return this;
      }

      public Settings lootTable(Optional lootTableKey) {
         this.lootTable = RegistryKeyedValue.fixed(lootTableKey);
         return this;
      }

      protected Optional getLootTableKey() {
         return (Optional)this.lootTable.get((RegistryKey)Objects.requireNonNull(this.registryKey, "Block id not set"));
      }

      public Settings burnable() {
         this.burnable = true;
         return this;
      }

      public Settings liquid() {
         this.liquid = true;
         return this;
      }

      public Settings solid() {
         this.forceSolid = true;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Settings notSolid() {
         this.forceNotSolid = true;
         return this;
      }

      public Settings pistonBehavior(PistonBehavior pistonBehavior) {
         this.pistonBehavior = pistonBehavior;
         return this;
      }

      public Settings air() {
         this.isAir = true;
         return this;
      }

      public Settings allowsSpawning(TypedContextPredicate predicate) {
         this.allowsSpawningPredicate = predicate;
         return this;
      }

      public Settings solidBlock(ContextPredicate predicate) {
         this.solidBlockPredicate = predicate;
         return this;
      }

      public Settings suffocates(ContextPredicate predicate) {
         this.suffocationPredicate = predicate;
         return this;
      }

      public Settings blockVision(ContextPredicate predicate) {
         this.blockVisionPredicate = predicate;
         return this;
      }

      public Settings postProcess(ContextPredicate predicate) {
         this.postProcessPredicate = predicate;
         return this;
      }

      public Settings emissiveLighting(ContextPredicate predicate) {
         this.emissiveLightingPredicate = predicate;
         return this;
      }

      public Settings requiresTool() {
         this.toolRequired = true;
         return this;
      }

      public Settings hardness(float hardness) {
         this.hardness = hardness;
         return this;
      }

      public Settings resistance(float resistance) {
         this.resistance = Math.max(0.0F, resistance);
         return this;
      }

      public Settings offset(OffsetType offsetType) {
         Offsetter var10001;
         switch (offsetType.ordinal()) {
            case 0:
               var10001 = null;
               break;
            case 1:
               var10001 = (state, pos) -> {
                  Block block = state.getBlock();
                  long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
                  float f = block.getMaxHorizontalModelOffset();
                  double d = MathHelper.clamp(((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                  double e = MathHelper.clamp(((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                  return new Vec3d(d, 0.0, e);
               };
               break;
            case 2:
               var10001 = (state, pos) -> {
                  Block block = state.getBlock();
                  long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
                  double d = ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * (double)block.getVerticalModelOffsetMultiplier();
                  float f = block.getMaxHorizontalModelOffset();
                  double e = MathHelper.clamp(((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                  double g = MathHelper.clamp(((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                  return new Vec3d(e, d, g);
               };
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         this.offsetter = var10001;
         return this;
      }

      public Settings noBlockBreakParticles() {
         this.blockBreakParticles = false;
         return this;
      }

      public Settings requires(FeatureFlag... features) {
         this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
         return this;
      }

      public Settings instrument(NoteBlockInstrument instrument) {
         this.instrument = instrument;
         return this;
      }

      public Settings replaceable() {
         this.replaceable = true;
         return this;
      }

      public Settings registryKey(RegistryKey registryKey) {
         this.registryKey = registryKey;
         return this;
      }

      public Settings overrideTranslationKey(String translationKey) {
         this.translationKey = RegistryKeyedValue.fixed(translationKey);
         return this;
      }

      protected String getTranslationKey() {
         return (String)this.translationKey.get((RegistryKey)Objects.requireNonNull(this.registryKey, "Block id not set"));
      }
   }

   @FunctionalInterface
   public interface TypedContextPredicate {
      boolean test(BlockState state, BlockView world, BlockPos pos, Object type);
   }

   @FunctionalInterface
   public interface Offsetter {
      Vec3d evaluate(BlockState state, BlockPos pos);
   }

   @FunctionalInterface
   public interface ContextPredicate {
      boolean test(BlockState state, BlockView world, BlockPos pos);
   }

   public abstract static class AbstractBlockState extends State {
      private static final Direction[] DIRECTIONS = Direction.values();
      private static final VoxelShape[] EMPTY_CULLING_FACES;
      private static final VoxelShape[] FULL_CULLING_FACES;
      private final int luminance;
      private final boolean hasSidedTransparency;
      private final boolean isAir;
      private final boolean burnable;
      /** @deprecated */
      @Deprecated
      private final boolean liquid;
      /** @deprecated */
      @Deprecated
      private boolean solid;
      private final PistonBehavior pistonBehavior;
      private final MapColor mapColor;
      private final float hardness;
      private final boolean toolRequired;
      private final boolean opaque;
      private final ContextPredicate solidBlockPredicate;
      private final ContextPredicate suffocationPredicate;
      private final ContextPredicate blockVisionPredicate;
      private final ContextPredicate postProcessPredicate;
      private final ContextPredicate emissiveLightingPredicate;
      @Nullable
      private final Offsetter offsetter;
      private final boolean blockBreakParticles;
      private final NoteBlockInstrument instrument;
      private final boolean replaceable;
      @Nullable
      private ShapeCache shapeCache;
      private FluidState fluidState;
      private boolean ticksRandomly;
      private boolean opaqueFullCube;
      private VoxelShape cullingShape;
      private VoxelShape[] cullingFaces;
      private boolean transparent;
      private int opacity;

      protected AbstractBlockState(Block block, Reference2ObjectArrayMap propertyMap, MapCodec codec) {
         super(block, propertyMap, codec);
         this.fluidState = Fluids.EMPTY.getDefaultState();
         Settings settings = block.settings;
         this.luminance = settings.luminance.applyAsInt(this.asBlockState());
         this.hasSidedTransparency = block.hasSidedTransparency(this.asBlockState());
         this.isAir = settings.isAir;
         this.burnable = settings.burnable;
         this.liquid = settings.liquid;
         this.pistonBehavior = settings.pistonBehavior;
         this.mapColor = (MapColor)settings.mapColorProvider.apply(this.asBlockState());
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
         } else if (((Block)this.owner).settings.forceNotSolid) {
            return false;
         } else if (this.shapeCache == null) {
            return false;
         } else {
            VoxelShape voxelShape = this.shapeCache.collisionShape;
            if (voxelShape.isEmpty()) {
               return false;
            } else {
               Box box = voxelShape.getBoundingBox();
               if (box.getAverageSideLength() >= 0.7291666666666666) {
                  return true;
               } else {
                  return box.getLengthY() >= 1.0;
               }
            }
         }
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
            Direction[] var1 = DIRECTIONS;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               Direction direction = var1[var3];
               this.cullingFaces[direction.ordinal()] = this.cullingShape.getFace(direction);
            }
         }

         this.transparent = ((Block)this.owner).isTransparent(this.asBlockState());
         this.opacity = ((Block)this.owner).getOpacity(this.asBlockState());
      }

      public Block getBlock() {
         return (Block)this.owner;
      }

      public RegistryEntry getRegistryEntry() {
         return ((Block)this.owner).getRegistryEntry();
      }

      /** @deprecated */
      @Deprecated
      public boolean blocksMovement() {
         Block block = this.getBlock();
         return block != Blocks.COBWEB && block != Blocks.BAMBOO_SAPLING && this.isSolid();
      }

      /** @deprecated */
      @Deprecated
      public boolean isSolid() {
         return this.solid;
      }

      public boolean allowsSpawning(BlockView world, BlockPos pos, EntityType type) {
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

      /** @deprecated */
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

      public int getComparatorOutput(World world, BlockPos pos) {
         return this.getBlock().getComparatorOutput(this.asBlockState(), world, pos);
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
         return this.shapeCache != null ? this.shapeCache.collisionShape : this.getCollisionShape(world, pos, ShapeContext.absent());
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
         Offsetter offsetter = this.offsetter;
         return offsetter != null ? offsetter.evaluate(this.asBlockState(), pos) : Vec3d.ZERO;
      }

      public boolean hasModelOffset() {
         return this.offsetter != null;
      }

      public boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
         return this.getBlock().onSyncedBlockEvent(this.asBlockState(), world, pos, type, data);
      }

      public void neighborUpdate(World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
         DebugInfoSender.sendNeighborUpdate(world, pos);
         this.getBlock().neighborUpdate(this.asBlockState(), world, pos, sourceBlock, wireOrientation, notify);
      }

      public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags) {
         this.updateNeighbors(world, pos, flags, 512);
      }

      public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         Direction[] var6 = AbstractBlock.DIRECTIONS;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            mutable.set(pos, (Direction)direction);
            world.replaceWithStateForNeighborUpdate(direction.getOpposite(), mutable, pos, this.asBlockState(), flags, maxUpdateDepth);
         }

      }

      public final void prepare(WorldAccess world, BlockPos pos, int flags) {
         this.prepare(world, pos, flags, 512);
      }

      public void prepare(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
         this.getBlock().prepare(this.asBlockState(), world, pos, flags, maxUpdateDepth);
      }

      public void onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify) {
         this.getBlock().onBlockAdded(this.asBlockState(), world, pos, state, notify);
      }

      public void onStateReplaced(ServerWorld world, BlockPos pos, boolean moved) {
         this.getBlock().onStateReplaced(this.asBlockState(), world, pos, moved);
      }

      public void onExploded(ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer stackMerger) {
         this.getBlock().onExploded(this.asBlockState(), world, pos, explosion, stackMerger);
      }

      public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
         this.getBlock().scheduledTick(this.asBlockState(), world, pos, random);
      }

      public void randomTick(ServerWorld world, BlockPos pos, Random random) {
         this.getBlock().randomTick(this.asBlockState(), world, pos, random);
      }

      public void onEntityCollision(World world, BlockPos pos, Entity entity, EntityCollisionHandler entityCollisionHandler) {
         this.getBlock().onEntityCollision(this.asBlockState(), world, pos, entity, entityCollisionHandler);
      }

      public void onStacksDropped(ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
         this.getBlock().onStacksDropped(this.asBlockState(), world, pos, tool, dropExperience);
      }

      public List getDroppedStacks(LootWorldContext.Builder builder) {
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

      @Nullable
      public NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
         return this.getBlock().createScreenHandlerFactory(this.asBlockState(), world, pos);
      }

      public boolean isIn(TagKey tag) {
         return this.getBlock().getRegistryEntry().isIn(tag);
      }

      public boolean isIn(TagKey tag, Predicate predicate) {
         return this.isIn(tag) && predicate.test(this);
      }

      public boolean isIn(RegistryEntryList blocks) {
         return blocks.contains(this.getBlock().getRegistryEntry());
      }

      public boolean isOf(RegistryEntry blockEntry) {
         return this.isOf((Block)blockEntry.value());
      }

      public Stream streamTags() {
         return this.getBlock().getRegistryEntry().streamTags();
      }

      public boolean hasBlockEntity() {
         return this.getBlock() instanceof BlockEntityProvider;
      }

      @Nullable
      public BlockEntityTicker getBlockEntityTicker(World world, BlockEntityType blockEntityType) {
         return this.getBlock() instanceof BlockEntityProvider ? ((BlockEntityProvider)this.getBlock()).getTicker(world, this.asBlockState(), blockEntityType) : null;
      }

      public boolean isOf(Block block) {
         return this.getBlock() == block;
      }

      public boolean matchesKey(RegistryKey key) {
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
         return this.shapeCache != null ? this.shapeCache.isSideSolid(direction, shapeType) : shapeType.matches(this.asBlockState(), world, pos, direction);
      }

      public boolean isFullCube(BlockView world, BlockPos pos) {
         return this.shapeCache != null ? this.shapeCache.isFullCube : this.getBlock().isShapeFullCube(this.asBlockState(), world, pos);
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

      static {
         EMPTY_CULLING_FACES = (VoxelShape[])Util.make(new VoxelShape[DIRECTIONS.length], (direction) -> {
            Arrays.fill(direction, VoxelShapes.empty());
         });
         FULL_CULLING_FACES = (VoxelShape[])Util.make(new VoxelShape[DIRECTIONS.length], (direction) -> {
            Arrays.fill(direction, VoxelShapes.fullCube());
         });
      }

      private static final class ShapeCache {
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
            } else {
               this.exceedsCube = Arrays.stream(Direction.Axis.values()).anyMatch((axis) -> {
                  return this.collisionShape.getMin(axis) < 0.0 || this.collisionShape.getMax(axis) > 1.0;
               });
               this.solidSides = new boolean[DIRECTIONS.length * SHAPE_TYPE_LENGTH];
               Direction[] var3 = DIRECTIONS;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Direction direction = var3[var5];
                  SideShapeType[] var7 = SideShapeType.values();
                  int var8 = var7.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     SideShapeType sideShapeType = var7[var9];
                     this.solidSides[indexSolidSide(direction, sideShapeType)] = sideShapeType.matches(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction);
                  }
               }

               this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
            }
         }

         public boolean isSideSolid(Direction direction, SideShapeType shapeType) {
            return this.solidSides[indexSolidSide(direction, shapeType)];
         }

         private static int indexSolidSide(Direction direction, SideShapeType shapeType) {
            return direction.ordinal() * SHAPE_TYPE_LENGTH + shapeType.ordinal();
         }
      }
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;

      // $FF: synthetic method
      private static OffsetType[] method_36719() {
         return new OffsetType[]{NONE, XZ, XYZ};
      }
   }
}

package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SnifferEntity extends AnimalEntity {
   private static final int field_42656 = 1700;
   private static final int field_42657 = 6000;
   private static final int field_42658 = 30;
   private static final int field_42659 = 120;
   private static final int field_42661 = 48000;
   private static final float field_44785 = 0.4F;
   private static final EntityDimensions DIMENSIONS;
   private static final TrackedData STATE;
   private static final TrackedData FINISH_DIG_TIME;
   public final AnimationState feelingHappyAnimationState = new AnimationState();
   public final AnimationState scentingAnimationState = new AnimationState();
   public final AnimationState sniffingAnimationState = new AnimationState();
   public final AnimationState diggingAnimationState = new AnimationState();
   public final AnimationState risingAnimationState = new AnimationState();

   public static DefaultAttributeContainer.Builder createSnifferAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.10000000149011612).add(EntityAttributes.MAX_HEALTH, 14.0);
   }

   public SnifferEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.getNavigation().setCanSwim(true);
      this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
      this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
      this.setPathfindingPenalty(PathNodeType.DAMAGE_CAUTIOUS, -1.0F);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(STATE, SnifferEntity.State.IDLING);
      builder.add(FINISH_DIG_TIME, 0);
   }

   public void onStartPathfinding() {
      super.onStartPathfinding();
      if (this.isOnFire() || this.isTouchingWater()) {
         this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
      }

   }

   public void onFinishPathfinding() {
      this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.getState() == SnifferEntity.State.DIGGING ? DIMENSIONS.scaled(this.getScaleFactor()) : super.getBaseDimensions(pose);
   }

   public boolean isSearching() {
      return this.getState() == SnifferEntity.State.SEARCHING;
   }

   public boolean isTempted() {
      return (Boolean)this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
   }

   public boolean canTryToDig() {
      return !this.isTempted() && !this.isPanicking() && !this.isTouchingWater() && !this.isInLove() && this.isOnGround() && !this.hasVehicle() && !this.isLeashed();
   }

   public boolean isDiggingOrSearching() {
      return this.getState() == SnifferEntity.State.DIGGING || this.getState() == SnifferEntity.State.SEARCHING;
   }

   private BlockPos getDigPos() {
      Vec3d vec3d = this.getDigLocation();
      return BlockPos.ofFloored(vec3d.getX(), this.getY() + 0.20000000298023224, vec3d.getZ());
   }

   private Vec3d getDigLocation() {
      return this.getPos().add(this.getRotationVecClient().multiply(2.25));
   }

   public boolean canUseQuadLeashAttachmentPoint() {
      return true;
   }

   public Vec3d[] getQuadLeashOffsets() {
      return Leashable.createQuadLeashOffsets(this, -0.01, 0.63, 0.38, 1.15);
   }

   private State getState() {
      return (State)this.dataTracker.get(STATE);
   }

   private SnifferEntity setState(State state) {
      this.dataTracker.set(STATE, state);
      return this;
   }

   public void onTrackedDataSet(TrackedData data) {
      if (STATE.equals(data)) {
         State state = this.getState();
         this.stopAnimations();
         switch (state.ordinal()) {
            case 1:
               this.feelingHappyAnimationState.startIfNotRunning(this.age);
               break;
            case 2:
               this.scentingAnimationState.startIfNotRunning(this.age);
               break;
            case 3:
               this.sniffingAnimationState.startIfNotRunning(this.age);
            case 4:
            default:
               break;
            case 5:
               this.diggingAnimationState.startIfNotRunning(this.age);
               break;
            case 6:
               this.risingAnimationState.startIfNotRunning(this.age);
         }

         this.calculateDimensions();
      }

      super.onTrackedDataSet(data);
   }

   private void stopAnimations() {
      this.diggingAnimationState.stop();
      this.sniffingAnimationState.stop();
      this.risingAnimationState.stop();
      this.feelingHappyAnimationState.stop();
      this.scentingAnimationState.stop();
   }

   public SnifferEntity startState(State state) {
      switch (state.ordinal()) {
         case 0:
            this.setState(SnifferEntity.State.IDLING);
            break;
         case 1:
            this.playSound(SoundEvents.ENTITY_SNIFFER_HAPPY, 1.0F, 1.0F);
            this.setState(SnifferEntity.State.FEELING_HAPPY);
            break;
         case 2:
            this.setState(SnifferEntity.State.SCENTING).playScentingSound();
            break;
         case 3:
            this.playSound(SoundEvents.ENTITY_SNIFFER_SNIFFING, 1.0F, 1.0F);
            this.setState(SnifferEntity.State.SNIFFING);
            break;
         case 4:
            this.setState(SnifferEntity.State.SEARCHING);
            break;
         case 5:
            this.setState(SnifferEntity.State.DIGGING).setDigging();
            break;
         case 6:
            this.playSound(SoundEvents.ENTITY_SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
            this.setState(SnifferEntity.State.RISING);
      }

      return this;
   }

   private SnifferEntity playScentingSound() {
      this.playSound(SoundEvents.ENTITY_SNIFFER_SCENTING, 1.0F, this.isBaby() ? 1.3F : 1.0F);
      return this;
   }

   private SnifferEntity setDigging() {
      this.dataTracker.set(FINISH_DIG_TIME, this.age + 120);
      this.getWorld().sendEntityStatus(this, (byte)63);
      return this;
   }

   public SnifferEntity finishDigging(boolean explored) {
      if (explored) {
         this.addExploredPosition(this.getSteppingPos());
      }

      return this;
   }

   Optional findSniffingTargetPos() {
      return IntStream.range(0, 5).mapToObj((i) -> {
         return FuzzyTargeting.find(this, 10 + 2 * i, 3);
      }).filter(Objects::nonNull).map(BlockPos::ofFloored).filter((pos) -> {
         return this.getWorld().getWorldBorder().contains(pos);
      }).map(BlockPos::down).filter(this::isDiggable).findFirst();
   }

   boolean canDig() {
      return !this.isPanicking() && !this.isTempted() && !this.isBaby() && !this.isTouchingWater() && this.isOnGround() && !this.hasVehicle() && this.isDiggable(this.getDigPos().down());
   }

   private boolean isDiggable(BlockPos pos) {
      return this.getWorld().getBlockState(pos).isIn(BlockTags.SNIFFER_DIGGABLE_BLOCK) && this.getExploredPositions().noneMatch((globalPos) -> {
         return GlobalPos.create(this.getWorld().getRegistryKey(), pos).equals(globalPos);
      }) && (Boolean)Optional.ofNullable(this.getNavigation().findPathTo((BlockPos)pos, 1)).map(Path::reachesTarget).orElse(false);
   }

   private void dropSeeds() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         if ((Integer)this.dataTracker.get(FINISH_DIG_TIME) == this.age) {
            BlockPos blockPos = this.getDigPos();
            this.forEachGiftedItem(serverWorld, LootTables.SNIFFER_DIGGING_GAMEPLAY, (serverWorldx, itemStack) -> {
               ItemEntity itemEntity = new ItemEntity(this.getWorld(), (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack);
               itemEntity.setToDefaultPickupDelay();
               serverWorldx.spawnEntity(itemEntity);
            });
            this.playSound(SoundEvents.ENTITY_SNIFFER_DROP_SEED, 1.0F, 1.0F);
            return;
         }
      }

   }

   private SnifferEntity spawnDiggingParticles(AnimationState diggingAnimationState) {
      boolean bl = diggingAnimationState.getTimeInMilliseconds((float)this.age) > 1700L && diggingAnimationState.getTimeInMilliseconds((float)this.age) < 6000L;
      if (bl) {
         BlockPos blockPos = this.getDigPos();
         BlockState blockState = this.getWorld().getBlockState(blockPos.down());
         if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            for(int i = 0; i < 30; ++i) {
               Vec3d vec3d = Vec3d.ofCenter(blockPos).add(0.0, -0.6499999761581421, 0.0);
               this.getWorld().addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
            }

            if (this.age % 10 == 0) {
               this.getWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), blockState.getSoundGroup().getHitSound(), this.getSoundCategory(), 0.5F, 0.5F, false);
            }
         }
      }

      if (this.age % 10 == 0) {
         this.getWorld().emitGameEvent(GameEvent.ENTITY_ACTION, this.getDigPos(), GameEvent.Emitter.of((Entity)this));
      }

      return this;
   }

   private SnifferEntity addExploredPosition(BlockPos pos) {
      List list = (List)this.getExploredPositions().limit(20L).collect(Collectors.toList());
      list.add(0, GlobalPos.create(this.getWorld().getRegistryKey(), pos));
      this.getBrain().remember(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, (Object)list);
      return this;
   }

   private Stream getExploredPositions() {
      return this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
   }

   public void jump() {
      super.jump();
      double d = this.moveControl.getSpeed();
      if (d > 0.0) {
         double e = this.getVelocity().horizontalLengthSquared();
         if (e < 0.01) {
            this.updateVelocity(0.1F, new Vec3d(0.0, 0.0, 1.0));
         }
      }

   }

   public void breed(ServerWorld world, AnimalEntity other) {
      ItemStack itemStack = new ItemStack(Items.SNIFFER_EGG);
      ItemEntity itemEntity = new ItemEntity(world, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), itemStack);
      itemEntity.setToDefaultPickupDelay();
      this.breed(world, other, (PassiveEntity)null);
      this.playSound(SoundEvents.BLOCK_SNIFFER_EGG_PLOP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
      world.spawnEntity(itemEntity);
   }

   public void onDeath(DamageSource damageSource) {
      this.startState(SnifferEntity.State.IDLING);
      super.onDeath(damageSource);
   }

   public void tick() {
      switch (this.getState().ordinal()) {
         case 4:
            this.playSearchingSound();
            break;
         case 5:
            this.spawnDiggingParticles(this.diggingAnimationState).dropSeeds();
      }

      super.tick();
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      boolean bl = this.isBreedingItem(itemStack);
      ActionResult actionResult = super.interactMob(player, hand);
      if (actionResult.isAccepted() && bl) {
         this.playEatSound();
      }

      return actionResult;
   }

   protected void playEatSound() {
      this.getWorld().playSoundFromEntity((Entity)null, this, SoundEvents.ENTITY_SNIFFER_EAT, SoundCategory.NEUTRAL, 1.0F, MathHelper.nextBetween(this.getWorld().random, 0.8F, 1.2F));
   }

   private void playSearchingSound() {
      if (this.getWorld().isClient() && this.age % 20 == 0) {
         this.getWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SNIFFER_SEARCHING, this.getSoundCategory(), 1.0F, 1.0F, false);
      }

   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_SNIFFER_STEP, 0.15F, 1.0F);
   }

   protected SoundEvent getAmbientSound() {
      return Set.of(SnifferEntity.State.DIGGING, SnifferEntity.State.SEARCHING).contains(this.getState()) ? null : SoundEvents.ENTITY_SNIFFER_IDLE;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_SNIFFER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SNIFFER_DEATH;
   }

   public int getMaxHeadRotation() {
      return 50;
   }

   public void setBaby(boolean baby) {
      this.setBreedingAge(baby ? -48000 : 0);
   }

   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return (PassiveEntity)EntityType.SNIFFER.create(world, SpawnReason.BREEDING);
   }

   public boolean canBreedWith(AnimalEntity other) {
      if (!(other instanceof SnifferEntity snifferEntity)) {
         return false;
      } else {
         Set set = Set.of(SnifferEntity.State.IDLING, SnifferEntity.State.SCENTING, SnifferEntity.State.FEELING_HAPPY);
         return set.contains(this.getState()) && set.contains(snifferEntity.getState()) && super.canBreedWith(other);
      }
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.SNIFFER_FOOD);
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return SnifferBrain.create(this.createBrainProfile().deserialize(dynamic));
   }

   public Brain getBrain() {
      return super.getBrain();
   }

   protected Brain.Profile createBrainProfile() {
      return Brain.createProfile(SnifferBrain.MEMORY_MODULES, SnifferBrain.SENSORS);
   }

   protected void mobTick(ServerWorld world) {
      Profiler profiler = Profilers.get();
      profiler.push("snifferBrain");
      this.getBrain().tick(world, this);
      profiler.swap("snifferActivityUpdate");
      SnifferBrain.updateActivities(this);
      profiler.pop();
      super.mobTick(world);
   }

   protected void sendAiDebugData() {
      super.sendAiDebugData();
      DebugInfoSender.sendBrainDebugData(this);
   }

   static {
      DIMENSIONS = EntityDimensions.changing(EntityType.SNIFFER.getWidth(), EntityType.SNIFFER.getHeight() - 0.4F).withEyeHeight(0.81F);
      STATE = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.SNIFFER_STATE);
      FINISH_DIG_TIME = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.INTEGER);
   }

   public static enum State {
      IDLING(0),
      FEELING_HAPPY(1),
      SCENTING(2),
      SNIFFING(3),
      SEARCHING(4),
      DIGGING(5),
      RISING(6);

      public static final IntFunction INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(State::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, State::getIndex);
      private final int index;

      private State(final int index) {
         this.index = index;
      }

      public int getIndex() {
         return this.index;
      }

      // $FF: synthetic method
      private static State[] method_49151() {
         return new State[]{IDLING, FEELING_HAPPY, SCENTING, SNIFFING, SEARCHING, DIGGING, RISING};
      }
   }
}

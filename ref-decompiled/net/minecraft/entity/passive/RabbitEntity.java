package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.PowderSnowJumpGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class RabbitEntity extends AnimalEntity {
   public static final double field_30356 = 0.6;
   public static final double field_30357 = 0.8;
   public static final double field_30358 = 1.0;
   public static final double ESCAPE_DANGER_SPEED = 2.2;
   public static final double MELEE_ATTACK_SPEED = 1.4;
   private static final TrackedData VARIANT;
   private static final int DEFAULT_MORE_CARROT_TICKS = 0;
   private static final Identifier KILLER_BUNNY;
   private static final int field_51585 = 3;
   private static final int field_51586 = 5;
   private static final Identifier KILLER_BUNNY_ATTACK_DAMAGE_MODIFIER_ID;
   private static final int field_30369 = 8;
   private static final int field_30370 = 40;
   private int jumpTicks;
   private int jumpDuration;
   private boolean lastOnGround;
   private int ticksUntilJump;
   int moreCarrotTicks = 0;

   public RabbitEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.jumpControl = new RabbitJumpControl(this);
      this.moveControl = new RabbitMoveControl(this);
      this.setSpeed(0.0);
   }

   protected void initGoals() {
      this.goalSelector.add(1, new SwimGoal(this));
      this.goalSelector.add(1, new PowderSnowJumpGoal(this, this.getWorld()));
      this.goalSelector.add(1, new EscapeDangerGoal(this, 2.2));
      this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
      this.goalSelector.add(3, new TemptGoal(this, 1.0, (stack) -> {
         return stack.isIn(ItemTags.RABBIT_FOOD);
      }, false));
      this.goalSelector.add(4, new FleeGoal(this, PlayerEntity.class, 8.0F, 2.2, 2.2));
      this.goalSelector.add(4, new FleeGoal(this, WolfEntity.class, 10.0F, 2.2, 2.2));
      this.goalSelector.add(4, new FleeGoal(this, HostileEntity.class, 4.0F, 2.2, 2.2));
      this.goalSelector.add(5, new EatCarrotCropGoal(this));
      this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
      this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
   }

   protected float getJumpVelocity() {
      float f = 0.3F;
      if (this.moveControl.getSpeed() <= 0.6) {
         f = 0.2F;
      }

      Path path = this.navigation.getCurrentPath();
      if (path != null && !path.isFinished()) {
         Vec3d vec3d = path.getNodePosition(this);
         if (vec3d.y > this.getY() + 0.5) {
            f = 0.5F;
         }
      }

      if (this.horizontalCollision || this.jumping && this.moveControl.getTargetY() > this.getY() + 0.5) {
         f = 0.5F;
      }

      return super.getJumpVelocity(f / 0.42F);
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

      if (!this.getWorld().isClient) {
         this.getWorld().sendEntityStatus(this, (byte)1);
      }

   }

   public float getJumpProgress(float tickProgress) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + tickProgress) / (float)this.jumpDuration;
   }

   public void setSpeed(double speed) {
      this.getNavigation().setSpeed(speed);
      this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
   }

   public void setJumping(boolean jumping) {
      super.setJumping(jumping);
      if (jumping) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJump() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, RabbitEntity.Variant.DEFAULT.index);
   }

   public void mobTick(ServerWorld world) {
      if (this.ticksUntilJump > 0) {
         --this.ticksUntilJump;
      }

      if (this.moreCarrotTicks > 0) {
         this.moreCarrotTicks -= this.random.nextInt(3);
         if (this.moreCarrotTicks < 0) {
            this.moreCarrotTicks = 0;
         }
      }

      if (this.isOnGround()) {
         if (!this.lastOnGround) {
            this.setJumping(false);
            this.scheduleJump();
         }

         if (this.getVariant() == RabbitEntity.Variant.EVIL && this.ticksUntilJump == 0) {
            LivingEntity livingEntity = this.getTarget();
            if (livingEntity != null && this.squaredDistanceTo(livingEntity) < 16.0) {
               this.lookTowards(livingEntity.getX(), livingEntity.getZ());
               this.moveControl.moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), this.moveControl.getSpeed());
               this.startJump();
               this.lastOnGround = true;
            }
         }

         RabbitJumpControl rabbitJumpControl = (RabbitJumpControl)this.jumpControl;
         if (!rabbitJumpControl.isActive()) {
            if (this.moveControl.isMoving() && this.ticksUntilJump == 0) {
               Path path = this.navigation.getCurrentPath();
               Vec3d vec3d = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
               if (path != null && !path.isFinished()) {
                  vec3d = path.getNodePosition(this);
               }

               this.lookTowards(vec3d.x, vec3d.z);
               this.startJump();
            }
         } else if (!rabbitJumpControl.canJump()) {
            this.enableJump();
         }
      }

      this.lastOnGround = this.isOnGround();
   }

   public boolean shouldSpawnSprintingParticles() {
      return false;
   }

   private void lookTowards(double x, double z) {
      this.setYaw((float)(MathHelper.atan2(z - this.getZ(), x - this.getX()) * 57.2957763671875) - 90.0F);
   }

   private void enableJump() {
      ((RabbitJumpControl)this.jumpControl).setCanJump(true);
   }

   private void disableJump() {
      ((RabbitJumpControl)this.jumpControl).setCanJump(false);
   }

   private void doScheduleJump() {
      if (this.moveControl.getSpeed() < 2.2) {
         this.ticksUntilJump = 10;
      } else {
         this.ticksUntilJump = 1;
      }

   }

   private void scheduleJump() {
      this.doScheduleJump();
      this.disableJump();
   }

   public void tickMovement() {
      super.tickMovement();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   public static DefaultAttributeContainer.Builder createRabbitAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 3.0).add(EntityAttributes.MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.ATTACK_DAMAGE, 3.0);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("RabbitType", RabbitEntity.Variant.INDEX_CODEC, this.getVariant());
      view.putInt("MoreCarrotTicks", this.moreCarrotTicks);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setVariant((Variant)view.read("RabbitType", RabbitEntity.Variant.INDEX_CODEC).orElse(RabbitEntity.Variant.DEFAULT));
      this.moreCarrotTicks = view.getInt("MoreCarrotTicks", 0);
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_RABBIT_DEATH;
   }

   public void playAttackSound() {
      if (this.getVariant() == RabbitEntity.Variant.EVIL) {
         this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }

   }

   public SoundCategory getSoundCategory() {
      return this.getVariant() == RabbitEntity.Variant.EVIL ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
   }

   @Nullable
   public RabbitEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      RabbitEntity rabbitEntity = (RabbitEntity)EntityType.RABBIT.create(serverWorld, SpawnReason.BREEDING);
      if (rabbitEntity != null) {
         Variant variant = getVariantFromPos(serverWorld, this.getBlockPos());
         if (this.random.nextInt(20) != 0) {
            label22: {
               if (passiveEntity instanceof RabbitEntity) {
                  RabbitEntity rabbitEntity2 = (RabbitEntity)passiveEntity;
                  if (this.random.nextBoolean()) {
                     variant = rabbitEntity2.getVariant();
                     break label22;
                  }
               }

               variant = this.getVariant();
            }
         }

         rabbitEntity.setVariant(variant);
      }

      return rabbitEntity;
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.RABBIT_FOOD);
   }

   public Variant getVariant() {
      return RabbitEntity.Variant.byIndex((Integer)this.dataTracker.get(VARIANT));
   }

   private void setVariant(Variant variant) {
      if (variant == RabbitEntity.Variant.EVIL) {
         this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(8.0);
         this.goalSelector.add(4, new MeleeAttackGoal(this, 1.4, true));
         this.targetSelector.add(1, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
         this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
         this.targetSelector.add(2, new ActiveTargetGoal(this, WolfEntity.class, true));
         this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).updateModifier(new EntityAttributeModifier(KILLER_BUNNY_ATTACK_DAMAGE_MODIFIER_ID, 5.0, EntityAttributeModifier.Operation.ADD_VALUE));
         if (!this.hasCustomName()) {
            this.setCustomName(Text.translatable(Util.createTranslationKey("entity", KILLER_BUNNY)));
         }
      } else {
         this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).removeModifier(KILLER_BUNNY_ATTACK_DAMAGE_MODIFIER_ID);
      }

      this.dataTracker.set(VARIANT, variant.index);
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.RABBIT_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.RABBIT_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.RABBIT_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponentTypes.RABBIT_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Variant variant = getVariantFromPos(world, this.getBlockPos());
      if (entityData instanceof RabbitData) {
         variant = ((RabbitData)entityData).variant;
      } else {
         entityData = new RabbitData(variant);
      }

      this.setVariant(variant);
      return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
   }

   private static Variant getVariantFromPos(WorldAccess world, BlockPos pos) {
      RegistryEntry registryEntry = world.getBiome(pos);
      int i = world.getRandom().nextInt(100);
      if (registryEntry.isIn(BiomeTags.SPAWNS_WHITE_RABBITS)) {
         return i < 80 ? RabbitEntity.Variant.WHITE : RabbitEntity.Variant.WHITE_SPLOTCHED;
      } else if (registryEntry.isIn(BiomeTags.SPAWNS_GOLD_RABBITS)) {
         return RabbitEntity.Variant.GOLD;
      } else {
         return i < 50 ? RabbitEntity.Variant.BROWN : (i < 90 ? RabbitEntity.Variant.SALT : RabbitEntity.Variant.BLACK);
      }
   }

   public static boolean canSpawn(EntityType entity, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      return world.getBlockState(pos.down()).isIn(BlockTags.RABBITS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
   }

   boolean wantsCarrots() {
      return this.moreCarrotTicks <= 0;
   }

   public void handleStatus(byte status) {
      if (status == 1) {
         this.spawnSprintingParticles();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleStatus(status);
      }

   }

   public Vec3d getLeashOffset() {
      return new Vec3d(0.0, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      VARIANT = DataTracker.registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
      KILLER_BUNNY = Identifier.ofVanilla("killer_bunny");
      KILLER_BUNNY_ATTACK_DAMAGE_MODIFIER_ID = Identifier.ofVanilla("evil");
   }

   public static class RabbitJumpControl extends JumpControl {
      private final RabbitEntity rabbit;
      private boolean canJump;

      public RabbitJumpControl(RabbitEntity rabbit) {
         super(rabbit);
         this.rabbit = rabbit;
      }

      public boolean isActive() {
         return this.active;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean canJump) {
         this.canJump = canJump;
      }

      public void tick() {
         if (this.active) {
            this.rabbit.startJump();
            this.active = false;
         }

      }
   }

   private static class RabbitMoveControl extends MoveControl {
      private final RabbitEntity rabbit;
      private double rabbitSpeed;

      public RabbitMoveControl(RabbitEntity owner) {
         super(owner);
         this.rabbit = owner;
      }

      public void tick() {
         if (this.rabbit.isOnGround() && !this.rabbit.jumping && !((RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
            this.rabbit.setSpeed(0.0);
         } else if (this.isMoving() || this.state == MoveControl.State.JUMPING) {
            this.rabbit.setSpeed(this.rabbitSpeed);
         }

         super.tick();
      }

      public void moveTo(double x, double y, double z, double speed) {
         if (this.rabbit.isTouchingWater()) {
            speed = 1.5;
         }

         super.moveTo(x, y, z, speed);
         if (speed > 0.0) {
            this.rabbitSpeed = speed;
         }

      }
   }

   private static class EscapeDangerGoal extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
      private final RabbitEntity rabbit;

      public EscapeDangerGoal(RabbitEntity rabbit, double speed) {
         super(rabbit, speed);
         this.rabbit = rabbit;
      }

      public void tick() {
         super.tick();
         this.rabbit.setSpeed(this.speed);
      }
   }

   private static class FleeGoal extends FleeEntityGoal {
      private final RabbitEntity rabbit;

      public FleeGoal(RabbitEntity rabbit, Class fleeFromType, float distance, double slowSpeed, double fastSpeed) {
         super(rabbit, fleeFromType, distance, slowSpeed, fastSpeed);
         this.rabbit = rabbit;
      }

      public boolean canStart() {
         return this.rabbit.getVariant() != RabbitEntity.Variant.EVIL && super.canStart();
      }
   }

   private static class EatCarrotCropGoal extends MoveToTargetPosGoal {
      private final RabbitEntity rabbit;
      private boolean wantsCarrots;
      private boolean hasTarget;

      public EatCarrotCropGoal(RabbitEntity rabbit) {
         super(rabbit, 0.699999988079071, 16);
         this.rabbit = rabbit;
      }

      public boolean canStart() {
         if (this.cooldown <= 0) {
            if (!getServerWorld(this.rabbit).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
               return false;
            }

            this.hasTarget = false;
            this.wantsCarrots = this.rabbit.wantsCarrots();
         }

         return super.canStart();
      }

      public boolean shouldContinue() {
         return this.hasTarget && super.shouldContinue();
      }

      public void tick() {
         super.tick();
         this.rabbit.getLookControl().lookAt((double)this.targetPos.getX() + 0.5, (double)(this.targetPos.getY() + 1), (double)this.targetPos.getZ() + 0.5, 10.0F, (float)this.rabbit.getMaxLookPitchChange());
         if (this.hasReached()) {
            World world = this.rabbit.getWorld();
            BlockPos blockPos = this.targetPos.up();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (this.hasTarget && block instanceof CarrotsBlock) {
               int i = (Integer)blockState.get(CarrotsBlock.AGE);
               if (i == 0) {
                  world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
                  world.breakBlock(blockPos, true, this.rabbit);
               } else {
                  world.setBlockState(blockPos, (BlockState)blockState.with(CarrotsBlock.AGE, i - 1), 2);
                  world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of((Entity)this.rabbit));
                  world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
               }

               this.rabbit.moreCarrotTicks = 40;
            }

            this.hasTarget = false;
            this.cooldown = 10;
         }

      }

      protected boolean isTargetPos(WorldView world, BlockPos pos) {
         BlockState blockState = world.getBlockState(pos);
         if (blockState.isOf(Blocks.FARMLAND) && this.wantsCarrots && !this.hasTarget) {
            blockState = world.getBlockState(pos.up());
            if (blockState.getBlock() instanceof CarrotsBlock && ((CarrotsBlock)blockState.getBlock()).isMature(blockState)) {
               this.hasTarget = true;
               return true;
            }
         }

         return false;
      }
   }

   public static enum Variant implements StringIdentifiable {
      BROWN(0, "brown"),
      WHITE(1, "white"),
      BLACK(2, "black"),
      WHITE_SPLOTCHED(3, "white_splotched"),
      GOLD(4, "gold"),
      SALT(5, "salt"),
      EVIL(99, "evil");

      public static final Variant DEFAULT = BROWN;
      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(Variant::getIndex, values(), (Object)DEFAULT);
      public static final Codec CODEC = StringIdentifiable.createCodec(Variant::values);
      /** @deprecated */
      @Deprecated
      public static final Codec INDEX_CODEC;
      public static final PacketCodec PACKET_CODEC;
      final int index;
      private final String id;

      private Variant(final int index, final String id) {
         this.index = index;
         this.id = id;
      }

      public String asString() {
         return this.id;
      }

      public int getIndex() {
         return this.index;
      }

      public static Variant byIndex(int index) {
         return (Variant)INDEX_MAPPER.apply(index);
      }

      // $FF: synthetic method
      private static Variant[] method_47859() {
         return new Variant[]{BROWN, WHITE, BLACK, WHITE_SPLOTCHED, GOLD, SALT, EVIL};
      }

      static {
         PrimitiveCodec var10000 = Codec.INT;
         IntFunction var10001 = INDEX_MAPPER;
         Objects.requireNonNull(var10001);
         INDEX_CODEC = var10000.xmap(var10001::apply, Variant::getIndex);
         PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, Variant::getIndex);
      }
   }

   public static class RabbitData extends PassiveEntity.PassiveData {
      public final Variant variant;

      public RabbitData(Variant variant) {
         super(1.0F);
         this.variant = variant;
      }
   }
}

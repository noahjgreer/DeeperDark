package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.StepAndDestroyBlockGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ZombieEntity extends HostileEntity {
   private static final Identifier BABY_SPEED_MODIFIER_ID = Identifier.ofVanilla("baby");
   private static final EntityAttributeModifier BABY_SPEED_BONUS;
   private static final Identifier REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID;
   private static final EntityAttributeModifier REINFORCEMENT_CALLEE_CHARGE_REINFORCEMENT_BONUS;
   private static final Identifier LEADER_ZOMBIE_BONUS_MODIFIER_ID;
   private static final Identifier ZOMBIE_RANDOM_SPAWN_BONUS_MODIFIER_ID;
   private static final TrackedData BABY;
   private static final TrackedData ZOMBIE_TYPE;
   private static final TrackedData CONVERTING_IN_WATER;
   public static final float field_30519 = 0.05F;
   public static final int field_30515 = 50;
   public static final int field_30516 = 40;
   public static final int field_30517 = 7;
   private static final int field_57696 = -1;
   private static final EntityDimensions BABY_BASE_DIMENSIONS;
   private static final float field_30518 = 0.1F;
   private static final Predicate DOOR_BREAK_DIFFICULTY_CHECKER;
   private static final boolean DEFAULT_IS_BABY = false;
   private static final boolean DEFAULT_CAN_BREAK_DOORS = false;
   private static final int DEFAULT_IN_WATER_TIME = 0;
   private final BreakDoorGoal breakDoorsGoal;
   private boolean canBreakDoors;
   private int inWaterTime;
   private int ticksUntilWaterConversion;

   public ZombieEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.breakDoorsGoal = new BreakDoorGoal(this, DOOR_BREAK_DIFFICULTY_CHECKER);
      this.canBreakDoors = false;
      this.inWaterTime = 0;
   }

   public ZombieEntity(World world) {
      this(EntityType.ZOMBIE, world);
   }

   protected void initGoals() {
      this.goalSelector.add(4, new DestroyEggGoal(this, 1.0, 3));
      this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(8, new LookAroundGoal(this));
      this.initCustomGoals();
   }

   protected void initCustomGoals() {
      this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
      this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
      this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
      this.targetSelector.add(1, (new RevengeGoal(this, new Class[0])).setGroupRevenge(ZombifiedPiglinEntity.class));
      this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
      this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
      this.targetSelector.add(5, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
   }

   public static DefaultAttributeContainer.Builder createZombieAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.FOLLOW_RANGE, 35.0).add(EntityAttributes.MOVEMENT_SPEED, 0.23000000417232513).add(EntityAttributes.ATTACK_DAMAGE, 3.0).add(EntityAttributes.ARMOR, 2.0).add(EntityAttributes.SPAWN_REINFORCEMENTS);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(BABY, false);
      builder.add(ZOMBIE_TYPE, 0);
      builder.add(CONVERTING_IN_WATER, false);
   }

   public boolean isConvertingInWater() {
      return (Boolean)this.getDataTracker().get(CONVERTING_IN_WATER);
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean canBreakDoors) {
      if (this.navigation.canControlOpeningDoors()) {
         if (this.canBreakDoors != canBreakDoors) {
            this.canBreakDoors = canBreakDoors;
            this.navigation.setCanOpenDoors(canBreakDoors);
            if (canBreakDoors) {
               this.goalSelector.add(1, this.breakDoorsGoal);
            } else {
               this.goalSelector.remove(this.breakDoorsGoal);
            }
         }
      } else if (this.canBreakDoors) {
         this.goalSelector.remove(this.breakDoorsGoal);
         this.canBreakDoors = false;
      }

   }

   public boolean isBaby() {
      return (Boolean)this.getDataTracker().get(BABY);
   }

   protected int getExperienceToDrop(ServerWorld world) {
      if (this.isBaby()) {
         this.experiencePoints = (int)((double)this.experiencePoints * 2.5);
      }

      return super.getExperienceToDrop(world);
   }

   public void setBaby(boolean baby) {
      this.getDataTracker().set(BABY, baby);
      if (this.getWorld() != null && !this.getWorld().isClient) {
         EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
         entityAttributeInstance.removeModifier(BABY_SPEED_MODIFIER_ID);
         if (baby) {
            entityAttributeInstance.addTemporaryModifier(BABY_SPEED_BONUS);
         }
      }

   }

   public void onTrackedDataSet(TrackedData data) {
      if (BABY.equals(data)) {
         this.calculateDimensions();
      }

      super.onTrackedDataSet(data);
   }

   protected boolean canConvertInWater() {
      return true;
   }

   public void tick() {
      if (!this.getWorld().isClient && this.isAlive() && !this.isAiDisabled()) {
         if (this.isConvertingInWater()) {
            --this.ticksUntilWaterConversion;
            if (this.ticksUntilWaterConversion < 0) {
               this.convertInWater();
            }
         } else if (this.canConvertInWater()) {
            if (this.isSubmergedIn(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.setTicksUntilWaterConversion(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   public void tickMovement() {
      if (this.isAlive()) {
         boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight();
         if (bl) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
            if (!itemStack.isEmpty()) {
               if (itemStack.isDamageable()) {
                  Item item = itemStack.getItem();
                  itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                  if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                     this.sendEquipmentBreakStatus(item, EquipmentSlot.HEAD);
                     this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  }
               }

               bl = false;
            }

            if (bl) {
               this.setOnFireFor(8.0F);
            }
         }
      }

      super.tickMovement();
   }

   private void setTicksUntilWaterConversion(int ticksUntilWaterConversion) {
      this.ticksUntilWaterConversion = ticksUntilWaterConversion;
      this.getDataTracker().set(CONVERTING_IN_WATER, true);
   }

   protected void convertInWater() {
      this.convertTo(EntityType.DROWNED);
      if (!this.isSilent()) {
         this.getWorld().syncWorldEvent((Entity)null, 1040, this.getBlockPos(), 0);
      }

   }

   protected void convertTo(EntityType entityType) {
      this.convertTo(entityType, EntityConversionContext.create(this, true, true), (newZombie) -> {
         newZombie.applyAttributeModifiers(newZombie.getWorld().getLocalDifficulty(newZombie.getBlockPos()).getClampedLocalDifficulty());
      });
   }

   @VisibleForTesting
   public boolean infectVillager(ServerWorld world, VillagerEntity villager) {
      ZombieVillagerEntity zombieVillagerEntity = (ZombieVillagerEntity)villager.convertTo(EntityType.ZOMBIE_VILLAGER, EntityConversionContext.create(villager, true, true), (zombieVillager) -> {
         zombieVillager.initialize(world, world.getLocalDifficulty(zombieVillager.getBlockPos()), SpawnReason.CONVERSION, new ZombieData(false, true));
         zombieVillager.setVillagerData(villager.getVillagerData());
         zombieVillager.setGossip(villager.getGossip().copy());
         zombieVillager.setOfferData(villager.getOffers().copy());
         zombieVillager.setExperience(villager.getExperience());
         if (!this.isSilent()) {
            world.syncWorldEvent((Entity)null, 1026, this.getBlockPos(), 0);
         }

      });
      return zombieVillagerEntity != null;
   }

   protected boolean burnsInDaylight() {
      return true;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (!super.damage(world, source, amount)) {
         return false;
      } else {
         LivingEntity livingEntity = this.getTarget();
         if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
            livingEntity = (LivingEntity)source.getAttacker();
         }

         if (livingEntity != null && world.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeValue(EntityAttributes.SPAWN_REINFORCEMENTS) && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());
            EntityType entityType = this.getType();
            ZombieEntity zombieEntity = (ZombieEntity)entityType.create(world, SpawnReason.REINFORCEMENT);
            if (zombieEntity == null) {
               return true;
            }

            for(int l = 0; l < 50; ++l) {
               int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               BlockPos blockPos = new BlockPos(m, n, o);
               if (SpawnRestriction.isSpawnPosAllowed(entityType, world, blockPos) && SpawnRestriction.canSpawn(entityType, world, SpawnReason.REINFORCEMENT, blockPos, world.random)) {
                  zombieEntity.setPosition((double)m, (double)n, (double)o);
                  if (!world.isPlayerInRange((double)m, (double)n, (double)o, 7.0) && world.doesNotIntersectEntities(zombieEntity) && world.isSpaceEmpty(zombieEntity) && (zombieEntity.canSpawnAsReinforcementInFluid() || !world.containsFluid(zombieEntity.getBoundingBox()))) {
                     zombieEntity.setTarget(livingEntity);
                     zombieEntity.initialize(world, world.getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.REINFORCEMENT, (EntityData)null);
                     world.spawnEntityAndPassengers(zombieEntity);
                     EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS);
                     EntityAttributeModifier entityAttributeModifier = entityAttributeInstance.getModifier(REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID);
                     double d = entityAttributeModifier != null ? entityAttributeModifier.value() : 0.0;
                     entityAttributeInstance.removeModifier(REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID);
                     entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID, d - 0.05, EntityAttributeModifier.Operation.ADD_VALUE));
                     zombieEntity.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).addPersistentModifier(REINFORCEMENT_CALLEE_CHARGE_REINFORCEMENT_BONUS);
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      boolean bl = super.tryAttack(world, target);
      if (bl) {
         float f = this.getWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
         if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
            target.setOnFireFor((float)(2 * (int)f));
         }
      }

      return bl;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public EntityType getType() {
      return super.getType();
   }

   protected boolean canSpawnAsReinforcementInFluid() {
      return false;
   }

   protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
      super.initEquipment(random, localDifficulty);
      if (random.nextFloat() < (this.getWorld().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int i = random.nextInt(3);
         if (i == 0) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("IsBaby", this.isBaby());
      view.putBoolean("CanBreakDoors", this.canBreakDoors());
      view.putInt("InWaterTime", this.isTouchingWater() ? this.inWaterTime : -1);
      view.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setBaby(view.getBoolean("IsBaby", false));
      this.setCanBreakDoors(view.getBoolean("CanBreakDoors", false));
      this.inWaterTime = view.getInt("InWaterTime", 0);
      int i = view.getInt("DrownedConversionTime", -1);
      if (i != -1) {
         this.setTicksUntilWaterConversion(i);
      } else {
         this.getDataTracker().set(CONVERTING_IN_WATER, false);
      }

   }

   public boolean onKilledOther(ServerWorld world, LivingEntity other) {
      boolean bl = super.onKilledOther(world, other);
      if ((world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD) && other instanceof VillagerEntity villagerEntity) {
         if (world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return bl;
         }

         if (this.infectVillager(world, villagerEntity)) {
            bl = false;
         }
      }

      return bl;
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   public boolean canPickupItem(ItemStack stack) {
      return stack.isIn(ItemTags.EGGS) && this.isBaby() && this.hasVehicle() ? false : super.canPickupItem(stack);
   }

   public boolean canGather(ServerWorld world, ItemStack stack) {
      return stack.isOf(Items.GLOW_INK_SAC) ? false : super.canGather(world, stack);
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Random random = world.getRandom();
      EntityData entityData = super.initialize(world, difficulty, spawnReason, entityData);
      float f = difficulty.getClampedLocalDifficulty();
      if (spawnReason != SpawnReason.CONVERSION) {
         this.setCanPickUpLoot(random.nextFloat() < 0.55F * f);
      }

      if (entityData == null) {
         entityData = new ZombieData(shouldBeBaby(random), true);
      }

      if (entityData instanceof ZombieData zombieData) {
         if (zombieData.baby) {
            this.setBaby(true);
            if (zombieData.tryChickenJockey) {
               if ((double)random.nextFloat() < 0.05) {
                  List list = world.getEntitiesByClass(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
                  if (!list.isEmpty()) {
                     ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
                     chickenEntity.setHasJockey(true);
                     this.startRiding(chickenEntity);
                  }
               } else if ((double)random.nextFloat() < 0.05) {
                  ChickenEntity chickenEntity2 = (ChickenEntity)EntityType.CHICKEN.create(this.getWorld(), SpawnReason.JOCKEY);
                  if (chickenEntity2 != null) {
                     chickenEntity2.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                     chickenEntity2.initialize(world, difficulty, SpawnReason.JOCKEY, (EntityData)null);
                     chickenEntity2.setHasJockey(true);
                     this.startRiding(chickenEntity2);
                     world.spawnEntity(chickenEntity2);
                  }
               }
            }
         }

         this.setCanBreakDoors(random.nextFloat() < f * 0.1F);
         if (spawnReason != SpawnReason.CONVERSION) {
            this.initEquipment(random, difficulty);
            this.updateEnchantments(world, random, difficulty);
         }
      }

      if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
         LocalDate localDate = LocalDate.now();
         int i = localDate.get(ChronoField.DAY_OF_MONTH);
         int j = localDate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && random.nextFloat() < 0.25F) {
            this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0F);
         }
      }

      this.applyAttributeModifiers(f);
      return (EntityData)entityData;
   }

   @VisibleForTesting
   public void setInWaterTime(int inWaterTime) {
      this.inWaterTime = inWaterTime;
   }

   @VisibleForTesting
   public void setTicksUntilWaterConversionDirect(int ticksUntilWaterConversion) {
      this.ticksUntilWaterConversion = ticksUntilWaterConversion;
   }

   public static boolean shouldBeBaby(Random random) {
      return random.nextFloat() < 0.05F;
   }

   protected void applyAttributeModifiers(float chanceMultiplier) {
      this.initAttributes();
      this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).overwritePersistentModifier(new EntityAttributeModifier(RANDOM_SPAWN_BONUS_MODIFIER_ID, this.random.nextDouble() * 0.05000000074505806, EntityAttributeModifier.Operation.ADD_VALUE));
      double d = this.random.nextDouble() * 1.5 * (double)chanceMultiplier;
      if (d > 1.0) {
         this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).overwritePersistentModifier(new EntityAttributeModifier(ZOMBIE_RANDOM_SPAWN_BONUS_MODIFIER_ID, d, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      }

      if (this.random.nextFloat() < chanceMultiplier * 0.05F) {
         this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).overwritePersistentModifier(new EntityAttributeModifier(LEADER_ZOMBIE_BONUS_MODIFIER_ID, this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.ADD_VALUE));
         this.getAttributeInstance(EntityAttributes.MAX_HEALTH).overwritePersistentModifier(new EntityAttributeModifier(LEADER_ZOMBIE_BONUS_MODIFIER_ID, this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
         this.setCanBreakDoors(true);
      }

   }

   protected void initAttributes() {
      this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * 0.10000000149011612);
   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
      super.dropEquipment(world, source, causedByPlayer);
      Entity entity = source.getAttacker();
      if (entity instanceof CreeperEntity creeperEntity) {
         if (creeperEntity.shouldDropHead()) {
            ItemStack itemStack = this.getSkull();
            if (!itemStack.isEmpty()) {
               creeperEntity.onHeadDropped();
               this.dropStack(world, itemStack);
            }
         }
      }

   }

   protected ItemStack getSkull() {
      return new ItemStack(Items.ZOMBIE_HEAD);
   }

   static {
      BABY_SPEED_BONUS = new EntityAttributeModifier(BABY_SPEED_MODIFIER_ID, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
      REINFORCEMENT_CALLER_CHARGE_MODIFIER_ID = Identifier.ofVanilla("reinforcement_caller_charge");
      REINFORCEMENT_CALLEE_CHARGE_REINFORCEMENT_BONUS = new EntityAttributeModifier(Identifier.ofVanilla("reinforcement_callee_charge"), -0.05000000074505806, EntityAttributeModifier.Operation.ADD_VALUE);
      LEADER_ZOMBIE_BONUS_MODIFIER_ID = Identifier.ofVanilla("leader_zombie_bonus");
      ZOMBIE_RANDOM_SPAWN_BONUS_MODIFIER_ID = Identifier.ofVanilla("zombie_random_spawn_bonus");
      BABY = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      ZOMBIE_TYPE = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
      CONVERTING_IN_WATER = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      BABY_BASE_DIMENSIONS = EntityType.ZOMBIE.getDimensions().scaled(0.5F).withEyeHeight(0.93F);
      DOOR_BREAK_DIFFICULTY_CHECKER = (difficulty) -> {
         return difficulty == Difficulty.HARD;
      };
   }

   private class DestroyEggGoal extends StepAndDestroyBlockGoal {
      DestroyEggGoal(final PathAwareEntity mob, final double speed, final int maxYDifference) {
         super(Blocks.TURTLE_EGG, mob, speed, maxYDifference);
      }

      public void tickStepping(WorldAccess world, BlockPos pos) {
         world.playSound((Entity)null, pos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieEntity.this.random.nextFloat() * 0.2F);
      }

      public void onDestroyBlock(World world, BlockPos pos) {
         world.playSound((Entity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
      }

      public double getDesiredDistanceToTarget() {
         return 1.14;
      }
   }

   public static class ZombieData implements EntityData {
      public final boolean baby;
      public final boolean tryChickenJockey;

      public ZombieData(boolean baby, boolean tryChickenJockey) {
         this.baby = baby;
         this.tryChickenJockey = tryChickenJockey;
      }
   }
}

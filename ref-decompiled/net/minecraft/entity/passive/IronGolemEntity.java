package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.IronGolemLookGoal;
import net.minecraft.entity.ai.goal.IronGolemWanderAroundGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.entity.ai.goal.WanderNearTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class IronGolemEntity extends GolemEntity implements Angerable {
   protected static final TrackedData IRON_GOLEM_FLAGS;
   private static final int HEALTH_PER_INGOT = 25;
   private static final boolean DEFAULT_PLAYER_CREATED = false;
   private int attackTicksLeft;
   private int lookingAtVillagerTicksLeft;
   private static final UniformIntProvider ANGER_TIME_RANGE;
   private int angerTime;
   @Nullable
   private UUID angryAt;

   public IronGolemEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initGoals() {
      this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0F));
      this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6, false));
      this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
      this.goalSelector.add(5, new IronGolemLookGoal(this));
      this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.add(8, new LookAroundGoal(this));
      this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
      this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
      this.targetSelector.add(3, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
      this.targetSelector.add(3, new ActiveTargetGoal(this, MobEntity.class, 5, false, false, (entity, world) -> {
         return entity instanceof Monster && !(entity instanceof CreeperEntity);
      }));
      this.targetSelector.add(4, new UniversalAngerGoal(this, false));
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(IRON_GOLEM_FLAGS, (byte)0);
   }

   public static DefaultAttributeContainer.Builder createIronGolemAttributes() {
      return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 100.0).add(EntityAttributes.MOVEMENT_SPEED, 0.25).add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.ATTACK_DAMAGE, 15.0).add(EntityAttributes.STEP_HEIGHT, 1.0);
   }

   protected int getNextAirUnderwater(int air) {
      return air;
   }

   protected void pushAway(Entity entity) {
      if (entity instanceof Monster && !(entity instanceof CreeperEntity) && this.getRandom().nextInt(20) == 0) {
         this.setTarget((LivingEntity)entity);
      }

      super.pushAway(entity);
   }

   public void tickMovement() {
      super.tickMovement();
      if (this.attackTicksLeft > 0) {
         --this.attackTicksLeft;
      }

      if (this.lookingAtVillagerTicksLeft > 0) {
         --this.lookingAtVillagerTicksLeft;
      }

      if (!this.getWorld().isClient) {
         this.tickAngerLogic((ServerWorld)this.getWorld(), true);
      }

   }

   public boolean shouldSpawnSprintingParticles() {
      return this.getVelocity().horizontalLengthSquared() > 2.500000277905201E-7 && this.random.nextInt(5) == 0;
   }

   public boolean canTarget(EntityType type) {
      if (this.isPlayerCreated() && type == EntityType.PLAYER) {
         return false;
      } else {
         return type == EntityType.CREEPER ? false : super.canTarget(type);
      }
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("PlayerCreated", this.isPlayerCreated());
      this.writeAngerToData(view);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setPlayerCreated(view.getBoolean("PlayerCreated", false));
      this.readAngerFromData(this.getWorld(), view);
   }

   public void chooseRandomAngerTime() {
      this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
   }

   public void setAngerTime(int angerTime) {
      this.angerTime = angerTime;
   }

   public int getAngerTime() {
      return this.angerTime;
   }

   public void setAngryAt(@Nullable UUID angryAt) {
      this.angryAt = angryAt;
   }

   @Nullable
   public UUID getAngryAt() {
      return this.angryAt;
   }

   private float getAttackDamage() {
      return (float)this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      this.attackTicksLeft = 10;
      world.sendEntityStatus(this, (byte)4);
      float f = this.getAttackDamage();
      float g = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
      DamageSource damageSource = this.getDamageSources().mobAttack(this);
      boolean bl = target.damage(world, damageSource, g);
      if (bl) {
         double var10000;
         if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)target;
            var10000 = livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);
         } else {
            var10000 = 0.0;
         }

         double d = var10000;
         double e = Math.max(0.0, 1.0 - d);
         target.setVelocity(target.getVelocity().add(0.0, 0.4000000059604645 * e, 0.0));
         EnchantmentHelper.onTargetDamaged(world, target, damageSource);
      }

      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return bl;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      Cracks.CrackLevel crackLevel = this.getCrackLevel();
      boolean bl = super.damage(world, source, amount);
      if (bl && this.getCrackLevel() != crackLevel) {
         this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
      }

      return bl;
   }

   public Cracks.CrackLevel getCrackLevel() {
      return Cracks.IRON_GOLEM.getCrackLevel(this.getHealth() / this.getMaxHealth());
   }

   public void handleStatus(byte status) {
      if (status == 4) {
         this.attackTicksLeft = 10;
         this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else if (status == 11) {
         this.lookingAtVillagerTicksLeft = 400;
      } else if (status == 34) {
         this.lookingAtVillagerTicksLeft = 0;
      } else {
         super.handleStatus(status);
      }

   }

   public int getAttackTicksLeft() {
      return this.attackTicksLeft;
   }

   public void setLookingAtVillager(boolean lookingAtVillager) {
      if (lookingAtVillager) {
         this.lookingAtVillagerTicksLeft = 400;
         this.getWorld().sendEntityStatus(this, (byte)11);
      } else {
         this.lookingAtVillagerTicksLeft = 0;
         this.getWorld().sendEntityStatus(this, (byte)34);
      }

   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_IRON_GOLEM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
   }

   protected ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (!itemStack.isOf(Items.IRON_INGOT)) {
         return ActionResult.PASS;
      } else {
         float f = this.getHealth();
         this.heal(25.0F);
         if (this.getHealth() == f) {
            return ActionResult.PASS;
         } else {
            float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, g);
            itemStack.decrementUnlessCreative(1, player);
            return ActionResult.SUCCESS;
         }
      }
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   public int getLookingAtVillagerTicks() {
      return this.lookingAtVillagerTicksLeft;
   }

   public boolean isPlayerCreated() {
      return ((Byte)this.dataTracker.get(IRON_GOLEM_FLAGS) & 1) != 0;
   }

   public void setPlayerCreated(boolean playerCreated) {
      byte b = (Byte)this.dataTracker.get(IRON_GOLEM_FLAGS);
      if (playerCreated) {
         this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b | 1));
      } else {
         this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b & -2));
      }

   }

   public void onDeath(DamageSource damageSource) {
      super.onDeath(damageSource);
   }

   public boolean canSpawn(WorldView world) {
      BlockPos blockPos = this.getBlockPos();
      BlockPos blockPos2 = blockPos.down();
      BlockState blockState = world.getBlockState(blockPos2);
      if (!blockState.hasSolidTopSurface(world, blockPos2, this)) {
         return false;
      } else {
         for(int i = 1; i < 3; ++i) {
            BlockPos blockPos3 = blockPos.up(i);
            BlockState blockState2 = world.getBlockState(blockPos3);
            if (!SpawnHelper.isClearForSpawn(world, blockPos3, blockState2, blockState2.getFluidState(), EntityType.IRON_GOLEM)) {
               return false;
            }
         }

         return SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), Fluids.EMPTY.getDefaultState(), EntityType.IRON_GOLEM) && world.doesNotIntersectEntities(this);
      }
   }

   public Vec3d getLeashOffset() {
      return new Vec3d(0.0, (double)(0.875F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
   }

   static {
      IRON_GOLEM_FLAGS = DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
      ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
   }
}

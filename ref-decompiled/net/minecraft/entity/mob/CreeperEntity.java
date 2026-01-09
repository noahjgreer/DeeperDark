package net.minecraft.entity.mob;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CreeperEntity extends HostileEntity {
   private static final TrackedData FUSE_SPEED;
   private static final TrackedData CHARGED;
   private static final TrackedData IGNITED;
   private static final boolean DEFAULT_CHARGED = false;
   private static final boolean DEFAULT_IGNITED = false;
   private static final short DEFAULT_FUSE = 30;
   private static final byte DEFAULT_EXPLOSION_RADIUS = 3;
   private int lastFuseTime;
   private int currentFuseTime;
   private int fuseTime = 30;
   private int explosionRadius = 3;
   private int headsDropped;

   public CreeperEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initGoals() {
      this.goalSelector.add(1, new SwimGoal(this));
      this.goalSelector.add(2, new CreeperIgniteGoal(this));
      this.goalSelector.add(3, new FleeEntityGoal(this, OcelotEntity.class, 6.0F, 1.0, 1.2));
      this.goalSelector.add(3, new FleeEntityGoal(this, CatEntity.class, 6.0F, 1.0, 1.2));
      this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
      this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(6, new LookAroundGoal(this));
      this.targetSelector.add(1, new ActiveTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
   }

   public static DefaultAttributeContainer.Builder createCreeperAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.25);
   }

   public int getSafeFallDistance() {
      return this.getTarget() == null ? this.getSafeFallDistance(0.0F) : this.getSafeFallDistance(this.getHealth() - 1.0F);
   }

   public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
      boolean bl = super.handleFallDamage(fallDistance, damagePerDistance, damageSource);
      this.currentFuseTime += (int)(fallDistance * 1.5);
      if (this.currentFuseTime > this.fuseTime - 5) {
         this.currentFuseTime = this.fuseTime - 5;
      }

      return bl;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(FUSE_SPEED, -1);
      builder.add(CHARGED, false);
      builder.add(IGNITED, false);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("powered", this.isCharged());
      view.putShort("Fuse", (short)this.fuseTime);
      view.putByte("ExplosionRadius", (byte)this.explosionRadius);
      view.putBoolean("ignited", this.isIgnited());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.dataTracker.set(CHARGED, view.getBoolean("powered", false));
      this.fuseTime = view.getShort("Fuse", (short)30);
      this.explosionRadius = view.getByte("ExplosionRadius", (byte)3);
      if (view.getBoolean("ignited", false)) {
         this.ignite();
      }

   }

   public void tick() {
      if (this.isAlive()) {
         this.lastFuseTime = this.currentFuseTime;
         if (this.isIgnited()) {
            this.setFuseSpeed(1);
         }

         int i = this.getFuseSpeed();
         if (i > 0 && this.currentFuseTime == 0) {
            this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
            this.emitGameEvent(GameEvent.PRIME_FUSE);
         }

         this.currentFuseTime += i;
         if (this.currentFuseTime < 0) {
            this.currentFuseTime = 0;
         }

         if (this.currentFuseTime >= this.fuseTime) {
            this.currentFuseTime = this.fuseTime;
            this.explode();
         }
      }

      super.tick();
   }

   public void setTarget(@Nullable LivingEntity target) {
      if (!(target instanceof GoatEntity)) {
         super.setTarget(target);
      }
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_CREEPER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CREEPER_DEATH;
   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
      super.dropEquipment(world, source, causedByPlayer);
      Entity entity = source.getAttacker();
      if (entity != this && entity instanceof CreeperEntity creeperEntity) {
         if (creeperEntity.shouldDropHead()) {
            creeperEntity.onHeadDropped();
            this.dropItem(world, Items.CREEPER_HEAD);
         }
      }

   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      return true;
   }

   public boolean isCharged() {
      return (Boolean)this.dataTracker.get(CHARGED);
   }

   public float getLerpedFuseTime(float tickProgress) {
      return MathHelper.lerp(tickProgress, (float)this.lastFuseTime, (float)this.currentFuseTime) / (float)(this.fuseTime - 2);
   }

   public int getFuseSpeed() {
      return (Integer)this.dataTracker.get(FUSE_SPEED);
   }

   public void setFuseSpeed(int fuseSpeed) {
      this.dataTracker.set(FUSE_SPEED, fuseSpeed);
   }

   public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
      super.onStruckByLightning(world, lightning);
      this.dataTracker.set(CHARGED, true);
   }

   protected ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isIn(ItemTags.CREEPER_IGNITERS)) {
         SoundEvent soundEvent = itemStack.isOf(Items.FIRE_CHARGE) ? SoundEvents.ITEM_FIRECHARGE_USE : SoundEvents.ITEM_FLINTANDSTEEL_USE;
         this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), (SoundEvent)soundEvent, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
         if (!this.getWorld().isClient) {
            this.ignite();
            if (!itemStack.isDamageable()) {
               itemStack.decrement(1);
            } else {
               itemStack.damage(1, player, (EquipmentSlot)getSlotForHand(hand));
            }
         }

         return ActionResult.SUCCESS;
      } else {
         return super.interactMob(player, hand);
      }
   }

   private void explode() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         float f = this.isCharged() ? 2.0F : 1.0F;
         this.dead = true;
         serverWorld.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, World.ExplosionSourceType.MOB);
         this.spawnEffectsCloud();
         this.onRemoval(serverWorld, Entity.RemovalReason.KILLED);
         this.discard();
      }

   }

   private void spawnEffectsCloud() {
      Collection collection = this.getStatusEffects();
      if (!collection.isEmpty()) {
         AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
         areaEffectCloudEntity.setRadius(2.5F);
         areaEffectCloudEntity.setRadiusOnUse(-0.5F);
         areaEffectCloudEntity.setWaitTime(10);
         areaEffectCloudEntity.setDuration(300);
         areaEffectCloudEntity.setPotionDurationScale(0.25F);
         areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
         Iterator var3 = collection.iterator();

         while(var3.hasNext()) {
            StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var3.next();
            areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
         }

         this.getWorld().spawnEntity(areaEffectCloudEntity);
      }

   }

   public boolean isIgnited() {
      return (Boolean)this.dataTracker.get(IGNITED);
   }

   public void ignite() {
      this.dataTracker.set(IGNITED, true);
   }

   public boolean shouldDropHead() {
      return this.isCharged() && this.headsDropped < 1;
   }

   public void onHeadDropped() {
      ++this.headsDropped;
   }

   static {
      FUSE_SPEED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
      CHARGED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      IGNITED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }
}

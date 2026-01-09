package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class WitherSkullEntity extends ExplosiveProjectileEntity {
   private static final TrackedData CHARGED;
   private static final boolean DEFAULT_DANGEROUS = false;

   public WitherSkullEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public WitherSkullEntity(World world, LivingEntity owner, Vec3d velocity) {
      super(EntityType.WITHER_SKULL, owner, velocity, world);
   }

   protected float getDrag() {
      return this.isCharged() ? 0.73F : super.getDrag();
   }

   public boolean isOnFire() {
      return false;
   }

   public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
      return this.isCharged() && WitherEntity.canDestroy(blockState) ? Math.min(0.8F, max) : max;
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         Entity entity = entityHitResult.getEntity();
         Entity var4 = this.getOwner();
         boolean bl;
         if (var4 instanceof LivingEntity livingEntity) {
            DamageSource damageSource = this.getDamageSources().witherSkull(this, livingEntity);
            bl = entity.damage(serverWorld, damageSource, 8.0F);
            if (bl) {
               if (entity.isAlive()) {
                  EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
               } else {
                  livingEntity.heal(5.0F);
               }
            }
         } else {
            bl = entity.damage(serverWorld, this.getDamageSources().magic(), 5.0F);
         }

         if (bl && entity instanceof LivingEntity livingEntity) {
            int i = 0;
            if (this.getWorld().getDifficulty() == Difficulty.NORMAL) {
               i = 10;
            } else if (this.getWorld().getDifficulty() == Difficulty.HARD) {
               i = 40;
            }

            if (i > 0) {
               livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * i, 1), this.getEffectCause());
            }
         }

      }
   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      if (!this.getWorld().isClient) {
         this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, World.ExplosionSourceType.MOB);
         this.discard();
      }

   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(CHARGED, false);
   }

   public boolean isCharged() {
      return (Boolean)this.dataTracker.get(CHARGED);
   }

   public void setCharged(boolean charged) {
      this.dataTracker.set(CHARGED, charged);
   }

   protected boolean isBurning() {
      return false;
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("dangerous", this.isCharged());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setCharged(view.getBoolean("dangerous", false));
   }

   static {
      CHARGED = DataTracker.registerData(WitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }
}

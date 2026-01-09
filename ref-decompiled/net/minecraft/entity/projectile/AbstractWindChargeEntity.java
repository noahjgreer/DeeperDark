package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWindChargeEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
   public static final ExplosionBehavior EXPLOSION_BEHAVIOR;
   public static final double field_52224 = 0.25;

   public AbstractWindChargeEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.accelerationPower = 0.0;
   }

   public AbstractWindChargeEntity(EntityType type, World world, Entity owner, double x, double y, double z) {
      super(type, x, y, z, world);
      this.setOwner(owner);
      this.accelerationPower = 0.0;
   }

   AbstractWindChargeEntity(EntityType entityType, double d, double e, double f, Vec3d vec3d, World world) {
      super(entityType, d, e, f, vec3d, world);
      this.accelerationPower = 0.0;
   }

   protected Box calculateDefaultBoundingBox(Vec3d pos) {
      float f = this.getType().getDimensions().width() / 2.0F;
      float g = this.getType().getDimensions().height();
      float h = 0.15F;
      return new Box(pos.x - (double)f, pos.y - 0.15000000596046448, pos.z - (double)f, pos.x + (double)f, pos.y - 0.15000000596046448 + (double)g, pos.z + (double)f);
   }

   public boolean collidesWith(Entity other) {
      return other instanceof AbstractWindChargeEntity ? false : super.collidesWith(other);
   }

   protected boolean canHit(Entity entity) {
      if (entity instanceof AbstractWindChargeEntity) {
         return false;
      } else {
         return entity.getType() == EntityType.END_CRYSTAL ? false : super.canHit(entity);
      }
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         Entity var5 = this.getOwner();
         LivingEntity var10000;
         if (var5 instanceof LivingEntity livingEntity) {
            var10000 = livingEntity;
         } else {
            var10000 = null;
         }

         LivingEntity livingEntity2 = var10000;
         Entity entity = entityHitResult.getEntity();
         if (livingEntity2 != null) {
            livingEntity2.onAttacking(entity);
         }

         DamageSource damageSource = this.getDamageSources().windCharge(this, livingEntity2);
         if (entity.damage(serverWorld, damageSource, 1.0F) && entity instanceof LivingEntity livingEntity3) {
            EnchantmentHelper.onTargetDamaged(serverWorld, livingEntity3, damageSource);
         }

         this.createExplosion(this.getPos());
      }
   }

   public void addVelocity(double deltaX, double deltaY, double deltaZ) {
   }

   protected abstract void createExplosion(Vec3d pos);

   protected void onBlockHit(BlockHitResult blockHitResult) {
      super.onBlockHit(blockHitResult);
      if (!this.getWorld().isClient) {
         Vec3i vec3i = blockHitResult.getSide().getVector();
         Vec3d vec3d = Vec3d.of(vec3i).multiply(0.25, 0.25, 0.25);
         Vec3d vec3d2 = blockHitResult.getPos().add(vec3d);
         this.createExplosion(vec3d2);
         this.discard();
      }

   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      if (!this.getWorld().isClient) {
         this.discard();
      }

   }

   protected boolean isBurning() {
      return false;
   }

   public ItemStack getStack() {
      return ItemStack.EMPTY;
   }

   protected float getDrag() {
      return 1.0F;
   }

   protected float getDragInWater() {
      return this.getDrag();
   }

   @Nullable
   protected ParticleEffect getParticleType() {
      return null;
   }

   public void tick() {
      if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopYInclusive() + 30) {
         this.createExplosion(this.getPos());
         this.discard();
      } else {
         super.tick();
      }

   }

   static {
      EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(true, false, Optional.empty(), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()));
   }
}

package net.minecraft.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FireballEntity extends AbstractFireballEntity {
   private static final byte DEFAULT_EXPLOSION_POWER = 1;
   private int explosionPower = 1;

   public FireballEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public FireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
      super(EntityType.FIREBALL, owner, velocity, world);
      this.explosionPower = explosionPower;
   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         boolean bl = serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
         this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, bl, World.ExplosionSourceType.MOB);
         this.discard();
      }

   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         Entity entity = entityHitResult.getEntity();
         Entity entity2 = this.getOwner();
         DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
         entity.damage(serverWorld, damageSource, 6.0F);
         EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
      }
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putByte("ExplosionPower", (byte)this.explosionPower);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.explosionPower = view.getByte("ExplosionPower", (byte)1);
   }
}

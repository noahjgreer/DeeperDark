package net.minecraft.entity.projectile;

import net.minecraft.block.AbstractBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LlamaSpitEntity extends ProjectileEntity {
   public LlamaSpitEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public LlamaSpitEntity(World world, LlamaEntity owner) {
      this(EntityType.LLAMA_SPIT, world);
      this.setOwner(owner);
      this.setPosition(owner.getX() - (double)(owner.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(owner.bodyYaw * 0.017453292F), owner.getEyeY() - 0.10000000149011612, owner.getZ() + (double)(owner.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(owner.bodyYaw * 0.017453292F));
   }

   protected double getGravity() {
      return 0.06;
   }

   public void tick() {
      super.tick();
      Vec3d vec3d = this.getVelocity();
      HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
      this.hitOrDeflect(hitResult);
      double d = this.getX() + vec3d.x;
      double e = this.getY() + vec3d.y;
      double f = this.getZ() + vec3d.z;
      this.updateRotation();
      float g = 0.99F;
      if (this.getWorld().getStatesInBox(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
         this.discard();
      } else if (this.isTouchingWater()) {
         this.discard();
      } else {
         this.setVelocity(vec3d.multiply(0.9900000095367432));
         this.applyGravity();
         this.setPosition(d, e, f);
      }
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      Entity entity = this.getOwner();
      if (entity instanceof LivingEntity livingEntity) {
         entity = entityHitResult.getEntity();
         DamageSource damageSource = this.getDamageSources().spit(this, livingEntity);
         World var6 = this.getWorld();
         if (var6 instanceof ServerWorld serverWorld) {
            if (entity.damage(serverWorld, damageSource, 1.0F)) {
               EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
            }
         }
      }

   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      super.onBlockHit(blockHitResult);
      if (!this.getWorld().isClient) {
         this.discard();
      }

   }

   protected void initDataTracker(DataTracker.Builder builder) {
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      double d = packet.getVelocityX();
      double e = packet.getVelocityY();
      double f = packet.getVelocityZ();

      for(int i = 0; i < 7; ++i) {
         double g = 0.4 + 0.1 * (double)i;
         this.getWorld().addParticleClient(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), d * g, e, f * g);
      }

      this.setVelocity(d, e, f);
   }
}

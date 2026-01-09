package net.minecraft.entity.projectile.thrown;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ThrownEntity extends ProjectileEntity {
   private static final float field_52510 = 12.25F;

   protected ThrownEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected ThrownEntity(EntityType type, double x, double y, double z, World world) {
      this(type, world);
      this.setPosition(x, y, z);
   }

   public boolean shouldRender(double distance) {
      if (this.age < 2 && distance < 12.25) {
         return false;
      } else {
         double d = this.getBoundingBox().getAverageSideLength() * 4.0;
         if (Double.isNaN(d)) {
            d = 4.0;
         }

         d *= 64.0;
         return distance < d * d;
      }
   }

   public boolean canUsePortals(boolean allowVehicles) {
      return true;
   }

   public void tick() {
      this.tickInitialBubbleColumnCollision();
      this.applyGravity();
      this.applyDrag();
      HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
      Vec3d vec3d;
      if (hitResult.getType() != HitResult.Type.MISS) {
         vec3d = hitResult.getPos();
      } else {
         vec3d = this.getPos().add(this.getVelocity());
      }

      this.setPosition(vec3d);
      this.updateRotation();
      this.tickBlockCollision();
      super.tick();
      if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
         this.hitOrDeflect(hitResult);
      }

   }

   private void applyDrag() {
      Vec3d vec3d = this.getVelocity();
      Vec3d vec3d2 = this.getPos();
      float g;
      if (this.isTouchingWater()) {
         for(int i = 0; i < 4; ++i) {
            float f = 0.25F;
            this.getWorld().addParticleClient(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
         }

         g = 0.8F;
      } else {
         g = 0.99F;
      }

      this.setVelocity(vec3d.multiply((double)g));
   }

   private void tickInitialBubbleColumnCollision() {
      if (this.firstUpdate) {
         Iterator var1 = BlockPos.iterate(this.getBoundingBox()).iterator();

         while(var1.hasNext()) {
            BlockPos blockPos = (BlockPos)var1.next();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.BUBBLE_COLUMN)) {
               blockState.onEntityCollision(this.getWorld(), blockPos, this, EntityCollisionHandler.DUMMY);
            }
         }
      }

   }

   protected double getGravity() {
      return 0.03;
   }
}

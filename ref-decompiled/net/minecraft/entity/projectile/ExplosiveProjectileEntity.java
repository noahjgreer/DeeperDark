package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ExplosiveProjectileEntity extends ProjectileEntity {
   public static final double DEFAULT_ACCELERATION_POWER = 0.1;
   public static final double field_51892 = 0.5;
   public double accelerationPower;

   protected ExplosiveProjectileEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.accelerationPower = 0.1;
   }

   protected ExplosiveProjectileEntity(EntityType type, double x, double y, double z, World world) {
      this(type, world);
      this.setPosition(x, y, z);
   }

   public ExplosiveProjectileEntity(EntityType type, double x, double y, double z, Vec3d velocity, World world) {
      this(type, world);
      this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
      this.refreshPosition();
      this.setVelocityWithAcceleration(velocity, this.accelerationPower);
   }

   public ExplosiveProjectileEntity(EntityType type, LivingEntity owner, Vec3d velocity, World world) {
      this(type, owner.getX(), owner.getY(), owner.getZ(), velocity, world);
      this.setOwner(owner);
      this.setRotation(owner.getYaw(), owner.getPitch());
   }

   protected void initDataTracker(DataTracker.Builder builder) {
   }

   public boolean shouldRender(double distance) {
      double d = this.getBoundingBox().getAverageSideLength() * 4.0;
      if (Double.isNaN(d)) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   protected RaycastContext.ShapeType getRaycastShapeType() {
      return RaycastContext.ShapeType.COLLIDER;
   }

   public void tick() {
      Entity entity = this.getOwner();
      this.applyDrag();
      if (this.getWorld().isClient || (entity == null || !entity.isRemoved()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
         HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
         Vec3d vec3d;
         if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getPos();
         } else {
            vec3d = this.getPos().add(this.getVelocity());
         }

         ProjectileUtil.setRotationFromVelocity(this, 0.2F);
         this.setPosition(vec3d);
         this.tickBlockCollision();
         super.tick();
         if (this.isBurning()) {
            this.setOnFireFor(1.0F);
         }

         if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitOrDeflect(hitResult);
         }

         this.addParticles();
      } else {
         this.discard();
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

         g = this.getDragInWater();
      } else {
         g = this.getDrag();
      }

      this.setVelocity(vec3d.add(vec3d.normalize().multiply(this.accelerationPower)).multiply((double)g));
   }

   private void addParticles() {
      ParticleEffect particleEffect = this.getParticleType();
      Vec3d vec3d = this.getPos();
      if (particleEffect != null) {
         this.getWorld().addParticleClient(particleEffect, vec3d.x, vec3d.y + 0.5, vec3d.z, 0.0, 0.0, 0.0);
      }

   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      return false;
   }

   protected boolean canHit(Entity entity) {
      return super.canHit(entity) && !entity.noClip;
   }

   protected boolean isBurning() {
      return true;
   }

   @Nullable
   protected ParticleEffect getParticleType() {
      return ParticleTypes.SMOKE;
   }

   protected float getDrag() {
      return 0.95F;
   }

   protected float getDragInWater() {
      return 0.8F;
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putDouble("acceleration_power", this.accelerationPower);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.accelerationPower = view.getDouble("acceleration_power", 0.1);
   }

   public float getBrightnessAtEyes() {
      return 1.0F;
   }

   private void setVelocityWithAcceleration(Vec3d velocity, double accelerationPower) {
      this.setVelocity(velocity.normalize().multiply(accelerationPower));
      this.velocityDirty = true;
   }

   protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
      super.onDeflected(deflector, fromAttack);
      if (fromAttack) {
         this.accelerationPower = 0.1;
      } else {
         this.accelerationPower *= 0.5;
      }

   }
}

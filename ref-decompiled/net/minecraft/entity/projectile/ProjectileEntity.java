package net.minecraft.entity.projectile;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectileEntity extends Entity implements Ownable {
   private static final boolean DEFAULT_LEFT_OWNER = false;
   private static final boolean DEFAULT_SHOT = false;
   @Nullable
   protected LazyEntityReference owner;
   private boolean leftOwner = false;
   private boolean shot = false;
   @Nullable
   private Entity lastDeflectedEntity;

   public ProjectileEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void setOwner(@Nullable LazyEntityReference owner) {
      this.owner = owner;
   }

   public void setOwner(@Nullable Entity owner) {
      this.setOwner(owner != null ? new LazyEntityReference(owner) : null);
   }

   @Nullable
   public Entity getOwner() {
      return (Entity)LazyEntityReference.resolve(this.owner, this.getWorld(), Entity.class);
   }

   public Entity getEffectCause() {
      return (Entity)MoreObjects.firstNonNull(this.getOwner(), this);
   }

   protected void writeCustomData(WriteView view) {
      LazyEntityReference.writeData(this.owner, view, "Owner");
      if (this.leftOwner) {
         view.putBoolean("LeftOwner", true);
      }

      view.putBoolean("HasBeenShot", this.shot);
   }

   protected boolean isOwner(Entity entity) {
      return this.owner != null && this.owner.uuidEquals(entity);
   }

   protected void readCustomData(ReadView view) {
      this.setOwner(LazyEntityReference.fromData(view, "Owner"));
      this.leftOwner = view.getBoolean("LeftOwner", false);
      this.shot = view.getBoolean("HasBeenShot", false);
   }

   public void copyFrom(Entity original) {
      super.copyFrom(original);
      if (original instanceof ProjectileEntity projectileEntity) {
         this.owner = projectileEntity.owner;
      }

   }

   public void tick() {
      if (!this.shot) {
         this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
         this.shot = true;
      }

      if (!this.leftOwner) {
         this.leftOwner = this.shouldLeaveOwner();
      }

      super.tick();
   }

   private boolean shouldLeaveOwner() {
      Entity entity = this.getOwner();
      if (entity != null) {
         Box box = this.getBoundingBox().stretch(this.getVelocity()).expand(1.0);
         return entity.getRootVehicle().streamSelfAndPassengers().filter(EntityPredicates.CAN_HIT).noneMatch((entityx) -> {
            return box.intersects(entityx.getBoundingBox());
         });
      } else {
         return true;
      }
   }

   public Vec3d calculateVelocity(double x, double y, double z, float power, float uncertainty) {
      return (new Vec3d(x, y, z)).normalize().add(this.random.nextTriangular(0.0, 0.0172275 * (double)uncertainty), this.random.nextTriangular(0.0, 0.0172275 * (double)uncertainty), this.random.nextTriangular(0.0, 0.0172275 * (double)uncertainty)).multiply((double)power);
   }

   public void setVelocity(double x, double y, double z, float power, float uncertainty) {
      Vec3d vec3d = this.calculateVelocity(x, y, z, power, uncertainty);
      this.setVelocity(vec3d);
      this.velocityDirty = true;
      double d = vec3d.horizontalLength();
      this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
      this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875));
      this.lastYaw = this.getYaw();
      this.lastPitch = this.getPitch();
   }

   public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
      float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
      float g = -MathHelper.sin((pitch + roll) * 0.017453292F);
      float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
      this.setVelocity((double)f, (double)g, (double)h, speed, divergence);
      Vec3d vec3d = shooter.getMovement();
      this.setVelocity(this.getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
   }

   public void onBubbleColumnSurfaceCollision(boolean drag, BlockPos pos) {
      double d = drag ? -0.03 : 0.1;
      this.setVelocity(this.getVelocity().add(0.0, d, 0.0));
      spawnBubbleColumnParticles(this.getWorld(), pos);
   }

   public void onBubbleColumnCollision(boolean drag) {
      double d = drag ? -0.03 : 0.06;
      this.setVelocity(this.getVelocity().add(0.0, d, 0.0));
      this.onLanding();
   }

   public static ProjectileEntity spawnWithVelocity(ProjectileCreator creator, ServerWorld world, ItemStack projectileStack, LivingEntity shooter, float roll, float power, float divergence) {
      return spawn(creator.create(world, shooter, projectileStack), world, projectileStack, (entity) -> {
         entity.setVelocity(shooter, shooter.getPitch(), shooter.getYaw(), roll, power, divergence);
      });
   }

   public static ProjectileEntity spawnWithVelocity(ProjectileCreator creator, ServerWorld world, ItemStack projectileStack, LivingEntity shooter, double velocityX, double velocityY, double velocityZ, float power, float divergence) {
      return spawn(creator.create(world, shooter, projectileStack), world, projectileStack, (entity) -> {
         entity.setVelocity(velocityX, velocityY, velocityZ, power, divergence);
      });
   }

   public static ProjectileEntity spawnWithVelocity(ProjectileEntity projectile, ServerWorld world, ItemStack projectileStack, double velocityX, double velocityY, double velocityZ, float power, float divergence) {
      return spawn(projectile, world, projectileStack, (entity) -> {
         projectile.setVelocity(velocityX, velocityY, velocityZ, power, divergence);
      });
   }

   public static ProjectileEntity spawn(ProjectileEntity projectile, ServerWorld world, ItemStack projectileStack) {
      return spawn(projectile, world, projectileStack, (entity) -> {
      });
   }

   public static ProjectileEntity spawn(ProjectileEntity projectile, ServerWorld world, ItemStack projectileStack, Consumer beforeSpawn) {
      beforeSpawn.accept(projectile);
      world.spawnEntity(projectile);
      projectile.triggerProjectileSpawned(world, projectileStack);
      return projectile;
   }

   public void triggerProjectileSpawned(ServerWorld world, ItemStack projectileStack) {
      EnchantmentHelper.onProjectileSpawned(world, projectileStack, this, (item) -> {
      });
      if (this instanceof PersistentProjectileEntity persistentProjectileEntity) {
         ItemStack itemStack = persistentProjectileEntity.getWeaponStack();
         if (itemStack != null && !itemStack.isEmpty() && !projectileStack.getItem().equals(itemStack.getItem())) {
            Objects.requireNonNull(persistentProjectileEntity);
            EnchantmentHelper.onProjectileSpawned(world, itemStack, this, persistentProjectileEntity::onBroken);
         }
      }

   }

   protected ProjectileDeflection hitOrDeflect(HitResult hitResult) {
      if (hitResult.getType() == HitResult.Type.ENTITY) {
         EntityHitResult entityHitResult = (EntityHitResult)hitResult;
         Entity entity = entityHitResult.getEntity();
         ProjectileDeflection projectileDeflection = entity.getProjectileDeflection(this);
         if (projectileDeflection != ProjectileDeflection.NONE) {
            if (entity != this.lastDeflectedEntity && this.deflect(projectileDeflection, entity, this.getOwner(), false)) {
               this.lastDeflectedEntity = entity;
            }

            return projectileDeflection;
         }
      } else if (this.deflectsAgainstWorldBorder() && hitResult instanceof BlockHitResult) {
         BlockHitResult blockHitResult = (BlockHitResult)hitResult;
         if (blockHitResult.isAgainstWorldBorder()) {
            ProjectileDeflection projectileDeflection2 = ProjectileDeflection.SIMPLE;
            if (this.deflect(projectileDeflection2, (Entity)null, this.getOwner(), false)) {
               this.setVelocity(this.getVelocity().multiply(0.2));
               return projectileDeflection2;
            }
         }
      }

      this.onCollision(hitResult);
      return ProjectileDeflection.NONE;
   }

   protected boolean deflectsAgainstWorldBorder() {
      return false;
   }

   public boolean deflect(ProjectileDeflection deflection, @Nullable Entity deflector, @Nullable Entity owner, boolean fromAttack) {
      deflection.deflect(this, deflector, this.random);
      if (!this.getWorld().isClient) {
         this.setOwner(owner);
         this.onDeflected(deflector, fromAttack);
      }

      return true;
   }

   protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
   }

   protected void onBroken(Item item) {
   }

   protected void onCollision(HitResult hitResult) {
      HitResult.Type type = hitResult.getType();
      if (type == HitResult.Type.ENTITY) {
         EntityHitResult entityHitResult = (EntityHitResult)hitResult;
         Entity entity = entityHitResult.getEntity();
         if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity) {
            ProjectileEntity projectileEntity = (ProjectileEntity)entity;
            projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this.getOwner(), this.getOwner(), true);
         }

         this.onEntityHit(entityHitResult);
         this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, (BlockState)null));
      } else if (type == HitResult.Type.BLOCK) {
         BlockHitResult blockHitResult = (BlockHitResult)hitResult;
         this.onBlockHit(blockHitResult);
         BlockPos blockPos = blockHitResult.getBlockPos();
         this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
      }

   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
      blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
   }

   protected boolean canHit(Entity entity) {
      if (!entity.canBeHitByProjectile()) {
         return false;
      } else {
         Entity entity2 = this.getOwner();
         return entity2 == null || this.leftOwner || !entity2.isConnectedThroughVehicle(entity);
      }
   }

   protected void updateRotation() {
      Vec3d vec3d = this.getVelocity();
      double d = vec3d.horizontalLength();
      this.setPitch(updateRotation(this.lastPitch, (float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875)));
      this.setYaw(updateRotation(this.lastYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875)));
   }

   protected static float updateRotation(float lastRot, float newRot) {
      while(newRot - lastRot < -180.0F) {
         lastRot -= 360.0F;
      }

      while(newRot - lastRot >= 180.0F) {
         lastRot += 360.0F;
      }

      return MathHelper.lerp(0.2F, lastRot, newRot);
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      Entity entity = this.getOwner();
      return new EntitySpawnS2CPacket(this, entityTrackerEntry, entity == null ? 0 : entity.getId());
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      Entity entity = this.getWorld().getEntityById(packet.getEntityData());
      if (entity != null) {
         this.setOwner(entity);
      }

   }

   public boolean canModifyAt(ServerWorld world, BlockPos pos) {
      Entity entity = this.getOwner();
      if (entity instanceof PlayerEntity) {
         return entity.canModifyAt(world, pos);
      } else {
         return entity == null || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
      }
   }

   public boolean canBreakBlocks(ServerWorld world) {
      return this.getType().isIn(EntityTypeTags.IMPACT_PROJECTILES) && world.getGameRules().getBoolean(GameRules.PROJECTILES_CAN_BREAK_BLOCKS);
   }

   public boolean canHit() {
      return this.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE);
   }

   public float getTargetingMargin() {
      return this.canHit() ? 1.0F : 0.0F;
   }

   public DoubleDoubleImmutablePair getKnockback(LivingEntity target, DamageSource source) {
      double d = this.getVelocity().x;
      double e = this.getVelocity().z;
      return DoubleDoubleImmutablePair.of(d, e);
   }

   public int getDefaultPortalCooldown() {
      return 2;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (!this.isAlwaysInvulnerableTo(source)) {
         this.scheduleVelocityUpdate();
      }

      return false;
   }

   @FunctionalInterface
   public interface ProjectileCreator {
      ProjectileEntity create(ServerWorld world, LivingEntity shooter, ItemStack stack);
   }
}

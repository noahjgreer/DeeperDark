package net.minecraft.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class FireworkRocketEntity extends ProjectileEntity implements FlyingItemEntity {
   private static final TrackedData ITEM;
   private static final TrackedData SHOOTER_ENTITY_ID;
   private static final TrackedData SHOT_AT_ANGLE;
   private static final int DEFAULT_LIFE = 0;
   private static final int DEFAULT_LIFE_TIME = 0;
   private static final boolean DEFAULT_SHOT_AT_ANGLE = false;
   private int life;
   private int lifeTime;
   @Nullable
   private LivingEntity shooter;

   public FireworkRocketEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.life = 0;
      this.lifeTime = 0;
   }

   public FireworkRocketEntity(World world, double x, double y, double z, ItemStack stack) {
      super(EntityType.FIREWORK_ROCKET, world);
      this.life = 0;
      this.lifeTime = 0;
      this.life = 0;
      this.setPosition(x, y, z);
      this.dataTracker.set(ITEM, stack.copy());
      int i = 1;
      FireworksComponent fireworksComponent = (FireworksComponent)stack.get(DataComponentTypes.FIREWORKS);
      if (fireworksComponent != null) {
         i += fireworksComponent.flightDuration();
      }

      this.setVelocity(this.random.nextTriangular(0.0, 0.002297), 0.05, this.random.nextTriangular(0.0, 0.002297));
      this.lifeTime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
   }

   public FireworkRocketEntity(World world, @Nullable Entity entity, double x, double y, double z, ItemStack stack) {
      this(world, x, y, z, stack);
      this.setOwner(entity);
   }

   public FireworkRocketEntity(World world, ItemStack stack, LivingEntity shooter) {
      this(world, shooter, shooter.getX(), shooter.getY(), shooter.getZ(), stack);
      this.dataTracker.set(SHOOTER_ENTITY_ID, OptionalInt.of(shooter.getId()));
      this.shooter = shooter;
   }

   public FireworkRocketEntity(World world, ItemStack stack, double x, double y, double z, boolean shotAtAngle) {
      this(world, x, y, z, stack);
      this.dataTracker.set(SHOT_AT_ANGLE, shotAtAngle);
   }

   public FireworkRocketEntity(World world, ItemStack stack, Entity entity, double x, double y, double z, boolean shotAtAngle) {
      this(world, stack, x, y, z, shotAtAngle);
      this.setOwner(entity);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(ITEM, getDefaultStack());
      builder.add(SHOOTER_ENTITY_ID, OptionalInt.empty());
      builder.add(SHOT_AT_ANGLE, false);
   }

   public boolean shouldRender(double distance) {
      return distance < 4096.0 && !this.wasShotByEntity();
   }

   public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
      return super.shouldRender(cameraX, cameraY, cameraZ) && !this.wasShotByEntity();
   }

   public void tick() {
      super.tick();
      HitResult hitResult;
      Vec3d vec3d3;
      if (this.wasShotByEntity()) {
         if (this.shooter == null) {
            ((OptionalInt)this.dataTracker.get(SHOOTER_ENTITY_ID)).ifPresent((id) -> {
               Entity entity = this.getWorld().getEntityById(id);
               if (entity instanceof LivingEntity) {
                  this.shooter = (LivingEntity)entity;
               }

            });
         }

         if (this.shooter != null) {
            if (this.shooter.isGliding()) {
               Vec3d vec3d = this.shooter.getRotationVector();
               double d = 1.5;
               double e = 0.1;
               Vec3d vec3d2 = this.shooter.getVelocity();
               this.shooter.setVelocity(vec3d2.add(vec3d.x * 0.1 + (vec3d.x * 1.5 - vec3d2.x) * 0.5, vec3d.y * 0.1 + (vec3d.y * 1.5 - vec3d2.y) * 0.5, vec3d.z * 0.1 + (vec3d.z * 1.5 - vec3d2.z) * 0.5));
               vec3d3 = this.shooter.getHandPosOffset(Items.FIREWORK_ROCKET);
            } else {
               vec3d3 = Vec3d.ZERO;
            }

            this.setPosition(this.shooter.getX() + vec3d3.x, this.shooter.getY() + vec3d3.y, this.shooter.getZ() + vec3d3.z);
            this.setVelocity(this.shooter.getVelocity());
         }

         hitResult = ProjectileUtil.getCollision(this, this::canHit);
      } else {
         if (!this.wasShotAtAngle()) {
            double f = this.horizontalCollision ? 1.0 : 1.15;
            this.setVelocity(this.getVelocity().multiply(f, 1.0, f).add(0.0, 0.04, 0.0));
         }

         vec3d3 = this.getVelocity();
         hitResult = ProjectileUtil.getCollision(this, this::canHit);
         this.move(MovementType.SELF, vec3d3);
         this.tickBlockCollision();
         this.setVelocity(vec3d3);
      }

      if (!this.noClip && this.isAlive() && hitResult.getType() != HitResult.Type.MISS) {
         this.hitOrDeflect(hitResult);
         this.velocityDirty = true;
      }

      this.updateRotation();
      if (this.life == 0 && !this.isSilent()) {
         this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
      }

      ++this.life;
      if (this.getWorld().isClient && this.life % 2 < 2) {
         this.getWorld().addParticleClient(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, -this.getVelocity().y * 0.5, this.random.nextGaussian() * 0.05);
      }

      if (this.life > this.lifeTime) {
         World var10 = this.getWorld();
         if (var10 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var10;
            this.explodeAndRemove(serverWorld);
         }
      }

   }

   private void explodeAndRemove(ServerWorld world) {
      world.sendEntityStatus(this, (byte)17);
      this.emitGameEvent(GameEvent.EXPLODE, this.getOwner());
      this.explode(world);
      this.discard();
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         this.explodeAndRemove(serverWorld);
      }

   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      BlockPos blockPos = new BlockPos(blockHitResult.getBlockPos());
      this.getWorld().getBlockState(blockPos).onEntityCollision(this.getWorld(), blockPos, this, EntityCollisionHandler.DUMMY);
      World var4 = this.getWorld();
      if (var4 instanceof ServerWorld serverWorld) {
         if (this.hasExplosionEffects()) {
            this.explodeAndRemove(serverWorld);
         }
      }

      super.onBlockHit(blockHitResult);
   }

   private boolean hasExplosionEffects() {
      return !this.getExplosions().isEmpty();
   }

   private void explode(ServerWorld world) {
      float f = 0.0F;
      List list = this.getExplosions();
      if (!list.isEmpty()) {
         f = 5.0F + (float)(list.size() * 2);
      }

      if (f > 0.0F) {
         if (this.shooter != null) {
            this.shooter.damage(world, this.getDamageSources().fireworks(this, this.getOwner()), 5.0F + (float)(list.size() * 2));
         }

         double d = 5.0;
         Vec3d vec3d = this.getPos();
         List list2 = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(5.0));
         Iterator var8 = list2.iterator();

         while(true) {
            LivingEntity livingEntity;
            do {
               do {
                  if (!var8.hasNext()) {
                     return;
                  }

                  livingEntity = (LivingEntity)var8.next();
               } while(livingEntity == this.shooter);
            } while(this.squaredDistanceTo(livingEntity) > 25.0);

            boolean bl = false;

            for(int i = 0; i < 2; ++i) {
               Vec3d vec3d2 = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5 * (double)i), livingEntity.getZ());
               HitResult hitResult = this.getWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
               if (hitResult.getType() == HitResult.Type.MISS) {
                  bl = true;
                  break;
               }
            }

            if (bl) {
               float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
               livingEntity.damage(world, this.getDamageSources().fireworks(this, this.getOwner()), g);
            }
         }
      }
   }

   private boolean wasShotByEntity() {
      return ((OptionalInt)this.dataTracker.get(SHOOTER_ENTITY_ID)).isPresent();
   }

   public boolean wasShotAtAngle() {
      return (Boolean)this.dataTracker.get(SHOT_AT_ANGLE);
   }

   public void handleStatus(byte status) {
      if (status == 17 && this.getWorld().isClient) {
         Vec3d vec3d = this.getVelocity();
         this.getWorld().addFireworkParticle(this.getX(), this.getY(), this.getZ(), vec3d.x, vec3d.y, vec3d.z, this.getExplosions());
      }

      super.handleStatus(status);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("Life", this.life);
      view.putInt("LifeTime", this.lifeTime);
      view.put("FireworksItem", ItemStack.CODEC, this.getStack());
      view.putBoolean("ShotAtAngle", (Boolean)this.dataTracker.get(SHOT_AT_ANGLE));
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.life = view.getInt("Life", 0);
      this.lifeTime = view.getInt("LifeTime", 0);
      this.dataTracker.set(ITEM, (ItemStack)view.read("FireworksItem", ItemStack.CODEC).orElse(getDefaultStack()));
      this.dataTracker.set(SHOT_AT_ANGLE, view.getBoolean("ShotAtAngle", false));
   }

   private List getExplosions() {
      ItemStack itemStack = (ItemStack)this.dataTracker.get(ITEM);
      FireworksComponent fireworksComponent = (FireworksComponent)itemStack.get(DataComponentTypes.FIREWORKS);
      return fireworksComponent != null ? fireworksComponent.explosions() : List.of();
   }

   public ItemStack getStack() {
      return (ItemStack)this.dataTracker.get(ITEM);
   }

   public boolean isAttackable() {
      return false;
   }

   private static ItemStack getDefaultStack() {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   public DoubleDoubleImmutablePair getKnockback(LivingEntity target, DamageSource source) {
      double d = target.getPos().x - this.getPos().x;
      double e = target.getPos().z - this.getPos().z;
      return DoubleDoubleImmutablePair.of(d, e);
   }

   static {
      ITEM = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
      SHOOTER_ENTITY_ID = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.OPTIONAL_INT);
      SHOT_AT_ANGLE = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }
}

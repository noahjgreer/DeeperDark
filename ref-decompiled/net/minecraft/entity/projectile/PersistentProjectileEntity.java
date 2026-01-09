package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class PersistentProjectileEntity extends ProjectileEntity {
   private static final double field_30657 = 2.0;
   private static final int field_54968 = 7;
   private static final float field_55017 = 0.6F;
   private static final float DEFAULT_DRAG = 0.99F;
   private static final short DEFAULT_LIFE = 0;
   private static final byte DEFAULT_SHAKE = 0;
   private static final boolean DEFAULT_IN_GROUND = false;
   private static final boolean DEFAULT_CRITICAL = false;
   private static final byte DEFAULT_PIERCE_LEVEL = 0;
   private static final TrackedData PROJECTILE_FLAGS;
   private static final TrackedData PIERCE_LEVEL;
   private static final TrackedData IN_GROUND;
   private static final int CRITICAL_FLAG = 1;
   private static final int NO_CLIP_FLAG = 2;
   @Nullable
   private BlockState inBlockState;
   protected int inGroundTime;
   public PickupPermission pickupType;
   public int shake;
   private int life;
   private double damage;
   private SoundEvent sound;
   @Nullable
   private IntOpenHashSet piercedEntities;
   @Nullable
   private List piercingKilledEntities;
   private ItemStack stack;
   @Nullable
   private ItemStack weapon;

   protected PersistentProjectileEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
      this.shake = 0;
      this.life = 0;
      this.damage = 2.0;
      this.sound = this.getHitSound();
      this.stack = this.getDefaultItemStack();
      this.weapon = null;
   }

   protected PersistentProjectileEntity(EntityType type, double x, double y, double z, World world, ItemStack stack, @Nullable ItemStack weapon) {
      this(type, world);
      this.stack = stack.copy();
      this.copyComponentsFrom(stack);
      Unit unit = (Unit)stack.remove(DataComponentTypes.INTANGIBLE_PROJECTILE);
      if (unit != null) {
         this.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
      }

      this.setPosition(x, y, z);
      if (weapon != null && world instanceof ServerWorld serverWorld) {
         if (weapon.isEmpty()) {
            throw new IllegalArgumentException("Invalid weapon firing an arrow");
         }

         this.weapon = weapon.copy();
         int i = EnchantmentHelper.getProjectilePiercing(serverWorld, weapon, this.stack);
         if (i > 0) {
            this.setPierceLevel((byte)i);
         }
      }

   }

   protected PersistentProjectileEntity(EntityType type, LivingEntity owner, World world, ItemStack stack, @Nullable ItemStack shotFrom) {
      this(type, owner.getX(), owner.getEyeY() - 0.10000000149011612, owner.getZ(), world, stack, shotFrom);
      this.setOwner(owner);
   }

   public void setSound(SoundEvent sound) {
      this.sound = sound;
   }

   public boolean shouldRender(double distance) {
      double d = this.getBoundingBox().getAverageSideLength() * 10.0;
      if (Double.isNaN(d)) {
         d = 1.0;
      }

      d *= 64.0 * getRenderDistanceMultiplier();
      return distance < d * d;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(PROJECTILE_FLAGS, (byte)0);
      builder.add(PIERCE_LEVEL, (byte)0);
      builder.add(IN_GROUND, false);
   }

   public void setVelocity(double x, double y, double z, float power, float uncertainty) {
      super.setVelocity(x, y, z, power, uncertainty);
      this.life = 0;
   }

   public void setVelocityClient(double x, double y, double z) {
      super.setVelocityClient(x, y, z);
      this.life = 0;
      if (this.isInGround() && MathHelper.squaredMagnitude(x, y, z) > 0.0) {
         this.setInGround(false);
      }

   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (!this.firstUpdate && this.shake <= 0 && data.equals(IN_GROUND) && this.isInGround()) {
         this.shake = 7;
      }

   }

   public void tick() {
      boolean bl = !this.isNoClip();
      Vec3d vec3d = this.getVelocity();
      BlockPos blockPos = this.getBlockPos();
      BlockState blockState = this.getWorld().getBlockState(blockPos);
      if (!blockState.isAir() && bl) {
         VoxelShape voxelShape = blockState.getCollisionShape(this.getWorld(), blockPos);
         if (!voxelShape.isEmpty()) {
            Vec3d vec3d2 = this.getPos();
            Iterator var7 = voxelShape.getBoundingBoxes().iterator();

            while(var7.hasNext()) {
               Box box = (Box)var7.next();
               if (box.offset(blockPos).contains(vec3d2)) {
                  this.setVelocity(Vec3d.ZERO);
                  this.setInGround(true);
                  break;
               }
            }
         }
      }

      if (this.shake > 0) {
         --this.shake;
      }

      if (this.isTouchingWaterOrRain()) {
         this.extinguish();
      }

      if (this.isInGround() && bl) {
         if (!this.getWorld().isClient()) {
            if (this.inBlockState != blockState && this.shouldFall()) {
               this.fall();
            } else {
               this.age();
            }
         }

         ++this.inGroundTime;
         if (this.isAlive()) {
            this.tickBlockCollision();
         }

         if (!this.getWorld().isClient) {
            this.setOnFire(this.getFireTicks() > 0);
         }

      } else {
         this.inGroundTime = 0;
         Vec3d vec3d3 = this.getPos();
         if (this.isTouchingWater()) {
            this.applyDrag(this.getDragInWater());
            this.spawnBubbleParticles(vec3d3);
         }

         if (this.isCritical()) {
            for(int i = 0; i < 4; ++i) {
               this.getWorld().addParticleClient(ParticleTypes.CRIT, vec3d3.x + vec3d.x * (double)i / 4.0, vec3d3.y + vec3d.y * (double)i / 4.0, vec3d3.z + vec3d.z * (double)i / 4.0, -vec3d.x, -vec3d.y + 0.2, -vec3d.z);
            }
         }

         float f;
         if (!bl) {
            f = (float)(MathHelper.atan2(-vec3d.x, -vec3d.z) * 57.2957763671875);
         } else {
            f = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
         }

         float g = (float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875);
         this.setPitch(updateRotation(this.getPitch(), g));
         this.setYaw(updateRotation(this.getYaw(), f));
         if (bl) {
            BlockHitResult blockHitResult = this.getWorld().getCollisionsIncludingWorldBorder(new RaycastContext(vec3d3, vec3d3.add(vec3d), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            this.applyCollision(blockHitResult);
         } else {
            this.setPosition(vec3d3.add(vec3d));
            this.tickBlockCollision();
         }

         if (!this.isTouchingWater()) {
            this.applyDrag(0.99F);
         }

         if (bl && !this.isInGround()) {
            this.applyGravity();
         }

         super.tick();
      }
   }

   private void applyCollision(BlockHitResult blockHitResult) {
      while(true) {
         if (this.isAlive()) {
            Vec3d vec3d = this.getPos();
            EntityHitResult entityHitResult = this.getEntityCollision(vec3d, blockHitResult.getPos());
            Vec3d vec3d2 = ((HitResult)Objects.requireNonNullElse(entityHitResult, blockHitResult)).getPos();
            this.setPosition(vec3d2);
            this.tickBlockCollision(vec3d, vec3d2);
            if (this.portalManager != null && this.portalManager.isInPortal()) {
               this.tickPortalTeleportation();
            }

            if (entityHitResult == null) {
               if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS) {
                  this.hitOrDeflect(blockHitResult);
                  this.velocityDirty = true;
               }
            } else {
               if (!this.isAlive() || this.noClip) {
                  continue;
               }

               ProjectileDeflection projectileDeflection = this.hitOrDeflect(entityHitResult);
               this.velocityDirty = true;
               if (this.getPierceLevel() > 0 && projectileDeflection == ProjectileDeflection.NONE) {
                  continue;
               }
            }
         }

         return;
      }
   }

   private void applyDrag(float drag) {
      Vec3d vec3d = this.getVelocity();
      this.setVelocity(vec3d.multiply((double)drag));
   }

   private void spawnBubbleParticles(Vec3d pos) {
      Vec3d vec3d = this.getVelocity();

      for(int i = 0; i < 4; ++i) {
         float f = 0.25F;
         this.getWorld().addParticleClient(ParticleTypes.BUBBLE, pos.x - vec3d.x * 0.25, pos.y - vec3d.y * 0.25, pos.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
      }

   }

   protected double getGravity() {
      return 0.05;
   }

   private boolean shouldFall() {
      return this.isInGround() && this.getWorld().isSpaceEmpty((new Box(this.getPos(), this.getPos())).expand(0.06));
   }

   private void fall() {
      this.setInGround(false);
      Vec3d vec3d = this.getVelocity();
      this.setVelocity(vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      this.life = 0;
   }

   protected boolean isInGround() {
      return (Boolean)this.dataTracker.get(IN_GROUND);
   }

   protected void setInGround(boolean inGround) {
      this.dataTracker.set(IN_GROUND, inGround);
   }

   public boolean isPushedByFluids() {
      return !this.isInGround();
   }

   public void move(MovementType type, Vec3d movement) {
      super.move(type, movement);
      if (type != MovementType.SELF && this.shouldFall()) {
         this.fall();
      }

   }

   protected void age() {
      ++this.life;
      if (this.life >= 1200) {
         this.discard();
      }

   }

   private void clearPiercingStatus() {
      if (this.piercingKilledEntities != null) {
         this.piercingKilledEntities.clear();
      }

      if (this.piercedEntities != null) {
         this.piercedEntities.clear();
      }

   }

   protected void onBroken(Item item) {
      this.weapon = null;
   }

   public void onBubbleColumnSurfaceCollision(boolean drag, BlockPos pos) {
      if (!this.isInGround()) {
         super.onBubbleColumnSurfaceCollision(drag, pos);
      }
   }

   public void onBubbleColumnCollision(boolean drag) {
      if (!this.isInGround()) {
         super.onBubbleColumnCollision(drag);
      }
   }

   public void addVelocity(double deltaX, double deltaY, double deltaZ) {
      if (!this.isInGround()) {
         super.addVelocity(deltaX, deltaY, deltaZ);
      }
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      Entity entity = entityHitResult.getEntity();
      float f = (float)this.getVelocity().length();
      double d = this.damage;
      Entity entity2 = this.getOwner();
      DamageSource damageSource = this.getDamageSources().arrow(this, (Entity)(entity2 != null ? entity2 : this));
      if (this.getWeaponStack() != null) {
         World var9 = this.getWorld();
         if (var9 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var9;
            d = (double)EnchantmentHelper.getDamage(serverWorld, this.getWeaponStack(), entity, damageSource, (float)d);
         }
      }

      int i = MathHelper.ceil(MathHelper.clamp((double)f * d, 0.0, 2.147483647E9));
      if (this.getPierceLevel() > 0) {
         if (this.piercedEntities == null) {
            this.piercedEntities = new IntOpenHashSet(5);
         }

         if (this.piercingKilledEntities == null) {
            this.piercingKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
            this.discard();
            return;
         }

         this.piercedEntities.add(entity.getId());
      }

      if (this.isCritical()) {
         long l = (long)this.random.nextInt(i / 2 + 2);
         i = (int)Math.min(l + (long)i, 2147483647L);
      }

      if (entity2 instanceof LivingEntity livingEntity) {
         livingEntity.onAttacking(entity);
      }

      boolean bl = entity.getType() == EntityType.ENDERMAN;
      int j = entity.getFireTicks();
      if (this.isOnFire() && !bl) {
         entity.setOnFireFor(5.0F);
      }

      if (entity.sidedDamage(damageSource, (float)i)) {
         if (bl) {
            return;
         }

         if (entity instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            if (!this.getWorld().isClient && this.getPierceLevel() <= 0) {
               livingEntity2.setStuckArrowCount(livingEntity2.getStuckArrowCount() + 1);
            }

            this.knockback(livingEntity2, damageSource);
            World var13 = this.getWorld();
            if (var13 instanceof ServerWorld) {
               ServerWorld serverWorld2 = (ServerWorld)var13;
               EnchantmentHelper.onTargetDamaged(serverWorld2, livingEntity2, damageSource, this.getWeaponStack());
            }

            this.onHit(livingEntity2);
            ServerPlayerEntity serverPlayerEntity;
            if (livingEntity2 instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity) {
               serverPlayerEntity = (ServerPlayerEntity)entity2;
               if (!this.isSilent() && livingEntity2 != serverPlayerEntity) {
                  serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, 0.0F));
               }
            }

            if (!entity.isAlive() && this.piercingKilledEntities != null) {
               this.piercingKilledEntities.add(livingEntity2);
            }

            if (!this.getWorld().isClient && entity2 instanceof ServerPlayerEntity) {
               serverPlayerEntity = (ServerPlayerEntity)entity2;
               if (this.piercingKilledEntities != null) {
                  Criteria.KILLED_BY_ARROW.trigger(serverPlayerEntity, this.piercingKilledEntities, this.weapon);
               } else if (!entity.isAlive()) {
                  Criteria.KILLED_BY_ARROW.trigger(serverPlayerEntity, List.of(entity), this.weapon);
               }
            }
         }

         this.playSound(this.sound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.discard();
         }
      } else {
         entity.setFireTicks(j);
         this.deflect(ProjectileDeflection.SIMPLE, entity, this.getOwner(), false);
         this.setVelocity(this.getVelocity().multiply(0.2));
         World var20 = this.getWorld();
         if (var20 instanceof ServerWorld) {
            ServerWorld serverWorld3 = (ServerWorld)var20;
            if (this.getVelocity().lengthSquared() < 1.0E-7) {
               if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                  this.dropStack(serverWorld3, this.asItemStack(), 0.1F);
               }

               this.discard();
            }
         }
      }

   }

   protected void knockback(LivingEntity target, DamageSource source) {
      float var10000;
      label18: {
         if (this.weapon != null) {
            World var6 = this.getWorld();
            if (var6 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var6;
               var10000 = EnchantmentHelper.modifyKnockback(serverWorld, this.weapon, target, source, 0.0F);
               break label18;
            }
         }

         var10000 = 0.0F;
      }

      double d = (double)var10000;
      if (d > 0.0) {
         double e = Math.max(0.0, 1.0 - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
         Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply(d * 0.6 * e);
         if (vec3d.lengthSquared() > 0.0) {
            target.addVelocity(vec3d.x, 0.1, vec3d.z);
         }
      }

   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      this.inBlockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
      super.onBlockHit(blockHitResult);
      ItemStack itemStack = this.getWeaponStack();
      World var4 = this.getWorld();
      if (var4 instanceof ServerWorld serverWorld) {
         if (itemStack != null) {
            this.onBlockHitEnchantmentEffects(serverWorld, blockHitResult, itemStack);
         }
      }

      Vec3d vec3d = this.getVelocity();
      Vec3d vec3d2 = new Vec3d(Math.signum(vec3d.x), Math.signum(vec3d.y), Math.signum(vec3d.z));
      Vec3d vec3d3 = vec3d2.multiply(0.05000000074505806);
      this.setPosition(this.getPos().subtract(vec3d3));
      this.setVelocity(Vec3d.ZERO);
      this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      this.setInGround(true);
      this.shake = 7;
      this.setCritical(false);
      this.setPierceLevel((byte)0);
      this.setSound(SoundEvents.ENTITY_ARROW_HIT);
      this.clearPiercingStatus();
   }

   protected void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack) {
      Vec3d vec3d = blockHitResult.getBlockPos().clampToWithin(blockHitResult.getPos());
      Entity var6 = this.getOwner();
      LivingEntity var10002;
      if (var6 instanceof LivingEntity livingEntity) {
         var10002 = livingEntity;
      } else {
         var10002 = null;
      }

      EnchantmentHelper.onHitBlock(world, weaponStack, var10002, this, (EquipmentSlot)null, vec3d, world.getBlockState(blockHitResult.getBlockPos()), (item) -> {
         this.weapon = null;
      });
   }

   public ItemStack getWeaponStack() {
      return this.weapon;
   }

   protected SoundEvent getHitSound() {
      return SoundEvents.ENTITY_ARROW_HIT;
   }

   protected final SoundEvent getSound() {
      return this.sound;
   }

   protected void onHit(LivingEntity target) {
   }

   @Nullable
   protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
      return ProjectileUtil.getEntityCollision(this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
   }

   protected boolean canHit(Entity entity) {
      if (entity instanceof PlayerEntity) {
         Entity var3 = this.getOwner();
         if (var3 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)var3;
            if (!playerEntity.shouldDamagePlayer((PlayerEntity)entity)) {
               return false;
            }
         }
      }

      return super.canHit(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putShort("life", (short)this.life);
      view.putNullable("inBlockState", BlockState.CODEC, this.inBlockState);
      view.putByte("shake", (byte)this.shake);
      view.putBoolean("inGround", this.isInGround());
      view.put("pickup", PersistentProjectileEntity.PickupPermission.CODEC, this.pickupType);
      view.putDouble("damage", this.damage);
      view.putBoolean("crit", this.isCritical());
      view.putByte("PierceLevel", this.getPierceLevel());
      view.put("SoundEvent", Registries.SOUND_EVENT.getCodec(), this.sound);
      view.put("item", ItemStack.CODEC, this.stack);
      view.putNullable("weapon", ItemStack.CODEC, this.weapon);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.life = view.getShort("life", (short)0);
      this.inBlockState = (BlockState)view.read("inBlockState", BlockState.CODEC).orElse((Object)null);
      this.shake = view.getByte("shake", (byte)0) & 255;
      this.setInGround(view.getBoolean("inGround", false));
      this.damage = view.getDouble("damage", 2.0);
      this.pickupType = (PickupPermission)view.read("pickup", PersistentProjectileEntity.PickupPermission.CODEC).orElse(PersistentProjectileEntity.PickupPermission.DISALLOWED);
      this.setCritical(view.getBoolean("crit", false));
      this.setPierceLevel(view.getByte("PierceLevel", (byte)0));
      this.sound = (SoundEvent)view.read("SoundEvent", Registries.SOUND_EVENT.getCodec()).orElse(this.getHitSound());
      this.setStack((ItemStack)view.read("item", ItemStack.CODEC).orElse(this.getDefaultItemStack()));
      this.weapon = (ItemStack)view.read("weapon", ItemStack.CODEC).orElse((Object)null);
   }

   public void setOwner(@Nullable Entity owner) {
      super.setOwner(owner);
      Entity var2 = owner;
      byte var3 = 0;

      PickupPermission var10001;
      label16:
      while(true) {
         switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
            case -1:
            default:
               var10001 = this.pickupType;
               break label16;
            case 0:
               PlayerEntity playerEntity = (PlayerEntity)var2;
               if (this.pickupType != PersistentProjectileEntity.PickupPermission.DISALLOWED) {
                  var3 = 1;
                  break;
               }

               var10001 = PersistentProjectileEntity.PickupPermission.ALLOWED;
               break label16;
            case 1:
               OminousItemSpawnerEntity ominousItemSpawnerEntity = (OminousItemSpawnerEntity)var2;
               var10001 = PersistentProjectileEntity.PickupPermission.DISALLOWED;
               break label16;
         }
      }

      this.pickupType = var10001;
   }

   public void onPlayerCollision(PlayerEntity player) {
      if (!this.getWorld().isClient && (this.isInGround() || this.isNoClip()) && this.shake <= 0) {
         if (this.tryPickup(player)) {
            player.sendPickup(this, 1);
            this.discard();
         }

      }
   }

   protected boolean tryPickup(PlayerEntity player) {
      boolean var10000;
      switch (this.pickupType.ordinal()) {
         case 0:
            var10000 = false;
            break;
         case 1:
            var10000 = player.getInventory().insertStack(this.asItemStack());
            break;
         case 2:
            var10000 = player.isInCreativeMode();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   protected ItemStack asItemStack() {
      return this.stack.copy();
   }

   protected abstract ItemStack getDefaultItemStack();

   protected Entity.MoveEffect getMoveEffect() {
      return Entity.MoveEffect.NONE;
   }

   public ItemStack getItemStack() {
      return this.stack;
   }

   public void setDamage(double damage) {
      this.damage = damage;
   }

   public boolean isAttackable() {
      return this.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE);
   }

   public void setCritical(boolean critical) {
      this.setProjectileFlag(1, critical);
   }

   private void setPierceLevel(byte level) {
      this.dataTracker.set(PIERCE_LEVEL, level);
   }

   private void setProjectileFlag(int index, boolean flag) {
      byte b = (Byte)this.dataTracker.get(PROJECTILE_FLAGS);
      if (flag) {
         this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b | index));
      } else {
         this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b & ~index));
      }

   }

   protected void setStack(ItemStack stack) {
      if (!stack.isEmpty()) {
         this.stack = stack;
      } else {
         this.stack = this.getDefaultItemStack();
      }

   }

   public boolean isCritical() {
      byte b = (Byte)this.dataTracker.get(PROJECTILE_FLAGS);
      return (b & 1) != 0;
   }

   public byte getPierceLevel() {
      return (Byte)this.dataTracker.get(PIERCE_LEVEL);
   }

   public void applyDamageModifier(float damageModifier) {
      this.setDamage((double)(damageModifier * 2.0F) + this.random.nextTriangular((double)this.getWorld().getDifficulty().getId() * 0.11, 0.57425));
   }

   protected float getDragInWater() {
      return 0.6F;
   }

   public void setNoClip(boolean noClip) {
      this.noClip = noClip;
      this.setProjectileFlag(2, noClip);
   }

   public boolean isNoClip() {
      if (!this.getWorld().isClient) {
         return this.noClip;
      } else {
         return ((Byte)this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
      }
   }

   public boolean canHit() {
      return super.canHit() && !this.isInGround();
   }

   public StackReference getStackReference(int mappedIndex) {
      return mappedIndex == 0 ? StackReference.of(this::getItemStack, this::setStack) : super.getStackReference(mappedIndex);
   }

   protected boolean deflectsAgainstWorldBorder() {
      return true;
   }

   static {
      PROJECTILE_FLAGS = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
      PIERCE_LEVEL = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
      IN_GROUND = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }

   public static enum PickupPermission {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static final Codec CODEC = Codec.BYTE.xmap(PickupPermission::fromOrdinal, (pickupPermission) -> {
         return (byte)pickupPermission.ordinal();
      });

      public static PickupPermission fromOrdinal(int ordinal) {
         if (ordinal < 0 || ordinal > values().length) {
            ordinal = 0;
         }

         return values()[ordinal];
      }

      // $FF: synthetic method
      private static PickupPermission[] method_36663() {
         return new PickupPermission[]{DISALLOWED, ALLOWED, CREATIVE_ONLY};
      }
   }
}

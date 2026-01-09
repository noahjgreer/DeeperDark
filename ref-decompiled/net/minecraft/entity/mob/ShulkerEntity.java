package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ShulkerEntity extends GolemEntity implements Monster {
   private static final Identifier COVERED_ARMOR_MODIFIER_ID = Identifier.ofVanilla("covered");
   private static final EntityAttributeModifier COVERED_ARMOR_BONUS;
   protected static final TrackedData ATTACHED_FACE;
   protected static final TrackedData PEEK_AMOUNT;
   protected static final TrackedData COLOR;
   private static final int field_30487 = 6;
   private static final byte field_30488 = 16;
   private static final byte field_30489 = 16;
   private static final int field_30490 = 8;
   private static final int field_30491 = 8;
   private static final int field_30492 = 5;
   private static final float field_30493 = 0.05F;
   private static final byte DEFAULT_PEEK = 0;
   private static final Direction DEFAULT_ATTACHED_FACE;
   static final Vector3f SOUTH_VECTOR;
   private static final float field_48343 = 3.0F;
   private float lastOpenProgress;
   private float openProgress;
   @Nullable
   private BlockPos lastAttachedBlock;
   private int teleportLerpTimer;
   private static final float field_30494 = 1.0F;

   public ShulkerEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.experiencePoints = 5;
      this.lookControl = new ShulkerLookControl(this);
   }

   protected void initGoals() {
      this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F, 0.02F, true));
      this.goalSelector.add(4, new ShootBulletGoal());
      this.goalSelector.add(7, new PeekGoal());
      this.goalSelector.add(8, new LookAroundGoal(this));
      this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{this.getClass()})).setGroupRevenge());
      this.targetSelector.add(2, new TargetPlayerGoal(this));
      this.targetSelector.add(3, new TargetOtherTeamGoal(this));
   }

   protected Entity.MoveEffect getMoveEffect() {
      return Entity.MoveEffect.NONE;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SHULKER_AMBIENT;
   }

   public void playAmbientSound() {
      if (!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return this.isClosed() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_SHULKER_HURT;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(ATTACHED_FACE, DEFAULT_ATTACHED_FACE);
      builder.add(PEEK_AMOUNT, (byte)0);
      builder.add(COLOR, (byte)16);
   }

   public static DefaultAttributeContainer.Builder createShulkerAttributes() {
      return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 30.0);
   }

   protected BodyControl createBodyControl() {
      return new ShulkerBodyControl(this);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setAttachedFace((Direction)view.read("AttachFace", Direction.INDEX_CODEC).orElse(DEFAULT_ATTACHED_FACE));
      this.dataTracker.set(PEEK_AMOUNT, view.getByte("Peek", (byte)0));
      this.dataTracker.set(COLOR, view.getByte("Color", (byte)16));
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("AttachFace", Direction.INDEX_CODEC, this.getAttachedFace());
      view.putByte("Peek", (Byte)this.dataTracker.get(PEEK_AMOUNT));
      view.putByte("Color", (Byte)this.dataTracker.get(COLOR));
   }

   public void tick() {
      super.tick();
      if (!this.getWorld().isClient && !this.hasVehicle() && !this.canStay(this.getBlockPos(), this.getAttachedFace())) {
         this.tryAttachOrTeleport();
      }

      if (this.tickOpenProgress()) {
         this.moveEntities();
      }

      if (this.getWorld().isClient) {
         if (this.teleportLerpTimer > 0) {
            --this.teleportLerpTimer;
         } else {
            this.lastAttachedBlock = null;
         }
      }

   }

   private void tryAttachOrTeleport() {
      Direction direction = this.findAttachSide(this.getBlockPos());
      if (direction != null) {
         this.setAttachedFace(direction);
      } else {
         this.tryTeleport();
      }

   }

   protected Box calculateDefaultBoundingBox(Vec3d pos) {
      float f = getExtraLength(this.openProgress);
      Direction direction = this.getAttachedFace().getOpposite();
      return calculateBoundingBox(this.getScale(), direction, f, pos);
   }

   private static float getExtraLength(float openProgress) {
      return 0.5F - MathHelper.sin((0.5F + openProgress) * 3.1415927F) * 0.5F;
   }

   private boolean tickOpenProgress() {
      this.lastOpenProgress = this.openProgress;
      float f = (float)this.getPeekAmount() * 0.01F;
      if (this.openProgress == f) {
         return false;
      } else {
         if (this.openProgress > f) {
            this.openProgress = MathHelper.clamp(this.openProgress - 0.05F, f, 1.0F);
         } else {
            this.openProgress = MathHelper.clamp(this.openProgress + 0.05F, 0.0F, f);
         }

         return true;
      }
   }

   private void moveEntities() {
      this.refreshPosition();
      float f = getExtraLength(this.openProgress);
      float g = getExtraLength(this.lastOpenProgress);
      Direction direction = this.getAttachedFace().getOpposite();
      float h = (f - g) * this.getScale();
      if (!(h <= 0.0F)) {
         List list = this.getWorld().getOtherEntities(this, calculateBoundingBox(this.getScale(), direction, g, f, this.getPos()), EntityPredicates.EXCEPT_SPECTATOR.and((entityx) -> {
            return !entityx.isConnectedThroughVehicle(this);
         }));
         Iterator var6 = list.iterator();

         while(var6.hasNext()) {
            Entity entity = (Entity)var6.next();
            if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
               entity.move(MovementType.SHULKER, new Vec3d((double)(h * (float)direction.getOffsetX()), (double)(h * (float)direction.getOffsetY()), (double)(h * (float)direction.getOffsetZ())));
            }
         }

      }
   }

   public static Box calculateBoundingBox(float scale, Direction facing, float extraLength, Vec3d pos) {
      return calculateBoundingBox(scale, facing, -1.0F, extraLength, pos);
   }

   public static Box calculateBoundingBox(float scale, Direction facing, float lastExtraLength, float extraLength, Vec3d pos) {
      Box box = new Box((double)(-scale) * 0.5, 0.0, (double)(-scale) * 0.5, (double)scale * 0.5, (double)scale, (double)scale * 0.5);
      double d = (double)Math.max(lastExtraLength, extraLength);
      double e = (double)Math.min(lastExtraLength, extraLength);
      Box box2 = box.stretch((double)facing.getOffsetX() * d * (double)scale, (double)facing.getOffsetY() * d * (double)scale, (double)facing.getOffsetZ() * d * (double)scale).shrink((double)(-facing.getOffsetX()) * (1.0 + e) * (double)scale, (double)(-facing.getOffsetY()) * (1.0 + e) * (double)scale, (double)(-facing.getOffsetZ()) * (1.0 + e) * (double)scale);
      return box2.offset(pos.x, pos.y, pos.z);
   }

   public boolean startRiding(Entity entity, boolean force) {
      if (this.getWorld().isClient()) {
         this.lastAttachedBlock = null;
         this.teleportLerpTimer = 0;
      }

      this.setAttachedFace(Direction.DOWN);
      return super.startRiding(entity, force);
   }

   public void stopRiding() {
      super.stopRiding();
      if (this.getWorld().isClient) {
         this.lastAttachedBlock = this.getBlockPos();
      }

      this.lastBodyYaw = 0.0F;
      this.bodyYaw = 0.0F;
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      this.setYaw(0.0F);
      this.headYaw = this.getYaw();
      this.resetPosition();
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public void move(MovementType type, Vec3d movement) {
      if (type == MovementType.SHULKER_BOX) {
         this.tryTeleport();
      } else {
         super.move(type, movement);
      }

   }

   public Vec3d getVelocity() {
      return Vec3d.ZERO;
   }

   public void setVelocity(Vec3d velocity) {
   }

   public void setPosition(double x, double y, double z) {
      BlockPos blockPos = this.getBlockPos();
      if (this.hasVehicle()) {
         super.setPosition(x, y, z);
      } else {
         super.setPosition((double)MathHelper.floor(x) + 0.5, (double)MathHelper.floor(y + 0.5), (double)MathHelper.floor(z) + 0.5);
      }

      if (this.age != 0) {
         BlockPos blockPos2 = this.getBlockPos();
         if (!blockPos2.equals(blockPos)) {
            this.dataTracker.set(PEEK_AMOUNT, (byte)0);
            this.velocityDirty = true;
            if (this.getWorld().isClient && !this.hasVehicle() && !blockPos2.equals(this.lastAttachedBlock)) {
               this.lastAttachedBlock = blockPos;
               this.teleportLerpTimer = 6;
               this.lastRenderX = this.getX();
               this.lastRenderY = this.getY();
               this.lastRenderZ = this.getZ();
            }
         }

      }
   }

   @Nullable
   protected Direction findAttachSide(BlockPos pos) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         if (this.canStay(pos, direction)) {
            return direction;
         }
      }

      return null;
   }

   boolean canStay(BlockPos pos, Direction direction) {
      if (this.isInvalidPosition(pos)) {
         return false;
      } else {
         Direction direction2 = direction.getOpposite();
         if (!this.getWorld().isDirectionSolid(pos.offset(direction), this, direction2)) {
            return false;
         } else {
            Box box = calculateBoundingBox(this.getScale(), direction2, 1.0F, pos.toBottomCenterPos()).contract(1.0E-6);
            return this.getWorld().isSpaceEmpty(this, box);
         }
      }
   }

   private boolean isInvalidPosition(BlockPos pos) {
      BlockState blockState = this.getWorld().getBlockState(pos);
      if (blockState.isAir()) {
         return false;
      } else {
         boolean bl = blockState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos());
         return !bl;
      }
   }

   protected boolean tryTeleport() {
      if (!this.isAiDisabled() && this.isAlive()) {
         BlockPos blockPos = this.getBlockPos();

         for(int i = 0; i < 5; ++i) {
            BlockPos blockPos2 = blockPos.add(MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8));
            if (blockPos2.getY() > this.getWorld().getBottomY() && this.getWorld().isAir(blockPos2) && this.getWorld().getWorldBorder().contains(blockPos2) && this.getWorld().isSpaceEmpty(this, (new Box(blockPos2)).contract(1.0E-6))) {
               Direction direction = this.findAttachSide(blockPos2);
               if (direction != null) {
                  this.detach();
                  this.setAttachedFace(direction);
                  this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
                  this.getWorld().emitGameEvent(GameEvent.TELEPORT, blockPos, GameEvent.Emitter.of((Entity)this));
                  this.dataTracker.set(PEEK_AMOUNT, (byte)0);
                  this.setTarget((LivingEntity)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public PositionInterpolator getInterpolator() {
      return null;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      Entity entity;
      if (this.isClosed()) {
         entity = source.getSource();
         if (entity instanceof PersistentProjectileEntity) {
            return false;
         }
      }

      if (!super.damage(world, source, amount)) {
         return false;
      } else {
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
            this.tryTeleport();
         } else if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            entity = source.getSource();
            if (entity != null && entity.getType() == EntityType.SHULKER_BULLET) {
               this.spawnNewShulker();
            }
         }

         return true;
      }
   }

   private boolean isClosed() {
      return this.getPeekAmount() == 0;
   }

   private void spawnNewShulker() {
      Vec3d vec3d = this.getPos();
      Box box = this.getBoundingBox();
      if (!this.isClosed() && this.tryTeleport()) {
         int i = this.getWorld().getEntitiesByType(EntityType.SHULKER, box.expand(8.0), Entity::isAlive).size();
         float f = (float)(i - 1) / 5.0F;
         if (!(this.getWorld().random.nextFloat() < f)) {
            ShulkerEntity shulkerEntity = (ShulkerEntity)EntityType.SHULKER.create(this.getWorld(), SpawnReason.BREEDING);
            if (shulkerEntity != null) {
               shulkerEntity.setColor(this.getColorOptional());
               shulkerEntity.refreshPositionAfterTeleport(vec3d);
               this.getWorld().spawnEntity(shulkerEntity);
            }

         }
      }
   }

   public boolean isCollidable(@Nullable Entity entity) {
      return this.isAlive();
   }

   public Direction getAttachedFace() {
      return (Direction)this.dataTracker.get(ATTACHED_FACE);
   }

   private void setAttachedFace(Direction face) {
      this.dataTracker.set(ATTACHED_FACE, face);
   }

   public void onTrackedDataSet(TrackedData data) {
      if (ATTACHED_FACE.equals(data)) {
         this.setBoundingBox(this.calculateBoundingBox());
      }

      super.onTrackedDataSet(data);
   }

   private int getPeekAmount() {
      return (Byte)this.dataTracker.get(PEEK_AMOUNT);
   }

   void setPeekAmount(int peekAmount) {
      if (!this.getWorld().isClient) {
         this.getAttributeInstance(EntityAttributes.ARMOR).removeModifier(COVERED_ARMOR_MODIFIER_ID);
         if (peekAmount == 0) {
            this.getAttributeInstance(EntityAttributes.ARMOR).addPersistentModifier(COVERED_ARMOR_BONUS);
            this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
            this.emitGameEvent(GameEvent.CONTAINER_CLOSE);
         } else {
            this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
            this.emitGameEvent(GameEvent.CONTAINER_OPEN);
         }
      }

      this.dataTracker.set(PEEK_AMOUNT, (byte)peekAmount);
   }

   public float getOpenProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastOpenProgress, this.openProgress);
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      this.bodyYaw = 0.0F;
      this.lastBodyYaw = 0.0F;
   }

   public int getMaxLookPitchChange() {
      return 180;
   }

   public int getMaxHeadRotation() {
      return 180;
   }

   public void pushAwayFrom(Entity entity) {
   }

   @Nullable
   public Vec3d getRenderPositionOffset(float tickProgress) {
      if (this.lastAttachedBlock != null && this.teleportLerpTimer > 0) {
         double d = (double)((float)this.teleportLerpTimer - tickProgress) / 6.0;
         d *= d;
         d *= (double)this.getScale();
         BlockPos blockPos = this.getBlockPos();
         double e = (double)(blockPos.getX() - this.lastAttachedBlock.getX()) * d;
         double f = (double)(blockPos.getY() - this.lastAttachedBlock.getY()) * d;
         double g = (double)(blockPos.getZ() - this.lastAttachedBlock.getZ()) * d;
         return new Vec3d(-e, -f, -g);
      } else {
         return null;
      }
   }

   protected float clampScale(float scale) {
      return Math.min(scale, 3.0F);
   }

   private void setColor(Optional color) {
      this.dataTracker.set(COLOR, (Byte)color.map((colorx) -> {
         return (byte)colorx.getIndex();
      }).orElse((byte)16));
   }

   public Optional getColorOptional() {
      return Optional.ofNullable(this.getColor());
   }

   @Nullable
   public DyeColor getColor() {
      byte b = (Byte)this.dataTracker.get(COLOR);
      return b != 16 && b <= 15 ? DyeColor.byIndex(b) : null;
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.SHULKER_COLOR ? castComponentValue(type, this.getColor()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.SHULKER_COLOR);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.SHULKER_COLOR) {
         this.setColor(Optional.of((DyeColor)castComponentValue(DataComponentTypes.SHULKER_COLOR, value)));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   static {
      COVERED_ARMOR_BONUS = new EntityAttributeModifier(COVERED_ARMOR_MODIFIER_ID, 20.0, EntityAttributeModifier.Operation.ADD_VALUE);
      ATTACHED_FACE = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
      PEEK_AMOUNT = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
      COLOR = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
      DEFAULT_ATTACHED_FACE = Direction.DOWN;
      SOUTH_VECTOR = (Vector3f)Util.make(() -> {
         Vec3i vec3i = Direction.SOUTH.getVector();
         return new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
      });
   }

   private class ShulkerLookControl extends LookControl {
      public ShulkerLookControl(final MobEntity entity) {
         super(entity);
      }

      protected void clampHeadYaw() {
      }

      protected Optional getTargetYaw() {
         Direction direction = ShulkerEntity.this.getAttachedFace().getOpposite();
         Vector3f vector3f = direction.getRotationQuaternion().transform(new Vector3f(ShulkerEntity.SOUTH_VECTOR));
         Vec3i vec3i = direction.getVector();
         Vector3f vector3f2 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         vector3f2.cross(vector3f);
         double d = this.x - this.entity.getX();
         double e = this.y - this.entity.getEyeY();
         double f = this.z - this.entity.getZ();
         Vector3f vector3f3 = new Vector3f((float)d, (float)e, (float)f);
         float g = vector3f2.dot(vector3f3);
         float h = vector3f.dot(vector3f3);
         return !(Math.abs(g) > 1.0E-5F) && !(Math.abs(h) > 1.0E-5F) ? Optional.empty() : Optional.of((float)(MathHelper.atan2((double)(-g), (double)h) * 57.2957763671875));
      }

      protected Optional getTargetPitch() {
         return Optional.of(0.0F);
      }
   }

   private class ShootBulletGoal extends Goal {
      private int counter;

      public ShootBulletGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
      }

      public boolean canStart() {
         LivingEntity livingEntity = ShulkerEntity.this.getTarget();
         if (livingEntity != null && livingEntity.isAlive()) {
            return ShulkerEntity.this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      public void start() {
         this.counter = 20;
         ShulkerEntity.this.setPeekAmount(100);
      }

      public void stop() {
         ShulkerEntity.this.setPeekAmount(0);
      }

      public boolean shouldRunEveryTick() {
         return true;
      }

      public void tick() {
         if (ShulkerEntity.this.getWorld().getDifficulty() != Difficulty.PEACEFUL) {
            --this.counter;
            LivingEntity livingEntity = ShulkerEntity.this.getTarget();
            if (livingEntity != null) {
               ShulkerEntity.this.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
               double d = ShulkerEntity.this.squaredDistanceTo(livingEntity);
               if (d < 400.0) {
                  if (this.counter <= 0) {
                     this.counter = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
                     ShulkerEntity.this.getWorld().spawnEntity(new ShulkerBulletEntity(ShulkerEntity.this.getWorld(), ShulkerEntity.this, livingEntity, ShulkerEntity.this.getAttachedFace().getAxis()));
                     ShulkerEntity.this.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2F + 1.0F);
                  }
               } else {
                  ShulkerEntity.this.setTarget((LivingEntity)null);
               }

               super.tick();
            }
         }
      }
   }

   class PeekGoal extends Goal {
      private int counter;

      public boolean canStart() {
         return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(toGoalTicks(40)) == 0 && ShulkerEntity.this.canStay(ShulkerEntity.this.getBlockPos(), ShulkerEntity.this.getAttachedFace());
      }

      public boolean shouldContinue() {
         return ShulkerEntity.this.getTarget() == null && this.counter > 0;
      }

      public void start() {
         this.counter = this.getTickCount(20 * (1 + ShulkerEntity.this.random.nextInt(3)));
         ShulkerEntity.this.setPeekAmount(30);
      }

      public void stop() {
         if (ShulkerEntity.this.getTarget() == null) {
            ShulkerEntity.this.setPeekAmount(0);
         }

      }

      public void tick() {
         --this.counter;
      }
   }

   class TargetPlayerGoal extends ActiveTargetGoal {
      public TargetPlayerGoal(final ShulkerEntity shulker) {
         super(shulker, PlayerEntity.class, true);
      }

      public boolean canStart() {
         return ShulkerEntity.this.getWorld().getDifficulty() == Difficulty.PEACEFUL ? false : super.canStart();
      }

      protected Box getSearchBox(double distance) {
         Direction direction = ((ShulkerEntity)this.mob).getAttachedFace();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().expand(4.0, distance, distance);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().expand(distance, distance, 4.0) : this.mob.getBoundingBox().expand(distance, 4.0, distance);
         }
      }
   }

   private static class TargetOtherTeamGoal extends ActiveTargetGoal {
      public TargetOtherTeamGoal(ShulkerEntity shulker) {
         super(shulker, LivingEntity.class, 10, true, false, (entity, world) -> {
            return entity instanceof Monster;
         });
      }

      public boolean canStart() {
         return this.mob.getScoreboardTeam() == null ? false : super.canStart();
      }

      protected Box getSearchBox(double distance) {
         Direction direction = ((ShulkerEntity)this.mob).getAttachedFace();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().expand(4.0, distance, distance);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().expand(distance, distance, 4.0) : this.mob.getBoundingBox().expand(distance, 4.0, distance);
         }
      }
   }

   private static class ShulkerBodyControl extends BodyControl {
      public ShulkerBodyControl(MobEntity mobEntity) {
         super(mobEntity);
      }

      public void tick() {
      }
   }
}

package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class BatEntity extends AmbientEntity {
   public static final float field_46966 = 0.5F;
   public static final float field_46967 = 10.0F;
   private static final TrackedData BAT_FLAGS;
   private static final int ROOSTING_FLAG = 1;
   private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
   private static final byte DEFAULT_BAT_FLAGS = 0;
   public final AnimationState flyingAnimationState = new AnimationState();
   public final AnimationState roostingAnimationState = new AnimationState();
   @Nullable
   private BlockPos hangingPosition;

   public BatEntity(EntityType entityType, World world) {
      super(entityType, world);
      if (!world.isClient) {
         this.setRoosting(true);
      }

   }

   public boolean isFlappingWings() {
      return !this.isRoosting() && (float)this.age % 10.0F == 0.0F;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(BAT_FLAGS, (byte)0);
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   public float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.isRoosting() && this.random.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_BAT_DEATH;
   }

   public boolean isPushable() {
      return false;
   }

   protected void pushAway(Entity entity) {
   }

   protected void tickCramming() {
   }

   public static DefaultAttributeContainer.Builder createBatAttributes() {
      return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 6.0);
   }

   public boolean isRoosting() {
      return ((Byte)this.dataTracker.get(BAT_FLAGS) & 1) != 0;
   }

   public void setRoosting(boolean roosting) {
      byte b = (Byte)this.dataTracker.get(BAT_FLAGS);
      if (roosting) {
         this.dataTracker.set(BAT_FLAGS, (byte)(b | 1));
      } else {
         this.dataTracker.set(BAT_FLAGS, (byte)(b & -2));
      }

   }

   public void tick() {
      super.tick();
      if (this.isRoosting()) {
         this.setVelocity(Vec3d.ZERO);
         this.setPos(this.getX(), (double)MathHelper.floor(this.getY()) + 1.0 - (double)this.getHeight(), this.getZ());
      } else {
         this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
      }

      this.updateAnimations();
   }

   protected void mobTick(ServerWorld world) {
      super.mobTick(world);
      BlockPos blockPos = this.getBlockPos();
      BlockPos blockPos2 = blockPos.up();
      if (this.isRoosting()) {
         boolean bl = this.isSilent();
         if (world.getBlockState(blockPos2).isSolidBlock(world, blockPos)) {
            if (this.random.nextInt(200) == 0) {
               this.headYaw = (float)this.random.nextInt(360);
            }

            if (world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, this) != null) {
               this.setRoosting(false);
               if (!bl) {
                  world.syncWorldEvent((Entity)null, 1025, blockPos, 0);
               }
            }
         } else {
            this.setRoosting(false);
            if (!bl) {
               world.syncWorldEvent((Entity)null, 1025, blockPos, 0);
            }
         }
      } else {
         if (this.hangingPosition != null && (!world.isAir(this.hangingPosition) || this.hangingPosition.getY() <= world.getBottomY())) {
            this.hangingPosition = null;
         }

         if (this.hangingPosition == null || this.random.nextInt(30) == 0 || this.hangingPosition.isWithinDistance(this.getPos(), 2.0)) {
            this.hangingPosition = BlockPos.ofFloored(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
         }

         double d = (double)this.hangingPosition.getX() + 0.5 - this.getX();
         double e = (double)this.hangingPosition.getY() + 0.1 - this.getY();
         double f = (double)this.hangingPosition.getZ() + 0.5 - this.getZ();
         Vec3d vec3d = this.getVelocity();
         Vec3d vec3d2 = vec3d.add((Math.signum(d) * 0.5 - vec3d.x) * 0.10000000149011612, (Math.signum(e) * 0.699999988079071 - vec3d.y) * 0.10000000149011612, (Math.signum(f) * 0.5 - vec3d.z) * 0.10000000149011612);
         this.setVelocity(vec3d2);
         float g = (float)(MathHelper.atan2(vec3d2.z, vec3d2.x) * 57.2957763671875) - 90.0F;
         float h = MathHelper.wrapDegrees(g - this.getYaw());
         this.forwardSpeed = 0.5F;
         this.setYaw(this.getYaw() + h);
         if (this.random.nextInt(100) == 0 && world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
            this.setRoosting(true);
         }
      }

   }

   protected Entity.MoveEffect getMoveEffect() {
      return Entity.MoveEffect.EVENTS;
   }

   protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
   }

   public boolean canAvoidTraps() {
      return true;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (this.isInvulnerableTo(world, source)) {
         return false;
      } else {
         if (this.isRoosting()) {
            this.setRoosting(false);
         }

         return super.damage(world, source, amount);
      }
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.dataTracker.set(BAT_FLAGS, view.getByte("BatFlags", (byte)0));
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putByte("BatFlags", (Byte)this.dataTracker.get(BAT_FLAGS));
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      if (pos.getY() >= world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY()) {
         return false;
      } else {
         int i = world.getLightLevel(pos);
         int j = 4;
         if (isTodayAroundHalloween()) {
            j = 7;
         } else if (random.nextBoolean()) {
            return false;
         }

         if (i > random.nextInt(j)) {
            return false;
         } else {
            return !world.getBlockState(pos.down()).isIn(BlockTags.BATS_SPAWNABLE_ON) ? false : canMobSpawn(type, world, spawnReason, pos, random);
         }
      }
   }

   private static boolean isTodayAroundHalloween() {
      LocalDate localDate = LocalDate.now();
      int i = localDate.get(ChronoField.DAY_OF_MONTH);
      int j = localDate.get(ChronoField.MONTH_OF_YEAR);
      return j == 10 && i >= 20 || j == 11 && i <= 3;
   }

   private void updateAnimations() {
      if (this.isRoosting()) {
         this.flyingAnimationState.stop();
         this.roostingAnimationState.startIfNotRunning(this.age);
      } else {
         this.roostingAnimationState.stop();
         this.flyingAnimationState.startIfNotRunning(this.age);
      }

   }

   static {
      BAT_FLAGS = DataTracker.registerData(BatEntity.class, TrackedDataHandlerRegistry.BYTE);
      CLOSE_PLAYER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(4.0);
   }
}

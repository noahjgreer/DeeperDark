package net.minecraft.entity.projectile;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FishingBobberEntity extends ProjectileEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Random velocityRandom;
   private boolean caughtFish;
   private int outOfOpenWaterTicks;
   private static final int field_30665 = 10;
   private static final TrackedData HOOK_ENTITY_ID;
   private static final TrackedData CAUGHT_FISH;
   private int removalTimer;
   private int hookCountdown;
   private int waitCountdown;
   private int fishTravelCountdown;
   private float fishAngle;
   private boolean inOpenWater;
   @Nullable
   private Entity hookedEntity;
   private State state;
   private final int luckBonus;
   private final int waitTimeReductionTicks;
   private final PositionInterpolator positionInterpolator;

   public FishingBobberEntity(EntityType type, World world, int luckBonus, int waitTimeReductionTicks) {
      super(type, world);
      this.velocityRandom = Random.create();
      this.inOpenWater = true;
      this.state = FishingBobberEntity.State.FLYING;
      this.positionInterpolator = new PositionInterpolator(this);
      this.luckBonus = Math.max(0, luckBonus);
      this.waitTimeReductionTicks = Math.max(0, waitTimeReductionTicks);
   }

   public FishingBobberEntity(EntityType entityType, World world) {
      this((EntityType)entityType, world, 0, 0);
   }

   public FishingBobberEntity(PlayerEntity thrower, World world, int luckBonus, int waitTimeReductionTicks) {
      this(EntityType.FISHING_BOBBER, world, luckBonus, waitTimeReductionTicks);
      this.setOwner(thrower);
      float f = thrower.getPitch();
      float g = thrower.getYaw();
      float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
      float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
      float j = -MathHelper.cos(-f * 0.017453292F);
      float k = MathHelper.sin(-f * 0.017453292F);
      double d = thrower.getX() - (double)i * 0.3;
      double e = thrower.getEyeY();
      double l = thrower.getZ() - (double)h * 0.3;
      this.refreshPositionAndAngles(d, e, l, g, f);
      Vec3d vec3d = new Vec3d((double)(-i), (double)MathHelper.clamp(-(k / j), -5.0F, 5.0F), (double)(-h));
      double m = vec3d.length();
      vec3d = vec3d.multiply(0.6 / m + this.random.nextTriangular(0.5, 0.0103365), 0.6 / m + this.random.nextTriangular(0.5, 0.0103365), 0.6 / m + this.random.nextTriangular(0.5, 0.0103365));
      this.setVelocity(vec3d);
      this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
      this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875));
      this.lastYaw = this.getYaw();
      this.lastPitch = this.getPitch();
   }

   @NotNull
   public PositionInterpolator getInterpolator() {
      return this.positionInterpolator;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(HOOK_ENTITY_ID, 0);
      builder.add(CAUGHT_FISH, false);
   }

   protected boolean deflectsAgainstWorldBorder() {
      return true;
   }

   public void onTrackedDataSet(TrackedData data) {
      if (HOOK_ENTITY_ID.equals(data)) {
         int i = (Integer)this.getDataTracker().get(HOOK_ENTITY_ID);
         this.hookedEntity = i > 0 ? this.getWorld().getEntityById(i - 1) : null;
      }

      if (CAUGHT_FISH.equals(data)) {
         this.caughtFish = (Boolean)this.getDataTracker().get(CAUGHT_FISH);
         if (this.caughtFish) {
            this.setVelocity(this.getVelocity().x, (double)(-0.4F * MathHelper.nextFloat(this.velocityRandom, 0.6F, 1.0F)), this.getVelocity().z);
         }
      }

      super.onTrackedDataSet(data);
   }

   public boolean shouldRender(double distance) {
      double d = 64.0;
      return distance < 4096.0;
   }

   public void tick() {
      this.velocityRandom.setSeed(this.getUuid().getLeastSignificantBits() ^ this.getWorld().getTime());
      this.getInterpolator().tick();
      super.tick();
      PlayerEntity playerEntity = this.getPlayerOwner();
      if (playerEntity == null) {
         this.discard();
      } else if (this.getWorld().isClient || !this.removeIfInvalid(playerEntity)) {
         if (this.isOnGround()) {
            ++this.removalTimer;
            if (this.removalTimer >= 1200) {
               this.discard();
               return;
            }
         } else {
            this.removalTimer = 0;
         }

         float f = 0.0F;
         BlockPos blockPos = this.getBlockPos();
         FluidState fluidState = this.getWorld().getFluidState(blockPos);
         if (fluidState.isIn(FluidTags.WATER)) {
            f = fluidState.getHeight(this.getWorld(), blockPos);
         }

         boolean bl = f > 0.0F;
         if (this.state == FishingBobberEntity.State.FLYING) {
            if (this.hookedEntity != null) {
               this.setVelocity(Vec3d.ZERO);
               this.state = FishingBobberEntity.State.HOOKED_IN_ENTITY;
               return;
            }

            if (bl) {
               this.setVelocity(this.getVelocity().multiply(0.3, 0.2, 0.3));
               this.state = FishingBobberEntity.State.BOBBING;
               return;
            }

            this.checkForCollision();
         } else {
            if (this.state == FishingBobberEntity.State.HOOKED_IN_ENTITY) {
               if (this.hookedEntity != null) {
                  if (!this.hookedEntity.isRemoved() && this.hookedEntity.getWorld().getRegistryKey() == this.getWorld().getRegistryKey()) {
                     this.setPosition(this.hookedEntity.getX(), this.hookedEntity.getBodyY(0.8), this.hookedEntity.getZ());
                  } else {
                     this.updateHookedEntityId((Entity)null);
                     this.state = FishingBobberEntity.State.FLYING;
                  }
               }

               return;
            }

            if (this.state == FishingBobberEntity.State.BOBBING) {
               Vec3d vec3d = this.getVelocity();
               double d = this.getY() + vec3d.y - (double)blockPos.getY() - (double)f;
               if (Math.abs(d) < 0.01) {
                  d += Math.signum(d) * 0.1;
               }

               this.setVelocity(vec3d.x * 0.9, vec3d.y - d * (double)this.random.nextFloat() * 0.2, vec3d.z * 0.9);
               if (this.hookCountdown <= 0 && this.fishTravelCountdown <= 0) {
                  this.inOpenWater = true;
               } else {
                  this.inOpenWater = this.inOpenWater && this.outOfOpenWaterTicks < 10 && this.isOpenOrWaterAround(blockPos);
               }

               if (bl) {
                  this.outOfOpenWaterTicks = Math.max(0, this.outOfOpenWaterTicks - 1);
                  if (this.caughtFish) {
                     this.setVelocity(this.getVelocity().add(0.0, -0.1 * (double)this.velocityRandom.nextFloat() * (double)this.velocityRandom.nextFloat(), 0.0));
                  }

                  if (!this.getWorld().isClient) {
                     this.tickFishingLogic(blockPos);
                  }
               } else {
                  this.outOfOpenWaterTicks = Math.min(10, this.outOfOpenWaterTicks + 1);
               }
            }
         }

         if (!fluidState.isIn(FluidTags.WATER) && !this.isOnGround() && this.hookedEntity == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
         }

         this.move(MovementType.SELF, this.getVelocity());
         this.tickBlockCollision();
         this.updateRotation();
         if (this.state == FishingBobberEntity.State.FLYING && (this.isOnGround() || this.horizontalCollision)) {
            this.setVelocity(Vec3d.ZERO);
         }

         double e = 0.92;
         this.setVelocity(this.getVelocity().multiply(0.92));
         this.refreshPosition();
      }
   }

   private boolean removeIfInvalid(PlayerEntity player) {
      ItemStack itemStack = player.getMainHandStack();
      ItemStack itemStack2 = player.getOffHandStack();
      boolean bl = itemStack.isOf(Items.FISHING_ROD);
      boolean bl2 = itemStack2.isOf(Items.FISHING_ROD);
      if (!player.isRemoved() && player.isAlive() && (bl || bl2) && !(this.squaredDistanceTo(player) > 1024.0)) {
         return false;
      } else {
         this.discard();
         return true;
      }
   }

   private void checkForCollision() {
      HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
      this.hitOrDeflect(hitResult);
   }

   protected boolean canHit(Entity entity) {
      return super.canHit(entity) || entity.isAlive() && entity instanceof ItemEntity;
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      if (!this.getWorld().isClient) {
         this.updateHookedEntityId(entityHitResult.getEntity());
      }

   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      super.onBlockHit(blockHitResult);
      this.setVelocity(this.getVelocity().normalize().multiply(blockHitResult.squaredDistanceTo(this)));
   }

   private void updateHookedEntityId(@Nullable Entity entity) {
      this.hookedEntity = entity;
      this.getDataTracker().set(HOOK_ENTITY_ID, entity == null ? 0 : entity.getId() + 1);
   }

   private void tickFishingLogic(BlockPos pos) {
      ServerWorld serverWorld = (ServerWorld)this.getWorld();
      int i = 1;
      BlockPos blockPos = pos.up();
      if (this.random.nextFloat() < 0.25F && this.getWorld().hasRain(blockPos)) {
         ++i;
      }

      if (this.random.nextFloat() < 0.5F && !this.getWorld().isSkyVisible(blockPos)) {
         --i;
      }

      if (this.hookCountdown > 0) {
         --this.hookCountdown;
         if (this.hookCountdown <= 0) {
            this.waitCountdown = 0;
            this.fishTravelCountdown = 0;
            this.getDataTracker().set(CAUGHT_FISH, false);
         }
      } else {
         float f;
         float g;
         float h;
         double d;
         double e;
         double j;
         BlockState blockState;
         if (this.fishTravelCountdown > 0) {
            this.fishTravelCountdown -= i;
            if (this.fishTravelCountdown > 0) {
               this.fishAngle += (float)this.random.nextTriangular(0.0, 9.188);
               f = this.fishAngle * 0.017453292F;
               g = MathHelper.sin(f);
               h = MathHelper.cos(f);
               d = this.getX() + (double)(g * (float)this.fishTravelCountdown * 0.1F);
               e = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
               j = this.getZ() + (double)(h * (float)this.fishTravelCountdown * 0.1F);
               blockState = serverWorld.getBlockState(BlockPos.ofFloored(d, e - 1.0, j));
               if (blockState.isOf(Blocks.WATER)) {
                  if (this.random.nextFloat() < 0.15F) {
                     serverWorld.spawnParticles(ParticleTypes.BUBBLE, d, e - 0.10000000149011612, j, 1, (double)g, 0.1, (double)h, 0.0);
                  }

                  float k = g * 0.04F;
                  float l = h * 0.04F;
                  serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, (double)l, 0.01, (double)(-k), 1.0);
                  serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, (double)(-l), 0.01, (double)k, 1.0);
               }
            } else {
               this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
               double m = this.getY() + 0.5;
               serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0, (double)this.getWidth(), 0.20000000298023224);
               serverWorld.spawnParticles(ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0, (double)this.getWidth(), 0.20000000298023224);
               this.hookCountdown = MathHelper.nextInt(this.random, 20, 40);
               this.getDataTracker().set(CAUGHT_FISH, true);
            }
         } else if (this.waitCountdown > 0) {
            this.waitCountdown -= i;
            f = 0.15F;
            if (this.waitCountdown < 20) {
               f += (float)(20 - this.waitCountdown) * 0.05F;
            } else if (this.waitCountdown < 40) {
               f += (float)(40 - this.waitCountdown) * 0.02F;
            } else if (this.waitCountdown < 60) {
               f += (float)(60 - this.waitCountdown) * 0.01F;
            }

            if (this.random.nextFloat() < f) {
               g = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
               h = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
               d = this.getX() + (double)(MathHelper.sin(g) * h) * 0.1;
               e = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
               j = this.getZ() + (double)(MathHelper.cos(g) * h) * 0.1;
               blockState = serverWorld.getBlockState(BlockPos.ofFloored(d, e - 1.0, j));
               if (blockState.isOf(Blocks.WATER)) {
                  serverWorld.spawnParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
               }
            }

            if (this.waitCountdown <= 0) {
               this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
               this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
            }
         } else {
            this.waitCountdown = MathHelper.nextInt(this.random, 100, 600);
            this.waitCountdown -= this.waitTimeReductionTicks;
         }
      }

   }

   private boolean isOpenOrWaterAround(BlockPos pos) {
      PositionType positionType = FishingBobberEntity.PositionType.INVALID;

      for(int i = -1; i <= 2; ++i) {
         PositionType positionType2 = this.getPositionType(pos.add(-2, i, -2), pos.add(2, i, 2));
         switch (positionType2.ordinal()) {
            case 0:
               if (positionType == FishingBobberEntity.PositionType.INVALID) {
                  return false;
               }
               break;
            case 1:
               if (positionType == FishingBobberEntity.PositionType.ABOVE_WATER) {
                  return false;
               }
               break;
            case 2:
               return false;
         }

         positionType = positionType2;
      }

      return true;
   }

   private PositionType getPositionType(BlockPos start, BlockPos end) {
      return (PositionType)BlockPos.stream(start, end).map(this::getPositionType).reduce((positionType, positionType2) -> {
         return positionType == positionType2 ? positionType : FishingBobberEntity.PositionType.INVALID;
      }).orElse(FishingBobberEntity.PositionType.INVALID);
   }

   private PositionType getPositionType(BlockPos pos) {
      BlockState blockState = this.getWorld().getBlockState(pos);
      if (!blockState.isAir() && !blockState.isOf(Blocks.LILY_PAD)) {
         FluidState fluidState = blockState.getFluidState();
         return fluidState.isIn(FluidTags.WATER) && fluidState.isStill() && blockState.getCollisionShape(this.getWorld(), pos).isEmpty() ? FishingBobberEntity.PositionType.INSIDE_WATER : FishingBobberEntity.PositionType.INVALID;
      } else {
         return FishingBobberEntity.PositionType.ABOVE_WATER;
      }
   }

   public boolean isInOpenWater() {
      return this.inOpenWater;
   }

   protected void writeCustomData(WriteView view) {
   }

   protected void readCustomData(ReadView view) {
   }

   public int use(ItemStack usedItem) {
      PlayerEntity playerEntity = this.getPlayerOwner();
      if (!this.getWorld().isClient && playerEntity != null && !this.removeIfInvalid(playerEntity)) {
         int i = 0;
         if (this.hookedEntity != null) {
            this.pullHookedEntity(this.hookedEntity);
            Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, Collections.emptyList());
            this.getWorld().sendEntityStatus(this, (byte)31);
            i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
         } else if (this.hookCountdown > 0) {
            LootWorldContext lootWorldContext = (new LootWorldContext.Builder((ServerWorld)this.getWorld())).add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.TOOL, usedItem).add(LootContextParameters.THIS_ENTITY, this).luck((float)this.luckBonus + playerEntity.getLuck()).build(LootContextTypes.FISHING);
            LootTable lootTable = this.getWorld().getServer().getReloadableRegistries().getLootTable(LootTables.FISHING_GAMEPLAY);
            List list = lootTable.generateLoot(lootWorldContext);
            Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, list);
            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
               ItemStack itemStack = (ItemStack)var7.next();
               ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), itemStack);
               double d = playerEntity.getX() - this.getX();
               double e = playerEntity.getY() - this.getY();
               double f = playerEntity.getZ() - this.getZ();
               double g = 0.1;
               itemEntity.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
               this.getWorld().spawnEntity(itemEntity);
               playerEntity.getWorld().spawnEntity(new ExperienceOrbEntity(playerEntity.getWorld(), playerEntity.getX(), playerEntity.getY() + 0.5, playerEntity.getZ() + 0.5, this.random.nextInt(6) + 1));
               if (itemStack.isIn(ItemTags.FISHES)) {
                  playerEntity.increaseStat((Identifier)Stats.FISH_CAUGHT, 1);
               }
            }

            i = 1;
         }

         if (this.isOnGround()) {
            i = 2;
         }

         this.discard();
         return i;
      } else {
         return 0;
      }
   }

   public void handleStatus(byte status) {
      if (status == 31 && this.getWorld().isClient) {
         Entity var3 = this.hookedEntity;
         if (var3 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)var3;
            if (playerEntity.isMainPlayer()) {
               this.pullHookedEntity(this.hookedEntity);
            }
         }
      }

      super.handleStatus(status);
   }

   protected void pullHookedEntity(Entity entity) {
      Entity entity2 = this.getOwner();
      if (entity2 != null) {
         Vec3d vec3d = (new Vec3d(entity2.getX() - this.getX(), entity2.getY() - this.getY(), entity2.getZ() - this.getZ())).multiply(0.1);
         entity.setVelocity(entity.getVelocity().add(vec3d));
      }
   }

   protected Entity.MoveEffect getMoveEffect() {
      return Entity.MoveEffect.NONE;
   }

   public void remove(Entity.RemovalReason reason) {
      this.setPlayerFishHook((FishingBobberEntity)null);
      super.remove(reason);
   }

   public void onRemoved() {
      this.setPlayerFishHook((FishingBobberEntity)null);
   }

   public void setOwner(@Nullable Entity owner) {
      super.setOwner(owner);
      this.setPlayerFishHook(this);
   }

   private void setPlayerFishHook(@Nullable FishingBobberEntity fishingBobber) {
      PlayerEntity playerEntity = this.getPlayerOwner();
      if (playerEntity != null) {
         playerEntity.fishHook = fishingBobber;
      }

   }

   @Nullable
   public PlayerEntity getPlayerOwner() {
      Entity entity = this.getOwner();
      PlayerEntity var10000;
      if (entity instanceof PlayerEntity playerEntity) {
         var10000 = playerEntity;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @Nullable
   public Entity getHookedEntity() {
      return this.hookedEntity;
   }

   public boolean canUsePortals(boolean allowVehicles) {
      return false;
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      Entity entity = this.getOwner();
      return new EntitySpawnS2CPacket(this, entityTrackerEntry, entity == null ? this.getId() : entity.getId());
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      if (this.getPlayerOwner() == null) {
         int i = packet.getEntityData();
         LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.getWorld().getEntityById(i), i);
         this.discard();
      }

   }

   static {
      HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
      CAUGHT_FISH = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }

   private static enum State {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;

      // $FF: synthetic method
      private static State[] method_36664() {
         return new State[]{FLYING, HOOKED_IN_ENTITY, BOBBING};
      }
   }

   static enum PositionType {
      ABOVE_WATER,
      INSIDE_WATER,
      INVALID;

      // $FF: synthetic method
      private static PositionType[] method_36665() {
         return new PositionType[]{ABOVE_WATER, INSIDE_WATER, INVALID};
      }
   }
}

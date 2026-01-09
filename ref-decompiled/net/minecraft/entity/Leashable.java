package net.minecraft.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Leashable {
   String LEASH_NBT_KEY = "leash";
   double DEFAULT_SNAPPING_DISTANCE = 12.0;
   double DEFAULT_ELASTIC_DISTANCE = 6.0;
   double field_60003 = 16.0;
   Vec3d ELASTICITY_MULTIPLIER = new Vec3d(0.8, 0.2, 0.8);
   float field_59997 = 0.7F;
   double field_59998 = 10.0;
   double field_59999 = 0.11;
   List HELD_ENTITY_ATTACHMENT_POINT = ImmutableList.of(new Vec3d(0.0, 0.5, 0.5));
   List LEASH_HOLDER_ATTACHMENT_POINT = ImmutableList.of(new Vec3d(0.0, 0.5, 0.0));
   List QUAD_LEASH_ATTACHMENT_POINTS = ImmutableList.of(new Vec3d(-0.5, 0.5, 0.5), new Vec3d(-0.5, 0.5, -0.5), new Vec3d(0.5, 0.5, -0.5), new Vec3d(0.5, 0.5, 0.5));

   @Nullable
   LeashData getLeashData();

   void setLeashData(@Nullable LeashData leashData);

   default boolean isLeashed() {
      return this.getLeashData() != null && this.getLeashData().leashHolder != null;
   }

   default boolean mightBeLeashed() {
      return this.getLeashData() != null;
   }

   default boolean canBeLeashedTo(Entity entity) {
      if (this == entity) {
         return false;
      } else {
         return this.getDistanceToCenter(entity) > this.getLeashSnappingDistance() ? false : this.canBeLeashed();
      }
   }

   default double getDistanceToCenter(Entity entity) {
      return entity.getBoundingBox().getCenter().distanceTo(((Entity)this).getBoundingBox().getCenter());
   }

   default boolean canBeLeashed() {
      return true;
   }

   default void setUnresolvedLeashHolderId(int unresolvedLeashHolderId) {
      this.setLeashData(new LeashData(unresolvedLeashHolderId));
      detachLeash((Entity)this, false, false);
   }

   default void readLeashData(ReadView view) {
      LeashData leashData = (LeashData)view.read("leash", Leashable.LeashData.CODEC).orElse((Object)null);
      if (this.getLeashData() != null && leashData == null) {
         this.detachLeashWithoutDrop();
      }

      this.setLeashData(leashData);
   }

   default void writeLeashData(WriteView view, @Nullable LeashData leashData) {
      view.putNullable("leash", Leashable.LeashData.CODEC, leashData);
   }

   private static void resolveLeashData(Entity entity, LeashData leashData) {
      if (leashData.unresolvedLeashData != null) {
         World var3 = entity.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            Optional optional = leashData.unresolvedLeashData.left();
            Optional optional2 = leashData.unresolvedLeashData.right();
            if (optional.isPresent()) {
               Entity entity2 = serverWorld.getEntity((UUID)optional.get());
               if (entity2 != null) {
                  attachLeash(entity, entity2, true);
                  return;
               }
            } else if (optional2.isPresent()) {
               attachLeash(entity, LeashKnotEntity.getOrCreate(serverWorld, (BlockPos)optional2.get()), true);
               return;
            }

            if (entity.age > 100) {
               entity.dropItem(serverWorld, Items.LEAD);
               ((Leashable)entity).setLeashData((LeashData)null);
            }
         }
      }

   }

   default void detachLeash() {
      detachLeash((Entity)this, true, true);
   }

   default void detachLeashWithoutDrop() {
      detachLeash((Entity)this, true, false);
   }

   default void onLeashRemoved() {
   }

   private static void detachLeash(Entity entity, boolean sendPacket, boolean dropItem) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData != null && leashData.leashHolder != null) {
         ((Leashable)entity).setLeashData((LeashData)null);
         ((Leashable)entity).onLeashRemoved();
         World var5 = entity.getWorld();
         if (var5 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var5;
            if (dropItem) {
               entity.dropItem(serverWorld, Items.LEAD);
            }

            if (sendPacket) {
               serverWorld.getChunkManager().sendToOtherNearbyPlayers(entity, new EntityAttachS2CPacket(entity, (Entity)null));
            }

            leashData.leashHolder.onHeldLeashUpdate((Leashable)entity);
         }
      }

   }

   static void tickLeash(ServerWorld world, Entity entity) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData != null && leashData.unresolvedLeashData != null) {
         resolveLeashData(entity, leashData);
      }

      if (leashData != null && leashData.leashHolder != null) {
         if (!entity.isAlive() || !leashData.leashHolder.isAlive()) {
            if (world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
               ((Leashable)entity).detachLeash();
            } else {
               ((Leashable)entity).detachLeashWithoutDrop();
            }
         }

         Entity entity2 = ((Leashable)entity).getLeashHolder();
         if (entity2 != null && entity2.getWorld() == entity.getWorld()) {
            double d = ((Leashable)entity).getDistanceToCenter(entity2);
            ((Leashable)entity).beforeLeashTick(entity2);
            if (d > ((Leashable)entity).getLeashSnappingDistance()) {
               world.playSound((Entity)null, entity2.getX(), entity2.getY(), entity2.getZ(), SoundEvents.ITEM_LEAD_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
               ((Leashable)entity).snapLongLeash();
            } else if (d > ((Leashable)entity).getElasticLeashDistance() - (double)entity2.getWidth() - (double)entity.getWidth() && ((Leashable)entity).applyElasticity(entity2, leashData)) {
               ((Leashable)entity).onLongLeashTick();
            } else {
               ((Leashable)entity).onShortLeashTick(entity2);
            }

            entity.setYaw((float)((double)entity.getYaw() - leashData.momentum));
            leashData.momentum *= (double)getSlipperiness(entity);
         }

      }
   }

   default void onLongLeashTick() {
      Entity entity = (Entity)this;
      entity.limitFallDistance();
   }

   default double getLeashSnappingDistance() {
      return 12.0;
   }

   default double getElasticLeashDistance() {
      return 6.0;
   }

   static float getSlipperiness(Entity entity) {
      if (entity.isOnGround()) {
         return entity.getWorld().getBlockState(entity.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91F;
      } else {
         return entity.isInFluid() ? 0.8F : 0.91F;
      }
   }

   default void beforeLeashTick(Entity leashHolder) {
      leashHolder.tickHeldLeash(this);
   }

   default void snapLongLeash() {
      this.detachLeash();
   }

   default void onShortLeashTick(Entity entity) {
   }

   default boolean applyElasticity(Entity leashHolder, LeashData leashData) {
      boolean bl = leashHolder.hasQuadLeashAttachmentPoints() && this.canUseQuadLeashAttachmentPoint();
      List list = calculateLeashElasticities((Entity)this, leashHolder, bl ? QUAD_LEASH_ATTACHMENT_POINTS : HELD_ENTITY_ATTACHMENT_POINT, bl ? QUAD_LEASH_ATTACHMENT_POINTS : LEASH_HOLDER_ATTACHMENT_POINT);
      if (list.isEmpty()) {
         return false;
      } else {
         Elasticity elasticity = Leashable.Elasticity.sumOf(list).multiply(bl ? 0.25 : 1.0);
         leashData.momentum += 10.0 * elasticity.torque();
         Vec3d vec3d = getLeashHolderMovement(leashHolder).subtract(((Entity)this).getMovement());
         ((Entity)this).addVelocityInternal(elasticity.force().multiply(ELASTICITY_MULTIPLIER).add(vec3d.multiply(0.11)));
         return true;
      }
   }

   private static Vec3d getLeashHolderMovement(Entity leashHolder) {
      if (leashHolder instanceof MobEntity mobEntity) {
         if (mobEntity.isAiDisabled()) {
            return Vec3d.ZERO;
         }
      }

      return leashHolder.getMovement();
   }

   private static List calculateLeashElasticities(Entity heldEntity, Entity leashHolder, List heldEntityAttachmentPoints, List leashHolderAttachmentPoints) {
      double d = ((Leashable)heldEntity).getElasticLeashDistance();
      Vec3d vec3d = getLeashHolderMovement(heldEntity);
      float f = heldEntity.getYaw() * 0.017453292F;
      Vec3d vec3d2 = new Vec3d((double)heldEntity.getWidth(), (double)heldEntity.getHeight(), (double)heldEntity.getWidth());
      float g = leashHolder.getYaw() * 0.017453292F;
      Vec3d vec3d3 = new Vec3d((double)leashHolder.getWidth(), (double)leashHolder.getHeight(), (double)leashHolder.getWidth());
      List list = new ArrayList();

      for(int i = 0; i < heldEntityAttachmentPoints.size(); ++i) {
         Vec3d vec3d4 = ((Vec3d)heldEntityAttachmentPoints.get(i)).multiply(vec3d2).rotateY(-f);
         Vec3d vec3d5 = heldEntity.getPos().add(vec3d4);
         Vec3d vec3d6 = ((Vec3d)leashHolderAttachmentPoints.get(i)).multiply(vec3d3).rotateY(-g);
         Vec3d vec3d7 = leashHolder.getPos().add(vec3d6);
         Optional var10000 = calculateLeashElasticity(vec3d7, vec3d5, d, vec3d, vec3d4);
         Objects.requireNonNull(list);
         var10000.ifPresent(list::add);
      }

      return list;
   }

   private static Optional calculateLeashElasticity(Vec3d leashHolderAttachmentPos, Vec3d heldEntityAttachmentPos, double elasticDistance, Vec3d heldEntityMovement, Vec3d heldEntityAttachmentPoint) {
      double d = heldEntityAttachmentPos.distanceTo(leashHolderAttachmentPos);
      if (d < elasticDistance) {
         return Optional.empty();
      } else {
         Vec3d vec3d = leashHolderAttachmentPos.subtract(heldEntityAttachmentPos).normalize().multiply(d - elasticDistance);
         double e = Leashable.Elasticity.calculateTorque(heldEntityAttachmentPoint, vec3d);
         boolean bl = heldEntityMovement.dotProduct(vec3d) >= 0.0;
         if (bl) {
            vec3d = vec3d.multiply(0.30000001192092896);
         }

         return Optional.of(new Elasticity(vec3d, e));
      }
   }

   default boolean canUseQuadLeashAttachmentPoint() {
      return false;
   }

   default Vec3d[] getQuadLeashOffsets() {
      return createQuadLeashOffsets((Entity)this, 0.0, 0.5, 0.5, 0.5);
   }

   static Vec3d[] createQuadLeashOffsets(Entity leashedEntity, double addedZOffset, double zOffset, double xOffset, double yOffset) {
      float f = leashedEntity.getWidth();
      double d = addedZOffset * (double)f;
      double e = zOffset * (double)f;
      double g = xOffset * (double)f;
      double h = yOffset * (double)leashedEntity.getHeight();
      return new Vec3d[]{new Vec3d(-g, h, e + d), new Vec3d(-g, h, -e + d), new Vec3d(g, h, -e + d), new Vec3d(g, h, e + d)};
   }

   default Vec3d getLeashOffset(float tickProgress) {
      return this.getLeashOffset();
   }

   default Vec3d getLeashOffset() {
      Entity entity = (Entity)this;
      return new Vec3d(0.0, (double)entity.getStandingEyeHeight(), (double)(entity.getWidth() * 0.4F));
   }

   default void attachLeash(Entity leashHolder, boolean sendPacket) {
      if (this != leashHolder) {
         attachLeash((Entity)this, leashHolder, sendPacket);
      }
   }

   private static void attachLeash(Entity entity, Entity leashHolder, boolean sendPacket) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData == null) {
         leashData = new LeashData(leashHolder);
         ((Leashable)entity).setLeashData(leashData);
      } else {
         Entity entity2 = leashData.leashHolder;
         leashData.setLeashHolder(leashHolder);
         if (entity2 != null && entity2 != leashHolder) {
            entity2.onHeldLeashUpdate((Leashable)entity);
         }
      }

      if (sendPacket) {
         World var5 = entity.getWorld();
         if (var5 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var5;
            serverWorld.getChunkManager().sendToOtherNearbyPlayers(entity, new EntityAttachS2CPacket(entity, leashHolder));
         }
      }

      if (entity.hasVehicle()) {
         entity.stopRiding();
      }

   }

   @Nullable
   default Entity getLeashHolder() {
      return getLeashHolder((Entity)this);
   }

   @Nullable
   private static Entity getLeashHolder(Entity entity) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData == null) {
         return null;
      } else {
         if (leashData.unresolvedLeashHolderId != 0 && entity.getWorld().isClient) {
            Entity var3 = entity.getWorld().getEntityById(leashData.unresolvedLeashHolderId);
            if (var3 instanceof Entity) {
               leashData.setLeashHolder(var3);
            }
         }

         return leashData.leashHolder;
      }
   }

   static List collectLeashablesHeldBy(Entity leashHolder) {
      return collectLeashablesAround(leashHolder, (leashable) -> {
         return leashable.getLeashHolder() == leashHolder;
      });
   }

   static List collectLeashablesAround(Entity entity, Predicate leashablePredicate) {
      return collectLeashablesAround(entity.getWorld(), entity.getBoundingBox().getCenter(), leashablePredicate);
   }

   static List collectLeashablesAround(World world, Vec3d pos, Predicate leashablePredicate) {
      double d = 32.0;
      Box box = Box.of(pos, 32.0, 32.0, 32.0);
      Stream var10000 = world.getEntitiesByClass(Entity.class, box, (entity) -> {
         boolean var10000;
         if (entity instanceof Leashable leashable) {
            if (leashablePredicate.test(leashable)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }).stream();
      Objects.requireNonNull(Leashable.class);
      return var10000.map(Leashable.class::cast).toList();
   }

   public static final class LeashData {
      public static final Codec CODEC;
      int unresolvedLeashHolderId;
      @Nullable
      public Entity leashHolder;
      @Nullable
      public Either unresolvedLeashData;
      public double momentum;

      private LeashData(Either unresolvedLeashData) {
         this.unresolvedLeashData = unresolvedLeashData;
      }

      LeashData(Entity leashHolder) {
         this.leashHolder = leashHolder;
      }

      LeashData(int unresolvedLeashHolderId) {
         this.unresolvedLeashHolderId = unresolvedLeashHolderId;
      }

      public void setLeashHolder(Entity leashHolder) {
         this.leashHolder = leashHolder;
         this.unresolvedLeashData = null;
         this.unresolvedLeashHolderId = 0;
      }

      static {
         CODEC = Codec.xor(Uuids.INT_STREAM_CODEC.fieldOf("UUID").codec(), BlockPos.CODEC).xmap(LeashData::new, (data) -> {
            Entity entity = data.leashHolder;
            if (entity instanceof LeashKnotEntity leashKnotEntity) {
               return Either.right(leashKnotEntity.getAttachedBlockPos());
            } else {
               return data.leashHolder != null ? Either.left(data.leashHolder.getUuid()) : (Either)Objects.requireNonNull(data.unresolvedLeashData, "Invalid LeashData had no attachment");
            }
         });
      }
   }

   public static record Elasticity(Vec3d force, double torque) {
      static Elasticity ZERO;

      public Elasticity(Vec3d vec3d, double d) {
         this.force = vec3d;
         this.torque = d;
      }

      static double calculateTorque(Vec3d force, Vec3d force2) {
         return force.z * force2.x - force.x * force2.z;
      }

      static Elasticity sumOf(List elasticities) {
         if (elasticities.isEmpty()) {
            return ZERO;
         } else {
            double d = 0.0;
            double e = 0.0;
            double f = 0.0;
            double g = 0.0;

            Elasticity elasticity;
            for(Iterator var9 = elasticities.iterator(); var9.hasNext(); g += elasticity.torque) {
               elasticity = (Elasticity)var9.next();
               Vec3d vec3d = elasticity.force;
               d += vec3d.x;
               e += vec3d.y;
               f += vec3d.z;
            }

            return new Elasticity(new Vec3d(d, e, f), g);
         }
      }

      public Elasticity multiply(double value) {
         return new Elasticity(this.force.multiply(value), this.torque * value);
      }

      public Vec3d force() {
         return this.force;
      }

      public double torque() {
         return this.torque;
      }

      static {
         ZERO = new Elasticity(Vec3d.ZERO, 0.0);
      }
   }
}

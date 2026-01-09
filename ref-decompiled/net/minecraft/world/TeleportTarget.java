package net.minecraft.world;

import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record TeleportTarget(ServerWorld world, Vec3d position, Vec3d velocity, float yaw, float pitch, boolean missingRespawnBlock, boolean asPassenger, Set relatives, PostDimensionTransition postTeleportTransition) {
   public static final PostDimensionTransition NO_OP = (entity) -> {
   };
   public static final PostDimensionTransition SEND_TRAVEL_THROUGH_PORTAL_PACKET = TeleportTarget::sendTravelThroughPortalPacket;
   public static final PostDimensionTransition ADD_PORTAL_CHUNK_TICKET = TeleportTarget::addPortalChunkTicket;

   public TeleportTarget(ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch, PostDimensionTransition postDimensionTransition) {
      this(world, pos, velocity, yaw, pitch, Set.of(), postDimensionTransition);
   }

   public TeleportTarget(ServerWorld world, Vec3d pos, Vec3d velocity, float yaw, float pitch, Set flags, PostDimensionTransition postDimensionTransition) {
      this(world, pos, velocity, yaw, pitch, false, false, flags, postDimensionTransition);
   }

   public TeleportTarget(ServerWorld world, Entity entity, PostDimensionTransition postDimensionTransition) {
      this(world, getWorldSpawnPos(world, entity), Vec3d.ZERO, world.getSpawnAngle(), 0.0F, false, false, Set.of(), postDimensionTransition);
   }

   public TeleportTarget(ServerWorld serverWorld, Vec3d vec3d, Vec3d vec3d2, float f, float g, boolean bl, boolean bl2, Set set, PostDimensionTransition postDimensionTransition) {
      this.world = serverWorld;
      this.position = vec3d;
      this.velocity = vec3d2;
      this.yaw = f;
      this.pitch = g;
      this.missingRespawnBlock = bl;
      this.asPassenger = bl2;
      this.relatives = set;
      this.postTeleportTransition = postDimensionTransition;
   }

   private static void sendTravelThroughPortalPacket(Entity entity) {
      if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
         serverPlayerEntity.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
      }

   }

   private static void addPortalChunkTicket(Entity entity) {
      entity.addPortalChunkTicketAt(BlockPos.ofFloored(entity.getPos()));
   }

   public static TeleportTarget missingSpawnBlock(ServerWorld world, Entity entity, PostDimensionTransition postDimensionTransition) {
      return new TeleportTarget(world, getWorldSpawnPos(world, entity), Vec3d.ZERO, world.getSpawnAngle(), 0.0F, true, false, Set.of(), postDimensionTransition);
   }

   private static Vec3d getWorldSpawnPos(ServerWorld world, Entity entity) {
      return entity.getWorldSpawnPos(world, world.getSpawnPos()).toBottomCenterPos();
   }

   public TeleportTarget withRotation(float yaw, float pitch) {
      return new TeleportTarget(this.world(), this.position(), this.velocity(), yaw, pitch, this.missingRespawnBlock(), this.asPassenger(), this.relatives(), this.postTeleportTransition());
   }

   public TeleportTarget withPosition(Vec3d position) {
      return new TeleportTarget(this.world(), position, this.velocity(), this.yaw(), this.pitch(), this.missingRespawnBlock(), this.asPassenger(), this.relatives(), this.postTeleportTransition());
   }

   public TeleportTarget asPassenger() {
      return new TeleportTarget(this.world(), this.position(), this.velocity(), this.yaw(), this.pitch(), this.missingRespawnBlock(), true, this.relatives(), this.postTeleportTransition());
   }

   public ServerWorld world() {
      return this.world;
   }

   public Vec3d position() {
      return this.position;
   }

   public Vec3d velocity() {
      return this.velocity;
   }

   public float yaw() {
      return this.yaw;
   }

   public float pitch() {
      return this.pitch;
   }

   public boolean missingRespawnBlock() {
      return this.missingRespawnBlock;
   }

   public boolean asPassenger() {
      return this.asPassenger;
   }

   public Set relatives() {
      return this.relatives;
   }

   public PostDimensionTransition postTeleportTransition() {
      return this.postTeleportTransition;
   }

   @FunctionalInterface
   public interface PostDimensionTransition {
      void onTransition(Entity entity);

      default PostDimensionTransition then(PostDimensionTransition next) {
         return (entity) -> {
            this.onTransition(entity);
            next.onTransition(entity);
         };
      }
   }
}

package net.minecraft.world.waypoint;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;

public abstract class TrackedWaypoint implements Waypoint {
   static final Logger LOGGER = LogUtils.getLogger();
   public static PacketCodec PACKET_CODEC = PacketCodec.of(TrackedWaypoint::writeBuf, TrackedWaypoint::fromBuf);
   protected final Either source;
   private final Waypoint.Config config;
   private final Type type;

   TrackedWaypoint(Either source, Waypoint.Config config, Type type) {
      this.source = source;
      this.config = config;
      this.type = type;
   }

   public Either getSource() {
      return this.source;
   }

   public abstract void handleUpdate(TrackedWaypoint waypoint);

   public void writeBuf(ByteBuf buf) {
      PacketByteBuf packetByteBuf = new PacketByteBuf(buf);
      packetByteBuf.writeEither(this.source, Uuids.PACKET_CODEC, PacketByteBuf::writeString);
      Waypoint.Config.PACKET_CODEC.encode(packetByteBuf, this.config);
      packetByteBuf.writeEnumConstant(this.type);
      this.writeAdditionalDataToBuf(buf);
   }

   public abstract void writeAdditionalDataToBuf(ByteBuf buf);

   private static TrackedWaypoint fromBuf(ByteBuf buf) {
      PacketByteBuf packetByteBuf = new PacketByteBuf(buf);
      Either either = packetByteBuf.readEither(Uuids.PACKET_CODEC, PacketByteBuf::readString);
      Waypoint.Config config = (Waypoint.Config)Waypoint.Config.PACKET_CODEC.decode(packetByteBuf);
      Type type = (Type)packetByteBuf.readEnumConstant(Type.class);
      return (TrackedWaypoint)type.factory.apply(either, config, packetByteBuf);
   }

   public static TrackedWaypoint ofPos(UUID source, Waypoint.Config config, Vec3i pos) {
      return new Positional(source, config, pos);
   }

   public static TrackedWaypoint ofChunk(UUID source, Waypoint.Config config, ChunkPos chunkPos) {
      return new ChunkBased(source, config, chunkPos);
   }

   public static TrackedWaypoint ofAzimuth(UUID source, Waypoint.Config config, float azimuth) {
      return new Azimuth(source, config, azimuth);
   }

   public static TrackedWaypoint empty(UUID uuid) {
      return new Empty(uuid);
   }

   public abstract double getRelativeYaw(World world, YawProvider yawProvider);

   public abstract Pitch getPitch(World world, PitchProvider cameraProvider);

   public abstract double squaredDistanceTo(Entity receiver);

   public Waypoint.Config getConfig() {
      return this.config;
   }

   static enum Type {
      EMPTY(Empty::new),
      VEC3I(Positional::new),
      CHUNK(ChunkBased::new),
      AZIMUTH(Azimuth::new);

      final TriFunction factory;

      private Type(final TriFunction factory) {
         this.factory = factory;
      }

      // $FF: synthetic method
      private static Type[] method_70779() {
         return new Type[]{EMPTY, VEC3I, CHUNK, AZIMUTH};
      }
   }

   private static class Positional extends TrackedWaypoint {
      private Vec3i pos;

      public Positional(UUID uuid, Waypoint.Config config, Vec3i pos) {
         super(Either.left(uuid), config, TrackedWaypoint.Type.VEC3I);
         this.pos = pos;
      }

      public Positional(Either source, Waypoint.Config config, PacketByteBuf buf) {
         super(source, config, TrackedWaypoint.Type.VEC3I);
         this.pos = new Vec3i(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
      }

      public void handleUpdate(TrackedWaypoint waypoint) {
         if (waypoint instanceof Positional positional) {
            this.pos = positional.pos;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", waypoint.getClass());
         }

      }

      public void writeAdditionalDataToBuf(ByteBuf buf) {
         VarInts.write(buf, this.pos.getX());
         VarInts.write(buf, this.pos.getY());
         VarInts.write(buf, this.pos.getZ());
      }

      private Vec3d getSourcePos(World world) {
         Optional var10000 = this.source.left();
         Objects.requireNonNull(world);
         return (Vec3d)var10000.map(world::getEntity).map((entity) -> {
            return entity.getBlockPos().getManhattanDistance(this.pos) > 3 ? null : entity.getEyePos();
         }).orElseGet(() -> {
            return Vec3d.ofCenter(this.pos);
         });
      }

      public double getRelativeYaw(World world, YawProvider yawProvider) {
         Vec3d vec3d = yawProvider.getCameraPos().subtract(this.getSourcePos(world)).rotateYClockwise();
         float f = (float)MathHelper.atan2(vec3d.getZ(), vec3d.getX()) * 57.295776F;
         return (double)MathHelper.subtractAngles(yawProvider.getCameraYaw(), f);
      }

      public Pitch getPitch(World world, PitchProvider cameraProvider) {
         Vec3d vec3d = cameraProvider.project(this.getSourcePos(world));
         boolean bl = vec3d.z > 1.0;
         double d = bl ? -vec3d.y : vec3d.y;
         if (d < -1.0) {
            return TrackedWaypoint.Pitch.DOWN;
         } else if (d > 1.0) {
            return TrackedWaypoint.Pitch.UP;
         } else {
            if (bl) {
               if (vec3d.y > 0.0) {
                  return TrackedWaypoint.Pitch.UP;
               }

               if (vec3d.y < 0.0) {
                  return TrackedWaypoint.Pitch.DOWN;
               }
            }

            return TrackedWaypoint.Pitch.NONE;
         }
      }

      public double squaredDistanceTo(Entity receiver) {
         return receiver.squaredDistanceTo(Vec3d.ofCenter(this.pos));
      }
   }

   private static class ChunkBased extends TrackedWaypoint {
      private ChunkPos chunkPos;

      public ChunkBased(UUID source, Waypoint.Config config, ChunkPos chunkPos) {
         super(Either.left(source), config, TrackedWaypoint.Type.CHUNK);
         this.chunkPos = chunkPos;
      }

      public ChunkBased(Either source, Waypoint.Config config, PacketByteBuf buf) {
         super(source, config, TrackedWaypoint.Type.CHUNK);
         this.chunkPos = new ChunkPos(buf.readVarInt(), buf.readVarInt());
      }

      public void handleUpdate(TrackedWaypoint waypoint) {
         if (waypoint instanceof ChunkBased chunkBased) {
            this.chunkPos = chunkBased.chunkPos;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", waypoint.getClass());
         }

      }

      public void writeAdditionalDataToBuf(ByteBuf buf) {
         VarInts.write(buf, this.chunkPos.x);
         VarInts.write(buf, this.chunkPos.z);
      }

      private Vec3d getChunkCenterPos(double y) {
         return Vec3d.ofCenter(this.chunkPos.getCenterAtY((int)y));
      }

      public double getRelativeYaw(World world, YawProvider yawProvider) {
         Vec3d vec3d = yawProvider.getCameraPos();
         Vec3d vec3d2 = vec3d.subtract(this.getChunkCenterPos(vec3d.getY())).rotateYClockwise();
         float f = (float)MathHelper.atan2(vec3d2.getZ(), vec3d2.getX()) * 57.295776F;
         return (double)MathHelper.subtractAngles(yawProvider.getCameraYaw(), f);
      }

      public Pitch getPitch(World world, PitchProvider cameraProvider) {
         double d = cameraProvider.getPitch();
         if (d < -1.0) {
            return TrackedWaypoint.Pitch.DOWN;
         } else {
            return d > 1.0 ? TrackedWaypoint.Pitch.UP : TrackedWaypoint.Pitch.NONE;
         }
      }

      public double squaredDistanceTo(Entity receiver) {
         return receiver.squaredDistanceTo(Vec3d.ofCenter(this.chunkPos.getCenterAtY(receiver.getBlockY())));
      }
   }

   private static class Azimuth extends TrackedWaypoint {
      private float azimuth;

      public Azimuth(UUID source, Waypoint.Config config, float azimuth) {
         super(Either.left(source), config, TrackedWaypoint.Type.AZIMUTH);
         this.azimuth = azimuth;
      }

      public Azimuth(Either source, Waypoint.Config config, PacketByteBuf buf) {
         super(source, config, TrackedWaypoint.Type.AZIMUTH);
         this.azimuth = buf.readFloat();
      }

      public void handleUpdate(TrackedWaypoint waypoint) {
         if (waypoint instanceof Azimuth azimuth) {
            this.azimuth = azimuth.azimuth;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", waypoint.getClass());
         }

      }

      public void writeAdditionalDataToBuf(ByteBuf buf) {
         buf.writeFloat(this.azimuth);
      }

      public double getRelativeYaw(World world, YawProvider yawProvider) {
         return (double)MathHelper.subtractAngles(yawProvider.getCameraYaw(), this.azimuth * 57.295776F);
      }

      public Pitch getPitch(World world, PitchProvider cameraProvider) {
         double d = cameraProvider.getPitch();
         if (d < -1.0) {
            return TrackedWaypoint.Pitch.DOWN;
         } else {
            return d > 1.0 ? TrackedWaypoint.Pitch.UP : TrackedWaypoint.Pitch.NONE;
         }
      }

      public double squaredDistanceTo(Entity receiver) {
         return Double.POSITIVE_INFINITY;
      }
   }

   private static class Empty extends TrackedWaypoint {
      private Empty(Either source, Waypoint.Config config, PacketByteBuf buf) {
         super(source, config, TrackedWaypoint.Type.EMPTY);
      }

      Empty(UUID source) {
         super(Either.left(source), Waypoint.Config.DEFAULT, TrackedWaypoint.Type.EMPTY);
      }

      public void handleUpdate(TrackedWaypoint waypoint) {
      }

      public void writeAdditionalDataToBuf(ByteBuf buf) {
      }

      public double getRelativeYaw(World world, YawProvider yawProvider) {
         return Double.NaN;
      }

      public Pitch getPitch(World world, PitchProvider cameraProvider) {
         return TrackedWaypoint.Pitch.NONE;
      }

      public double squaredDistanceTo(Entity receiver) {
         return Double.POSITIVE_INFINITY;
      }
   }

   public interface YawProvider {
      float getCameraYaw();

      Vec3d getCameraPos();
   }

   public interface PitchProvider {
      Vec3d project(Vec3d sourcePos);

      double getPitch();
   }

   public static enum Pitch {
      NONE,
      UP,
      DOWN;

      // $FF: synthetic method
      private static Pitch[] method_71494() {
         return new Pitch[]{NONE, UP, DOWN};
      }
   }
}

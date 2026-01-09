package net.minecraft.network.packet.s2c.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record DebugBeeCustomPayload(Bee beeInfo) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugBeeCustomPayload::write, DebugBeeCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/bee");

   private DebugBeeCustomPayload(PacketByteBuf buf) {
      this(new Bee(buf));
   }

   public DebugBeeCustomPayload(Bee bee) {
      this.beeInfo = bee;
   }

   private void write(PacketByteBuf buf) {
      this.beeInfo.write(buf);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public Bee beeInfo() {
      return this.beeInfo;
   }

   public static record Bee(UUID uuid, int entityId, Vec3d pos, @Nullable Path path, @Nullable BlockPos hivePos, @Nullable BlockPos flowerPos, int travelTicks, Set goals, List disallowedHives) {
      public Bee(PacketByteBuf buf) {
         this(buf.readUuid(), buf.readInt(), buf.readVec3d(), (Path)buf.readNullable(Path::fromBuf), (BlockPos)buf.readNullable(BlockPos.PACKET_CODEC), (BlockPos)buf.readNullable(BlockPos.PACKET_CODEC), buf.readInt(), (Set)buf.readCollection(HashSet::new, PacketByteBuf::readString), buf.readList(BlockPos.PACKET_CODEC));
      }

      public Bee(UUID uuid, int entityId, Vec3d vec3d, @Nullable Path path, @Nullable BlockPos hive, @Nullable BlockPos flower, int travelTicks, Set set, List list) {
         this.uuid = uuid;
         this.entityId = entityId;
         this.pos = vec3d;
         this.path = path;
         this.hivePos = hive;
         this.flowerPos = flower;
         this.travelTicks = travelTicks;
         this.goals = set;
         this.disallowedHives = list;
      }

      public void write(PacketByteBuf buf) {
         buf.writeUuid(this.uuid);
         buf.writeInt(this.entityId);
         buf.writeVec3d(this.pos);
         buf.writeNullable(this.path, (bufx, path) -> {
            path.toBuf(bufx);
         });
         buf.writeNullable(this.hivePos, BlockPos.PACKET_CODEC);
         buf.writeNullable(this.flowerPos, BlockPos.PACKET_CODEC);
         buf.writeInt(this.travelTicks);
         buf.writeCollection(this.goals, PacketByteBuf::writeString);
         buf.writeCollection(this.disallowedHives, BlockPos.PACKET_CODEC);
      }

      public boolean isHiveAt(BlockPos pos) {
         return Objects.equals(pos, this.hivePos);
      }

      public String getName() {
         return NameGenerator.name(this.uuid);
      }

      public String toString() {
         return this.getName();
      }

      public UUID uuid() {
         return this.uuid;
      }

      public int entityId() {
         return this.entityId;
      }

      public Vec3d pos() {
         return this.pos;
      }

      @Nullable
      public Path path() {
         return this.path;
      }

      @Nullable
      public BlockPos hivePos() {
         return this.hivePos;
      }

      @Nullable
      public BlockPos flowerPos() {
         return this.flowerPos;
      }

      public int travelTicks() {
         return this.travelTicks;
      }

      public Set goals() {
         return this.goals;
      }

      public List disallowedHives() {
         return this.disallowedHives;
      }
   }
}

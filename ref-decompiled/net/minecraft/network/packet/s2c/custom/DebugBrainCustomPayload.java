package net.minecraft.network.packet.s2c.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record DebugBrainCustomPayload(Brain brainDump) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugBrainCustomPayload::write, DebugBrainCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/brain");

   private DebugBrainCustomPayload(PacketByteBuf buf) {
      this(new Brain(buf));
   }

   public DebugBrainCustomPayload(Brain brain) {
      this.brainDump = brain;
   }

   private void write(PacketByteBuf buf) {
      this.brainDump.write(buf);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public Brain brainDump() {
      return this.brainDump;
   }

   public static record Brain(UUID uuid, int entityId, String name, String profession, int experience, float health, float maxHealth, Vec3d pos, String inventory, @Nullable Path path, boolean wantsGolem, int angerLevel, List possibleActivities, List runningTasks, List memories, List gossips, Set pois, Set potentialPois) {
      public Brain(PacketByteBuf buf) {
         this(buf.readUuid(), buf.readInt(), buf.readString(), buf.readString(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readVec3d(), buf.readString(), (Path)buf.readNullable(Path::fromBuf), buf.readBoolean(), buf.readInt(), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readString), (Set)buf.readCollection(HashSet::new, BlockPos.PACKET_CODEC), (Set)buf.readCollection(HashSet::new, BlockPos.PACKET_CODEC));
      }

      public Brain(UUID uuid, int entityId, String name, String profession, int xp, float health, float maxHealth, Vec3d vec3d, String string, @Nullable Path path, boolean wantsGolem, int angerLevel, List list, List list2, List list3, List list4, Set set, Set set2) {
         this.uuid = uuid;
         this.entityId = entityId;
         this.name = name;
         this.profession = profession;
         this.experience = xp;
         this.health = health;
         this.maxHealth = maxHealth;
         this.pos = vec3d;
         this.inventory = string;
         this.path = path;
         this.wantsGolem = wantsGolem;
         this.angerLevel = angerLevel;
         this.possibleActivities = list;
         this.runningTasks = list2;
         this.memories = list3;
         this.gossips = list4;
         this.pois = set;
         this.potentialPois = set2;
      }

      public void write(PacketByteBuf buf) {
         buf.writeUuid(this.uuid);
         buf.writeInt(this.entityId);
         buf.writeString(this.name);
         buf.writeString(this.profession);
         buf.writeInt(this.experience);
         buf.writeFloat(this.health);
         buf.writeFloat(this.maxHealth);
         buf.writeVec3d(this.pos);
         buf.writeString(this.inventory);
         buf.writeNullable(this.path, (bufx, path) -> {
            path.toBuf(bufx);
         });
         buf.writeBoolean(this.wantsGolem);
         buf.writeInt(this.angerLevel);
         buf.writeCollection(this.possibleActivities, PacketByteBuf::writeString);
         buf.writeCollection(this.runningTasks, PacketByteBuf::writeString);
         buf.writeCollection(this.memories, PacketByteBuf::writeString);
         buf.writeCollection(this.gossips, PacketByteBuf::writeString);
         buf.writeCollection(this.pois, BlockPos.PACKET_CODEC);
         buf.writeCollection(this.potentialPois, BlockPos.PACKET_CODEC);
      }

      public boolean isPointOfInterest(BlockPos pos) {
         return this.pois.contains(pos);
      }

      public boolean isPotentialJobSite(BlockPos pos) {
         return this.potentialPois.contains(pos);
      }

      public UUID uuid() {
         return this.uuid;
      }

      public int entityId() {
         return this.entityId;
      }

      public String name() {
         return this.name;
      }

      public String profession() {
         return this.profession;
      }

      public int experience() {
         return this.experience;
      }

      public float health() {
         return this.health;
      }

      public float maxHealth() {
         return this.maxHealth;
      }

      public Vec3d pos() {
         return this.pos;
      }

      public String inventory() {
         return this.inventory;
      }

      @Nullable
      public Path path() {
         return this.path;
      }

      public boolean wantsGolem() {
         return this.wantsGolem;
      }

      public int angerLevel() {
         return this.angerLevel;
      }

      public List possibleActivities() {
         return this.possibleActivities;
      }

      public List runningTasks() {
         return this.runningTasks;
      }

      public List memories() {
         return this.memories;
      }

      public List gossips() {
         return this.gossips;
      }

      public Set pois() {
         return this.pois;
      }

      public Set potentialPois() {
         return this.potentialPois;
      }
   }
}

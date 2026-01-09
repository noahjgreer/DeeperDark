package net.minecraft.network.packet.s2c.custom;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;

public record DebugBreezeCustomPayload(BreezeInfo breezeInfo) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugBreezeCustomPayload::write, DebugBreezeCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/breeze");

   private DebugBreezeCustomPayload(PacketByteBuf buf) {
      this(new BreezeInfo(buf));
   }

   public DebugBreezeCustomPayload(BreezeInfo breezeInfo) {
      this.breezeInfo = breezeInfo;
   }

   private void write(PacketByteBuf buf) {
      this.breezeInfo.write(buf);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public BreezeInfo breezeInfo() {
      return this.breezeInfo;
   }

   public static record BreezeInfo(UUID uuid, int id, Integer attackTarget, BlockPos jumpTarget) {
      public BreezeInfo(PacketByteBuf buf) {
         this(buf.readUuid(), buf.readInt(), (Integer)buf.readNullable(PacketByteBuf::readInt), (BlockPos)buf.readNullable(BlockPos.PACKET_CODEC));
      }

      public BreezeInfo(UUID uUID, int i, Integer integer, BlockPos blockPos) {
         this.uuid = uUID;
         this.id = i;
         this.attackTarget = integer;
         this.jumpTarget = blockPos;
      }

      public void write(PacketByteBuf buf) {
         buf.writeUuid(this.uuid);
         buf.writeInt(this.id);
         buf.writeNullable(this.attackTarget, PacketByteBuf::writeInt);
         buf.writeNullable(this.jumpTarget, BlockPos.PACKET_CODEC);
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

      public int id() {
         return this.id;
      }

      public Integer attackTarget() {
         return this.attackTarget;
      }

      public BlockPos jumpTarget() {
         return this.jumpTarget;
      }
   }
}

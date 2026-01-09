package net.minecraft.network.packet.c2s.common;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record ResourcePackStatusC2SPacket(UUID id, Status status) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ResourcePackStatusC2SPacket::write, ResourcePackStatusC2SPacket::new);

   private ResourcePackStatusC2SPacket(PacketByteBuf buf) {
      this(buf.readUuid(), (Status)buf.readEnumConstant(Status.class));
   }

   public ResourcePackStatusC2SPacket(UUID uUID, Status status) {
      this.id = uUID;
      this.status = status;
   }

   private void write(PacketByteBuf buf) {
      buf.writeUuid(this.id);
      buf.writeEnumConstant(this.status);
   }

   public PacketType getPacketType() {
      return CommonPackets.RESOURCE_PACK;
   }

   public void apply(ServerCommonPacketListener serverCommonPacketListener) {
      serverCommonPacketListener.onResourcePackStatus(this);
   }

   public UUID id() {
      return this.id;
   }

   public Status status() {
      return this.status;
   }

   public static enum Status {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED,
      DOWNLOADED,
      INVALID_URL,
      FAILED_RELOAD,
      DISCARDED;

      public boolean hasFinished() {
         return this != ACCEPTED && this != DOWNLOADED;
      }

      // $FF: synthetic method
      private static Status[] method_36961() {
         return new Status[]{SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED, DOWNLOADED, INVALID_URL, FAILED_RELOAD, DISCARDED};
      }
   }
}

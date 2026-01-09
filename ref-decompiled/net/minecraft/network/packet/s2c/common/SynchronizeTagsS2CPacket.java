package net.minecraft.network.packet.s2c.common;

import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.tag.TagPacketSerializer;

public class SynchronizeTagsS2CPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(SynchronizeTagsS2CPacket::write, SynchronizeTagsS2CPacket::new);
   private final Map groups;

   public SynchronizeTagsS2CPacket(Map groups) {
      this.groups = groups;
   }

   private SynchronizeTagsS2CPacket(PacketByteBuf buf) {
      this.groups = buf.readMap(PacketByteBuf::readRegistryRefKey, TagPacketSerializer.Serialized::fromBuf);
   }

   private void write(PacketByteBuf buf) {
      buf.writeMap(this.groups, PacketByteBuf::writeRegistryKey, (bufx, serializedGroup) -> {
         serializedGroup.writeBuf(bufx);
      });
   }

   public PacketType getPacketType() {
      return CommonPackets.UPDATE_TAGS;
   }

   public void apply(ClientCommonPacketListener clientCommonPacketListener) {
      clientCommonPacketListener.onSynchronizeTags(this);
   }

   public Map getGroups() {
      return this.groups;
   }
}

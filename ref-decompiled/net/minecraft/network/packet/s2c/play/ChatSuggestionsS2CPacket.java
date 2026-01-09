package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ChatSuggestionsS2CPacket(Action action, List entries) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ChatSuggestionsS2CPacket::write, ChatSuggestionsS2CPacket::new);

   private ChatSuggestionsS2CPacket(PacketByteBuf buf) {
      this((Action)buf.readEnumConstant(Action.class), buf.readList(PacketByteBuf::readString));
   }

   public ChatSuggestionsS2CPacket(Action action, List list) {
      this.action = action;
      this.entries = list;
   }

   private void write(PacketByteBuf buf) {
      buf.writeEnumConstant(this.action);
      buf.writeCollection(this.entries, PacketByteBuf::writeString);
   }

   public PacketType getPacketType() {
      return PlayPackets.CUSTOM_CHAT_COMPLETIONS;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onChatSuggestions(this);
   }

   public Action action() {
      return this.action;
   }

   public List entries() {
      return this.entries;
   }

   public static enum Action {
      ADD,
      REMOVE,
      SET;

      // $FF: synthetic method
      private static Action[] method_44784() {
         return new Action[]{ADD, REMOVE, SET};
      }
   }
}

package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;

public record CommandSuggestionsS2CPacket(int id, int start, int length, List suggestions) implements Packet {
   public static final PacketCodec CODEC;

   public CommandSuggestionsS2CPacket(int completionId, Suggestions suggestions) {
      this(completionId, suggestions.getRange().getStart(), suggestions.getRange().getLength(), suggestions.getList().stream().map((suggestion) -> {
         return new Suggestion(suggestion.getText(), Optional.ofNullable(suggestion.getTooltip()).map(Texts::toText));
      }).toList());
   }

   public CommandSuggestionsS2CPacket(int i, int j, int k, List list) {
      this.id = i;
      this.start = j;
      this.length = k;
      this.suggestions = list;
   }

   public PacketType getPacketType() {
      return PlayPackets.COMMAND_SUGGESTIONS;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onCommandSuggestions(this);
   }

   public Suggestions getSuggestions() {
      StringRange stringRange = StringRange.between(this.start, this.start + this.length);
      return new Suggestions(stringRange, this.suggestions.stream().map((suggestion) -> {
         return new com.mojang.brigadier.suggestion.Suggestion(stringRange, suggestion.text(), (Message)suggestion.tooltip().orElse((Object)null));
      }).toList());
   }

   public int id() {
      return this.id;
   }

   public int start() {
      return this.start;
   }

   public int length() {
      return this.length;
   }

   public List suggestions() {
      return this.suggestions;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, CommandSuggestionsS2CPacket::id, PacketCodecs.VAR_INT, CommandSuggestionsS2CPacket::start, PacketCodecs.VAR_INT, CommandSuggestionsS2CPacket::length, CommandSuggestionsS2CPacket.Suggestion.CODEC.collect(PacketCodecs.toList()), CommandSuggestionsS2CPacket::suggestions, CommandSuggestionsS2CPacket::new);
   }

   public static record Suggestion(String text, Optional tooltip) {
      public static final PacketCodec CODEC;

      public Suggestion(String string, Optional optional) {
         this.text = string;
         this.tooltip = optional;
      }

      public String text() {
         return this.text;
      }

      public Optional tooltip() {
         return this.tooltip;
      }

      static {
         CODEC = PacketCodec.tuple(PacketCodecs.STRING, Suggestion::text, TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC, Suggestion::tooltip, Suggestion::new);
      }
   }
}

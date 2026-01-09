package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.RecipeDisplayEntry;

public record RecipeBookAddS2CPacket(List entries, boolean replace) implements Packet {
   public static final PacketCodec CODEC;

   public RecipeBookAddS2CPacket(List list, boolean bl) {
      this.entries = list;
      this.replace = bl;
   }

   public PacketType getPacketType() {
      return PlayPackets.RECIPE_BOOK_ADD;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onRecipeBookAdd(this);
   }

   public List entries() {
      return this.entries;
   }

   public boolean replace() {
      return this.replace;
   }

   static {
      CODEC = PacketCodec.tuple(RecipeBookAddS2CPacket.Entry.PACKET_CODEC.collect(PacketCodecs.toList()), RecipeBookAddS2CPacket::entries, PacketCodecs.BOOLEAN, RecipeBookAddS2CPacket::replace, RecipeBookAddS2CPacket::new);
   }

   public static record Entry(RecipeDisplayEntry contents, byte flags) {
      public static final byte SHOW_NOTIFICATION = 1;
      public static final byte HIGHLIGHTED = 2;
      public static final PacketCodec PACKET_CODEC;

      public Entry(RecipeDisplayEntry display, boolean showNotification, boolean highlighted) {
         this(display, (byte)((showNotification ? 1 : 0) | (highlighted ? 2 : 0)));
      }

      public Entry(RecipeDisplayEntry recipeDisplayEntry, byte b) {
         this.contents = recipeDisplayEntry;
         this.flags = b;
      }

      public boolean shouldShowNotification() {
         return (this.flags & 1) != 0;
      }

      public boolean isHighlighted() {
         return (this.flags & 2) != 0;
      }

      public RecipeDisplayEntry contents() {
         return this.contents;
      }

      public byte flags() {
         return this.flags;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(RecipeDisplayEntry.PACKET_CODEC, Entry::contents, PacketCodecs.BYTE, Entry::flags, Entry::new);
      }
   }
}

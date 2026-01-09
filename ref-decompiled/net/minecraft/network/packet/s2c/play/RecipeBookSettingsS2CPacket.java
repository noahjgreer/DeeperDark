package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.book.RecipeBookOptions;

public record RecipeBookSettingsS2CPacket(RecipeBookOptions bookSettings) implements Packet {
   public static final PacketCodec CODEC;

   public RecipeBookSettingsS2CPacket(RecipeBookOptions recipeBookOptions) {
      this.bookSettings = recipeBookOptions;
   }

   public PacketType getPacketType() {
      return PlayPackets.RECIPE_BOOK_SETTINGS;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onRecipeBookSettings(this);
   }

   public RecipeBookOptions bookSettings() {
      return this.bookSettings;
   }

   static {
      CODEC = PacketCodec.tuple(RecipeBookOptions.PACKET_CODEC, RecipeBookSettingsS2CPacket::bookSettings, RecipeBookSettingsS2CPacket::new);
   }
}

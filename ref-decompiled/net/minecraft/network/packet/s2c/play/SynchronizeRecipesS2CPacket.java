package net.minecraft.network.packet.s2c.play;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;

public record SynchronizeRecipesS2CPacket(Map itemSets, CuttingRecipeDisplay.Grouping stonecutterRecipes) implements Packet {
   public static final PacketCodec CODEC;

   public SynchronizeRecipesS2CPacket(Map map, CuttingRecipeDisplay.Grouping grouping) {
      this.itemSets = map;
      this.stonecutterRecipes = grouping;
   }

   public PacketType getPacketType() {
      return PlayPackets.UPDATE_RECIPES;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onSynchronizeRecipes(this);
   }

   public Map itemSets() {
      return this.itemSets;
   }

   public CuttingRecipeDisplay.Grouping stonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.map(HashMap::new, RegistryKey.createPacketCodec(RecipePropertySet.REGISTRY), RecipePropertySet.PACKET_CODEC), SynchronizeRecipesS2CPacket::itemSets, CuttingRecipeDisplay.Grouping.codec(), SynchronizeRecipesS2CPacket::stonecutterRecipes, SynchronizeRecipesS2CPacket::new);
   }
}

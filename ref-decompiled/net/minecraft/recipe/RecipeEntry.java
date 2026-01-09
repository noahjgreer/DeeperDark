package net.minecraft.recipe;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public record RecipeEntry(RegistryKey id, Recipe value) {
   public static final PacketCodec PACKET_CODEC;

   public RecipeEntry(RegistryKey registryKey, Recipe recipe) {
      this.id = registryKey;
      this.value = recipe;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof RecipeEntry) {
            RecipeEntry recipeEntry = (RecipeEntry)o;
            if (this.id == recipeEntry.id) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return this.id.toString();
   }

   public RegistryKey id() {
      return this.id;
   }

   public Recipe value() {
      return this.value;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(RegistryKey.createPacketCodec(RegistryKeys.RECIPE), RecipeEntry::id, Recipe.PACKET_CODEC, RecipeEntry::value, RecipeEntry::new);
   }
}

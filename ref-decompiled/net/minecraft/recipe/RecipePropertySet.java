package net.minecraft.recipe;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class RecipePropertySet {
   public static final RegistryKey REGISTRY = RegistryKey.ofRegistry(Identifier.ofVanilla("recipe_property_set"));
   public static final RegistryKey SMITHING_BASE = register("smithing_base");
   public static final RegistryKey SMITHING_TEMPLATE = register("smithing_template");
   public static final RegistryKey SMITHING_ADDITION = register("smithing_addition");
   public static final RegistryKey FURNACE_INPUT = register("furnace_input");
   public static final RegistryKey BLAST_FURNACE_INPUT = register("blast_furnace_input");
   public static final RegistryKey SMOKER_INPUT = register("smoker_input");
   public static final RegistryKey CAMPFIRE_INPUT = register("campfire_input");
   public static final PacketCodec PACKET_CODEC;
   public static final RecipePropertySet EMPTY;
   private final Set usableItems;

   private RecipePropertySet(Set usableItems) {
      this.usableItems = usableItems;
   }

   private static RegistryKey register(String id) {
      return RegistryKey.of(REGISTRY, Identifier.ofVanilla(id));
   }

   public boolean canUse(ItemStack stack) {
      return this.usableItems.contains(stack.getRegistryEntry());
   }

   static RecipePropertySet of(Collection ingredients) {
      Set set = (Set)ingredients.stream().flatMap(Ingredient::getMatchingItems).collect(Collectors.toUnmodifiableSet());
      return new RecipePropertySet(set);
   }

   static {
      PACKET_CODEC = Item.ENTRY_PACKET_CODEC.collect(PacketCodecs.toList()).xmap((items) -> {
         return new RecipePropertySet(Set.copyOf(items));
      }, (set) -> {
         return List.copyOf(set.usableItems);
      });
      EMPTY = new RecipePropertySet(Set.of());
   }
}

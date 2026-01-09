package net.minecraft.recipe.display;

import net.minecraft.registry.Registry;

public class SlotDisplays {
   public static SlotDisplay.Serializer registerAndGetDefault(Registry registries) {
      Registry.register(registries, (String)"empty", SlotDisplay.EmptySlotDisplay.SERIALIZER);
      Registry.register(registries, (String)"any_fuel", SlotDisplay.AnyFuelSlotDisplay.SERIALIZER);
      Registry.register(registries, (String)"item", SlotDisplay.ItemSlotDisplay.SERIALIZER);
      Registry.register(registries, (String)"item_stack", SlotDisplay.StackSlotDisplay.SERIALIZER);
      Registry.register(registries, (String)"tag", SlotDisplay.TagSlotDisplay.SERIALIZER);
      Registry.register(registries, (String)"smithing_trim", SlotDisplay.SmithingTrimSlotDisplay.SERIALIZER);
      Registry.register(registries, (String)"with_remainder", SlotDisplay.WithRemainderSlotDisplay.SERIALIZER);
      return (SlotDisplay.Serializer)Registry.register(registries, (String)"composite", SlotDisplay.CompositeSlotDisplay.SERIALIZER);
   }
}

package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;

public record RepairableComponent(RegistryEntryList items) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("items").forGetter(RepairableComponent::items)).apply(instance, RepairableComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public RepairableComponent(RegistryEntryList registryEntryList) {
      this.items = registryEntryList;
   }

   public boolean matches(ItemStack stack) {
      return stack.isIn(this.items);
   }

   public RegistryEntryList items() {
      return this.items;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntryList(RegistryKeys.ITEM), RepairableComponent::items, RepairableComponent::new);
   }
}

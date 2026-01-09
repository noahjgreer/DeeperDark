package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.registry.entry.RegistryEntry;

public record ProvidesTrimMaterialComponent(LazyRegistryEntryReference material) {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public ProvidesTrimMaterialComponent(RegistryEntry material) {
      this(new LazyRegistryEntryReference(material));
   }

   /** @deprecated */
   @Deprecated
   public ProvidesTrimMaterialComponent(RegistryKey material) {
      this(new LazyRegistryEntryReference(material));
   }

   public ProvidesTrimMaterialComponent(LazyRegistryEntryReference lazyRegistryEntryReference) {
      this.material = lazyRegistryEntryReference;
   }

   public Optional getMaterial(RegistryWrapper.WrapperLookup registries) {
      return this.material.resolveEntry(registries);
   }

   public LazyRegistryEntryReference material() {
      return this.material;
   }

   static {
      CODEC = LazyRegistryEntryReference.createCodec(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.ENTRY_CODEC).xmap(ProvidesTrimMaterialComponent::new, ProvidesTrimMaterialComponent::material);
      PACKET_CODEC = LazyRegistryEntryReference.createPacketCodec(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.ENTRY_PACKET_CODEC).xmap(ProvidesTrimMaterialComponent::new, ProvidesTrimMaterialComponent::material);
   }
}

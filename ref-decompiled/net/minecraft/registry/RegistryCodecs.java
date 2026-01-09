package net.minecraft.registry;

import com.mojang.serialization.Codec;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.registry.entry.RegistryFixedCodec;

public class RegistryCodecs {
   public static Codec entryList(RegistryKey registryRef, Codec elementCodec) {
      return entryList(registryRef, elementCodec, false);
   }

   public static Codec entryList(RegistryKey registryRef, Codec elementCodec, boolean alwaysSerializeAsList) {
      return RegistryEntryListCodec.create(registryRef, RegistryElementCodec.of(registryRef, elementCodec), alwaysSerializeAsList);
   }

   public static Codec entryList(RegistryKey registryRef) {
      return entryList(registryRef, false);
   }

   public static Codec entryList(RegistryKey registryRef, boolean alwaysSerializeAsList) {
      return RegistryEntryListCodec.create(registryRef, RegistryFixedCodec.of(registryRef), alwaysSerializeAsList);
   }
}

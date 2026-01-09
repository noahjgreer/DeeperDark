package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;

public record FlatLevelGeneratorPreset(RegistryEntry displayItem, FlatChunkGeneratorConfig settings) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Item.ENTRY_CODEC.fieldOf("display").forGetter((preset) -> {
         return preset.displayItem;
      }), FlatChunkGeneratorConfig.CODEC.fieldOf("settings").forGetter((preset) -> {
         return preset.settings;
      })).apply(instance, FlatLevelGeneratorPreset::new);
   });
   public static final Codec ENTRY_CODEC;

   public FlatLevelGeneratorPreset(RegistryEntry registryEntry, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
      this.displayItem = registryEntry;
      this.settings = flatChunkGeneratorConfig;
   }

   public RegistryEntry displayItem() {
      return this.displayItem;
   }

   public FlatChunkGeneratorConfig settings() {
      return this.settings;
   }

   static {
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, CODEC);
   }
}

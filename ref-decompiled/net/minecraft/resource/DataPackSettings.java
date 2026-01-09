package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DataPackSettings {
   public static final DataPackSettings SAFE_MODE = new DataPackSettings(ImmutableList.of("vanilla"), ImmutableList.of());
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((settings) -> {
         return settings.enabled;
      }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((settings) -> {
         return settings.disabled;
      })).apply(instance, DataPackSettings::new);
   });
   private final List enabled;
   private final List disabled;

   public DataPackSettings(List enabled, List disabled) {
      this.enabled = ImmutableList.copyOf(enabled);
      this.disabled = ImmutableList.copyOf(disabled);
   }

   public List getEnabled() {
      return this.enabled;
   }

   public List getDisabled() {
      return this.disabled;
   }
}

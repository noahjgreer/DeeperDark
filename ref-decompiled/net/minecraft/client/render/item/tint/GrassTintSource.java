package net.minecraft.client.render.item.tint;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.biome.GrassColors;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record GrassTintSource(float temperature, float downfall) implements TintSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.rangedInclusiveFloat(0.0F, 1.0F).fieldOf("temperature").forGetter(GrassTintSource::temperature), Codecs.rangedInclusiveFloat(0.0F, 1.0F).fieldOf("downfall").forGetter(GrassTintSource::downfall)).apply(instance, GrassTintSource::new);
   });

   public GrassTintSource() {
      this(0.5F, 1.0F);
   }

   public GrassTintSource(float f, float g) {
      this.temperature = f;
      this.downfall = g;
   }

   public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
      return GrassColors.getColor((double)this.temperature, (double)this.downfall);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public float temperature() {
      return this.temperature;
   }

   public float downfall() {
      return this.downfall;
   }
}

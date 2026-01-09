package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record CountProperty(boolean normalize) implements NumericProperty {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("normalize", true).forGetter(CountProperty::normalize)).apply(instance, CountProperty::new);
   });

   public CountProperty(boolean bl) {
      this.normalize = bl;
   }

   public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
      float f = (float)stack.getCount();
      float g = (float)stack.getMaxCount();
      return this.normalize ? MathHelper.clamp(f / g, 0.0F, 1.0F) : MathHelper.clamp(f, 0.0F, g);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public boolean normalize() {
      return this.normalize;
   }
}

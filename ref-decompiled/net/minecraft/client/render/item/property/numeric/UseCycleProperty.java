package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record UseCycleProperty(float period) implements NumericProperty {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.POSITIVE_FLOAT.optionalFieldOf("period", 1.0F).forGetter(UseCycleProperty::period)).apply(instance, UseCycleProperty::new);
   });

   public UseCycleProperty(float f) {
      this.period = f;
   }

   public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
      return holder != null && holder.getActiveItem() == stack ? (float)holder.getItemUseTimeLeft() % this.period : 0.0F;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public float period() {
      return this.period;
   }
}

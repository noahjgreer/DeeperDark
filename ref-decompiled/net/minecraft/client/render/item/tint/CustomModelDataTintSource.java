package net.minecraft.client.render.item.tint;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record CustomModelDataTintSource(int index, int defaultColor) implements TintSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataTintSource::index), Codecs.RGB.fieldOf("default").forGetter(CustomModelDataTintSource::defaultColor)).apply(instance, CustomModelDataTintSource::new);
   });

   public CustomModelDataTintSource(int i, int j) {
      this.index = i;
      this.defaultColor = j;
   }

   public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
      CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
      if (customModelDataComponent != null) {
         Integer integer = customModelDataComponent.getColor(this.index);
         if (integer != null) {
            return ColorHelper.fullAlpha(integer);
         }
      }

      return ColorHelper.fullAlpha(this.defaultColor);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public int index() {
      return this.index;
   }

   public int defaultColor() {
      return this.defaultColor;
   }
}

package net.minecraft.client.render.item.property.numeric;

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
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record CustomModelDataFloatProperty(int index) implements NumericProperty {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataFloatProperty::index)).apply(instance, CustomModelDataFloatProperty::new);
   });

   public CustomModelDataFloatProperty(int i) {
      this.index = i;
   }

   public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
      CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
      if (customModelDataComponent != null) {
         Float float_ = customModelDataComponent.getFloat(this.index);
         if (float_ != null) {
            return float_;
         }
      }

      return 0.0F;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public int index() {
      return this.index;
   }
}
